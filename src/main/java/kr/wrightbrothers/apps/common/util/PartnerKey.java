package kr.wrightbrothers.apps.common.util;

public class PartnerKey {

	public static final String ApplicationName 	= "PARTNER";
	public static final String BasePackage		= "kr.wrightbrothers.apps";

	public static class WBConfig {
		public static class Mybatis {
			// 기본 데이터 설정
			public static final String DefaultDataSource 			= "DefaultDataSource";
			public static final String DefaultLog4jdbcProxySource 	= "DefaultLog4jdbcProxySource";
			public static final String DefaultSqlSessionFactory 	= "DefaultSqlSessionFactory";
			public static final String DefaultSqlSessionTemplate 	= "DefaultSqlSessionTemplate";
			public static final String DefaultTransactionManager 	= "DefaultTransactionManager";

			// 어드민 데이터 설정
			public static final String AdminDataSource 				= "AdminDataSource";
			public static final String AdminLog4jdbcProxySource 	= "AdminLog4jdbcProxySource";
			public static final String AdminSqlSessionFactory 		= "AdminSqlSessionFactory";
			public static final String AdminSqlSessionTemplate 		= "AdminSqlSessionTemplate";
			public static final String AdminTransactionManager 		= "AdminTransactionManager";

			public static final String SessionDataSource 			= "SessionDataSource";
			public static final String SessionTransactionManager 	= "SessionTransactionManager";

			/** 전체 데이터 베이스 */
			public static final String GlobalTransactionManager 	= "GlobalTransactionManager";
		}
	}

	public static class Jwt {
		public static final String HeaderName	= "X-AUTH-TOKEN";
		public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
		public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
	}

	public static class WBDataBase {

		public static class Alias {
			public static final String Default 	= "Default";
			public static final String Admin	= "Admin";
		}

		public static class TransactionManager {
			public static final String Default 		= WBConfig.Mybatis.DefaultTransactionManager;
			public static final String Admin		= WBConfig.Mybatis.AdminTransactionManager;
			public static final String Global 		= WBConfig.Mybatis.GlobalTransactionManager;
		}
	}

	public static class File {
		public static class EXT {
			public static final String TIF 		= "tif";
			public static final String TIFF 	= "tiff";
		}
	}

	public static enum JwtCode {
		DENIED,
		ACCESS,
		EXPIRED
	}
}
