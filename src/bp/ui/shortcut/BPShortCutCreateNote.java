package bp.ui.shortcut;

import java.util.ArrayList;
import java.util.List;

import bp.BPCore;
import bp.BPGUICore;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.format.BPFormat;
import bp.format.BPFormatManager;
import bp.project.BPResourceProject;
import bp.project.BPResourceProjectNotes;
import bp.res.BPResource;
import bp.util.DateUtil;

public class BPShortCutCreateNote extends BPShortCutBase
{
	public boolean run()
	{
		String[] params = m_params;
		String projectname = params[0];
		String dd = DateUtil.formatTime(System.currentTimeMillis(), "yyyyMMdd-HHmm");
		String fn = "MarkDown";
		if (params.length > 1)
			fn = params[1];
		String prefix = null;
		if (params.length > 2)
			prefix = params[2];
		String newresname;
		if (prefix != null && prefix.length() > 0)
			newresname = prefix + "-" + dd;
		else
			newresname = dd;
		BPFormat format = BPFormatManager.getFormatByName(fn);
		String ext = format.getExts()[0];
		BPResourceProjectNotes prj = (BPResourceProjectNotes) BPCore.getProjectsContext().getProjectByName(projectname);
		if (prj != null)
		{
			BPResource res = prj.createNote(newresname, ext);
			BPGUICore.runOnMainFrame(mf -> mf.openResource(res, format, null, false, null));
		}
		return true;
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = (BPSettingBase) super.getSetting();
		BPResourceProject[] prjs = BPCore.getProjectsContext().listProject();
		List<String> prjnames = new ArrayList<String>();
		for (BPResourceProject prj : prjs)
		{
			if (prj != null && prj instanceof BPResourceProjectNotes)
			{
				prjnames.add(prj.getName());
			}
		}
		rc.addItem(BPSettingItem.create("projectname", "Project(Notes) Name", BPSettingItem.ITEM_TYPE_SELECT, prjnames.toArray(new String[prjnames.size()])).setRequired(true));
		// rc.addItem(BPSettingItem.create("projectname", "Project(Notes) Name",
		// BPSettingItem.ITEM_TYPE_SELECT, prjnames.toArray(new
		// String[prjnames.size()])).setRequired(true));
		rc.set("projectname", getParamValue(0));
		return rc;
	}

	public void setSetting(BPSetting setting)
	{
		super.setSetting(setting);
		String prjname = setting.get("projectname");
		if (prjname == null)
			prjname = "";
		m_params = new String[] { prjname };
	}
}
