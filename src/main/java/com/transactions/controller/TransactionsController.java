package com.transactions.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transactions.entity.SummaryOfTransactions;
import com.transactions.entity.Transaction;
import com.transactions.service.TransactionsService;

@RestController
@RequestMapping("/v1/transaction-summary")
public class TransactionsController {

	@Autowired
	TransactionsService transactionsSevice;

	@PostMapping(path = "/transaction", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> addTransaction(@RequestBody @Valid Transaction transaction) {
		boolean created = transactionsSevice.addTransaction(transaction);

		if (!created) {
			return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
		} else {

			return new ResponseEntity<>("", HttpStatus.CREATED);
		}

	}

	@GetMapping(path = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SummaryOfTransactions> getLastMinuteTrasactionsSummary() {
		SummaryOfTransactions summary = transactionsSevice.getSummary();
		return new ResponseEntity<>(summary, HttpStatus.OK);

	}

}
