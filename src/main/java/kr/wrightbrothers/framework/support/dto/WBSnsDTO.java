package kr.wrightbrothers.framework.support.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WBSnsDTO {

	private Header header;
	private Ack ack;
	private Object body;

	@Data
	@Jacksonized
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
	public static class Header {

		/** 애플리케이션 이름 */
		@JsonProperty("ApplicationName")
		private String appNm;
		/** 내부문서명 */
		@JsonProperty("DocumentName")
		private String docuNm;
		/** 프로세스 아이디 */
		@JsonProperty("UniqueId")
		private String uniqueId;
		/** 트랜젝션 타입 */
		@JsonProperty("TransactionType")
		private String trsctnTp;
		

	}

	@Data
	@Jacksonized
	@SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
	public static class Ack {

		/** 애플리케이션 이름 */
		private String msgId;
		/** 애플리케이션 이름 */
		private String appNm;
		/** 프로세스 아이디 */
		private String uniqueId;
		/** 처리상태 코드 */
		private String stusCd;
		/** 에러메세지 */
		private String errMsg;
		/** 큐명 */
		private String queueName;
		
		
	}
}
