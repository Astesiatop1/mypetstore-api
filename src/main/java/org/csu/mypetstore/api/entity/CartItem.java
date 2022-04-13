package org.csu.mypetstore.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("cartitem")
public class CartItem {

    @TableId(value = "ID",type = IdType.INPUT)
    private int id;
    @TableField(value = "USERNAME")
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
