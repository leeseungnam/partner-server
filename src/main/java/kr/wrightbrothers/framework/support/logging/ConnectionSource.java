package kr.wrightbrothers.framework.support.logging;

import ch.qos.logback.core.spi.LifeCycle;
import kr.wrightbrothers.framework.support.logging.dialect.SQLDialectCode;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSource extends LifeCycle {

    Connection getConnection() throws SQLException;

    SQLDialectCode getSQLDialectCode();

    boolean supportsGetGeneratedKeys();

    boolean supportsBatchUpdates();
}
