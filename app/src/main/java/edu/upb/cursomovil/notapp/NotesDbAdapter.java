/*
 * Copyright (C) 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package edu.upb.cursomovil.notapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Simple notes database access helper class. Defines the basic CRUD operations
 * for the notepad example, and gives the ability to list all notes as well as
 * retrieve or modify a specific note.
 * 
 * This has been improved from the first version of this tutorial through the
 * addition of better error handling and also using returning a Cursor instead
 * of using a collection of inner classes (which is less scalable and not
 * recommended).
 */
public class NotesDbAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_TYPE = "type"; // 'audio', 'text', 'image'
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_IMAGE = "image";
    public static final String KEY_ARCHIVED= "archived";
    public static final String KEY_REMINDER = "reminder";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_UPDATED_AT = "updated_at";
    public static final String KEY_TEXT_BODY = "text_body";
    public static final String KEY_AUDIO_PATH = "audio_path";
    public static final String KEY_IMAGE_PATH = "image_path";


    private static final String TAG = "NotesDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation sql statement
     */

    private static final String DATABASE_NAME = "notapp";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table "+DATABASE_TABLE+" ("+KEY_ROWID+" integer primary key autoincrement, "+
                    KEY_TITLE+" text default 'no title', "+
                    KEY_TYPE+" text not null, "+
                    KEY_ARCHIVED+" integer default 0, "+
                    KEY_REMINDER+" datetime default null, "+
                    KEY_CREATED_AT+" datetime default (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW')), "+
                    KEY_UPDATED_AT+" datetime default (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW')), "+
                    KEY_TEXT_BODY+" text default null, "+
                    KEY_AUDIO_PATH+" text default null, "+
                    KEY_IMAGE_PATH+" text default null);";



    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /*if(oldVersion<1) {
                db.execSQL("ALTER TABLE notes ADD COLUMN enabled integer");
            }*/

            //db.execSQL("ALTER TABLE notes ADD COLUMN date datetime default (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW'))");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public NotesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @return rowId or -1 if failed
     */
    public long createTextNote(String title, String text_body, String reminder) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_TEXT_BODY, text_body);
        initialValues.put(KEY_TYPE, TYPE_TEXT);
        initialValues.put(KEY_REMINDER, reminder);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public long createAudioNote(String title, String audioPath, String reminder) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_AUDIO_PATH, audioPath);
        initialValues.put(KEY_TYPE, TYPE_AUDIO);
        initialValues.put(KEY_REMINDER, reminder);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public long createImageNote(String title, String imagePath, String reminder) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_IMAGE_PATH, imagePath);
        initialValues.put(KEY_TYPE, TYPE_AUDIO);
        initialValues.put(KEY_REMINDER, reminder);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllNotes() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE,
                KEY_TEXT_BODY, KEY_REMINDER, KEY_ARCHIVED, KEY_TYPE, KEY_AUDIO_PATH}, null, null, null, null, KEY_CREATED_AT + " DESC");
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
//    public Cursor fetchNote(long rowId) throws SQLException {
//
//        Cursor mCursor =
//
//                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
//                        KEY_TITLE, KEY_BODY}, KEY_ROWID + "= ? ", new String[]{rowId+""}, null,
//                        null, null, null, null);
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//
//    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
//    public boolean updateNote(long rowId, String title, String body) {
//        ContentValues args = new ContentValues();
//        args.put(KEY_TITLE, title);
//        args.put(KEY_BODY, body);
//
//        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
//    }
}
