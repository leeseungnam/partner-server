package kr.wrightbrothers.framework.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.util.PartnerKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;

@Slf4j
public class WBController {

	private final ObjectMapper mapper = new ObjectMapper();
	private final String MSG_PRE_FIX = "api.message.common.";

	/**
	 * 오류 로그 설정
	 * @param e
	 */
	public void LogStackTrace(Throwable e) {
		log.error("", e);
	}

	/**
	 * 반환 요청 데이터를 사용하지 않을 시 사용
	 *
	 * @return WBModel
	 */
	public WBModel noneDataResponse() {
		return new WBModel();
	}

	/**
	 * 정상 처리 메시지 처리
	 */
	public WBModel noneMgsResponse(MessageSourceAccessor messageSourceAccessor) {
		WBModel response = new WBModel();
		response.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(MSG_PRE_FIX + "complete"));
		return response;
	}

	/**
	 * 정상 등록 메시지 처리
	 */
	public WBModel insertMsgResponse(MessageSourceAccessor messageSourceAccessor) {
		WBModel response = new WBModel();
		response.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(MSG_PRE_FIX + "save.success"));
		return response;
	}

	/**
	 * 기본 Data Response
	 *
	 * @param obj 요청 데이터
	 * @return WBModel
	 */
	public WBModel defaultResponse(Object obj) {
		WBModel response = new WBModel();
		response.addObject(WBKey.WBModel.DefaultDataKey, obj);
//		dataLog(WBKey.WBModel.DefaultDataKey, obj);

		return response;
	}

	/**
	 * 기본 Data Response
	 *
	 * @param obj 요청 데이터
	 * @return WBModel
	 */
	public WBModel defaultInsertResponse(Object obj, MessageSourceAccessor messageSourceAccessor) {
		WBModel response = new WBModel();
		response.addObject(WBKey.WBModel.DefaultDataKey, obj);
		response.addObject(PartnerKey.WBConfig.Message.Alias, messageSourceAccessor.getMessage(MSG_PRE_FIX + "save.success"));

		return response;
	}

	/**
	 * 멀티 Data Response
	 * @param model 요청 데이터
	 * @return WBModel
	 */
	public WBModel multiResponse(WBModel model) {
//		dataLog("WBModel", model);
		return model;
	}

	private void dataLog(String name, Object obj) {
		try {
			log.debug("= Response Data. =================");
			log.debug("Json Properties Name - {}.\n{}",
					name, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
