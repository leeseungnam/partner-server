package kr.wrightbrothers.framework.support.quartz.dto;

import kr.wrightbrothers.framework.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.Job;
import org.quartz.JobDataMap;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuartzDto {
	
	private String jobName;
	private String groupName;
	private JobDataMap parameter;
	private Class<? extends Job> JobClass;
	private String executeJobClass;
	private int intervalInSeconds;
	private String cronExpression;
	
	public void addParameter(String parameterKey, Object parameterValue) throws Exception {
		if(parameter == null) parameter = new JobDataMap();
		if (parameterValue instanceof String) {
			this.parameter.put(parameterKey, parameterValue);
		} else {
			this.parameter.put(parameterKey, JsonUtil.ToString(parameterValue));
		}
	}
}
