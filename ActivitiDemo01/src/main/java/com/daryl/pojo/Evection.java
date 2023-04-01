package com.daryl.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 出差申请的pojo对象
 */
@Data
public class Evection implements Serializable {
    private Long id;

    private String evctionName;

    /**
     * 出差的天数
     */
    private Double num;

    private Date beginDate;

    private Date endDate;

    private String destination;

    private String reason;
}
