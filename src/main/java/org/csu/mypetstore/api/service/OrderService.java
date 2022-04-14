package org.csu.mypetstore.api.service;



import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.OrderInfo;
import org.csu.mypetstore.api.vo.LineItemVO;
import org.csu.mypetstore.api.vo.OrderInfoVO;
import org.csu.mypetstore.api.vo.OrderVO;

import java.util.List;

public interface OrderService {
    CommonResponse<List<OrderVO>> getOrdersByUserName(String username);

    CommonResponse<List<LineItemVO>> getLineItems(int orderId);

    CommonResponse<List<OrderVO>> getOrdersByOrderId(int orderId);

    CommonResponse<List<OrderVO>> createOrder(String username);

    CommonResponse setOrderInfo(OrderInfo orderInfo);

//    CommonResponse viewOrderInfo(OrderInfoVO orderInfoVOBill,OrderInfoVO orderInfoVOShip);

}

