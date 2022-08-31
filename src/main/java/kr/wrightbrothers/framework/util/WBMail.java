package kr.wrightbrothers.framework.util;//package kr.wrightbrothers.framework.util;
//
//import java.util.HashMap;
//import java.util.LinkedList;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.thymeleaf.context.Context;
//import org.thymeleaf.spring5.SpringTemplateEngine;
//
//import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
//import com.amazonaws.services.simpleemail.model.Body;
//import com.amazonaws.services.simpleemail.model.Content;
//import com.amazonaws.services.simpleemail.model.Destination;
//import com.amazonaws.services.simpleemail.model.Message;
//import com.amazonaws.services.simpleemail.model.SendEmailRequest;
//import com.amazonaws.services.simpleemail.model.SendEmailResult;
//
//import kr.wrightbrothers.framework.support.WBKey;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Data
//public class WBMail {
//
//	private String from = WBKey.InfoMailAddr;
//    private LinkedList<String> recipients = new LinkedList<>();
//    private HashMap<String, String> values = new HashMap<String, String>();
//    private String subject;
//    private String content;
//    private String templateName;
//    
//	@Autowired
//	private AmazonSimpleEmailServiceAsync amazonSimpleEmailServiceAsync;
//	
//	@Autowired 
//	private SpringTemplateEngine templateEngine;
//
//	/**
//	 * 전송 메일 주소
//	 * @param email
//	 */
//	public void addTo(String email){
//        this.recipients.add(email);
//    }
//	
//	/**
//	 * 템플릿에 사용중인 바인딩 데이터
//	 * @param key
//	 * @param value
//	 */
//	public void addContext(String key, String value) {
//		this.values.put(key, value);
//	}
//	
//	/**
//	 * 메일 전송
//	 */
//	public void send() {
//		Destination destination = new Destination().withToAddresses(getRecipients());
//		Content subjectContent = new Content(getSubject());
//        Content htmlContent = new Content().withData(templateHtml());
//        Body msgBody = new Body().withHtml(htmlContent);
//        Message message = new Message(subjectContent, msgBody);
//        SendEmailRequest request = new SendEmailRequest(getFrom(), destination, message);
//        SendEmailResult result = amazonSimpleEmailServiceAsync.sendEmail(request);
//		log.debug(result.getMessageId());
//	}
//	
//	/**
//	 * 템플릿 파일을 이용해 메일 내용 생성
//	 * @return
//	 */
//	private String templateHtml() {
//		Context context = new Context();
//		if(!getValues().isEmpty())
//			getValues().forEach((key, value)->{ context.setVariable(key, value); });
//		
//		return templateEngine.process(getTemplateName(), context);
//	}
//}
