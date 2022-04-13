package org.csu.mypetstore.api.vo;


import lombok.Data;
import org.csu.mypetstore.api.entity.CartItem;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
public class CartVO {
    // String：ItemId作为Key使用
    private final Map<String, CartItem> itemMap = Collections.synchronizedMap(new HashMap<String, CartItem>());
    // 是否已经合并
    private boolean merged = false;
    // 每个CartItem有一个userid，这里的id是当购物车内没有CartItem时发挥作用
    private String username = null;

    private BigDecimal totalprice;

    public void initMap(Map<String, CartItem> itemMap){
        this.itemMap.putAll(itemMap);
    }

    public boolean containsItemId(String itemId) {
        return itemMap.containsKey(itemId);
    }

    public CartItem removeItemById(String itemId) {
        // 删除失败返回null，否则返回被删除的CartItemVO
        return itemMap.remove(itemId);
    }

    public void addToItemMap(String itemId,CartItem cartItem){
        itemMap.put(itemId,cartItem);
    }

//    public CartItem incrementQuantityByItemId(String itemId) {
//        CartItemVO cartItem = itemMap.get(itemId);
//        cartItem.incrementQuantity();
//        return cartItem;
//    }

    public CartItem setQuantityByItemId(String itemId, int quantity) {
        CartItem cartItem = itemMap.get(itemId);
        cartItem.setQuantity(quantity);
        return cartItem;
    }

    public BigDecimal getSubTotal() {
        BigDecimal subTotal = new BigDecimal("0");
        for (CartItem cartItem : itemMap.values()) {
            BigDecimal qty = new BigDecimal(cartItem.getQuantity());
//            subTotal = subTotal.add(cartItem.multiply(qty));
        }
        return subTotal;
    }

}
