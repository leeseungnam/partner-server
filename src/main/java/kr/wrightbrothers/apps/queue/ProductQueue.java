package kr.wrightbrothers.apps.queue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.type.DocumentSNS;
import kr.wrightbrothers.apps.common.type.ProductStatusCode;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import kr.wrightbrothers.apps.product.dto.ProductUpdateDto;
import kr.wrightbrothers.apps.queue.service.ProductQueueService;
import kr.wrightbrothers.framework.support.WBSQS;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO;
import kr.wrightbrothers.framework.util.WBAwsSns;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import kr.wrightbrothers.framework.support.dto.WBSnsDTO.Header;

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
                    DocumentSNS.UPDATE_PRODUCT.equals(documentSNS) ?
                            new UpdateSendDto(partnerCode, productCode) : productQueueService.findProductSnsData(partnerCode, productCode)
            );
        } catch (Exception e) {
            log.error("Product SNS Sender Error. {}", e.getMessage());
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
            JSONParser parser = new JSONParser();
            JSONObject body = (JSONObject) parser.parse(new ObjectMapper().writeValueAsString(snsDto.getBody()));

            // SQS 수신 된 Admin 2.0 입력 상품 입점몰 신규 등록
            if (PartnerKey.TransactionType.Insert.equals(snsDto.getHeader().getTrsctnTp())) {
                productQueueService.insertProductSqsData(body);
                ackMessage(snsDto);
                return;
            }

            // SQS 수신 된 Admin 2.0 입력 파트너 검수  결과 등록
            if (DocumentSNS.RESULT_INSPECTION.getName().equals(header.getDocuNm())) {
                ProductUpdateDto productUpdateDto =
                        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                                .convertValue(snsDto.getBody(), ProductUpdateDto.class);

                // 기본 데이터 초기화 설정
                productUpdateDto.setSqsLog(new String[]{"검수 완료"});
                productUpdateDto.setAopUserId(body.get("confirmUserId").toString());
                productUpdateDto.setAopPartnerCode(body.get("partnerCode").toString());
                productUpdateDto.setSqsProductCode(productUpdateDto.getProduct().getProductCode());

                // 검수반려 처리
                if (ProductStatusCode.REJECT_INSPECTION.getCode().equals(productUpdateDto.getSellInfo().getProductStatusCode())) {
                    String log = "검수 반려\n(" + body.get("rejectReason") + ")";
                    productUpdateDto.setSqsLog(new String[]{log});
                }

                // 검수 결과 처리
                productQueueService.updateInspectionSqsData(productUpdateDto);
            }

            ackMessage(snsDto);
        } catch (Exception e) {
            log.error("Product SQS Receive Error. {}", e.getMessage());
            ackMessage(snsDto, e);
        }
    }

    @Getter
    static class UpdateSendDto {
        private final String partnerCode; // 파트너 코드
        private final String productCode; // 상품 코드

        public UpdateSendDto(String partnerCode, String productCode) {
            this.partnerCode = partnerCode;
            this.productCode = productCode;
        }
    }
}
