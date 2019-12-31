package com.dangdang.cymbal.mybatisplus;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuration for mybatis plus.
 *
 * @auther GeZhen
 */
@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.dangdang.cymbal.**.mapper")
public class MybatisplusConfiguration {

    /**
     * Plugin of pagination.
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor().setCountSqlParser(new JsqlParserCountOptimize(true));
    }
}
