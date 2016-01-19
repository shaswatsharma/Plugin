package com.plugin.util;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.plugin.pojo.MethodDetails;
import com.plugin.pojo.ParameterDetails;

public class ClassParser {

	private int count;
	private LineNumberReader lr;
	private static ClassParser methodfile;
	private String content, line;

	private ArrayList<MethodDetails> list;

	public ClassParser(String path) {
		content = path;
		readLines();
		// printlinenumbers();
	}

	private void readLines() {
		try {
			String body = new String();
			list = new ArrayList<MethodDetails>();
			MethodDetails methodDetails = new MethodDetails();
			lr = new LineNumberReader(new StringReader(content));
			boolean flag = true;
			boolean bodyflag = false;

			// traverse until the end of the file
			while ((line = lr.readLine()) != null) {

				// initialize count=0 wen u find " class " for the first
				// time..Indicates beginning of class
				if (line.contains(" class ") && !line.contains("//") && flag && !line.contains("\" class \"")) {
					System.out.println("In line no" + lr.getLineNumber() + " class begins");
					count = 0;
				}

				if (bodyflag) {
					if (body == null)
						body = line;
					else {
						body = body + "\n";
						body = body + line;
					}
				}

				if (line.contains("/*")) {
					flag = false;
				}

				if (line.contains("*/")) {
					flag = true;
				}

				if (line.contains("{") && count != 0 && !line.contains("//") && flag) {
					count++;
				}
				if (line.contains("}") && count != 0 && !line.contains("//") && flag) {
					count--;
					// if count becomes "0" then that will mark the end of the
					// method, so add the current_line_number,and the body of
					// the method to the map
					if (count == 0) {
						methodDetails.setEndingIndex(String.valueOf(lr.getLineNumber()));
						// next line will help to remove the final
						// closingbrace"}" from body section and includes
						body = body.substring(0, body.length() - 1);
						methodDetails.setBody(body);
						body = null;
						bodyflag = false;
						list.add(methodDetails);
					}
				}

				/*
				 * For first "{" inside class
				 */
				if (line.contains("{") && (!line.contains("=")) && (!line.contains(" class ")) && count == 0
						&& !line.contains("//") && flag) {

					MethodDetails md = new MethodDetails();
					md.setStartingIndex(String.valueOf(lr.getLineNumber()));
					methodDetails = md;
					count++;
					if (count == 1) {
						bodyflag = true;
					}
					// String[] parts;
					String[] parts = splitByThrows(line, md);

					parseLeftpart(parts[0], md);
					parseRightPart(parts[1], md);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// method used to obtain all the details stored in the map.
	private void printlinenumbers() {
		int i = 0;
		MethodDetails methodDetails;
		System.out.println(list.size());
		while (i < list.size()) {
			methodDetails = list.get(i);
			System.out.println("Starting Index : " + methodDetails.getStartingIndex() + "\nEnd Index : "
					+ methodDetails.getEndingIndex() + "\nAccess Specifier : " + methodDetails.getSpecifier()
					+ "\nAcess Modifier : " + methodDetails.getModifier() + "\nMethod Name : " + methodDetails.getName()
					+ "\nMethod Exception : " + methodDetails.getException() + "\nMethod Body : "
					+ methodDetails.getBody());
			ParameterDetails parameterDetails = methodDetails.getParameters();
			System.out.println("\n Parameter Type 1 : " + parameterDetails.getParameterType1()
					+ "\n Parameter Name 1 : " + parameterDetails.getParameterName1() + "\n Parameter Type 2 : "
					+ parameterDetails.getParameterType2() + "\n Parameter Name 2 : "
					+ parameterDetails.getParameterName2() + "\n Parameter Type 3 : "
					+ parameterDetails.getParameterType3() + "\n Parameter Name 3 : "
					+ parameterDetails.getParameterName3() + "\n Parameter Type 4 : "
					+ parameterDetails.getParameterType4() + "\n Parameter Name 4 : "
					+ parameterDetails.getParameterName4() + "\n Parameter Type 5 : "
					+ parameterDetails.getParameterType5() + "\n Parameter Name 5 : "
					+ parameterDetails.getParameterName5());

			// System.out.println("starting index: " +
			// Integer.valueOf(map.get(StartIndex)) + " ending index :"
			// + Integer.valueOf(map.get(EndIndex)) + " accessSpecifier=" +
			// map.get(accessSpecifier)
			// + " accessmodifier=" + map.get(accessmodifier) + " returnType=" +
			// map.get(returnType)
			// + " methodName=" + map.get(methodName) + "\n firstParameterType="
			// + map.get(parameterType1)
			// + " firstParameter=" + map.get(parameter1) + "\n
			// secondParameterType=" + map.get(parameterType2)
			// + " secondParameter=" + map.get(parameter2) + "\n
			// thirdParameterType=" + map.get(parameterType3)
			// + " thirdParameter=" + map.get(parameter3) + "\n
			// fourthParameterType=" + map.get(parameterType4)
			// + " fourthParameter=" + map.get(parameter4) + "\n
			// fifthParameterType=" + map.get(parameterType5)
			// + " fifthParameter=" + map.get(parameter5) + "\n
			// ExceptionThrown=" + map.get(exceptionThrown)
			// + "\n methodBody=" + map.get(methodBody));
			i++;
		}
	}

	// checks if their is any "throws" keyword in the currrent line
	private String[] splitByThrows(String line, MethodDetails methodDetails) {
		String[] parts;
		// if their is any "throws" keyword then split the current line using it
		// and use the left_String to obtain the method details
		// and right_String to obtain the "type of Exception" thrown by that
		// method

		if (line.contains("throws")) {
			String[] throwsSplit = line.split("throws");
			parts = throwsSplit[0].split("\\(");
			methodDetails.setException(throwsSplit[1].replaceAll("[^a-zA-Z0-9]", ""));
		}
		// And if their is no "throws" keyword, then split the line using "(" to
		// seperate the paremeter_section and method_name section
		else {
			parts = line.split("\\(");
		}
		return parts;
	}

	private void parseLeftpart(String leftSubString, MethodDetails methodDetails) {
		StringTokenizer str;
		if (leftSubString != null) {
			str = new StringTokenizer(leftSubString);

			while (str.hasMoreTokens()) {
				boolean value = false;
				String temp = null;
				switch (temp = str.nextToken()) {
				case "public":
				case "private":
				case "protected":
					methodDetails.setSpecifier(temp);
					value = true;
					break;

				case "static":
				case "final":
				case "synchronized":
					methodDetails.setModifier(temp);
					value = true;
					break;

				default:
					break;

				}
				if (value == true)
					continue;

				if (methodDetails.getReturnType() == null) {
					methodDetails.setReturnType(temp);
					continue;
				}

				if (methodDetails.getName() == null) {
					methodDetails.setName(temp);
					;
					continue;
				}

			}
		}
	}

	private void parseRightPart(String rightSubString, MethodDetails methodDetails) {
		StringTokenizer str;
		// parsing the second part of the string
		ParameterDetails parameterDetails = new ParameterDetails();
		if (rightSubString != null) {
			// if this method has only one parameter, then retrive the
			// parameterType and parameterName and store it in map
			if (!rightSubString.contains(",")) {
				str = new StringTokenizer(rightSubString);
				String temp = null;
				while (str.hasMoreTokens()) {
					temp = str.nextToken();
					// retrieving the one and only parameterType
					if (parameterDetails.getParameterType1() == null) {
						parameterDetails.setParameterType1(temp.replaceAll("[^a-zA-Z0-9]", ""));
						continue;
					}
					// retrieving the one and only parameter
					if (parameterDetails.getParameterName1() == null) {
						parameterDetails.setParameterName1(temp.replaceAll("[^a-zA-Z0-9]", ""));
						continue;
					}
				}
				methodDetails.setTotalParameters(1);
			}
			// if their are more than one parameters
			else {
				// split it using "," and get Strings of the form "parameterType
				// parameterValue"
				String[] secondHalf = rightSubString.split("\\,");

				for (String param : secondHalf) {
					// Now split these strings using " " and obtain
					// parameterType and parameterValue separately
					str = new StringTokenizer(param);
					String temp = null;
					while (str.hasMoreTokens()) {
						temp = str.nextToken();
						// for parameter1
						if (parameterDetails.getParameterType1() == null) {
							parameterDetails.setParameterType1(temp.replaceAll("[^a-zA-Z0-9]", ""));
							continue;
						}

						if (parameterDetails.getParameterName1() == null) {
							parameterDetails.setParameterName1(temp.replaceAll("[^a-zA-Z0-9]", ""));
							continue;
						}
						// for parameter2
						if (parameterDetails.getParameterType2() == null) {
							parameterDetails.setParameterType2(temp.replaceAll("[^a-zA-Z0-9]", ""));
							continue;
						}

						if (parameterDetails.getParameterName2() == null) {
							parameterDetails.setParameterName2(temp.replaceAll("[^a-zA-Z0-9]", ""));
							continue;
						}
						// for parameter3
						if (parameterDetails.getParameterType3() == null) {
							parameterDetails.setParameterType3(temp.replaceAll("[^a-zA-Z0-9]", ""));
							continue;
						}

						if (parameterDetails.getParameterName3() == null) {
							parameterDetails.setParameterName3(temp.replaceAll("[^a-zA-Z0-9]", ""));
							continue;
						}
						// for parameter4
						if (parameterDetails.getParameterType4() == null) {
							parameterDetails.setParameterType4(temp.replaceAll("[^a-zA-Z0-9]", ""));
							continue;
						}

						if (parameterDetails.getParameterName4() == null) {
							parameterDetails.setParameterName4(temp.replaceAll("[^a-zA-Z0-9]", ""));
							continue;
						}
						// for parameter5
						if (parameterDetails.getParameterType5() == null) {
							parameterDetails.setParameterType5(temp.replaceAll("[^a-zA-Z0-9]", ""));
							continue;
						}

						if (parameterDetails.getParameterName5() == null) {
							parameterDetails.setParameterName5(temp.replaceAll("[^a-zA-Z0-9]", ""));
							continue;
						}
					}
				}
				methodDetails.setTotalParameters(secondHalf.length);
			}
			methodDetails.setParameters(parameterDetails);
		}
	}

	public static void main(String[] args) {
		methodfile = new ClassParser("C:\\Users\\I323334\\Desktop\\Validate.java");
	}

	public ArrayList<MethodDetails> getAllMethodDetails() {
		return list;
	}
}
