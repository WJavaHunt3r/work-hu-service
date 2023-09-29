package com.ktk.workhuservice;

import com.ktk.workhuservice.service.UserImportService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkHuServiceApplication {

	public WorkHuServiceApplication(UserImportService userImportService) {
		userImportService.importUsersFromCsv();
	}

	public static void main(String[] args) {
		SpringApplication.run(WorkHuServiceApplication.class, args);
	}
}
