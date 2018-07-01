package com.transactions.service;

import static java.lang.System.currentTimeMillis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.transactions.entity.SummaryOfTransactions;
import com.transactions.entity.Transaction;

@Service
public class TransactionsServiceImpl implements TransactionsService {

	private final static Logger logger = LoggerFactory.getLogger(TransactionsServiceImpl.class);

	LinkedHashMap<Long, SummaryOfTransactions> lastMinuteTransactions = new LinkedHashMap<>(60); // initial capacity 60
	                                                                                             // initial load factor .75
	SummaryOfTransactions summary;
	private ReadWriteLock lock = new ReentrantReadWriteLock(true); // enable fairness to prevent thread starving

	@Override
	public SummaryOfTransactions getSummary() {
		fillLastMinuteSummary(lastMinuteTransactions);
		return summary;

	}

	// every time new transaction comes update the lastMinute list
	@Override
	public Boolean addTransaction(Transaction transaction) {
		logger.debug("start adding new transaction");
		if (currentTimeMillis() - transaction.getTimestamp() <= 60000) {
			Lock writeLock = lock.writeLock();
			logger.debug("start writing blocking");
			writeLock.lock();

			try {
                 // if time stamp exist before add to it the new values
				if (lastMinuteTransactions.containsKey(transaction.getTimestamp())) {
					SummaryOfTransactions alreadyExistsummary = lastMinuteTransactions.get(transaction.getTimestamp());
					alreadyExistsummary.setCount(alreadyExistsummary.getCount() + 1);
					alreadyExistsummary.setSum(alreadyExistsummary.getSum() + transaction.getAmount());
					alreadyExistsummary.setAvg(alreadyExistsummary.getSum() / alreadyExistsummary.getCount());
					if (alreadyExistsummary.getMax() < transaction.getAmount())
						alreadyExistsummary.setMax(transaction.getAmount());
					if (alreadyExistsummary.getMin() > transaction.getAmount())
						alreadyExistsummary.setMin(transaction.getAmount());
				} else {
					// if new time stamp add a new entry
					SummaryOfTransactions newSummary = new SummaryOfTransactions();
					newSummary.setAvg(0.0);
					newSummary.setTimestamp(transaction.getTimestamp());
					newSummary.setCount(1L);
					newSummary.setMax(transaction.getAmount());
					newSummary.setMin(transaction.getAmount());
					newSummary.setSum(transaction.getAmount());
					lastMinuteTransactions.put(newSummary.getTimestamp(), newSummary);
				}
			} finally {
				logger.debug("release write lock");
				writeLock.unlock();
			}
			logger.debug("the transaction created successfully");
			return true; // Transaction is within the last Minute
		}

		else
			logger.debug("the transaction wasn't within the last minute");
			return false;// Transaction is not within the last Minute

	}

	private void fillLastMinuteSummary(Map<Long, SummaryOfTransactions> lastMinuteTransation) {
		logger.debug("getting the last minute transactions");
		List<SummaryOfTransactions> summaries = new ArrayList<>(lastMinuteTransation.values());
		logger.debug("start reading blocking");
		Lock readLock = lock.readLock();
		readLock.lock();
		try {
            // filter summaries to get only values of the last minute
			final List<SummaryOfTransactions> lastMinuteSummaries = summaries.stream()
					.filter(s -> currentTimeMillis() - s.getTimestamp() <= 60000).collect(Collectors.toList());

			Long count = lastMinuteSummaries.stream().collect(Collectors.summingLong(SummaryOfTransactions::getCount));

			if (count > 0) {

				summary = new SummaryOfTransactions();
				summary.setCount(count);
				summary.setSum(
						lastMinuteSummaries.stream().collect(Collectors.summingDouble(SummaryOfTransactions::getSum)));
				summary.setAvg(summary.getSum() / summary.getCount());
				summary.setMax(lastMinuteSummaries.stream().max(Comparator.comparing(SummaryOfTransactions::getMax))
						.get().getMax());
				summary.setMin(lastMinuteSummaries.stream().min(Comparator.comparing(SummaryOfTransactions::getMin))
						.get().getMin());
			} else {
				// when count becomes 0 just make empty summary end empty the old collection
				lastMinuteTransactions = new LinkedHashMap<>();
				summary = new SummaryOfTransactions();
				summary.setAvg(0.0);
				summary.setCount(0L);
				summary.setMin(0.0);
				summary.setSum(0.0);
				summary.setMax(0.0);
			}
		} finally {
			logger.debug("release read lock");
			readLock.unlock();
		}
	}

}