package kr.wrightbrothers.framework.support.logging;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import kr.wrightbrothers.framework.support.logging.dialect.DBUtil;
import kr.wrightbrothers.framework.support.logging.dialect.SQLDialect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;

public abstract class DBAppenderBase<E> extends UnsynchronizedAppenderBase<E> {

    protected ConnectionSource connectionSource;
    protected boolean cnxSupportsGetGeneratedKeys = false;
    protected boolean cnxSupportsBatchUpdates = false;
    protected SQLDialect sqlDialect;

    protected abstract Method getGeneratedKeysMethod();

    protected abstract String getInsertSQL();

    @Override
    public void start() {

        if (connectionSource == null) {
            throw new IllegalStateException("DBAppender cannot function without a connection source");
        }

        sqlDialect = DBUtil.getDialectFromCode(connectionSource.getSQLDialectCode());
        if (getGeneratedKeysMethod() != null) {
            cnxSupportsGetGeneratedKeys = connectionSource.supportsGetGeneratedKeys();
        } else {
            cnxSupportsGetGeneratedKeys = false;
        }
        cnxSupportsBatchUpdates = connectionSource.supportsBatchUpdates();
        if (!cnxSupportsGetGeneratedKeys && (sqlDialect == null)) {
            throw new IllegalStateException(
                            "DBAppender cannot function if the JDBC driver does not support getGeneratedKeys method *and* without a specific SQL dialect");
        }

        super.start();
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    public void setConnectionSource(ConnectionSource connectionSource) {
        this.connectionSource = connectionSource;
    }

    @Override
    public void append(E eventObject) {
        Connection connection = null;
        PreparedStatement insertStatement = null;
        try {
            connection = connectionSource.getConnection();
            connection.setAutoCommit(false);

            if (cnxSupportsGetGeneratedKeys) {
                String EVENT_ID_COL_NAME = "EVENT_ID";
                insertStatement = connection.prepareStatement(getInsertSQL(), new String[] { EVENT_ID_COL_NAME });
            } else {
                insertStatement = connection.prepareStatement(getInsertSQL());
            }

            long eventId;
            // inserting an event and getting the result must be exclusive
            synchronized (this) {
                subAppend(eventObject, connection, insertStatement);
                eventId = selectEventId(insertStatement, connection);
            }
            secondarySubAppend(eventObject, connection, eventId);

            connection.commit();
        } catch (Throwable sqle) {
            addError("problem appending event", sqle);
        } finally {
            DBHelper.closeStatement(insertStatement);
            DBHelper.closeConnection(connection);
        }
    }

    protected abstract void subAppend(E eventObject, Connection connection, PreparedStatement statement) throws Throwable;

    protected abstract void secondarySubAppend(E eventObject, Connection connection, long eventId) throws Throwable;

    protected long selectEventId(PreparedStatement insertStatement, Connection connection) throws SQLException, InvocationTargetException {
        ResultSet rs = null;
        Statement idStatement = null;
        try {
            boolean gotGeneratedKeys = false;
            if (cnxSupportsGetGeneratedKeys) {
                try {
                    rs = (ResultSet) getGeneratedKeysMethod().invoke(insertStatement, (Object[]) null);
                    gotGeneratedKeys = true;
                } catch (InvocationTargetException ex) {
                    Throwable target = ex.getTargetException();
                    if (target instanceof SQLException) {
                        throw (SQLException) target;
                    }
                    throw ex;
                } catch (IllegalAccessException ex) {
                    addWarn("IllegalAccessException invoking PreparedStatement.getGeneratedKeys", ex);
                }
            }

            if (!gotGeneratedKeys) {
                idStatement = connection.createStatement();
                idStatement.setMaxRows(1);
                String selectInsertIdStr = sqlDialect.getSelectInsertId();
                rs = idStatement.executeQuery(selectInsertIdStr);
            }
            rs.next();
            return rs.getLong(1);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignored) {
                }
            }
            DBHelper.closeStatement(idStatement);
        }
    }

    @Override
    public void stop() {
        super.stop();
    }
}
