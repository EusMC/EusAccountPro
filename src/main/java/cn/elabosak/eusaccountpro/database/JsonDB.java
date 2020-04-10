package cn.elabosak.eusaccountpro.database;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.*;



public class JsonDB extends Database {

    @Override
    public String getSecretKey(UUID uuid) throws IOException {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        File file = new File("/JsonDB/players/"+uuid_string+".json");
        if(!file.exists()){
            return null; //文件不存在，返回null
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
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
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
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        File file = new File("/JsonDB/players/"+uuid_string+".json");
        if(!file.exists()){
            return false; //文件不存在，返回false
        }else{
            return true;
        }
    }

    @Override
    public boolean deletePlayer(UUID uuid) {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        File file = new File("/JsonDB/players/"+uuid_string+".json");
        if(!file.exists()){
            return false; //文件不存在，返回false
        }else{
            file.delete();
            return true;
        }
    }
}