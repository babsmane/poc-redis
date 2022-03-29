package sn.awi.redis.publication.exception;

public class PublicationException extends RuntimeException {

    public PublicationException() {
    }

    public PublicationException(String message) {
        super(message);
    }

    public PublicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PublicationException(Throwable cause) {
        super(cause);
    }

}
