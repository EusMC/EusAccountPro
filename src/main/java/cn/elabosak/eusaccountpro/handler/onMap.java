package cn.elabosak.eusaccountpro.handler;

import cn.elabosak.eusaccountpro.utils.MapRender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public final class onMap implements Listener {
    @EventHandler
    public void onMap(MapInitializeEvent event){
        MapView m = event.getMap();
        for (MapRenderer renderer : m.getRenderers()){
            m.removeRenderer(renderer);
        }
        m.addRenderer(new MapRender());
    }
}
