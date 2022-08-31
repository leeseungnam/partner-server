package kr.wrightbrothers.framework.support.logging.dialect;

public class SQLiteDialect implements SQLDialect {
    public static final String SELECT_CURRVAL = "SELECT last_insert_rowid();";

    public String getSelectInsertId() {
        return SELECT_CURRVAL;
    }
}
