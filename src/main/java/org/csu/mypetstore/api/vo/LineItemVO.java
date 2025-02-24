package org.csu.mypetstore.api.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LineItemVO {
    // lineitem 表字段注入
    private int orderId;
    private int lineNumber;
    private String itemId;
    private int quantity;
    private BigDecimal unitPrice;
    private String description;

    // 另外计算注入
    private BigDecimal total = new BigDecimal(0);

    // 计算total的方法
    public void calculateTotal() {
        if (itemId != null && unitPrice != null) {
            total = unitPrice.multiply(new BigDecimal(this.quantity));
        }
    }

}
