package kr.wrightbrothers.apps.batch;

import kr.wrightbrothers.apps.batch.service.BatchService;
import kr.wrightbrothers.framework.support.quartz.DisallowConcurrent;
import kr.wrightbrothers.framework.util.StaticContextAccessor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회원가입 후 3일간 소속된 스토어가 없는 회원에게 메일 발송
 */
@Slf4j
public class PartnerContractRenewalJob extends DisallowConcurrent {

    @Override
    protected void doExecute() throws Exception {

        try {
            BatchService service = StaticContextAccessor.getBean(BatchService.class);
            service.renewalContract();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }

    }
}
