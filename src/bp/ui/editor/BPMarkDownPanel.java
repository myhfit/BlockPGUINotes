package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.data.BPTextContainer;
import bp.data.BPTextContainerBase;
import bp.format.BPFormat;
import bp.format.BPFormatMarkdown;
import bp.res.BPResource;
import bp.res.BPResourceFile;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceFileLocal;
import bp.ui.scomp.BPMarkDownCodePane;
import bp.ui.scomp.BPMarkDownView;
import bp.ui.scomp.BPEditorPane;
import bp.ui.scomp.BPTextPane;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIUtil;
import bp.util.IOUtil;
import bp.util.MarkdownUtil;
import bp.util.Std;
import bp.util.TextUtil;

public class BPMarkDownPanel extends BPTextPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5642804718744148187L;

	protected BPMarkDownView m_preview;
	protected Consumer<BPEditorPane> m_changedhandler;
	protected AtomicBoolean m_changed = new AtomicBoolean(false);
	protected boolean m_istextconchange = false;
	protected JScrollPane m_psrc;
	protected JScrollPane m_pdest;
	protected boolean m_canpreview = true;
	protected JPanel m_sp;

	public BPMarkDownPanel()
	{
	}

	protected void init()
	{
		setLayout(new BorderLayout());
		JPanel sp = new JPanel();
		m_sp = sp;
		sp.setLayout(new GridLayout(1, 2, 0, 0));
		m_scroll = new JScrollPane();
		m_psrc = m_scroll;
		m_pdest = new JScrollPane();

		m_txt = createTextPane();
		m_changedhandler = this::onTextChanged;
		m_preview = new BPMarkDownView();

		m_txt.setMonoFont();
		m_preview.setHTMLFont();

		m_psrc.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_pdest.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));

		sp.add(m_psrc);
		sp.add(m_pdest);

		m_psrc.setViewportView(m_txt);
		m_pdest.setViewportView(m_preview);

		m_psrc.getVerticalScrollBar().addAdjustmentListener(this::onPScrollChanged);

		initActions();
		add(sp, BorderLayout.CENTER);

		m_txt.setOnPosChanged(this::onPosChanged);
		m_txt.resizeDoc();
		m_txt.setChangedHandler(m_changedhandler);
	}

	protected void onTextChanged(BPEditorPane comp)
	{
		m_changed.set(true);
		UIUtil.laterUI(() ->
		{
			if (m_changed.compareAndSet(true, false))
			{
				m_preview.setMD(comp.getText());
				syncScrollPos(true);
			}
		});
	}

	protected void syncScrollPos(boolean forward)
	{
		if (forward)
		{
			int s = m_psrc.getVerticalScrollBar().getValue();
			int m = m_psrc.getVerticalScrollBar().getMaximum();
			int news = (int) ((float) m_pdest.getVerticalScrollBar().getMaximum() / (float) m * (float) s);
			if (news >= 0)
				m_pdest.getViewport().setViewPosition(new Point(0, news));
		}
	}

	protected void onPScrollChanged(AdjustmentEvent e)
	{
		syncScrollPos(true);
	}

	protected BPTextPane createTextPane()
	{
		return new BPMarkDownCodePane();
	}

	public void toggleRightPanel()
	{
		boolean canpreview = !m_canpreview;
		m_canpreview = canpreview;
		if (canpreview)
		{
			m_sp.add(m_pdest);
			// onTextChanged(m_txt);
		}
		else
		{
			m_sp.remove(m_pdest);
		}
		m_sp.validate();
	}

	public final static class BPEditorFactoryMarkDown implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatMarkdown.FORMAT_MARKDOWN };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPMarkDownPanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res.isFileSystem() && ((BPResourceFileSystem) res).isFile())
			{
				BPTextContainer con = new BPTextContainerBase();
				con.bind(res);
				((BPMarkDownPanel) editor).bind(con);
			}
		}

		public String getName()
		{
			return "MarkDown Editor";
		}
	}

	public final static class BPEditorFactoryMarkDown2ExtBrowser implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatMarkdown.FORMAT_MARKDOWN };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			if (res.isFileSystem() && ((BPResourceFileSystem) res).isFile())
			{
				BPResourceFile fres = (BPResourceFile) res;
				try
				{
					String txt = fres.useInputStream((in) ->
					{
						return TextUtil.toString(IOUtil.read(in), "utf-8");
					});
					if (txt != null)
					{
						File ftmp = File.createTempFile("mdv", ".html");
						String html = MarkdownUtil.md2html(txt).replace("<table>", "<table border=1 cellspacing='0' cellpadding='2'>");
						BPResourceFileLocal lf = new BPResourceFileLocal(ftmp);
						boolean success = lf.useOutputStream(out ->
						{
							IOUtil.write(out, TextUtil.fromString(html, "utf-8"));
							return true;
						});
						if (success)
							CommonUIOperations.openExternal(lf);
					}
				}
				catch (IOException e)
				{
					Std.err(e);
				}
			}
			return null;
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
		}

		public String getName()
		{
			return "MarkDown Viewer(External WebBrowser)";
		}

		public boolean checkSameTab()
		{
			return false;
		}
	}

	public void clearResource()
	{
		m_preview.clearResource();
		super.clearResource();
	}

	public void activeEditor()
	{
		m_txt.resizeDoc();
	}
}