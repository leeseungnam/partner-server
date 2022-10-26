package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.type.PaymentStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import software.amazon.awssdk.services.licensemanager.model.LicenseStatus;

import java.util.List;

public class OrderExcelDto {

    @Getter
    @Builder
    public static class Param {
        private String partnerCode;
        private List<String> orderNoList;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private int productCount;
        private String orderDay;
        private String orderNo;
        private String orderUserName;
        private String orderStatus;
        private String orderName;
        private String productName;
        private String productOption;
        private Integer productQty;
        private Long productSellAmount;
        private Long productAmount;
        private Long paymentAmount;
        private Long productDeliveryChargeAmount;
        private String paymentMethod;
        private String paymentDay;
        private String paymentStatus;
        private String cancelDay;
        private String cancelReason;
        private String address;
        private String requestDetail;
        private String orderMemo;

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = OrderStatusCode.of(orderStatus).getName();
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = PaymentMethodCode.of(paymentMethod).getName();
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = PaymentStatusCode.of(paymentStatus).getName();
        }
    }
}
