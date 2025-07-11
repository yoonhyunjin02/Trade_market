package com.owl.trade_market;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradeMarketApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

		System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
		System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("google.maps.api.key", dotenv.get("GOOGLE_MAPS_API_KEY"));

		SpringApplication.run(TradeMarketApplication.class, args);
	}
}