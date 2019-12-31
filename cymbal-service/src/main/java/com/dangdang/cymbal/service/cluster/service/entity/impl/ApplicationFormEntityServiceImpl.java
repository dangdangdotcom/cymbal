package com.dangdang.cymbal.service.cluster.service.entity.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.domain.po.ApplicationForm;
import com.dangdang.cymbal.domain.po.ApplicationFormStatus;
import com.dangdang.cymbal.service.cluster.mapper.ApplicationFormMapper;
import com.dangdang.cymbal.service.cluster.service.entity.ApplicationFormEntityService;
import com.dangdang.cymbal.service.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implement of ApplicationFormEntityService.
 *
 * @auther GeZhen
 */
@Service
public class ApplicationFormEntityServiceImpl extends ServiceImpl<ApplicationFormMapper, ApplicationForm>
        implements ApplicationFormEntityService {

    @Override
    public Page<ApplicationForm> queryByStatusWithPage(final ApplicationFormStatus status, final Pageable pageable) {
        return queryWithPage(this.lambdaQuery().eq(ApplicationForm::getStatus, status), pageable);
    }

    @Override
    public Page<ApplicationForm> queryByUserNameWithPage(final String userName, final Pageable pageable) {
        return queryWithPage(this.lambdaQuery().eq(ApplicationForm::getApplicantEnName, userName), pageable);
    }

    private Page<ApplicationForm> queryWithPage(final LambdaQueryChainWrapper<ApplicationForm> queryChainWrapper,
            final Pageable pageable) {
        IPage<ApplicationForm> mybatisPage = PageUtil.convertToMybatisPage(pageable);
        queryChainWrapper.page(mybatisPage);
        return PageUtil.convertToSpringPage(mybatisPage, pageable);
    }
}
