package org.fl.noodlenotify.console.domain;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_CUSTOMER")
public class CustomerMd implements java.io.Serializable {

	private static final long serialVersionUID = -7856386332878028902L;

	private long customer_Id;
	private String name;
	private String ip;
	private int port;
	private String url;
	private String type;
	private int check_Port;
	private String check_Url;
	private String check_Type;
	private byte system_Status;
	private byte manual_Status;
	private CustomerGroupMd customerGroupMd;
	

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "CUSTOMER_ID", nullable = false, length = 16)
	public long getCustomer_Id() {
		return this.customer_Id;
	}

	public void setCustomer_Id(long customer_Id) {
		this.customer_Id = customer_Id;
	}

	@Column(name = "NAME", length = 32)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "IP", nullable = false, length = 64)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "PORT", nullable = false, length = 8)
	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	@Column(name = "URL", nullable = true, length = 512)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "TYPE", nullable = false, length = 32)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "CHECK_PORT", nullable = false, length = 8)
	public int getCheck_Port() {
		return check_Port;
	}

	public void setCheck_Port(int check_Port) {
		this.check_Port = check_Port;
	}

	@Column(name = "CHECK_URL", nullable = true, length = 512)
	public String getCheck_Url() {
		return check_Url;
	}

	public void setCheck_Url(String check_Url) {
		this.check_Url = check_Url;
	}

	@Column(name = "CHECK_TYPE", nullable = false, length = 32)
	public String getCheck_Type() {
		return check_Type;
	}

	public void setCheck_Type(String check_Type) {
		this.check_Type = check_Type;
	}

	@Column(name = "SYSTEM_STATUS", nullable = false, length = 1)
	public byte getSystem_Status() {
		return system_Status;
	}

	public void setSystem_Status(byte system_Status) {
		this.system_Status = system_Status;
	}

	@Column(name = "MANUAL_STATUS", nullable = false, length = 1)
	public byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(byte manual_Status) {
		this.manual_Status = manual_Status;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMERGROUP_NM", nullable = true)
	public CustomerGroupMd getCustomerGroupMd() {
		return customerGroupMd;
	}

	public void setCustomerGroupMd(CustomerGroupMd customerGroupMd) {
		this.customerGroupMd = customerGroupMd;
	}
}
