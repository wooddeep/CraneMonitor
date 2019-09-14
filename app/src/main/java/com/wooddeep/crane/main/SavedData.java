package com.wooddeep.crane.main;

public class SavedData {
    public float angle;
    public float range;
    public float vangle;

    public SavedData() {
    }

    public SavedData(float a, float r) {
        this.angle = a;
        this.range = r;
    }

    public SavedData(float a, float va, float r) {
        this.angle = a;
        this.range = r;
        this.vangle = va;
    }
}