package sn.awi.redis.catalog.exception;

public class CatalogLoaderException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public CatalogLoaderException() {
        super();
    }

    public CatalogLoaderException(String s) {
        super(s);
    }

    public CatalogLoaderException(String s, Throwable cause) {
        super(s, cause);
    }

}
