package org.csu.mypetstore.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.*;
import org.csu.mypetstore.api.persistence.*;
import org.csu.mypetstore.api.service.OrderService;
import org.csu.mypetstore.api.vo.CartVO;
import org.csu.mypetstore.api.vo.LineItemVO;
import org.csu.mypetstore.api.vo.OrderInfoVO;
import org.csu.mypetstore.api.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public CommonResponse setOrderInfo(OrderInfoVO orderInfoVO) {
        OrderInfoVO orderInfoVO1=new OrderInfoVO();
        if(orderInfoVO==null){
            return CommonResponse.createForError("订单信息获取失败");
        }
        orderInfoVO1.setExpiryDate(orderInfoVO.getExpiryDate());
        orderInfoVO1.setCreditCard(orderInfoVO.getCreditCard());
        orderInfoVO1.setCardType(orderInfoVO.getCardType());
        orderInfoVO1.setBillToLastName(orderInfoVO.getBillToLastName());
        orderInfoVO1.setBillToFirstName(orderInfoVO.getBillToFirstName());
        orderInfoVO1.setBillZip(orderInfoVO.getBillZip());
        orderInfoVO1.setBillState(orderInfoVO.getBillState());
        orderInfoVO1.setBillCity(orderInfoVO.getBillCity());
        orderInfoVO1.setBillCountry(orderInfoVO.getBillCountry());
        orderInfoVO1.setBillAddress1(orderInfoVO.getBillAddress1());
        orderInfoVO1.setBillAddress2(orderInfoVO.getBillAddress2());
        orderInfoVO1.setShipToLastName(orderInfoVO.getShipToLastName());
        orderInfoVO1.setShipToFirstName(orderInfoVO.getShipToFirstName());
        orderInfoVO1.setShipZip(orderInfoVO.getShipZip());
        orderInfoVO1.setShipState(orderInfoVO.getShipState());
        orderInfoVO1.setShipCity(orderInfoVO.getShipCity());
        orderInfoVO1.setShipCountry(orderInfoVO.getShipCountry());
        orderInfoVO1.setShipAddress1(orderInfoVO.getShipAddress1());
        orderInfoVO1.setShipAddress2(orderInfoVO.getShipAddress2());
        orderInfoVO1.setTime(orderInfoVO.getTime());

        return CommonResponse.createForSuccess(orderInfoVO1);
    }


    @Override
    public CommonResponse<List<OrderVO>> createOrder(String username,
                                                     OrderInfoVO orderInfoVOBill,
                                                     OrderInfoVO orderInfoVOShip) {
        if(orderInfoVOBill==null){
            return CommonResponse.createForError("获取订单信息失败");
        }

        QueryWrapper<CartItem> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("USERNAME",username);
        List<CartItem> cartItemList=cartItemMapper.selectList(queryWrapper);
        if(cartItemList.size()==0){
            return CommonResponse.createForError("购物车中没有商品，无法生成订单");
        }

        QueryWrapper<Sequence> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("name","ordernum");
        Sequence sequence =sequenceMapper.selectOne(queryWrapper1);

        Order order=new Order();

        order.setOrderId(sequence.getNextId()+1);
        order.setUsername(username);
        order.setOrderDate(orderInfoVOBill.getTime());
        order.setBillAddress1(orderInfoVOBill.getBillAddress1());
        order.setBillAddress2(orderInfoVOBill.getBillAddress2());
        order.setBillCity(orderInfoVOBill.getBillCity());
        order.setBillZip(orderInfoVOBill.getBillZip());
        order.setBillState(orderInfoVOBill.getBillState());
        order.setBillCountry(orderInfoVOBill.getBillCountry());
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

        order.setBillToFirstName(orderInfoVOBill.getBillToFirstName());
        order.setBillToLastName(orderInfoVOBill.getBillToLastName());
        order.setCreditCard(orderInfoVOBill.getCreditCard());
        order.setExpiryDate(orderInfoVOBill.getExpiryDate());
        order.setCardType(orderInfoVOBill.getCardType());

        if(orderInfoVOShip!=null){
            order.setShipToFirstName(orderInfoVOShip.getShipToFirstName());
            order.setShipToLastName(orderInfoVOShip.getShipToLastName());
            order.setShipAddress1(orderInfoVOShip.getShipAddress1());
            order.setShipAddress2(orderInfoVOShip.getShipAddress2());
            order.setShipCity(orderInfoVOShip.getShipCity());
            order.setShipZip(orderInfoVOShip.getShipZip());
            order.setShipState(orderInfoVOShip.getShipState());
            order.setShipCountry(orderInfoVOShip.getShipCountry());
        }else{
            order.setShipToFirstName(orderInfoVOBill.getBillToFirstName());
            order.setShipToLastName(orderInfoVOBill.getBillToLastName());
            order.setShipAddress1(orderInfoVOBill.getBillAddress1());
            order.setShipAddress2(orderInfoVOBill.getBillAddress2());
            order.setShipCity(orderInfoVOBill.getBillCity());
            order.setShipZip(orderInfoVOBill.getBillZip());
            order.setShipState(orderInfoVOBill.getBillState());
            order.setShipCountry(orderInfoVOBill.getBillCountry());
        }


        order.setLocale("CA");
        orderMapper.insert(order);

        OrderStatus orderStatus=new OrderStatus();
        orderStatus.setOrderId(sequence.getNextId()+1);
        orderStatus.setLineNum(sequence.getNextId()+1);
        orderStatus.setTimestamp(orderInfoVOBill.getTime());
        orderStatus.setStatus("P");

        List<OrderVO> orderVOList=new ArrayList<>();
        List<LineItemVO> lineItemVOList=getLineItems(order.getOrderId()).getData();
        OrderVO orderVO=orderToOrderVO(order,orderStatus,lineItemVOList);
        orderVOList.add(orderVO);
        orderStatusMapper.insert(orderStatus);

        sequence.setNextId(sequence.getNextId()+1);
        sequenceMapper.update(sequence,queryWrapper1);

        cartItemMapper.delete(queryWrapper);

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
