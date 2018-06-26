package red.man10.man10chinchirorin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import red.man10.man10vaultapiplus.JPYBalanceFormat;
import red.man10.man10vaultapiplus.enums.TransactionCategory;
import red.man10.man10vaultapiplus.enums.TransactionType;

import java.util.UUID;

public class MCRCommand implements CommandExecutor {
    Man10Chinchirorin plugin;
    public MCRCommand (Man10Chinchirorin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("This command can only be executed by a player.");
            return false;
        }
        Player p = (Player) sender;
        if(!plugin.power&&!p.hasPermission("mcr.op")){
            p.sendMessage(plugin.prefix+"§c機能停止中です");
            return true;
        }
        if(!p.hasPermission("mcr.use")){
            p.sendMessage(plugin.prefix+"§cマンチロリンを使う権限がありません");
            return true;
        }
        if(args.length == 0){
            p.sendMessage("§f=========="+plugin.prefix+"§f==========");
            p.sendMessage("");
            if(plugin.parent == null){
                p.sendMessage("§4§l現在マンチロは行われていません");
            }else{
                p.sendMessage("§e§lマンチロが行われています！！ §a§l親: "+ Bukkit.getPlayer(plugin.parent).getDisplayName());
                p.sendMessage("§eベット金額: "+new JPYBalanceFormat(plugin.onebet).getString() +" 必要金額: "+new JPYBalanceFormat(plugin.onebet*5).getString());
                p.sendMessage("§a§l募集人数: §e"+plugin.maxplayers+"人 §2§l参加人数: §e"+plugin.joinplayers.size()+"人 §e§l合計賭け金: "+new JPYBalanceFormat(plugin.totalBet.getBalance()).getString()+"円");
                for(UUID uuid:plugin.joinplayers){
                    p.sendMessage("§c§l"+Bukkit.getPlayer(uuid).getDisplayName());
                }
            }
            p.sendMessage("");
            p.sendMessage("§6/mcr start [賭け金] [最大人数] §f: 親としてマンチロをスタートします");
            p.sendMessage("§6/mcr join §f: マンチロに参加します");
            p.sendMessage("§6/mcr rule §f: ルールを見ます");
            p.sendMessage("§4§lJ§6§lA§e§lC§a§lK§2§lP§b§lO§3§lT§f§l: §e§l"+new JPYBalanceFormat(plugin.getJackpot()).getString()+"円");
            p.sendMessage("§f=========="+plugin.prefix+"§f==========");
            p.sendMessage("§7Version: 1.2");
            p.sendMessage("§cCreated by Mr_IK");
            return true;
        }else if(args.length == 1){
            if(args[0].equalsIgnoreCase("join")){
                if(plugin.vault.getBalance(p.getUniqueId())<plugin.onebet*5){
                    p.sendMessage(plugin.prefix+"§cお金が足りません！ 必要金額: "+new JPYBalanceFormat(plugin.onebet*5).getString());
                    return true;
                }
                if(plugin.parent == null) {
                    p.sendMessage(plugin.prefix+"§cマンチロ中ではありません");
                    return true;
                }
                if(plugin.gametime){
                    p.sendMessage(plugin.prefix+"§cもう定員です");
                    return true;
                }
                if(plugin.joinplayers.contains(p.getUniqueId())){
                    p.sendMessage(plugin.prefix+"§cすでに参加してます。");
                    return true;
                }
                if(plugin.parent==p.getUniqueId()){
                    p.sendMessage(plugin.prefix+"§cあなたは親です。");
                    return true;
                }
                plugin.vault.transferMoneyPlayerToPool(p.getUniqueId(),plugin.totalBet.getId(),plugin.onebet*5,TransactionCategory.GAMBLE,TransactionType.BET,"mcr bet: "+p.getName());
                p.sendMessage(plugin.prefix+"§c§l"+p.getDisplayName()+"§a§lさんが参加しました。");
                for(UUID uuid:plugin.joinplayers){
                    Bukkit.getPlayer(uuid).sendMessage(plugin.prefix+"§c§l"+p.getDisplayName()+"§a§lさんが参加しました。");
                }
                Bukkit.getPlayer(plugin.parent).sendMessage(plugin.prefix+"§c§l"+p.getDisplayName()+"§a§lさんが参加しました。");
                plugin.joinplayers.add(p.getUniqueId());
                if(plugin.joinplayers.size() == plugin.maxplayers){
                    MCRData.gamePush1();
                }
                return true;
            }else if(args[0].equalsIgnoreCase("cancel")){
                if(!p.hasPermission("mcr.op")){
                    p.sendMessage(plugin.prefix+"§cあなたには権限がありません！");
                    return true;
                }
                if(plugin.parent != null) {
                    MCRData.cancel();
                    p.sendMessage(plugin.prefix+"§cキャンセルしました");
                }else{
                    p.sendMessage(plugin.prefix+"§cマンチロ中ではありません");
                }
                return true;
            }else if(args[0].equalsIgnoreCase("off")){
                if(!p.hasPermission("mcr.op")){
                    p.sendMessage(plugin.prefix+"§cあなたには権限がありません！");
                    return true;
                }
                if(plugin.parent != null) {
                    MCRData.cancel();
                    p.sendMessage(plugin.prefix+"§cキャンセルしました");
                    plugin.power = false;
                }else{
                    plugin.power = false;
                }
                p.sendMessage(plugin.prefix+"§cOFFしました。");
                return true;
            }else if(args[0].equalsIgnoreCase("on")){
                if(!p.hasPermission("mcr.op")){
                    p.sendMessage(plugin.prefix+"§cあなたには権限がありません！");
                    return true;
                }
                plugin.power = true;
                p.sendMessage(plugin.prefix+"§aONしました。");
                return true;
            }else if(args[0].equalsIgnoreCase("rule")){
                p.sendMessage("§f=========="+plugin.prefix+"§f==========");
                p.sendMessage("§6役一覧: [1:1:1 jackpotチャンス] [それ以外の三つ揃い ゾロメ] [出目合計10 man10] [3・1・5 サイコー] [出目合計5 dan5]");
                p.sendMessage("§6役一覧: [二つそろって残りが・・ その数字が強さ]");
                p.sendMessage("");
                p.sendMessage("§e配当率: 『サイコー:4倍勝(親のみ)』『ゾロメ:3倍勝』『man10:2倍勝』『dan5:2倍負』 通常:1倍負/勝");
                p.sendMessage("§ejackpotの払い出し金額: 賭け金x10 or jackpotすべて のどちらか金額が低いほう");
                p.sendMessage("§f=========="+plugin.prefix+"§f==========");
                return true;
            }
        }else if(args.length == 3){
            if(args[0].equalsIgnoreCase("start")){
                if(plugin.parent != null) {
                    p.sendMessage(plugin.prefix+"§c現在マンチロ中です！");
                    return true;
                }
                if(!p.hasPermission("mcr.start")){
                    p.sendMessage(plugin.prefix+"§cマンチロリンをスタートする権限がありません");
                    return true;
                }
                double bit = -1;
                int max = -1;
                try{
                    bit = Double.parseDouble(args[1]);
                    max = Integer.parseInt(args[2]);
                }catch (NumberFormatException e){
                    p.sendMessage(plugin.prefix+"§c数字で入力してください。");
                    return true;
                }
                if(bit < 1000000){
                    p.sendMessage(plugin.prefix+"§c賭け金は100万以上の数字を入力してください。");
                    return true;
                }else if(max <= 0||max >= 11){
                    p.sendMessage(plugin.prefix+"§c人数は1以上10以下の数字を入力してください。");
                    return true;
                }
                if(plugin.vault.getBalance(p.getUniqueId())<bit*5*max){
                    p.sendMessage(plugin.prefix+"§c必要金額を持っていません！: "+new JPYBalanceFormat(bit*5*max).getString());
                    return true;
                }
                plugin.vault.transferMoneyPlayerToPool(p.getUniqueId(),plugin.totalBet.getId(),bit*5*max,TransactionCategory.GAMBLE,TransactionType.BET,"mcr start: "+p.getName());
                MCRData.gameStart(p.getUniqueId(),bit,max);
                return true;
            }else if(args[0].equalsIgnoreCase("new")){
                if(plugin.parent != null) {
                    p.sendMessage(plugin.prefix+"§c現在マンチロ中です！");
                    return true;
                }
                if(!p.hasPermission("mcr.start")){
                    p.sendMessage(plugin.prefix+"§cマンチロリンをスタートする権限がありません");
                    return true;
                }
                double bit = -1;
                int max = -1;
                try{
                    bit = Double.parseDouble(args[1]);
                    max = Integer.parseInt(args[2]);
                }catch (NumberFormatException e){
                    p.sendMessage(plugin.prefix+"§c数字で入力してください。");
                    return true;
                }
                if(bit < 1000000){
                    p.sendMessage(plugin.prefix+"§c賭け金は100万以上の数字を入力してください。");
                    return true;
                }else if(max <= 0||max >= 11){
                    p.sendMessage(plugin.prefix+"§c人数は1以上10以下の数字を入力してください。");
                    return true;
                }
                if(plugin.vault.getBalance(p.getUniqueId())<bit*5*max){
                    p.sendMessage(plugin.prefix+"§c必要金額を持っていません！: "+new JPYBalanceFormat(bit*5*max).getString());
                    return true;
                }
                plugin.vault.transferMoneyPlayerToPool(p.getUniqueId(),plugin.totalBet.getId(),bit*5*max,TransactionCategory.GAMBLE,TransactionType.BET,"mcr start: "+p.getName());
                MCRData.gameStart(p.getUniqueId(),bit,max);
                return true;
            }
        }
        p.sendMessage("§f=========="+plugin.prefix+"§f==========");
        p.sendMessage("");
        if(plugin.parent == null){
            p.sendMessage("§4§l現在マンチロは行われていません");
        }else{
            p.sendMessage("§e§lマンチロが行われています！！ §a§l親: "+ Bukkit.getPlayer(plugin.parent).getDisplayName());
            p.sendMessage("§eベット金額: "+new JPYBalanceFormat(plugin.onebet).getString() +" 必要金額: "+new JPYBalanceFormat(plugin.onebet*5).getString());
            p.sendMessage("§a§l募集人数: §e"+plugin.maxplayers+"人 §2§l参加人数: §e"+plugin.joinplayers.size()+"人 §e§l合計賭け金: "+new JPYBalanceFormat(plugin.totalBet.getBalance()).getString()+"円");
            for(UUID uuid:plugin.joinplayers){
                p.sendMessage("§c§l"+Bukkit.getPlayer(uuid).getDisplayName());
            }
        }
        p.sendMessage("");
        p.sendMessage("§6/mcr start [賭け金] [最大人数] §f: 親としてマンチロをスタートします");
        p.sendMessage("§6/mcr join §f: マンチロに参加します");
        p.sendMessage("§6/mcr rule §f: ルールを見ます");
        p.sendMessage("§4§lJ§6§lA§e§lC§a§lK§2§lP§b§lO§3§lT§f§l: §e§l"+new JPYBalanceFormat(plugin.getJackpot()).getString()+"円");
        p.sendMessage("§f=========="+plugin.prefix+"§f==========");
        p.sendMessage("§7Version: 1.2");
        p.sendMessage("§cCreated by Mr_IK");
        return true;
    }
}
