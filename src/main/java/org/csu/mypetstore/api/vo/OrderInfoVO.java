package org.csu.mypetstore.api.vo;

import lombok.Data;

import java.util.Date;

@Data
public class OrderInfoVO {
    private String shipAddress1;
    private String shipAddress2;
    private String shipCity;
    private String shipState;
    private String shipZip;
    private String shipCountry;
    private String billAddress1;
    private String billAddress2;
    private String billCity;
    private String billState;
    private String billZip;
    private String billCountry;
    private String billToFirstName;
    private String billToLastName;
    private String shipToFirstName;
    private String shipToLastName;
    private String creditCard;
    private String expiryDate;
    private String cardType;
    private Date time;
}
