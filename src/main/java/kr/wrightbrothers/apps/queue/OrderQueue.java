package kr.wrightbrothers.apps.queue;

import kr.wrightbrothers.framework.support.WBSQS;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderQueue extends WBSQS {

    private final WBAwsSns sender;

}
