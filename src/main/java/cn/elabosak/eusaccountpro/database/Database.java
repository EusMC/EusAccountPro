package cn.elabosak.eusaccountpro.database;

import java.util.UUID;

public abstract class Database {

    public abstract String getSecretKey(UUID uuid);

    public abstract boolean updatePlayer(UUID uuid, String secretKey);

}
