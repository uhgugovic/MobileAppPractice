package ru.mirea.artemovnp.employeedb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface SuperHeroDao {
    @Query("SELECT * FROM superheroes")
    List<SuperHero> getAll();

    @Query("SELECT * FROM superheroes WHERE id = :id")
    SuperHero getById(long id);

    @Insert
    void insert(SuperHero hero);

    @Update
    void update(SuperHero hero);

    @Delete
    void delete(SuperHero hero);
}