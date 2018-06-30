package com.transactions.service;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static java.lang.System.currentTimeMillis;
import com.transactions.entity.SummaryOfTransactions;
import com.transactions.entity.Transaction;

public class TransactionsServiceImpl implements TransactionsService {

	LinkedHashMap<Long, Transaction> lastMinuteTransactions = new LinkedHashMap<>();

	SummaryOfTransactions summary;

	private ReadWriteLock lock = new ReentrantReadWriteLock(true); // fairness mode to prevent starving

	@Override
	public SummaryOfTransactions getSummary() {
		Lock readLock = lock.readLock();
		readLock.lock();
		try {
			createLastMinuteSummary(lastMinuteTransactions);
			if (summary != null)
				return summary;

			// return default summary with empty values
			summary = new SummaryOfTransactions();
			summary.setAvg(0.0);
			summary.setCount(0L);
			summary.setMin(0.0);
			summary.setSum(0.0);
			summary.setMax(0.0);
			return summary;
		} finally {
			readLock.unlock();
		}

	}

	// every time new transaction comes update the lastMinute list
	@Override
	public boolean addTransaction(Transaction transaction) {

		// TODO handle else condition
		if (currentTimeMillis() - transaction.getTimestamp() <= 60000
				&& currentTimeMillis() - transaction.getTimestamp() >= 0) {
			Lock writeLock = lock.writeLock();
			writeLock.lock();
			try {

				if (lastMinuteTransactions.containsKey(transaction.getTimestamp())) {
					Transaction alreadyExistTransaction = lastMinuteTransactions.get(transaction.getTimestamp());
					alreadyExistTransaction.setAmount(alreadyExistTransaction.getAmount() + transaction.getAmount());
					lastMinuteTransactions.put(transaction.getTimestamp(), alreadyExistTransaction);
				} else {
					lastMinuteTransactions.put(transaction.getTimestamp(), transaction);
				}

			} finally {
				writeLock.unlock();
			}
			return true;// the transaction created successfully.
		}else {
			
			return false;// the transaction failed
		}

	}

	private void createLastMinuteSummary(HashMap<Long, Transaction> transactionMap) {
		summary = new SummaryOfTransactions();
		// filter to get the last minute transactions only
		List<Transaction> transactions = new ArrayList<>(transactionMap.values());
		final List<Double> lastMinuteAmounts = transactions.stream()
				.filter(transaction -> transaction.getTimestamp() <= 6000).map(Transaction::getAmount)
				.collect(toList());
		final Long count = lastMinuteAmounts.stream().count();
		summary.setCount(count);
		if (count > 0) {
			summary.setSum(lastMinuteAmounts.stream().mapToDouble(Double::doubleValue).sum());
			summary.setAvg(lastMinuteAmounts.stream().mapToDouble(Double::doubleValue).average().getAsDouble());
			summary.setMax(lastMinuteAmounts.stream().max(Double::compareTo).get());
			summary.setMin(lastMinuteAmounts.stream().min(Double::compareTo).get());
		}

	}

}