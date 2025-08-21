package com.messi.rbm.authsim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Minimal representation of an RBM message supporting several content types.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Message(
        String name,
        String messageId,
        String text,
        UploadedRbmFile uploadedRbmFile,
        RichCard richCard,
        ContentInfo contentInfo,
        Representative representative,
        String sendTime
) {
    public record Representative(String representativeType) {}

    public record UploadedRbmFile(String fileName, String thumbnailName) {}

    public record ContentInfo(String fileUrl, String thumbnailUrl, Boolean forceRefresh) {}

    public record RichCard(StandaloneCard standaloneCard) {
        public record StandaloneCard(CardContent cardContent) {
            public record CardContent(String title, String description, Media media) {
                public record Media(String height, ContentInfo contentInfo) {}
            }
        }
    }
}
