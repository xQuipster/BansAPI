package com.shymaks.bansapi;

import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Date;

public class Ban {
    private final String player;
    private final Date expires;
    private final String banner;
    private final String reason;
    private final String type;

    public Date getExpireDate() {
        return expires;
    }

    public String getBanner() {
        return banner;
    }

    public String getType() {
        return type;
    }

    public String getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }

    public Ban (String player, String type, Date expires, String banner, String reason){
        this.player = player;
        this.type = type;
        this.expires = expires;
        this.banner = banner;
        this.reason = reason;
    }
    public Ban (Player player, String type, Date expires, String banner, String reason){
        this.player = player.getName();
        this.type = type;
        this.expires = expires;
        this.banner = banner;
        this.reason = reason;
    }
    public Ban (String player, String type, TimePeriod time, String banner, String reason){
        this.player = player;
        this.type = type;
        if (time.isNone()){
            this.expires = null;
        }else{
            this.expires = BansAPI.INSTANCE.addTimeToDate(Date.from(Instant.now()), time);
        }
        this.banner = banner;
        this.reason = reason;
    }
    public Ban (Player player, String type, TimePeriod time, String banner, String reason){
        this.player = player.getName();
        this.type = type;
        if (time.isNone()){
            this.expires = null;
        }else{
            this.expires = BansAPI.INSTANCE.addTimeToDate(Date.from(Instant.now()), time);
        }
        this.banner = banner;
        this.reason = reason;
    }
    public Ban (String player, String type, Date expires, Player banner, String reason){
        this.player = player;
        this.type = type;
        this.expires = expires;
        this.banner = banner.getName();
        this.reason = reason;
    }
    public Ban (Player player, String type, Date expires, Player banner, String reason){
        this.player = player.getName();
        this.type = type;
        this.expires = expires;
        this.banner = banner.getName();
        this.reason = reason;
    }
    public Ban (String player, String type, TimePeriod time, Player banner, String reason){
        this.player = player;
        this.type = type;
        if (time.isNone()){
            this.expires = null;
        }else{
            this.expires = BansAPI.INSTANCE.addTimeToDate(Date.from(Instant.now()), time);
        }
        this.banner = banner.getName();
        this.reason = reason;
    }
    public Ban (Player player, String type, TimePeriod time, Player banner, String reason){
        this.player = player.getName();
        this.type = type;
        if (time.isNone()){
            this.expires = null;
        }else{
            this.expires = BansAPI.INSTANCE.addTimeToDate(Date.from(Instant.now()), time);
        }
        this.banner = banner.getName();
        this.reason = reason;
    }
    public static Ban fromString(String s){
        Ban ban = null;
        String[] args = s.split(":");
        if (args.length >= 5){
            String player = args[0];
            String type = args[1];
            String banner = args[2];
            String dateS = args[3];
            StringBuilder reason = new StringBuilder();
            for (int i = 4; i < args.length; i++){
                reason.append(args[i]);
                if (i + 1 < args.length){
                    reason.append(":");
                }
            }
            Date date = BansAPI.INSTANCE.stringToDate(dateS);
            ban = new Ban(player, type, date, banner, reason.toString());
        }
        return ban;
    }
    public String banToString(){
        return getPlayer() + ":" + getType() + ":" + getBanner() + ":" + BansAPI.INSTANCE.dateToString(getExpireDate()) + ":" + getReason();
    }
    public boolean isPermanent(){
        return getExpireDate() == null;
    }
}
