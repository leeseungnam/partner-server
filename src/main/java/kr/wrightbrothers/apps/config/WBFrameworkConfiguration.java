package kr.wrightbrothers.apps.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.framework.support.interceptor.WBInterceptor;
import kr.wrightbrothers.apps.util.PartnerKey;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import kr.wrightbrothers.framework.support.reloader.MybatisSqlAutoReloader;
import kr.wrightbrothers.framework.support.transaction.WBMultiTransactionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WBFrameworkConfiguration {

	private final ApplicationContext ac;
	private final String namespace = "kr.wrightbrothers.apps.common.config.query.Config.";

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * 멀티 트랜젝션 이용 시 설정하는 글로벌 트랜젝션
	 */
	@Bean(PartnerKey.WBDataBase.TransactionManager.Global)
	public PlatformTransactionManager multiTransactionManager(
			@Qualifier(PartnerKey.WBConfig.Mybatis.DefaultTransactionManager) PlatformTransactionManager defaultTransactionManager,
			@Qualifier(PartnerKey.WBConfig.Mybatis.AdminTransactionManager) PlatformTransactionManager adminTransactionManager
			) {
	    return new WBMultiTransactionManager(defaultTransactionManager, adminTransactionManager);
	}

	/**
	 * HTTP 통신에 대한 정보를 관리하기 위한 Interceptor 객체
	 */
	@Bean
	public WBInterceptor interceptor() {
		return new WBInterceptor();
	}

	/**
	 * DB 연결하는 공통 객체
	 */
	@Bean
	public WBCommonDao commonDao(
				@Qualifier(PartnerKey.WBConfig.Mybatis.DefaultSqlSessionTemplate) SqlSession defaultSqlSession,
				@Qualifier(PartnerKey.WBConfig.Mybatis.AdminSqlSessionTemplate) SqlSession adminSqlSession
			) {
		Map<String, SqlSession> sqlSessionMap = new HashMap<String, SqlSession>();
		sqlSessionMap.put(PartnerKey.WBDataBase.Alias.Default, defaultSqlSession);
		sqlSessionMap.put(PartnerKey.WBDataBase.Alias.Admin, adminSqlSession);
		return new WBCommonDao(sqlSessionMap);
	}
	
	/**
	 * Mybatis Query 변경 시 자동 로그 기능
	 */
	@Bean
	public MybatisSqlAutoReloader mybatisSqlAutoReloader(@Qualifier(PartnerKey.WBConfig.Mybatis.DefaultSqlSessionFactory) SqlSessionFactory sqlSessionFactory) {
		MybatisSqlAutoReloader reloader = new MybatisSqlAutoReloader();
		reloader.setEnableAutoReload(true);
		reloader.setSqlSessionFactory(sqlSessionFactory);
		reloader.setMapperLocations("/kr/wrightbrothers/apps/**");
		return reloader;
	}
	
}
