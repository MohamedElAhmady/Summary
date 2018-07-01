package com.transactions.controller;

import static java.lang.System.currentTimeMillis;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.transactions.entity.SummaryOfTransactions;
import com.transactions.entity.Transaction;
import com.transactions.service.TransactionsService;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionsController.class)
@ContextConfiguration(classes = TransactionsController.class)
public class TransactionsControllerTest {

	final static Logger logger = LoggerFactory.getLogger(TransactionsControllerTest.class);

	@Autowired
	private MockMvc mvc;

	@MockBean
	private TransactionsService transactionsService;

	@Test
	public void testTransactionWithinTheLastMinute() throws Exception {
		logger.debug("test add transaction within last minute");
		Mockito.when(transactionsService.addTransaction(Mockito.any(Transaction.class))).thenReturn(true);
		String json = "{\r\n" + "	\"amount\":1.3,\r\n" + "	\"timestamp\":" + currentTimeMillis() + "\n" + "}";

		mvc.perform(post("/v1/transaction-summary/transactions").accept(MediaType.APPLICATION_JSON).content(json)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

	}

	@Test
	public void testTransactionOutTheLastMinute() throws Exception {
		logger.debug("test add transaction not within last minute");
		Mockito.when(transactionsService.addTransaction(Mockito.any(Transaction.class))).thenReturn(false);
		String json = "{\r\n" + "	\"amount\":13.3,\r\n" + "	\"timestamp\":1478192204000\n" + "}";

		mvc.perform(post("/v1/transaction-summary/transactions").accept(MediaType.APPLICATION_JSON).content(json)
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

	}

	@Test
	public void testGetLastMinuteTransactionsSummary() throws Exception {
		logger.debug("test get summary");
		SummaryOfTransactions summary = new SummaryOfTransactions();
		summary.setCount(0L);
		summary.setMax(0.0);
		summary.setMin(0.0);
		summary.setSum(0.0);
		summary.setAvg(0.0);

		Mockito.when(transactionsService.getSummary()).thenReturn(summary);

		mvc.perform(get("/v1/transaction-summary/statistics").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

	}

}
