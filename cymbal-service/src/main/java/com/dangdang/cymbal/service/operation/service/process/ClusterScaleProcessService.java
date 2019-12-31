package com.dangdang.cymbal.service.operation.service.process;

import com.dangdang.cymbal.domain.bo.InstanceBO;
import com.dangdang.cymbal.domain.po.ClusterScale;

import java.util.List;

/**
 * Do scale for redis cluster.
 *
 * @author GeZhen
 */
public interface ClusterScaleProcessService {

    /**
     * Do scale for a redis cluster.
     *
     * @param clusterScale scale detail
     * @return new instances of scale
     */
    List<InstanceBO> doScale(ClusterScale clusterScale);

    /**
     * Retry last fail enlarge.
     *
     * @param scaleId scale id
     */
    void retryLastScale(Integer scaleId);
}
