package com.fabianbleile.fordigitalimmigrants.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ContactDao {
    @Query("SELECT * FROM contact")
    List<Contact> getAll();

    @Query("SELECT * FROM contact WHERE cid IN (:userIds)")
    List<Contact> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM contact WHERE name LIKE :first AND "
            + "phonenumber LIKE :last LIMIT 1")

    Contact findByName(String first, String last);

    @Insert
    void insertAll(Contact... contacts);

    @Insert
    void insertContact(Contact contact);

    @Delete
    void delete(Contact contact);
}
