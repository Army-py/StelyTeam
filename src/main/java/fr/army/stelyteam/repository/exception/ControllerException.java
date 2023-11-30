package fr.army.stelyteam.repository.exception;

public abstract class ControllerException extends Exception {

    private final String detailedMessage;

    protected ControllerException(String message, String detailedMessage) {
        super(message);
        this.detailedMessage = detailedMessage;
    }

    private String getDetailedMessage() {
        return detailedMessage;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " : " + getDetailedMessage();
    }
}
