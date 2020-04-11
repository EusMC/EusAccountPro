package cn.elabosak.eusaccountpro.controller;

import cn.elabosak.eusaccountpro.EusAccountPro;
import cn.elabosak.eusaccountpro.exception.NotRegistered;
import org.bukkit.entity.Player;

import java.io.IOException;

import static cn.elabosak.eusaccountpro.utils.Authenticator.getTOTPCode;

/**
 * Designed for player managing
 */
public class AuthController {
    EusAccountPro plugin;

    public AuthController(EusAccountPro instance) {
        plugin = instance;
    }

    /**
     * Register player with TOTP secret key
     * @param player
     * @param secretKey
     * @return
     */
    public boolean register(Player player, String secretKey) throws IOException {
        return plugin.getDatabase().updatePlayer(player.getUniqueId(), secretKey);
    }

    /**
     * Get player's TOTP secret key
     * @param player
     * @return null: player not registered
     */
    public String getSecretKey(Player player) throws IOException {
        return plugin.getDatabase().getSecretKey(player.getUniqueId());
    }

    /**
     * Verify whether the input code from player is valid or not
     * @param player
     * @param code
     * @return
     */
    public boolean verify(Player player, String code) throws NotRegistered, IOException {
        String secretKey = getSecretKey(player);
        if (secretKey == null) throw new NotRegistered(); // player not registered

        return code.equals(getTOTPCode(secretKey));
    }
}
