package com.example.rbm.simulator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentMessageResponse {
    private String name;
    private OffsetDateTime sendTime;
    private DeliveryState deliveryState;
    private Representative representative;
    private String text;
    private RichCard richCard;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(OffsetDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public DeliveryState getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(DeliveryState deliveryState) {
        this.deliveryState = deliveryState;
    }

    public Representative getRepresentative() {
        return representative;
    }

    public void setRepresentative(Representative representative) {
        this.representative = representative;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public RichCard getRichCard() {
        return richCard;
    }

    public void setRichCard(RichCard richCard) {
        this.richCard = richCard;
    }
}
