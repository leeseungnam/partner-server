package kr.wrightbrothers.framework.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WBController {

	private final ObjectMapper mapper = new ObjectMapper();
	
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
