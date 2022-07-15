package org.petrowich.gallerychecker.scheduler.external.exceptions;

import org.petrowich.gallerychecker.models.master.tubes.Tube;

public class FetchJobException extends Exception {
    private final Tube tube;

    public FetchJobException(String message, Tube tube) {
        super(message);
        this.tube = tube;
    }

    public FetchJobException(String message, Tube tube, Exception exception) {
        super(message, exception);
        this.tube = tube;
    }

    @Override
    public String getMessage() {
        return String.format("Fetch from %s error: %s", tube, super.getMessage());
    }
}
