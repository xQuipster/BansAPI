package com.shymaks.bansapi;

import javax.annotation.Nonnull;
import java.util.Date;

public class TimePeriod {
    final private int years;
    final private int months;
    final private int days;
    final private int hours;
    final private int minutes;

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getMonths() {
        return months;
    }

    public int getYears() {
        return years;
    }
    public TimePeriod(int years, int months, int days, int hours, int minutes){
        this.years = years;
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }
    public static TimePeriod getNone(){
        return new TimePeriod(-25, 0, 0,0,0);
    }
    public boolean isNotZero(){
        return years != 0 || months != 0 || days != 0 || hours != 0 || minutes != 0;
    }
    public static TimePeriod between(@Nonnull Date date1, @Nonnull Date date2){
        Date dateB;
        Date dateA;
        if (date1.after(date2)){
            dateA = date1;
            dateB = date2;
        }else if (date2.after(date1)){
            dateA = date2;
            dateB = date1;
        }else{
            return new TimePeriod(0, 0, 0, 0, 0);
        }
        int years = dateA.getYear() - dateB.getYear();
        int months = dateA.getMonth() - dateB.getMonth();
        int days = dateA.getDate() - dateB.getDate();
        int hours = dateA.getHours() - dateB.getHours();
        int minutes = dateA.getMinutes() - dateB.getMinutes();
        if (minutes < 0){
            for (; minutes < 0; minutes+=60){
                hours-=1;
            }
        }
        if (hours < 0){
            for (; hours < 0; hours+=24){
                days-=1;
            }
        }
        if (days < 0){
            int maxDays = 30;
            int month1 = months;
            int years1 = years;
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
            for (; days < 0; days+=maxDays){
                months-=1;

                maxDays = 30;
                month1 = months;
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
        if (months < 0){
            for (; months < 0; months+=12){
                years-=1;
            }
        }
        return new TimePeriod(years, months, days, hours, minutes);
    }
    public String periodToString(){
        if (getYears() == -25 && getMinutes() == 0 && getHours() == 0 && getDays() == 0 && getMonths() == 0){
            return "none";
        }else{
            String y = "";
            if (getYears() != 0){
                y = getYears() + "y";
            }
            String mon = "";
            if (getMonths() != 0){
                mon = getMonths() + "mon";
            }
            String d = "";
            if (getDays() != 0){
                d = getDays() + "d";
            }
            String h = "";
            if (getHours() != 0){
                h = getHours() + "h";
            }
            String m = "";
            if (getMinutes() != 0){
                m = getMinutes() + "m";
            }
            return y + mon + d + h + m;
        }
    }
    public static TimePeriod fromString(String s){
        if (s.equalsIgnoreCase("none")){
            return new TimePeriod(-25, 0, 0, 0, 0);
        }else{
            int y = 0;
            int mon = 0;
            int d = 0;
            int h = 0;
            int m = 0;
            for (int i = 0; i < s.length(); i++){
                if (s.toLowerCase().charAt(i) == 'y'){
                    try {
                        y+=Integer.parseInt(s.substring(0, i));
                        s = s.substring(i + 1);
                        i = 0;
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }else if (s.toLowerCase().length() >= i + 3 && s.toLowerCase().charAt(i) == 'm' && s.toLowerCase().charAt(i + 1) == 'o' && s.toLowerCase().charAt(i + 2) == 'n'){
                    try {
                        mon+=Integer.parseInt(s.substring(0, i));
                        s = s.substring(i + 3);
                        i = 0;
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }else if (s.toLowerCase().charAt(i) == 'd'){
                    try {
                        d+=Integer.parseInt(s.substring(0, i));
                        s = s.substring(i + 1);
                        i = 0;
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }else if (s.toLowerCase().charAt(i) == 'h'){
                    try {
                        h+=Integer.parseInt(s.substring(0, i));
                        s = s.substring(i + 1);
                        i = 0;
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }else if (s.toLowerCase().charAt(i) == 'm'){
                    try {
                        m+=Integer.parseInt(s.substring(0, i));
                        s = s.substring(i + 1);
                        i = 0;
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            TimePeriod period = new TimePeriod(y, mon, d, h, m);
            return period.isNotZero() ? period : null;
        }
    }
    public boolean isNone(){
        return years == -25 && minutes == 0 && hours == 0 && days == 0 && months == 0;
    }
}
