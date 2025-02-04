package bp.ui.view;

import bp.project.BPResourceProject;
import bp.project.BPResourceProjectNotes;

public class BPProjectOverviewCompFactoryNotes implements BPProjectOverviewCompFactory<BPResourceProjectNotes>
{
	public BPProjectOverviewComp<BPResourceProjectNotes> create(BPResourceProjectNotes prj)
	{
		BPProjectOverviewPanelNotes rc = new BPProjectOverviewPanelNotes();
		rc.setup(prj);
		return rc;
	}

	public boolean check(BPResourceProject prj)
	{
		return prj instanceof BPResourceProjectNotes;
	}
}
