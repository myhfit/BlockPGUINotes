package bp.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.lang.ref.WeakReference;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.UIConfigs;
import bp.project.BPResourceProjectNotes;
import bp.ui.scomp.BPLabel;
import bp.ui.scomp.BPTextField;
import bp.util.ObjUtil;

public class BPProjectOverviewPanelNotes extends JPanel implements BPProjectOverviewComp<BPResourceProjectNotes>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3374967334306688721L;

	protected WeakReference<BPResourceProjectNotes> m_prjref;
	protected JPanel m_panmain;

	public BPProjectOverviewPanelNotes()
	{
		initUI();
	}

	protected void initUI()
	{
		JScrollPane scroll = new JScrollPane();
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_panmain = new JPanel();
		m_panmain.setLayout(new BoxLayout(m_panmain, BoxLayout.Y_AXIS));

		scroll.setViewportView(m_panmain);

		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);
	}

	public void setup(BPResourceProjectNotes prj)
	{
		m_prjref = new WeakReference<BPResourceProjectNotes>(prj);
		initDatas();
	}

	protected void initDatas()
	{
		m_panmain.removeAll();
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
				m_panmain.add(makeFLine("TodoList Count:", txttl));
				m_panmain.add(makeFLine("TodoItem Count:", txtti));
			}
		}
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
		rc.setBorder(new CompoundBorder(new EmptyBorder(0, 2, 0, 0), new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER())));
		return rc;
	}
}
