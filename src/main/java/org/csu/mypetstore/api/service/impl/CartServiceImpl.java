package org.csu.mypetstore.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.Cart;
import org.csu.mypetstore.api.entity.Item;
import org.csu.mypetstore.api.persistence.CartMapper;
import org.csu.mypetstore.api.persistence.ItemMapper;
import org.csu.mypetstore.api.service.CartService;
import org.csu.mypetstore.api.vo.CartItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public CommonResponse<List<Cart>> getCartByUsername(String username) {
        QueryWrapper<Cart> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("USERNAME",username);
        List<Cart> cartList = cartMapper.selectList(queryWrapper);
        if (cartList.size() == 0) {
            return CommonResponse.createForError("该用户购物车为空");
        }
        return CommonResponse.createForSuccess(cartList);

    }

    @Override
    public CommonResponse<Cart> insertCart(Cart cart) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USERNAME", cart.getUserid()).eq("ITEM_ID", cart.getItemid());
        Cart cart1 = cartMapper.selectOne(queryWrapper);
        //插入时已经有该商品信息 需要更新
        if (cart1 != null) {
            cartMapper.update(cart, queryWrapper);
        } else {
            cartMapper.insert(cart);
        }
        return CommonResponse.createForSuccess(cart);

    }

    @Override
    public CommonResponse<Cart> insertCart(String username, String itemId) {
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            return CommonResponse.createForSuccessMessage("加入购物车的物品id不正确");
        }
        Cart cart = new Cart();
        cart.setUserid(username);
        cart.setItemid(itemId);
        cart.setProductid(item.getProductId());
        cart.setDescription(item.getAttribute1());
        cart.setInstock("true");
        cart.setQuantity(1);
        cart.setListprice(item.getListPrice());
        cart.setTotalcost(item.getListPrice());

        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USERNAME", cart.getUserid()).eq("ITEM_ID", cart.getItemid());
        Cart cart1 = cartMapper.selectOne(queryWrapper);
        //插入时已经有该商品信息 需要更新
        if (cart1 != null) {
            cart.setQuantity(cart1.getQuantity()+1);
            cart.setTotalcost(cart.getListprice().add(cart1.getTotalcost()));
            cartMapper.update(cart, queryWrapper);
        } else {
            cartMapper.insert(cart);
        }
        return CommonResponse.createForSuccess(cart);

    }

    @Override
    public CommonResponse<Cart> deleteOneCart(String username, String itemId) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USERNAME", username).eq("ITEM_ID", itemId);
        cartMapper.delete(queryWrapper);
        return CommonResponse.createForSuccessMessage("购物车（单种商品）删除成功");
    }

    @Override
    public CommonResponse<List<Cart>> deleteAllCart(String username) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USERNAME", username);
        cartMapper.delete(queryWrapper);
        return CommonResponse.createForSuccessMessage("订单提交后删除购物车中所有的信息");

    }

//    @Override
//    public CommonResponse<Cart> updateCart(String username, Cart cart) {
//        //只需使用itemid和quantity属性
//        Item item = itemMapper.selectById(cart.getItemid());
//        if (item == null) {
//            return CommonResponse.createForError("没有该Item");
//        }
//
//        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("USERNAME", username).eq("ITEM_ID", cart.getItemid());
//
//        cart.setUserid(username);
//        cart.setProductid(item.getProductId());
//        cart.setDescription(item.getAttribute1());
//        cart.setInstock("true");
//        cart.setListprice(item.getListPrice());
//        BigDecimal numbers = new BigDecimal(cart.getQuantity().toString());
//        BigDecimal totalCost = item.getListPrice().multiply(numbers);
//        cart.setTotalcost(totalCost);
//        cartMapper.update(cart, queryWrapper);
//        return CommonResponse.createForSuccess(cart);
//
//    }


    @Override
    public CommonResponse updateCartItemQty(Cart cart, int quantity, String itemId) {
        Map<String, CartItemVO> itemMap = cart.getItemMap();

        return null;
    }
}
