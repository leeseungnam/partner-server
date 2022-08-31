package kr.wrightbrothers.framework.lang;

import java.sql.SQLException;

public class WBRollBackException extends SQLException {

	private static final long serialVersionUID = 5702989552133619465L;

	public WBRollBackException() {
        super();
    }

    public WBRollBackException(String message) {
        super(message);
    }

    public WBRollBackException(String message, Throwable cause) {
        super(message, cause);
    }

    public WBRollBackException(Throwable cause) {
        super(cause);
    }
}
