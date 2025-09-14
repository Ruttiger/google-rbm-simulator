package win.agus4the.rbm.simulator.service;

import win.agus4the.rbm.simulator.model.messaging.Message;
import win.agus4the.rbm.simulator.model.messaging.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utility component that determines the message type based on its content.
 */
@Component
public class MessageTypeDetector {

    private static final Logger log = LoggerFactory.getLogger(MessageTypeDetector.class);

    public MessageType detect(final Message message) {
        MessageType type;
        if (message == null || message.contentMessage() == null) {
            type = MessageType.TEXT;
        } else if (message.contentMessage().richCard() != null) {
            type = MessageType.RICH_CARD;
        } else if (message.contentMessage().uploadedRbmFile() != null
                || message.contentMessage().contentInfo() != null) {
            type = MessageType.MEDIA;
        } else {
            type = MessageType.TEXT;
        }
        log.debug("Detected message type={} for message={}", type, message);
        return type;
    }
}
