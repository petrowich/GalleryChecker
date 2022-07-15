package org.petrowich.gallerychecker.processig.stored.upload.exceptions;

public class GalleryUploadException extends RuntimeException {
    public GalleryUploadException(String message) {
        super(message);
    }

    public GalleryUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
