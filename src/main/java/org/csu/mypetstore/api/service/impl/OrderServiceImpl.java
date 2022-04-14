package org.csu.mypetstore.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.*;
import org.csu.mypetstore.api.persistence.*;
import org.csu.mypetstore.api.service.OrderService;
import org.csu.mypetstore.api.vo.LineItemVO;
import org.csu.mypetstore.api.vo.OrderInfoVO;
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
    private CartItemMapper cartItemMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private SequenceMapper sequenceMapper;
    @Autowired
    private OrderInfoMapper orderInfoMapper;


    public CommonResponse<List<OrderVO>> getOrdersByUserName(String username){
        QueryWrapper<Order> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("userid",username);
        List<Order> orderList=orderMapper.selectList(queryWrapper);
        if(orderList.size()==0){
            return CommonResponse.createForError("该用户没有已完成的订单");
        }

        List<OrderVO> orderVOList=new ArrayList<>();
        for(Order order : orderList){
            OrderStatus orderStatus=orderStatusMapper.selectById(order.getOrderId());
            if(orderStatus==null){
                return CommonResponse.createForError("查询失败，orderstatus没有信息");
            }
            List<LineItemVO> lineItemVOList=getLineItems(order.getOrderId()).getData();
            OrderVO orderVO=orderToOrderVO(order,orderStatus,lineItemVOList);
            orderVOList.add(orderVO);
        }
        return CommonResponse.createForSuccess(orderVOList);
    }

    @Override
    public CommonResponse<List<OrderVO>> getOrdersByOrderId(int orderId) {
        Order order = orderMapper.selectById(orderId);
        if(order==null){
            return CommonResponse.createForError("查询失败，order没有信息");
        }

        OrderStatus orderStatus=orderStatusMapper.selectById(orderId);
        if(orderStatus==null){
            return CommonResponse.createForError("查询失败，orderstatus没有信息");
        }

        List<OrderVO> orderVOList=new ArrayList<>();
        List<LineItemVO> lineItemVOList=getLineItems(order.getOrderId()).getData();
        OrderVO orderVO=orderToOrderVO(order,orderStatus,lineItemVOList);
        orderVOList.add(orderVO);
        return CommonResponse.createForSuccess(orderVOList);
    }

    @Override
    public CommonResponse setOrderInfo(OrderInfo orderInfo) {
        OrderInfoVO orderInfoVO1=new OrderInfoVO();
        if(orderInfo ==null){
            return CommonResponse.createForError("订单信息获取失败");
        }
        orderInfoVO1.setExpiryDate(orderInfo.getExpiryDate());
        orderInfoVO1.setCreditCard(orderInfo.getCreditCard());
        orderInfoVO1.setCardType(orderInfo.getCardType());
        orderInfoVO1.setBillToLastName(orderInfo.getBillToLastName());
        orderInfoVO1.setBillToFirstName(orderInfo.getBillToFirstName());
        orderInfoVO1.setBillZip(orderInfo.getBillZip());
        orderInfoVO1.setBillState(orderInfo.getBillState());
        orderInfoVO1.setBillCity(orderInfo.getBillCity());
        orderInfoVO1.setBillCountry(orderInfo.getBillCountry());
        orderInfoVO1.setBillAddress1(orderInfo.getBillAddress1());
        orderInfoVO1.setBillAddress2(orderInfo.getBillAddress2());
        orderInfoVO1.setShipToLastName(orderInfo.getShipToLastName());
        orderInfoVO1.setShipToFirstName(orderInfo.getShipToFirstName());
        orderInfoVO1.setShipZip(orderInfo.getShipZip());
        orderInfoVO1.setShipState(orderInfo.getShipState());
        orderInfoVO1.setShipCity(orderInfo.getShipCity());
        orderInfoVO1.setShipCountry(orderInfo.getShipCountry());
        orderInfoVO1.setShipAddress1(orderInfo.getShipAddress1());
        orderInfoVO1.setShipAddress2(orderInfo.getShipAddress2());
        orderInfoVO1.setTime(orderInfo.getOrderDate());

        return CommonResponse.createForSuccess(orderInfoVO1);
    }

    @Override
    public CommonResponse<List<OrderVO>> createOrder(String username) {

        QueryWrapper<CartItem> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("USERNAME",username);
        List<CartItem> cartItemList=cartItemMapper.selectList(queryWrapper);
        if(cartItemList.size()==0){
            return CommonResponse.createForError("购物车中没有商品，无法生成订单");
        }

        QueryWrapper<OrderInfo> queryWrapper2=new QueryWrapper<>();
        queryWrapper2.eq("userid",username);
        OrderInfo orderInfo=orderInfoMapper.selectOne(queryWrapper2);
        if(orderInfo==null){
            return CommonResponse.createForError("获取订单信息失败");
        }

        QueryWrapper<Sequence> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("name","ordernum");
        Sequence sequence =sequenceMapper.selectOne(queryWrapper1);

        Order order=new Order();

        order.setOrderId(sequence.getNextId()+1);
        order.setUsername(username);
        order.setOrderDate(orderInfo.getOrderDate());
        order.setBillAddress1(orderInfo.getBillAddress1());
        order.setBillAddress2(orderInfo.getBillAddress2());
        order.setBillCity(orderInfo.getBillCity());
        order.setBillZip(orderInfo.getBillZip());
        order.setBillState(orderInfo.getBillState());
        order.setBillCountry(orderInfo.getBillCountry());
        order.setCourier("UPS");

        BigDecimal totalPrice= new BigDecimal(0);
        int lineNum=1;
        for(CartItem cartItem : cartItemList){
            totalPrice=totalPrice.add(cartItem.getTotalcost());
            LineItem lineItem=new LineItem();
            lineItem.setOrderId(sequence.getNextId()+1);
            lineItem.setLineNum(lineNum);
            lineItem.setItemId(cartItem.getItemid());
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setUnitPrice(cartItem.getListprice());
            lineNum++;
            lineItemMapper.insert(lineItem);
        }
        order.setTotalPrice(totalPrice);

        order.setBillToFirstName(orderInfo.getBillToFirstName());
        order.setBillToLastName(orderInfo.getBillToLastName());
        order.setCreditCard(orderInfo.getCreditCard());
        order.setExpiryDate(orderInfo.getExpiryDate());
        order.setCardType(orderInfo.getCardType());

        order.setShipToFirstName(orderInfo.getShipToFirstName());
        order.setShipToLastName(orderInfo.getShipToLastName());
        order.setShipAddress1(orderInfo.getShipAddress1());
        order.setShipAddress2(orderInfo.getShipAddress2());
        order.setShipCity(orderInfo.getShipCity());
        order.setShipZip(orderInfo.getShipZip());
        order.setShipState(orderInfo.getShipState());
        order.setShipCountry(orderInfo.getShipCountry());

        order.setLocale("CA");
        orderMapper.insert(order);

        OrderStatus orderStatus=new OrderStatus();
        orderStatus.setOrderId(sequence.getNextId()+1);
        orderStatus.setLineNum(sequence.getNextId()+1);
        orderStatus.setTimestamp(orderInfo.getOrderDate());
        orderStatus.setStatus("P");

        List<OrderVO> orderVOList=new ArrayList<>();
        List<LineItemVO> lineItemVOList=getLineItems(order.getOrderId()).getData();
        OrderVO orderVO=orderToOrderVO(order,orderStatus,lineItemVOList);
        orderVOList.add(orderVO);
        orderStatusMapper.insert(orderStatus);

        sequence.setNextId(sequence.getNextId()+1);
        sequenceMapper.update(sequence,queryWrapper1);

        cartItemMapper.delete(queryWrapper);

        orderInfoMapper.delete(queryWrapper2);

        return CommonResponse.createForSuccess(orderVOList);
    }


    @Override
    public CommonResponse<List<LineItemVO>> getLineItems(int orderId) {
        QueryWrapper<LineItem> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("orderid",orderId);
        List<LineItem> lineItemList=lineItemMapper.selectList(queryWrapper);

        List<LineItemVO> lineItemVOList=new ArrayList<>();
        for(LineItem lineItem : lineItemList){
            LineItemVO lineItemVO=lineItemToVO(lineItem);
            lineItemVOList.add(lineItemVO);
        }
        return CommonResponse.createForSuccess(lineItemVOList);
    }


    private LineItemVO lineItemToVO(LineItem lineItem) {

        LineItemVO lineItemVO = new LineItemVO();
        lineItemVO.setOrderId(lineItem.getOrderId());
        lineItemVO.setLineNumber(lineItem.getLineNum());
        lineItemVO.setItemId(lineItem.getItemId());
        lineItemVO.setQuantity(lineItem.getQuantity());
        lineItemVO.setUnitPrice(lineItem.getUnitPrice());

        lineItemVO.calculateTotal();
        return lineItemVO;
    }


    private OrderVO orderToOrderVO(Order order, OrderStatus orderStatus, List<LineItemVO> lineItems){
        OrderVO orderVO=new OrderVO();
        orderVO.setOrderId(order.getOrderId());
        orderVO.setUsername(order.getUsername());
        orderVO.setOrderDate(order.getOrderDate());
        orderVO.setShipAddress1(order.getShipAddress1());
        orderVO.setShipAddress2(order.getShipAddress2());
        orderVO.setShipCity(order.getShipCity());
        orderVO.setShipState(order.getShipState());
        orderVO.setShipZip(order.getShipZip());
        orderVO.setShipCountry(order.getShipCountry());
        orderVO.setBillAddress1(order.getBillAddress1());
        orderVO.setBillAddress2(order.getBillAddress2());
        orderVO.setBillCity(order.getBillCity());
        orderVO.setBillState(order.getBillState());
        orderVO.setBillZip(order.getBillZip());
        orderVO.setBillCountry(order.getBillCountry());
        orderVO.setCourier(order.getCourier());
        orderVO.setTotalPrice(order.getTotalPrice());
        orderVO.setBillToFirstName(order.getBillToFirstName());
        orderVO.setBillToLastName(order.getBillToLastName());
        orderVO.setShipToFirstName(order.getShipToFirstName());
        orderVO.setShipToLastName(order.getShipToLastName());
        orderVO.setCreditCard(order.getCreditCard());
        orderVO.setExpiryDate(order.getExpiryDate());
        orderVO.setCardType(order.getCardType());
        orderVO.setLocale(order.getLocale());

        // orderstaus 注入
        orderVO.setStatus(orderStatus.getStatus());
        // LineItems
        orderVO.setLineItems(lineItems);

        return orderVO;
    }

}
