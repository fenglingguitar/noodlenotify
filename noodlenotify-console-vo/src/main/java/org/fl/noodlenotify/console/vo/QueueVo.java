package org.fl.noodlenotify.console.vo;

import java.util.Date;

public class QueueVo implements java.io.Serializable {

	private static final long serialVersionUID = -5061266701308182392L;

	private String queue_Nm;
	private Byte manual_Status;
	private Byte is_Repeat;
	private Long expire_Time;
	private Long interval_Time;
	private Long dph_Delay_Time;
	private Long dph_Timeout;
	private Integer new_Pop_ThreadNum;
	private Integer new_Exe_ThreadNum;
	private Integer portion_Pop_ThreadNum;
	private Integer portion_Exe_ThreadNum;
	private Date create_Tm;
	private Integer new_Len;
	private Integer portion_Len;
	
	private Long rev_T_Cnt_Mit;
	private Long rev_T_Cnt_Hor;
	private Double rev_O_Rate_Mit;
	private Double rev_O_Rate_Hor;
	private Double rev_E_Rate_Mit;
	private Double rev_E_Rate_Hor;
	
	private Long dph_T_Cnt_Mit;
	private Long dph_T_Cnt_Hor;
	private Double dph_O_Rate_Mit;
	private Double dph_O_Rate_Hor;
	private Double dph_E_Rate_Mit;
	private Double dph_E_Rate_Hor;
	/* 1分钟过期数量*/
	private Long dph_OD_Cnt_Mit;
	/* 一小时过期数量*/
	private Long dph_OD_Cnt_Hor;
	
	public String getQueue_Nm() {
		return queue_Nm;
	}

	public void setQueue_Nm(String queue_Nm) {
		this.queue_Nm = queue_Nm;
	}

	public Byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(Byte manual_Status) {
		this.manual_Status = manual_Status;
	}

	public Byte getIs_Repeat() {
		return is_Repeat;
	}

	public void setIs_Repeat(Byte is_Repeat) {
		this.is_Repeat = is_Repeat;
	}

	public Long getExpire_Time() {
		return expire_Time;
	}

	public void setExpire_Time(Long expire_Time) {
		this.expire_Time = expire_Time;
	}

	public Long getInterval_Time() {
		return interval_Time;
	}

	public void setInterval_Time(Long interval_Time) {
		this.interval_Time = interval_Time;
	}

	public Integer getNew_Pop_ThreadNum() {
		return new_Pop_ThreadNum;
	}

	public void setNew_Pop_ThreadNum(Integer new_Pop_ThreadNum) {
		this.new_Pop_ThreadNum = new_Pop_ThreadNum;
	}

	public Integer getNew_Exe_ThreadNum() {
		return new_Exe_ThreadNum;
	}

	public void setNew_Exe_ThreadNum(Integer new_Exe_ThreadNum) {
		this.new_Exe_ThreadNum = new_Exe_ThreadNum;
	}

	public Integer getPortion_Pop_ThreadNum() {
		return portion_Pop_ThreadNum;
	}

	public void setPortion_Pop_ThreadNum(Integer portion_Pop_ThreadNum) {
		this.portion_Pop_ThreadNum = portion_Pop_ThreadNum;
	}

	public Integer getPortion_Exe_ThreadNum() {
		return portion_Exe_ThreadNum;
	}

	public void setPortion_Exe_ThreadNum(Integer portion_Exe_ThreadNum) {
		this.portion_Exe_ThreadNum = portion_Exe_ThreadNum;
	}

	public Date getCreate_Tm() {
		return create_Tm;
	}

	public void setCreate_Tm(Date create_Tm) {
		this.create_Tm = create_Tm;
	}

	public Integer getNew_Len() {
		return new_Len;
	}

	public void setNew_Len(Integer new_Len) {
		this.new_Len = new_Len;
	}

	public Integer getPortion_Len() {
		return portion_Len;
	}

	public void setPortion_Len(Integer portion_Len) {
		this.portion_Len = portion_Len;
	}

	public Long getRev_T_Cnt_Mit() {
		return rev_T_Cnt_Mit;
	}

	public void setRev_T_Cnt_Mit(Long rev_T_Cnt_Mit) {
		this.rev_T_Cnt_Mit = rev_T_Cnt_Mit;
	}

	public Long getRev_T_Cnt_Hor() {
		return rev_T_Cnt_Hor;
	}

	public void setRev_T_Cnt_Hor(Long rev_T_Cnt_Hor) {
		this.rev_T_Cnt_Hor = rev_T_Cnt_Hor;
	}

	public Double getRev_O_Rate_Mit() {
		return rev_O_Rate_Mit;
	}

	public void setRev_O_Rate_Mit(Double rev_O_Rate_Mit) {
		this.rev_O_Rate_Mit = rev_O_Rate_Mit;
	}

	public Double getRev_O_Rate_Hor() {
		return rev_O_Rate_Hor;
	}

	public void setRev_O_Rate_Hor(Double rev_O_Rate_Hor) {
		this.rev_O_Rate_Hor = rev_O_Rate_Hor;
	}

	public Double getRev_E_Rate_Mit() {
		return rev_E_Rate_Mit;
	}

	public void setRev_E_Rate_Mit(Double rev_E_Rate_Mit) {
		this.rev_E_Rate_Mit = rev_E_Rate_Mit;
	}

	public Double getRev_E_Rate_Hor() {
		return rev_E_Rate_Hor;
	}

	public void setRev_E_Rate_Hor(Double rev_E_Rate_Hor) {
		this.rev_E_Rate_Hor = rev_E_Rate_Hor;
	}

	public Long getDph_T_Cnt_Mit() {
		return dph_T_Cnt_Mit;
	}

	public void setDph_T_Cnt_Mit(Long dph_T_Cnt_Mit) {
		this.dph_T_Cnt_Mit = dph_T_Cnt_Mit;
	}

	public Long getDph_T_Cnt_Hor() {
		return dph_T_Cnt_Hor;
	}

	public void setDph_T_Cnt_Hor(Long dph_T_Cnt_Hor) {
		this.dph_T_Cnt_Hor = dph_T_Cnt_Hor;
	}

	public Double getDph_O_Rate_Mit() {
		return dph_O_Rate_Mit;
	}

	public void setDph_O_Rate_Mit(Double dph_O_Rate_Mit) {
		this.dph_O_Rate_Mit = dph_O_Rate_Mit;
	}

	public Double getDph_O_Rate_Hor() {
		return dph_O_Rate_Hor;
	}

	public void setDph_O_Rate_Hor(Double dph_O_Rate_Hor) {
		this.dph_O_Rate_Hor = dph_O_Rate_Hor;
	}

	public Double getDph_E_Rate_Mit() {
		return dph_E_Rate_Mit;
	}

	public void setDph_E_Rate_Mit(Double dph_E_Rate_Mit) {
		this.dph_E_Rate_Mit = dph_E_Rate_Mit;
	}

	public Double getDph_E_Rate_Hor() {
		return dph_E_Rate_Hor;
	}

	public void setDph_E_Rate_Hor(Double dph_E_Rate_Hor) {
		this.dph_E_Rate_Hor = dph_E_Rate_Hor;
	}

	public Long getDph_Delay_Time() {
		return dph_Delay_Time;
	}

	public void setDph_Delay_Time(Long dph_Delay_Time) {
		this.dph_Delay_Time = dph_Delay_Time;
	}

	public Long getDph_Timeout() {
		return dph_Timeout;
	}

	public void setDph_Timeout(Long dph_Timeout) {
		this.dph_Timeout = dph_Timeout;
	}

	public Long getDph_OD_Cnt_Mit() {
		return dph_OD_Cnt_Mit;
	}

	public void setDph_OD_Cnt_Mit(Long dph_OD_Cnt_Mit) {
		this.dph_OD_Cnt_Mit = dph_OD_Cnt_Mit;
	}

	public Long getDph_OD_Cnt_Hor() {
		return dph_OD_Cnt_Hor;
	}

	public void setDph_OD_Cnt_Hor(Long dph_OD_Cnt_Hor) {
		this.dph_OD_Cnt_Hor = dph_OD_Cnt_Hor;
	}

}
