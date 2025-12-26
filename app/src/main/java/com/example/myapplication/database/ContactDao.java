package com.example.myapplication.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * ContactDao
 * - Data Access Object cho Contact
 * - Các thao tác CRUD với database
 */
@Dao
public interface ContactDao {

    @Query("SELECT * FROM contacts ORDER BY name ASC")
    List<Contact> getAllContacts();

    @Query("SELECT * FROM contacts WHERE id = :id")
    Contact getContactById(int id);

    @Insert
    void insertContact(Contact contact);

    @Update
    void updateContact(Contact contact);

    @Delete
    void deleteContact(Contact contact);

    @Query("DELETE FROM contacts WHERE id = :id")
    void deleteContactById(int id);
}

