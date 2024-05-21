package com.ktk.workhuservice;

import com.ktk.workhuservice.config.MicrosoftConfig;
import com.ktk.workhuservice.config.TelegramConfig;
import com.ktk.workhuservice.service.UserImportService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({MicrosoftConfig.class, TelegramConfig.class})
public class WorkHuServiceApplication {

    public WorkHuServiceApplication(UserImportService userImportService) {
        userImportService.importUsersFromCsv();
    }

    public static void main(String[] args) {
        SpringApplication.run(WorkHuServiceApplication.class, args);
    }
}
