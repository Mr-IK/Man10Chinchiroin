package red.man10.man10chinchirorin;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import red.man10.man10vaultapiplus.JPYBalanceFormat;
import red.man10.man10vaultapiplus.enums.TransactionCategory;
import red.man10.man10vaultapiplus.enums.TransactionType;

import java.util.Random;
import java.util.UUID;

public class MCRData {
    static Man10Chinchirorin plugin;
    public static void loadEnable(Man10Chinchirorin plugin){
        MCRData.plugin = plugin;
    }

    public static boolean gameStart(UUID uuid,double onebet,int maxplayer){
        if(plugin.parent != null){
            return false;
        }
        plugin.maxplayers = maxplayer;
        plugin.onebet = onebet;
        plugin.parent = uuid;
        plugin.parentbal = onebet * 5 * maxplayer;
        for(Player p:Bukkit.getOnlinePlayers()){
            sendHoverText(p,plugin.prefix+"§a§l"+Bukkit.getPlayer(uuid).getDisplayName()+"§f§lさんにより§d§l"+maxplayer+"§f§l人募集の§e§l"+new JPYBalanceFormat(onebet).getString()+"円§f§lマンチロが開始されました！§a§l: /mcr","§e参加する(必要: "+ (onebet * 5)+")","/mcr join");
        }
        plugin.timer.betTime();
        return true;
    }

    public static void reset(){
        for(UUID uuid:plugin.joinplayers){
            plugin.totalBet.transferBalanceToPlayer(uuid,plugin.onebet*5,TransactionCategory.GAMBLE,TransactionType.DEPOSIT,"mcr cancel: "+Bukkit.getPlayer(uuid).getName());
        }
        plugin.joinplayers.clear();
        plugin.onebet = -1;
        plugin.parent = null;
        plugin.parenta = -1;
        plugin.parentbal = -1;
        plugin.maxplayers = -1;
        plugin.gametime = false;
        plugin.vault.giveCountyMoney(plugin.totalBet.getBalance(),TransactionType.FEE,"add jackpot bal");
        plugin.vault.takeMoneyPoolMoney(plugin.totalBets,plugin.totalBet.getBalance(),TransactionType.FEE,"take jackpot bal");
        Bukkit.broadcastMessage(plugin.prefix+"§a§lマンチロが終了しました。");
    }

    public static void timeEnd(){
        plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),plugin.parent,plugin.parentbal,TransactionCategory.GAMBLE,TransactionType.DEPOSIT,"mcr parent deposit: "+Bukkit.getPlayer(plugin.parent).getName());
        Bukkit.broadcastMessage(plugin.prefix+"§4§l子が集まらなかったため中止しました。");
        reset();
    }

    public static void cancel(){
        plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),plugin.parent,plugin.parentbal,TransactionCategory.GAMBLE,TransactionType.DEPOSIT,"mcr parent deposit: "+Bukkit.getPlayer(plugin.parent).getName());
        Bukkit.broadcastMessage(plugin.prefix+"§4§lキャンセルされました。");
        reset();
    }

    public static void gamePush1(){
        plugin.gametime = true;
        Bukkit.broadcastMessage(plugin.prefix+"§a§lマンチロがスタートしました！");
        Bukkit.broadcastMessage(plugin.prefix+"§a§l"+Bukkit.getPlayer(plugin.parent).getDisplayName()+"§f§lさん(親)がサイコロを振っています…§e§l§kaaa");
        new BukkitRunnable(){
            @Override
            public void run() {
                cancel();
                if(!plugin.gametime){
                    return;
                }
                Random rnd1 = new Random();
                Random rnd2 = new Random();
                Random rnd3 = new Random();
                int dice1 = rnd1.nextInt(5)+1;
                int dice2 = rnd2.nextInt(5)+1;
                int dice3 = rnd3.nextInt(5)+1;
                Bukkit.broadcastMessage(plugin.prefix+"§a§lﾊﾟｶｯ！  §f§l "+dice1+"・"+dice2+"・"+dice3+" ！！");
                String result = RoleData.mainhantei(dice1,dice2,dice3);
                if(result.equalsIgnoreCase("ナシ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§a§l役無し (ﾟ∀ﾟ)ｷﾀｺﾚ!!");
                    oyaLose(1.0);
                }else if(result.equalsIgnoreCase("dan5")){
                    Bukkit.broadcastMessage(plugin.prefix+"§6§lダンゴ ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!!");
                    oyaLose(2.0);
                }else if(result.equalsIgnoreCase("イチ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§e§lイチ (ﾟ∀ﾟ)ｷﾀｺﾚ!!");
                    oyaLose(1.0);
                }else if(result.equalsIgnoreCase("ニ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§f§lニが役に決まりました！");
                    plugin.parenta = 2;
                    childturn();
                }else if(result.equalsIgnoreCase("サン")){
                    Bukkit.broadcastMessage(plugin.prefix+"§f§lサンが役に決まりました！");
                    plugin.parenta = 3;
                    childturn();
                }else if(result.equalsIgnoreCase("シ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§f§lシが役に決まりました！");
                    plugin.parenta = 4;
                    childturn();
                }else if(result.equalsIgnoreCase("ゴ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§f§lゴが役に決まりました！");
                    plugin.parenta = 5;
                    childturn();
                }else if(result.equalsIgnoreCase("ロ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§c§lロ (ﾟ∀ﾟ)ｷﾀｺﾚ!!");
                    oyaWin(1.0);
                }else if(result.equalsIgnoreCase("man10")){
                    Bukkit.broadcastMessage(plugin.prefix+"§c§lマンジュウ (ﾟ∀ﾟ)ｷﾀｺﾚ!!");
                    oyaWin(2.0);
                }else if(result.equalsIgnoreCase("ゾロメ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§4§lゾロメ ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!!");
                    oyaWin(3.0);
                }else if(result.equalsIgnoreCase("ピンゾロ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§4§lJ§6§lA§e§lC§a§lK§2§lP§b§lO§3§lT§f§l チャンス ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!!");
                    Oyajackpotchance();
                }else{
                    Bukkit.broadcastMessage(plugin.prefix+"§4エラー発生。未知の目です。");
                    reset();
                }
            }
        }.runTaskTimer(plugin,100,20);
    }

    public static void Oyajackpotchance(){
        Bukkit.broadcastMessage(plugin.prefix+"§4§lJ§6§lA§e§lC§a§lK§2§lP§b§lO§3§lT§f§l チャンスサイコロを振っています…§e§l§kaaa");
        new BukkitRunnable(){
            @Override
            public void run() {
                cancel();
                if(!plugin.gametime){
                    return;
                }
                Random rnd1 = new Random();
                int dice1 = rnd1.nextInt(5)+1;
                Bukkit.broadcastMessage(plugin.prefix+"§a§lﾊﾟｶｯ！  §f§l "+dice1+"！！");
                if(dice1==1){
                    Bukkit.broadcastMessage(plugin.prefix+"§0§l§kaaaaa§4§lJ§6§lA§e§lC§a§lK§2§lP§b§lO§3§lT§0§l§kaaaaa§6§l§n§o ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!!");
                    Bukkit.broadcastMessage(plugin.prefix+"§e§l結果: §a§l親の勝利！！");
                    double jack = -1;
                    if(plugin.getJackpot() < plugin.onebet * 10){
                        jack = plugin.getJackpot();
                    }else {
                        jack = plugin.onebet * 10;
                    }
                    plugin.takeJackpot(jack);
                    plugin.vault.givePlayerMoney(plugin.parent,jack,TransactionType.DEPOSIT,"mcr jackpot!! user: "+Bukkit.getPlayer(plugin.parent).getName());
                    plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),plugin.parent,plugin.parentbal,TransactionCategory.GAMBLE,TransactionType.WIN,"mcr jackpot!! user: "+Bukkit.getPlayer(plugin.parent).getName());
                    Bukkit.broadcastMessage(plugin.prefix+"§a§l"+Bukkit.getPlayer(plugin.parent).getDisplayName()+"§f§l: §e§l"+new JPYBalanceFormat(plugin.onebet*5*plugin.maxplayers).getString()+"円 → "+new JPYBalanceFormat(plugin.parentbal + jack).getString()+"円");
                }else{
                    Bukkit.broadcastMessage(plugin.prefix+"§7§l残念！外れた！");
                    Bukkit.broadcastMessage(plugin.prefix+"§e§l結果: §a§l引き分け！");
                    plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),plugin.parent,plugin.parentbal,TransactionCategory.GAMBLE,TransactionType.WIN,"mcr parent deposit: "+Bukkit.getPlayer(plugin.parent).getName());
                }
                reset();
            }
        }.runTaskTimer(plugin,100,20);
    }

    public static void Childjackpotchance(UUID uuid){
        jackskip = true;
        Bukkit.broadcastMessage(plugin.prefix+"§4§lJ§6§lA§e§lC§a§lK§2§lP§b§lO§3§lT§f§l チャンスサイコロを振っています…§e§l§kaaa");
        new BukkitRunnable(){
            @Override
            public void run() {
                cancel();
                if(!plugin.gametime){
                    return;
                }
                Random rnd1 = new Random();
                int dice1 = rnd1.nextInt(5)+1;
                Bukkit.broadcastMessage(plugin.prefix+"§a§lﾊﾟｶｯ！  §f§l "+dice1+"！！");
                if(dice1==1){
                    Bukkit.broadcastMessage(plugin.prefix+"§0§l§kaaaaa§4§lJ§6§lA§e§lC§a§lK§2§lP§b§lO§3§lT§0§l§kaaaaa§6§l§n§o ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!!");
                    Bukkit.broadcastMessage(plugin.prefix+"§e§l結果: §a§l子の勝利！！");
                    double jack = -1;
                    if(plugin.getJackpot() < plugin.onebet * 10){
                        jack = plugin.getJackpot();
                    }else {
                        jack = plugin.onebet * 10;
                    }
                    plugin.takeJackpot(jack);
                    plugin.vault.givePlayerMoney(uuid,jack,TransactionType.WIN,"mcr jackpot!! deposit: "+Bukkit.getPlayer(uuid).getName());
                    plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),uuid,plugin.onebet*5,TransactionCategory.GAMBLE,TransactionType.DEPOSIT,"mcr jackpot!! deposit: "+Bukkit.getPlayer(uuid).getName());
                    Bukkit.broadcastMessage(plugin.prefix+"§a§l"+Bukkit.getPlayer(uuid).getDisplayName()+"§f§l: §e§l"+new JPYBalanceFormat(plugin.onebet*5).getString()+"円 → "+new JPYBalanceFormat(plugin.onebet*5 + jack).getString()+"円");
                }else{
                    Bukkit.broadcastMessage(plugin.prefix+"§7§l残念！外れた！");
                    draw(uuid);
                }
                plugin.joinplayers.remove(uuid);
                jackskip = false;
            }
        }.runTaskTimer(plugin,100,20);
    }

    public static boolean jackskip = false;

    public static void childturn(){
        Bukkit.broadcastMessage(plugin.prefix+"§a§l子のターンが開始されました！");
        new BukkitRunnable(){
            @Override
            public void run() {
                if(plugin.joinplayers.size()==0){
                    cancel();
                    if(!plugin.gametime){
                        return;
                    }
                    plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),plugin.parent,plugin.parentbal - (plugin.parentbal/100),TransactionCategory.GAMBLE,TransactionType.DEPOSIT,"mcr parent deposit: "+Bukkit.getPlayer(plugin.parent).getName());
                    Bukkit.broadcastMessage(plugin.prefix+"§a§l"+Bukkit.getPlayer(plugin.parent).getDisplayName()+"§f§l: §e§l"+new JPYBalanceFormat(plugin.onebet*5*plugin.maxplayers).getString()+"円 → "+new JPYBalanceFormat(plugin.parentbal).getString()+"円§e(うち手数料"+new JPYBalanceFormat((plugin.parentbal/100)).getString()+"円)");
                    plugin.addJackpot(plugin.parentbal/100);
                    reset();
                    return;
                }
                if(!jackskip) {
                    UUID uuid = plugin.joinplayers.get(0);
                    childbattle(uuid);
                }
            }
        }.runTaskTimer(plugin,20,120);
    }

    public static void childbattle(UUID uuid){
        Bukkit.broadcastMessage(plugin.prefix+"§a§l"+Bukkit.getPlayer(uuid).getDisplayName()+"§f§lさん(子)がサイコロを振っています…§e§l§kaaa");
        new BukkitRunnable(){
            @Override
            public void run() {
                cancel();
                if(!plugin.gametime){
                    return;
                }
                Random rnd1 = new Random();
                Random rnd2 = new Random();
                Random rnd3 = new Random();
                int dice1 = rnd1.nextInt(5)+1;
                int dice2 = rnd2.nextInt(5)+1;
                int dice3 = rnd3.nextInt(5)+1;
                Bukkit.broadcastMessage(plugin.prefix+"§a§lﾊﾟｶｯ！  §f§l "+dice1+"・"+dice2+"・"+dice3+" ！！");
                String result = RoleData.mainhantei(dice1,dice2,dice3);
                if(result.equalsIgnoreCase("ナシ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§a§l役無し (ﾟ∀ﾟ)ｷﾀｺﾚ!!");
                    vsOya(true,uuid,1.0);
                }else if(result.equalsIgnoreCase("dan5")){
                    Bukkit.broadcastMessage(plugin.prefix+"§6§lダンゴ ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!!");
                    vsOya(true,uuid,2.0);
                }else if(result.equalsIgnoreCase("イチ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§e§lイチ (ﾟ∀ﾟ)ｷﾀｺﾚ!!");
                    vsOya(true,uuid,1.0);
                }else if(result.equalsIgnoreCase("ニ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§f§lニが役に決まりました！");
                    if(plugin.parenta == 2){
                        draw(uuid);
                    }else {
                        vsOya(true,uuid,1.0);
                    }
                }else if(result.equalsIgnoreCase("サン")){
                    Bukkit.broadcastMessage(plugin.prefix+"§f§lサンが役に決まりました！");
                    if(plugin.parenta == 3){
                        draw(uuid);
                    }else if(plugin.parenta > 3){
                        vsOya(true,uuid,1.0);
                    }else {
                        vsOya(false,uuid,1.0);
                    }
                }else if(result.equalsIgnoreCase("シ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§f§lシが役に決まりました！");
                    if(plugin.parenta == 4){
                        draw(uuid);
                    }else if(plugin.parenta > 4){
                        vsOya(true,uuid,1.0);
                    }else {
                        vsOya(false,uuid,1.0);
                    }
                }else if(result.equalsIgnoreCase("ゴ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§f§lゴが役に決まりました！");
                    if(plugin.parenta == 5){
                        draw(uuid);
                    }else {
                        vsOya(false,uuid,1.0);
                    }
                }else if(result.equalsIgnoreCase("ロ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§c§lロ (ﾟ∀ﾟ)ｷﾀｺﾚ!!");
                    vsOya(false,uuid,1.0);
                }else if(result.equalsIgnoreCase("man10")){
                    Bukkit.broadcastMessage(plugin.prefix+"§c§lマンジュウ (ﾟ∀ﾟ)ｷﾀｺﾚ!!");
                    vsOya(false,uuid,2.0);
                }else if(result.equalsIgnoreCase("ゾロメ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§4§lゾロメ ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!!");
                    vsOya(false,uuid,3.0);
                }else if(result.equalsIgnoreCase("ピンゾロ")){
                    Bukkit.broadcastMessage(plugin.prefix+"§4§lJ§6§lA§e§lC§a§lK§2§lP§b§lO§3§lT§f§l チャンス ｷﾀ━━━━(ﾟ∀ﾟ)━━━━!!");
                    Childjackpotchance(uuid);
                }else{
                    Bukkit.broadcastMessage(plugin.prefix+"§4エラー発生。未知の目です。");
                    reset();
                }
            }
        }.runTaskTimer(plugin,100,20);
    }

    public static void oyaWin(double bairitu){
        Bukkit.broadcastMessage(plugin.prefix+"§e§l結果: §a§l親の勝利！！");
        double with = plugin.onebet * bairitu * plugin.maxplayers;
        plugin.parentbal = with + plugin.parentbal;
        double retn = (plugin.onebet * 5) - (plugin.onebet * bairitu) - (plugin.onebet/100);
        for(UUID uuid:plugin.joinplayers){
            plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),uuid,retn,TransactionCategory.GAMBLE,TransactionType.DEPOSIT,"mcr lose return: "+Bukkit.getPlayer(uuid).getName());
            Bukkit.broadcastMessage(plugin.prefix+"§c§l"+Bukkit.getPlayer(uuid).getDisplayName()+"§f§l: §e§l"+new JPYBalanceFormat(plugin.onebet*5).getString()+"円 → "+new JPYBalanceFormat(retn+(plugin.onebet/100)).getString()+"円§e(うち手数料"+new JPYBalanceFormat((plugin.onebet/100)).getString()+"円)");
            plugin.addJackpot(plugin.onebet/100);
        }
        plugin.joinplayers.clear();
        plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),plugin.parent,plugin.parentbal - (plugin.parentbal/100),TransactionCategory.GAMBLE,TransactionType.DEPOSIT,"mcr parent deposit: "+Bukkit.getPlayer(plugin.parent).getName());
        Bukkit.broadcastMessage(plugin.prefix+"§a§l"+Bukkit.getPlayer(plugin.parent).getDisplayName()+"§f§l: §e§l"+new JPYBalanceFormat(plugin.onebet*5*plugin.maxplayers).getString()+"円 → "+new JPYBalanceFormat(plugin.parentbal).getString()+"円§e(うち手数料"+new JPYBalanceFormat((plugin.parentbal/100)).getString()+"円)");
        plugin.addJackpot(plugin.parentbal/100);
        reset();
    }
    public static void oyaLose(double bairitu){
        Bukkit.broadcastMessage(plugin.prefix+"§e§l結果: §c§l子の勝利！！");
        double with = plugin.onebet * bairitu ;
        plugin.parentbal = plugin.parentbal - (with*plugin.maxplayers);
        for(UUID uuid:plugin.joinplayers){
            Bukkit.broadcastMessage(plugin.prefix+"§c§l"+Bukkit.getPlayer(uuid).getDisplayName()+"§f§l: §e§l"+new JPYBalanceFormat(plugin.onebet*5).getString()+"円 → "+new JPYBalanceFormat(with + (plugin.onebet * 5)).getString()+"円§e(うち手数料"+new JPYBalanceFormat((plugin.onebet/100)).getString()+"円)");
            plugin.addJackpot(plugin.onebet/100);
            plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),uuid,with + (plugin.onebet * 5) - (plugin.onebet/100) ,TransactionCategory.GAMBLE,TransactionType.DEPOSIT,"mcr win: "+Bukkit.getPlayer(uuid).getName());
        }
        plugin.joinplayers.clear();
        plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),plugin.parent,plugin.parentbal - (plugin.parentbal/100),TransactionCategory.GAMBLE,TransactionType.DEPOSIT,"mcr parent deposit: "+Bukkit.getPlayer(plugin.parent).getName());
        Bukkit.broadcastMessage(plugin.prefix+"§a§l"+Bukkit.getPlayer(plugin.parent).getDisplayName()+"§f§l: §e§l"+new JPYBalanceFormat(plugin.onebet*5*plugin.maxplayers).getString()+"円 → "+new JPYBalanceFormat(plugin.parentbal).getString()+"円§e(うち手数料"+new JPYBalanceFormat((plugin.parentbal/100)).getString()+"円)");
        plugin.addJackpot(plugin.parentbal/100);
        reset();
    }

    public static void vsOya(boolean Oyawin,UUID uuid,double bairitu){
        if(Oyawin){
            Bukkit.broadcastMessage(plugin.prefix+"§e§l結果: §a§l親の勝利！！");
            double with = plugin.onebet * bairitu;
            plugin.parentbal = plugin.parentbal + with;
            double retn = (plugin.onebet * 5) - (plugin.onebet * bairitu);
            Bukkit.broadcastMessage(plugin.prefix+"§a§l"+Bukkit.getPlayer(uuid).getDisplayName()+"§f§l: §e§l"+new JPYBalanceFormat(plugin.onebet*5).getString()+"円 → "+new JPYBalanceFormat(retn).getString()+"円§e(うち手数料"+new JPYBalanceFormat((plugin.onebet/100)).getString()+"円)");
            plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),uuid,retn - (plugin.onebet/100),TransactionCategory.GAMBLE,TransactionType.DEPOSIT,"mcr lose return: "+Bukkit.getPlayer(uuid).getName());
            plugin.addJackpot(plugin.onebet/100);
        }else{
            Bukkit.broadcastMessage(plugin.prefix+"§e§l結果: §c§l子の勝利！！");
            double with = plugin.onebet * bairitu;
            plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),uuid,with + (plugin.onebet * 5) - (plugin.onebet/100),TransactionCategory.GAMBLE,TransactionType.WIN,"mcr win: "+Bukkit.getPlayer(uuid).getName());
            Bukkit.broadcastMessage(plugin.prefix+"§c§l"+Bukkit.getPlayer(uuid).getDisplayName()+"§f§l: §e§l"+new JPYBalanceFormat(plugin.onebet*5).getString()+"円 → "+new JPYBalanceFormat(with + (plugin.onebet * 5)).getString()+"円§e(うち手数料"+new JPYBalanceFormat((plugin.onebet/100)).getString()+"円)");
            plugin.addJackpot(plugin.onebet/100);
            plugin.parentbal = plugin.parentbal - (with);
        }
        plugin.joinplayers.remove(uuid);
    }

    public static void draw(UUID uuid){
        Bukkit.broadcastMessage(plugin.prefix+"§e§l結果: §a§l引き分け！！");
        Bukkit.broadcastMessage(plugin.prefix+"§c§l"+Bukkit.getPlayer(uuid).getDisplayName()+"§f§l: §e§l"+new JPYBalanceFormat(plugin.onebet*5).getString()+"円 → "+new JPYBalanceFormat(plugin.onebet*5).getString()+"円");
        plugin.vault.transferMoneyPoolToPlayer(plugin.totalBet.getId(),uuid,plugin.onebet*5,TransactionCategory.GAMBLE,TransactionType.DEPOSIT,"mcr draw: "+Bukkit.getPlayer(uuid).getName());
        plugin.joinplayers.remove(uuid);
    }

    public static void sendHoverText(Player p, String text, String hoverText, String command){
        //////////////////////////////////////////
        //      ホバーテキストとイベントを作成する
        HoverEvent hoverEvent = null;
        if(hoverText != null){
            BaseComponent[] hover = new ComponentBuilder(hoverText).create();
            hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover);
        }

        //////////////////////////////////////////
        //   クリックイベントを作成する
        ClickEvent clickEvent = null;
        if(command != null){
            clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,command);
        }

        BaseComponent[] message = new ComponentBuilder(text).event(hoverEvent).event(clickEvent). create();
        p.spigot().sendMessage(message);
    }


}
