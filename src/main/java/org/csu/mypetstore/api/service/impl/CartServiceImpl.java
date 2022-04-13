package org.csu.mypetstore.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.csu.mypetstore.api.common.CommonResponse;
import org.csu.mypetstore.api.entity.CartItem;
import org.csu.mypetstore.api.entity.Item;
import org.csu.mypetstore.api.persistence.CartItemMapper;
import org.csu.mypetstore.api.persistence.ItemMapper;
import org.csu.mypetstore.api.service.CartService;
import org.csu.mypetstore.api.vo.CartItemVO;
import org.csu.mypetstore.api.vo.CartVO;
import org.csu.mypetstore.api.vo.ItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("cartService")
public class CartServiceImpl implements CartService {


    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private ItemMapper itemMapper;

    @Override
    public CommonResponse updateCartItemQty(CartVO cart, int quantity, String itemId) {
        Map<String, CartItem> itemMap = cart.getItemMap();
        if (!cart.containsItemId(itemId)) {
            // 临时购物车中没有，说明数据库中肯定也没有
            return CommonResponse.createForError("购物车中没有这个item商品");
        }
        try {
            CartItem cartItem = itemMap.get(itemId);

            // 已登录，已合并，从临时购物车中更新的同时需要更新数据库
            // 持久化：构造更新器更新数据库quantity字段
//            UpdateWrapper<CartItem> updateWrapper = new UpdateWrapper<>();
//            updateWrapper.eq("ID", cartItem.getId()).set("quantity", quantity);
//            cartItemMapper.update(null, updateWrapper);
            cartItem.setQuantity(quantity);
            cartItem.setTotalcost(BigDecimal.valueOf(cartItem.getListprice().doubleValue()*quantity));
            QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("USERNAME", cartItem.getUserid()).eq("ITEM_ID", cartItem.getItemid());
            cartItemMapper.update(cartItem, queryWrapper);
//                CartItem persisItem = VOToCartItem(cartItem);
//                persisItem.setQuantity(quantity);
//                cartItemMapper.updateById(persisItem);


            // 未登录，未合并，只需从临时购物车中更新
            cartItem.setQuantity(quantity);
            return CommonResponse.createForSuccessMessage("商品"+itemId+"数量更新成功");
        } catch (Exception e) {
            return CommonResponse.createForError("数量更新过程出错");
        }
    }

    @Override
    public CartVO getCart(String username) {
        System.out.println("getCart");
        CartVO cartVO = new CartVO();
        if (username != null) {
            System.out.println(username);
            List<CartItem> cartItems = this.getCartItemsByUsername(username);
            System.out.println(cartItems);
            for (CartItem cartItem : cartItems) {
                cartVO.getItemMap().put(cartItem.getItemid(), cartItem);
            }
            cartVO.setUsername(username);
        }
        // 传入username为null则默认新建一个新的临时购物车
        return cartVO;
    }

    @Override
    public List<CartItem> getCartItemsByUsername(String username) {
        System.out.println("getCartItemsByUsername");
        QueryWrapper<CartItem> cartItemQW = new QueryWrapper<>();
        cartItemQW.eq("USERNAME", username);
        List<CartItem> cartItems = cartItemMapper.selectList(cartItemQW);
        List<CartItem> res = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            res.add(cartItem);
        }
        return res;
    }



    private void plusQty(CartVO cart, String itemId, int plusQty, boolean isPersis){
        Map<String, CartItem> itemMap = cart.getItemMap();
        CartItem cartItem = itemMap.get(itemId);
        int afterPlusQty = cartItem.getQuantity() + plusQty;
        if (isPersis) {
            // 需要持久化，从临时购物车中更新之前需要更新数据库
//            CartItem persisItem = VOToCartItem(cartItem);
//            persisItem.setQuantity(afterPlusQty);
//            // 持久化：更新数据库记录
//            cartItemMapper.updateById(persisItem);
        }
        // 不需要持久化，只需从临时购物车中更新
        cartItem.setQuantity(afterPlusQty);
    }

    @Override
    public BigDecimal getTotalPrice(CartVO cartVO) {
        QueryWrapper<CartItem> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("USERNAME",cartVO.getUsername());
        List<CartItem> cartList = cartItemMapper.selectList(queryWrapper);

        BigDecimal totalPrice= new BigDecimal(0);
        for(CartItem cartItem : cartList){
            totalPrice=totalPrice.add(cartItem.getTotalcost());
        }
        return totalPrice;
    }

    private CartItem VOToCartItem(CartItemVO cartItemVO) {
        CartItem cartItem = new CartItem();
//        cartItem.setCartItemId(cartItemVO.getCartItemId());
        cartItem.setUserid(cartItemVO.getUsername());
        cartItem.setItemid(cartItemVO.getItemId());
        cartItem.setQuantity(cartItemVO.getQuantity());



        return cartItem;
    }

    private CartItemVO itemToCartItemVO(ItemVO item, String userid) {
        CartItemVO cartItemVO = new CartItemVO();
        cartItemVO.setUsername(userid);
        cartItemVO.setItemId(item.getItemId());
        cartItemVO.setQuantity(1);
        cartItemVO.setUnitPrice(item.getListPrice());
        cartItemVO.setInStock(item.getQuantity() > 0);
        cartItemVO.setAttribute1(item.getAttribute1());
        cartItemVO.setAttribute2(item.getAttribute2());
        cartItemVO.setInventQty(item.getQuantity());
        cartItemVO.calculateTotal();

        return cartItemVO;
    }


    @Override
    public CommonResponse<List<CartItem>> getCartByUsername(String username) {
        QueryWrapper<CartItem> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("USERNAME",username);
        List<CartItem> cartList = cartItemMapper.selectList(queryWrapper);
        if (cartList.size() == 0) {
            return CommonResponse.createForError("该用户购物车为空");
        }
        return CommonResponse.createForSuccess(cartList);
    }


    @Override
    public CommonResponse insertCartItem(CartVO cartVO, String itemId) {
        Item item = itemMapper.selectById(itemId);
        System.out.println(itemId);
        if (item == null) {
            return CommonResponse.createForSuccessMessage("加入购物车的物品id不正确");
        }
        if (cartVO.getItemMap().isEmpty()) {
            CartItem cartItem = new CartItem();
            cartItem.setUserid(cartVO.getUsername());
            cartItem.setItemid(itemId);
            cartItem.setProductid(item.getProductId());
            cartItem.setDescription(item.getAttribute1());
            cartItem.setInstock("true");
            cartItem.setQuantity(1);
            cartItem.setListprice(item.getListPrice());
            cartItem.setTotalcost(item.getListPrice());

            cartItemMapper.insert(cartItem);

            cartVO.addToItemMap(itemId,cartItem);
        } else {
            Map<String, CartItem> itemMap = cartVO.getItemMap();
            CartItem cartItem = itemMap.get(itemId);
            System.out.println(itemMap);
            System.out.println(cartItem);

            if (cartItem == null) {
                System.out.println("添加");
                cartItem = new CartItem();
                cartItem.setUserid(cartVO.getUsername());
                cartItem.setItemid(itemId);
                cartItem.setProductid(item.getProductId());
                cartItem.setDescription(item.getAttribute1());
                cartItem.setInstock("true");
                cartItem.setQuantity(1);
                cartItem.setListprice(item.getListPrice());
                cartItem.setTotalcost(item.getListPrice());

                cartItemMapper.insert(cartItem);

                itemMap.put(item.getItemId(), cartItem);

            } else {
                System.out.println("更新");
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartItem.setTotalcost(cartItem.getTotalcost().add(cartItem.getListprice()));
                QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("USERNAME", cartItem.getUserid()).eq("ITEM_ID", cartItem.getItemid());
                cartItemMapper.update(cartItem, queryWrapper);

            }
        }
        return CommonResponse.createForSuccess("商品" + itemId + "添加成功", cartVO);
    }

    @Override
    public CommonResponse<CartItem> deleteOneCart(String username, String itemId) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USERNAME", username).eq("ITEM_ID", itemId);
        cartItemMapper.delete(queryWrapper);
        return CommonResponse.createForSuccessMessage("购物车（单种商品）删除成功");
    }

    @Override
    public CommonResponse<List<CartItem>> deleteAllCart(String username) {
        QueryWrapper<CartItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USERNAME", username);
        cartItemMapper.delete(queryWrapper);
        return CommonResponse.createForSuccessMessage("订单提交后删除购物车中所有的信息");

    }
}
