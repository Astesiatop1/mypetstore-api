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
        session.setAttribute("cart", cart);
        return CommonResponse.createForSuccess(cart);
    }


    @GetMapping("{id}")//ok
    @ResponseBody
    public CommonResponse<List<CartItem>> getCartByUsername(@PathVariable("id") String username) {
        return cartService.getCartByUsername(username);
    }

    @GetMapping("viewCart")
    @ResponseBody
    public CommonResponse<CartVO> viewCart(HttpSession session) {
        CartVO cart = (CartVO) session.getAttribute("cart");
        cart.setTotalprice(cartService.getTotalPrice(cart));
        return CommonResponse.createForSuccess(cart);
    }

    @PostMapping("/items/{item_id}")//ok
    @ResponseBody
    public CommonResponse<CartVO> insertCartItem(HttpSession session, @PathVariable("item_id") String itemId){
        CartVO cartVO = (CartVO)session.getAttribute("cart");
        AccountVO accountVO = (AccountVO) session.getAttribute("login_account");
        if(accountVO == null) {
            return CommonResponse.createForError("用户未登录，请先登录");
        }

        if (cartVO == null){
            cartVO = new CartVO();
            cartVO.setUsername(accountVO.getUsername());
            cartVO.initMap(new HashMap<String, CartItem>());
        }

        CommonResponse response = cartService.insertCartItem(cartVO,itemId);
        session.setAttribute("cart",cartVO);
        return response;
    }

    @DeleteMapping("{id}")//OK
    @ResponseBody
    public CommonResponse<List<CartItem>> deleteCart(@PathVariable("id") String username){

        return cartService.deleteAllCart(username);
    }

    @DeleteMapping("items/{ITEM_ID}")//ok
    @ResponseBody
    public CommonResponse<CartItem> deleteOneCart(@PathVariable("ITEM_ID")String itemId,
                                                  HttpSession session){
        AccountVO account = (AccountVO) session.getAttribute("login_account");
        return cartService.deleteOneCart(account.getUsername(),itemId);
    }



    @PatchMapping("/items/{item_id}/quantity/{qty}")
    @ResponseBody
    public CommonResponse updateItemQtyFromCart(HttpSession session,
                                                @PathVariable("item_id") String itemId,
                                                @PathVariable String qty){

        AccountVO accountVO = new AccountVO();
        accountVO = (AccountVO) session.getAttribute("login_Account");
        CartVO cartVO = cartService.getCart(accountVO.getUsername());
        int quantity;
        try {
            quantity = Integer.parseInt(qty);
        } catch (Exception e) {
            return CommonResponse.createForError("数量参数qty错误");
        }
        CommonResponse response = cartService.updateCartItemQty(cartVO, quantity, itemId);
        session.setAttribute("cartVO",cartVO);
        return response;
    }


    @PutMapping("{id}")
    @ResponseBody
    public CommonResponse<CartItem> updateCart(@PathVariable("id") String username, CartItem cart){
//        return cartService.updateCart(username,cart);
        return null;
    }


}
