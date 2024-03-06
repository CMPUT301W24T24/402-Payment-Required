package com.example.qrcheckin.core;

public class IncrementableInt {
    private int value;

    public IncrementableInt(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    public void increment(){
        this.value++;
    }
}
