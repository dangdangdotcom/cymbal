package com.dangdang.cymbal.domain.bo;

import com.dangdang.cymbal.domain.po.ConfigDetail;
import com.dangdang.cymbal.domain.po.ConfigDict;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * BO of {@link ConfigDetail}.
 *
 * @auther GeZhen
 */
@Getter
@Setter
@ToString
@Builder
public class ConfigDetailBO {

    private ConfigDetail configDetail;

    private ConfigDict configDict;
}
