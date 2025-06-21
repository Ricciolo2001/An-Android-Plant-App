package com.example.apppiantina.model;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class GardenModel {

    private static GardenModel instance;

    private final List<Piantina> piantine = new ArrayList<>();
    private final List<MoistureSensor> sensori = new ArrayList<>();
    private final Map<String, List<MoistureSensor>> associazioni = new HashMap<>();
    // Map<nomePiantina, List<MoistureSensor>>

    private GardenModel() {
    }

    public static synchronized GardenModel getInstance() {
        if (instance == null) {
            instance = new GardenModel();
        }
        return instance;
    }

    public void aggiungiPiantina(Piantina piantina) {
        if (getPiantinaByName(piantina.getName()) == null) {
            piantine.add(piantina);
            associazioni.put(piantina.getName(), new ArrayList<>());
        }
    }

    public Piantina getPiantinaByName(String name) {
        for (Piantina p : piantine) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public void aggiungiSensore(String nomePiantina, MoistureSensor sensore) {
        if (!sensori.contains(sensore)) {
            sensori.add(sensore);
        }

        List<MoistureSensor> lista = associazioni.get(nomePiantina);
        if (lista != null && lista.stream().noneMatch(s -> s.thingId.equals(sensore.thingId))) {
            lista.add(sensore);
        }
    }

    public void aggiungiSensore(MoistureSensor sensore) {
        if (!sensori.contains(sensore))
            sensori.add(sensore);
    }

    public boolean rimuoviSensore(String nomePiantina, String thingId) {
        List<MoistureSensor> lista = associazioni.get(nomePiantina);
        if (lista != null) {
            return lista.removeIf(s -> s.thingId.equals(thingId));
        }
        return false;
    }

    public List<MoistureSensor> getSensoriPerPiantina(String nomePiantina) {
        return associazioni.getOrDefault(nomePiantina, new ArrayList<>());
    }

    public MoistureSensor getSensoreGlobale(String thingId) {
        for (MoistureSensor s : sensori) {
            if (s.thingId.equals(thingId)) {
                return s;
            }
        }
        return null;
    }

    public List<Piantina> getPiantine() {
        return piantine;
    }

    public List<MoistureSensor> getTuttiISensori() {
        return sensori;
    }


    public Piantina getPiantinaBySensor(String thingId) {
        for (Map.Entry<String, List<MoistureSensor>> entry : associazioni.entrySet()) {
            for (MoistureSensor sensor : entry.getValue()) {
                if (sensor.thingId.equals(thingId)) {
                    return getPiantinaByName(entry.getKey());
                }
            }
        }
        return null;
    }
}
