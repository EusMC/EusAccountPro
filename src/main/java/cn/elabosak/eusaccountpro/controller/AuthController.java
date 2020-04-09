package cn.elabosak.eusaccountpro.controller;

import cn.elabosak.eusaccountpro.EusAccountPro;
import org.bukkit.entity.Player;

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
    public boolean register(Player player, String secretKey) {

    }

    /**
     * Get player's TOTP secret key
     * @param player
     * @return null: player not registered
     */
    public String getSecretKey(Player player) {
        return null;
    }

    /**
     * Verify whether the input code from player is valid or not
     * @param player
     * @param code
     * @return
     */
    public boolean verify(Player player, String code) {
        String secretKey = getSecretKey(player);
        if (secretKey == null) return false; // player not registered

        return code.equals(getTOTPCode(secretKey));
    }
}
