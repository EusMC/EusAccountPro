package cn.elabosak.eusaccountpro;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;

import java.io.File;
import java.util.UUID;


public final class EusAccountPro extends JavaPlugin {

    public Map<Player,String> tempcode = new HashMap<Player,String>();
    public Map<UUID, String> uuid = new HashMap<UUID, String>();

    //这里要写一个监听器，监听玩家在线、离线

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage(ChatColor.GOLD.BOLD+"+++ EusAccountPro Online √ +++");
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        reloadConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage(ChatColor.RED.BOLD+"--- EusAccountPro Offline × ---");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(command.getName().equalsIgnoreCase("eap")){
            if (sender instanceof Player){
                Player p = (Player) sender;
                if (args[0].equalsIgnoreCase("create")){
                    //输入eap create，即判断后创建2fa
                    return true;
                }else{
                    if (args[0].equalsIgnoreCase("delete")){
                        //输入eap delete，即判断后删除2fa
                        return true;
                    }else{
                        if(args[0].equalsIgnoreCase("safepoint")){
                            //输入eap safepoint，当玩家激活了2fa后，需要验证2fa的时候，自动传送到这个坐标，以免遭遇伤害
                        }else{
                            if (args[0].equalsIgnoreCase("")){
                                //仅输入eap，显示使用帮助
                                return true;
                            }else{
                                //错误指令，提示后显示2使用帮助
                                return true;
                            }
                        }
                    }
                }
            }else{
                sender.sendMessage(ChatColor.RED.BOLD+"你必须作为一个玩家执行此命令");
                return true;
            }
        }
        if(command.getName().equalsIgnoreCase("2fa")){
            if (sender instanceof Player){
                //获取用户输入
                Player p = (Player) sender;
                if(args[0] == ""){
                    //什么都没有，显示使用方法
                    return true;
                }else{
                    //待加入：先行判断，1.该玩家是否已激活2fa 2.该玩家是否已经验证过2fa
                    String code = args[0];
                    uuid.put(p.getPlayer().getUniqueId(),code);

                }
            }else{
                sender.sendMessage(ChatColor.RED.BOLD+"你必须作为一个玩家执行此命令");
                return true;
            }
        }
        if(command.getName().equalsIgnoreCase("eapre")){
            if(sender.hasPermission("EusAccountPro.admin")){
                if(args[0] == ""){
                    //什么都没有，显示使用方法
                    return true;
                }else{
                    String player = args[0]; //定义此为该玩家的名称，接下来验证是否已激活2fa，若为是，则删除其记录

                }
            }else{
                sender.sendMessage(ChatColor.RED.BOLD+"你没有使用此命令的权限");
            }
        }
        return false;
    }
}