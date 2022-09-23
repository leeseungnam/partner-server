package kr.wrightbrothers.apps.queue;

import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.queue.service.ProductQueueService;
import kr.wrightbrothers.framework.support.WBSQS;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kr.wrightbrothers.framework.support.dto.WBSnsDTO.Header;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductQueue extends WBSQS {

    private final WBAwsSns sender;
    private final String documentName = "입점몰 상품 등록/수정";
    private final ProductQueueService productQueueService;
    @Value("${cloud.aws.sns.partner}")
    private String topic;
    @Value("${cloud.aws.sqs.partner}")
    private String queueName;

    /**
     * 입점몰 API -> ADMIN 2.0 API
     * AWS SNS Message Queue 발송 처리
     *
     * @param partnerCode 파트너 코드
     * @param productCode 상폼 코드
     * @param transactionType 전송 타입 I/U
     */
    public void sendToAdmin(String partnerCode,
                            String productCode,
                            String transactionType) {
        try {
            // AWS SNS 전송
            sender.send(
                    topic,
                    Header.builder()
                            .docuNm(documentName)
                            .trsctnTp(transactionType)
                            .build(),
                    // 상품 SNS 전송 데이터
                    productQueueService.findProductSnsData(partnerCode, productCode)
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * ADMIN 2.0 API -> 입점몰 API
     * AWS SQS Message Queue 수신 처리
     *
     * @param message SQS 상품 수신 데이터
     */
    public void receiveFromAdmin(String message) {
        WBSnsDTO snsDto = null;

        try {
            // 초기화
            initMessage(message, queueName);
            snsDto = getSqsMessage(WBSnsDTO.class);

            // SQS 수신 된 Admin 2.0 입력 상품 입점몰 신규 등록
            if (PartnerKey.TransactionType.Insert.equals(snsDto.getHeader().getTrsctnTp())) {
                productQueueService.insertProductSqsData();

                ackMessage(snsDto);
                return;
            }

            // 입점몰 상품
            productQueueService.updateProductSqsData();
            ackMessage(snsDto);
        } catch (Exception e) {
            log.error(e.getMessage());
            ackMessage(snsDto, e);
        }
    }
}
