package bp.ui.form;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bp.ui.scomp.BPCheckBox;
import bp.ui.scomp.BPComboBox;
import bp.ui.scomp.BPTextField;
import bp.util.DateUtil;
import bp.util.ObjUtil;

public class BPFormPanelScheduleCalendar extends BPFormPanelSchedule
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4900835366862460606L;

	protected BPTextField m_txttime;
	protected BPTextField m_txtinterval;
	protected BPComboBox<Map<String, Object>> m_cmbunit;
	protected BPCheckBox m_chkrunonce;

	@SuppressWarnings("unchecked")
	public Map<String, Object> getFormData()
	{
		Map<String, Object> rc = super.getFormData();
		String timestr = m_txttime.getNotEmptyText();
		long time = DateUtil.parseTime(timestr);
		if (time == -1)
		{
			return null;
		}
		rc.put("starttime", time);
		rc.put("interval", ObjUtil.toInt(m_txtinterval.getNotEmptyText(), 10));
		Map<String, Object> unit = (Map<String, Object>) m_cmbunit.getSelectedItem();
		rc.put("unit", ObjUtil.toInt(unit.get("value"), null));
		rc.put("runonce", m_chkrunonce.isSelected());
		return rc;
	}

	protected void initForm()
	{
		super.initForm();
		m_txttime = makeSingleLineTextField();
		m_txtinterval = makeSingleLineTextField();
		m_cmbunit = makeComboBox(this::getUnitLabel);
		m_chkrunonce = makeCheckBox();
		
		initUnits();

		addSeparator("Condition");
		addLine(new String[] { "Start Time" }, new Component[] { m_txttime }, () -> !m_txttime.isEmpty() && (DateUtil.parseTime(m_txttime.getNotEmptyText()) != -1));
		addLine(new String[] { "Interval" }, new Component[] { m_txtinterval }, () -> m_txtinterval.isInt());
		addLine(new String[] { "Unit" }, new Component[] { m_cmbunit }, () -> m_cmbunit.getSelectedIndex() > -1);
		addLine(new String[] { "Run Once" }, new Component[] { m_chkrunonce });
	}

	protected void initUnits()
	{
		List<Map<String, Object>> unitdatas = new ArrayList<Map<String, Object>>();
		Map<Integer, String> units = DateUtil.getCalendarUnits();
		units.forEach((v, s) ->
		{
			unitdatas.add(ObjUtil.makeMap("label", s, "value", v));
		});
		m_cmbunit.getBPModel().setDatas(unitdatas);
	}

	@SuppressWarnings("unchecked")
	protected Object getUnitLabel(Object o)
	{
		Map<String, Object> m = (Map<String, Object>) o;
		if (m == null)
			return "";
		return m.get("label");
	}

	public void showData(Map<String, ?> data, boolean editable)
	{
		super.showData(data, editable);
		setComponentValue(m_txttime, data, "starttime", editable, m -> DateUtil.formatTime(ObjUtil.toLong(m.get("starttime"), null)));
		setComponentValue(m_txtinterval, data, "interval", editable);
		setComponentValue(m_cmbunit, data, "unit", editable, m -> getUnitByValue(ObjUtil.toInt(m.get("unit"), null)));
		setComponentValue(m_chkrunonce, data, "runonce", editable);
	}

	protected Map<String, Object> getUnitByValue(int v)
	{
		List<Map<String, Object>> unitdatas = m_cmbunit.getBPModel().getDatas();
		for (Map<String, Object> unit : unitdatas)
		{
			if (unit.get("value").equals(v))
			{
				return unit;
			}
		}
		return null;
	}
}