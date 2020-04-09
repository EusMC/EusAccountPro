package cn.elabosak.eusaccountpro;

import cn.elabosak.eusaccountpro.database.DBType;

import java.util.Arrays;

public class EAPConfig {
    public static DBType dbType;

    public EAPConfig(EusAccountPro instance) {
        try {
            dbType = DBType.valueOf(instance.getConfig().getString("Storage.type"));
        } catch (IllegalArgumentException e) {
            instance.getLogger().severe("Storage type not supported. Available: " + Arrays.toString(DBType.values()));
        }
    }
}
