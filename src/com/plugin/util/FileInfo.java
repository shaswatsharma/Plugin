package com.plugin.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class FileInfo {

	private String destination = "C:\\Users\\I320234\\Desktop\\Custom Method.txt";
	private String name, accessSpecifier, accessModifier, returnType, resultMethod, comments, filePath, addComment,
			annotation;
	private ArrayList<HashMap<String, String>> list;
	private final String KEY_NAME = "name";
	private final String KEY_TYPE = "type";
	private boolean isReturnTypeCustom = false;
	private String beforeComment = "";
	private String body;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isReturnTypeCustom() {
		return isReturnTypeCustom;
	}

	public void setReturnTypeCustom(boolean isReturnTypeCustom) {
		this.isReturnTypeCustom = isReturnTypeCustom;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {// name of the method to be appended
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {// return type of method
		this.returnType = returnType;
	}

	public String getAccessSpecifier() {
		return accessSpecifier;
	}

	public void setAccessSpecifier(String accessSpecifier) {// access Specifier
															// of the method to
															// be appended
		this.accessSpecifier = accessSpecifier;
	}

	public String getAccessModifier() {
		return accessModifier;
	}

	public void setAccessModifier(String accessModifier) {// set the access
															// specifier of the
															// resulting method
		this.accessModifier = accessModifier;
	}

	public ArrayList<HashMap<String, String>> getList() {
		return list;
	}

	public void setList(ArrayList<HashMap<String, String>> list) {
		this.list = list;
	}

	/**
	 * 
	 * @param comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}

	private void createInitialCommentSection() {// To create comment section
												// before appending the new
												// method
		beforeComment = beforeComment + "\n/**" + "\n" + comments + "\n \n";
		String parName = null;
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				HashMap<String, String> map = (HashMap<String, String>) list.get(i);

				parName = (String) map.get(KEY_NAME);

				if (parName != null) {
					beforeComment = beforeComment + "@param" + " " + parName + "\n";

				}
			}
			// if return type of the method is void then append only "return" in
			// the comment section else append along with its returntype
		}
		if (returnType.equals("void")) {
			beforeComment = beforeComment + "\n" + "*/\n";
		} else {
			beforeComment = beforeComment + "@return" + "\n" + "*/ \n";
		}

	}

	public String createMethod() {// to create the result method which has to be
									// appended //public final int count() {

		annotation = (annotation == "none") ? "" : "@" + annotation + "\n";
		accessSpecifier = (accessSpecifier == "default") ? "" : accessSpecifier + " ";
		accessModifier = (accessModifier == "default") ? "" : accessModifier + " ";
		createInitialCommentSection();
		resultMethod = beforeComment + annotation + accessSpecifier + accessModifier + returnType + " " + name + "(";

		saveCustomReturnType();

		String val = null;
		String parName = null;
		String parType = null;
		if (list != null && list.size() != 0) {
			HashMap<String, String> map = (HashMap<String, String>) list.get(0);
			parName = map.get(KEY_NAME);
			parType = map.get(KEY_TYPE);
			if (parName != null && parType != null) {
				resultMethod = resultMethod + parType + " " + parName;

			}
		}
		if (list != null) {
			for (int i = 1; i < list.size(); i++) {
				HashMap<String, String> map = (HashMap<String, String>) list.get(i);

				parName = (String) map.get(KEY_NAME);
				parType = (String) map.get(KEY_TYPE);
				if (parName != null && parType != null) {
					val = ", " + parType + " " + parName;
					resultMethod = resultMethod + val;
				}

			}
		}
		resultMethod = resultMethod + ") {\n" + body;
		if (returnType != "void") {
			resultMethod += "\n return null;\n}";
		} else
			resultMethod += "\n}";
		return resultMethod;
	}

	/*
	 * Method to save custom returnTypes
	 */
	private void saveCustomReturnType() {
		try {
			String customReturnType = null;
			if (isReturnTypeCustom == true)
				customReturnType = getReturnType();
			BufferedWriter out = new BufferedWriter(
					new FileWriter("C:\\Users\\I323305\\Desktop\\Customtypes.txt", true));
			out.write("\r\n" + customReturnType);
			// out.newLine();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public String[] getCustomReturnType() { Filereader fileReader = new
	 * Filereader("C:\\Users\\I323305\\Desktop\\Customtypes.txt"); String
	 * content = fileReader.readContents(); String[] dataTypes =
	 * content.split("\n"); return dataTypes; }
	 */

	/*
	 * public static void main(String args[]) {
	 * 
	 * HashMap<String, String> map1 = new HashMap<String, String>();
	 * map1.put("name", "par1"); map1.put("type", "int");
	 * 
	 * HashMap<String, String> map2 = new HashMap<String, String>();
	 * map2.put("name", "par2"); map2.put("type", "float");
	 * 
	 * HashMap<String, String> map3 = new HashMap<String, String>();
	 * map3.put("name", "par3"); map3.put("type", "Long");
	 * 
	 * HashMap<String, String> map4 = new HashMap<String, String>();
	 * map4.put("name", "par4"); map4.put("type", "String");
	 * 
	 * ArrayList<HashMap<String, String>> testlist = new
	 * ArrayList<HashMap<String, String>>(); testlist.add(map1);
	 * testlist.add(map2); testlist.add(map3); testlist.add(map4);
	 * 
	 * FileInfo info = new FileInfo(); info.setAccessModifier("final");
	 * info.setAccessSpecifier("public"); info.setName("testmethod");
	 * info.setReturnType("void"); info.setList(testlist);
	 * 
	 * info.createMethod(); }
	 */
}
