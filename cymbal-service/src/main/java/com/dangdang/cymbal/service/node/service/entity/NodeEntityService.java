package com.dangdang.cymbal.service.node.service.entity;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dangdang.cymbal.domain.po.Node;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @auther GeZhen
 */
public interface NodeEntityService extends IService<Node> {

    /**
     * Get node by ip.
     *
     * @param ip ip
     * @return node of ip
     */
    Node getByIp(String ip);

    /**
     * Query nodes with page.
     *
     * @param pageable pageable
     * @return page query result
     */
    Page<Node> queryWithPage(Pageable pageable);
}
