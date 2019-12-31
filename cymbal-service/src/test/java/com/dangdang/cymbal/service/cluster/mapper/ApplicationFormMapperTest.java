package com.dangdang.cymbal.service.cluster.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dangdang.cymbal.domain.po.ApplicationForm;
import com.dangdang.cymbal.domain.po.ApplicationFormStatus;
import com.dangdang.cymbal.domain.po.RedisPersistenceType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for {@link ApplicationFormMapper}
 *
 * @author GeZhen
 */
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class ApplicationFormMapperTest {

    @Resource
    ApplicationFormMapper applicationFormMapper;

    @Test
    public void insert() {
        ApplicationForm form = buildApplicationForm();
        assertThat(applicationFormMapper.insert(form)).isEqualTo(1);
    }

    @Test
    public void page() {
        for (int i = 0; i < 10; i++) {
            ApplicationForm form = buildApplicationForm();
            applicationFormMapper.insert(form);
        }
        Page<ApplicationForm> page = new Page<ApplicationForm>(1, 5);
        page.addOrder(new OrderItem());
        IPage<ApplicationForm> ipage = applicationFormMapper.selectPage(page, Wrappers.emptyWrapper());

        System.out.println("total: " + ipage.getTotal());
        System.out.println("current: " + ipage.getCurrent());
        System.out.println("page: " + ipage.getPages());

        page.getRecords().stream().forEach(System.out::println);

        assertThat(page.getRecords().size()).isEqualTo(page.getSize());
    }

    private ApplicationForm buildApplicationForm() {
        ApplicationForm pojo = new ApplicationForm();
        pojo.setApplicantEnName("dangdang");
        pojo.setApplicantCnName("当当");
        //        pojo.setEnv(Environment.TEST.toString());
        //        pojo.setIdc(InternetDataCenter.test.toString());
        //        pojo.setClusterMode(ServerMode.CLUSTER.toString());
        pojo.setRedisVersion("3.2.11");
        pojo.setCacheSize(4);
        pojo.setMasterCount(3);
        pojo.setReplicaCount(1);
        pojo.setRedisPersistenceType(RedisPersistenceType.NO);
        pojo.setEnableSentinel(true);
        pojo.setPassword("b840fc02d524045429941cc15f59e41cb7be6c52");
        pojo.setDescription("test application form");
        pojo.setStatus(ApplicationFormStatus.DRAFT);

        return pojo;
    }
}

