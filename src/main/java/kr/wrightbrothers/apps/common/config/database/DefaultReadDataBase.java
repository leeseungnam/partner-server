package kr.wrightbrothers.apps.common.config.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import kr.wrightbrothers.apps.common.util.PartnerKey.WBConfig;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.interceptor.MyBatisInterceptor;
import lombok.RequiredArgsConstructor;
import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import net.sf.log4jdbc.tools.Log4JdbcCustomFormatter;
import net.sf.log4jdbc.tools.LoggingType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.stream.Stream;

@Configuration
@Lazy
@RequiredArgsConstructor
@EnableConfigurationProperties
@EnableTransactionManagement
public class DefaultReadDataBase {

	private final ApplicationContext ac;
	@Value("${spring.datasource-partner-read.username}")
	private String username;
	@Value("${spring.datasource-partner-read.password}")
	private String password;
	@Value("${spring.datasource-partner-read.jdbc-url}")
	private String url;
	@Value("${spring.datasource-partner-read.driver-class-name}")
	private String driverClassName;
	@Value("${spring.datasource-partner-read.maximumPoolSize}")
	private int maximumPoolSize;
	
	@Bean(name = WBConfig.Mybatis.DefaultReadDataSource)
	@Primary
	public DataSource DataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setUsername(username);
		hikariConfig.setPassword(password);
		hikariConfig.setJdbcUrl(url);
		hikariConfig.setAutoCommit(false);
		hikariConfig.setDriverClassName(driverClassName);
		hikariConfig.setMaximumPoolSize(maximumPoolSize);
		return new HikariDataSource(hikariConfig);
	}

	@Bean(name = WBConfig.Mybatis.DefaultReadLog4jdbcProxySource)
	@Primary
	public Log4jdbcProxyDataSource Log4jdbcProxySource() {
		Log4jdbcProxyDataSource proxyDataSource = new Log4jdbcProxyDataSource(DataSource());
		Log4JdbcCustomFormatter formatter = new Log4JdbcCustomFormatter();
		formatter.setLoggingType(LoggingType.MULTI_LINE);
		formatter.setSqlPrefix(WBKey.SqlPrefix);
		proxyDataSource.setLogFormatter(formatter);
		return proxyDataSource;
	}

	@Bean(name = WBConfig.Mybatis.DefaultReadSqlSessionFactory)
	@Primary
	public SqlSessionFactory SqlSessionFactory() throws Exception {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		Log4jdbcProxyDataSource log4jdbcProxyDataSource = Log4jdbcProxySource();
		sessionFactoryBean.setDataSource(log4jdbcProxyDataSource);
		
		org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
		configuration.setMapUnderscoreToCamelCase(true);
		configuration.setJdbcTypeForNull(JdbcType.NULL);
		configuration.setCacheEnabled(true);
		configuration.setLazyLoadingEnabled(true);
		configuration.setMultipleResultSetsEnabled(true);
		configuration.setDefaultFetchSize(10000);
		configuration.setCallSettersOnNulls(false);

		sessionFactoryBean.setConfiguration(configuration);
		sessionFactoryBean.setPlugins(new MyBatisInterceptor());
        
		Resource[] resources = ac.getResources(
					"classpath:/kr/wrightbrothers/apps/**/query/*.xml"
				);
		sessionFactoryBean.setMapperLocations(Stream.of(resources).flatMap(Stream::of).toArray(Resource[]::new));
		return sessionFactoryBean.getObject();
	}

	@Bean(name = WBConfig.Mybatis.DefaultReadSqlSessionTemplate)
	@Primary
	public SqlSessionTemplate SqlSessionTemplate() throws Exception {
		return new SqlSessionTemplate(SqlSessionFactory());
	}

	@Bean(name = WBConfig.Mybatis.DefaultReadTransactionManager)
	@Primary
	public PlatformTransactionManager TransactionManager() {
		return new DataSourceTransactionManager(Log4jdbcProxySource());
	}

}
