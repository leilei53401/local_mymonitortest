package com.voole.ad.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.type.Alias;

/**
 * 文件信息
 * @author shaoyl
 *
 */
@Alias(value = "MonFileInfo")
public class MonFileInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	private long id; 
	private String fname;
	private long fsize;
	private String fpath;	
	private String fhost;
	private String status;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public long getFsize() {
		return fsize;
	}
	public void setFsize(long fsize) {
		this.fsize = fsize;
	}
	public String getFpath() {
		return fpath;
	}
	public void setFpath(String fpath) {
		this.fpath = fpath;
	}
	public String getFhost() {
		return fhost;
	}
	public void setFhost(String fhost) {
		this.fhost = fhost;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "MonFileInfo [id=" + id + ", fname=" + fname + ", fsize="
				+ fsize + ", fpath=" + fpath + ", fhost=" + fhost + ", status="
				+ status + "]";
	}
	
}
