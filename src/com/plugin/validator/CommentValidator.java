package com.plugin.validator;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.plugin.util.Constants;

public class CommentValidator {
	public static int READ_LIMIT = 100000;

	public List<Map<String, Integer>> checkComments(String content) {
		List<Map<String, Integer>> list = null;
		try {
			list = new ArrayList<Map<String, Integer>>();
			int i = 0;
			LineNumberReader lr = new LineNumberReader(new StringReader(content));
			String line = null;
			int count = 0;
			int start = 1;
			int endByteCount = 0;
			int startByteCount = 0;
			boolean value = false;
			while (true) {
				Map<String, Integer> map = new HashMap<String, Integer>();
				boolean flag = false;

				if (!value && (line = lr.readLine()) != null) {
					if (line.contains("class ")) {
						value = true;
						start = lr.getLineNumber();
						startByteCount = endByteCount;
					}
					endByteCount += line.length();
				}

				if (value) {
					while ((line = lr.readLine()) != null) {
						endByteCount += line.length();
						if (count != 0 && line.trim().isEmpty()) {
							count--;
						}
						if (line.contains("/*"))
							flag = true;

						if (line.contains("/*") || line.contains("//")) {
							break;
						}

						count++;
					}
					if (count != 0) {
						start++;
						map.put(Constants.KEY_START, start);
						map.put(Constants.KEY_END, lr.getLineNumber() - 1);
						map.put(Constants.KEY_COUNT, count);
						map.put(Constants.KEY_CHAR_START, startByteCount);
						map.put(Constants.KEY_CHAR_END, endByteCount);
						list.add(map);
					}

					while (flag && (line = lr.readLine()) != null && (endByteCount += line.length()) > 0) {
						if (line.contains("*/")) {
							flag = false;
							break;
						}
					}
					start = lr.getLineNumber();
					startByteCount = endByteCount;
					// System.out.println(start + 1000);
					count = 0;
				}
				if (line == null) {
					lr.close();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
