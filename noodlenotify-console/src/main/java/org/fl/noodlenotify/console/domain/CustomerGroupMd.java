package org.fl.noodlenotify.console.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_CUSTOMER_GROUP")
public class CustomerGroupMd implements java.io.Serializable {

	private static final long serialVersionUID = 5248390504913904876L;

	private String customerGroup_Nm;
	private Byte manual_Status;

	@Id
	@Column(name = "CUSTOMERGROUP_NM", nullable = false, length = 32)
	public String getCustomerGroup_Nm() {
		return customerGroup_Nm;
	}

	public void setCustomerGroup_Nm(String customerGroup_Nm) {
		this.customerGroup_Nm = customerGroup_Nm;
	}

	@Column(name = "MANUAL_STATUS", nullable = false, length = 1)
	public Byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(Byte manual_Status) {
		this.manual_Status = manual_Status;
	}

}
