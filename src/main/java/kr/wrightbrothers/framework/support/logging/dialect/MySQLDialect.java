package kr.wrightbrothers.framework.support.logging.dialect;

public class MySQLDialect implements SQLDialect {
    public static final String SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";

    public String getSelectInsertId() {
        return SELECT_LAST_INSERT_ID;
    }
}
