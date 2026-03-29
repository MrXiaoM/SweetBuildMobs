package top.mrxiaom.sweet.buildmobs.builtin.loc;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.sweet.buildmobs.api.ILocProvider;
import top.mrxiaom.sweet.buildmobs.data.Build;
import top.mrxiaom.sweet.buildmobs.data.match.BuildMatchResult;
import top.mrxiaom.sweet.buildmobs.data.match.MatchBlock;
import top.mrxiaom.sweet.buildmobs.enums.EnumFacing;

public class LocCenterBottom implements ILocProvider {
    public static final ILocProvider INSTANCE = new LocCenterBottom();
    public static final ILocProvider.Provider PROVIDER = new Provider();
    private LocCenterBottom() {}

    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    public @NotNull Location read(@NotNull BuildMatchResult result) {
        World world = result.world();
        MatchBlock clicked = result.clickedBlock();
        EnumFacing facing = result.checkFacing();
        int[] offset = facing.toWorldOffset(clicked.layerBlock());
        int oX = clicked.block().getX() - offset[0];
        int oY = clicked.block().getY() - offset[1];
        int oZ = clicked.block().getZ() - offset[2];
        Build build = result.build();
        int[] size = build.getLayerSize(facing);
        double x = oX + (size[0] / 2.0);
        double y = oY;
        double z = oZ + (size[2] / 2.0);
        return new Location(world, x, y, z);
    }

    public static class Provider implements ILocProvider.Provider {
        private Provider() {}

        @Override
        public @Nullable ILocProvider parse(@NotNull ConfigurationSection config) {
            if ("CENTER_BOTTOM".equalsIgnoreCase(config.getString("type"))) {
                return INSTANCE;
            }
            return null;
        }
    }
}
