package com.transactions.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Transaction {

	double amount;
	long timestamp;

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(timestamp).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Transaction) {
			final Transaction other = (Transaction) obj;
			return new EqualsBuilder().append(timestamp, other.timestamp).isEquals();
		} else {
			return false;
		}
	}

}
