package bp.ext;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import bp.project.BPResourceProjectNotes;
import bp.ui.editor.BPEditorActionManager;
import bp.ui.editor.BPMarkDownPanel;
import bp.ui.view.BPProjectOverviewCompFactoryNotes;
import bp.ui.view.BPProjectOverviewManager;
import bp.util.Std;

public class BPExtensionLoaderGUINotes implements BPExtensionLoaderGUISwing
{
	public String getName()
	{
		return "Notes GUI-Swing";
	}

	public String[] getParentExts()
	{
		return new String[] { "GUI-Swing", "Notes" };
	}

	public String[] getDependencies()
	{
		return null;
	}

	public final static Action[] getBarActions(BPMarkDownPanel panel)
	{
		List<Action> acts = new ArrayList<Action>();
		try
		{
			acts.add(BPExtensionActionMarkDown.getExportHTMLAction(panel));
		}
		catch (Exception e)
		{
			Std.err(e);
		}
		return acts.toArray(new Action[acts.size()]);
	}

	public void preload()
	{
		BPEditorActionManager.registerBarActionFactories(BPMarkDownPanel.class, BPExtensionLoaderGUINotes::getBarActions);
		BPProjectOverviewManager.register(BPResourceProjectNotes.PRJTYPE_NOTES, new BPProjectOverviewCompFactoryNotes());
	}
}
