package cn.elabosak.eusaccountpro.database;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import cn.elabosak.eusaccountpro.utils.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import cn.elabosak.eusaccountpro.EusAccountPro;
import cn.elabosak.eusaccountpro.utils.str2loc;

public class JsonDB extends Database {

    EusAccountPro plugin;

    @Override
    public String getSecretKey(UUID uuid) {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        String Filepath = "plugins/EusAccountPro/JsonDB/Players/"+uuid_string+".json";
        File file = new File(Filepath);
        if(!file.exists()){
            return null; //文件不存在，返回null
        }else{
            String js = FileUtil.ReadFile(Filepath);
            JSONObject jsonObject = JSON.parseObject(js);
            String secretKey_json = jsonObject.getString("secretKey");
            return secretKey_json;
        }
    }

    @Override
    public boolean updatePlayer(UUID uuid, String secretKey) throws IOException {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        Map<String,String> data = new HashMap<String,String>();
        data.put("secretKey",secretKey);
        Object mapJson = JSONObject.toJSON(data);
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
        return file.exists(); //文件不存在，返回false
    }

    @Override
    public boolean deletePlayer(UUID uuid) {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        File file = new File("plugins/EusAccountPro/JsonDB/Players/"+uuid_string+".json");
        File QRCode = new File("plugins/EusAccountPro/QRCode/"+uuid_string+".png");
        if(!file.exists() && !QRCode.exists()){
            return false; //文件不存在，返回false
        }else{
            return file.delete() && QRCode.delete();
        }
    }

    @Override
    public boolean SafePoint(UUID uuid, Location safepoint) throws IOException {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        Map<String,String> loc = new HashMap<String,String>();
        loc.put("safepoint",str2loc.loc2str(safepoint));
        Object locJson = JSONObject.toJSON(loc);
        File mkdirs = new File("plugins/EusAccountPro/JsonDB/SafePoint/");
        if(!mkdirs.exists()){
            mkdirs.mkdirs();
        }
        String format = ".json";
        String file_name = uuid_string + format;
        File file = new File("plugins/EusAccountPro/JsonDB/SafePoint//"+file_name);
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
    public Location getSafePoint(UUID uuid) {
        String uuid_string = uuid.toString(); //将uuid的数据类型转换为String
        String Filepath = "plugins/EusAccountPro/JsonDB/SafePoint/"+uuid_string+".json";
        File file = new File(Filepath);
        if(!file.exists()){
            return null; //文件不存在，返回null
        }else{
            String js = FileUtil.ReadFile(Filepath);
            JSONObject jsonObject = JSON.parseObject(js);
            String safepoint_json = jsonObject.getString("safepoint");
            if (safepoint_json != null){
                return str2loc.str2loc(safepoint_json);

            }else{
                return null;
            }
        }
    }

}