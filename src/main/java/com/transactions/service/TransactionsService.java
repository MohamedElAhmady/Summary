package com.transactions.service;

import com.transactions.entity.SummaryOfTransactions;
import com.transactions.entity.Transaction;

public interface TransactionsService {

	public SummaryOfTransactions getSummary();

	public Boolean addTransaction(Transaction transaction);
}
