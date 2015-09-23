package org.fl.noodlenotify.console.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_AXL_GROUP_NUM")
public class GroupAuxiliaryMd implements java.io.Serializable {

	private static final long serialVersionUID = 6044951323552034122L;
	private long group_Num;

	@Id
	@Column(name = "GROUP_NUM", nullable = false, length = 20)
	public long getGroup_Num() {
		return group_Num;
	}

	public void setGroup_Num(long group_Num) {
		this.group_Num = group_Num;
	}

}
