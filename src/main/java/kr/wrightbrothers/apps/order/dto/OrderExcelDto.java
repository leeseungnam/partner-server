package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.annotation.ExcelBody;
import kr.wrightbrothers.apps.common.type.ExcelBodyType;
import kr.wrightbrothers.apps.common.type.OrderStatusCode;
import kr.wrightbrothers.apps.common.type.PaymentMethodCode;
import kr.wrightbrothers.apps.common.type.PaymentStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
        @ExcelBody(colIndex = 1)
        private String orderDay;
        @ExcelBody(colIndex = 2)
        private String orderNo;
        @ExcelBody(colIndex = 3)
        private String orderUserName;
        @ExcelBody(colIndex = 4)
        private String orderStatus;
        @ExcelBody(colIndex = 5)
        private String orderName;
        @ExcelBody(colIndex = 6, bodyType = ExcelBodyType.LONG_TEXT)
        private String productName;
        @ExcelBody(colIndex = 7, bodyType = ExcelBodyType.LONG_TEXT)
        private String productOption;
        @ExcelBody(colIndex = 8, bodyType = ExcelBodyType.NUMBER)
        private Integer productQty;
        @ExcelBody(colIndex = 9, bodyType = ExcelBodyType.NUMBER)
        private Long productSellAmount;
        @ExcelBody(colIndex = 10, bodyType = ExcelBodyType.NUMBER)
        private Long productAmount;
        @ExcelBody(colIndex = 12, bodyType = ExcelBodyType.NUMBER)
        private Long paymentAmount;
        @ExcelBody(colIndex = 11, bodyType = ExcelBodyType.NUMBER)
        private Long productDeliveryChargeAmount;
        @ExcelBody(colIndex = 13)
        private String paymentMethod;
        @ExcelBody(colIndex = 14)
        private String paymentDay;
        @ExcelBody(colIndex = 15)
        private String paymentStatus;
        @ExcelBody(colIndex = 16)
        private String cancelDay;
        @ExcelBody(colIndex = 17)
        private String cancelReason;
        @ExcelBody(colIndex = 18, bodyType = ExcelBodyType.LONG_TEXT)
        private String address;
        @ExcelBody(colIndex = 19, bodyType = ExcelBodyType.LONG_TEXT)
        private String requestDetail;
        @ExcelBody(colIndex = 20, bodyType = ExcelBodyType.LONG_TEXT)
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
