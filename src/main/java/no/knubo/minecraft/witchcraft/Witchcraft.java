package no.knubo.minecraft.witchcraft;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Witchcraft extends JavaPlugin implements Listener {

    public static final String KNUBOSTAIR = "knubostair";

    @Override
    public void onEnable() {
        getLogger().info("Plugin started");
        Class<? extends Event> foo;
        getServer().getPluginManager().registerEvents(this, this);
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

        if(block.getLocation().getY() > 154) {
            return;
        }

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

        location = location.add(2, 0, 0);

        location.getBlock().setType(Material.COBBLESTONE_STAIRS);
        location.getBlock().setMetadata(KNUBOSTAIR, new FixedMetadataValue(this, 1));

        BlockData blockData = location.getBlock().getBlockData();
        ((Directional) blockData).setFacing(BlockFace.EAST);
        location.getBlock().setBlockData(blockData);

    }
}
