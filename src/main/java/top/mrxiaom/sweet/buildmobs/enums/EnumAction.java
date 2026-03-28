package top.mrxiaom.sweet.buildmobs.enums;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum EnumAction {
    LEFT, RIGHT, SHIFT_LEFT, SHIFT_RIGHT;

    @Nullable
    public static EnumAction fromEvent(@NotNull PlayerInteractEvent e) {
        Action action = e.getAction();
        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
            return e.getPlayer().isSneaking() ? SHIFT_LEFT : LEFT;
        }
        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            return e.getPlayer().isSneaking() ? SHIFT_RIGHT : RIGHT;
        }
        return null;
    }
}
