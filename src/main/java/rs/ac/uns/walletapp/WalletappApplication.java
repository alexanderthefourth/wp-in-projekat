package rs.ac.uns.walletapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WalletappApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletappApplication.class, args);
	}

}
