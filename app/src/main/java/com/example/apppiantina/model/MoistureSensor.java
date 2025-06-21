package com.example.apppiantina.model;

public class MoistureSensor {
    public MoistureSensor(String thingId){

    }
    public String thingId;
    public String timestamp;
    public String status;
    public SensorValues values;

    public static class SensorValues {
        public double soilMoisture;
        public double temperature;
        public double humidity;
        public int battery;
    }

}