package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.type.DeliveryStatusCode;
import kr.wrightbrothers.apps.common.type.DeliveryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class DeliveryExcelDto {

    @Getter
    @Builder
    public static class Param {
        private String partnerCode;
        private List<String> deliveryList;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private int orderProductCount;
        private int productCount;
        private String paymentDay;
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
        private String deliveryStatus;
        private String deliveryCompany;
        private String invoiceNo;
        private String recipientName;
        private String recipientUserPhone;
        private String recipientAddress;
        private String recipientAddressDetail;
        private String requestDetail;
        private String deliveryMemo;

        public void setDeliveryStatus(String deliveryStatus) {
            this.deliveryStatus = DeliveryStatusCode.of(deliveryStatus).getName();
        }

        public void setDeliveryType(String deliveryType) {
            this.deliveryType = DeliveryType.of(deliveryType).getName();
        }
    }

}
