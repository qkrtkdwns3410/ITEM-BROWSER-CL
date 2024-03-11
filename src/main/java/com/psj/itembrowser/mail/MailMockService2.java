package com.psj.itembrowser.mail;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MailMockService2 {
	
	private final MailMockService mailMockService;
	
	@Transactional(readOnly = true)
	public CompletableFuture<Void> dbSave() {
		// 비동기 작업을 위한 CompletableFuture 리스트를 생성합니다.
		List<CompletableFuture<Boolean>> futures = IntStream.range(0, 100)
			.mapToObj(i -> mailMockService.sendMockMail())
			.collect(Collectors.toList());
		
		// 모든 CompletableFuture가 완료될 때까지 기다립니다.
		return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
			.thenApply(v -> {
				// 모든 작업이 완료된 후, 성공 및 실패 카운트를 계산합니다.
				long successCount = futures.stream()
					.filter(CompletableFuture::join) // join()을 사용하여 결과를 가져옵니다.
					.count();
				long failCount = futures.size() - successCount;
				
				// 결과를 로깅합니다.
				log.info("성공 횟수 : {}", successCount);
				log.info("실패 횟수 : {}", failCount);
				
				log.info("메일 보내기 작업이 성공한 친구들만 별도 db에 기록작업을 수행한다.");
				return null; // Void 타입의 CompletableFuture를 반환하기 위해 null을 사용합니다.
			});
	}
}