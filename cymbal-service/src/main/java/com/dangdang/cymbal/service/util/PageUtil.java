package com.dangdang.cymbal.service.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Util for page between Spring-Data and Mybatis-Plus.
 *
 * @auther GeZhen
 */
public class PageUtil {

    public static <T> IPage<T> convertToMybatisPage(final Pageable pageable) {
        final Page page = new Page(pageable.getPageNumber() + 1, pageable.getPageSize());
        pageable.getSort().get().forEach(each -> {
            if (each.isAscending()) {
                page.addOrder(OrderItem.asc(each.getProperty()));
            } else {
                page.addOrder(OrderItem.desc(each.getProperty()));
            }
        });
        return page;
    }

    public static <T> org.springframework.data.domain.Page convertToSpringPage(final IPage page,
            final Pageable pageable) {
        return new PageImpl(page.getRecords(), pageable, page.getTotal());
    }
}
