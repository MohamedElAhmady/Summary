package com.transactions.service;

import static java.lang.System.currentTimeMillis;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import com.transactions.entity.SummaryOfTransactions;
import com.transactions.entity.Transaction;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = TransactionsServiceImpl.class)
public class TransactionsServiceTest {

	private final static Logger logger = LoggerFactory.getLogger(TransactionsServiceTest.class);

	@InjectMocks
	TransactionsServiceImpl transactionsService;

	final private Transaction transactionWithinLastMinute = new Transaction();
	final private Transaction notInLastMinute = new Transaction();
	final private LinkedHashMap<Long, SummaryOfTransactions> transactions = new LinkedHashMap<>();
	private SummaryOfTransactions firstSummary = null;
	private SummaryOfTransactions secondSummary = null;

	@Before
	public void init() {

		firstSummary = new SummaryOfTransactions();
		firstSummary.setTimestamp(currentTimeMillis());
		firstSummary.setCount(3L);
		firstSummary.setAvg(6.0);
		firstSummary.setMax(9.0);
		firstSummary.setMin(3.0);
		firstSummary.setSum(18.0);

		secondSummary = new SummaryOfTransactions();
		secondSummary.setTimestamp(currentTimeMillis() - 20000);
		secondSummary.setCount(3L);
		secondSummary.setAvg(8.0);
		secondSummary.setMax(10.0);
		secondSummary.setMin(5.0);
		secondSummary.setSum(24.0);

		transactions.put(firstSummary.getTimestamp(), firstSummary);
		transactions.put(secondSummary.getTimestamp(), secondSummary);

		transactionWithinLastMinute.setAmount(5.4);
		transactionWithinLastMinute.setTimestamp(currentTimeMillis());

		notInLastMinute.setAmount(10.5);
		notInLastMinute.setTimestamp(currentTimeMillis() - (60000 * 2));
	}

	@Test
	public void testAddTransactionWithinLastMinute() {
		logger.debug("in testAddTransactionWithinLastMinute");
		boolean result = transactionsService.addTransaction(transactionWithinLastMinute);
		assertEquals(true, result);
	}

	@Test
	public void testAddTransactionNotInLastMinute() {
		logger.debug("in testAddTransactionNotInLastMinute");
		boolean result = transactionsService.addTransaction(notInLastMinute);
		assertEquals(false, result);
	}

	@Test
	public void testEmptyLastMinuteSummary()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		logger.debug("in testEmptyLastMinuteSummary");

		Field field = TransactionsServiceImpl.class.getDeclaredField("lastMinuteTransactions");
		field.setAccessible(true);
		field.set(transactionsService, new LinkedHashMap<Long, SummaryOfTransactions>());
		SummaryOfTransactions result = transactionsService.getSummary();
		assertEquals(result.getSum(), 0.0, 0.001);
	}

	@Test
	public void testGetSummaryOfLastMinute()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {

		logger.debug("in testGetSummaryOfLastMinute");
		Field field = TransactionsServiceImpl.class.getDeclaredField("lastMinuteTransactions");
		field.setAccessible(true);
		field.set(transactionsService, transactions);
		SummaryOfTransactions result = transactionsService.getSummary();
		assertEquals(result.getSum(), firstSummary.getSum() + secondSummary.getSum(), 0.001);
		assertEquals(result.getMin(), 3.0, 0.001);
		assertEquals(result.getMax(), 10.0, 0.001);
		assertEquals(result.getAvg(), result.getSum() / result.getCount(), 0.001);
	}

	@Test
	public void testSummaryForEntriesNotWithinLastMinute()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		logger.debug("in testSummaryForEntriesNotWithinLastMinute");
		SummaryOfTransactions thirdSummary = new SummaryOfTransactions();
		thirdSummary.setCount(3L);
		thirdSummary.setAvg(8.0);
		thirdSummary.setMax(10.0);
		thirdSummary.setMin(5.0);
		thirdSummary.setSum(24.0);
		thirdSummary.setTimestamp(currentTimeMillis() - 120000);
		transactions.put(thirdSummary.getTimestamp(), thirdSummary);
		Field field = TransactionsServiceImpl.class.getDeclaredField("lastMinuteTransactions");
		field.setAccessible(true);
		field.set(transactionsService, transactions);
		SummaryOfTransactions result = transactionsService.getSummary();
		assertEquals(result.getSum(), firstSummary.getSum() + secondSummary.getSum(), 0.001);
	}

}
