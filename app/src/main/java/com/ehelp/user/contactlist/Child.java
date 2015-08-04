package com.ehelp.user.contactlist;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Child implements Serializable{
	private String username;//用户名
	private String headphoto;//头像
	private String phonenumber;//手机号
	private String online_status;//是否在线
	
	
	public String getPhone() {
		return phonenumber;
	}
	public void setPhone(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
}
	public String getOnline_status() {
		return online_status;
	}
	public void setOnline_status(String online_status) {
		this.online_status = online_status;
	}
	public String getHeadphoto() {
		return headphoto;
	}
	public void setHeadphoto(String headphoto) {
		this.headphoto = headphoto;
	}
	
}