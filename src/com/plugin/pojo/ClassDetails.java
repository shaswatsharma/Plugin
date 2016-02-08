package com.plugin.pojo;

import java.util.List;

public class ClassDetails {
	private List<MethodDetails> methods;
	private List<ClassVariableDetails> variables;
	private String className;
 
	
	public List<MethodDetails> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodDetails> methods) {
		this.methods = methods;
	}

	public List<ClassVariableDetails> getVariables() {
		return variables;
	}

	public void setVariables(List<ClassVariableDetails> variables) {
		this.variables = variables;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
