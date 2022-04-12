package org.csu.mypetstore.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.Cart;
import org.csu.mypetstore.api.entity.LineItem;
import org.csu.mypetstore.api.entity.Order;
import org.csu.mypetstore.api.persistence.CartMapper;
import org.csu.mypetstore.api.persistence.LineItemMapper;
import org.csu.mypetstore.api.persistence.OrderMapper;
import org.csu.mypetstore.api.service.OrderService;
import org.csu.mypetstore.api.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private LineItemMapper lineItemMapper;
    @Autowired
    private CartMapper cartMapper;

    @Override
    public CommonResponse<OrderVO> insertOrder(Order order) {
        if (order == null) {
            return CommonResponse.createForSuccessMessage("订单为空");
        }

        //获取当前购物车的内容
        String userId = order.getUsername();
        BigDecimal totalCost = new BigDecimal(0);

        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USERNAME", userId);
        List<Cart> cartList = cartMapper.selectList(queryWrapper);
        cartMapper.delete(queryWrapper);

        List<LineItem> lineItemList = new ArrayList<>();
        for (Cart cart : cartList) {
            LineItem lineItem = new LineItem();
            lineItem.setOrderId(order.getOrderId());
            lineItem.setLineNum(1000);
            lineItem.setItemId(cart.getItemid());
            lineItem.setQuantity(cart.getQuantity());
            lineItem.setUnitPrice(cart.getTotalcost());
            lineItemMapper.insert(lineItem);
            totalCost = totalCost.add(cart.getTotalcost());
            lineItemList.add(lineItem);
        }
        order.setTotalPrice(totalCost);
        orderMapper.insert(order);
        OrderVO orderVO = orderToOrderVO(order);

        return CommonResponse.createForSuccess(orderVO);
    }

    @Override
    public CommonResponse<OrderVO> getOrder(int orderId) {
        QueryWrapper<LineItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderid", orderId);
        List<LineItem> lineItemList = lineItemMapper.selectList(queryWrapper);
        if (lineItemList.size() == 0) {
            return CommonResponse.createForSuccessMessage("没有该订单号所对应的信息");
        }
        Order order = orderMapper.selectById(orderId);
        OrderVO orderVO = orderToOrderVO(order);
        return CommonResponse.createForSuccess(orderVO);
    }

    @Override
    public CommonResponse<List<Order>> getOrdersByUsername(String username) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userid", username);
        List<Order> orderList = orderMapper.selectList(queryWrapper);
        if (orderList.size() == 0) {
            return CommonResponse.createForSuccessMessage("没有该用户的订单");
        }
        return CommonResponse.createForSuccess(orderList);
    }


    private OrderVO orderToOrderVO(Order order){
        OrderVO orderVO = new OrderVO();
        orderVO.setOrder(order);
        QueryWrapper<LineItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("orderid", order.getOrderId());
        List<LineItem> lineItemList = lineItemMapper.selectList(queryWrapper);
        orderVO.setLineItems(lineItemList);
        return orderVO;
    }

}
