package com.psj.itembrowser.security.login.integrationTest;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psj.itembrowser.member.service.MemberService;
import com.psj.itembrowser.security.login.domain.dto.request.LoginRequestDTO;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = Replace.AUTO_CONFIGURED)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class LoginApiControllerIntegrationTest {
	
	private final static String EXIST_USER_EMAIL = "qkrtkdwns3410@naver.com";
	private final static String EXIST_USER_PASSWORD = "jiohioqh123!@#";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private MemberService memberService;
	
	private LoginRequestDTO validLoginRequestDTO;
	
	@BeforeEach
	void setUp() {
		validLoginRequestDTO = new LoginRequestDTO(EXIST_USER_EMAIL, EXIST_USER_PASSWORD);
	}
	
	@Test
	@Sql(scripts = {"classpath:sql/h2/member/insert_member.sql"})
	@DisplayName("로그인시 엑세스토큰과 리프레시 토큰 정상 발급")
	void whenValidLogin_thenReturnsAccessTokenAndRefreshToken() throws Exception {
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(validLoginRequestDTO)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").exists())
			.andExpect(jsonPath("$.refreshToken").exists())
			.andDo(document("login-success",
				requestFields(
					fieldWithPath("email").description("사용자의 이메일"),
					fieldWithPath("password").description("사용자의 비밀번호")
				),
				responseFields(
					fieldWithPath("accessToken").description("엑세스 토큰"),
					fieldWithPath("refreshToken").description("리프레시 토큰")
				)));
	}
}