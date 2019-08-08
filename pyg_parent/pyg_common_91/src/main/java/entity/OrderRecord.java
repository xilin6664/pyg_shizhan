package entity;

import java.io.Serializable;

/**
 * 订单记录对象，保存秒杀商品id和用户的id
 */
public class OrderRecord implements Serializable{

    private Long goodId;

    private String username;

    public OrderRecord() {
    }

    public OrderRecord(Long goodId, String username) {
        this.goodId = goodId;
        this.username = username;
    }

    public Long getGoodId() {
        return goodId;
    }

    public void setGoodId(Long goodId) {
        this.goodId = goodId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
