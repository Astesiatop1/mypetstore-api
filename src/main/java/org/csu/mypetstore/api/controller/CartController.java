package org.csu.mypetstore.api.controller;

import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.CartItem;
import org.csu.mypetstore.api.service.CartService;
import org.csu.mypetstore.api.vo.AccountVO;
import org.csu.mypetstore.api.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/carts/")
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping("")
    @ResponseBody
    public CommonResponse<CartVO> registerCart(HttpSession session) {
        AccountVO account = (AccountVO) session.getAttribute("login_account");
        CartVO cart = account == null ? cartService.getCart(null) : cartService.getCart(account.getUsername());
        return CommonResponse.createForSuccess(cart);
    }


    @GetMapping("{id}")//ok
    @ResponseBody
    public CommonResponse<List<CartItem>> getCartByUsername(@PathVariable("id") String username) {
        return cartService.getCartByUsername(username);
    }

    @GetMapping("{username}/viewCart")
    @ResponseBody
    public CommonResponse<CartVO> viewCart(@PathVariable("username") String username) {
        CartVO cartVO = cartService.getCart(username);
        cartVO.setTotalprice(cartService.getTotalPrice(cartVO));
        return CommonResponse.createForSuccess(cartVO);
    }

    @PostMapping("{username}/items/{item_id}")//ok
    @ResponseBody
    public CommonResponse<CartVO> insertCartItem(@PathVariable("username") String username, @PathVariable("item_id") String itemId){
        if(username==null){
            return CommonResponse.createForError("用户未登录，请先登录");
        }

        CartVO cartVO = cartService.getCart(username);
        if (cartVO == null){
            cartVO = new CartVO();
            cartVO.initMap(new HashMap<String, CartItem>());
        }
        cartVO.setUsername(username);

        CommonResponse response = cartService.insertCartItem(cartVO,itemId);
        return response;
    }

    @DeleteMapping("{id}")//OK
    @ResponseBody
    public CommonResponse<List<CartItem>> deleteCart(@PathVariable("id") String username){

        return cartService.deleteAllCart(username);
    }

    @DeleteMapping("{username}/items/{ITEM_ID}")//ok
    @ResponseBody
    public CommonResponse<CartItem> deleteOneCart(
            @PathVariable("username")String username,
            @PathVariable("ITEM_ID")String itemId
                                                   ){

        return cartService.deleteOneCart(username,itemId);
    }



    @PatchMapping("{username}/items/{item_id}/quantity/{qty}")
    @ResponseBody
    public CommonResponse updateItemQtyFromCart(
                                                @PathVariable("username") String username,
                                                @PathVariable("item_id") String itemId,
                                                @PathVariable String qty){

        CartVO cartVO = cartService.getCart(username);
        int quantity;
        try {
            quantity = Integer.parseInt(qty);
        } catch (Exception e) {
            return CommonResponse.createForError("数量参数qty错误");
        }
        CommonResponse response = cartService.updateCartItemQty(cartVO, quantity, itemId);
        return response;
    }


    @PutMapping("{id}")
    @ResponseBody
    public CommonResponse<CartItem> updateCart(@PathVariable("id") String username, CartItem cart){
//        return cartService.updateCart(username,cart);
        return null;
    }


}
