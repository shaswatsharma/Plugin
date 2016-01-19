package firstplugin.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class JUnitBuilder extends IncrementalProjectBuilder {

	@Override
	protected IProject[] build(int arg0, Map arg1, IProgressMonitor arg2) throws CoreException {
		IProject[] iProjects = new IProject[1];
		iProjects[0] = getProject();
		return iProjects;
	}

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

}
