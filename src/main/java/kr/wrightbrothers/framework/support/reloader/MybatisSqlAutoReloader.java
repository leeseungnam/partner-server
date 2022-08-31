/*=========================================================
*Copyright(c) 2018 Hyundai Merchant Marine
*@FileName : QueryAutoReloader.java
*@Create Data : 2018. 10. 15.
*@History :
* hmm5114,	1.0,	2018. 10. 15.	최초 작성
=========================================================*/
package kr.wrightbrothers.framework.support.reloader;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SuppressWarnings("deprecation")
public class MybatisSqlAutoReloader implements DisposableBean, InitializingBean, ApplicationContextAware {

	private ApplicationContext applicationContext;
	private ScheduledExecutorService pool;
	private Boolean enableAutoReload = true;
	private String mapperLocations;
	private MapperScannerConfigurer config;
	private SqlSessionFactory sqlSessionFactory;

	/**
	 * 
	 * @param enableAutoReload
	 */
	public void setEnableAutoReload(Boolean enableAutoReload) {
		this.enableAutoReload = enableAutoReload;
	}

	/**
	 * 
	 * @param mapperLocations
	 */
	public void setMapperLocations(String mapperLocations) {
		if(!StringUtils.isEmpty(mapperLocations)){
			this.mapperLocations = mapperLocations;
		}
	}

	/**
	 * 
	 * @param config
	 */
	public void setConfig(MapperScannerConfigurer config) {
		this.config = config;
	}

	/**
	 * 
	 * @param sqlSessionFactory
	 */
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		if(!enableAutoReload){
			return;
		}

		checkProperties();
		String mapperLocations = getMapperLocations();
		pool = Executors.newScheduledThreadPool(2);

		final AutoReloadScanner scaner = new AutoReloadScanner(mapperLocations);
		scaner.start();

		pool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					scaner.scanAllFileChange();
				} catch (NoSuchFieldException e) {
					log.error(e.getMessage());
				} catch (SecurityException e) {
					log.error(e.getMessage());
				} catch (IllegalArgumentException e) {
					log.error(e.getMessage());
				} catch (IllegalAccessException e) {
					log.error(e.getMessage());
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		}, 2, 2, TimeUnit.SECONDS);

		pool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					scaner.scanHotspotFileChange();
				} catch (NoSuchFieldException e) {
					log.error(e.getMessage());
				} catch (SecurityException e) {
					log.error(e.getMessage());
				} catch (IllegalArgumentException e) {
					log.error(e.getMessage());
				} catch (IllegalAccessException e) {
					log.error(e.getMessage());
				} catch (IOException e) {
					log.error(e.getMessage());
				}
			}
		}, 2, 100, TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 * @return
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws Exception
	 */
	private String getMapperLocations() throws NoSuchFieldException, IllegalAccessException, Exception {

		if(mapperLocations != null){
			return mapperLocations;
		}

		if(config != null){
			Field field = config.getClass().getDeclaredField("basePackage");
			field.setAccessible(true);
			return (String) field.get(config);
		}

		if(sqlSessionFactory != null){
			Field field = sqlSessionFactory.getClass().getDeclaredField("mapperLocations");
			field.setAccessible(true);
			Resource[] mapperLocations = (Resource[]) field.get(sqlSessionFactory);
			StringBuilder sb = new StringBuilder();
			for(Resource r : mapperLocations){
				String n = r.getURL().toString();
				sb.append(n).append("\n");
			}
			return sb.toString();
		}
		throw new RuntimeException("mapperLocations 읽기 실패！");
	}

	/**
	 * sqlSessionFactory 속성을 설정하지 않으면 기본 모드로 직접 초기화
	 */
	private void checkProperties() {
		// 데이터 소스를 지정하지 않으면 기본 메소드를 사용하여 데이터 소스를 가져옵니다.
		if(sqlSessionFactory == null){
			try {
				sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
			} catch (BeansException e) {
				throw new RuntimeException("데이터 소스를 가져 오는 데 실패했습니다.！", e);
			}
		}

		// 구성 파일을 지정하지 않으면 구성 파일이 기본적으로 확보됩니다
		if(config == null && mapperLocations == null){
      try {
				config = applicationContext.getBean(MapperScannerConfigurer.class);
			} catch (BeansException e) {
				System.err.println("구성 파일을 가져 오는 데 실패했습니다.！");
			}
		}

		if(config == null && mapperLocations == null){
			throw new RuntimeException("구성 매퍼 위치 설정에 실패했습니다! 구성 등록 정보를 설정하십시오. 그렇지 않으면 자동 핫로드가 작동하지 않습니다!");
		}
	}

	@Override
	public void destroy() throws Exception {
		if(pool == null){
			return;
		}
		pool.shutdown(); // 스레드 풀 리소스
	}

	/**
	 * 자동 과부하 스캐너의 구현
	 */
	class AutoReloadScanner {

		static final String XML_RESOURCE_PATTERN = "query/*.xml";
		static final String CLASSPATH_ALL_URL_PREFIX = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;
		static final int expireTimes = 600 * 2;		// 2 분 이내에 더 이상 수정하지 않고 핫스팟이 아닌 파일이됩니다. 실시간 모니터링은 없습니다.

		//스캔 할 패키지
		String[] basePackages;
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		//모든파일MAP
		Map<String, String> files = new ConcurrentHashMap<String, String>();
		Map<String, AtomicInteger> hotspot = new ConcurrentHashMap<String, AtomicInteger>();

		public AutoReloadScanner(String basePackage) {
			basePackages = StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
		}

		public void scanHotspotFileChange() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {

			if(hotspot.isEmpty()){
				return;
			}

			List<String> list = new ArrayList<String>();
			for(Map.Entry<String, AtomicInteger> e : hotspot.entrySet()){
				String url = e.getKey();
				AtomicInteger counter = e.getValue();
				if(counter.incrementAndGet() >= expireTimes){
					list.add(url);
				}
				if(hasChange(url,files.get(url))){
					reload(url);
					counter.set(0);
				}
			}

			if (!list.isEmpty()) {
				for (String s : list) {
					hotspot.remove(s);
				}
			}
		}

		/**
		 * 
		 * @param url
		 * @throws IOException 
		 * @throws IllegalAccessException 
		 * @throws IllegalArgumentException 
		 * @throws SecurityException 
		 * @throws NoSuchFieldException 
		 */
		private void reload(String url) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
			reloadAll();
		}

		/**
		 * @throws IOException 
		 * @throws IllegalAccessException 
		 * @throws IllegalArgumentException 
		 * @throws SecurityException 
		 * @throws NoSuchFieldException 
		 * 
		 */
		private void reloadAll() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			StopWatch sw = new StopWatch("mybatis mapper auto reload");
			sw.start();
			Configuration configuration = getConfiguration();
			for(Map.Entry<String, String> entry : files.entrySet()){
				String location = entry.getKey();
				Resource r = resourcePatternResolver.getResource(location);
				try {
					XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(r.getInputStream(), configuration, r.toString(), configuration.getSqlFragments());
					xmlMapperBuilder.parse();
		        } finally {
		          ErrorContext.instance().reset();
		        }
			}
			sw.stop();
			log.debug(sw.shortSummary());
		}

		/**
		 * @throws IOException 
		 * @throws IllegalAccessException 
		 * @throws IllegalArgumentException 
		 * @throws SecurityException 
		 * @throws NoSuchFieldException 
		 * 
		 */
		public void scanAllFileChange() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, IOException {
			for(Map.Entry<String, String> entry : files.entrySet()){
				String url = entry.getKey();
				if(hasChange(url, entry.getValue())){
					if(!hotspot.containsKey(url)){
						hotspot.put(url, new AtomicInteger(0));
						reload(url);
					}
				}
			}
		}

		/**
		 * 
		 * @param url
		 * @param tag
		 * @return
		 */
		private boolean hasChange(String url, String tag) {
			Resource r= resourcePatternResolver.getResource(url);
			String newTag = getTag(r);
			if(!tag.equals(newTag)){
				files.put(url, newTag);	
				return true;
			}
			return false;
		}

		/**
		 * 
		 * @param r
		 * @return
		 */
		private String getTag(Resource r) {
			try {
				StringBuilder sb = new StringBuilder();
				sb.append(r.contentLength());
				sb.append(r.lastModified());
				return sb.toString();
			} catch (IOException e) {
				throw new RuntimeException("파일 태그 정보를 가져 오는 데 실패했습니다！r=" + r, e);
			}
		}

		/**
		 * @throws IOException 
		 * 
		 */
		public void start() throws IOException {
			for (String basePackage : basePackages) {
				Resource[] resources = getResource(basePackage);
				if (resources != null) {
					for (Resource r : resources) {
						String tag = getTag(r);
						files.put(r.getURL().toString(), tag);
					}
				}
			}
		}

		/**
		 * 
		 * @param basePackage
		 * @return
		 * @throws IOException 
		 */
		public Resource[] getResource(String basePackage) throws IOException {
			if (!basePackage.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
				basePackage = CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(applicationContext.getEnvironment().resolveRequiredPlaceholders(basePackage)) + "/" + XML_RESOURCE_PATTERN;
			}
			Resource[] resources = resourcePatternResolver.getResources(basePackage);
			return resources;
		}

	   /**
	    * 구성 정보를 얻으면 매회 다시 획득해야합니다. 그렇지 않으면 xml을 다시로드해도 작동하지 않습니다.
	    * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	    */
		private Configuration getConfiguration() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			Configuration configuration = sqlSessionFactory.getConfiguration();
			removeConfig(configuration);
			return configuration;
		}

		/**
		 * 불필요한 구성 항목을 삭제하십시오.
		 * @param configuration
		 * @throws IllegalAccessException 
		 * @throws IllegalArgumentException 
		 * @throws SecurityException 
		 * @throws NoSuchFieldException 
		 */
		private void removeConfig(Configuration configuration) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			Class<?> classConfig = configuration.getClass();
			clearMap(classConfig, configuration, "mappedStatements");
			clearMap(classConfig, configuration, "caches");
			clearMap(classConfig, configuration, "resultMaps");
			clearMap(classConfig, configuration, "parameterMaps");
			clearMap(classConfig, configuration, "keyGenerators");
			clearMap(classConfig, configuration, "sqlFragments");
			clearSet(classConfig, configuration, "loadedResources");
		}

		/**
		 * 
		 * @param classConfig
		 * @param configuration
		 * @param fieldName
		 * @throws SecurityException 
		 * @throws NoSuchFieldException 
		 * @throws IllegalAccessException 
		 * @throws IllegalArgumentException 
		 * @throws Exception
		 */
		private void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			Field field = classConfig.getDeclaredField(fieldName);
			field.setAccessible(true);
			Map<?, ?> mapConfig = (Map<?, ?>) field.get(configuration);
			mapConfig.clear();
		}

		/**
		 * 
		 * @param classConfig
		 * @param configuration
		 * @param fieldName
		 * @throws SecurityException 
		 * @throws NoSuchFieldException 
		 * @throws IllegalAccessException 
		 * @throws IllegalArgumentException 
		 * @throws Exception
		 */
		private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException  {
			Field field = classConfig.getDeclaredField(fieldName);
			field.setAccessible(true);
			Set<?> setConfig = (Set<?>) field.get(configuration);
			setConfig.clear();
		}
	}
}
