package com.plugin.validator;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.plugin.util.Constants;

public class NewCommentValidator {

	private int charCount = 0, lastLineCharCount = 0;;
	private List<Map<String, Integer>> list;
	private String currentLine = "";
	private boolean classFlag = false;
	private boolean commentFlag = false;
	private int startChar, endChar, startLine, endLine, uncommentedLineCount = 0;

	public List<Map<String, Integer>> checkComments(String content) {
		list = new ArrayList<Map<String, Integer>>();
		LineNumberReader lr = new LineNumberReader(new StringReader(content));
		/*
		 * Read characters and add to toal count in "charCount" variable
		 */

		try {
			while ((currentLine = lr.readLine()) != null) {
				// +2 for \n which does not get counted
				charCount += currentLine.length() + 2;
				System.out.println(currentLine.length());

				if (currentLine.contains("class")) {
					classFlag = true;
					startChar = charCount;
					// +1 to point to correct line number
					startLine = lr.getLineNumber() + 1;
					continue;
				}

				if (classFlag) {
					// replacement is done to avoid counting empty lines of code
					// even with braces
					if (currentLine.replace("\n", "").replace("\t", "").replace(" ", "").replace("}", "")
							.replace("{", "").length() != 0) {

						if (currentLine.contains("//")) {
							endChar = lastLineCharCount - 2;
							endLine = lr.getLineNumber() - 1;

							Map<String, Integer> map = new HashMap<String, Integer>();
							map.put(Constants.KEY_CHAR_START, startChar);
							map.put(Constants.KEY_CHAR_END, endChar);
							map.put(Constants.KEY_COUNT, uncommentedLineCount);
							map.put(Constants.KEY_START, startLine);
							map.put(Constants.KEY_END, endLine);
							list.add(map);

							startChar = charCount;
							startLine = lr.getLineNumber() + 1;
							uncommentedLineCount = 0;

						} else if (currentLine.contains("/*")) {
							commentFlag = true;
							// -2 to remove \n so that Code selection reaches
							// last line above comment
							endChar = charCount - currentLine.length() - 2;
							// -1 to point to correct line number
							endLine = lr.getLineNumber() - 1;
							Map<String, Integer> map = new HashMap<String, Integer>();
							map.put(Constants.KEY_CHAR_START, startChar);
							map.put(Constants.KEY_CHAR_END, endChar);
							map.put(Constants.KEY_COUNT, uncommentedLineCount);
							map.put(Constants.KEY_START, startLine);
							map.put(Constants.KEY_END, endLine);
							list.add(map);
						} else if (currentLine.contains("*/")) {
							commentFlag = false;
							startChar = charCount;
							// +1 to point to correct line number
							startLine = lr.getLineNumber() + 1;
							uncommentedLineCount = 0;
						} /*
							 * else if (!commentFlag &&
							 * currentLine.contains("//")) { if
							 * (uncommentedLineCount > 5) { Map<String, Integer>
							 * map = new HashMap<String, Integer>(); endChar =
							 * charCount; endLine = lr.getLineNumber();
							 * 
							 * map.put(Constants.KEY_CHAR_START, startChar);
							 * map.put(Constants.KEY_CHAR_END, endChar);
							 * map.put(Constants.KEY_COUNT,
							 * uncommentedLineCount);
							 * map.put(Constants.KEY_START, startLine);
							 * map.put(Constants.KEY_END, endLine);
							 * uncommentedLineCount = 0; startChar = charCount;
							 * } else { uncommentedLineCount = 0; } }
							 */
						else if (!commentFlag)
							uncommentedLineCount++;

					}
				}
				lastLineCharCount = charCount;
			}
			if (list.size() == 0 || uncommentedLineCount > 5) {
				// When there is no comment
				Map<String, Integer> map = new HashMap<String, Integer>();
				endChar = charCount;
				endLine = lr.getLineNumber();
				map.put(Constants.KEY_CHAR_START, startChar);
				map.put(Constants.KEY_CHAR_END, endChar - 2);
				map.put(Constants.KEY_COUNT, uncommentedLineCount);
				map.put(Constants.KEY_START, startLine);
				map.put(Constants.KEY_END, endLine);
				list.add(map);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;

	}

}
