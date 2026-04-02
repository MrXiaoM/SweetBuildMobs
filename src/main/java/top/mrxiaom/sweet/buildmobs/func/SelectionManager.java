package top.mrxiaom.sweet.buildmobs.func;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Pair;
import top.mrxiaom.sweet.buildmobs.Messages;
import top.mrxiaom.sweet.buildmobs.SweetBuildMobs;
import top.mrxiaom.sweet.buildmobs.utils.Selection;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AutoRegister
public class SelectionManager extends AbstractModule implements Listener {
    private static class SelectionData {
        private final Player player;
        private Block block1 = null, block2 = null;
        private Selection selected = null;
        private SelectionData(Player player) {
            this.player = player;
        }

        public void show() {
            if (selected != null) {
                selected.particle().show(player);
            }
        }
    }
    private final Map<UUID, SelectionData> selectionMap = new HashMap<>();
    public SelectionManager(SweetBuildMobs plugin) {
        super(plugin);
        registerEvents();
        plugin.getScheduler().runTaskTimerAsync(() -> {
            for (SelectionData data : selectionMap.values()) {
                data.show();
            }
        }, 20L, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        resetSelection(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        resetSelection(e.getPlayer());
    }

    private Boolean isLeftClickBlock(Action action) {
        switch (action) {
            case LEFT_CLICK_BLOCK:
                return true;
            case RIGHT_CLICK_BLOCK:
                return false;
            default:
                return null;
        }
    }

    private static boolean isOffHand(PlayerInteractEvent e) {
        try {
            return EquipmentSlot.OFF_HAND.equals(e.getHand());
        } catch (LinkageError ignored) {
            return false;
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.useInteractedBlock() == Event.Result.DENY) return;
        if (e.useItemInHand() == Event.Result.DENY) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        Player player = e.getPlayer();
        SelectionData data = selectionMap.get(player.getUniqueId());
        if (data == null) return;
        ItemStack item = e.getItem();
        // 不处理玩家放置方块、副手点击的情况
        if (item != null && item.getType().isBlock() || isOffHand(e)) return;
        e.setCancelled(true);
        Boolean leftClick = isLeftClickBlock(e.getAction());
        if (leftClick == null) return;
        if (leftClick) {
            data.block1 = block;
            Block block2 = data.block2;
            if (block2 != null && block2.getWorld() != block.getWorld()) {
                data.block2 = null;
            }
            Messages.Selection.select_pos1.tm(player,
                    Pair.of("%x%", block.getX()),
                    Pair.of("%y%", block.getY()),
                    Pair.of("%z%", block.getZ()));
        } else {
            data.block2 = block;
            Block block1 = data.block1;
            if (block1 != null && block1.getWorld() != block.getWorld()) {
                data.block1 = null;
            }
            Messages.Selection.select_pos2.tm(player,
                    Pair.of("%x%", block.getX()),
                    Pair.of("%y%", block.getY()),
                    Pair.of("%z%", block.getZ()));
        }
        Block block1 = data.block1;
        Block block2 = data.block2;
        if (block1 != null && block2 != null) {
            data.selected = Selection.of(block1, block2);
            data.selected.particle().show(player);
            int sizeX = Math.abs(block2.getX() - block1.getX()) + 1;
            int sizeY = Math.abs(block2.getY() - block1.getY()) + 1;
            int sizeZ = Math.abs(block2.getZ() - block1.getZ()) + 1;
            Messages.Selection.available.tm(player,
                    Pair.of("%size_x%", sizeX),
                    Pair.of("%size_y%", sizeY),
                    Pair.of("%size_z%", sizeZ));
        } else {
            data.selected = null;
        }
    }

    public void startSelection(@NotNull Player player) {
        selectionMap.put(player.getUniqueId(), new SelectionData(player));
    }

    public void resetSelection(@NotNull Player player) {
        selectionMap.remove(player.getUniqueId());
    }

    public boolean isInSelectMode(@NotNull Player player) {
        return selectionMap.containsKey(player.getUniqueId());
    }

    @Nullable
    public Selection getSelection(@NotNull Player player) {
        SelectionData data = selectionMap.get(player.getUniqueId());
        return data == null ? null : data.selected;
    }

    public static SelectionManager inst() {
        return instanceOf(SelectionManager.class);
    }
}
