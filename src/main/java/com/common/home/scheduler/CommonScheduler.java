package com.common.home.scheduler;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
@Component
public class CommonScheduler {
	
	//@Scheduled(initialDelay = 15*1000, fixedDelay=20000)//15s init -> cycle 20s
	@Scheduled(cron="0 30 9 * * *")//
	public void test() {
		
	}
}
