package cn.elabosak.eusaccountpro.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MapRender extends MapRenderer {

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File(Bukkit.getWorldContainer(),"/QRCode/"+player.getUniqueId().toString()+".png"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        map.setScale(MapView.Scale.NORMAL);
        canvas.drawImage(5,5,img);
    }

}
