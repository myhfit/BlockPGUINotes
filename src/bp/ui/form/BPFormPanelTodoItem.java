package bp.ui.form;

import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import bp.data.BPTodoList.BPTodoItemType;
import bp.format.BPFormat;
import bp.format.BPFormatFeature;
import bp.format.BPFormatManager;
import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPTextField;
import bp.util.ObjUtil;

public class BPFormPanelTodoItem extends BPFormPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4755000298731564893L;

	protected BPTextField m_txtname;
	protected BPTextField m_txtcontent;
	protected BPCheckBox m_chkisdone;
	protected BPComboBox<BPFormat> m_cmbcontenttype;
	protected BPComboBox<BPTodoItemType> m_cmbeletype;

	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = new HashMap<String, Object>();
		rc.put("name", m_txtname.getNotEmptyText());
		rc.put("content", m_txtcontent.getNotEmptyText());
		BPFormat format = (BPFormat) m_cmbcontenttype.getSelectedItem();
		if (format != null)
			rc.put("contenttype", format.getName());
		BPTodoItemType itemtype = (BPTodoItemType) m_cmbeletype.getSelectedItem();
		rc.put("eletype", itemtype == null ? 0 : itemtype.ordinal());
		rc.put("done", m_chkisdone.isSelected());
		return rc;
	}

	protected void initForm()
	{
		m_txtname = makeSingleLineTextField();
		m_txtcontent = makeSingleLineTextField();
		m_cmbcontenttype = makeComboBox(f -> f == null ? "" : ((BPFormat) f).getName());
		m_cmbcontenttype.replaceWBorder();
		m_cmbeletype = makeComboBox(null);
		m_cmbeletype.replaceWBorder();
		m_chkisdone = makeCheckBox();

		initFormats();
		initItemTypes();

		addLine(new String[] { "Name" }, new Component[] { m_txtname }, () -> !m_txtname.isEmpty());
		addLine(new String[] { "Content" }, new Component[] { m_txtcontent });
		addLine(new String[] { "Content Type" }, new Component[] { m_cmbcontenttype });
		addLine(new String[] { "Done" }, new Component[] { m_chkisdone });
		addLine(new String[] { "Todo Type" }, new Component[] { m_cmbeletype });
	}

	protected void initFormats()
	{
		m_cmbcontenttype.getBPModel().setDatas(BPFormatManager.getFormatsByFeature(BPFormatFeature.TEXT));
	}

	protected void initItemTypes()
	{
		m_cmbeletype.getBPModel().setDatas(Arrays.asList(BPTodoItemType.values()));
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		setComponentValue(m_txtname, data, "name", editable);
		setComponentValue(m_txtcontent, data, "content", editable);
		setComponentValue(m_cmbcontenttype, data, "contenttype", editable, (m) ->
		{
			String fstr = ObjUtil.toString(m.get("contenttype"));
			return fstr == null ? null : BPFormatManager.getFormatByName(fstr);
		});
		setComponentValue(m_chkisdone, data, "done", editable);
		setComponentValue(m_cmbeletype, data, "eletype", editable, m -> ObjUtil.enumFromOrdinal(BPTodoItemType.class, ObjUtil.toInt(m.get("eletype"), 0)));
	}
}