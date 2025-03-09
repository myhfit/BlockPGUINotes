package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

import bp.BPCore;
import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.data.BPDataContainer;
import bp.data.BPDataContainerBase;
import bp.data.BPDiagram;
import bp.data.BPJSONContainerBase;
import bp.data.BPMContainer;
import bp.data.BPMHolder;
import bp.data.BPTodoList;
import bp.data.BPTodoList.BPTodoItem;
import bp.format.BPFormat;
import bp.format.BPFormatNotesJSON;
import bp.project.BPResourceProject;
import bp.project.BPResourceProjectNotes;
import bp.res.BPResource;
import bp.res.BPResourceHolder;
import bp.ui.BPViewer;
import bp.ui.actions.BPAction;
import bp.ui.container.BPToolBarSQ;
import bp.ui.dialog.BPDialogSimple;
import bp.ui.form.BPForm;
import bp.ui.form.BPFormManager;
import bp.ui.res.icon.BPIconResV;
import bp.ui.scomp.BPTable;
import bp.ui.scomp.BPTable.BPTableColumnModel;
import bp.ui.scomp.BPTable.BPTableModel;
import bp.ui.table.BPTableFuncsTodoItem;
import bp.ui.util.CommonUIOperations;
import bp.ui.util.UIUtil;
import bp.util.ClassUtil;

public class BPTodoListPanel extends JPanel implements BPEditor<JPanel>, BPViewer<BPMContainer<BPTodoList>>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7936610905968745685L;

	protected boolean m_needsave;
	protected String m_id;
	protected int m_channelid;
	protected BPMContainer<BPTodoList> m_con;
	protected JScrollPane m_scroll;
	protected BPToolBarSQ m_toolbar;
	protected BPTable<BPTodoItem> m_table;
	protected BPTodoList m_todolist;
	protected Consumer<BPTodoItem> m_editfunc;

	protected BiConsumer<String, Boolean> m_state_changed;

	public BPTodoListPanel()
	{
		init();
		initBPActions();
	}

	protected void init()
	{
		m_editfunc = this::onEditItem;
		removeAll();
		m_table = new BPTable<BPTodoItem>();
		m_table.setTableFont();
		BPTableFuncsTodoItem funcs = new BPTableFuncsTodoItem();
		funcs.setOpenCallback(m_editfunc);
		BPTableModel<BPTodoItem> model = new BPTableModel<BPTodoItem>(funcs);
		m_table.setModel(model);
		BPTableColumnModel tcm = m_table.getBPColumnModel();
		m_table.setAutoResizeMode(BPTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		{
			DefaultTableCellRenderer r2 = new DefaultTableCellRenderer();
			r2.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
			tcm.getColumnBuilder(0).setMaxWidth(UIUtil.scale(60)).setCellRenderer(r2);
			tcm.getColumn(1).setPreferredWidth(UIUtil.scale(160));
			tcm.getColumn(2).setPreferredWidth(UIUtil.scale(1000));
			tcm.getColumnBuilder(3).setMaxWidth(UIUtil.scale(60)).setCellRenderer(r2);
			tcm.getColumnBuilder(4).setMaxWidth(UIUtil.scale(90)).setCellRenderer(r2);
		}
		m_scroll = new JScrollPane();
		m_toolbar = new BPToolBarSQ(true);

		Action actadd = BPAction.build("add").tooltip("Create Item").callback(this::onAdd).vIcon(BPIconResV.ADD()).getAction();
		Action actdel = BPAction.build("del").tooltip("Remove Item(s)").callback(this::onDel).vIcon(BPIconResV.DEL()).getAction();
		Action actedit = BPAction.build("edit").tooltip("Edit Item").callback(this::onEdit).vIcon(BPIconResV.EDIT()).getAction();
		m_toolbar.setActions(new Action[] { BPAction.separator(), actadd, actdel, BPAction.separator(), actedit });

		m_scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_toolbar.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));

		m_scroll.setViewportView(m_table);

		setLayout(new BorderLayout());
		add(m_scroll, BorderLayout.CENTER);
		add(m_toolbar, BorderLayout.WEST);
	}

	protected void initBPActions()
	{
	}

	public BPComponentType getComponentType()
	{
		return BPComponentType.CUSTOMCOMP;
	}

	public JPanel getComponent()
	{
		return this;
	}

	public void bind(BPMContainer<BPTodoList> con, boolean noread)
	{
		m_con = con;
		BPTodoList d = null;
		if (!noread)
		{
			if (con.canOpen())
			{
				m_con.open();
				d = m_con.readMData(false);
			}
			if (d == null)
			{
				d = new BPTodoList();
			}
			m_todolist = d;
			m_table.getBPTableModel().setDatas(d.getItems());
		}
	}

	public void unbind()
	{
		if (m_con != null)
			m_con.close();
		m_con = null;
	}

	public BPMContainer<BPTodoList> getDataContainer()
	{
		return m_con;
	}

	public void focusEditor()
	{
	}

	public String getEditorInfo()
	{
		return null;
	}

	protected void onCreate(ActionEvent e)
	{
		Action actcreatenode = BPAction.build("Node").name("Node").getAction();
		Action actcreatelink = BPAction.build("Link").name("Link").getAction();
		JPopupMenu pop = new JPopupMenu();
		JComponent[] comps = UIUtil.makeMenuItems(new Action[] { actcreatenode, actcreatelink });
		for (JComponent comp : comps)
		{
			pop.add(comp);
		}
		JComponent source = (JComponent) e.getSource();
		JComponent par = (JComponent) source.getParent();
		pop.show(par, source.getX() + source.getWidth(), source.getY());
	}

	public void save()
	{
		m_con.open();
		try
		{
			m_con.writeMData(m_todolist, true);
			setNeedSave(false);

			BPResource res = m_con.getResource();
			BPResourceProject prj = BPCore.getProjectsContext().getRootProject(res);
			if (prj != null && BPResourceProjectNotes.PRJTYPE_NOTES.equals(prj.getProjectTypeName()))
				((BPResourceProjectNotes) prj).refreshNote(m_todolist, res);
		}
		finally
		{
			m_con.close();
		}
		BPResource res = m_con.getResource();
		if (res != null)
		{
			BPResource par = res.getParentResource();
			if (par != null)
			{
				CommonUIOperations.refreshPathTree(par, false);
				CommonUIOperations.refreshResourceCache(par);
			}
		}
	}

	protected void onEditItem(BPTodoItem item)
	{
		BPForm<?> form = ClassUtil.tryLoopSuperClass((cls) -> BPFormManager.getForm(cls.getName()), item.getClass(), BPTodoItem.class);
		form.showData(item.getMappedData());

		JScrollPane scroll = new JScrollPane((Component) form);
		scroll.setPreferredSize(new Dimension(400, 300));
		scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		int r = BPDialogSimple.showComponent(scroll, BPDialogSimple.COMMANDBAR_OK_CANCEL, null, "Edit Todo", this.getFocusCycleRootAncestor());
		if (r == BPDialogSimple.COMMAND_OK)
		{
			item.setMappedData(form.getFormData());
			m_table.refreshData();
			setNeedSave(true);
		}
	}

	public void reloadData()
	{
	}

	public boolean needSave()
	{
		return m_needsave;
	}

	public void setNeedSave(boolean needsave)
	{
		m_needsave = needsave;
		m_state_changed.accept(m_id, needsave);
	}

	public void setID(String id)
	{
		m_id = id;
	}

	public String getID()
	{
		return m_id;
	}

	public void setChannelID(int channelid)
	{
		m_channelid = channelid;
	}

	public int getChannelID()
	{
		return m_channelid;
	}

	public void setOnDynamicInfo(Consumer<String> info)
	{
	}

	public void setOnStateChanged(BiConsumer<String, Boolean> handler)
	{
		m_state_changed = handler;
	}

	public BPDataContainer createDataContainer(BPResource res)
	{
		if (res != null && res.isFileSystem())
		{
			BPJSONContainerBase<BPDiagram> con = new BPJSONContainerBase<BPDiagram>();
			con.bind(res);
			return con;
		}
		else
		{
			BPDataContainer con = new BPDataContainerBase();
			con.bind(res);
			return con;
		}
	}

	public static class BPEditorFactoryTodoList implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatNotesJSON.FORMAT_NOTES_JSON };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPTodoListPanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			BPTodoListPanel pnl = (BPTodoListPanel) editor;
			if (res instanceof BPResourceHolder)
			{
				BPMHolder<BPTodoList> con = new BPMHolder<BPTodoList>();
				con.bind(res);
				con.setData(((BPResourceHolder) res).getData());
				pnl.bind(con);
			}
			else
			{
				BPJSONContainerBase<BPTodoList> con = new BPJSONContainerBase<BPTodoList>();
				con.bind(res);
				pnl.bind(con);
			}
		}

		public String getName()
		{
			return "TodoList Panel";
		}

		public boolean handleFormat(String formatkey)
		{
			return false;
		}

		public String getExtPrefix(String ext)
		{
			return ".todo";
		}
	}

	protected void onAdd(ActionEvent e)
	{
		BPTodoItem item = new BPTodoItem();
		m_todolist.getItems().add(item);
		m_table.refreshData();
		setNeedSave(true);

	}

	protected void onDel(ActionEvent e)
	{
		m_todolist.getItems().removeAll(m_table.getSelectedDatas());
		m_table.refreshData();
		setNeedSave(true);
	}

	protected void onEdit(ActionEvent e)
	{
		onEditItem(m_table.getSelectedData());
	}
}