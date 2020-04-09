package cn.elabosak.eusaccountpro.database;

import java.util.UUID;

public class JsonDB extends Database {
    @Override
    String getSecretKey(UUID uuid) {
        return null;
    }

    @Override
    boolean updatePlayer(UUID uuid, String secretKey) {
        return false;
    }
}
