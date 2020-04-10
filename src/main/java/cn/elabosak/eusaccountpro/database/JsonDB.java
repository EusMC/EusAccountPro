package cn.elabosak.eusaccountpro.database;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import cn.elabosak.eusaccountpro.controller.AuthController;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.zxing.Reader;
import org.bukkit.entity.Player;

public class JsonDB extends Database {
    Gson gson = new Gson();

    @Override
    public String getSecretKey(UUID uuid) {

        return null;
    }

    @Override
    public boolean updatePlayer(UUID uuid, String secretKey) {
        String uuid_string = uuid.toString();
        JSONObject jsonObject = new JSONObject();
        Object pj = jsonObject.put(uuid_string, secretKey);
        String playerJson = jsonObject.toJSONString(pj);
        File file = new File("/players/"+uuid_string+".json");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            JSONObject.writeJSONString(fileOutputStream,playerJson);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}