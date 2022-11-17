package kr.wrightbrothers.framework.support;

public class WBKey {

	public static final String SqlPrefix 			= "SQL         :  \n";
	public static final String Locale 				= "System.Locale";
	public static final String Error 				= "E";
	public static final String Success 				= "S";
	public static final String View 				= "JSON";
	public static final String UUID 				= "WBUUID";
	public static final String SessionID 			= "WRIGHTBROTHERS_SESSIONID";
	public static final String InfoMailAddr 		= "info@wrightbrothers.kr";
	public static final String ExcludeUrlPatterns 	= "System.Exclude.Url.Patterns";
	public static final String CrossDomainUrl 		= "System.Cross.Domain.Url";
	public static final String Y 					= "Y";
	public static final String N 					= "N";
	public static final String Single 				= "S";
	public static final String WBAdminUser			= "A";
	public static final String UTF8					= "UTF-8";
	public static final String RequestMethodGet		= "GET";
	public static final String RequestMethodPup		= "PUT";
	public static final String RequestMethodPost	= "POST";
	public static final String RequestMethodDelete	= "DELETE";
	public static final String RequestMethodOption	= "OPTIONS";
	public static final String TempUser				= "Temp-System";
	public static final String JWTExpired			= "JWT-expired";
	
	public static class Product {
		public static final String partner 			= "P";
	}
	
	/**
	 * 파일 관련
	 * @author ISJUNG
	 *
	 */
	public static class File {
		public static final String TempPath 			= "System.File.Temp.Path";
		public static final String UploadFileDataSet 	= "fileDataSet";
		public static final String UploadFileNo 		= "fileNo";
		public static class Excel {
			public static final String xlsx 			= ".xlsx";
		}
	}
	
	/**
	 * 트랜젝션 정보
	 * @author ISJUNG
	 *
	 */
	public static class TransactionType {
		public static final String Insert 	= "I";
		public static final String Update 	= "U";
		public static final String Delete 	= "D";
		public static final String Read 	= "R";
		public static final String Clone	= "C";
	}
	
	/**
	 * 
	 * @author ISJUNG
	 *
	 */
	public static class WBModel {
		public static final String DefaultDataKey 				= "data";
		public static final String UserKey 						= "user";
		public static final String DefaultDataTotalCountKey 	= "totalItems";
	}
	
	public static class WBDataBase {
		
		public static class Alias {
			public static final String Default 	= "Default";
		}
		
		public static class TransactionManager {
			public static final String Default 		= "DefaultTransactionManager";
		}
	}
	
	public static class Message {
		public static class Type {
			public static final String Error 		= "E";
			public static final String Info 		= "I";
			public static final String Warning 		= "W";
			public static final String Notification = "N";
		}
	}
	
	public static class Aws {
		public static class A3 {
			public static final String Product_Img_Path 		= "product/";
			public static final String Partner_Img_Path 		= "partner/";
			public static final String CPLB_Img_Path 			= "cplb/";
			public static final String Brand_Img_Path			= "brand/";
			public static final String Editor_Img_Path			= "editor/";
			public static final String tif_Img_Path				= "tif/";
		}
		public static class Sns {
			public static final String GroupId = "WB";
		}
	}
	
	public static class Jwt {
		public static final String Secretkey 			= "System.Jwt.Secretkey";
		public static final String TokenKey 			= "TokenKey";
		public static final String HeaderName 			= "X-AUTH-TOKEN";
		public static final String AccessTokenName 		= "AccessTokenName";	
	}

	public static class Valid {
		public static final String Excel	= "%s, 엑셀 업로드 파일의 데이터가 올바르지 않습니다.";
		public static final String Param	= "%s, 필수 항목의 값을 입력 하세요.";
		public static final String Number	= "%s, 숫자만 입력할 수 있습니다.";
		public static final String Size		= "%s, 입력 범위를 다시 확인 하세요.";
		public static final String Date		= "%s, 날짜 입력이 올바르지 않습니다.";
		public static final Long MinPrice	= 1000L;
		public static final Long MaxPrice	= 100000000L;
	}

	public static class Regex {
		public static final String Date	= "^((19|20)\\d{2})(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[0-1])";
		public static final String PurPrdtNo = "^\\d{6}[P,T,C,S]\\d{3}";
	}
}
