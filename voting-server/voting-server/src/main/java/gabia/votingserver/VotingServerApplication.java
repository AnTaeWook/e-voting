package gabia.votingserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class VotingServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotingServerApplication.class, args);
	}
}
