package kr.wrightbrothers.framework.support.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true, includeFieldNames = true)
@EqualsAndHashCode(callSuper = false)
public class WBCode {

	/**
	 * 
	 */
	private String value;
	
	/**
	 * 
	 */
	private String text;
	
	/**
	 * 
	 */
	private String desc;
	
}
