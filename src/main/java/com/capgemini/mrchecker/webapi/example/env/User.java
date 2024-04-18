package com.capgemini.mrchecker.webapi.example.env;

import lombok.Getter;

@Getter
public enum User {
	SOURCE_USER(GetEnvironmentParam.SOURCE_USER, GetEnvironmentParam.SOURCE_USER_PASSWORD, "source_user"),
	TARGET_USER(GetEnvironmentParam.TARGET_USER, GetEnvironmentParam.TARGET_USER_PASSWORD, "target_user");;
	
	private String username, userPassword, userFullName;
	
	User(GetEnvironmentParam username, GetEnvironmentParam password, String userFullName) {
		this.username = username.getValue();
		this.userPassword = password.getValue();
		this.userFullName = userFullName;
	}
}