package com.example.apppiantina.model;

public class MoistureSensor {
    public MoistureSensor(String thingId){

    }
    public String thingId;
    public SensorValues values;

    public static class SensorValues {
        public double soilMoisture;
    }

}