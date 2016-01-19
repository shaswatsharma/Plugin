package firstplugin.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.plugin.util.ClassParser;

public class JUnitHandler extends AbstractHandler {
	String activeProjectName;
	IProject activeProject;

	private IWorkbenchWindow window;
	private IWorkbenchPage activePage;

	private IProject theProject;
	private IResource theResource;
	private IFile theFile;

	private String workspaceName;
	private String projectName;
	private String fileName;
	/**
	 * The constructor.
	 */
	public JUnitHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.getActiveEditor();

		if (editor == null)
			return null;

		if (!(editor instanceof ITextEditor))
			return null;
		ITextEditor ite = (ITextEditor) editor;
		IDocument doc = ite.getDocumentProvider().getDocument(ite.getEditorInput());

		String message = createJUnitCases(doc.get());
		MessageDialog.openInformation(window.getShell(), "FirstPlugin", doc.get());

		createNewFile();


		return null;
	}

	public void createNewFile() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();

		System.out.println(projects[0]);

		activeProject = root.getProject("tets");

		IFile file = activeProject.getFile("testing.java");
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
