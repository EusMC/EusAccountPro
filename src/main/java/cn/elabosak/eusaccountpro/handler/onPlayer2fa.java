package cn.elabosak.eusaccountpro.handler;

import cn.elabosak.eusaccountpro.EusAccountPro;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class onPlayer2fa implements Listener {

    EusAccountPro eusAccountPro;

    @EventHandler
    public void onPlayerCommandSend(PlayerCommandSendEvent event){
        if(eusAccountPro.loggedIn.get(event.getPlayer())){
            event.getPlayer().teleport(eusAccountPro.odloc.get(event.getPlayer()));
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }
}
