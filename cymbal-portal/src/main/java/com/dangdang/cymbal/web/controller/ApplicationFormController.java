package com.dangdang.cymbal.web.controller;


import com.dangdang.cymbal.common.spi.UserProcessService;
import com.dangdang.cymbal.domain.po.ApplicationForm;
import com.dangdang.cymbal.domain.po.ApplicationFormStatus;
import com.dangdang.cymbal.service.cluster.service.entity.ApplicationFormEntityService;
import com.dangdang.cymbal.service.cluster.service.process.ApplicationFormProcessService;
import com.dangdang.cymbal.service.node.exception.NotEnoughResourcesException;
import com.dangdang.cymbal.web.object.converter.PageConverter;
import com.dangdang.cymbal.web.object.dto.DataTablePageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.security.Principal;
import java.util.Objects;

@Controller
@Slf4j
public class ApplicationFormController extends BaseController {

    @Resource
    private ApplicationFormEntityService applicationFormEntityService;

    @Resource
    private ApplicationFormProcessService applicationFormProcessService;

    @Resource
    private PageConverter<ApplicationForm> pageConverter;

    @Resource
    private UserProcessService userProcessService;

    @GetMapping(value = "/application-form/page")
    public ModelAndView page(@RequestParam(required = false) ApplicationFormStatus status) {
        ModelAndView modelAndView = new ModelAndView("applicationForm/nav/application_form_nav");
        if (Objects.nonNull(status)) {
            modelAndView.addObject("status", status.name());
        }
        return modelAndView;
    }

    @GetMapping(value = "/import/application-form/page")
    public ModelAndView importPage() {
        ModelAndView modelAndView = new ModelAndView("applicationForm/nav/redis_import_nav");
        return modelAndView;
    }

    @GetMapping(value = "/user/application-forms")
    @ResponseBody
    public DataTablePageDTO<ApplicationForm> queryByUserNameWithPage(@AuthenticationPrincipal Principal principal,
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer length,
            @RequestParam Integer draw) {
        Page<ApplicationForm> page = applicationFormEntityService
                .queryByUserNameWithPage(principal.getName(), createPage(start, length));
        DataTablePageDTO<ApplicationForm> pageDTO = pageConverter.toDTO(page, draw);
        return pageDTO;
    }

    @GetMapping(value = "/application-forms")
    @ResponseBody
    public DataTablePageDTO<ApplicationForm> queryByStatusWithPage(@RequestParam ApplicationFormStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer start,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) Integer length,
            @RequestParam Integer draw) {
        Page<ApplicationForm> page = applicationFormEntityService
                .queryByStatusWithPage(status, createPage(start, length));
        DataTablePageDTO<ApplicationForm> pageDTO = pageConverter.toDTO(page, draw);
        return pageDTO;
    }

    @PostMapping(value = "/application-forms")
    @ResponseBody
    public void createApplicationForm(@RequestBody ApplicationForm applicationForm,
            @AuthenticationPrincipal Principal principal) {
        if (Objects.isNull(applicationForm.getApplicantEnName())) {
            applicationForm.setApplicantEnName(principal.getName());
            applicationForm.setApplicantCnName(userProcessService.getUserCnName(principal.getName()));
        }
        applicationFormProcessService.saveRedisApplicationForm(applicationForm);
    }

    @PutMapping(value = "/application-forms")
    @ResponseBody
    public ResponseEntity<String> updateApplicationForm(@RequestBody ApplicationForm applicationForm) {
        try {
            applicationFormProcessService.updateRedisApplicationForm(applicationForm);
        } catch (IllegalStateException e) {
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/application-forms/{applicationFormId}/deny")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public void denyApplicationForm(@PathVariable Integer applicationFormId, @RequestBody String approvalOpinion) {
        applicationFormProcessService.denyRedisApplicationForm(applicationFormId, approvalOpinion);
    }

    @PostMapping(value = "/application-forms/{applicationFormId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<String> approveApplicationForm(@RequestBody ApplicationForm applicationForm) {
        try {
            String clusterId = applicationFormProcessService.approveRedisApplicationForm(applicationForm);
            return ResponseEntity.ok(clusterId);
        } catch (NotEnoughResourcesException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    private Pageable createPage(final Integer start, final Integer length) {
        Sort sort = Sort.by(Sort.Order.desc("last_changed_date"));
        return PageRequest.of(start / length, length, sort);
    }
}
