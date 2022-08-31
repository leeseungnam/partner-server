package kr.wrightbrothers.framework.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class WBCommon {

	private String state;
	private String uuid;
	private String msgCode;
	private String msgType;
	private String message;
	private String token;
	
	public WBCommon(String state) {
		this.state = state;
	}
}
