package com.baomidou.mybatisplus.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description TODO
 * @ClassName BaseDomain
 * @Author littlehui
 * @Date 2021/6/24 23:03
 * @Version 1.0
 **/
@Data
public class BaseDomain implements Serializable {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("update_time")
    private Long updateTime;

    @TableField("create_time")
    private Long createTime;

}
