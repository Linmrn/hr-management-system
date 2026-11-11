package net.xju.common.framework.simplemvc;

public class SimpleMVCException extends RuntimeException {
    public SimpleMVCException() {
    }

    public SimpleMVCException(String message) {
        super(message);
    }

    public SimpleMVCException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleMVCException(Throwable cause) {
        super(cause);
    }

    public SimpleMVCException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
