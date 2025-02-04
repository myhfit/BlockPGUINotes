package bp.ui.editor;

import java.util.Map;

import bp.config.BPConfig;
import bp.data.BPDiagram;
import bp.data.BPJSONContainerBase;
import bp.data.BPMHolder;
import bp.data.BPSLData;
import bp.data.BPTodoList;
import bp.format.BPFormat;
import bp.format.BPFormatNotesJSON;
import bp.res.BPResource;
import bp.res.BPResourceHolder;
import bp.res.BPResourceIO;
import bp.util.IOUtil;
import bp.util.JSONUtil;
import bp.util.TextUtil;

public class BPEditorFactoryNotesJSON implements BPEditorFactory
{
	public String[] getFormats()
	{
		return new String[] { BPFormatNotesJSON.FORMAT_NOTES_JSON };
	}

	public boolean showInCreate()
	{
		return false;
	}

	public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
	{
		String resname = res.getName();
		if (resname.toLowerCase().endsWith(".todo.bpnj"))
			return new BPTodoListPanel();
		else if (resname.toLowerCase().endsWith(".diag.bpnj"))
			return new BPDiagramPanel();
		String text = TextUtil.toString(((BPResourceIO) res).useInputStream((in) -> IOUtil.read(in)), "utf-8");
		Map<String, Object> mo = JSONUtil.decode(text);
		if (mo != null)
		{
			String clsname = (String) mo.get(BPSLData.CLSNAME_FIELD);
			if (clsname != null && BPTodoList.class.getName().equals(clsname))
			{
				return new BPTodoListPanel();
			}
		}
		return null;
	}

	public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
	{
		if (editor instanceof BPTodoListPanel)
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
		else if (editor instanceof BPDiagramPanel)
		{
			BPDiagramPanel pnl = (BPDiagramPanel) editor;
			if (res instanceof BPResourceHolder)
			{
				BPMHolder<BPDiagram> con = new BPMHolder<BPDiagram>();
				con.bind(res);
				con.setData(((BPResourceHolder) res).getData());
				pnl.bind(con);
			}
			else
			{
				BPJSONContainerBase<BPDiagram> con = new BPJSONContainerBase<BPDiagram>();
				con.bind(res);
				pnl.bind(con);
			}
		}
	}

	public String getName()
	{
		return "Notes Editors";
	}

	public boolean handleFormat(String formatkey)
	{
		return true;
	}
}