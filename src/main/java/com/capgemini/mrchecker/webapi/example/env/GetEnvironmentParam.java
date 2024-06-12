package com.capgemini.mrchecker.webapi.example.env;

import java.util.Objects;

import com.capgemini.mrchecker.test.core.BaseTest;
import com.capgemini.mrchecker.test.core.exceptions.BFInputDataException;

public enum GetEnvironmentParam {
	// Reads the environment variable from /src/resources/environments/environment.csv
	// Name of enum must be in line with cell name in /src/resources/environments/environment.csv

	PAGE,
	SOURCE_USER_PASSWORD,
	SOURCE_USER,
	TARGET_USER_PASSWORD,
	TARGET_USER,
	USER_A_PASSWORD,
	USER_A;
	
	private String value;
	
	@Override
	public String toString() {
		return getValue();
	}
	
	public String getValue() {
		if (null == this.value) {
			if (Objects.isNull(BaseTest.getEnvironmentService())) {
				throw new BFInputDataException("Environment Parameters class wasn't initialized properly");
			}
			this.value = BaseTest.getEnvironmentService()
					.getValue(this.name())
					.trim();
		}
		return this.value;
	}
}