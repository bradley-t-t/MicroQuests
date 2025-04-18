package com.trenton.microquests.listeners;

import com.trenton.coreapi.api.ListenerBase;
import com.trenton.microquests.MicroQuests;
import com.trenton.microquests.competition.Competition;
import com.trenton.microquests.competition.quests.CraftQuest;
import com.trenton.microquests.competition.quests.GatherQuest;
import com.trenton.microquests.competition.quests.KillQuest;
import com.trenton.microquests.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;

public class CompetitionListener implements ListenerBase, Listener {
    private MicroQuests plugin;
    private ConfigManager configManager;
    private Set<UUID> optOut;

    @Override
    public void register(Plugin plugin) {
        this.plugin = (MicroQuests) plugin;
        this.configManager = this.plugin.getConfigManager();
        this.optOut = configManager.getOptOut();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player)) return;
        Player player = (Player) event.getEntity().getKiller();
        if (optOut.contains(player.getUniqueId())) return;

        Competition comp = plugin.getCompetitionManager().getActiveCompetition();
        if (comp == null || !comp.isActive()) return;

        if (comp.getQuest() instanceof KillQuest quest && quest.getMob() == event.getEntityType()) {
            comp.incrementProgress(player.getUniqueId());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (optOut.contains(player.getUniqueId())) return;

        Competition comp = plugin.getCompetitionManager().getActiveCompetition();
        if (comp == null || !comp.isActive()) return;

        if (comp.getQuest() instanceof GatherQuest quest && quest.getItem() == event.getBlock().getType()) {
            comp.incrementProgress(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (optOut.contains(player.getUniqueId())) return;

        Competition comp = plugin.getCompetitionManager().getActiveCompetition();
        if (comp == null || !comp.isActive()) return;

        if (comp.getQuest() instanceof GatherQuest quest && quest.getItem() == event.getItem().getItemStack().getType()) {
            comp.incrementProgress(player.getUniqueId());
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (optOut.contains(player.getUniqueId())) return;

        Competition comp = plugin.getCompetitionManager().getActiveCompetition();
        if (comp == null || !comp.isActive()) return;

        if (comp.getQuest() instanceof CraftQuest quest && quest.getItem() == event.getCurrentItem().getType()) {
            int amountCrafted = event.getCurrentItem().getAmount();
            for (int i = 0; i < amountCrafted; i++) {
                comp.incrementProgress(player.getUniqueId());
            }
        }
    }
}