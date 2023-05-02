package kr.wrightbrothers.apps.product.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductListViewDto {

	/** 상품코드 */
	private String prdtCd;
	/** 상품타입 */
	private String prdtTp;
	/** 상품리스트타입 */
	private String prdtListTp;
	/** 매입신청번호,공급사,파트너코드 */
	private String purRqNo;
	/** 판매자 유저 코드 */
	private String sellUsrCd;
	/** 대카테고리 */
	private String cagyDpthOne;
	/** 중카테고리 */
	private String cagyDpthTwo;
	/** 소카테고리 */
	private String cagyDpthThr;
	/** 상품명 */
	private String prdtNm;
	/** 브랜드코드 */
	private String brdNo;
	/** 모델코드 */
	private String mdlNo;
	/** 연식 */
	private String mdlYear;
	/** 상품상태 */
	private String prdtStusCd;
	/** 파일번호 */
	private String prdtFileNo;
	/** 파일경로 */
	private String fileSrc;
	/** 보여주기여부 */
	private String dpFlg;
	/** 렌탈유무 */
	private String rntlFlg;
	/** 프레임소재 */
	private String framMtrlCd;
	/** 프레임사이즈 */
	private String framSzCd;
	/** 구동계 */
	private String dtiTpCd;
	/** 최소키 */
	private String minimHgtPn;
	/** 최대키 */
	private String maxumHgtPn;
	/** 36개월기준가 (렌탈) */
	private String thsiBseAmt;
	/** 36개월렌탈료 (렌탈) */
	private String thsiMonAmt;
	/** 상품판매채널 */
	private String prdtSellChnlCd;
	/** 상품금액 */
	private String prdtAmt;
	/** 판매가 */
	private String fnlSellAmt;
	/** 할인설정여부 */
	private String dscntFlg;
	/** 할인타입 */
	private String dscntTp;
	/** 할인율,할인액 */
	private String dscntAmt;
	/** 수량 */
	private String invtyQty;
	/** 상품상태 - C2C용 */
	private String prdtSaleStus;
	/** 상품사이즈 - C2C용 */
	private String prdtSz;
	/** 사용대상 - C2C용 */
	private String foslUse;
	/** 사용연령 */
	private String emrkAge;
	/** 지역 */
	private String addsSign;
	/** 유저 아이디 */
	private String usrId;
    
}
