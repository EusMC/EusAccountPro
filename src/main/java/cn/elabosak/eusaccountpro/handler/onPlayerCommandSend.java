package cn.elabosak.eusaccountpro.handler;

import cn.elabosak.eusaccountpro.EusAccountPro;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class onPlayerCommandSend implements Listener {

    EusAccountPro eusAccountPro;

    @EventHandler
    public void onPlayerCommandSend (PlayerCommandSendEvent event){
        if(eusAccountPro.verify.get(event.getPlayer())){
            Listener listener = new onMapInitialize();
            PlayerInteractEvent.getHandlerList().unregister(listener); //onMap监听器关闭
            event.getPlayer().getInventory().clear();
            event.getPlayer().getInventory().addItem((ItemStack) eusAccountPro.oldInvs.get(event.getPlayer()));
            event.getPlayer().sendMessage(ChatColor.GREEN+"§l创建成功");
            Listener self = new onPlayerCommandSend();
            PlayerInteractEvent.getHandlerList().unregister(self);
        }
    }
}
