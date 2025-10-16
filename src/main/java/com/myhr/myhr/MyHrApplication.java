package com.myhr.myhr;

import com.myhr.myhr.domain.Role;
import com.myhr.myhr.infrastructure.entity.UserAccountEntity;
import com.myhr.myhr.infrastructure.repository.UserAccountJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MyHrApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyHrApplication.class, args);
	}

	@Bean
	CommandLineRunner seedAdmin(UserAccountJpaRepository users,
								PasswordEncoder encoder,
								@Value("${spring.mail.username}") String mailUser,
								@Value("${ADMIN_PASSWORD:Admin!234}") String adminPass) {
		return args -> {
			if (!users.existsByEmail(mailUser)) {
				UserAccountEntity admin = new UserAccountEntity();
				admin.setEmail(mailUser);
				admin.setPasswordHash(encoder.encode(adminPass));
				admin.setRole(com.myhr.myhr.domain.Role.ADMIN);
				admin.setActive(true);
				users.save(admin);
				System.out.println(">>> Admin kullanıcı oluşturuldu: " + mailUser);
			}
		};
	}
}

