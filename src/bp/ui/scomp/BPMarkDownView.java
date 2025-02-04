package bp.ui.scomp;

import bp.util.MarkdownUtil;

public class BPMarkDownView extends BPHTMLView
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3964306515382021423L;

	public BPMarkDownView()
	{
	}

	public void setMD(String text)
	{
		if (text == null || text.length() == 0)
		{
			setText("");
			return;
		}
		setHTML(MarkdownUtil.fix_md2html_swing(MarkdownUtil.md2html(text)));
	}
}
