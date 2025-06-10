package ru.mirea.artemovnp.employeedb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "superheroes")
public class SuperHero {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String power;
    public int strengthLevel;

    public SuperHero(String name, String power, int strengthLevel) {
        this.name = name;
        this.power = power;
        this.strengthLevel = strengthLevel;
    }
}