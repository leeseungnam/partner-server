package kr.wrightbrothers.framework.support;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO.Ack;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Data
@SuppressWarnings("unchecked")
public class WBSQS {

	private Map<String, String> jsonMap = new HashMap<String, String>();
	private final String messageKey = "Message";
	private final String messageID = "MessageId";
	private ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	private String queueName;
	@Value("${system.name}")
	private String server;
	@Value("${cloud.aws.credentials.accessKey}")
	private String awsAccessKey;
	@Value("${cloud.aws.credentials.secretKey}")
	private String awsSecretKey;
	@Value("${cloud.aws.region.static}")
	private String region;
	@Value("${cloud.aws.sns.ack}")
	private String ack;
	@Value("${cloud.aws.sns.arn}")
	private String arn;
	
	/**
	 * 
	 * @param message
	 */
	public void initMessage(String message, String queueName) {
		try {
			this.queueName = queueName;
			log.debug("message : \n{}", message);
			this.jsonMap = mapper.readValue(message, HashMap.class);
		} catch (JsonProcessingException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	/**
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public <T> T getSqsMessage(Class<?> clazz) {
		return getSqsMessage(this.messageKey, clazz);
	}
	
	/**
	 * 메세지 아이디
	 * @return
	 */
	public String getSqsMessageID() {
		return jsonMap.get(this.messageID);
	}
	
	/**
	 * 
	 * @param <T>
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> T getSqsMessage(String key, Class<?> clazz) {
		try {
			return (T) mapper.readValue(jsonMap.get(key), clazz);
		} catch (JsonProcessingException e) {
			log.error(ExceptionUtils.getStackTrace(e));
		}
		return null;
	}
	
	/**
	 * Body 데이터 바인딩
	 * @param <T>
	 * @param body
	 * @param clazz
	 * @return
	 */
	public <T> T getBody(Object body, Class<?> clazz) {
		return (T) mapper.convertValue(body, clazz);
	}
	
	/**
	 * 처리상태 전송
	 * @param wbMsgDto
	 */
	public void ackMessage(WBSnsDTO wbMsgDto) {
		ackMessage(wbMsgDto, WBKey.Success, null);
	}
	
	/**
	 * 
	 * @param wbMsgDto
	 * @param e
	 */
	public void ackMessage(WBSnsDTO wbMsgDto, Exception e) {
		ackMessage(wbMsgDto, WBKey.Error, e);
	}
	
	/**
	 * 처리상태 전송
	 * @param stusCd
	 */
	public void ackMessage(WBSnsDTO wbMsgDto, String stusCd, Exception e) {
		AmazonSNS sns = AmazonSNSClientBuilder.standard().withRegion(region)
		.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey)))
		.build();
		String errorMsg = null;
		if(e != null) errorMsg = ExceptionUtils.getStackTrace(e);
		wbMsgDto.setAck(Ack.builder().appNm(server).msgId(getSqsMessageID()).stusCd(stusCd).errMsg(errorMsg).uniqueId(wbMsgDto.getHeader().getUniqueId()).queueName(queueName).build());
		PublishResult res = null;
		try {
			res = sns.publish(new PublishRequest()
					.withTopicArn(this.arn + this.ack)
					.withMessage(new ObjectMapper().writeValueAsString(wbMsgDto))
					.withMessageGroupId(WBKey.Aws.Sns.GroupId)
					.withMessageDeduplicationId(UUID.randomUUID().toString()));
			log.debug("ackMessageID : " + res.getMessageId());
		} catch (JsonProcessingException e1) {
			log.error(ExceptionUtils.getStackTrace(e1));
		}
	}
}
