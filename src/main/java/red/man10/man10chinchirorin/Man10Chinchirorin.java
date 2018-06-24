package red.man10.man10chinchirorin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import red.man10.man10vaultapiplus.Man10VaultAPI;
import red.man10.man10vaultapiplus.MoneyPoolObject;
import red.man10.man10vaultapiplus.enums.MoneyPoolTerm;
import red.man10.man10vaultapiplus.enums.MoneyPoolType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class Man10Chinchirorin extends JavaPlugin implements Listener {

    ////////////////////////
    //マンチロの根幹となる変数
    public Man10VaultAPI vault = null;
    boolean power = true;
    Timer timer;
    String prefix = "§f§l[§d§lマ§a§lン§f§lチロ§f§l]§r";
    Long totalBets = null;
    MoneyPoolObject totalBet;
    double jackpot = 0;
    FileConfiguration config;
    ////////////////////////
    //マンチロのゲームごとに必要な変数
    boolean gametime = false;
    double onebet = -1;
    ArrayList<UUID> joinplayers;
    UUID parent = null;
    int parenta = -1;
    int maxplayers = -1;
    double parentbal = -1;
    ////////////////////////
    //debug用
    HashMap<UUID,ArrayList<Integer>> debug;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        config = getConfig();
        jackpot = config.getDouble("jackpot");
        if(!config.contains("id")){
            config.set("id",new MoneyPoolObject("Man10Chinchirorin", MoneyPoolTerm.SHORT_TERM, MoneyPoolType.GAMBLE_POOL, "ManChirorin Betting Pool").getId());
            saveConfig();
        }
        totalBets = config.getLong("id");
        totalBet = new MoneyPoolObject("Man10Chinchirorin", totalBets);
        vault = new Man10VaultAPI("ManChirorin");
        timer = new Timer(this);
        MCRData.loadEnable(this);
        RoleData.loadEnable(this);
        getCommand("mcr").setExecutor(new MCRCommand(this));
        Bukkit.getServer().getPluginManager().registerEvents(this,this);
        joinplayers = new ArrayList<>();
        debug = new HashMap<>();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(parent != null) {
            MCRData.cancel();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if(parent == uuid){
            Bukkit.broadcastMessage(prefix+"§4§l親("+p.getDisplayName()+"§4§l)がサーバーから退出したためキャンセルします");
            MCRData.cancel();
        }else if(joinplayers.contains(uuid)){
            Bukkit.broadcastMessage(prefix+"§4§l子("+p.getDisplayName()+"§4§l)がサーバーから退出したためキャンセルします");
            MCRData.cancel();
        }
    }

    public double getJackpot(){
        return jackpot;
    }

    public void addJackpot(Double d){
        config.set("jackpot",jackpot+d);
        saveConfig();
        jackpot = jackpot + d;
    }

    public void takeJackpot(Double d){
        config.set("jackpot",jackpot-d);
        saveConfig();
        jackpot = jackpot - d;
    }
}
