package kr.wrightbrothers.framework.support.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true, includeFieldNames = true)
@EqualsAndHashCode(callSuper = false)
public class WBAtte {

	private String atte;
	
}
