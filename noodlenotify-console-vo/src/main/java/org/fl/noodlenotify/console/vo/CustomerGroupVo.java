package org.fl.noodlenotify.console.vo;

public class CustomerGroupVo implements java.io.Serializable {
	
	private static final long serialVersionUID = -2030752486865252965L;
	
	private String customerGroup_Nm;
	private byte manual_Status;

	public String getCustomerGroup_Nm() {
		return customerGroup_Nm;
	}

	public void setCustomerGroup_Nm(String customerGroup_Nm) {
		this.customerGroup_Nm = customerGroup_Nm;
	}

	public byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(byte manual_Status) {
		this.manual_Status = manual_Status;
	}
}
