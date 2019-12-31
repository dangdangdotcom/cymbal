package com.dangdang.cymbal.web.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * Controller for download.
 *
 * @auther GeZhen
 */
@Controller
public class DownloadController {

    @GetMapping(value = "/download/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public InputStreamSource downloadTemplate(final HttpServletRequest request, final HttpServletResponse response,
            @PathVariable String fileName) {
        if ("Template".equalsIgnoreCase(fileName)) {
            response.setHeader("Content-Disposition", "attachment;filename=node-upload-template.xlsx");
            return new InputStreamResource(this.getFileInputStream("static/doc/node-upload-template.xlsx"));
        } else if ("help-doc".equalsIgnoreCase(fileName)) {
            response.setHeader("Content-Disposition", "attachment;filename=cymbal-manual.docx");
            return new InputStreamResource(this.getFileInputStream("static/doc/cymbal-manual.docx"));
        }
        return null;
    }

    private InputStream getFileInputStream(final String fileName) {
        return this.getClass().getClassLoader().getResourceAsStream(fileName);
    }
}
