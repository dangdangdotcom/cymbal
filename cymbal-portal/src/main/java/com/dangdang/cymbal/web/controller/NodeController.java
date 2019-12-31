package com.dangdang.cymbal.web.controller;

import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.dangdang.cymbal.service.node.service.process.NodeProcessService;
import com.dangdang.cymbal.web.object.converter.NodeConverter;
import com.dangdang.cymbal.web.object.dto.NodeDTO;
import org.apache.poi.util.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * Node controller.
 *
 * @auther GeZhen
 */
@Controller
public class NodeController extends BaseController {

    private static final String FILE_UPLOAD_DIR = System.getProperty("java.io.tmpdir");

    public NodeController() {
        File dir = new File(FILE_UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Resource
    private NodeEntityService nodeEntityService;

    @Resource
    private NodeProcessService nodeProcessService;

    @Resource
    private NodeConverter nodeConverter;

    @GetMapping("/nodes/page")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView page(final HttpServletRequest request, final HttpServletResponse response) {
        return new ModelAndView("node/nav");
    }

    // TODO: Change to return DTO.
    @GetMapping("/nodes")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public List<Node> queryAll() {
        List<Node> nodes = nodeEntityService.list();
        return nodes;
    }

    @PostMapping("/nodes")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<Integer> createNodesFromExcel(@RequestParam("uploadFile") final MultipartFile uploadFile)
            throws IOException {
        String filePath = copyToUploadDir(uploadFile);
        int nodeCount = nodeProcessService.createNodesFromExcel(filePath);
        return ResponseEntity.created(URI.create("/nodes")).body(nodeCount);
    }

    @PatchMapping("/nodes")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public List<NodeDTO> initNodes(@RequestBody final Set<Integer> nodeIds) {
        List<Node> failedNodes = nodeProcessService.initNodes(nodeIds);
        return nodeConverter.posToDtos(failedNodes);
    }

    @PutMapping("/nodes/{nodeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public void updateNode(@PathVariable final String nodeId, @RequestBody final NodeDTO nodeDTO) {
        Node node = nodeConverter.dtoToPo(nodeDTO);
        nodeProcessService.updateNode(node);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NullPointerException.class)
    public void notFound() {

    }

    private String copyToUploadDir(final MultipartFile uploadFile) throws IOException {
        String newFileName = getNewFileName(uploadFile.getOriginalFilename());
        File newFile = new File(FILE_UPLOAD_DIR, newFileName);
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
        try (FileOutputStream output = new FileOutputStream(newFile)) {
            IOUtils.copy(uploadFile.getInputStream(), output);
            return newFile.getAbsolutePath();
        }
    }

    private String getNewFileName(final String originalFilename) {
        return DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + "_" + originalFilename;
    }
}
