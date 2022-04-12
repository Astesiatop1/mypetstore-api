package org.csu.mypetstore.api.vo;

import lombok.Data;
import org.csu.mypetstore.api.entity.LineItem;
import org.csu.mypetstore.api.entity.Order;

import java.util.List;

@Data
public class OrderVO {
    //order表中内容
//    private int orderId;
//    private String username;
//    private String orderDate;
//    private String shipAddress1;
//    private String shipAddress2;
//    private String shipCity;
//    private String shipState;
//    private String shipZip;
//    private String shipCountry;
//    private String billAddress1;
//    private String billAddress2;
//    private String billCity;
//    private String billState;
//    private String billZip;
//    private String billCountry;
//    private String courier;
//    private BigDecimal totalPrice;
//    private String billToFirstName;
//    private String billToLastName;
//    private String shipToFirstName;
//    private String shipToLastName;
//    private String creditCard;
//    private String expiryDate;
//    private String cardType;
//    private String locale;
    private Order order;

    //lineItem
    private List<LineItem> lineItems;
}

