package com.example.telemetryviewer.models;

public class TelemetryIndex {

    public short date;
    public short month;
    public short year;

    public short hour;
    public short minute;
    public short second;

    public short end_date;
    public short end_month;
    public short end_year;

    public short end_hour;
    public short end_minute;
    public short end_second;

    public String event;

    public long start_address;
    public long end_address;

    @Override
    public String toString() {
        return String.format("Событие: %s с %02d:%02d:%02d по %02d:%02d:%02d", event, hour, minute, second, end_hour, end_minute, end_second);
    }
    public boolean checkIsAvailable(int day, int month, int year) {
        return day == date && month == this.month && year == this.year;
    }
}
