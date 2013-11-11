package de.svdragster.safetrade;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.factory.ItemFactory;
import net.canarymod.api.factory.ObjectFactory;
import net.canarymod.api.inventory.Inventory;
import net.canarymod.api.inventory.Item;
import net.canarymod.api.inventory.ItemType;
import net.canarymod.api.inventory.slot.GrabMode;
import net.canarymod.api.inventory.slot.SecondarySlotType;
import net.canarymod.chat.Colors;
import net.canarymod.chat.TextFormat;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.ConnectionHook;
import net.canarymod.hook.player.DisconnectionHook;
import net.canarymod.hook.player.EntityRightClickHook;
import net.canarymod.hook.player.InventoryHook;
import net.canarymod.hook.player.SlotClickHook;
import net.canarymod.plugin.PluginListener;

public class SafeTradeListener implements PluginListener {
	public static ArrayList<String> TradeRequests = new ArrayList<String>();
	public static ArrayList<Player> Trading = new ArrayList<Player>();
	public static ArrayList<Player> One = new ArrayList<Player>();
	public static ArrayList<Player> Two = new ArrayList<Player>();
	
	private static final String PERMISSION_UPDATE = "safetrade.checkupdates";
	public static final String PERMISSION_REQUEST = "safetrade.request";
	public static final String PERMISSION_ACCEPT = "safetrade.accept";
	
	public static final int[] Line = {4, 13, 22, 31, 40, 49};
	public static final int[] Space1 = {1, 2, 3, 9, 10, 11, 12, 18, 19, 20, 21, 27, 28, 29, 30, 36, 37, 38, 39, 45, 46, 47, 48};
	public static final int[] Space2 = {5, 6, 7, 8, 14, 15, 16, 17, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44, 50, 51};
	
	private static final String USER_AGENT = "Minecraft Server " + Canary.getServer().getName();
	public static final String VERSION = "1.11";
	
	
	public boolean CanAccept(Player player) {
		for (int i=0; i<TradeRequests.size(); i++) {
			String[] table = TradeRequests.get(i).split(";");
			if (player.getName().equalsIgnoreCase(table[1])) {
				TradeRequests.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public void MakeLine(Inventory inv) {
		ItemFactory ifactory = Canary.factory().getItemFactory();
		for (int i=0; i<Line.length; i++) {
			Item line = ifactory.newItem(ItemType.Stone);
			line.setDisplayName(Colors.GRAY + "Line");
			line.setAmount(1);
			line.setSlot(Line[i]);
			inv.setSlot(line);
		}
	}
	
	public void Trade(Player one, Player two) {
		One.add(one);
		Two.add(two);
		Trading.add(one);
		Trading.add(two);
		ObjectFactory ofactory = Canary.factory().getObjectFactory();
		Inventory inv = ofactory.newCustomStorageInventory("Trading", 8);
		one.openInventory(inv);
		two.openInventory(inv);
		MakeLine(inv);
		SetReady(one, inv, false);
		SetReady(two, inv, false);
	}
	
	public boolean BothReady(Inventory inv) {
		if ((inv.getSlot(0).getId() == 35 && inv.getSlot(0).getDamage() == 5) && (inv.getSlot(53).getId() == 35 && inv.getSlot(53).getDamage() == 5)) {
			return true;
		}
		return false;
	}
	
	public boolean IsReady(Player player, Inventory inv) {
		if (One.contains(player)) {
			if (inv.getSlot(0) != null) {
				if (inv.getSlot(0).getType() == ItemType.WoolRed) {
					return false;
				} else {
					return true;
				}
			}
			
		}
		
		if (Two.contains(player)) {
			if (inv.getSlot(53) != null) {
				if (inv.getSlot(53).getType() == ItemType.WoolRed) {
					return false;
				} else {
					return true;
				}
			}
		}
		return true;
	}
	
	public void SetAllFalse(Inventory inv) {
		Item item1 = inv.getSlot(0);
		item1.setDamage(14);
		String[] text1 = item1.getDisplayName().split(":");
		item1.setDisplayName(Colors.LIGHT_GREEN + text1[0] + ": Click here if you are ready");
		inv.update();
		Item item2 = inv.getSlot(53);
		item2.setDamage(14);
		String[] text2 = item2.getDisplayName().split(":");
		item2.setDisplayName(Colors.LIGHT_GREEN + text2[0] + ": Click here if you are ready");
		inv.update();
	}
	
	public void SetReady(Player player, Inventory inv, boolean ready) {
		ItemFactory ifactory = Canary.factory().getItemFactory();
		if (IsReady(player, inv)) {
			if (One.contains(player)) {
				Item RedWool = ifactory.newItem(35);
				RedWool.setAmount(1);
				RedWool.setDamage(14);
				RedWool.setSlot(0);
				RedWool.setDisplayName(Colors.LIGHT_GREEN + player.getName() + ": Click here if you are ready");
				inv.setSlot(RedWool);
			}
			
			if (Two.contains(player)) {
				Item RedWool = ifactory.newItem(35);
				RedWool.setAmount(1);
				RedWool.setDamage(14);
				RedWool.setSlot(53);
				RedWool.setDisplayName(Colors.LIGHT_GREEN + player.getName() + ": Click here if you are ready");
				inv.setSlot(RedWool);
			}
		} else {
			if (One.contains(player)) {
				Item GreenWool = ifactory.newItem(35);
				GreenWool.setAmount(1);
				GreenWool.setDamage(5);
				GreenWool.setSlot(0);
				GreenWool.setDisplayName(Colors.RED + player.getName() + ": Click here if you are not ready");
				inv.setSlot(GreenWool);
			}
			
			if (Two.contains(player)) {
				Item GreenWool = ifactory.newItem(35);
				GreenWool.setAmount(1);
				GreenWool.setDamage(5);
				GreenWool.setSlot(53);
				GreenWool.setDisplayName(Colors.RED + player.getName() + ": Click here if you are not ready");
				inv.setSlot(GreenWool);
			}
		}	
	}
	
	public void FinishTrade(Inventory inv) {
		String[] tempplayer1 = inv.getSlot(0).getDisplayName().split(":");
		String player1 = tempplayer1[0];
		String[] tempplayer2 = inv.getSlot(53).getDisplayName().split(":");
		String player2 = tempplayer2[0];
		Player p1 = Canary.getServer().getPlayer(TextFormat.removeFormatting(player1)); // I'm running out of names
		Player p2 = Canary.getServer().getPlayer(TextFormat.removeFormatting(player2));
		p1.message(Colors.GREEN + "--Trade with " + p2.getName() + " successful!--");
		p2.message(Colors.GREEN + "--Trade with " + p1.getName() + " successful!--");
		ObjectFactory ofactory = Canary.factory().getObjectFactory();
		Inventory inv1 = ofactory.newCustomStorageInventory("Traded items", 4);
		Inventory inv2 = ofactory.newCustomStorageInventory("Traded items", 4);
		for (int i=0; i<Space1.length; i++) {
			Item olditem = inv.getSlot(Space1[i]);
			if (olditem != null) {
				Item item = olditem.clone();
				inv1.addItem(item);
			}
		}
		for (int i=0; i<Space2.length; i++) {
			Item olditem = inv.getSlot(Space2[i]);
			if (olditem != null) {
				Item item = olditem.clone();
				inv2.addItem(item);
			}
		}
		inv.clearContents(); // <--- This gave me so much trouble.
		p1.openInventory(inv2);
		p2.openInventory(inv1);
	}
	
	@HookHandler
	public void onEntityRightClick(EntityRightClickHook hook) {
		Player clicker = hook.getPlayer();
		if (!clicker.hasPermission(PERMISSION_REQUEST)) {
			clicker.notice("You do not have permission to trade.");
			return;
		}
		if (Trading.contains(clicker)) {
			Trading.remove(clicker); // Just in case
		}
		if (hook.getEntity().isPlayer()) {
			Player accepter = (Player) hook.getEntity();
			if (!accepter.hasPermission(PERMISSION_ACCEPT)) {
				clicker.notice(accepter.getName() + " does not have permission to trade.");
				return;
			}
			if (Trading.contains(accepter)) {
				clicker.notice(accepter.getName() + " is currently trading.");
				return;
			}
			if (CanAccept(clicker)) {
				Trade(clicker, accepter);
				return;
			}
			String TempRequest = clicker.getName() + ";" + accepter.getName();
			if (!TradeRequests.contains(TempRequest)) {
				TradeRequests.add(TempRequest);
				clicker.message(Colors.GREEN + "Trade request to " + accepter.getName() + " sent.");
				accepter.message(Colors.GREEN + clicker.getName() + " wants to trade with you! Rightclick him to accept.");
			} else {
				TradeRequests.remove(TempRequest);
				clicker.message(Colors.RED + "Trade request to " + accepter.getName() + " cancelled.");
				accepter.message(Colors.RED + clicker.getName() + " doesn't want to trade with you anymore.");
			}
		}
	}
	
	@HookHandler
	public void onInventory(InventoryHook hook) {
		if (hook.isClosing()) {
			if (hook.getInventory().getInventoryName().contains("Trading")) {
				if (One.contains(hook.getPlayer())) {
					for (int i=0; i<Space1.length; i++) {
						if (hook.getInventory().getSlot(Space1[i]) != null) {
							hook.getPlayer().getInventory().insertItem(hook.getInventory().getSlot(Space1[i]));
						}
					}
					One.remove(hook.getPlayer());
				}
				if (Two.contains(hook.getPlayer())) {
					for (int i=0; i<Space2.length; i++) {
						if (hook.getInventory().getSlot(Space2[i]) != null) {
							hook.getPlayer().getInventory().insertItem(hook.getInventory().getSlot(Space2[i]));
						}
					}
					Two.remove(hook.getPlayer());
				}
				if (Trading.contains(hook.getPlayer())) {
					Trading.remove(hook.getPlayer());
				}
			}
		}
	}
	
	public boolean OnePlace(Player player, Inventory inv, int slot) {
		for (int i=0; i<Space1.length; i++) {
			if (slot == Space1[i]) {
				if (Two.contains(player)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean TwoPlace(Player player, Inventory inv, int slot) {
		for (int i=0; i<Space2.length; i++) {
			if (slot == Space2[i]) {
				if (One.contains(player)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@HookHandler
	public void onSlotClick(SlotClickHook hook) {
		if (hook.getInventory().getInventoryName().contains("Trading")) {
			if (hook.getItem() != null) {
				/** The Items seperating the places where players put their items */
				Item item = hook.getItem();
				if (item.getDisplayName().equalsIgnoreCase(Colors.GRAY + "Line")) {
					hook.setCanceled();
				}
			}
			
			/** Ready button */
			if (hook.getItem() != null) {
				if (hook.getItem().getId() == 35 && hook.getItem().getDamage() == 14) {
					if (hook.getItem().getDisplayName().contains(hook.getPlayer().getName())) {
						SetReady(hook.getPlayer(), hook.getInventory(), true);
					}
					if (BothReady(hook.getInventory())) {
						FinishTrade(hook.getInventory());
					}
					hook.setCanceled();
				} else if (hook.getItem().getId() == 35 && hook.getItem().getDamage() == 5) {
					if (hook.getItem().getDisplayName().contains(hook.getPlayer().getName())) {
						SetReady(hook.getPlayer(), hook.getInventory(), false);
					}
					hook.setCanceled();
					return;
				}
			}		
			/** Preventing the player to change the items when he's ready */
			if (hook.getItem() != null) {
				if (IsReady(hook.getPlayer(), hook.getInventory())) {
					if (hook.getItem().getId() != 35) { // It's annoying when clicking ready buttons
						hook.getPlayer().notice("You can't move items when you're ready!");
					}
					hook.setCanceled();
				}
			}
			
			/** Set ready state to false when moving items */
			if (hook.getItem() != null) {
				if (hook.getItem().getId() != 35 && !hook.getItem().getDisplayName().contains("ready")) {
					SetAllFalse(hook.getInventory());
				}
			}
			
			/** Blocking shift+click */
			if (hook.getGrabMode() == GrabMode.SHIFT_CLICK) {
				hook.setCanceled();
			}
			
			/** Blocking double-click */
			if (hook.getGrabMode() == GrabMode.DOUBLE_CLICK) {
				hook.setCanceled();
			}
			
			if (hook.getGrabMode() == GrabMode.HOVER_SWAP) {
				hook.setCanceled();
			}
			
			/** Where One is allowed to place his items */
			if (hook.getSecondarySlotType() == SecondarySlotType.CONTAINER) {
				if (OnePlace(hook.getPlayer(), hook.getInventory(), hook.getSlotId())) {
					hook.setCanceled();
				}
			}
			
			/** Where Two is allowed to place his items */
			if (hook.getSecondarySlotType() == SecondarySlotType.CONTAINER) {
				if (TwoPlace(hook.getPlayer(), hook.getInventory(), hook.getSlotId())) {
					hook.setCanceled();
				}
			}
		}
	}
	
	@HookHandler
	public void onLogout(DisconnectionHook hook) {
		if (Trading.contains(hook.getPlayer())) {
			Trading.remove(hook.getPlayer());
		}
		if (One.contains(hook.getPlayer())) {
			One.remove(hook.getPlayer());
		}
		if (Two.contains(hook.getPlayer())) {
			Two.remove(hook.getPlayer());
		}
	}
	
	@HookHandler
	public void onLogin(ConnectionHook hook) {
		if (hook.getPlayer().hasPermission(PERMISSION_UPDATE)) {
			try {
				String result = sendGet();
				hook.getPlayer().message(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String sendGet() throws Exception {
		String MYIDSTART = "svdragster>";
		String MYIDEND = "<svdragster";
		String url = "http://sv.slyip.net/checkupdate.php?version=" + VERSION + "&plugin=safetrade";
 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		// optional default is GET
		con.setRequestMethod("GET");
 
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		String result = response.toString();
		if (result.contains(MYIDSTART) && result.contains(MYIDEND)) {
			int endPos = result.indexOf(MYIDEND);
			result = Colors.ORANGE + "<SafeTrade> "+ Colors.GREEN + "Update available at: " + Colors.WHITE + result.substring(MYIDSTART.length(), endPos);
		}
		
		return result;
	}
}
