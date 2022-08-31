package kr.wrightbrothers.framework.support.dao;

import kr.wrightbrothers.framework.support.WBKey.WBDataBase;
import lombok.Data;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Map;

@Data
public class WBCommonDao implements ICommonDao{

	private Map<String, SqlSession> sqlSessionMap;
	
	public WBCommonDao(Map<String, SqlSession> sqlSessionMap) {
		this.sqlSessionMap = sqlSessionMap;
	}
	
	public <T> T selectOne(String statement) {
		return selectOne(statement, WBDataBase.Alias.Default);
	}

	@Override
	public <T> T selectOne(String statement, Object parameter) {
		return selectOne(statement, parameter, WBDataBase.Alias.Default);
	}
	
	@Override
	public <T> T selectOne(String statement, Object parameter, String alias) {
		return getSqlSessionFactoryList(alias).selectOne(statement, parameter);
	}

	@Override
	public <E> List<E> selectList(String statement) {
		return selectList(statement, null, WBDataBase.Alias.Default);
	}
	
	@Override
	public <E> List<E> selectList(String statement, Object parameter) {
		return selectList(statement, parameter, WBDataBase.Alias.Default);
	}

	@Override
	public <E> List<E> selectList(String statement, Object parameter, String alias) {
		return getSqlSessionFactoryList(alias).selectList(statement, parameter);
	}

	@Override
	public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
		return selectList(statement, parameter, rowBounds, WBDataBase.Alias.Default);
	}

	@Override
	public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds, String alias) {
		return getSqlSessionFactoryList(alias).selectList(statement, parameter, rowBounds);
	}

	@Override
	public int insert(String statement, Object parameter) {
		return insert(statement, parameter, WBDataBase.Alias.Default);
	}
	
	@Override
	public int insert(String statement, Object parameter, String alias) {
		return getSqlSessionFactoryList(alias).insert(statement, parameter);
	}
	
	@Override
	public int update(String statement, Object parameter) {
		return update(statement, parameter, WBDataBase.Alias.Default);
	}

	@Override
	public int update(String statement, Object parameter, String alias) {
		return getSqlSessionFactoryList(alias).update(statement, parameter);
	}

	@Override
	public int delete(String statement, Object parameter) {
		return delete(statement, parameter, WBDataBase.Alias.Default);
	}

	@Override
	public int delete(String statement, Object parameter, String alias) {
		return getSqlSessionFactoryList(alias).delete(statement, parameter);
	}
	
	/**
	 * 
	 * @param dbAlias
	 * @return
	 */
	public SqlSession getSqlSessionFactoryList(String dbAlias) {
		return getSqlSessionMap().get(dbAlias);
	}

}
