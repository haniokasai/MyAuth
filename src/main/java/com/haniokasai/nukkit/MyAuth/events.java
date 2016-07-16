package com.haniokasai.nukkit.MyAuth;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.DoorToggleEvent;
import cn.nukkit.event.entity.EntityArmorChangeEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerItemConsumeEvent;
import cn.nukkit.event.player.PlayerItemHeldEvent;
import cn.nukkit.event.player.PlayerMoveEvent;

public class events implements Listener {
// copied from https://github.com/MCFT-Server/SimpleLogin
    Main plugin;

    public events(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!Main.lged.containsKey(event.getPlayer().getName())) {
			event.setCancelled();
		}
	}
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!Main.lged.containsKey(event.getPlayer().getName())) {
			event.setCancelled();
		}
	}
	@EventHandler
	public void onDoorToggle(DoorToggleEvent event) {
		if (!Main.lged.containsKey(event.getPlayer().getName())) {
			event.setCancelled();
		}
	}
	@EventHandler
	public void onArmorChange(EntityArmorChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			if (!Main.lged.containsKey(event.getEntity().getName())) {
				event.setCancelled();
			}
		}
	}
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		if (!Main.lged.containsKey(event.getPlayer().getName())) {
			event.setCancelled();
		}
	}
	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (Main.lged.containsKey(event.getPlayer().getName())) {
			return;
		}
		String message = event.getMessage();
		if(message.startsWith("/login") || message.startsWith("/register")) {
			return;
		}
		event.setCancelled();
	}

	@EventHandler
	public void onChat(PlayerChatEvent event) {
		if (!Main.lged.containsKey(event.getPlayer().getName())) {
			event.setCancelled();
		}
	}
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		if (!Main.lged.containsKey(event.getPlayer().getName())) {
			event.setCancelled();
		}
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!Main.lged.containsKey(event.getPlayer().getName())) {
			event.setCancelled();
		}
	}
	@EventHandler
	public void onItemConsume(PlayerItemConsumeEvent event) {
		if (!Main.lged.containsKey(event.getPlayer().getName())) {
			event.setCancelled();
		}
	}

	@EventHandler
	public void onItemHeld(PlayerItemHeldEvent event) {
		if (!Main.lged.containsKey(event.getPlayer().getName())) {
			event.setCancelled();
		}
	}
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (!Main.lged.containsKey(event.getPlayer().getName())) {
			event.setCancelled();
		}
	}

}
