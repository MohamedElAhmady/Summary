package com.transactions.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SummaryOfTransactions {

	private Double sum;

	private Double avg;

	private Double max;

	private Double min;

	private Long count;

	@JsonIgnore
	private long timestamp;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public Double getSum() {
		return sum;
	}

	public void setSum(Double sum) {
		this.sum = sum;
	}

	public Double getAvg() {
		return avg;
	}

	public void setAvg(Double avg) {
		this.avg = avg;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(timestamp).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SummaryOfTransactions) {
			final SummaryOfTransactions other = (SummaryOfTransactions) obj;
			return new EqualsBuilder().append(timestamp, other.timestamp).isEquals();
		} else {
			return false;
		}
	}

}
