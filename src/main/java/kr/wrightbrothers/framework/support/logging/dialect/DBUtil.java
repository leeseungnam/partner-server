package kr.wrightbrothers.framework.support.logging.dialect;

import ch.qos.logback.core.spi.ContextAwareBase;
import lombok.extern.slf4j.Slf4j;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Slf4j
public class DBUtil extends ContextAwareBase {
    private static final String MYSQL_PART = "mysql";
    private static final String H2_PART = "h2";

    public static SQLDialectCode discoverSQLDialect(DatabaseMetaData meta) {
        SQLDialectCode dialectCode = SQLDialectCode.UNKNOWN_DIALECT;
        try {
            String dbName = meta.getDatabaseProductName().toLowerCase();
            if (dbName.contains(MYSQL_PART)) {
                return SQLDialectCode.MYSQL_DIALECT;
            } else if (dbName.contains(H2_PART)) {
                return SQLDialectCode.H2_DIALECT;
            } else {
                return SQLDialectCode.UNKNOWN_DIALECT;
            }
        } catch (SQLException sqle) {
            log.error(sqle.getMessage());
        }

        return dialectCode;
    }

    public static SQLDialect getDialectFromCode(SQLDialectCode sqlDialectType) {
        SQLDialect sqlDialect = null;
        switch (sqlDialectType) {
	        case MYSQL_DIALECT:
	            sqlDialect = new MySQLDialect();
	            break;
	
	        case H2_DIALECT:
	            sqlDialect = new H2Dialect();
	            break;
	        default:
	        	break;
	
        }
        return sqlDialect;
    }

    public boolean supportsGetGeneratedKeys(DatabaseMetaData meta) {
        try {
            return (Boolean) DatabaseMetaData.class.getMethod("supportsGetGeneratedKeys", (Class[]) null).invoke(meta, (Object[]) null);
        } catch (Throwable e) {
            addInfo("Could not call supportsGetGeneratedKeys method. This may be recoverable");
            return false;
        }
    }

    public boolean supportsBatchUpdates(DatabaseMetaData meta) {
        try {
            return meta.supportsBatchUpdates();
        } catch (Throwable e) {
            addInfo("Missing DatabaseMetaData.supportsBatchUpdates method.");
            return false;
        }
    }
}
