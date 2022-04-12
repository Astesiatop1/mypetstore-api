package org.csu.mypetstore.api.service;

import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.Cart;

import java.util.List;

public interface CartService {
    CommonResponse<List<Cart>> getCartByUsername(String username);

    CommonResponse<Cart> insertCart(Cart cart);

    CommonResponse<Cart> insertCart(String username,String itemId);

    CommonResponse<Cart> deleteOneCart(String username,String itemId);

    CommonResponse<List<Cart>> deleteAllCart(String username);

    CommonResponse updateCartItemQty(Cart cart, int quantity, String itemId);
}
