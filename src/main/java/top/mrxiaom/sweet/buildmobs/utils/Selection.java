package top.mrxiaom.sweet.buildmobs.utils;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.data.Result;
import top.mrxiaom.pluginbase.utils.CollectionUtils;
import top.mrxiaom.sweet.buildmobs.enums.EnumFacing;

import java.util.*;

public class Selection {
    private final World world;
    private final int fromX, fromY, fromZ;
    private final int toX, toY, toZ;
    private ParticleArea particle;

    private Selection(World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        this.world = world;
        this.fromX = Math.min(fromX, toX);
        this.fromY = Math.min(fromY, toY);
        this.fromZ = Math.min(fromZ, toZ);
        this.toX = Math.max(fromX, toX);
        this.toY = Math.max(fromY, toY);
        this.toZ = Math.max(fromZ, toZ);
    }

    public ParticleArea particle() {
        if (particle == null) {
            particle = new ParticleArea();
        }
        return particle;
    }

    /**
     * 保存选取区域到配置文件
     * @param facing 要求面向方向
     * @return 保存的配置
     */
    public Result<Saved> saveBlockLayers(EnumFacing facing) {
        return saveBlockLayers(facing, "abcdefghijkmlnopqrstuvwxyz0123456789".toCharArray());
    }

    /**
     * 保存选取区域到配置文件
     * @param facing 要求面向方向
     * @param dict 方块定义所用字典
     * @return 保存的配置
     */
    public Result<Saved> saveBlockLayers(EnumFacing facing, char[] dict) {

        Saved config = new Saved();
        // 获取选取区域在本地坐标系的尺寸
        int[] localSize = facing.toLocalOffset(
                Math.abs(toX - fromX) + 1,
                Math.abs(toY - fromY) + 1,
                Math.abs(toZ - fromZ) + 1,
                true
        );
        int sizeLocalX = localSize[1];
        int sizeLocalY = localSize[2];
        Map<Material, Integer> numbers = new HashMap<>();
        Map<Integer, char[][]> layers = new HashMap<>();
        for (int y = fromY; y <= toY; y++)
            for (int x = fromX; x <= toX; x++)
                for (int z = fromZ; z <= toZ; z++) {
                    // 获取本地坐标
                    int[] offset = facing.toLocalOffset(x - fromX, y - fromY, z - fromZ);
                    int layerNum = Math.abs(offset[0]);
                    int localX = offset[1];
                    int localY = offset[2];
                    char[][] layer = CollectionUtils.getOrPut(layers, layerNum, () -> {
                        char[][] chars = new char[sizeLocalY][sizeLocalX];
                        for (char[] array : chars) {
                            Arrays.fill(array, ' ');
                        }
                        return chars;
                    });
                    Block block = world.getBlockAt(x, y, z);
                    if (block.isEmpty()) continue;
                    Material type = block.getType();
                    int index;
                    if (!numbers.containsKey(type)) {
                        // 储存方块定义
                        index = numbers.size();
                        if (index >= dict.length) {
                            return Result.illegalState("方块类型过多，超过了 " + dict.length + " 种方块，停止保存");
                        }
                        char ch = dict[index];
                        config.defines.put(ch, type);
                        numbers.put(type, index);
                    } else {
                        index = numbers.get(type);
                    }
                    // 储存方块坐标
                    layer[localY][localX] = dict[index];
                }

        // 正式保存数据到配置中
        for (Map.Entry<Integer, char[][]> entry : layers.entrySet()) {
            List<String> lines = new ArrayList<>();
            for (char[] line : entry.getValue()) {
                // 倒序插入行到配置中
                lines.add(0, String.valueOf(line));
            }
            config.layers.put(entry.getKey(), lines);
        }

        return Result.ok(config);
    }

    public World world() {
        return world;
    }

    public int fromX() {
        return fromX;
    }

    public int fromY() {
        return fromY;
    }

    public int fromZ() {
        return fromZ;
    }

    public int toX() {
        return toX;
    }

    public int toY() {
        return toY;
    }

    public int toZ() {
        return toZ;
    }

    public static Selection of(Block pos1, Block pos2) {
        if (pos1.getWorld() != pos2.getWorld()) {
            throw new IllegalArgumentException("两个方块应该要在同一个世界中");
        }
        return of(pos1.getWorld(), pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    public static Selection of(World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return new Selection(world, fromX, fromY, fromZ, toX, toY, toZ);
    }

    public static class Saved {
        private final Map<Character, Material> defines = new HashMap<>();
        private final Map<Integer, List<String>> layers = new HashMap<>();
        private Saved() {}

        public Map<Character, Material> defines() {
            return defines;
        }

        public Map<Integer, List<String>> layers() {
            return layers;
        }
    }

    public class ParticleArea {
        private final List<double[]> faces;
        private final List<double[]> horizontalEdges;
        private final List<double[]> verticalEdges;
        private final double[][] corners;
        private ParticleArea() {
            // Minecraft Wiki: 一个方块的坐标实际上是这个方块的西北下角那一点的坐标。
            // 所以更大的那个坐标需要 +1，延伸到方块的东南上角以包裹方块
            double minX = fromX, minY = fromY, minZ = fromZ;
            double maxX = toX + 1, maxY = toY + 1, maxZ = toZ + 1;

            faces = new ArrayList<>();
            faces.addAll(generateFaceX(minX, minY, maxY, minZ, maxZ));
            faces.addAll(generateFaceX(maxX, minY, maxY, minZ, maxZ));
            faces.addAll(generateFaceY(minY, minX, maxX, minZ, maxZ));
            faces.addAll(generateFaceY(maxY, minX, maxX, minZ, maxZ));
            faces.addAll(generateFaceZ(minZ, minX, maxX, minY, maxY));
            faces.addAll(generateFaceZ(maxZ, minX, maxX, minY, maxY));

            horizontalEdges = new ArrayList<>();
            horizontalEdges.addAll(generateLine(minX, maxY, minZ, maxX, maxY, minZ));
            horizontalEdges.addAll(generateLine(maxX, maxY, minZ, maxX, maxY, maxZ));
            horizontalEdges.addAll(generateLine(maxX, maxY, maxZ, minX, maxY, maxZ));
            horizontalEdges.addAll(generateLine(minX, maxY, maxZ, minX, maxY, minZ));

            horizontalEdges.addAll(generateLine(minX, minY, minZ, maxX, minY, minZ));
            horizontalEdges.addAll(generateLine(maxX, minY, minZ, maxX, minY, maxZ));
            horizontalEdges.addAll(generateLine(maxX, minY, maxZ, minX, minY, maxZ));
            horizontalEdges.addAll(generateLine(minX, minY, maxZ, minX, minY, minZ));

            verticalEdges = new ArrayList<>();
            verticalEdges.addAll(generateLine(minX, minY, minZ, minX, maxY, minZ));
            verticalEdges.addAll(generateLine(maxX, minY, minZ, maxX, maxY, minZ));
            verticalEdges.addAll(generateLine(minX, minY, maxZ, minX, maxY, maxZ));
            verticalEdges.addAll(generateLine(maxX, minY, maxZ, maxX, maxY, maxZ));

            corners = new double[][] {
                    { minX, minY, minZ },
                    { maxX, minY, minZ },
                    { minX, minY, maxZ },
                    { maxX, minY, maxZ },
                    { minX, maxY, minZ },
                    { maxX, maxY, minZ },
                    { minX, maxY, maxZ },
                    { maxX, maxY, maxZ },
            };
        }

        /**
         * 向玩家显示选取区域粒子可视化
         * @param player 玩家
         */
        public void show(@NotNull Player player) {
            show(player, Particle.FIREWORKS_SPARK, Particle.VILLAGER_HAPPY, Particle.VILLAGER_HAPPY, null);
        }

        /**
         * 向玩家显示选取区域粒子可视化
         * @param player 玩家
         * @param face 六个面的粒子类型
         * @param horizontalEdge 八条水平边的粒子类型
         * @param verticalEdge 四条垂直边的粒子类型
         * @param corner 八个角的粒子类型
         */
        public void show(@NotNull Player player, @Nullable Particle face, @Nullable Particle horizontalEdge, @Nullable Particle verticalEdge, @Nullable Particle corner) {
            if (face != null) {
                for (double[] p : faces) {
                    spawn(player, face, p);
                }
            }
            if (horizontalEdge != null) {
                for (double[] p : horizontalEdges) {
                    spawn(player, horizontalEdge, p);
                }
            }
            if (verticalEdge != null) {
                for (double[] p : verticalEdges) {
                    spawn(player, verticalEdge, p);
                }
            }
            if (corner != null) {
                for (double[] p : corners) {
                    spawn(player, corner, p);
                }
            }
        }

        private void spawn(Player player, Particle particle, double[] p) {
            player.spawnParticle(particle, p[0], p[1], p[2], 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private static final double STEP = 0.5;
    private static List<double[]> generateFaceX(double x, double y1, double y2, double z1, double z2) {
        List<double[]> points = new ArrayList<>();
        for (double y = y1; y <= y2 + 1e-9; y += STEP)
            for (double z = z1; z <= z2 + 1e-9; z += STEP)
                points.add(new double[] { x, y, z });
        return points;
    }

    private static List<double[]> generateFaceY(double y, double x1, double x2, double z1, double z2) {
        List<double[]> points = new ArrayList<>();
        for (double x = x1; x <= x2 + 1e-9; x += STEP)
            for (double z = z1; z <= z2 + 1e-9; z += STEP)
                points.add(new double[] { x, y, z });
        return points;
    }

    private static List<double[]> generateFaceZ(double z, double x1, double x2, double y1, double y2) {
        List<double[]> points = new ArrayList<>();
        for (double x = x1; x <= x2 + 1e-9; x += STEP)
            for (double y = y1; y <= y2 + 1e-9; y += STEP)
                points.add(new double[] { x, y, z });
        return points;
    }

    private static List<double[]> generateLine(
            double x1, double y1, double z1,
            double x2, double y2, double z2
    ) {
        List<double[]> linePoints = new ArrayList<>();

        double lenX = x2 - x1;
        double lenY = y2 - y1;
        double lenZ = z2 - z1;

        double length = Math.sqrt(lenX * lenX + lenY * lenY + lenZ * lenZ);
        int pointCount = (int) Math.round(length / STEP) + 1;

        for (int i = 0; i < pointCount; i++) {
            double t = (double) i / (pointCount - 1);
            double x = x1 + t * lenX;
            double y = y1 + t * lenY;
            double z = z1 + t * lenZ;
            linePoints.add(new double[] {x, y, z });
        }

        return linePoints;
    }
}
