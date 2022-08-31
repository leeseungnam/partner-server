package kr.wrightbrothers.framework.util;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.framework.support.WBKey;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO;
import kr.wrightbrothers.framework.support.dto.WBSnsDTO.Header;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.UUID;

@Slf4j
public class WBAwsSns {

	private final AmazonSNS sns;
	@Value("${cloud.aws.sns.arn}")
	private String arn;
	@Value("${cloud.aws.sns.auto}")
	private Boolean use;
	@Value("${system.name}")
	private String server;



	public WBAwsSns(AmazonSNS sns) {
		this.sns = sns;
	}
	
	public PublishResult send(String snsNm, Header header, Object body) throws JsonProcessingException {
		return send(this.arn, snsNm, header, body);
	}
	
	public PublishResult send(String arn, String snsNm, Header header, Object body) throws JsonProcessingException {
		WBSnsDTO snsDto = WBSnsDTO.builder().build();
		header.setAppNm(server);
		header.setUniqueId(RandomKey.getUniqueId());
		snsDto.setHeader(header);
		snsDto.setBody(body);
		if(use) {
			PublishRequest publishRequest = new PublishRequest()
					.withTopicArn(arn + snsNm)
					.withMessage(new ObjectMapper().writeValueAsString(snsDto))
					.withMessageGroupId(WBKey.Aws.Sns.GroupId)
					.withMessageDeduplicationId(UUID.randomUUID().toString());

			log.error("===================================================");
			log.error("WB ADMIN SNS Send Publish.");
			log.error("Topic Arn, {}", publishRequest.getTopicArn());
			log.error("Massage GroupId, {}", publishRequest.getMessageGroupId());
			log.error("UUID, {}", publishRequest.getMessageDeduplicationId());
			log.error("Message, {}", publishRequest.getMessage());
			log.error("===================================================");

			return sns.publish(publishRequest);
		} else {
			return new PublishResult().withMessageId("9458703958230948");
		}
	}
}
