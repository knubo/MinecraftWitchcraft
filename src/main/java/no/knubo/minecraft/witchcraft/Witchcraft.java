package no.knubo.minecraft.witchcraft;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Witchcraft extends JavaPlugin implements Listener {

    public static final String KNUBOSTAIR = "knubostair";
    private Entity witch;

    @Override
    public void onEnable() {
        getLogger().info("Plugin started");
        Class<? extends Event> foo;
        getServer().getPluginManager().registerEvents(this, this);

        this.getCommand("elevator").setExecutor(new ElevatorCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin stopped");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Block block = e.getTo().getBlock().getRelative(BlockFace.DOWN);

        List<MetadataValue> stairMeta = block.getMetadata(KNUBOSTAIR);

        if (stairMeta == null || stairMeta.isEmpty()) {
            return;
        }

        if (block.getLocation().getY() > 154) {
            buildLanding(block);
            return;
        }
        World world = block.getWorld();

        world.setStorm(false);
        world.setThundering(false);

        buildStair(block, stairMeta);
    }

    @EventHandler
    public void onMonsterHurt(EntityDamageByEntityEvent e) {
        if (e.getEntity().getCustomName() != null && e.getEntity().getCustomName().equals("Wabbado") && e.getEntity().getScoreboardTags().contains("knubo")) {
            e.getEntity().remove();
            witch = null;
        }
    }

    private void buildLanding(Block block) {
        Location location = block.getLocation();
        World world = location.getWorld();

        world.setStorm(true);
        world.setThundering(true);
        world.setThunderDuration(60);

        for (int x = 0; x < 30; x++) {
            for (int z = -5; z < 30; z++) {
                int x1 = location.getBlockX() + x;
                int y1 = location.getBlockY();
                int z1 = location.getBlockZ() + z;
                world.getBlockAt(x1, y1, z1).setType(Material.COBBLESTONE);
                world.spawnParticle(Particle.CLOUD, x1, y1 + 15, z1, 1);
            }
        }
        location.add(15, 2, 15);
        if (witch != null) {
            witch.remove();
        }
        witch = world.spawnEntity(location, EntityType.VILLAGER);
        witch.setGlowing(true);
        witch.setCustomName("Wabbado");
        witch.setCustomNameVisible(true);
        witch.addScoreboardTag("knubo");
    }

    private void buildStair(Block block, List<MetadataValue> stairMeta) {
        int stepCount = stairMeta.get(0).asInt();

        for (int i = 0; i < 3; i++) {

            Block newblock = block.getLocation().add(1, 1, stepCount - i).getBlock();

            if (newblock.getType() == Material.COBBLESTONE_STAIRS) {
                continue;
            }

            newblock.setType(Material.COBBLESTONE_STAIRS);
            newblock.setMetadata(KNUBOSTAIR, new FixedMetadataValue(this, i));

            BlockData blockData = newblock.getBlockData();
            ((Directional) blockData).setFacing(BlockFace.EAST);
            newblock.setBlockData(blockData);
        }

        Location baseLocation = block.getLocation().add(1, 2, stepCount - 4);

        for (int y = 0; y < 5; y++) {
            for (int z = 0; z < 5; z++) {
                Block blockToModify = baseLocation.add(0, 0, 1).getBlock();

                boolean drawWall = y == 4 || z == 0 || z == 4;

                blockToModify.setType(drawWall ? Material.COBBLESTONE : Material.AIR);
            }
            baseLocation.add(0, 1, -5);
        }
    }

    @EventHandler
    public void onPlayerEnterGame(PlayerJoinEvent event) {
        Location location = event.getPlayer().getLocation();
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        getLogger().info("current Y coordinate is:" + location.getY());
        if (location.getY() > 80) {
            return;
        }

        location = location.add(2, 0, 0);

        location.getBlock().setType(Material.COBBLESTONE_STAIRS);
        location.getBlock().setMetadata(KNUBOSTAIR, new FixedMetadataValue(this, 1));

        BlockData blockData = location.getBlock().getBlockData();
        ((Directional) blockData).setFacing(BlockFace.EAST);
        location.getBlock().setBlockData(blockData);

    }
}
