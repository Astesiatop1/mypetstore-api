package org.csu.mypetstore.api.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.Order;
import org.csu.mypetstore.api.entity.OrderInfo;
import org.csu.mypetstore.api.persistence.OrderInfoMapper;
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
    private OrderInfoMapper orderInfoMapper;

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
    public CommonResponse<List<OrderVO>> createOrderByUsername(@PathVariable("username") String username) {
        return orderService.createOrder(username);
    }

    @PostMapping("billinfo/{username}")
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
            @PathVariable("username") String username) {

        OrderInfo orderInfoBill = new OrderInfo();
        orderInfoBill.setUsername(username);

        orderInfoBill.setBillToFirstName(billToFirstName);
        orderInfoBill.setBillToLastName(billToLastName);
        orderInfoBill.setBillAddress1(billAddress1);
        orderInfoBill.setBillAddress2(billAddress2);
        orderInfoBill.setBillCity(billCity);
        orderInfoBill.setBillCountry(billCountry);
        orderInfoBill.setBillState(billState);
        orderInfoBill.setBillZip(billZip);
        orderInfoBill.setCardType(cardType);
        orderInfoBill.setCreditCard(creditCard);
        orderInfoBill.setExpiryDate(expiryDate);

        orderInfoBill.setShipToFirstName(billToFirstName);
        orderInfoBill.setShipToLastName(billToLastName);
        orderInfoBill.setShipAddress1(billAddress1);
        orderInfoBill.setShipAddress2(billAddress2);
        orderInfoBill.setShipCity(billCity);
        orderInfoBill.setShipCountry(billCountry);
        orderInfoBill.setShipState(billState);
        orderInfoBill.setShipZip(billZip);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String dateStr = simpleDateFormat.format(date);
        Date date2 = new Date();
        try {
            date2 = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        orderInfoBill.setOrderDate(date2);

        if(orderInfoMapper.selectById(username)!=null){
            orderInfoMapper.deleteById(username);
        }
        orderInfoMapper.insert(orderInfoBill);

        CommonResponse response = orderService.setOrderInfo(orderInfoBill);
        return response;
    }


    @PostMapping("shipinfo/{username}")
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
            @PathVariable String username) {

        OrderInfo orderInfoShip = new OrderInfo();

        orderInfoShip.setShipToFirstName(shipToFirstName);
        orderInfoShip.setShipToLastName(shipToLastName);
        orderInfoShip.setShipAddress1(shipAddress1);
        orderInfoShip.setShipAddress2(shipAddress2);
        orderInfoShip.setShipCity(shipCity);
        orderInfoShip.setShipCountry(shipCountry);
        orderInfoShip.setShipState(shipState);
        orderInfoShip.setShipZip(shipZip);

        UpdateWrapper<OrderInfo> updateWrapper=new UpdateWrapper<>();
        updateWrapper.eq("userid",username).set("shipaddr1",shipAddress1)
                .set("shipaddr2",shipAddress2).set("shipcity",shipCity)
                .set("shipstate",shipState).set("shipzip",shipZip)
                .set("shipcountry",shipCountry).set("shiptofirstname",shipToFirstName)
                .set("shiptolastname",shipToLastName);
        orderInfoMapper.update(null,updateWrapper);

        CommonResponse response = orderService.setOrderInfo(orderInfoShip);
        return response;
    }

//    @GetMapping("viewOrderInfo")
//    @ResponseBody
//    public CommonResponse viewOrderInfo(HttpSession session){
//        OrderInfoVO orderInfoVOBill= (OrderInfoVO) session.getAttribute("orderInfoBill");
//        OrderInfoVO orderInfoVOShip= (OrderInfoVO) session.getAttribute("orderInfoShip");
//        return orderService.viewOrderInfo(orderInfoVOBill,orderInfoVOShip);
//    }
}