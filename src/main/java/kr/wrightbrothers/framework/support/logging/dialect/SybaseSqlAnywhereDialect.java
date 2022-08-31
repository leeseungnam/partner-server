package kr.wrightbrothers.framework.support.logging.dialect;

public class SybaseSqlAnywhereDialect implements SQLDialect {

    public static final String SELECT_CURRVAL = "SELECT @@identity id";

    public String getSelectInsertId() {
        return SELECT_CURRVAL;
    }

}
