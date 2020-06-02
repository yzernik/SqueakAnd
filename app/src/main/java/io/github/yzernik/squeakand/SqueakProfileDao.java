package io.github.yzernik.squeakand;

/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SqueakProfileDao {

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " ORDER BY profile_id ASC")
    LiveData<List<SqueakProfile>> getProfiles();

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " WHERE keyPair IS NOT NULL " + " ORDER BY profile_id ASC")
    LiveData<List<SqueakProfile>> getSigningProfiles();

    @Query("SELECT * from " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " WHERE keyPair IS NULL " + " ORDER BY profile_id ASC")
    LiveData<List<SqueakProfile>> getContactProfiles();

    @Query("SELECT * FROM " + SqueakRoomDatabase.TABLE_NAME_PROFILE + " WHERE profile_id = :profileId")
    LiveData<SqueakProfile> fetchProfileById(int profileId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SqueakProfile squeakProfile);

    @Update
    void update(SqueakProfile squeakProfile);

    @Query("DELETE FROM " + SqueakRoomDatabase.TABLE_NAME_PROFILE)
    void deleteAll();
}
