package com.plugin.pojo;

import java.util.List;

public class MethodDetails {
	private String returnType;
	private String name;
	private String specifier;
	private String modifier;
	private String body;
	private List<ParameterDetails> parameters;
	private String exception;
	private String startingIndex;
	private String endingIndex;

	public void setStartingIndex(String startingIndex) {
		this.startingIndex = startingIndex;
	}

	public String getStartingIndex() {
		return startingIndex;
	}

	public void setEndingIndex(String endingIndex) {
		this.endingIndex = endingIndex;
	}

	public String getEndingIndex() {
		return endingIndex;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpecifier() {
		return specifier;
	}

	public void setSpecifier(String specifier) {
		this.specifier = specifier;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<ParameterDetails> getParameters() {
		return parameters;
	}

	public void setParameters(List<ParameterDetails> parameters) {
		this.parameters = parameters;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getException() {
		return exception;
	}
}
