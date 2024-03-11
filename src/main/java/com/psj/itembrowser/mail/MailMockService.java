package com.psj.itembrowser.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@Slf4j
@Transactional(readOnly = true)
public class MailMockService {
    
    @Transactional(readOnly = false)
    public void dbSave() {
        long successCount = 0L;
        long failCount = 0L;
        
        for (int i = 0; i < 100; i++) {
            try {
                Future<Boolean> result = sendMockMail();
                if (result.get()) {
                    successCount++;
                    log.info("메일을 보내는데 성공했습니다.");
                } else {
                    failCount++;
                    log.info("메일을 보내는데 실패했습니다.");
                }
            } catch (InterruptedException | ExecutionException e) {
                log.error("메일을 보내는데 실패했습니다. - inner exception", e);
            }
        }
        
        log.info("성공 횟수 : {}", successCount);
        log.info("실패 횟수 : {}", failCount);
        
        log.info("메일 보내기 작업이 성공한 친구들만 별도 db에 기록작업을 수행한다.");
    }
    
    @Async
    public Future<Boolean> sendMockMail() {
        try {
            Thread.sleep(200);
            
            System.out.println(Thread.currentThread().getName() + "\t" + "메일을 보냅니다.");
            
            Thread.sleep(200);
            
            return AsyncResult.forValue(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return AsyncResult.forValue(false);
    }
}