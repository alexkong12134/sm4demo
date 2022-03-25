package com.alex.sm4demo.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author Alex
 * @since 2022-03-24
 */
@Getter
@Setter
@TableName("excel_person")
public class ExcelPerson implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("IDNUM")
    private Integer idnum;

    @TableField("uname")
    private String uname;

    @TableField("addr")
    private String addr;

    @TableField("age")
    private String age;

    @TableField("sex")
    private String sex;

    @TableField("idcard")
    private String idcard;

    @TableField("createtime")
    private String createtime;

    @TableField("updatatime")
    private String updatatime;

    @TableField("showtext")
    private String showtext;

    @TableField("phone")
    private String phone;

    @TableField("work_addr")
    private String workAddr;


}
