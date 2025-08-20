package com.example.rbm.simulator.dto;

public class DeliveryState {
    private MessageState messageState;

    public DeliveryState() {
    }

    public DeliveryState(MessageState messageState) {
        this.messageState = messageState;
    }

    public MessageState getMessageState() {
        return messageState;
    }

    public void setMessageState(MessageState messageState) {
        this.messageState = messageState;
    }
}
