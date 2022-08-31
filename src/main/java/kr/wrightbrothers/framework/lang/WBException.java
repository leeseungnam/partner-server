package kr.wrightbrothers.framework.lang;

public class WBException extends RuntimeException {

	private static final long serialVersionUID = -4801663608562746803L;

	public WBException() {
        super();
    }

    public WBException(String message) {
        super(message);
    }

    public WBException(String message, Throwable cause) {
        super(message, cause);
    }

    public WBException(Throwable cause) {
        super(cause);
    }

    protected WBException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
