package kr.wrightbrothers.apps.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class OrderDeliverySendDto {

    @Getter
    @AllArgsConstructor
    public static class Delivery {
        private String orderNo;
        private String orderProductSeq;
        private String productCode;
        private String deliveryCompanyCode;
        private String invoiceNo;
        private String invoiceNoInputDate;
        private String updateUserId;
    }

    @Getter
    @AllArgsConstructor
    public static class Return {

    }

}
