package kr.wrightbrothers.apps.batch;

import kr.wrightbrothers.apps.common.util.QuartzUtil;
import kr.wrightbrothers.framework.support.WBController;
import kr.wrightbrothers.framework.support.WBModel;
import kr.wrightbrothers.framework.support.quartz.dto.QuartzDto;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class QuartzScheduleController extends WBController {

	@Autowired
	private QuartzUtil quartzUtil;
	
	/**
	 * 상품 등록
	 * @param paramDto
	 * @return
	 * @throws Exception
	 */
	@PostMapping("common/schedule/addJob")
	public WBModel addJob(@RequestBody QuartzDto paramDto) throws Exception {
		paramDto.setJobClass((Class<? extends Job>)Class.forName(paramDto.getExecuteJobClass()));
//		paramDto.setJobClass((Class<? extends Job>)PartnerNotRegisterJob.class);
		/** SimpleJob */
		if(paramDto.getIntervalInSeconds() > 0)
			quartzUtil.addQuartzSimpleJob(paramDto, paramDto.getIntervalInSeconds(), new Date(System.currentTimeMillis()).getTime());
		
		/** CronJob */
		else
			quartzUtil.addQuartzCronJob(paramDto, paramDto.getCronExpression(), new Date(System.currentTimeMillis()).getTime());
		
		return noneDataResponse();
	}
}
