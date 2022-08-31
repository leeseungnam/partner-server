package kr.wrightbrothers.framework.support.dao;

import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface ICommonDao {

	  <T> T selectOne(String statement, Object parameter);

	  <T> T selectOne(String statement, Object parameter, String alias);
	  
	  <E> List<E> selectList(String statement);

	  <E> List<E> selectList(String statement,Object parameter);

	  <E> List<E> selectList(String statement, Object parameter, String alias);

	  <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds, String alias);

	  int insert(String statement, Object parameter, String alias);

	  int insert(String statement, Object parameter);
	  
	  int update(String statement, Object parameter, String alias);

	  int update(String statement, Object parameter);
	  
	  int delete(String statement, Object parameter, String alias);
	  
	  int delete(String statement, Object parameter);

	  <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);
	  
}
