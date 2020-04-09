package cn.elabosak.eusaccountpro.database;

import java.lang.reflect.Array;
import java.util.UUID;
import cn.elabosak.eusaccountpro.controller.AuthController;
import com.google.gson.Gson;
import org.bukkit.entity.Player;

public class JsonDB extends Database {
    Gson gson = new Gson();
    @Override
    String getSecretKey(UUID uuid) {
        return null;
    }

    @Override
    boolean updatePlayer(UUID uuid, String secretKey) {
        String jsonObject = gson.toJson(uuid,secretKey);
        return false;
    }
}
