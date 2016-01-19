package firstplugin.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import com.plugin.pojo.MethodDetails;
import com.plugin.util.ClassParser;
import com.plugin.util.Constants;
import com.plugin.validator.CommentValidator;;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SampleHandler() {
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
		ITextEditor ite = (ITextEditor) editor;
		IDocument doc = ite.getDocumentProvider().getDocument(ite.getEditorInput());

		/*
		 * Checking for Sufficient Comments
		 */
		String message = validateComments(doc.get());

		message += validateArguments(doc.get());

		if (!message.isEmpty() || !message.equals("")) {
			MessageDialog.openInformation(window.getShell(), "FirstPlugin", message);
		} else {
			page.saveEditor(editor, false);

		}

		return null;
	}

	private String validateArguments(String content) {
		String message = "";
		ClassParser classParser = new ClassParser(content);
		ArrayList<MethodDetails> list = classParser.getAllMethodDetails();
		for (int i = 0; i < list.size(); i++) {
			MethodDetails methodDetails = list.get(i);
			int params = methodDetails.getTotalParameters();
			if (params > 5)
				message += "\nMethod Name \"" + methodDetails.getName() + "\" has " + params
						+ " parameters. Please Reduce it to MAX 5.";
		}
		return message;
	}

	private String validateComments(String content) {
		CommentValidator commentValidator = new CommentValidator();
		List<Map<String, Integer>> list = commentValidator.checkComments(content);
		String message = "You have not entered sufficient comments between Lines \n";
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			Map<String, Integer> map = list.get(i);
			if (map.get(Constants.KEY_COUNT) > 5) {
				count++;
				message += map.get(Constants.KEY_START) + " and " + map.get(Constants.KEY_END) + "\n";

			}
		}
		if (count != 0)
			return message;
		else
			return "";
	}
}
