package bp.ui.shortcut;

import java.util.function.BiConsumer;

public class BPShortCutFactoryNote implements BPShortCutFactory
{
	public void register(BiConsumer<String, BPShortCutFactory> regfunc)
	{
		regfunc.accept(BPShortCutCreateNote.SCKEY_NOTE, this);
	}

	public BPShortCut createShortCut(String key)
	{
		BPShortCut rc = null;
		switch (key)
		{
			case BPShortCutCreateNote.SCKEY_NOTE:
			{
				rc = new BPShortCutCreateNote();
				break;
			}
		}
		return rc;
	}
}
