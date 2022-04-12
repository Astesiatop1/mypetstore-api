package org.csu.mypetstore.api.controller;
import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.Order;
import org.csu.mypetstore.api.service.OrderService;
import org.csu.mypetstore.api.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/orders/")
class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("")
    @ResponseBody
    public CommonResponse<OrderVO> insertOrder(Order order){
        System.out.println(order.getOrderId());
        return orderService.insertOrder(order);
    }

    @GetMapping("numbers/{id}")//只能搜到有Lineitem的
    @ResponseBody
    public CommonResponse<OrderVO> getOrder(@PathVariable("id") int orderId){
        try {
            return orderService.getOrder(orderId);
        }catch (Exception exception){
            return CommonResponse.createForSuccessMessage("输入的订单号不是数字");
        }
    }

    @GetMapping("{id}")//ok
    @ResponseBody
    public CommonResponse<List<Order>> getOrdersByUsername(@PathVariable("id") String username) {
        return orderService.getOrdersByUsername(username);
    }
}
