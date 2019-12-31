package com.dangdang.cymbal.service.node.service.entity.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.service.node.mapper.NodeMapper;
import com.dangdang.cymbal.service.node.service.entity.NodeEntityService;
import com.dangdang.cymbal.service.util.PageUtil;
import com.google.common.base.Preconditions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Entity service of node.
 *
 * @auther GeZhen
 */
@Service
public class NodeEntityServiceImpl extends ServiceImpl<NodeMapper, Node> implements NodeEntityService {

    @Override
    public Node getByIp(final String ip) {
        Preconditions.checkArgument(Objects.nonNull(ip));
        return this.lambdaQuery().eq(Node::getIp, ip).one();
    }

    @Override
    public Page<Node> queryWithPage(final Pageable pageable) {
        IPage<Node> mybatisPage = PageUtil.convertToMybatisPage(pageable);
        this.lambdaQuery().page(mybatisPage);
        return PageUtil.convertToSpringPage(mybatisPage, pageable);
    }
}
