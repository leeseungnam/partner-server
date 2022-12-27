package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.annotation.ExcelBody;
import kr.wrightbrothers.apps.common.constants.ExcelConst;
import kr.wrightbrothers.apps.common.constants.OrderConst;
import kr.wrightbrothers.apps.common.constants.PaymentConst;
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
        @ExcelBody(colIndex = 6, bodyType = ExcelConst.Type.LONG_TEXT)
        private String productName;
        @ExcelBody(colIndex = 7, bodyType = ExcelConst.Type.LONG_TEXT)
        private String productOption;
        @ExcelBody(colIndex = 8, bodyType = ExcelConst.Type.NUMBER)
        private Integer productQty;
        @ExcelBody(colIndex = 9, bodyType = ExcelConst.Type.NUMBER)
        private Long productSellAmount;
        @ExcelBody(colIndex = 10, bodyType = ExcelConst.Type.NUMBER)
        private Long productAmount;
        @ExcelBody(colIndex = 11, bodyType = ExcelConst.Type.NUMBER)
        private Long productDeliveryChargeAmount;
        @ExcelBody(colIndex = 12, bodyType = ExcelConst.Type.NUMBER)
        private Long point;
        @ExcelBody(colIndex = 13, bodyType = ExcelConst.Type.NUMBER)
        private Long salesAmount;
        @ExcelBody(colIndex = 14)
        private String rentalFlag;
        @ExcelBody(colIndex = 15, bodyType = ExcelConst.Type.NUMBER)
        private Long paymentAmount;
        @ExcelBody(colIndex = 16)
        private String paymentMethod;
        @ExcelBody(colIndex = 17)
        private String paymentDay;
        @ExcelBody(colIndex = 18)
        private String paymentStatus;
        @ExcelBody(colIndex = 19)
        private String cancelDay;
        @ExcelBody(colIndex = 20, bodyType = ExcelConst.Type.LONG_TEXT)
        private String cancelReason;
        @ExcelBody(colIndex = 21)
        private String recipientName;
        @ExcelBody(colIndex = 22)
        private String recipientUserPhone;
        @ExcelBody(colIndex = 23, bodyType = ExcelConst.Type.LONG_TEXT)
        private String address;
        @ExcelBody(colIndex = 24, bodyType = ExcelConst.Type.LONG_TEXT)
        private String requestDetail;
        @ExcelBody(colIndex = 25, bodyType = ExcelConst.Type.LONG_TEXT)
        private String orderMemo;

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = OrderConst.Status.of(orderStatus).getName();
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = PaymentConst.Method.of(paymentMethod).getName();
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = PaymentConst.Status.of(paymentStatus).getName();
        }
    }
}
