package kr.wrightbrothers.apps.common.util;

public class PartnerKey {

	public static final String ApplicationName 	= "PARTNER";
	public static final String BasePackage		= "kr.wrightbrothers.apps";

	public static final String INTSTRING_TRUE 	= "1";
	public static final String INTSTRING_FALSE 	= "0";

	public static class WBConfig {

		public static class Message {
			public static final String Alias = "message";
			public static final String LANG_KO = "ko";
			public static final String LANG_EN = "en";
			public static final String LANG_JA = "ja";

		}
		public static class Mybatis {
			// 기본 데이터 설정
			public static final String DefaultDataSource 			= "DefaultDataSource";
			public static final String DefaultLog4jdbcProxySource 	= "DefaultLog4jdbcProxySource";
			public static final String DefaultSqlSessionFactory 	= "DefaultSqlSessionFactory";
			public static final String DefaultSqlSessionTemplate 	= "DefaultSqlSessionTemplate";
			public static final String DefaultTransactionManager 	= "DefaultTransactionManager";

			// 파트너 데이터 설정(읽기권한)
			public static final String DefaultReadDataSource 			= "DefaultReadDataSource";
			public static final String DefaultReadLog4jdbcProxySource 	= "DefaultReadLog4jdbcProxySource";
			public static final String DefaultReadSqlSessionFactory 	= "DefaultReadSqlSessionFactory";
			public static final String DefaultReadSqlSessionTemplate 	= "DefaultReadSqlSessionTemplate";
			public static final String DefaultReadTransactionManager 	= "DefaultReadTransactionManager";

			// 어드민 데이터 설정
			public static final String AdminDataSource 				= "AdminDataSource";
			public static final String AdminLog4jdbcProxySource 	= "AdminLog4jdbcProxySource";
			public static final String AdminSqlSessionFactory 		= "AdminSqlSessionFactory";
			public static final String AdminSqlSessionTemplate 		= "AdminSqlSessionTemplate";
			public static final String AdminTransactionManager 		= "AdminTransactionManager";

			// 어드민 데이터 설정(읽기권한)
			public static final String AdminReadDataSource 			= "AdminReadDataSource";
			public static final String AdminReadLog4jdbcProxySource = "AdminReadLog4jdbcProxySource";
			public static final String AdminReadSqlSessionFactory 	= "AdminReadSqlSessionFactory";
			public static final String AdminReadSqlSessionTemplate 	= "AdminReadSqlSessionTemplate";
			public static final String AdminReadTransactionManager 	= "AdminReadTransactionManager";

			public static final String SessionDataSource 			= "SessionDataSource";
			public static final String SessionTransactionManager 	= "SessionTransactionManager";

			/** 전체 데이터 베이스 */
			public static final String GlobalTransactionManager 	= "GlobalTransactionManager";
		}
	}

	public static class TransactionType {
		public static final String Insert 	= "I";
		public static final String Update 	= "U";
		public static final String Delete 	= "D";
		public static final String Read 	= "R";
	}

	public static class Jwt {
		public static class Alias {
			public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
			public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
			public static final String AUTH = "auth";
			public static final String USER_AUTH = "userAuth";
			public static final String NAME = "name";
			public static final String STATUS = "status";
		}
		public static class Header {
			public static final String X_AUTH_TOKEN	= "X-AUTH-TOKEN";
			public static final String AUTHORIZATION = "Authorization";
		}

		public static class Type {
			public static final String BEARER = "Bearer ";
		}
	}

	public static class Const {
		public static final String Y = "Y";
		public static final String N = "N";
		public static final String DP = "DP";
	}

	public static class WBDataBase {

		public static class Alias {
			public static final String Default		= "Default";
			public static final String DefaultRead	= "DefaultRead";
			public static final String Admin		= "Admin";
			public static final String AdminRead 	= "AdminRead";
		}

		public static class TransactionManager {
			public static final String Default 		= WBConfig.Mybatis.DefaultTransactionManager;
			public static final String DefaultRead 	= WBConfig.Mybatis.DefaultReadTransactionManager;
			public static final String Admin		= WBConfig.Mybatis.AdminTransactionManager;
			public static final String AdminRead	= WBConfig.Mybatis.AdminReadTransactionManager;
			public static final String Global 		= WBConfig.Mybatis.GlobalTransactionManager;
		}
	}

	public static class File {
		public static class EXT {
			public static final String TIF 		= "tif";
			public static final String TIFF 	= "tiff";
		}
	}

	public static class Aws {
		public static class A3 {
			public static final String Product_Img_Path 		= "product/";
			public static final String Partner_Img_Path 		= "partner/";
			public static final String Brand_Img_Path			= "brand/";
			public static final String Editor_Img_Path			= "editor/";
			public static final String tif_Img_Path				= "tif/";
		}
		public static class Sns {
			public static final String GroupId = "WB";
		}
	}

	public static class Regex {
		public static final String Date	= "^((19|20)\\d{2})(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[0-1])";
	}
	public static enum JwtCode {
		DENIED,
		ACCESS,
		EXPIRED
	}
}
