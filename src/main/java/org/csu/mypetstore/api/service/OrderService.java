package org.csu.mypetstore.api.service;



import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.Order;
import org.csu.mypetstore.api.vo.CartVO;
import org.csu.mypetstore.api.vo.LineItemVO;
import org.csu.mypetstore.api.vo.OrderInfoVO;
import org.csu.mypetstore.api.vo.OrderVO;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface OrderService {
    CommonResponse<List<OrderVO>> getOrdersByUserName(String username);

    CommonResponse<List<LineItemVO>> getLineItems(int orderId);

    CommonResponse<List<OrderVO>> getOrdersByOrderId(int orderId);

    CommonResponse<List<OrderVO>> createOrder(String username,OrderInfoVO orderInfoVOBill,OrderInfoVO OrderInfoVOShip);

    CommonResponse setOrderInfo(OrderInfoVO orderInfoVO);


}

