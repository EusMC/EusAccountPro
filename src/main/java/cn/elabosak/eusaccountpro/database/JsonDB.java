package cn.elabosak.eusaccountpro.database;

import java.util.UUID;

public class JsonDB extends Database {
    @Override
    public String getSecretKey(UUID uuid) {
        return null;
    }

    @Override
    public boolean updatePlayer(UUID uuid, String secretKey) {
        return false;
    }
}
