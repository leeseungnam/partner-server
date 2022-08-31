package kr.wrightbrothers.framework.support.logging;

import kr.wrightbrothers.framework.support.dto.TransactionLogDTO;
import lombok.Data;
import org.apache.ibatis.session.SqlSession;

@Data
public class WBTransactionLog {

	private SqlSession defaultSqlSession;
	
	public WBTransactionLog(SqlSession defaultSqlSession) {
		this.defaultSqlSession = defaultSqlSession;
	}
	
	public void addTransactionalData(String sqlId, TransactionLogDTO paramData) {
		defaultSqlSession.insert(sqlId, paramData);
	}
}
