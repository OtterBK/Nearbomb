package nearbomb.nearbomb;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Bomb {

    public static ArrayList<Entity> bombList = new ArrayList<Entity>(); //폭탄 부착된 리스트

    private final long period = 1; //단위

    private Player owner;
    private Location onwer_lastLoc;
    private long leftTime = 3;
    private Entity entity;
    private int timerID = -1;

    public Bomb(Player player, Entity entity, long delay){

        this.owner = player;
        this.leftTime = (long)((double)delay / period * 20);
        this.entity = entity;


        bombTimer();
    }

    public void bombTimer(){

        if(bombList.contains(entity)) return;

        bombList.add(entity);
        this.onwer_lastLoc = owner.getLocation();

        final Hologram hologram = HologramsAPI.createHologram(NearBomb.plugin, entity.getLocation().clone().add(0, 1.5f, 0));
        hologram.appendTextLine("§4§l0:0" + (int)(leftTime / (20 / period)));
        entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.0f); //초기 효과

        timerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(NearBomb.plugin, new Runnable(){
            public void run(){
                if(entity.isDead()) {
                    hologram.delete();
                    bombList.remove(entity);
                    return; //이미 죽었으면 패스
                }
                if(leftTime == 0) {
//                    entity.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, entity.getLocation(), 1, 0.0F, 0.0f, 0.0f, 0.0f); //폭파 효과
//                    entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

                    entity.getWorld().createExplosion(entity.getLocation(), 1.5f);

                    hologram.delete();

                    //엔티티 타입에 따라 다른 삭제 방식
                    if(entity instanceof LivingEntity){ //몹의 경우 데미지 줘서 없애기
                        LivingEntity lvEntity = (LivingEntity) entity;
                        lvEntity.damage(1000000);
                    } else if(entity instanceof Item){
                        entity.remove(); //아이템의 경우 삭제
                    } else { //그 외의 경우 그냥 삭제
                        entity.remove();
                    }

                    bombList.remove(entity);
                    Bukkit.getScheduler().cancelTask(timerID);

                } else {
                    if(owner.getLocation().distance(onwer_lastLoc) == 0){
                        return;
                    } else {
                        onwer_lastLoc = owner.getLocation();
                    }

                    hologram.teleport(entity.getLocation().clone().add(0, 1.5f, 0));
                    if(leftTime % 10 == 0){
                        TextLine hl = (TextLine)hologram.getLine(0);
                        hl.setText("§4§l0:0" + (int)(leftTime / (20 / period)));
                        entity.getWorld().spawnParticle(Particle.FLAME, entity.getLocation(), 10, 0.0F, 0.0f, 0.0f, 0.2f); //타이머 효과
                        entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_NOTE_HAT, 1.0f, 1.0f);
                    }
                    leftTime--;
                }

            }
        },0l, period);
    }

}
