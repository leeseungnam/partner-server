package kr.wrightbrothers.apps.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryUpdateDto {
    /** 주문 번호 */
    @NotBlank(message = "주문 번호")
    private String orderNo;

    /** 주문 상품 SEQ Array */
    @NotNull(message = "주문 상품 SEQ")
    private Integer[] orderProductSeqArray;

    /** 수령자 명 */
    @NotBlank(message = "수령자명")
    private String recipientName;

    /** 수령자 연락처 */
    @NotBlank(message = "수령자 연락처")
    private String recipientPhone;

    /** 수령자 우편번호 */
    @NotBlank(message = "수령자 우편번호")
    private String recipientAddressZipCode;

    /** 수령자 주소 */
    @NotBlank(message = "수령자 주소")
    private String recipientAddress;

    /** 수령자 상세주소 */
    private String recipientAddressDetail;

    /** 파트너 코드 */
    private String partnerCode;

    /** 사용자 아이디 */
    @JsonIgnore
    private String userId;

    public void setAopPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public void setAopUserId(String userId) {
        this.userId = userId;
    }
}
