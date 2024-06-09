package com.shymaks.bansapi;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public final class BansAPI extends JavaPlugin {

    Logger logger;

    FileConfiguration config;
    File configFile;
    public static BansAPI INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        logger = Logger.getLogger("Minecraft");
        loadConfig();
    }

    public void saveBansConfig() {
        try {
            config.save(configFile);
        }catch (Exception e){
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public FileConfiguration getBansConfig(){
        return config;
    }

    private void loadConfig() {
        try {
            configFile = new File(getDataFolder(), "bans.yml");
            if (!configFile.exists()){
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            saveBansConfig();
            optimizeList();
        }catch (Exception e){
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }

    }
    public ArrayList<Ban> getBans(String type){
        optimizeList();
        ArrayList<Ban> bans = new ArrayList<>();
        for (Object o : getBansConfig().getList("bans")){
            if (o instanceof String){
                String s = (String) o;
                Ban ban = Ban.fromString(s);
                if (ban != null && Objects.equals(ban.getType(), type)){
                    bans.add(ban);
                }
            }
        }
        return bans;
    }

    public void executeBan(Ban ban){
        if (isBanned(ban.getPlayer(), ban.getType())){
            unban(ban.getPlayer(), ban.getType());
        }
        if (getBansConfig().contains("bans")){
            if (getBansConfig().isList("bans")){
                optimizeList();
                ArrayList<String> list = (ArrayList<String>) getBansConfig().getList("bans");
                list.add(ban.banToString());
                getBansConfig().set("bans", list);
            }else{
                getBansConfig().set("bans", new ArrayList<>());
            }
        }else{
            getBansConfig().addDefault("bans", new ArrayList<>());
        }
        saveBansConfig();
    }

    private void optimizeList() {
        if (getBansConfig().contains("bans")){
            if (getBansConfig().isList("bans")){
                ArrayList<Object> list = (ArrayList<Object>) getBansConfig().getList("bans");
                HashMap<String, ArrayList<String>> banned = new HashMap<>();
                for (int i = 0; i < list.size(); i++){
                    if (!(list.get(i) instanceof String)){
                        list.remove(i);
                        i-=1;
                    }else if (Ban.fromString((String) list.get(i)) == null){
                        list.remove(i);
                        i-=1;
                    }else {
                        Ban ban = Ban.fromString((String) list.get(i));
                        if (!banned.containsKey(ban.getType())){
                            ArrayList<String> list1 = new ArrayList<>();
                            list1.add(ban.getPlayer().toLowerCase());
                            banned.put(ban.getType(), list1);
                        }else{
                            ArrayList<String> list1 = banned.get(ban.getType());
                            if (list1.contains(ban.getPlayer().toLowerCase())){
                                list.remove(i);
                                i-=1;
                                continue;
                            }else{
                                list1.add(ban.getPlayer().toLowerCase());
                                banned.put(ban.getType(), list1);
                            }
                        }
                        if (ban.getExpireDate() != null && !ban.getExpireDate().after(Date.from(Instant.now()))){
                            list.remove(i);
                            i-=1;
                        }
                    }
                }
                getBansConfig().set("bans", list);
            }else{
                getBansConfig().set("bans", new ArrayList<>());
            }
        }else{
            getBansConfig().addDefault("bans", new ArrayList<>());
        }
        saveBansConfig();
    }

    public void unban(Player player, String type){
        unban(player.getName(), type);
    }
    public void unban(String player, String type){
        if (isBanned(player, type)){
            optimizeList();
            ArrayList<String> list = (ArrayList<String>) getBansConfig().getList("bans");
            for (int i = 0; i < list.size(); i++){
                Ban ban = Ban.fromString(list.get(i));
                assert ban != null;
                if (ban.getPlayer().equalsIgnoreCase(player) && ban.getType().equals(type)){
                    list.remove(i);
                    break;
                }
            }
            getBansConfig().set("bans", list);
            saveBansConfig();
        }
    }

    public Date stringToDate(String dateS) {
        if (!dateS.equalsIgnoreCase("none")){
            String[] dateArgs = dateS.split("-");
            if (dateArgs.length == 5){
                try {
                    int y = Integer.parseInt(dateArgs[0]);
                    int mon = Integer.parseInt(dateArgs[1]);
                    int d = Integer.parseInt(dateArgs[2]);
                    int h = Integer.parseInt(dateArgs[3]);
                    int m = Integer.parseInt(dateArgs[4]);
                    Date date1 = new Date();
                    date1.setYear(y);
                    date1.setMonth(mon);
                    date1.setDate(d);
                    date1.setHours(h);
                    date1.setMinutes(m);
                    return date1;
                }catch (Exception e){
                    return null;
                }
            }
        }
        return null;
    }
    public String dateToString (Date date){
        if (date != null){
            return date.getYear() + "-" + date.getMonth() + "-" + date.getDate() + "-" + date.getHours() + "-" + date.getMinutes();
        }else{
            return null;
        }
    }

    public boolean isBanned(String player, String type){
        boolean a = false;
        optimizeList();
        for (Ban ban : getBans(type)){
            if (ban.getPlayer().equalsIgnoreCase(player)){
                a = true;
                break;
            }
        }
        return a;
    }
    public boolean isBanned(Player player, String type){
        return isBanned(player.getName(), type);
    }
    public Date addTimeToDate(@Nonnull Date date, @Nonnull TimePeriod period){
        date.setYear(date.getYear() + period.getYears());
        int month = date.getMonth() + period.getMonths();
        int days = date.getDate() + period.getDays();
        int hours = date.getHours() + period.getHours();
        int minutes = date.getMinutes() + period.getMinutes();
        if (minutes >= 60){
            for (; minutes >= 60; minutes-=60){
                hours+=1;
            }
        }else if (minutes < 0){
            for (; minutes < 0; minutes+=60){
                hours-=1;
            }
        }
        if (hours >= 24){
            for (; hours >= 24; hours-=24){
                days+=1;
            }
        }else if (hours < 0){
            for (; hours < 0; hours+=24){
                days-=1;
            }
        }
        int maxDays = 30;
        int month1 = month;
        int years1 = date.getYear();
        for (; month1 >= 12; month1-=12){
            years1+=1;
        }
        for (; month1 < 0; month1+=12){
            years1-=1;
        }
        switch (month1){
            case 0:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
            case 2:
                maxDays = 31;
                break;
            case 1:
                if (years1%4==0){
                    maxDays = 29;
                }else{
                    maxDays = 28;
                }
                break;
        }
        if (days >= maxDays){
            for (; days >= maxDays; days-=maxDays){
                month+=1;

                maxDays = 30;
                month1 = month;
                for (; month1 >= 12; month1-=12){
                    years1+=1;
                }
                for (; month1 < 0; month1+=12){
                    years1-=1;
                }
                switch (month1){
                    case 0:
                    case 4:
                    case 6:
                    case 7:
                    case 9:
                    case 11:
                    case 2:
                        maxDays = 31;
                        break;
                    case 1:
                        if (years1%4==0){
                            maxDays = 29;
                        }else{
                            maxDays = 28;
                        }
                        break;
                }
            }
        }else if (days <= 0){
            for (; days <= 0; days+=maxDays){
                month+=1;

                maxDays = 30;
                month1 = month;
                for (; month1 >= 12; month1-=12){
                    years1+=1;
                }
                for (; month1 < 0; month1+=12){
                    years1-=1;
                }
                switch (month1){
                    case 0:
                    case 4:
                    case 6:
                    case 7:
                    case 9:
                    case 11:
                    case 2:
                        maxDays = 31;
                        break;
                    case 1:
                        if (years1%4==0){
                            maxDays = 29;
                        }else{
                            maxDays = 28;
                        }
                        break;
                }
            }
        }
        for (; month >= 12; month-=12){
            date.setYear(date.getYear() + 1);
        }
        for (; month < 0; month+=12){
            date.setYear(date.getYear() - 1);
        }
        date.setMonth(month);
        date.setDate(days);
        date.setHours(hours);
        date.setMinutes(minutes);
        return date;
    }
}
