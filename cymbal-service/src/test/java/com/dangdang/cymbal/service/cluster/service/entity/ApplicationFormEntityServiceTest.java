package com.dangdang.cymbal.service.cluster.service.entity;

import com.dangdang.cymbal.domain.po.ApplicationForm;
import com.dangdang.cymbal.domain.po.ApplicationFormStatus;
import com.dangdang.cymbal.domain.po.Environment;
import com.dangdang.cymbal.domain.po.InternetDataCenter;
import com.dangdang.cymbal.domain.po.RedisMode;
import com.dangdang.cymbal.domain.po.RedisPersistenceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link ApplicationFormEntityService}.
 *
 * @auther GeZhen
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationFormEntityServiceTest {

    @Resource
    ApplicationFormEntityService applicationFormEntityService;

    @Before
    public void setUp() {
    }

    @Test
    public void save() {
        ApplicationForm applicationForm = buildApplicationForm();
        applicationFormEntityService.save(applicationForm);
        assertThat(applicationForm.getId()).isNotNull();
    }

    @Test
    public void queryByStatusWithPage() {
        for (int i = 0; i < 10; i++) {
            applicationFormEntityService.save(buildApplicationForm());
        }
        Page<ApplicationForm> page = applicationFormEntityService
                .queryByStatusWithPage(ApplicationFormStatus.DRAFT, PageRequest.of(1, 5));
        assertThat(page.getTotalElements()).isEqualTo(10L);
        assertThat(page.getContent().size()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    private ApplicationForm buildApplicationForm() {
        ApplicationForm pojo = new ApplicationForm();
        pojo.setApplicantEnName("nobody");
        pojo.setApplicantCnName("无名氏");
        pojo.setEnv(Environment.TEST);
        pojo.setIdc(InternetDataCenter.TEST);
        pojo.setRedisMode(RedisMode.CLUSTER);
        pojo.setRedisVersion("4.0.12");
        pojo.setCacheSize(4);
        pojo.setMasterCount(3);
        pojo.setReplicaCount(1);
        pojo.setRedisPersistenceType(RedisPersistenceType.RDB);
        pojo.setEnableSentinel(false);
        pojo.setPassword("b840fc02d524045429941cc15f59e41cb7be6c52");
        pojo.setDescription("测试申请单");
        pojo.setStatus(ApplicationFormStatus.DRAFT);

        return pojo;
    }
}