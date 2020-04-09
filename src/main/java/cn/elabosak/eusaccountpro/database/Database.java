package cn.elabosak.eusaccountpro.database;

import java.util.UUID;

public abstract class Database {

    abstract String getSecretKey(UUID uuid);

    abstract boolean updatePlayer(UUID uuid, String secretKey);

}
