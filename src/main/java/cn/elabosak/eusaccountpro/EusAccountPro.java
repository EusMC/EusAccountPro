package cn.elabosak.eusaccountpro;

import cn.elabosak.eusaccountpro.controller.AuthController;
import cn.elabosak.eusaccountpro.database.Database;
import cn.elabosak.eusaccountpro.database.JsonDB;
import cn.elabosak.eusaccountpro.database.MySQL;
import cn.elabosak.eusaccountpro.database.SQLite;
import cn.elabosak.eusaccountpro.exception.NotRegistered;
import cn.elabosak.eusaccountpro.handler.onMap;
import cn.elabosak.eusaccountpro.utils.Authenticator;
import cn.elabosak.eusaccountpro.utils.MapRender;
import com.google.gson.Gson;
import com.google.zxing.WriterException;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import static cn.elabosak.eusaccountpro.utils.Authenticator.getTOTPCode;


public final class EusAccountPro extends JavaPlugin {

    public HashMap<Player, Inventory> oldInvs = new HashMap<Player, Inventory>();
    public HashMap<Player, Boolean> loggedIn = new HashMap<>();
    public HashMap<Player, Boolean> verify = new HashMap<>();

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
        if(getDatabase().isPlayerRegistered(event.getPlayer().getUniqueId())){
            event.getPlayer().sendMessage(ChatColor.GREEN.BOLD+"+ EusAccountPro 正在保护你的账户");
            Location odLoc = event.getPlayer().getLocation();
            Location loc = getDatabase().getSafePoint(event.getPlayer().getUniqueId());
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
            event.getPlayer().teleport(loc);
            while(!loggedIn.get(event.getPlayer())){ //重复判断状态
                if(loggedIn.get(event.getPlayer())){
                    break;
                }
            }
            event.getPlayer().teleport(odLoc);
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
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
                        if(getDatabase().getSafePoint(uuid) != null){
                            p.sendMessage(ChatColor.BOLD + "+ EAP -> " + ChatColor.BOLD + "正在创建二步验证QRcode...");
                            oldInvs.put(p, p.getInventory());
                            p.sendMessage(ChatColor.BOLD + "+ EAP -> " + ChatColor.BOLD + "物品栏已保存...");
                            p.getInventory().clear();
                            String secretKey = Authenticator.generateSecretKey(); //生成SecretKey
                            authController.register(p,secretKey); //注册
                            String QRCode_url = Authenticator.getGoogleAuthenticatorQRCode(secretKey, getServer().getName() , p.getName());
                            Authenticator.createQRCode(QRCode_url, "plugins\\EusAccountPro\\QRCode\\"+uuid.toString()+".png", 300 ,300);
                            p.getInventory().addItem(new ItemStack(Material.MAP));
                            getServer().getPluginManager().registerEvents(new onMap(),this); //onMap监听器开启
                            verify.put(p,false);
                            p.sendMessage(ChatColor.GREEN+"请扫描二维码，并使用 /eap verify <code> 进行初始验证");
                            while(!verify.get(p)){
                                if (verify.get(p)){
                                    break;
                                }
                            }
                            PlayerInteractEvent.getHandlerList().unregister(new onMap()); //onMap监听器关闭
                            p.getInventory().clear();
                            p.getInventory().addItem((ItemStack) oldInvs.get(p));
                            p.sendMessage(ChatColor.GREEN.BOLD+"创建成功");
                            return true;
                        }else{
                            p.sendMessage(ChatColor.RED+"尚未设置安全点，请在安全的地方运行"+ChatColor.GREEN.BOLD+" /eap safepoint");
                            return true;
                        }
                    } catch (IOException | WriterException e) {
                        e.printStackTrace();
                        p.sendMessage(ChatColor.RED+"程序异常，创建失败");
                        return true;
                    }

                } else {
                    if (args[0].equalsIgnoreCase("delete")) {
                        //输入eap delete，即判断后删除2fa
                        if(getDatabase().isPlayerRegistered(p.getUniqueId())){
                            if (getDatabase().deletePlayer(p.getUniqueId())){
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
                            if(getDatabase().SafePoint(uuid, safepoint)){
                                p.sendMessage(ChatColor.GREEN.BOLD+"安全点已记录");
                                return true;
                            }else{
                                p.sendMessage(ChatColor.BOLD.RED+"安全点记录失败");
                                return true;
                            }
                        } else {
                            if (args[0].equalsIgnoreCase("verify")){
                                if (sender instanceof Player) {
                                    Scanner scanner = new Scanner(args[1]);
                                    String code = scanner.nextLine();
                                    try {
                                        if (code.equals(getTOTPCode(getDatabase().getSecretKey(p.getUniqueId())))) {
                                            p.sendMessage(ChatColor.GREEN.BOLD+"初始化验证成功");
                                            verify.put(p,true);
                                            return true;
                                        } else {
                                            p.sendMessage(ChatColor.RED.BOLD+"动态密码无效，验证失败");
                                            return true;
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    sender.sendMessage(ChatColor.BOLD + "你必须作为一个玩家执行此命令");
                                    return true;
                                }
                            } else {
                                //仅输入eap或指令错误，显示使用帮助
                                p.sendMessage(ChatColor.GREEN.BOLD+"+++++ EusAccountPro +++++");
                                p.sendMessage(ChatColor.GREEN+"/eap safepoint 记录玩家安全点");
                                p.sendMessage(ChatColor.GREEN+"/eap create 注册EAP");
                                p.sendMessage(ChatColor.GREEN+"/eap delete 注销EAP");
                                p.sendMessage(ChatColor.GREEN+"/eap verify <code> 初始化二步验证");
                                p.sendMessage(ChatColor.GREEN+"/2fa <code> 进服二步验证");
                                p.sendMessage(ChatColor.BLUE+"/eapre [玩家名] 强制删除二步验证 (需要管理员权限)");
                                p.sendMessage(ChatColor.GREEN.BOLD+"----- EusAccountPro -----");
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
                String code = args[0];
                if (code == null) {
                    //什么都没有，显示使用方法
                    sender.sendMessage(ChatColor.RED.BOLD+"数据缺失，请输入 /2fa <code>");
                    return true;
                } else {
                    //待加入：先行判断，1.该玩家是否已激活2fa 2.该玩家是否已经验证过2fa
                    if (loggedIn.getOrDefault(p, false)) {
                        p.sendMessage(ChatColor.AQUA + "您已认证");
                        return true;
                    }
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
            if (sender instanceof Player){
                if (sender.hasPermission("EusAccountPro.admin")) {
                    String input = args[0];
                    if (input == null) {
                        //什么都没有，显示使用方法
                        sender.sendMessage(ChatColor.RED.BOLD+"目标缺失，请输入 /eapre [玩家名]");
                        return true;
                    } else {
                        Player target = Bukkit.getPlayer(args[0]); //定义此为该玩家的名称，接下来验证是否已激活2fa，若为是，则删除其记录
                        if (target == null){
                            sender.sendMessage("请填写目标");
                        }else{
                            UUID uuid = target.getUniqueId();
                            if(getDatabase().isPlayerRegistered(uuid)){
                                if (getDatabase().deletePlayer(uuid)){
                                    sender.sendMessage(ChatColor.GREEN.BOLD+"删除成功");
                                }else{
                                    sender.sendMessage(ChatColor.GREEN.BOLD+"删除失败");
                                }
                            }else{
                                sender.sendMessage(ChatColor.RED.BOLD + "该玩家尚未注册");
                                return true;
                            }
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.BOLD + "你没有使用此命令的权限");
                }
            }else{
                sender.sendMessage(ChatColor.BOLD + "你必须作为一个玩家执行此命令");
                return true;
            }
        }
        return false;
    }

    public Database getDatabase() {
        return database;
    }
}
