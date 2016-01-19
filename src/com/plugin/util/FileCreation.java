package com.plugin.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileCreation {
	private static File newfile;
	private String filename, path;
	private String resultFileName = null;
	private static FileCreation file;
	private Boolean bool;
	private String accessSpecifier;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getAccessSpecifier() {
		return accessSpecifier;
	}

	public void setAccessSpecifier(String accessSpecifier) {
		this.accessSpecifier = accessSpecifier;
	}

	public void createFile() {
		this.resultFileName = filename + ".java";
		// createNewFile();
		if (createNewFile() == true) {
			addBasicSyntax();
		} else {
			System.out.println("File with this name in this path already exists");
		}

	}

	private Boolean createNewFile() {// method that create new file

		try {
			newfile = new File(path + resultFileName);
			bool = newfile.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bool;
	}

	private void addBasicSyntax() {
		String resultClass = accessSpecifier + " class" + " " + filename + " {\n\n" + "}";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(path + resultFileName));
			out.write(resultClass);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public static void main(String args[]) { file = new
	 * FileCreation("userFile");
	 * 
	 * }
	 */}