package kr.wrightbrothers.apps.config.support;

public class AdminKey {

	public static final String ApplicationName = "PARTNER";

	public static class WBConfig {
		public static class Mybatis {

			public static final String DefaultDataSource 			= "DefaultDataSource";
			public static final String DefaultLog4jdbcProxySource 	= "DefaultLog4jdbcProxySource";
			public static final String DefaultSqlSessionFactory 	= "DefaultSqlSessionFactory";
			public static final String DefaultSqlSessionTemplate 	= "DefaultSqlSessionTemplate";
			public static final String DefaultTransactionManager 	= "DefaultTransactionManager";

			public static final String SessionDataSource 			= "SessionDataSource";
			public static final String SessionTransactionManager 	= "SessionTransactionManager";

			/** 전체 데이터 베이스 */
			public static final String GlobalTransactionManager 	= "GlobalTransactionManager";
		}
	}

	public static class WBDataBase {

		public static class Alias {
			public static final String Default 	= "Default";
		}

		public static class TransactionManager {
			public static final String Default 		= WBConfig.Mybatis.DefaultTransactionManager;
			public static final String Global 		= WBConfig.Mybatis.GlobalTransactionManager;
		}
	}

	public static class File {
		public static class EXT {
			public static final String TIF 		= "tif";
			public static final String TIFF 	= "tiff";
		}
	}

}
