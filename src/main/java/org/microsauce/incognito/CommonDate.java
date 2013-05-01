package org.microsauce.incognito;


public class CommonDate {

    private Integer year;
    private Integer month;
    private Integer dayOfMonth;
    private Integer hour;
    private Integer minute;
    private Integer second;
    private Integer millis;

    public CommonDate(Integer year, Integer month, Integer dayOfMonth, Integer hour, Integer minute, Integer second, Integer millis) {
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.millis = millis;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public Integer getHour() {
        return hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public Integer getSecond() {
        return second;
    }

    public Integer getMillis() {
        return millis;
    }
}
