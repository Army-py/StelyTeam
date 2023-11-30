package fr.army.stelyteam.repository.exception;

public abstract class RepositoryException extends Exception {

    private final String detailedMessage;

    protected RepositoryException(String message, String detailedMessage) {
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
