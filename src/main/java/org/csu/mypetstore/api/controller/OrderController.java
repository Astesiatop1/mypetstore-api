package org.csu.mypetstore.api.controller;

import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.Order;
import org.csu.mypetstore.api.service.CartService;
import org.csu.mypetstore.api.service.OrderService;
import org.csu.mypetstore.api.vo.CartVO;
import org.csu.mypetstore.api.vo.OrderInfoVO;
import org.csu.mypetstore.api.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/orders/")
class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @GetMapping("user/{username}/orders")//completely OK
    @ResponseBody
    public CommonResponse<List<OrderVO>> getOrdersByUserName(@PathVariable("username") String username) {
        return orderService.getOrdersByUserName(username);
    }

    //通过orderId获取对应订单详情
    @GetMapping("user/orders/{id}")//completely OK
    @ResponseBody
    public CommonResponse<List<OrderVO>> getOrdersByOrderId(@PathVariable("id") int orderId) {
        return orderService.getOrdersByOrderId(orderId);
    }

    @PostMapping("neworder/{username}")
    @ResponseBody
    public CommonResponse<List<OrderVO>> createOrderByUsername(@PathVariable("username") String username,HttpSession session) {
        OrderInfoVO orderInfoVOBill=(OrderInfoVO) session.getAttribute("orderInfoBill");
        OrderInfoVO orderInfoVOShip=(OrderInfoVO) session.getAttribute("orderInfoShip");
        session.setAttribute("cart",null);
        session.setAttribute("orderInfoBill",null);
        session.setAttribute("orderInfoShip",null);
        return orderService.createOrder(username,orderInfoVOBill,orderInfoVOShip);
    }

    @PostMapping("setOrderInfoBill")
    @ResponseBody
    public CommonResponse setOrderInfoBill(
            @RequestParam String billAddress1,
            @RequestParam String billAddress2,
            @RequestParam String billCity,
            @RequestParam String billState,
            @RequestParam String billZip,
            @RequestParam String billCountry,
            @RequestParam String creditCard,
            @RequestParam String expiryDate,
            @RequestParam String cardType,
            @RequestParam String billToFirstName,
            @RequestParam String billToLastName,
            HttpSession session) {

        OrderInfoVO orderInfoVOBill = new OrderInfoVO();
        orderInfoVOBill.setBillToFirstName(billToFirstName);
        orderInfoVOBill.setBillToLastName(billToLastName);
        orderInfoVOBill.setBillAddress1(billAddress1);
        orderInfoVOBill.setBillAddress2(billAddress2);
        orderInfoVOBill.setBillCity(billCity);
        orderInfoVOBill.setBillCountry(billCountry);
        orderInfoVOBill.setBillState(billState);
        orderInfoVOBill.setBillZip(billZip);
        orderInfoVOBill.setCardType(cardType);
        orderInfoVOBill.setCreditCard(creditCard);
        orderInfoVOBill.setExpiryDate(expiryDate);

        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String now= dateFormat.format(date);
        try {
            date = dateFormat.parse(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       orderInfoVOBill.setTime(date);

        session.setAttribute("orderInfoBill",orderInfoVOBill);
        session.setAttribute("orderInfoShip",null);
        CommonResponse response = orderService.setOrderInfo(orderInfoVOBill);
        return response;
    }


    @PostMapping("setOrderInfoShip")
    @ResponseBody
    public CommonResponse setOrderInfoShip(
            @RequestParam String shipAddress1,
            @RequestParam String shipAddress2,
            @RequestParam String shipCity,
            @RequestParam String shipState,
            @RequestParam String shipZip,
            @RequestParam String shipCountry,
            @RequestParam String shipToFirstName,
            @RequestParam String shipToLastName,
            HttpSession session) {

        OrderInfoVO orderInfoVO = new OrderInfoVO();

        orderInfoVO.setShipToFirstName(shipToFirstName);
        orderInfoVO.setShipToLastName(shipToLastName);
        orderInfoVO.setShipAddress1(shipAddress1);
        orderInfoVO.setShipAddress2(shipAddress2);
        orderInfoVO.setShipCity(shipCity);
        orderInfoVO.setShipCountry(shipCountry);
        orderInfoVO.setShipState(shipState);
        orderInfoVO.setShipZip(shipZip);

        session.setAttribute("orderInfoShip",orderInfoVO);
        CommonResponse response = orderService.setOrderInfo(orderInfoVO);
        return response;
    }
}