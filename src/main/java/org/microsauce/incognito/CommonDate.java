package org.microsauce.incognito;


public class CommonDate {

    /**
     * The for digit year: yyyy
     */
    private Integer year;

    /**
     * Values: 1-12
     */
    private Integer month;

    /**
     * Values: 1-31
     */
    private Integer dayOfMonth;

    /**
     * Values: 1-24
     */
    private Integer hour;

    /**
     * Values: 0-59
     */
    private Integer minute;

    /**
     * Values: 0-59
     */
    private Integer second;

    /**
     * Values: 0-999
     */
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

    public String toString() {
        return year+"-"+month+"-"+dayOfMonth+" "+hour+":"+minute+":"+second+"."+millis;
    }
}
