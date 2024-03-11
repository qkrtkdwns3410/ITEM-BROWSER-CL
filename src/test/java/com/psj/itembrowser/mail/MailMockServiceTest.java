package com.psj.itembrowser.mail;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootTest
@EnableAsync
class MailMockServiceTest {
    
    @Autowired
    private MailMockService mailMockService;
    
    @Test
    void dbSave() {
        long startTime = System.currentTimeMillis();
        
        mailMockService.dbSave();
        
        long endTime = System.currentTimeMillis();
        
        Assertions.assertThat(endTime - startTime).isLessThan(1000)
                .as("메일 보내기 작업이 성공한 친구들만 별도 db에 기록작업을 수행한다.");
    }
}