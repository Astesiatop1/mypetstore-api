package org.csu.mypetstore.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("cart")
public class Cart {
    @TableId(value = "USERNAME", type = IdType.INPUT)
    private String userid;
    @TableField(value = "ITEM_ID")
    private String itemid;
    @TableField(value = "PRODUCT_ID")
    private String productid;
    @TableField(value = "ATTRIBUTE1")
    private String description;
    @TableField(value = "IS_STOCK")
    private String instock;
    @TableField(value = "QUANTITY")
    private Integer quantity;
    @TableField(value = "LIST_PRICE")
    private BigDecimal listprice;
    @TableField(value = "TOTAL_COST")
    private BigDecimal totalcost;
}
