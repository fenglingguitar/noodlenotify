package org.fl.noodlenotify.console.vo;

import java.util.Date;

public class QueueVo implements java.io.Serializable {

	private static final long serialVersionUID = -5061266701308182392L;

	private String queue_Nm;
	private byte manual_Status;
	private byte is_Repeat;
	private long expire_Time;
	private byte is_Trace;
	private long interval_Time;
	private long dph_Delay_Time;
	private long dph_Timeout;
	private int new_Pop_ThreadNum;
	private int new_Exe_ThreadNum;
	private int portion_Pop_ThreadNum;
	private int portion_Exe_ThreadNum;
	private Date create_Tm;
	private int new_Len;
	private int portion_Len;
	
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
	/* 1分钟过期数量*/
	private long dph_OD_Cnt_Mit;
	/* 一小时过期数量*/
	private long dph_OD_Cnt_Hor;
	
	public String getQueue_Nm() {
		return queue_Nm;
	}

	public void setQueue_Nm(String queue_Nm) {
		this.queue_Nm = queue_Nm;
	}

	public byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(byte manual_Status) {
		this.manual_Status = manual_Status;
	}

	public byte getIs_Repeat() {
		return is_Repeat;
	}

	public void setIs_Repeat(byte is_Repeat) {
		this.is_Repeat = is_Repeat;
	}

	public long getExpire_Time() {
		return expire_Time;
	}

	public void setExpire_Time(long expire_Time) {
		this.expire_Time = expire_Time;
	}

	public long getInterval_Time() {
		return interval_Time;
	}

	public void setInterval_Time(long interval_Time) {
		this.interval_Time = interval_Time;
	}
	
	public byte getIs_Trace() {
		return is_Trace;
	}

	public void setIs_Trace(byte is_Trace) {
		this.is_Trace = is_Trace;
	}

	public int getNew_Pop_ThreadNum() {
		return new_Pop_ThreadNum;
	}

	public void setNew_Pop_ThreadNum(int new_Pop_ThreadNum) {
		this.new_Pop_ThreadNum = new_Pop_ThreadNum;
	}

	public int getNew_Exe_ThreadNum() {
		return new_Exe_ThreadNum;
	}

	public void setNew_Exe_ThreadNum(int new_Exe_ThreadNum) {
		this.new_Exe_ThreadNum = new_Exe_ThreadNum;
	}

	public int getPortion_Pop_ThreadNum() {
		return portion_Pop_ThreadNum;
	}

	public void setPortion_Pop_ThreadNum(int portion_Pop_ThreadNum) {
		this.portion_Pop_ThreadNum = portion_Pop_ThreadNum;
	}

	public int getPortion_Exe_ThreadNum() {
		return portion_Exe_ThreadNum;
	}

	public void setPortion_Exe_ThreadNum(int portion_Exe_ThreadNum) {
		this.portion_Exe_ThreadNum = portion_Exe_ThreadNum;
	}

	public Date getCreate_Tm() {
		return create_Tm;
	}

	public void setCreate_Tm(Date create_Tm) {
		this.create_Tm = create_Tm;
	}

	public int getNew_Len() {
		return new_Len;
	}

	public void setNew_Len(int new_Len) {
		this.new_Len = new_Len;
	}

	public int getPortion_Len() {
		return portion_Len;
	}

	public void setPortion_Len(int portion_Len) {
		this.portion_Len = portion_Len;
	}

	public long getRev_T_Cnt_Mit() {
		return rev_T_Cnt_Mit;
	}

	public void setRev_T_Cnt_Mit(long rev_T_Cnt_Mit) {
		this.rev_T_Cnt_Mit = rev_T_Cnt_Mit;
	}

	public long getRev_T_Cnt_Hor() {
		return rev_T_Cnt_Hor;
	}

	public void setRev_T_Cnt_Hor(long rev_T_Cnt_Hor) {
		this.rev_T_Cnt_Hor = rev_T_Cnt_Hor;
	}

	public double getRev_O_Rate_Mit() {
		return rev_O_Rate_Mit;
	}

	public void setRev_O_Rate_Mit(double rev_O_Rate_Mit) {
		this.rev_O_Rate_Mit = rev_O_Rate_Mit;
	}

	public double getRev_O_Rate_Hor() {
		return rev_O_Rate_Hor;
	}

	public void setRev_O_Rate_Hor(double rev_O_Rate_Hor) {
		this.rev_O_Rate_Hor = rev_O_Rate_Hor;
	}

	public double getRev_E_Rate_Mit() {
		return rev_E_Rate_Mit;
	}

	public void setRev_E_Rate_Mit(double rev_E_Rate_Mit) {
		this.rev_E_Rate_Mit = rev_E_Rate_Mit;
	}

	public double getRev_E_Rate_Hor() {
		return rev_E_Rate_Hor;
	}

	public void setRev_E_Rate_Hor(double rev_E_Rate_Hor) {
		this.rev_E_Rate_Hor = rev_E_Rate_Hor;
	}

	public long getDph_T_Cnt_Mit() {
		return dph_T_Cnt_Mit;
	}

	public void setDph_T_Cnt_Mit(long dph_T_Cnt_Mit) {
		this.dph_T_Cnt_Mit = dph_T_Cnt_Mit;
	}

	public long getDph_T_Cnt_Hor() {
		return dph_T_Cnt_Hor;
	}

	public void setDph_T_Cnt_Hor(long dph_T_Cnt_Hor) {
		this.dph_T_Cnt_Hor = dph_T_Cnt_Hor;
	}

	public double getDph_O_Rate_Mit() {
		return dph_O_Rate_Mit;
	}

	public void setDph_O_Rate_Mit(double dph_O_Rate_Mit) {
		this.dph_O_Rate_Mit = dph_O_Rate_Mit;
	}

	public double getDph_O_Rate_Hor() {
		return dph_O_Rate_Hor;
	}

	public void setDph_O_Rate_Hor(double dph_O_Rate_Hor) {
		this.dph_O_Rate_Hor = dph_O_Rate_Hor;
	}

	public double getDph_E_Rate_Mit() {
		return dph_E_Rate_Mit;
	}

	public void setDph_E_Rate_Mit(double dph_E_Rate_Mit) {
		this.dph_E_Rate_Mit = dph_E_Rate_Mit;
	}

	public double getDph_E_Rate_Hor() {
		return dph_E_Rate_Hor;
	}

	public void setDph_E_Rate_Hor(double dph_E_Rate_Hor) {
		this.dph_E_Rate_Hor = dph_E_Rate_Hor;
	}

	public long getDph_Delay_Time() {
		return dph_Delay_Time;
	}

	public void setDph_Delay_Time(long dph_Delay_Time) {
		this.dph_Delay_Time = dph_Delay_Time;
	}

	public long getDph_Timeout() {
		return dph_Timeout;
	}

	public void setDph_Timeout(long dph_Timeout) {
		this.dph_Timeout = dph_Timeout;
	}

	public long getDph_OD_Cnt_Mit() {
		return dph_OD_Cnt_Mit;
	}

	public void setDph_OD_Cnt_Mit(long dph_OD_Cnt_Mit) {
		this.dph_OD_Cnt_Mit = dph_OD_Cnt_Mit;
	}

	public long getDph_OD_Cnt_Hor() {
		return dph_OD_Cnt_Hor;
	}

	public void setDph_OD_Cnt_Hor(long dph_OD_Cnt_Hor) {
		this.dph_OD_Cnt_Hor = dph_OD_Cnt_Hor;
	}

}
