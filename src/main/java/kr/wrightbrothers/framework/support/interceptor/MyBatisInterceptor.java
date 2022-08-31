package kr.wrightbrothers.framework.support.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class MyBatisInterceptor implements Interceptor {

	private final ObjectFactory OBJECT_FACTORY = new DefaultObjectFactory();
	private final ObjectWrapperFactory OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	private final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		MetaObject metaObject = MetaObject.forObject(statementHandler, OBJECT_FACTORY, OBJECT_WRAPPER_FACTORY, REFLECTOR_FACTORY);
		RowBounds rb = (RowBounds) metaObject.getValue("delegate.rowBounds");
		/* 페이징 쿼리가 아닐 시 return */
		if (rb == null || rb == RowBounds.DEFAULT)
			return invocation.proceed();
		
		BoundSql boundSql = statementHandler.getBoundSql();
		String sql = boundSql.getSql();
		Object parameter = boundSql.getParameterObject();
		List<ParameterMapping> params = boundSql.getParameterMappings();
		/* 페이징 쿼리 일시 total count 및 페이징 쿼리 가공 */
		setTotalItems(metaObject, invocation, sql, parameter, params);
		
		/* 페이징 쿼리 변경 */
		setPageSql(metaObject, sql, parameter, boundSql);
		
		return invocation.proceed();
	}

	/**
	 * 페이징 쿼리 변경
	 */
	private void setPageSql(MetaObject metaObject, String sql, Object parameter, BoundSql boundSql) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
		ParameterMapping rOne = new ParameterMapping.Builder(mappedStatement.getConfiguration(),"rOne",int.class).jdbcType(JdbcType.INTEGER).mode(ParameterMode.IN).build();
		ParameterMapping rTwo = new ParameterMapping.Builder(mappedStatement.getConfiguration(),"rTwo",int.class).jdbcType(JdbcType.INTEGER).mode(ParameterMode.IN).build();
		
		Method one = parameter.getClass().getMethod("getROne");
		Method two = parameter.getClass().getMethod("getCount");
		boundSql.setAdditionalParameter("rOne", one.invoke(parameter));
		boundSql.setAdditionalParameter("rTwo", two.invoke(parameter));
		boundSql.getParameterMappings().add(rOne);
		boundSql.getParameterMappings().add(rTwo);
		
		List<ParameterMapping> params = boundSql.getParameterMappings();
		metaObject.setValue("delegate.boundSql.parameterMappings", params);
		
		StringBuffer pageSql = new StringBuffer(sql);
		pageSql.append("\n").append("limit ?, ?").append("\n");
		metaObject.setValue("delegate.boundSql.sql", pageSql.toString());
		metaObject.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
		metaObject.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
	}

	/**
	 * 전체 카운트 조회
	 */
	private void setTotalItems(MetaObject metaObject, Invocation invocation, String sql, Object parameter, List<ParameterMapping> params) throws Throwable {
		PreparedStatement countStatement = null;
		ResultSet rs = null;
		try {
			Connection connection = (Connection) invocation.getArgs()[0];
			countStatement = connection.prepareStatement("select count(*) from ( \n" + sql + "\n) a");

			if(params == null || params.size() == 0) {
				metaObject.setValue("delegate.boundSql.parameterMappings", new ArrayList<ParameterMapping>());
			}else {
				List<ParameterMapping> countParam = new ArrayList<ParameterMapping>();
				for (ParameterMapping param : params) {
					if (!param.getProperty().equalsIgnoreCase("rOne") && !param.getProperty().equalsIgnoreCase("rTwo"))
						countParam.add(param);
				}
				metaObject.setValue("delegate.boundSql.parameterMappings", countParam);
			}
			ParameterHandler parameterHandler = (ParameterHandler) metaObject.getValue("delegate.parameterHandler");
			parameterHandler.setParameters(countStatement);

			rs = countStatement.executeQuery();
			if (rs.next()) {
				Method method = parameter.getClass().getMethod("setTotalItems", int.class);
				method.invoke(parameter, rs.getInt(1));
			}
		}catch (Exception e) {
			log.error(e.getMessage());
		}finally {
			if(countStatement != null) countStatement.close();
			if(rs != null) rs.close();
		}
	}
}
