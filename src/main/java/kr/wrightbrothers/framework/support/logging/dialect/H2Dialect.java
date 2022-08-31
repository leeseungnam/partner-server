package kr.wrightbrothers.framework.support.logging.dialect;

public class H2Dialect implements SQLDialect {
    public static final String SELECT_CURRVAL = "CALL IDENTITY()";

    public String getSelectInsertId() {
        return SELECT_CURRVAL;
    }

}
