package kr.wrightbrothers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.wrightbrothers.apps.common.config.security.jwt.JwtTokenProvider;
import kr.wrightbrothers.apps.sign.dto.UserDetailDto;
import kr.wrightbrothers.apps.sign.dto.UserPrincipal;
import kr.wrightbrothers.apps.user.dto.UserAuthDto;
import kr.wrightbrothers.framework.support.dao.WBCommonDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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

	public UserDetailDto userDetailDto;
	public final String AUTH_HEADER = "Authorization";
	public String JWT_TOKEN = "";

	public OperationRequestPreprocessor requestDocument() {
		return preprocessRequest(
				removeHeaders("Content-Length", "Accept"),
				modifyUris()
						.scheme("https")
						.host("partner.wrightbrothers.kr")
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

	@BeforeEach
	void setUp() {
		SecurityContext context = SecurityContextHolder.getContext();

		userDetailDto = UserDetailDto.builder()
				.userId("test@wrightbrothers.kr")
				.userPwd("")
				.userAuth(UserAuthDto.builder()
								.partnerCode("PT0000001")
						//.partnerCode("35")
								.authCode("ROLE_ADMIN")
						.build())
				.build();

		UserPrincipal principal = new UserPrincipal("test@wrightbrothers.kr", "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")), UserAuthDto.builder().authCode("ROLE_ADMIN").partnerCode("PT0000001").build());
		context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, "", List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		JWT_TOKEN = jwtTokenProvider.generateAccessToken(authentication);
	}
}
