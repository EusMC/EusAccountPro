package cn.elabosak.eusaccountpro;

import cn.elabosak.eusaccountpro.controller.AuthController;
import cn.elabosak.eusaccountpro.database.Database;
import cn.elabosak.eusaccountpro.database.JsonDB;
import cn.elabosak.eusaccountpro.database.MySQL;
import cn.elabosak.eusaccountpro.database.SQLite;
import cn.elabosak.eusaccountpro.exception.NotRegistered;
import cn.elabosak.eusaccountpro.utils.Authenticator;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class EusAccountPro extends JavaPlugin {

    Gson gson = new Gson();
    public static Map<Player, String> tempCode = new HashMap<Player, String>();
    public static String uuid;
    public HashMap<Player, Inventory> oldInvs = new HashMap<Player, Inventory>();
    public HashMap<Player, Boolean> loggedIn = new HashMap<>();

    AuthController authController;
    Database database;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage(ChatColor.BOLD + "+++ EusAccountPro Online √ +++");
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
        }
        reloadConfig();

        new EAPConfig(this);

        switch (EAPConfig.dbType) {
            case SQLite:
                // TODO SQLite support
                database = new SQLite();
                break;
            case MySQL:
                // TODO MySQL support
                database = new MySQL();
                break;
            case JSON:
                database = new JsonDB();
            default:
                database = new JsonDB();
        }
        authController = new AuthController(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage(ChatColor.BOLD + "--- EusAccountPro Offline × ---");
    }

    // TODO 这里要写一个监听器，监听玩家在线、离线
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        if(database.isPlayerRegistered(event.getPlayer().getUniqueId()) == true){
            event.getPlayer().sendMessage(ChatColor.GREEN.BOLD+"+ EusAccountPro 正在保护你的账户");
            Location odLoc = event.getPlayer().getLocation();
            Location loc = database.getSafePoint(event.getPlayer().getUniqueId());
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
            event.getPlayer().teleport(loc);
            while(loggedIn.put(event.getPlayer(),false)){ //重复判断状态
                if(loggedIn.put(event.getPlayer(), true)){
                    event.getPlayer().teleport(odLoc);
                    event.getPlayer().setGameMode(GameMode.SURVIVAL);
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("eap")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args[0].equalsIgnoreCase("create")) {
                    //输入eap create，即判断后创建2fa
                    UUID uuid = p.getUniqueId();
                    try {
                        if(database.getSafePoint(uuid) != null){
                            p.sendMessage(ChatColor.BOLD + "+ EAP -> " + ChatColor.BOLD + "正在创建二步验证QRcode...");
                            oldInvs.put(p, p.getInventory());
                            p.sendMessage(ChatColor.BOLD + "+ EAP -> " + ChatColor.BOLD + "物品栏已保存...");
                            p.getInventory().clear();
                            String uuid_string = p.getUniqueId().toString();
                            String secretKey = Authenticator.generateSecretKey(); //生成SecretKey
                            // TODO Generate & display QRCode
                            String get_QRCode = Authenticator.getGoogleAuthenticatorQRCode(secretKey, getServer().getName() , p.getName());


                            return true;
                        }else{
                            p.sendMessage(ChatColor.RED+"尚未设置安全点，请在安全的地方运行"+ChatColor.GREEN.BOLD+" /eap safepoint");
                            return true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        p.sendMessage(ChatColor.RED+"程序异常，创建失败");
                        return true;
                    }

                } else {
                    if (args[0].equalsIgnoreCase("delete")) {
                        //输入eap delete，即判断后删除2fa
                        if(database.isPlayerRegistered(p.getUniqueId()) == true){
                            if (database.deletePlayer(p.getUniqueId()) == true){
                                p.sendMessage(ChatColor.GREEN.BOLD + "删除成功");
                                return true;
                            }else{
                                p.sendMessage(ChatColor.RED + "删除失败");
                                return true;
                            }
                        }else{
                            p.sendMessage(ChatColor.RED+"尚未注册");
                            return true;
                        }
                    } else {
                        if (args[0].equalsIgnoreCase("safepoint")) {
                            //输入eap safepoint，当玩家激活了2fa后，需要验证2fa的时候，自动传送到这个坐标，以免遭遇伤害
                            Location safepoint = p.getLocation();
                            UUID uuid = p.getUniqueId();
                            if(database.SafePoint(uuid,safepoint) == true){
                                p.sendMessage(ChatColor.GREEN.BOLD+"安全点已记录");
                            }else{
                                p.sendMessage(ChatColor.RED+"安全点记录失败");
                            }
                        } else {
                            if (args[0].equalsIgnoreCase("")) {
                                //仅输入eap，显示使用帮助
                                return true;
                            } else {
                                //错误指令，提示后显示使用帮助
                                return true;
                            }
                        }
                    }
                }
            } else {
                sender.sendMessage(ChatColor.BOLD + "你必须作为一个玩家执行此命令");
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("2fa")) {
            if (sender instanceof Player) {
                //获取用户输入
                Player p = (Player) sender;
                if (args[0].equals("")) {
                    //什么都没有，显示使用方法
                    return true;
                } else {
                    //待加入：先行判断，1.该玩家是否已激活2fa 2.该玩家是否已经验证过2fa
                    if (loggedIn.getOrDefault(p, false)) {
                        p.sendMessage(ChatColor.AQUA + "您已认证");
                        return true;
                    }
                    String code = args[0];
                    try {
                        if (authController.verify(p, code)) {
                            // Success
                            loggedIn.put(p, true);
                            p.sendMessage(ChatColor.GREEN + "认证成功");
                        } else {
                            // Invalid code
                            p.sendMessage(ChatColor.YELLOW + "认证失败");
                        }
                    } catch (NotRegistered | IOException e) {
                        p.sendMessage(ChatColor.RED + "尚未注册或程序异常");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.BOLD + "你必须作为一个玩家执行此命令");
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("eapre")) {
            if (sender.hasPermission("EusAccountPro.admin")) {
                if (args[0].equals("")) {
                    //什么都没有，显示使用方法
                    return true;
                } else {
                    Player target = Bukkit.getPlayer(args[0]); //定义此为该玩家的名称，接下来验证是否已激活2fa，若为是，则删除其记录
                    String uuid = target.getUniqueId().toString();
                    File file = new File("/players/"+uuid+".json");
                    if (!file.exists()) {
                        sender.sendMessage(ChatColor.RED.BOLD + "该玩家尚未注册");
                        return true;
                    }else{
                        try{
                            file.delete();
                            sender.sendMessage(ChatColor.GREEN.BOLD + "玩家 " + args[0] + " 的二步验证记录已删除");
                        }catch (RuntimeException e){
                            sender.sendMessage(ChatColor.RED.BOLD + "运行异常," + "玩家 " + args[0] + " 的二步验证记录无法删除");
                        }
                    }
                }
            } else {
                sender.sendMessage(ChatColor.BOLD + "你没有使用此命令的权限");
            }
        }
        return false;
    }

    public Database getDatabase() {
        return database;
    }
}
