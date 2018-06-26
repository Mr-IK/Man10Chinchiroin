package red.man10.man10chinchirorin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import red.man10.man10vaultapiplus.JPYBalanceFormat;

public class Timer {
    Man10Chinchirorin plugin;
    int time;
    public Timer(Man10Chinchirorin plugin){
        this.plugin = plugin;
    }
    public void betTime(){

        time = plugin.config.getInt("time",120);

        new BukkitRunnable(){
            @Override
            public void run() {
                if(plugin.parent == null) {
                    time = 0;
                    cancel();
                    return;
                }
                if(plugin.joinplayers.size()==plugin.maxplayers) {
                    time = 0;
                    cancel();
                    return;
                }

                if (time == 0){
                    MCRData.timeEnd();
                    time = 0;
                    cancel();
                    return;
                }

                if (time % 60 == 0&&3600 > time){
                    for(Player p:Bukkit.getOnlinePlayers()){
                        MCRData.sendHoverText(p,plugin.prefix + "§6募集終了まで残り§e§l" + time/60 + "分","§e参加する(必要: "+ (plugin.onebet * 5)+")","/mcr join");
                    }
                }else if ((time % 10 == 0&&60 > time) || time <= 5 ){
                    for(Player p:Bukkit.getOnlinePlayers()){
                        MCRData.sendHoverText(p,plugin.prefix + "§6募集終了まで残り§e§l" + time + "秒","§e参加する(必要: "+ (plugin.onebet * 5)+")","/mcr join");
                    }
                }

                time--;

            }
        }.runTaskTimer(plugin,0,20);
    }
}
