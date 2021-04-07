package nearbomb.nearbomb;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class NearBomb extends JavaPlugin implements Listener {

    public static JavaPlugin plugin;

    @Override
    public void onEnable() {

        this.plugin = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        getServer().getLogger().info("NearBomb 플러그인 로드 완료");
        detectScheduler(3,3,3);
    }

    @Override
    public void onDisable() {
        getServer().getLogger().info("NearBomb 플러그인 언로드 완료");
    }


    public void detectScheduler(double nearX, double nearY, double nearZ){

        long delay = 3; //폭파 딜레이

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

            public void run(){
                
                for(Player p : Bukkit.getOnlinePlayers()){ //모든 플레이어에 대해
                    List<Entity> nearEntities = p.getNearbyEntities(nearX,nearY,nearZ);
                    for(Entity entity : nearEntities){
                        if(!(entity instanceof  Player))  new Bomb(p, entity, delay); //플레이어 제외
                    }
                }
                
            }

        }, 1l, 1l);

    }

    @EventHandler
    public void onExplosive(BlockExplodeEvent e){
        for(Block b : e.blockList()){
            b.setType(Material.AIR);
        }
    }

}
