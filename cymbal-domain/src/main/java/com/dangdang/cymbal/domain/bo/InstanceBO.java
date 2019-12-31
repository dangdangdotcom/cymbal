package com.dangdang.cymbal.domain.bo;

import com.dangdang.cymbal.domain.po.Node;
import com.dangdang.cymbal.domain.po.Instance;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * BO of {@link Instance}.
 * Include self redis server instance, master and node model.
 *
 * @auther GeZhen
 */
@Getter
@Setter
@ToString
@Builder
public class InstanceBO {

    private Instance self;

    private InstanceBO master;

    private Node node;

    transient private String password;

    private int cacheSize;
}
