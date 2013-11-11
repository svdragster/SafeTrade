package de.svdragster.safetrade;

import net.canarymod.Canary;
import net.canarymod.plugin.Plugin;

public class SafeTrade extends Plugin {

	@Override
	public void disable() {
		
	}

	@Override
	public boolean enable() {
		Canary.hooks().registerListener(new SafeTradeListener(), this);
		return true;
	}

}
