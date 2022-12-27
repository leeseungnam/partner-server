package kr.wrightbrothers.apps.order.dto;

import kr.wrightbrothers.apps.common.annotation.ExcelBody;
import kr.wrightbrothers.apps.common.constants.DeliveryConst;
import kr.wrightbrothers.apps.common.constants.ExcelConst;
import kr.wrightbrothers.apps.common.util.MaskingUtil;
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
        @ExcelBody(colIndex = 1)
        private String paymentDay;
        @ExcelBody(colIndex = 2)
        private String orderNo;
        @ExcelBody(colIndex = 3)
        private String orderDay;
        @ExcelBody(colIndex = 4, bodyType = ExcelConst.Type.LONG_TEXT)
        private String orderName;
        @ExcelBody(colIndex = 5)
        private String productCode;
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
        private Long paymentAmount;
        @ExcelBody(colIndex = 13)
        private String orderUserName;
        @ExcelBody(colIndex = 14)
        private String deliveryType;
        @ExcelBody(colIndex = 15)
        private String deliveryPaymentType;
        @ExcelBody(colIndex = 16)
        private String deliveryStatus;
        @ExcelBody(colIndex = 17)
        private String deliveryCompany;
        @ExcelBody(colIndex = 18)
        private String invoiceNo;
        @ExcelBody(colIndex = 19)
        private String recipientName;
        @ExcelBody(colIndex = 20)
        private String recipientUserPhone;
        @ExcelBody(colIndex = 21, bodyType = ExcelConst.Type.LONG_TEXT)
        private String recipientAddress;
        @ExcelBody(colIndex = 22, bodyType = ExcelConst.Type.LONG_TEXT)
        private String recipientAddressDetail;
        @ExcelBody(colIndex = 23, bodyType = ExcelConst.Type.LONG_TEXT)
        private String requestDetail;
        @ExcelBody(colIndex = 24, bodyType = ExcelConst.Type.LONG_TEXT)
        private String deliveryMemo;

        public void setDeliveryStatus(String deliveryStatus) {
            this.deliveryStatus = DeliveryConst.Status.of(deliveryStatus).getName();
        }

        public void setDeliveryType(String deliveryType) {
            this.deliveryType = DeliveryConst.Status.of(deliveryType).getName();
        }

        public void setRecipientName(String recipientName) {
            this.recipientName = MaskingUtil.maskingName(recipientName);
        }

        public void setRecipientUserPhone(String recipientUserPhone) {
            this.recipientUserPhone = MaskingUtil.maskingPhone(recipientUserPhone);
        }
    }

}
