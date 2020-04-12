package cn.elabosak.eusaccountpro.database;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.elabosak.eusaccountpro.utils.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import cn.elabosak.eusaccountpro.EusAccountPro;

import javax.xml.bind.Marshaller;

public class JsonDB extends Database {

    EusAccountPro plugin;

    @Override
    public String getSecretKey(UUID uuid) throws IOException {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        String Filepath = "plugins/EusAccountPro/JsonDB/Players/"+uuid_string+".json";
        File file = new File(Filepath);
        if(!file.exists()){
            return null; //文件不存在，返回null
        }else{
            String js = FileUtil.ReadFile(Filepath);
            JSONObject jsonObject = JSON.parseObject(js);
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
    public boolean updatePlayer(UUID uuid, String secretKey) throws IOException {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        JSONObject jsonObject = new JSONObject();
        Map<String,String> data = new HashMap<String,String>();
        data.put("uuid",uuid_string);
        data.put("secretKey",secretKey);
        String mapJson = jsonObject.toJSONString(data);
        File mkdirs = new File("plugins/EusAccountPro/JsonDB/Players/");
        if(!mkdirs.exists()){
            mkdirs.mkdirs();
        }
        String format = ".json";
        String file_name = uuid_string + format;
        File file = new File("plugins/EusAccountPro/JsonDB/Players//"+file_name);
        if(!file.exists()){
            file.createNewFile();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            JSONObject.writeJSONString(fileOutputStream,mapJson);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean isPlayerRegistered(UUID uuid) {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        File file = new File("plugins/EusAccountPro/JsonDB/Players/"+uuid_string+".json");
        if(!file.exists()){
            return false; //文件不存在，返回false
        }else{
            return true;
        }
    }

    @Override
    public boolean deletePlayer(UUID uuid) {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        File file = new File("plugins/EusAccountPro/JsonDB/Players/"+uuid_string+".json");
        if(!file.exists()){
            return false; //文件不存在，返回false
        }else{
            file.delete();
            return true;
        }
    }

    @Override
    public boolean SafePoint(UUID uuid, Location safepoint) throws IOException {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        JSONObject jsonObject = new JSONObject();
        Map<String,String> loc = new HashMap<String,String>();
        loc.put("safepoint",safepoint.toString());
        String locJson = jsonObject.toJSONString(loc);
        File mkdirs = new File("plugins/EusAccountPro/JsonDB/safepoint/");
        if(!mkdirs.exists()){
            mkdirs.mkdirs();
        }
        String format = ".json";
        String file_name = uuid_string + format;
        File file = new File("plugins/EusAccountPro/JsonDB/safepoint//"+file_name);
        if(!file.exists()){
            file.createNewFile();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            JSONObject.writeJSONString(fileOutputStream,locJson);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Location getSafePoint(UUID uuid) throws IOException {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        String Filepath = "plugins/EusAccountPro/JsonDB/safepoint/"+uuid_string+".json";
        File file = new File(Filepath);
        if(!file.exists()){
            return null; //文件不存在，返回null
        }else{
            String js = FileUtil.ReadFile(Filepath);
            JSONObject jsonObject = JSON.parseObject(js);
            JSONArray safepoint_json = jsonObject.getJSONArray("safepoint");
            if (safepoint_json != null){
                String safepoint_string = safepoint_json.toString();
                String[] arg = safepoint_string.split(",");
                double[] parsed = new double[3];
                for (int a = 0; a < 3; a++) {
                    parsed[a] = Double.parseDouble(arg[a+1]);
                }
                Location safepoint = new Location (Bukkit.getWorld(arg[0]), parsed[0], parsed[1], parsed[2]);
                return safepoint;
            }else{
                return null;
            }
        }
    }

}