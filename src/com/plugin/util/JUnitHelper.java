package com.plugin.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class JUnitHelper {

	public void createNewFile() {
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject iProject = myWorkspaceRoot.getProject();
		IFile file = iProject.getFile("test.java");
		String contents = "Whatever";
		InputStream source = new ByteArrayInputStream(contents.getBytes());
		try {
			file.create(source, false, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public String createJUnitCases(String content) {

		ClassParser classParser = new ClassParser(content);
		return null;
	}
}
