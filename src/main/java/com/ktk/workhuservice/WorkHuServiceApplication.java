package com.ktk.workhuservice;

import com.ktk.workhuservice.data.Season;
import com.ktk.workhuservice.data.Team;
import com.ktk.workhuservice.data.Transaction;
import com.ktk.workhuservice.data.User;
import com.ktk.workhuservice.service.UserImportService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@SpringBootApplication
public class WorkHuServiceApplication implements RepositoryRestConfigurer {

	public WorkHuServiceApplication(UserImportService userImportService) {
		userImportService.importUsersFromCsv();
	}

	public static void main(String[] args) {
		SpringApplication.run(WorkHuServiceApplication.class, args);
	}

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
		config.exposeIdsFor(Team.class, Transaction.class, Season.class, User.class);
	}
}
