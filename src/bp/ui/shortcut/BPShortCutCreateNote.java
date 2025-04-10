package bp.ui.shortcut;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import bp.util.TextUtil;

public class BPShortCutCreateNote extends BPShortCutBase
{
	protected final static String SC_KEY_PRJNAME = "projectname";
	protected final static String SC_KEY_FN = "fn";
	protected final static String SC_KEY_PREFIX = "prefix";

	public final static String SCKEY_NOTE = "Create Note";

	public boolean run()
	{
		String projectname = TextUtil.eds(getParam(SC_KEY_PRJNAME));
		String dd = DateUtil.formatTime(System.currentTimeMillis(), "yyyyMMdd-HHmm");
		String fn = getParam(SC_KEY_FN, "MarkDown");
		String prefix = TextUtil.eds(getParam(SC_KEY_PREFIX));
		String newresname;
		if (prefix != null)
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
		rc.addItem(BPSettingItem.create(SC_KEY_PRJNAME, "Project(Notes) Name", BPSettingItem.ITEM_TYPE_SELECT, prjnames.toArray(new String[prjnames.size()])).setRequired(true));
		rc.addItem(BPSettingItem.create(SC_KEY_FN, "Function", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create(SC_KEY_PREFIX, "Prefix", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.setAll(m_params);
		return rc;
	}

	public void setSetting(BPSetting setting)
	{
		super.setSetting(setting);
		m_params = setParamsFromSetting(new LinkedHashMap<String, Object>(), setting, true, false, SC_KEY_PRJNAME, SC_KEY_FN, SC_KEY_PREFIX);
	}

	public String getShortCutKey()
	{
		return SCKEY_NOTE;
	}

	protected String[] getParamKeys()
	{
		return new String[] { SC_KEY_PRJNAME, SC_KEY_FN, SC_KEY_PREFIX };
	}
}
