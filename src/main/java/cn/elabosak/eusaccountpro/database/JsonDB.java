package cn.elabosak.eusaccountpro.database;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.apache.commons.io.*;
import org.bukkit.util.FileUtil;


public class JsonDB extends Database {
    Gson gson = new Gson();

    @Override
    public String getSecretKey(UUID uuid) throws IOException {
        String uuid_string = uuid.toString();
        File file = new File("/JsonDB/players/"+uuid_string+".json");
        if(!file.exists()){
            //文件不存在，返回false
            return null;
        }else{
            String file_string = FileUtils.readFileToString(file, "UTF-8");
            JSONObject jsonObject = JSON.parseObject(file_string);
            JSONArray secretKey_json = jsonObject.getJSONArray("secretKey");
            if (secretKey_json != null){
               String secretKey = secretKey_json.toString();
               return secretKey;
            }else{
                return null;
            }
        }
    }

    @Override
    public boolean updatePlayer(UUID uuid, String secretKey) {
        String uuid_string = uuid.toString();
        JSONObject jsonObject = new JSONObject();
        Map<String,String> data = new HashMap<String,String>();
        data.put("uuid",uuid_string);
        data.put("secretKey",secretKey);
        String mapJson = jsonObject.toJSONString(data);
        File file = new File("/JsonDB/players/"+uuid_string+".json");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            JSONObject.writeJSONString(fileOutputStream,mapJson);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean isPlayerRegistered(UUID uuid) {
        String uuid_string = uuid.toString();
        File file = new File("/JsonDB/players/"+uuid_string+".json");
        if(!file.exists()){
            //文件不存在，返回false
            return false;
        }else{
            return true;
        }
    }

    @Override
    public boolean deletePlayer(UUID uuid) {
        String uuid_string = uuid.toString();
        File file = new File("/JsonDB/players/"+uuid_string+".json");
        if(!file.exists()){
            //文件不存在，返回false
            return false;
        }else{
            file.delete();
            return true;
        }
    }
}