package sap_code_governance_tool.handlers;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.plugin.pojo.ClassDetails;
import com.plugin.pojo.ClassVariableDetails;
import com.plugin.pojo.MethodDetails;
import com.plugin.util.ClassParser;
import com.plugin.util.Constants;
import com.plugin.validator.NewCommentValidator;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SaveHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SaveHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.getActiveEditor();

		if (editor == null)
			return null;

		if (!(editor instanceof ITextEditor))
			return null;

		Constants.ACTIVE_FILE_NAME = editor.getTitle();
		Constants.ACTIVE_FILE_PATH = editor.getTitleToolTip();

		ITextEditor ite = (ITextEditor) editor;
		IDocument doc = ite.getDocumentProvider().getDocument(ite.getEditorInput());

		/*
		 * Checking for Sufficient Comments
		 */
		String message = validateComments(doc.get());

		message += validateArgumentsAndVariables(doc.get());

		if (!message.isEmpty() || !message.equals("")) {
			MessageDialog.openInformation(window.getShell(), "SAP Code Governance", message);
		} else {
			page.saveEditor(editor, false);

		}

		return null;
	}

	private String validateArgumentsAndVariables(String content) {
		String message = "";
		ClassParser classParser = new ClassParser(content);
		ClassDetails classDetails = classParser.getClassDetails();

		// Validating Arguments of Methods
		List<MethodDetails> list = classDetails.getMethods();
		for (int i = 0; i < list.size(); i++) {
			MethodDetails methodDetails = list.get(i);
			int params = methodDetails.getParameters().size();
			if (params > 5)
				message += "\nMethod Name \"" + methodDetails.getName() + "\" has " + params
						+ " parameters. Please Reduce it to MAX 5.";
		}

		// Validating Global Variables

		List<ClassVariableDetails> variableList = classDetails.getVariables();
		for (int i = 0; i < variableList.size(); i++) {
			ClassVariableDetails obj = variableList.get(i);
			if (obj.getModifier().trim().equals("final")) {
				if (!isCaps(obj.getName())) {
					message += "\nName of Global final variable \"" + obj.getName() + "\" should be in UpperCase.\n";
				}
			}
		}

		return message;
	}

	private boolean isCaps(String name) {
		for (int i = 0; i < name.length(); i++) {
			if (Character.isLowerCase(name.charAt(i)))
				return false;
		}
		return true;
	}

	private String validateComments(String content) {
		NewCommentValidator commentValidator = new NewCommentValidator();
		List<Map<String, Integer>> list = commentValidator.checkComments(content);
		String message = "You have not entered sufficient comments between Lines \n";
		int count = 0;

		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActivePart();
		IFile file = (IFile) workbenchPart.getSite().getPage().getActiveEditor().getEditorInput()
				.getAdapter(IFile.class);
		if (file == null)
			try {
				throw new FileNotFoundException();
			} catch (FileNotFoundException e) {
			}

		IMarker marker;

		int depth = IResource.DEPTH_INFINITE;
		try {
			file.deleteMarkers(IMarker.TASK, true, depth);
		} catch (CoreException e) {
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String, Integer> map = list.get(i);
			if (map.get(Constants.KEY_COUNT) > 5) {
				count++;
				message += map.get(Constants.KEY_START) + " and " + map.get(Constants.KEY_END) + "\n";

				try {
					marker = file.createMarker(IMarker.TASK);
					marker.setAttribute(IMarker.CHAR_START, map.get(Constants.KEY_CHAR_START));
					marker.setAttribute(IMarker.CHAR_END, map.get(Constants.KEY_CHAR_END));
					marker.setAttribute(IMarker.MESSAGE, "Add comments in this section");
					marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				} catch (CoreException e) {
					e.printStackTrace();
				}

			}

		}
		message += "(Please followup with Blue Block on text editor's right side)\n";
		if (count != 0)
			return message;
		else
			return "";
	}
}
