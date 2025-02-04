package bp.ext;

import java.awt.Window;
import java.lang.ref.WeakReference;

import javax.swing.Action;

import bp.res.BPResource;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceFileLocal;
import bp.ui.actions.BPAction;
import bp.ui.editor.BPMarkDownPanel;
import bp.ui.res.icon.BPIconResV;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIStd;
import bp.util.MarkdownUtil;
import bp.util.TextUtil;

public class BPExtensionActionMarkDown
{
	public final static Action getExportHTMLAction(BPMarkDownPanel panel)
	{
		WeakReference<BPMarkDownPanel> ref = new WeakReference<BPMarkDownPanel>(panel);
		return BPAction.build("Export HTML").callback((e) ->
		{
			String html = MarkdownUtil.fix_md2html_export(MarkdownUtil.md2html(ref.get().getTextPanel().getText()));
			String presetfilename = null;
			BPResource res = ref.get().getDataContainer().getResource();
			if (res != null && res.isFileSystem() && ((BPResourceFileSystem) res).exists())
			{
				BPResourceFile resfs = (BPResourceFile) res;
				String oldfilename = resfs.getFileFullName();
				String ext = resfs.getExt();
				presetfilename = oldfilename.substring(0, oldfilename.length() - ext.length()) + ".html";
			}
			String filename = CommonUIOperations.showSaveFileDialog((Window) panel.getTopLevelAncestor(), presetfilename);
			if (filename != null)
			{
				BPResourceFileLocal nres = new BPResourceFileLocal(filename);
				Exception e3 = nres.useOutputStream((out) ->
				{
					try
					{
						out.write(TextUtil.fromString(html, "utf-8"));
					}
					catch (Exception e2)
					{
						return e2;
					}
					return null;
				});
				if (e3 != null)
				{
					UIStd.err(e3);
				}
			}
		}).vIcon(BPIconResV.CLONE()).name("Export HTML").tooltip("Export HTML").getAction();
	}
}
