package org.csu.mypetstore.api.service;



import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.Order;
import org.csu.mypetstore.api.vo.OrderVO;

import java.util.List;

public interface OrderService {
    CommonResponse<OrderVO> insertOrder(Order order);

    CommonResponse<OrderVO> getOrder(int orderId);

    CommonResponse<List<Order>> getOrdersByUsername(String username);

}

