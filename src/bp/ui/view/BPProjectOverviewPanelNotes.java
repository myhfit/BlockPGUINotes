package bp.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.BPCore;
import bp.cache.BPCacheDataFileSystem;
import bp.cache.BPCacheFileSystem;
import bp.cache.BPTreeCacheNode;
import bp.config.UIConfigs;
import bp.project.BPResourceProjectNotes;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextField;
import bp.ui.util.UIUtil;
import bp.util.ObjUtil;

public class BPProjectOverviewPanelNotes extends JPanel implements BPProjectOverviewComp<BPResourceProjectNotes>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3374967334306688721L;

	protected WeakReference<BPResourceProjectNotes> m_prjref;
	protected JPanel m_pantodo;
	protected JPanel m_pannote;

	public BPProjectOverviewPanelNotes()
	{
		initUI();
	}

	protected void initUI()
	{
		JScrollPane scrolltodo = new JScrollPane();
		JScrollPane scrollnote = new JScrollPane();
		scrolltodo.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollnote.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
		m_pantodo = new JPanel();
		m_pannote = new JPanel();
		m_pantodo.setLayout(new BoxLayout(m_pantodo, BoxLayout.Y_AXIS));
		m_pannote.setLayout(new BoxLayout(m_pannote, BoxLayout.Y_AXIS));
		m_pantodo.setBorder(null);
		m_pannote.setBorder(null);
		m_pannote.setBackground(UIConfigs.COLOR_TEXTBG());

		scrollnote.setViewportView(m_pannote);
		scrolltodo.setViewportView(m_pantodo);

		setLayout(new GridLayout(1, 2, 0, 0));
		add(scrollnote);
		add(scrolltodo);
	}

	public void setup(BPResourceProjectNotes prj)
	{
		m_prjref = new WeakReference<BPResourceProjectNotes>(prj);
		initDatas();
	}

	protected void initDatas()
	{
		m_pantodo.removeAll();
		WeakReference<BPResourceProjectNotes> prjref = m_prjref;
		if (prjref != null)
		{
			BPResourceProjectNotes prj = prjref.get();
			if (prj != null)
			{
				Map<String, Object> overview = prj.getOverview();
				int todolistcount = ObjUtil.toInt(overview.get("count_todolist"), 0);
				int todoitemcount = ObjUtil.toInt(overview.get("count_todoitem"), 0);
				BPTextField txttl = new BPTextField();
				BPTextField txtti = new BPTextField();
				txttl.setLabelFont();
				txtti.setLabelFont();
				txttl.setEditable(false);
				txtti.setEditable(false);
				txttl.setText(Integer.toString(todolistcount));
				txtti.setText(Integer.toString(todoitemcount));
				m_pantodo.add(makeHLine(" TODO"));
				m_pantodo.add(makeFLine("TodoList Count:", txttl));
				m_pantodo.add(makeFLine("TodoItem Count:", txtti));

				m_pannote.add(makeHLine(" NOTE"));
				BPCacheFileSystem cache = BPCore.FS_CACHE;
				String prjkey = prj.getProjectKey();
				List<BPTreeCacheNode<BPCacheDataFileSystem>> fs = cache.searchFileByName(".md", ".md", -1, k -> prjkey.equals(k));
				for (BPTreeCacheNode<BPCacheDataFileSystem> f : fs)
				{
					m_pannote.add(makeLinkLine(f.getKey(), f.getKey()));
				}
			}
		}
	}

	protected JPanel makeLinkLine(String label, String fname)
	{
		JPanel rc = new JPanel();
		BPLabel lbl = new BPLabel(label);
		lbl.setMonoFont();
		rc.setPreferredSize(new Dimension(500, UIConfigs.TEXTFIELD_HEIGHT()));

		rc.setLayout(new BorderLayout());
		rc.add(lbl, BorderLayout.WEST);
		rc.setMaximumSize(new Dimension(4000, UIConfigs.TEXTFIELD_HEIGHT()));
		rc.setBackground(UIConfigs.COLOR_TEXTBG());
		lbl.setBorder(new CompoundBorder(new EmptyBorder(0, 8, 0, 8), new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER())));
		return rc;
	}

	protected JPanel makeHLine(String label)
	{
		JPanel rc = new JPanel();
		BPLabel lbl = new BPLabel(label);
		lbl.setLabelFont();
		lbl.setFont(UIUtil.deltaFont(lbl.getFont(), 2));
		lbl.setPreferredSize(new Dimension(500, UIConfigs.TEXTFIELD_HEIGHT()));

		rc.setLayout(new BorderLayout());
		rc.add(lbl, BorderLayout.WEST);
		rc.setMaximumSize(new Dimension(4000, UIConfigs.TEXTFIELD_HEIGHT()));
		rc.setBorder(new CompoundBorder(new EmptyBorder(0, 0, 0, 0), new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER())));
		return rc;
	}

	protected JPanel makeFLine(String label, Component comp)
	{
		JPanel rc = new JPanel();
		BPLabel lbl = new BPLabel(label);
		lbl.setMonoFont();
		lbl.setPreferredSize(new Dimension(120, UIConfigs.TEXTFIELD_HEIGHT()));

		rc.setLayout(new BorderLayout());
		rc.add(lbl, BorderLayout.WEST);
		rc.add(comp, BorderLayout.CENTER);
		rc.setMaximumSize(new Dimension(4000, UIConfigs.TEXTFIELD_HEIGHT()));
		rc.setBorder(new CompoundBorder(new EmptyBorder(0, 8, 0, 8), new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER())));
		return rc;
	}
}
