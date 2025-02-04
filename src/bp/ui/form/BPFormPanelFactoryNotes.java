package bp.ui.form;

import java.util.function.BiConsumer;

import bp.data.BPTodoList.BPTodoItem;
import bp.schedule.BPScheduleCalendar;

public class BPFormPanelFactoryNotes implements BPFormPanelFactory
{
	public void register(BiConsumer<String, Class<? extends BPFormPanel>> regfunc)
	{
		regfunc.accept(BPScheduleCalendar.class.getName(), BPFormPanelScheduleCalendar.class);
		regfunc.accept(BPTodoItem.class.getName(), BPFormPanelTodoItem.class);
	}
}
