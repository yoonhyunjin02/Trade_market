package com.owl.trade_market.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chatroom")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "buyer_id", nullable = false)
    private User buyer;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats = new ArrayList<>();

    // 기본 생성자
    public ChatRoom() {
    }

    // 채팅방 식별을 위한 entity를 받는 생성자
    public ChatRoom(Product product, User buyer) {
        this.product = product;
        this.buyer = buyer;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    // 연관관계 편의 메서드
    public void addChat(Chat chat) {
        getChats().add(chat);
        chat.setChatRoom(this);
    }

    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", product=" + product +
                ", buyer=" + buyer +
                '}';
    }
}
