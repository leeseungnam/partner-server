package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.type.DeliveryType;
import kr.wrightbrothers.apps.common.type.OrderProductStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ReturnExcelDto {

    @Getter
    @Builder
    public static class Param {
        private String partnerCode;
        private List<String> returnList;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private int orderProductCount;
        private int productCount;
        private String requestReturnDay;
        private String orderNo;
        private String orderDay;
        private String orderName;
        private String productCode;
        private String productName;
        private String productOption;
        private Integer productQty;
        private Long productSellAmount;
        private Long productAmount;
        private Long productDeliveryChargeAmount;
        private Long paymentAmount;
        private String orderUserName;
        private String deliveryType;
        private String completeReturnDay;
        private String returnStatus;
        private String deliveryCompany;
        private String invoiceNo;
        private String recipientName;
        private String recipientUserPhone;
        private String recipientAddress;
        private String recipientAddressDetail;
        private String reason;

        public void setDeliveryType(String deliveryType) {
            this.deliveryType = DeliveryType.of(deliveryType).getName();
        }

        public void setReturnStatus(String returnStatus) {
            this.returnStatus = OrderProductStatusCode.of(returnStatus).getName();
        }
    }

}
