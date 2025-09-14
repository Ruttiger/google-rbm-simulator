package com.messi.rbm.simulator.service;

import com.messi.rbm.simulator.model.messaging.Message;
import com.messi.rbm.simulator.model.messaging.MessageType;
import org.springframework.stereotype.Component;

/**
 * Utility component that determines the message type based on its content.
 */
@Component
public class MessageTypeDetector {

    public MessageType detect(final Message message) {
        if (message == null || message.contentMessage() == null) {
            return MessageType.TEXT;
        }
        if (message.contentMessage().richCard() != null) {
            return MessageType.RICH_CARD;
        }
        if (message.contentMessage().uploadedRbmFile() != null
                || message.contentMessage().contentInfo() != null) {
            return MessageType.MEDIA;
        }
        return MessageType.TEXT;
    }
}
