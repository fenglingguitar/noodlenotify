package org.fl.noodlenotify.console.vo;

public class CustomerGroupVo implements java.io.Serializable {
	
	private static final long serialVersionUID = -2030752486865252965L;
	
	private String customerGroup_Nm;
	private Byte manual_Status;

	public String getCustomerGroup_Nm() {
		return customerGroup_Nm;
	}

	public void setCustomerGroup_Nm(String customerGroup_Nm) {
		this.customerGroup_Nm = customerGroup_Nm;
	}

	public Byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(Byte manual_Status) {
		this.manual_Status = manual_Status;
	}
}
