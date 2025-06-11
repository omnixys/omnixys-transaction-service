package com.omnixys.transaction;

import org.springframework.boot.SpringApplication;

public class TestTransactionApplication {

	public static void main(String[] args) {
		SpringApplication.from(TransactionApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
