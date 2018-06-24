package red.man10.man10chinchirorin;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

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
                    Bukkit.broadcastMessage(plugin.prefix + "§6募集終了まで残り§e§l" + time/60 + "分");
                }else if ((time % 10 == 0&&60 > time) || time <= 5 ){
                    Bukkit.broadcastMessage(plugin.prefix + "§6募集終了まで残り§e§l" + time + "秒");
                }

                time--;

            }
        }.runTaskTimer(plugin,0,20);
    }
}
