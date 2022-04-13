package org.csu.mypetstore.api.service;

import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.CartItem;
import org.csu.mypetstore.api.vo.CartItemVO;
import org.csu.mypetstore.api.vo.CartVO;

import java.util.List;

public interface CartService {
    CommonResponse<List<CartItem>> getCartByUsername(String username);

//    CommonResponse<CartItem> insertCart(CartItem cart);

    CommonResponse insertCartItem(CartVO cartVO, String itemId);

    CommonResponse<CartItem> deleteOneCart(String username, String itemId);

    CommonResponse<List<CartItem>> deleteAllCart(String username);

    CommonResponse updateCartItemQty(CartVO cart, int quantity, String itemId);

//    CartVO mergeCarts(CartVO tempCart, CartVO persisCart);

    CartVO getCart(String username);

    List<CartItem> getCartItemsByUsername(String username);

}
