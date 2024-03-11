package com.psj.itembrowser.mail;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MailMockService {
	
	@Async("mailSenderExecutor")
	public CompletableFuture<Boolean> sendMockMail() {
		log.info(Thread.currentThread().getName() + " - 메일을 보내는 중입니다.");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// CompletableFuture.completedFuture를 사용하여 즉시 완료된 Future를 반환합니다.
		return CompletableFuture.completedFuture(true);
	}
}