package org.fl.noodlenotify.console.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_QUEUE")
public class QueueMd implements java.io.Serializable {

	private static final long serialVersionUID = 1637107096467838240L;

	private String queue_Nm;
	private byte manual_Status;
	private byte is_Repeat;
	private long expire_Time;
	private long dph_Delay_Time;
	private long dph_Timeout;
	private int new_Pop_ThreadNum;
	private int new_Exe_ThreadNum;
	private int portion_Pop_ThreadNum;
	private int portion_Exe_ThreadNum;
	private long interval_Time;
	private Date create_Tm;
	
	private long rev_T_Cnt_Mit;
	private long rev_T_Cnt_Hor;
	private double rev_O_Rate_Mit;
	private double rev_O_Rate_Hor;
	private double rev_E_Rate_Mit;
	private double rev_E_Rate_Hor;
	
	private long dph_T_Cnt_Mit;
	private long dph_T_Cnt_Hor;
	private double dph_O_Rate_Mit;
	private double dph_O_Rate_Hor;
	private double dph_E_Rate_Mit;
	private double dph_E_Rate_Hor;
	
	private long dph_OD_Cnt_Mit;
	private long dph_OD_Cnt_Hor;

	@Id
	@Column(name = "QUEUE_NM", nullable = false, length = 32)
	public String getQueue_Nm() {
		return queue_Nm;
	}

	public void setQueue_Nm(String queue_Nm) {
		this.queue_Nm = queue_Nm;
	}

	@Column(name = "MANUAL_STATUS", nullable = false, length = 1)
	public byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(byte manual_Status) {
		this.manual_Status = manual_Status;
	}

	@Column(name = "IS_REPEAT", nullable = false, length = 1)
	public byte getIs_Repeat() {
		return is_Repeat;
	}

	public void setIs_Repeat(byte is_Repeat) {
		this.is_Repeat = is_Repeat;
	}

	@Column(name = "EXPIRE_TIME", nullable = false, length = 16)
	public long getExpire_Time() {
		return expire_Time;
	}

	public void setExpire_Time(long expire_Time) {
		this.expire_Time = expire_Time;
	}

	@Column(name = "NEW_POP_THREADNUM", nullable = false, length = 16)
	public int getNew_Pop_ThreadNum() {
		return new_Pop_ThreadNum;
	}

	public void setNew_Pop_ThreadNum(int new_Pop_ThreadNum) {
		this.new_Pop_ThreadNum = new_Pop_ThreadNum;
	}

	@Column(name = "NEW_EXE_THREADNUM", nullable = false, length = 16)
	public int getNew_Exe_ThreadNum() {
		return new_Exe_ThreadNum;
	}

	public void setNew_Exe_ThreadNum(int new_Exe_ThreadNum) {
		this.new_Exe_ThreadNum = new_Exe_ThreadNum;
	}

	@Column(name = "PORTION_POP_THREADNUM", nullable = false, length = 16)
	public int getPortion_Pop_ThreadNum() {
		return portion_Pop_ThreadNum;
	}

	public void setPortion_Pop_ThreadNum(int portion_Pop_ThreadNum) {
		this.portion_Pop_ThreadNum = portion_Pop_ThreadNum;
	}

	@Column(name = "PORTION_EXE_THREADNUM", nullable = false, length = 16)
	public int getPortion_Exe_ThreadNum() {
		return portion_Exe_ThreadNum;
	}

	public void setPortion_Exe_ThreadNum(int portion_Exe_ThreadNum) {
		this.portion_Exe_ThreadNum = portion_Exe_ThreadNum;
	}

	@Column(name = "INTERVAL_TIME", nullable = false, length = 16)
	public long getInterval_Time() {
		return interval_Time;
	}

	public void setInterval_Time(long interval_Time) {
		this.interval_Time = interval_Time;
	}

	@Column(name = "CREATE_TM", nullable = false)
	public Date getCreate_Tm() {
		return create_Tm;
	}

	public void setCreate_Tm(Date create_Tm) {
		this.create_Tm = create_Tm;
	}

	@Column(name = "REV_T_CNT_MIT", nullable = false, length = 16)
	public long getRev_T_Cnt_Mit() {
		return rev_T_Cnt_Mit;
	}

	public void setRev_T_Cnt_Mit(long rev_T_Cnt_Mit) {
		this.rev_T_Cnt_Mit = rev_T_Cnt_Mit;
	}

	@Column(name = "REV_T_CNT_HOR", nullable = false, length = 16)
	public long getRev_T_Cnt_Hor() {
		return rev_T_Cnt_Hor;
	}

	public void setRev_T_Cnt_Hor(long rev_T_Cnt_Hor) {
		this.rev_T_Cnt_Hor = rev_T_Cnt_Hor;
	}

	@Column(name = "REV_O_RATE_MIT", nullable = false, length = 16)
	public double getRev_O_Rate_Mit() {
		return rev_O_Rate_Mit;
	}

	public void setRev_O_Rate_Mit(double rev_O_Rate_Mit) {
		this.rev_O_Rate_Mit = rev_O_Rate_Mit;
	}

	@Column(name = "REV_O_RATE_HOR", nullable = false, length = 16)
	public double getRev_O_Rate_Hor() {
		return rev_O_Rate_Hor;
	}

	public void setRev_O_Rate_Hor(double rev_O_Rate_Hor) {
		this.rev_O_Rate_Hor = rev_O_Rate_Hor;
	}

	@Column(name = "REV_E_RATE_MIT", nullable = false, length = 16)
	public double getRev_E_Rate_Mit() {
		return rev_E_Rate_Mit;
	}

	public void setRev_E_Rate_Mit(double rev_E_Rate_Mit) {
		this.rev_E_Rate_Mit = rev_E_Rate_Mit;
	}

	@Column(name = "REV_E_RATE_HOR", nullable = false, length = 16)
	public double getRev_E_Rate_Hor() {
		return rev_E_Rate_Hor;
	}

	public void setRev_E_Rate_Hor(double rev_E_Rate_Hor) {
		this.rev_E_Rate_Hor = rev_E_Rate_Hor;
	}

	@Column(name = "DPH_T_CNT_MIT", nullable = false, length = 16)
	public long getDph_T_Cnt_Mit() {
		return dph_T_Cnt_Mit;
	}

	public void setDph_T_Cnt_Mit(long dph_T_Cnt_Mit) {
		this.dph_T_Cnt_Mit = dph_T_Cnt_Mit;
	}

	@Column(name = "DPH_T_CNT_HOR", nullable = false, length = 16)
	public long getDph_T_Cnt_Hor() {
		return dph_T_Cnt_Hor;
	}

	public void setDph_T_Cnt_Hor(long dph_T_Cnt_Hor) {
		this.dph_T_Cnt_Hor = dph_T_Cnt_Hor;
	}

	@Column(name = "DPH_O_RATE_MIT", nullable = false, length = 16)
	public double getDph_O_Rate_Mit() {
		return dph_O_Rate_Mit;
	}

	public void setDph_O_Rate_Mit(double dph_O_Rate_Mit) {
		this.dph_O_Rate_Mit = dph_O_Rate_Mit;
	}

	@Column(name = "DPH_O_RATE_HOR", nullable = false, length = 16)
	public double getDph_O_Rate_Hor() {
		return dph_O_Rate_Hor;
	}

	public void setDph_O_Rate_Hor(double dph_O_Rate_Hor) {
		this.dph_O_Rate_Hor = dph_O_Rate_Hor;
	}

	@Column(name = "DPH_E_RATE_MIT", nullable = false, length = 16)
	public double getDph_E_Rate_Mit() {
		return dph_E_Rate_Mit;
	}

	public void setDph_E_Rate_Mit(double dph_E_Rate_Mit) {
		this.dph_E_Rate_Mit = dph_E_Rate_Mit;
	}

	@Column(name = "DPH_E_RATE_HOR", nullable = false, length = 16)
	public double getDph_E_Rate_Hor() {
		return dph_E_Rate_Hor;
	}

	public void setDph_E_Rate_Hor(double dph_E_Rate_Hor) {
		this.dph_E_Rate_Hor = dph_E_Rate_Hor;
	}
	
	@Column(name = "DPH_DELAY_TIME", nullable = false, length = 16)
	public long getDph_Delay_Time() {
		return dph_Delay_Time;
	}

	public void setDph_Delay_Time(long dph_Delay_Time) {
		this.dph_Delay_Time = dph_Delay_Time;
	}
	
	@Column(name = "DPH_TIMEOUT", nullable = false, length = 16)
	public long getDph_Timeout() {
		return dph_Timeout;
	}

	public void setDph_Timeout(long dph_Timeout) {
		this.dph_Timeout = dph_Timeout;
	}
	
	@Column(name = "DPH_OD_CNT_MIT", nullable = false, length = 16)
	public long getDph_OD_Cnt_Mit() {
		return dph_OD_Cnt_Mit;
	}

	public void setDph_OD_Cnt_Mit(long dph_OD_Cnt_Mit) {
		this.dph_OD_Cnt_Mit = dph_OD_Cnt_Mit;
	}
	
	@Column(name = "DPH_OD_CNT_HOR", nullable = false, length = 16)
	public long getDph_OD_Cnt_Hor() {
		return dph_OD_Cnt_Hor;
	}

	public void setDph_OD_Cnt_Hor(long dph_OD_Cnt_Hor) {
		this.dph_OD_Cnt_Hor = dph_OD_Cnt_Hor;
	}
	
}
