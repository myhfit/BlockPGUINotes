package bp.ui.table;

import java.util.function.Consumer;

import javax.swing.Action;

import bp.data.BPTodoList.BPTodoItem;
import bp.ui.actions.BPAction;
import bp.ui.scomp.BPTable;
import bp.util.LogicUtil.WeakRefGoConsumer;

public class BPTableFuncsTodoItem extends BPTableFuncsBase<BPTodoItem>
{
	protected WeakRefGoConsumer<BPTodoItem> m_opencbref;

	public BPTableFuncsTodoItem()
	{
		m_colnames = new String[] { "Done", "Name", "Content", "CType", "TodoType" };
		m_cols = new Class[] { String.class, String.class, String.class, String.class, Object.class };
	}

	public void setOpenCallback(Consumer<BPTodoItem> cb)
	{
		m_opencbref = new WeakRefGoConsumer<BPTodoItem>(cb);
	}

	public Object getValue(BPTodoItem o, int row, int col)
	{
		switch (col)
		{
			case 0:
				return o.done ? "Yes" : "No";
			case 1:
				return o.name;
			case 2:
				return o.content;
			case 3:
				return o.contenttype;
			case 4:
				return o.eletype;
			default:
				return "";
		}
	}

	public void setValue(Object v, BPTodoItem item, int row, int col)
	{
		switch (col)
		{
			case 0:
				item.done = (Boolean) v;
				break;
			case 1:
				item.name = (String) v;
				break;
			case 2:
				item.content = (String) v;
				break;
			default:
				break;
		}
	}

	public boolean isEditable(BPTodoItem o, int row, int col)
	{
		return false;
	}

	public Action getOpenAction(BPTable<BPTodoItem> table, BPTodoItem data, int row, int col)
	{
		return BPAction.build("").callback((e) -> m_opencbref.accept(data)).getAction();
	}

	public boolean allowTooltip()
	{
		return true;
	}

	public String getTooltip(BPTodoItem o, int row, int col)
	{
		return o.content;
	}
}
