package kr.wrightbrothers.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.BaseControllerTests;
import kr.wrightbrothers.apps.common.util.AwsSesUtil;
import kr.wrightbrothers.apps.email.dto.SingleEmailDto;
import kr.wrightbrothers.apps.email.service.EmailService;
import kr.wrightbrothers.apps.product.dto.ProductUpdateDto;
import kr.wrightbrothers.apps.queue.service.ProductQueueService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


class AwsTest extends BaseControllerTests {

    @Autowired
    private AwsSesUtil awsSesUtil;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ProductQueueService productQueueService;

    @Test
    @DisplayName("SES 메일발송 테스트")
    void SesSendTest() throws Exception {

//        JSONParser jsonParser = new JSONParser();
//        JSONObject jsonObject = (JSONObject) jsonParser.parse("{\"ProductBasicSpecification\":{\"MaximumHeightPerson\":\"175\",\"FrameSizeCode\":\"S\",\"BrakeTypeCode\":\"T02\",\"MinimumHeightPerson\":\"168\",\"FrameMaterialCode\":\"F03\",\"SalesCategoryCode\":\"S01\",\"BikeTare\":\"9.2\",\"WheelSizeCode\":\"WH1\",\"SuspensionTypeCode\":\"S04\",\"PurposeThemeCode\":\"T03\",\"DrivetrainTypeCode\":\"D06\"},\"ProductDelivery\":[{\"ProductDeliveryCode\":\"D01\"}],\"ProductOptin\":[{\"InventoryQuantity\":\"1\",\"metadata\":\"옵션명-옵션항목\",\"OptionSurcharge\":\"10000\",\"OptionValue\":\"옵션항목\",\"OptionSequence\":\"1\",\"OptionName\":\"옵션명\"}],\"ProductInformationBulletin\":{\"CategoryCode\":\"123\",\"ProductAttribute2\":\"S\",\"ProductAttribute1\":\"Propel Advanced Disc 2\",\"ProductAttribute4\":\"1\",\"ProductAttribute3\":\"9.2\",\"ProductAttribute10\":\"4\",\"ProductAttribute11\":\"02-000-0000\",\"ProductAttribute12\":null,\"ProductAttribute9\":\"3\",\"ProductAttribute6\":\"2022\",\"ProductAttribute5\":\"2\",\"ProductAttribute8\":\"(주)라이트브라더스\",\"ProductAttribute7\":\"05\"},\"ProductEmbarkAge\":[{\"EmbarkAge\":\"A01\"}],\"ProductGuideanceComment\":{\"ProductGuideanceCommentOne\":\"상품 상세 설명\",\"ProductGuideanceCommentFour\":\"배송 안내 사항 ..............................\",\"MerchandiserComment\":null,\"ProductGuideanceCommentThree\":\"QA\",\"ProductGuideanceCommentTwo\":\"상품 안내 사항 ..............................\"},\"ProductFrameSpecificationInformation\":{\"HorizontalTopLength\":\"\",\"FrameReach\":\"\",\"FileList\":[],\"FrameStackable\":\"\",\"SeatTubeLength\":\"\",\"SeatTubeAngle\":\"\"},\"ProductMain\":{\"YoutubeUrl\":\"\",\"ProductSerialNumber\":null,\"ProductName\":\"Propel Advanced Disc 3\",\"CategoryDepthThree\":\"51794\",\"CreateDate\":\"2022-10-18 13:38:00\",\"AdministrativeCountyCode\":null,\"ProductFileNo\":\"202210141708285281059636291439625908\",\"CategoryDepthTwo\":\"BA001\",\"BrandName\":\"자이언트\",\"FrameSizeCode\":\"S\",\"UpdateDate\":\"2022-10-18 14:53:44\",\"BrandNumber\":\"72\",\"UpdateUserId\":\"yumsksk@wrightbrothers.kr\",\"ModelNumber\":\"958F7839DB\",\"ModelName\":null,\"RentalFlag\":\"N\",\"ProductCode\":\"PARBFEWHXNB\",\"FileList\":[],\"ProductType\":\"P05\",\"CategoryDepthOne\":\"B0001\",\"CreateUserId\":\"wbtest\",\"UseFlag\":\"Y\",\"PrductBarcode\":\"1111\",\"AsIsIndex\":\"0\",\"PurchaseRequestNumber\":\"PT0000001\",\"ModelYear\":\"2022\",\"AdministrativeDivisionCode\":null},\"ProductRental\":null,\"ProductSearchDefault\":[],\"ProductDeliveryDetail\":{\"ExchangeCharge\":100000,\"TermsFreeCharge\":3000,\"ReturnAddressDetail\":\"주경빌딩 1층\",\"ChargeBase\":3000,\"UnstoringAddress\":\"서울특별시 강남구 강남대로 154길 37\",\"SurchargeIsolated\":0,\"DeliveryBundleFlag\":\"N\",\"SurchargeJejudo\":0,\"ReturnDeliveryCompanyCode\":\"cjgls\",\"ReturnAddress\":\"서울특별시 강남구 강남대로 154길 37\",\"SurchargeFlag\":\"N\",\"UnstoringZipCode\":\"06035\",\"ChargeType\":\"CT2\",\"PaymentType\":\"P02\",\"AreaCode\":null,\"ReturnCharge\":1000000,\"UnstoringAddressDetail\":\"주경빌딩 2층\",\"ReturnZipCode\":\"06035\"},\"ProductSellChannel\":[{\"ProductSellChannelCode\":\"1\"}],\"ProductSellInformation\":{\"InventoryQuantity\":\"1\",\"ProductAmount\":\"3900000\",\"PurchaseAmount\":\"3900000\",\"ChangeFlag\":\"N\",\"ProductSellStartDate\":\"2022-10-18 13:38:00\",\"DiscountType\":\"2\",\"ProductStatusCode\":\"S01\",\"DiscountFlag\":\"N\",\"DisplayFlag\":\"N\",\"FinalSellAmount\":\"3900000\",\"DiscountAmount\":\"0\"}}");
//
//        productQueueService.updateProductSqsData(new org.json.JSONObject(new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).writeValueAsString(jsonObject)));


//        emailService.singleSendEmail(SingleEmailDto.ReqBody.builder()
//                        .authCode("3333")
//                        .emailType("1")
//                        .userId("chals@wrightbrothers.kr")
//                .build());


//        String subject = "메일발송 테스트";
//
//        Context context = new Context();
//        context.setVariable("code", 7777);
//
//        awsSesUtil.singleSend(
//                subject,
//                "userMailAuth",
//                context,
//                "chals@wrightbrothers.kr");
    }

}
