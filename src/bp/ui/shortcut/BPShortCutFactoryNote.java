package bp.ui.shortcut;

import java.util.function.BiConsumer;

public class BPShortCutFactoryNote implements BPShortCutFactory
{
	public void register(BiConsumer<String, BPShortCutFactory> regfunc)
	{
		regfunc.accept("Create Note", this);
	}

	public BPShortCut createShortCut(String key)
	{
		BPShortCut rc = null;
		switch (key)
		{
			case "Create Note":
			{
				rc = new BPShortCutCreateNote();
				break;
			}
		}
		return rc;
	}
}
