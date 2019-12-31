package com.dangdang.cymbal.service.cluster.service.process.impl;

import com.dangdang.cymbal.domain.po.ApplicationForm;
import com.dangdang.cymbal.domain.po.ApplicationFormStatus;
import com.dangdang.cymbal.service.cluster.service.entity.ApplicationFormEntityService;
import com.dangdang.cymbal.service.cluster.service.process.ApplicationFormProcessService;
import com.dangdang.cymbal.service.cluster.service.process.ClusterProcessService;
import com.dangdang.cymbal.service.util.service.MailService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Implement of {@link ApplicationFormProcessService}.
 *
 * @auther GeZhen
 */
@Slf4j
@Service
public class ApplicationFormProcessServiceImpl implements ApplicationFormProcessService {

    private static final String MAIL_TITLE_CREATE = "收到新的申请单";

    @Resource
    private ApplicationFormEntityService applicationFormEntityService;

    @Resource
    private MailService mailService;

    @Resource
    private ClusterProcessService clusterProcessService;

    private final static Interner<String> INTERNER = Interners.newWeakInterner();

    @Override
    public Integer saveRedisApplicationForm(final ApplicationForm applicationForm) {
        applicationFormEntityService.saveOrUpdate(applicationForm);
        sendMailToAdminIfNeeded(applicationForm);
        return applicationForm.getId();
    }

    private void sendMailToAdminIfNeeded(final ApplicationForm applicationForm) {
        if (ApplicationFormStatus.PENDING.equals(applicationForm.getStatus())) {
            mailService.sendHtmlMailToAdmin(MAIL_TITLE_CREATE, getMailContent(applicationForm));
        }
    }

    private String getMailContent(final ApplicationForm form) {
        StringBuilder content = new StringBuilder();
        content.append("申请人: ").append(form.getApplicantCnName()).append("\n");
        content.append("集群描述: ").append(form.getDescription()).append("\n");
        content.append("所属系统: ").append(form.getBelongSystem()).append("\n");
        content.append("集群模式: ").append(form.getRedisMode()).append("\n");
        content.append("主节点数: ").append(form.getMasterCount()).append("\n");
        content.append("每个主节点从节点数: ").append(form.getReplicaCount()).append("\n");
        content.append("每个节点缓存大小: ").append(form.getCacheSize()).append("GB").append("\n");
        content.append("总缓存大小: ").append(form.getCacheSize() * form.getMasterCount()).append("GB").append("\n");
        content.append("总缓存大小(加从节点): ")
                .append(form.getCacheSize() * form.getMasterCount() * (form.getReplicaCount() + 1)).append("GB")
                .append("\n");
        content.append("持久化方式: ").append(form.getRedisPersistenceType()).append("\n");
        content.append("集群密码: ").append(form.getPassword()).append("\n");
        content.append("是否需要sentinel: ").append(form.isEnableSentinel()).append("\n");
        content.append("环境类型: ").append(form.getEnv()).append("\n");
        content.append("数据中心: ").append(form.getIdc()).append("\n");
        return content.toString();
    }

    @Override
    @Transactional
    public void denyRedisApplicationForm(final Integer applicationFormId, final String approvalOpinion) {
        ApplicationForm applicationForm = applicationFormEntityService.getById(applicationFormId);
        updateRedisApplicationFormToDenied(applicationForm, approvalOpinion);
    }

    private void updateRedisApplicationFormToDenied(final ApplicationForm applicationForm,
            final String approvalOpinion) {
        applicationForm.setStatus(ApplicationFormStatus.DENIED);
        applicationForm.setApprovalOpinion(approvalOpinion);
        applicationFormEntityService.updateById(applicationForm);
    }

    @Override
    public void updateRedisApplicationForm(final ApplicationForm applicationForm) {
        checkCanUpdate(applicationForm);
        applicationFormEntityService.updateById(applicationForm);
        sendMailToAdminIfNeeded(applicationForm);
    }

    private void checkCanUpdate(final ApplicationForm applicationForm) {
        ApplicationForm applicationFormOld = applicationFormEntityService.getById(applicationForm.getId());
        Preconditions.checkState(ApplicationFormStatus.DRAFT.equals(applicationFormOld.getStatus()));
    }

    @Override
    @Transactional
    public String approveRedisApplicationForm(final ApplicationForm applicationForm) {
        // TODO: Better lock
        synchronized (INTERNER.intern(ApplicationForm.class.getName() + applicationForm.getId())) {
            updateRedisApplicationFormToApproved(applicationForm);
            return clusterProcessService.createRedisClusterByRedisApplicationForm(applicationForm);
        }
    }

    private void updateRedisApplicationFormToApproved(final ApplicationForm applicationForm) {
        ApplicationForm applicationFormOld = applicationFormEntityService.getById(applicationForm.getId());
        checkCanApprove(applicationFormOld);
        applicationForm.setStatus(ApplicationFormStatus.APPROVED);
        applicationFormEntityService.updateById(applicationForm);
    }

    private void checkCanApprove(final ApplicationForm applicationForm) {
        ApplicationForm applicationFormOld = applicationFormEntityService.getById(applicationForm.getId());
        Preconditions.checkState(ApplicationFormStatus.PENDING.equals(applicationFormOld.getStatus()));
    }
}
