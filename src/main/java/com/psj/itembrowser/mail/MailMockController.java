package com.psj.itembrowser.mail;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MailMockController {
	
	private final MailMockService2 mailMockService;
	
	@GetMapping("/mail")
	public void mail() {
		long start = System.currentTimeMillis();
		
		mailMockService.dbSave().thenRun(() -> {
			long end = System.currentTimeMillis();
			
			log.info("메일 보내기 작업이 완료되었습니다. 소요시간 : {}", end - start);
		});
	}
}