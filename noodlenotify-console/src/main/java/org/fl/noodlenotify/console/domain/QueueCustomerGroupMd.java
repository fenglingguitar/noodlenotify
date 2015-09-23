package org.fl.noodlenotify.console.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_QUEUE_CUSTOMER_GROUP")
public class QueueCustomerGroupMd implements java.io.Serializable {

	private static final long serialVersionUID = 6328110704380175731L;
	
	private QueueMd queueMd;
	private CustomerGroupMd customerGroupMd;
	private long customer_Num;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QUEUE_NM", nullable = false)
	public QueueMd getQueueMd() {
		return queueMd;
	}

	public void setQueueMd(QueueMd queueMd) {
		this.queueMd = queueMd;
	}

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMERGROUP_NM", nullable = false)
	public CustomerGroupMd getCustomerGroupMd() {
		return customerGroupMd;
	}

	public void setCustomerGroupMd(CustomerGroupMd customerGroupMd) {
		this.customerGroupMd = customerGroupMd;
	}

	@Column(name = "CUSTOMER_NUM", nullable = false, length = 16)
	public long getCustomer_Num() {
		return customer_Num;
	}

	public void setCustomer_Num(long customer_Num) {
		this.customer_Num = customer_Num;
	}

}
