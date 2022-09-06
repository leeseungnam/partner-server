package kr.wrightbrothers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import kr.wrightbrothers.framework.util.JwtToken;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
public class BaseControllerTests {

	@Autowired
	public MockMvc mockMvc;
	@Autowired
	public ObjectMapper objectMapper;
	@Autowired
	public WBCommonDao dao;
	@Autowired
	public JwtTokenProvider jwtTokenProvider;

	public String JWT_TOKEN = "";

	public OperationRequestPreprocessor requestDocument() {
		return preprocessRequest(
				removeHeaders("Content-Length", "Accept"),
				modifyUris()
						.scheme("https")
						.host("admin.wrightbrothers.kr")
						.removePort(),
				prettyPrint()
		);
	}

	public OperationResponsePreprocessor responseDocument() {
		return preprocessResponse(
				removeHeaders("Vary", "Content-Language", "Cache-Control", "Content-Length"),
				prettyPrint()
		);
	}

}
