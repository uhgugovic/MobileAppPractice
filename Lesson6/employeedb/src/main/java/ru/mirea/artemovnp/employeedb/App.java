package ru.mirea.artemovnp.employeedb;

import android.app.Application;
import androidx.room.Room;

public class App extends Application {
    private static App instance;
    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "superhero-db")
                .allowMainThreadQueries() // Только для демонстрации!
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}