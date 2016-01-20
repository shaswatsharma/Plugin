package firstplugin.handlers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.plugin.pojo.MethodDetails;
import com.plugin.util.ClassParser;
import com.plugin.util.MethodParser;

public class JUnitHandler extends AbstractHandler {
	String activeProjectName;
	IProject activeProject;
	IWorkbenchPage page;
	IEditorPart editor;

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

		page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		editor = page.getActiveEditor();
		String fileName = page.getActiveEditor().getEditorInput().getName();

		if (editor == null)
			return null;

		if (!(editor instanceof ITextEditor))
			return null;
		ITextEditor ite = (ITextEditor) editor;
		IDocument doc = ite.getDocumentProvider().getDocument(ite.getEditorInput());
		String classBody = createJUnitCases(doc.get());
		System.out.println(classBody);

		boolean isCreated;
		try {
			createNewFile(fileName, classBody);
			isCreated = true;
		} catch (ResourceException e1) {
			isCreated = false;
		}

		if (!isCreated) {
			IFile file = activeProject.getFile("Test" + fileName);
			try {
				InputStream is = file.getContents();
				// existing JUnit class body
				String existingClassBody = getStringFromInputStream(is);
				System.out.println(existingClassBody);
				/*
				 * // Adding New JUnits for Existing Method's new conditions
				 * ArrayList<MethodDetails> newMethods =
				 * addJUnitsForNewConditions(classBody, existingClassBody); if
				 * (newMethods != null) { existingClassBody =
				 * existingClassBody.substring(0,
				 * existingClassBody.trim().length() - 1); existingClassBody =
				 * existingClassBody + createNewJUnitCases(newMethods) + "\n}";
				 * is = new ByteArrayInputStream(existingClassBody.getBytes());
				 * file.setContents(is, true, true, null);
				 * 
				 * MessageDialog.openInformation(window.getShell(),
				 * "FirstPlugin", "New test methods added to existing " +
				 * fileName + "Test JUnit Class."); }
				 * 
				 */

				ArrayList<MethodDetails> newMethods = checkForNewMethods(classBody, existingClassBody);
				String newClassBody = "";
				// Adding New Methods
				if (newMethods != null) {
					existingClassBody = existingClassBody.substring(0, existingClassBody.trim().length() - 1);
					newClassBody = existingClassBody + createNewJUnitCases(newMethods) + "\n}";
					is = new ByteArrayInputStream(newClassBody.getBytes());
					file.setContents(is, true, true, null);

					MessageDialog.openInformation(window.getShell(), "FirstPlugin",
							"New test methods added to existing " + fileName + "Test JUnit Class.");
				}

			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	private ArrayList<MethodDetails> addJUnitsForNewConditions(String classBody, String newClassBody) {
		ClassParser classParser = new ClassParser(classBody);
		ArrayList<MethodDetails> changedJUnitMethods = classParser.getAllMethodDetails();

		classParser = new ClassParser(newClassBody);
		ArrayList<MethodDetails> testClassMethods = classParser.getAllMethodDetails();

		ArrayList<MethodDetails> newMethods = new ArrayList<MethodDetails>();

		for (int i = 0; i < changedJUnitMethods.size(); i++) {
			boolean isNew = true;
			for (int j = 0; j < testClassMethods.size(); j++) {
				if (changedJUnitMethods.get(i).getName().equals(testClassMethods.get(j).getName()))
					isNew = false;
			}
			if (isNew) {
				changedJUnitMethods.get(i).setName(changedJUnitMethods.get(i).getName().substring(0,
						changedJUnitMethods.get(i).getName().trim().length() - 4));
				newMethods.add(changedJUnitMethods.get(i));
			}
		}

		if (newMethods.size() > 0)
			return newMethods;
		else
			return null;

	}

	private ArrayList<MethodDetails> checkForNewMethods(String newContent, String oldContent) {
		ClassParser classParser = new ClassParser(newContent);
		ArrayList<MethodDetails> newList = classParser.getAllMethodDetails();

		classParser = new ClassParser(oldContent);
		ArrayList<MethodDetails> oldList = classParser.getAllMethodDetails();
		ArrayList<MethodDetails> newMethods = new ArrayList<MethodDetails>();
		for (int i = 0; i < newList.size(); i++) {
			boolean isNew = true;
			for (int j = 0; j < oldList.size(); j++) {
				if (newList.get(i).getName().equals(oldList.get(j).getName()))
					isNew = false;
			}
			if (isNew) {
				newList.get(i).setName(newList.get(i).getName().substring(0, newList.get(i).getName().trim().length()));
				newMethods.add(newList.get(i));
			}
		}
		if (newMethods.size() > 0)
			return newMethods;
		else
			return null;

	}

	// convert InputStream to String
	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

	public void createNewFile(String fileName, String classBody) throws ResourceException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();

		System.out.println(projects[0]);

		activeProject = root.getProject("tets");

		IFile file = activeProject.getFile("Test" + fileName);
		String contents = "public class Test" + fileName.replace(".java", "") + " { \n\n" + classBody + "\n}";
		InputStream source = new ByteArrayInputStream(contents.getBytes());
		try {
			file.create(source, false, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private IFile getActiveIFile() {
		IEditorInput input = editor.getEditorInput();
		IFile original = (input instanceof IFileEditorInput) ? ((IFileEditorInput) input).getFile() : null;
		return original;
	}

	public String createJUnitCases(String content) {

		ClassParser classParser = new ClassParser(content);
		ArrayList<MethodDetails> list = classParser.getAllMethodDetails();
		int i = 0;
		String classBody = "";
		while (i < list.size()) {
			MethodDetails methodDetails = list.get(i);

			MethodParser methodParser = new MethodParser();
			classBody += methodParser.generateJUnitMethod(methodDetails);

			i++;
		}
		return classBody;
	}

	public String createNewJUnitCases(ArrayList<MethodDetails> list) {

		int i = 0;
		String classBody = "";
		while (i < list.size()) {
			MethodDetails methodDetails = list.get(i);

			MethodParser methodParser = new MethodParser();
			classBody += methodParser.generateJUnitMethod(methodDetails);

			i++;
		}
		return classBody;

	}

}
