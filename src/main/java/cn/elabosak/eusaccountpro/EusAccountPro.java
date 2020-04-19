package cn.elabosak.eusaccountpro;

import cn.elabosak.eusaccountpro.controller.AuthController;
import cn.elabosak.eusaccountpro.database.Database;
import cn.elabosak.eusaccountpro.database.JsonDB;
import cn.elabosak.eusaccountpro.database.MySQL;
import cn.elabosak.eusaccountpro.database.SQLite;
import cn.elabosak.eusaccountpro.exception.NotRegistered;
import cn.elabosak.eusaccountpro.utils.Authenticator;
import com.google.zxing.WriterException;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.*;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;


public final class EusAccountPro extends JavaPlugin implements Listener{

    public HashMap<Player, ItemStack[]> oldInvs = new HashMap<Player, ItemStack[]>();
    public HashMap<Player, Boolean> loggedIn = new HashMap<>();
    public HashMap<Player, Boolean> verify = new HashMap<>();
    public HashMap<Player, Location> odloc = new HashMap<>();
    public HashMap<Player, GameMode> odgmode = new HashMap<>();
    public HashMap<Player, Boolean> isCreating = new HashMap<>();
    public HashMap<Player, Boolean> verifyHigh = new HashMap<>();

    AuthController authController;
    Database database;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "+++ EusAccountPro Online √ +++");
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
        getServer().getPluginManager().registerEvents(this,this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "--- EusAccountPro Offline × ---");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        verify.put(event.getPlayer(),true);
        isCreating.put(event.getPlayer(),false);
        verifyHigh.put(event.getPlayer(),false);
        if(getDatabase().isPlayerRegistered(event.getPlayer().getUniqueId())){
            event.getPlayer().sendMessage(ChatColor.GREEN+"§l+ EusAccountPro is protecting your account +");
            Location odLoc = event.getPlayer().getLocation();
            odloc.put(event.getPlayer(),odLoc);
            Location safePoint = getDatabase().getSafePoint(event.getPlayer().getUniqueId());
            odgmode.put(event.getPlayer(),event.getPlayer().getGameMode());
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
            event.getPlayer().teleport(safePoint);
            oldInvs.put(event.getPlayer(),event.getPlayer().getInventory().getContents());
            event.getPlayer().getInventory().clear();
            event.getPlayer().sendMessage(ChatColor.GREEN+"Use "+ChatColor.GOLD+"/2fa <code>"+ChatColor.GREEN+" to verify");
        }else{
            event.getPlayer().sendMessage(ChatColor.BLUE+"§lEAP -> EusAccountPro has been launched -");
            event.getPlayer().sendMessage(ChatColor.GREEN+"§lEAP -> Use "+ChatColor.GOLD+"/eap create"+ChatColor.GREEN+"§l to create 2-step verification -");
            verifyHigh.put(event.getPlayer(),false);
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("eap")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length >= 1){
                    if (args[0].equalsIgnoreCase("create")) {
                        UUID uuid = p.getUniqueId();
                            if(args.length == 1){
                                try {
                                    if(getDatabase().getSafePoint(uuid) != null){
                                        if(isCreating.get(p)){
                                            p.sendMessage(ChatColor.RED+"§lYou are already creating EAP");
                                            return true;
                                        }else{
                                            isCreating.put(p,true);
                                            p.sendMessage(ChatColor.GREEN + "§l+ EAP -> " + ChatColor.GOLD + "Creating 2FA QRCode ...");
                                            oldInvs.put(p, p.getInventory().getContents());
                                            p.sendMessage(ChatColor.GREEN + "§l+ EAP -> " + ChatColor.GOLD + "The inventory is saved ...");
                                            p.getInventory().clear();
                                            String secretKey = Authenticator.generateSecretKey();
                                            try {
                                                authController.register(p,secretKey);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                                p.sendMessage(ChatColor.RED+"Abnormal program, abnormal authController.register()");
                                                return true;
                                            }
                                            try {
                                                String QRCode_url = Authenticator.getGoogleAuthenticatorQRCode(authController.getSecretKey(p), p.getName() , getConfig().getString("Account.Display"));
                                                Authenticator.createQRCode(QRCode_url, "plugins/EusAccountPro/QRCode/",uuid.toString()+".png", 128 ,128);
                                            } catch (WriterException | IOException e) {
                                                e.printStackTrace();
                                                p.sendMessage(ChatColor.RED+"Program exception, createQRCode() exception");
                                                return true;
                                            }
                                            ItemStack map = new ItemStack(Material.FILLED_MAP);
                                            MapView view = getServer().createMap(getServer().getWorlds().get(0));
                                            for(MapRenderer renderer : view.getRenderers())
                                                view.removeRenderer(renderer);
                                            view.addRenderer(new MapRenderer() {
                                                @Override
                                                public void render(MapView map, MapCanvas canvas, Player player) {
                                                    BufferedImage img;
                                                    try{
                                                        img = ImageIO.read(new File("plugins/EusAccountPro/QRCode/"+player.getUniqueId().toString()+".png"));
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                        return;
                                                    }
                                                    map.setScale(MapView.Scale.NORMAL);
                                                    canvas.drawImage(0,0,img);
                                                }
                                            });
                                            MapMeta mapMeta = ((MapMeta)map.getItemMeta());
                                            mapMeta.setMapView(view);
                                            map.setItemMeta(mapMeta);
                                            p.getInventory().setItem(4,map);
                                            p.getInventory().setHeldItemSlot(4);
                                            verify.put(p,false);
                                            p.sendMessage(ChatColor.GREEN+"Please scan the QRCode and use "+ChatColor.GOLD+"/eap verify <code>"+ChatColor.GREEN+" for initial verification");
                                            p.sendMessage(ChatColor.GOLD+"If you cannot scan the QRCode, please enter the following key "+authController.getSecretKey(p));
                                            return true;
                                        }
                                    }else{
                                        p.sendMessage(ChatColor.RED+"No safety point has been set, please run"+ChatColor.GREEN+"§l /eap safepoint"+ChatColor.RED+" in a safe place...");
                                        return true;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    p.sendMessage(ChatColor.RED+"Program exception, getSafePoint() exception");
                                    return true;
                                }
                            }else{
                                p.sendMessage(ChatColor.RED+"Too much data, please use "+ChatColor.GREEN+"/eap creat");
                            }

                    } else {
                        if (args[0].equalsIgnoreCase("delete")) {
                            //输入eap delete，即判断后删除2fa
                            if(isCreating.get(p)){
                                p.sendMessage(ChatColor.RED+"§lYou are already creating EAP");
                                return true;
                            }else{
                                if(!loggedIn.get(p)){
                                    p.sendMessage(ChatColor.RED+"§lPlease verify first");
                                    return true;
                                }else{
                                    if(getDatabase().isPlayerRegistered(p.getUniqueId())){
                                        if (getDatabase().deletePlayer(p.getUniqueId())){
                                            p.sendMessage(ChatColor.GREEN + "§lSuccessfully deleted");
                                            return true;
                                        }else{
                                            p.sendMessage(ChatColor.RED + "§lFailed to delete");
                                            return true;
                                        }
                                    }else{
                                        p.sendMessage(ChatColor.RED+"§lNot yet registered");
                                        return true;
                                    }
                                }
                            }
                        } else {
                            if (args[0].equalsIgnoreCase("safepoint")) {
                                //输入eap safepoint，当玩家激活了2fa后，需要验证2fa的时候，自动传送到这个坐标，以免遭遇伤害
                                Location safepoint = p.getLocation();
                                UUID uuid = p.getUniqueId();
                                try {
                                    if(getDatabase().SafePoint(uuid, safepoint)){
                                        p.sendMessage(ChatColor.GREEN+"§lSafety point recorded");
                                        return true;
                                    }else{
                                        p.sendMessage(ChatColor.RED+"§lSecurity point record failed");
                                        return true;
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (args[0].equalsIgnoreCase("verify")){
                                    if(!verify.get(p)){
                                        if(args.length != 2){
                                            p.sendMessage(ChatColor.RED+"Please provide a dynamic password");
                                            return true;
                                        }else{
                                            try {
                                                if(authController.verify(p,args[1])){
                                                    verify.put(p,true);
                                                    p.sendMessage(ChatColor.GREEN+"§lInitial verification succeeded");
                                                    p.getInventory().setContents(oldInvs.get(p));
                                                    isCreating.put(p,false);
                                                    verifyHigh.put(p,true);
                                                    loggedIn.put(p,true);
                                                    return true;
                                                }else{
                                                    p.sendMessage(ChatColor.GOLD+"§lThe secret key is "+authController.getSecretKey(p));
                                                    p.sendMessage(ChatColor.RED+"§lDynamic password is invalid, verification fails");
                                                    return true;
                                                }
                                            } catch (NotRegistered | IOException notRegistered) {
                                                notRegistered.printStackTrace();
                                            }
                                        }
                                    }else{
                                        p.sendMessage(ChatColor.RED+"You don't need to verify");
                                        return true;
                                    }
                                } else {
                                    p.sendMessage(ChatColor.RED+"§l+++++ EusAccountPro +++++");
                                    p.sendMessage(ChatColor.GREEN+"/eap safepoint"+ChatColor.GOLD+" Record player safety points");
                                    p.sendMessage(ChatColor.GREEN+"/eap create"+ChatColor.GOLD+" Create 2FA");
                                    p.sendMessage(ChatColor.GREEN+"/eap delete"+ChatColor.GOLD+" Delete your 2FA");
                                    p.sendMessage(ChatColor.GREEN+"/eap verify <code>"+ChatColor.GOLD+" Initial verification");
                                    p.sendMessage(ChatColor.GREEN+"/2fa <code>"+ChatColor.GOLD+" Verify when you're entering the server");
                                    p.sendMessage(ChatColor.BLUE+"/eapre <PlayerName>"+ChatColor.GOLD+" Forcibly delete any player's 2FA (Op Only)");
                                    p.sendMessage(ChatColor.RED+"§l----- EusAccountPro -----");
                                    return true;
                                }
                            }
                        }
                    }
                }else{
                    p.sendMessage(ChatColor.RED+"§l+++++ EusAccountPro +++++");
                    p.sendMessage(ChatColor.GREEN+"/eap safepoint"+ChatColor.GOLD+" Record player safety points");
                    p.sendMessage(ChatColor.GREEN+"/eap create"+ChatColor.GOLD+" Create 2FA");
                    p.sendMessage(ChatColor.GREEN+"/eap delete"+ChatColor.GOLD+" Delete your 2FA");
                    p.sendMessage(ChatColor.GREEN+"/eap verify <code>"+ChatColor.GOLD+" Initial verification");
                    p.sendMessage(ChatColor.GREEN+"/2fa <code>"+ChatColor.GOLD+" Verify when you're entering the server");
                    p.sendMessage(ChatColor.BLUE+"/eapre <PlayerName>"+ChatColor.GOLD+" Forcibly delete any player's 2FA (Op Only)");
                    p.sendMessage(ChatColor.RED+"§l----- EusAccountPro -----");
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.BOLD + "You must execute this command as a player");
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("2fa")) {
            if (sender instanceof Player) {
                //获取用户输入
                Player p = (Player) sender;
                if (args.length != 1) {
                    //什么都没有，显示使用方法
                    sender.sendMessage(ChatColor.RED+"§lThe data is abnormal, please enter "+ChatColor.GOLD+"/2fa <code>");
                    return true;
                } else {
                    //待加入：先行判断，1.该玩家是否已激活2fa 2.该玩家是否已经验证过2fa
                    if(verifyHigh.get(p)){
                        p.sendMessage(ChatColor.GOLD+"§lYou have completed initialization verification and do not need to use /2fa");
                        return true;
                    }else{
                        if (loggedIn.getOrDefault(p, false)) {
                            p.sendMessage(ChatColor.AQUA + "§lYou have been verified");
                            return true;
                        }else{
                            if (isCreating.get(p)){
                                p.sendMessage(ChatColor.RED+"§lYou are already creating EAP");
                                return true;
                            }else{
                                try {
                                    if (authController.verify(p, args[0])) {
                                        // Success
                                        loggedIn.put(p, true);
                                        p.sendMessage(ChatColor.GREEN + "Success");
                                        p.getInventory().setContents(oldInvs.get(p));
                                        p.setGameMode(odgmode.get(p));
                                        p.teleport(odloc.get(p));
                                        return true;
                                    } else {
                                        // Invalid code
                                        p.sendMessage(ChatColor.YELLOW + "Failed");
                                        return true;
                                    }
                                } catch (NotRegistered | IOException e) {
                                    p.sendMessage(ChatColor.RED + "Not registered or abnormal program");
                                    return true;
                                }
                            }
                        }
                    }
                }
            } else {
                sender.sendMessage(ChatColor.BOLD + "You must execute this command as a player");
                return true;
            }
        }
        if (command.getName().equalsIgnoreCase("eapre")) {
            if (sender instanceof Player){
                if (sender.hasPermission("EusAccountPro.admin")) {
                    if (args.length != 1) {
                        sender.sendMessage(ChatColor.RED+"§lMissing target, please enter "+ChatColor.GOLD+"/eapre <PlayerName>");
                        return true;
                    } else {
                        Player target = Bukkit.getPlayer(args[0]); //定义此为该玩家的名称，接下来验证是否已激活2fa，若为是，则删除其记录
                        UUID uuid = target.getUniqueId();
                        if(getDatabase().isPlayerRegistered(uuid)){
                            if (getDatabase().deletePlayer(uuid)){
                                isCreating.put(target,false);
                                verifyHigh.put(target,false);
                                verify.put(target,true);
                                sender.sendMessage(ChatColor.GREEN+"§lSuccess");
                                return true;
                            }else{
                                sender.sendMessage(ChatColor.GREEN+"§lFailed");
                                return true;
                            }
                        }else{
                            sender.sendMessage(ChatColor.RED + "§lThe player has not registered");
                            return true;
                        }

                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "§lYou do not have permission to use this command");
                }
            }else{
                sender.sendMessage(ChatColor.RED + "§lYou must execute this command as a player");
                return true;
            }
        }
        return false;
    }

    public Database getDatabase() {
        return database;
    }
}
