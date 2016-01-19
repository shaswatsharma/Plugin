package com.plugin.pojo;

public class MethodDetails {
	private String returnType;
	private String name;
	private String specifier;
	private String modifier;
	private String body;
	private ParameterDetails parameters;
	private String exception;
	private String startingIndex;
	private String endingIndex;
	private int totalParameters;

	public int getTotalParameters() {
		return totalParameters;
	}

	public void setTotalParameters(int totalParameters) {
		this.totalParameters = totalParameters;
	}

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

	public ParameterDetails getParameters() {
		return parameters;
	}

	public void setParameters(ParameterDetails parameters) {
		this.parameters = parameters;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public String getException() {
		return exception;
	}
}
