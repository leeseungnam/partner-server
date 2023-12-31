package kr.wrightbrothers.apps.common.constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Notification {
    // 파트너 회원가입 완료 즉시
    SIGN_UP("KA01TP221130015224471v82jM4J1XOd", "ATA",
            "[라이트브라더스] 회원가입 완료 안내\n" +
                    "#{고객명}님, 파트너센터 회원가입이 완료되었습니다. 판매를 위해서는 스토어 등록이 필요합니다. \n" +
                    "스토어 등록은 사업자 등록이 된 사업자만 가능하며, 스토어 등록을 진행한 계정은 해당 스토어의 관리자로 등록됩니다.\n" +
                    "이후, 관리자 변경은 불가하니 사업자 대표자 명의로 등록 부탁드립니다.\n" +
                    "\n" +
                    "◼︎입점 시 필요서류\n" +
                    "사업자 등록증, 통장 사본\n" +
                    "\n" +
                    "◼︎고객센터 \n" +
                    "문의 : 02-797-0521\n" +
                    "\n" +
                    "감사합니다.", ""),
    // 파트너 비밀번호 변경 후 즉시
    CHANGE_PASSWORD("KA01TP221125053653236zVTgLJFEd2o", "ATA",
            "[라이트브라더스] 비밀번호 변경 안내\n" +
                    "파트너센터 계정 비밀번호가 변경되었습니다.\n" +
                    "\n" +
                    "만약 비밀번호를 변경한 적이 없다면, 파트너센터 고객센터로 문의하시기 바랍니다.\n" +
                    "파트너 고객센터 : 02-797-0521", ""),
    // 스토어 등록 완료 즉시
    REGISTER_STORE("KA01TP221206011337903N5wdR7lNdYz", "ATA",
            "[라이트브라더스] 스토어 등록 완료 안내\n" +
                    "#{스토어명}님, 라이트브라더스 스토어 등록이 완료되었습니다.\n" +
                    "\n" +
                    "파트너센터 입점 담당자가 곧 연락을 드릴 예정이며, 계약까지는 평균 영업일 기준 약 1~2일이 소요됩니다.\n" +
                    "연락 전까지 입점에 필요한 계약 서류를 준비해 주세요.\n" +
                    "\n" +
                    "◼︎ 입점 필요서류\n" +
                    "사업자 등록증, 통장 사본", ""),
    // 스토어 심사 승인 완료 즉시
    APPROVAL_STORE("KA01TP2211290655129106EK1PU6k4AL", "ATA",
            "[라이트브라더스] 스토어 심사 완료 안내\n" +
                    "{스토어명}님, 라이트브라더스 입점을 축하합니다!\n" +
                    "\n" +
                    "상품을 등록하여, 라이트브라더스에서 판매해 보세요.\n" +
                    "판매자님의 입점 계약은 아래와 같습니다.\n" +
                    "\n" +
                    "◼︎ 입점 계약 기간\n" +
                    "#{계약시작일} ~ #{계약종료일}\n" +
                    "* 별도의 판매 조건 변경 요청이 없으면, 계약기간은 자동으로 연장 갱신됩니다.\n" +
                    "\n" +
                    "◼︎ 판매 수수료\n" +
                    "- 자전거 : #{수수료}%\n" +
                    "- 의류/용품 : #{수수료}%\n" +
                    "- 부품 : #{수수료}%\n" +
                    "\n" +
                    "\n" +
                    "◼︎ 카드 결제와 통장 결제 수수료는 동일합니다.\n" +
                    "\n" +
                    "※상품 등록 시 주의 사항※\n" +
                    "직거래 유도 행위(직거래 요청, 연락처 기재 등)를 할 경우 판매 불가합니다. 모조품을 판매할 경우, 법적인 처벌을 받을 수 있습니다.\n" +
                    "\n" +
                    "자세한 계약 정보를 확인하시려면, 파트너센터 > 판매자 정보를 확인해 주세요.", ""),
    // 스토어 심사 반려 즉시
    REJECT_STORE("KA01TP221130023847390TnrVNI1gdNu", "ATA",
            "[라이트브라더스] 스토어 심사 반려 안내\n" +
                    "{스토어명}님, 스토어 심사 반려되었습니다.\n" +
                    "\n" +
                    "스토어 정보를 재심사 하시려면, 심사 반려 사유를 확인하여 수정해주세요.\n" +
                    "심사 통과되어 라이트브라더스와 함께 할 수 있기를 희망합니다.\n" +
                    "\n" +
                    "왜 심사반려 되나요? \n" +
                    "◼︎ 주요 심사 반려 원인 Best 5\n" +
                    "1. 입점 담당자와 연락이 안되요.\n" +
                    "2. 스토어 명이 다른 판매자와 비슷해요.\n" +
                    "3. 사업자등록증과 입력한 정보가 달라요.\n" +
                    "4. 고객센터 전화번호가 연결되지 않아요.\n" +
                    "5. 세금계산서 이메일 주소가 잘못되었어요.", ""),
    // 계약 완료 즉시
    CONTRACT_COMPLETE("KA01TP221124102236480VPRUGOqjmpn", "ATA",
            "[라이트브라더스] 파트너센터 계약 안내\n" +
                    "{스토어명}님, 라이트브라더스 파트너센터 입점 계약 완료되었습니다.\n" +
                    "\n" +
                    "{스토어명}님의 입점 계약은 아래와 같습니다.\n" +
                    "\n" +
                    "◼︎ 입점 계약 기간\n" +
                    "{계약시작일} ~ {계약종료일}\n" +
                    "\n" +
                    "◼︎ 주의사항\n" +
                    "- 최초 입점 계약 시, 해당년 말일까지 계약기간이 설정 됩니다.\n" +
                    "- 이후 별도의 변경 요청이 없으면 1년 단위로 자동 계약 연장 됩니다. (별도 계약서 작성 없음)\n" +
                    "- 계약 관련한 판매 조건 변경 요청이 있다면, 계약종료일 기준 30일 이전까지 입점 담당자에게 알려주세요.\n" +
                    "\n" +
                    "자세한 계약 정보를 확인하시려면, 파트너센터 판매자 정보를 확인해 주세요.", ""),
    // 스토어 갱신 계약일 오전 9시
    CONTRACT_RENEWAL("KA01TP2211250534541908RaIuahsSFR", "ATA",
            "[라이트브라더스] 파트너센터 계약 갱신 안내\n" +
                    "{스토어명}님, 라이트브라더스 파트너센터 입점 계약 갱신되었습니다.\n" +
                    "\n" +
                    "◼︎ 입점 갱신 계약 기간\n" +
                    "{계약시작일} ~ {계약종료일}\n" +
                    "\n" +
                    "◼︎ 주의사항\n" +
                    "- 자동 갱신으로 인한 계약 일자는 매년 1월 1일부터 12월 31일까지입니다.\n" +
                    "- 이후 별도의 변경 요청이 없으면 1년 단위로 자동 계약 연장됩니다. (별도 계약서 작성 없음)\n" +
                    "- 계약 관련한 판매 조건 변경 요청이 있다면, 계약 종료일 기준 30일 이전까지 라이트브라더스 입점 담당자에게 알려주세요.\n" +
                    "\n" +
                    "자세한 계약 정보를 확인하시려면, 파트너센터 판매자 정보를 확인해 주세요.", ""),
    // 스토어 계약 종료일 오전 9시
    CONTRACT_END("KA01TP221125061528124QKziRUpfQ0Q", "ATA",
            "[라이트브라더스] 파트너센터 계약 종료 안내\n" +
                    "#{스토어명}님, 오늘을 마지막으로 라이트브라더스 파트너센터 입점 계약이 종료될 예정입니다.\n" +
                    "그동안 라이트브라더스와 함께해 주셔서 진심으로 감사드립니다.\n" +
                    "\n" +
                    "#{스토어명}님께서 판매 중인 모든 상품은 판매중지, 미노출 처리되며, \n" +
                    "계약 종료 시점(금일 23시 59분) 이전까지 판매된 상품은 구매확정된 주문 건에 한해 기존 정산 방식과 동일하게 지급될 예정입니다.\n" +
                    "파트너센터 모든 메뉴에 대한 조회는 계속 가능합니다.\n" +
                    "\n" +
                    "계약을 다시 진행하려면 파트너 고객센터로 연락해 주세요.", ""),
    // 스토어 계약 종료일 60일전 오전 9시
    CONTRACT_60DAY_PRIOR_END("KA01TP221125061710667Q68wz1wg2ol", "ATA",
            "[라이트브라더스] 계약 갱신 안내 \n" +
                    "안녕하세요 #{스토어명}님, \n" +
                    "\n" +
                    "그동안 라이트브라더스와 함께해 주셔서 감사드립니다.\n" +
                    "라이트브라더스 판매자 계약이 60일 뒤에 만료되어 안내드립니다.\n" +
                    "\n" +
                    "◼︎ 입점 계약 기간\n" +
                    "#{계약시작일} ~ #{계약종료일}\n" +
                    "\n" +
                    "◼︎ 주의사항\n" +
                    "- 최초 입점 계약 시, 해당 년 말일까지 계약기간이 설정됩니다.\n" +
                    "- 이후 별도의 변경 요청이 없으면 1년 단위로 자동 계약 연장됩니다. (별도 계약서 작성 없음)\n" +
                    "- 계약 관련한 판매 조건 변경 요청이 있다면, 계약 종료일 기준 30일 이전까지 라이트브라더스 입점 담당자에게 알려주세요.\n" +
                    "\n" +
                    "계약 갱신 관련하여 곧 담당자가 연락드릴 예정입니다.", ""),
    // 스토어 계약 종료일 40일전 오전 9시
    CONTRACT_40DAY_PRIOR_END("KA01TP221125061850966Xj7xzHh8T4V", "ATA",
            "[라이트브라더스] 계약 갱신 안내 \n" +
                    "안녕하세요 #{스토어명}님,\n" +
                    "\n" +
                    "그동안 라이트브라더스와 함께해 주셔서 감사드립니다.\n" +
                    "라이트브라더스 판매자 계약이 40일 뒤에 만료되어 안내드립니다.\n" +
                    "\n" +
                    "◼︎ 입점 계약 기간\n" +
                    "#{계약시작일} ~ #{계약종료일}\n" +
                    "\n" +
                    "◼︎ 주의사항\n" +
                    "- 최초 입점 계약 시, 해당 년 말일까지 계약기간이 설정됩니다.\n" +
                    "- 이후 별도의 변경 요청이 없으면 1년 단위로 자동 계약 연장됩니다. (별도 계약서 작성 없음)\n" +
                    "- 계약 관련한 판매 조건 변경 요청이 있다면, 계약 종료일 기준 30일 이전까지 라이트브라더스 입점 담당자에게 알려주세요.\n" +
                    "\n" +
                    "계약 갱신 관련하여 곧 담당자가 연락드릴 예정입니다.", ""),
    // 스토어 운영 위반 처리 즉시
    VIOLATION("KA01TP221125062005405r73xWwDmVTw", "ATA",
            "[라이트브라더스] 파트너센터 운영 위반 안내\n" +
                    "#{스토어명}님, 라이트브라더스 파트너센터 판매 운영 정책 위반되어 안내드립니다.\n" +
                    "\n" +
                    "위반한 사항이 수정되기 전까지는 파트너센터 스토어 운영 중지 처리됩니다.\n" +
                    "스토어를 계속 운영하시려면 위반 사항에 대한 조치 부탁드립니다.\n" +
                    "자세한 운영 위반 사항에 대해서는 '파트너센터 > 판매자 정보'에서 확인 부탁드립니다.\n" +
                    "\n" +
                    "이 밖에 궁금하신 사항은 라이트브라더스 파트너 고객센터로 문의 바랍니다.", ""),
    // 스토어 정산대금 확정일 오전 9시
    SETTLEMENT("P_AUTH_0012", "ATA",
            "[라이트브라더스] 정산 안내\n" +
                    "#{스토어명}님, #{정산월} 정산 대금이 확정되어 안내드립니다.\n" +
                    "자세한 정산 내용은 정산관리 > 정산내역 화면에서 확인부탁드립니다.\n" +
                    "\n" +
                    "◼︎ 정산금액 산정 기간\n" +
                    "전월 1일 ~ 31일\n" +
                    "\n" +
                    "◼︎ 주의사항\n" +
                    "- 정산 금액은 구매확정일 기준으로 산정되며, 매월 1일~말일 동안 구매확정된 건에 대한 금액입니다.\n" +
                    "- 구매 확정은 고객이 직접 처리하거나, 배송 완료 후 7일이 지나면 자동 구매확정 처리됩니다.\n" +
                    "- 정산대금은 결제금액에서 판매수수료를 차감한 금액이 지급됩니다.", ""),
    REQUEST_RETURN_ORDER("KA01TP221125062832333Z3i1wwr1KgR", "ATA",
            "[라이트브라더스] 반품 요청 안내\n" +
                    "안녕하세요 #{스토어명}님,\n" +
                    "고객님께서 반품 요청하였습니다.\n" +
                    "\n" +
                    "'파트너센터 > 반품관리'에서 반품 요청 주문 건을 확인하시어 처리 부탁드립니다.\n" +
                    "다른 문의 사항 있으면, 파트너 고객센터로 문의하세요.", ""),
    CONFIRM_RETURN_ORDER("KA01TP230103100346231IvgjdzrxpTP", "ATA","", ""),
    DELIVERY_START("KA01TP221229160607874r65H47dcQMP", "ATA","", ""),
    REQUEST_CANCEL_ORDER("KA01TP221221071445546IUnUstCSVNm", "ATA",
            "[라이트브라더스] 주문 취소 요청 안내\n" +
                    "안녕하세요 #{스토어명}님,\n" +
                    "주문하신 고객님께서 주문취소를 요청하였습니다.\n" +
                    "\n" +
                    "'파트너센터 > 주문관리'에서 취소 요청 주문 건을 확인하시어 처리 부탁드립니다.\n" +
                    "다른 문의 사항 있으면, 파트너 고객센터로 문의 주세요.", ""),
    AUTH_PHONE("", "SMS", "[라이트브라더스] 인증번호 #{authCode} 를 입력해 주세요.", "휴대폰번호 인증"),
    NULL("","", "", "");

    private final String messageId;
    private final String messageType;
    private final String messageText;
    private final String desc;

    Notification(String messageId, String messageType, String messageText, String desc) {
        this.messageId = messageId;
        this.messageType = messageType;
        this.messageText = messageText;
        this.desc = desc;
    }
}
