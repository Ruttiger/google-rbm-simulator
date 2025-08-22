package com.messi.rbm.simulator.service;

import com.messi.rbm.simulator.model.Message;
import com.messi.rbm.simulator.model.MessageType;
import org.springframework.stereotype.Component;

@Component
public class MessageTypeDetector {

    public MessageType detect(Message message) {
        if (message.contentMessage() != null) {
            if (message.contentMessage().richCard() != null) {
                return MessageType.RICH_CARD;
            }
            if (message.contentMessage().uploadedRbmFile() != null
                    || message.contentMessage().contentInfo() != null) {
                return MessageType.MEDIA;
            }
        }
        return MessageType.TEXT;
    }
}
