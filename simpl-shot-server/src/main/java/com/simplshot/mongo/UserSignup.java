package com.simplshot.mongo;

public class UserSignup {
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getOccpation() {
		return occpation;
	}
	public void setOccpation(String occpation) {
		this.occpation = occpation;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	private String email;
	private String password;
	private int age;
	private String occpation;
	private String sex;
	private String userName;
}
