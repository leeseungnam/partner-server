package kr.wrightbrothers.apps.queue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.product.dto.ProductInsertDto;
import kr.wrightbrothers.apps.product.dto.ProductUpdateDto;
import kr.wrightbrothers.apps.queue.service.ProductQueueService;
import kr.wrightbrothers.framework.support.WBSQS;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import kr.wrightbrothers.framework.support.dto.WBSnsDTO.Header;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductQueue extends WBSQS {

    private final WBAwsSns sender;
    private final ProductQueueService productQueueService;
    @Value("${cloud.aws.sns.product}")
    private String topic;
    @Value("${cloud.aws.sqs.product}")
    private String queueName;

    /**
     * 입점몰 API -> ADMIN 2.0 API
     * AWS SNS Message Queue 발송 처리
     *
     * @param documentSNS 문서 명
     * @param partnerCode 파트너 코드
     * @param productCode 상폼 코드
     * @param transactionType 전송 타입 I/U
     */
    public void sendToAdmin(DocumentSNS documentSNS,
                            String partnerCode,
                            String productCode,
                            String transactionType) {
        try {
            // AWS SNS 전송
            sender.send(
                    topic,
                    Header.builder()
                            .docuNm(documentSNS.getName())
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
    @SqsListener(value = "${cloud.aws.sqs.product}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void receiveFromAdmin(String message) {
        WBSnsDTO snsDto = null;

        try {
            // 초기화
            initMessage(message, queueName);
            snsDto = getSqsMessage(WBSnsDTO.class);
            Header header = snsDto.getHeader();
            JSONObject body = new JSONObject(new ObjectMapper().writeValueAsString(snsDto.getBody()));

            log.info("Header SQS Info. {}", header);
            log.info("Product SQS Info. {}", body);

            // SQS 수신 된 Admin 2.0 입력 상품 입점몰 신규 등록
            if (PartnerKey.TransactionType.Insert.equals(snsDto.getHeader().getTrsctnTp())) {
                productQueueService.insertProductSqsData(body);
                ackMessage(snsDto);
                return;
            }

            // SQS 수신 된 Admin 2.0 입력 파트너 검수 결과 등록
            if (DocumentSNS.RESULT_INSPECTION.getName().equals(header.getDocuNm())) {
                ProductUpdateDto productUpdateDto =
                        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                .convertValue(snsDto.getBody(), ProductUpdateDto.class);

                // 기본 데이터 초기화 설정
                productUpdateDto.setSqsLog(new String[]{"검수완료"});
                productUpdateDto.setAopUserId(body.getString("confirmUserId"));
                productUpdateDto.setAopPartnerCode(body.getString("partnerCode"));
                productUpdateDto.setProductCode(productUpdateDto.getProduct().getProductCode());

                if (!ObjectUtils.isEmpty(body.getString("rejectDesc"))) {
                    String log = "검수반려\n(" + body.getString("rejectDesc") + ")";
                    productUpdateDto.setSqsLog(new String[]{log});
                }

                // 검수 결과 처리
                productQueueService.updateInspectionSqsData(productUpdateDto);
            } else {
                // 상품 수정 처리
                productQueueService.updateProductSqsData(body);
            }

            ackMessage(snsDto);
        } catch (Exception e) {
            log.error(e.getMessage());
            ackMessage(snsDto, e);
        }
    }
}
