package com.github.masterdxy.gateway.test.dubbo.provider;

import java.io.Serializable;

public class EchoDTO implements Serializable {

	private String  name;
	private Integer age;
	private Boolean speakEnglish;

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

	public Integer getAge () {
		return age;
	}

	public void setAge (Integer age) {
		this.age = age;
	}

	public Boolean getSpeakEnglish () {
		return speakEnglish;
	}

	public void setSpeakEnglish (Boolean speakEnglish) {
		this.speakEnglish = speakEnglish;
	}
}
