package org.csu.mypetstore.api.controller;

import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.Cart;
import org.csu.mypetstore.api.service.CartService;
import org.csu.mypetstore.api.vo.AccountVO;
import org.csu.mypetstore.api.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/carts/")
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("{id}")//ok
    @ResponseBody
    public CommonResponse<List<Cart>> getCartByUsername(@PathVariable("id") String username) {
        return cartService.getCartByUsername(username);
    }

    @PostMapping()//ok
    @ResponseBody
    public CommonResponse<Cart> insertCart(Cart cart){
        return cartService.insertCart(cart);
    }

    @PostMapping("{id}/{item}")//OK?
    @ResponseBody
    public CommonResponse<Cart> insertCart(@PathVariable("id") String username,@PathVariable("item") String itemId){
        return cartService.insertCart(username,itemId);
    }

    @DeleteMapping("{id}")//OK
    @ResponseBody
    public CommonResponse<List<Cart>> deleteCart(@PathVariable("id") String username){
        return cartService.deleteAllCart(username);
    }

    @DeleteMapping("{id}/items")//ok
    @ResponseBody
    public CommonResponse<Cart> deleteOneCart(@PathVariable("id")String username,@RequestParam("ITEM_ID")String itemId){
        return cartService.deleteOneCart(username,itemId);
    }

    @PostMapping("/items/{item_id}/quantity/{qty}")
    @ResponseBody
    public CommonResponse updateItemQtyFromCart(HttpSession session,
                                                @PathVariable("item_id") String itemId,
                                                @PathVariable String qty){

        AccountVO accountVO = new AccountVO();
        accountVO = (AccountVO) session.getAttribute("login_Account");
        CartVO cartVO = (CartVO) session.getAttribute("cart");
        CartVO cart = cartService.mergeCarts(cartVO, null);

        int quantity;
        try {
            quantity = Integer.parseInt(qty);
        } catch (Exception e) {
            return CommonResponse.createForError("数量参数qty错误");
        }


        CommonResponse response = cartService.updateCartItemQty(cart, quantity, itemId);
        return response;

    }


    @PutMapping("{id}")
    @ResponseBody
    public CommonResponse<Cart> updateCart(@PathVariable("id") String username, Cart cart){
//        return cartService.updateCart(username,cart);
        return null;
    }


}
