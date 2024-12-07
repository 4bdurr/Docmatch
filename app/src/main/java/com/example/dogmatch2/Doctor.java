package com.example.dogmatch2;

public class Doctor {
    private String name;
    private String specialty;
    private double distance;
    private String imageUrl;

    public Doctor(String name, String specialty, double distance, String imageUrl) {
        this.name = name;
        this.specialty = specialty;
        this.distance = distance;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public double getDistance() {
        return distance;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}

