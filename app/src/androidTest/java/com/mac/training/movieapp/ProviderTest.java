package com.mac.training.movieapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

public class ProviderTest extends AndroidTestCase {

    private static final String TEST_GENRE_NAME = "Family";
    private static final String TEST_UPDATE_GENRE_NAME = "SciFi";
    private static final String TEST_MOVIE_NAME = "Back to the Future";
    private static final String TEST_UPDATE_MOVIE_NAME = "Back to the Future II";
    private static final String TEST_MOVIE_RELEASE_DATE = "1985-09-15";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testDeleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testDeleteAllRecords();
    }

    public void testDeleteAllRecords() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                MovieContract.GenreEntry.CONTENT_URI,
                null,
                null
        );

        // Select * from movies
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Make sure there are no records
        assertEquals(0, cursor.getCount());

        // Select * from genres
        cursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Make sure there are no records
        assertEquals(0, cursor.getCount());

        cursor.close();
    }

    public void testGetType() {
        // content_authority = "content://com.training.mac.moviedatabase/:

        String type = mContext.getContentResolver().getType(MovieContract.GenreEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com..../genre
        assertEquals(MovieContract.GenreEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.GenreEntry.buildGenreUri(0));
        assertEquals(MovieContract.GenreEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        assertEquals(MovieContract.MovieEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieUri(0));
        assertEquals(MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);

    }

    public void testInsertReadGenre() {
        ContentValues genreContentValues = getGenreContentValues();
        Uri genreInsertUri = mContext
                .getContentResolver()
                .insert(MovieContract.GenreEntry.CONTENT_URI, genreContentValues);
        long genreRowId = ContentUris.parseId(genreInsertUri);

        assertTrue(genreRowId>0);

        // selects all
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        validateCursor(cursor, genreContentValues);
        cursor.close();

        // select specific
        cursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.buildGenreUri(genreRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(cursor, genreContentValues);
        cursor.close();
    }

    /// testInsertReadMovie
    public void testInsertReadMovie(){
        ContentValues genreContentValues = getGenreContentValues();
        Uri genreInsertUri = mContext
                .getContentResolver()
                .insert(
                        MovieContract.GenreEntry.CONTENT_URI,
                        genreContentValues
                );
        long genreRowId = ContentUris.parseId(genreInsertUri);

        ContentValues movieContentValues = getMovieContentValues(genreRowId);
        Uri movieInsertUri = mContext
                .getContentResolver()
                .insert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieContentValues
                );
        long movieRowId = ContentUris.parseId(movieInsertUri);

        assertTrue(movieRowId > 0);

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, movieContentValues);
        movieCursor.close();

        movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMovieUri(movieRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, movieContentValues);
        movieCursor.close();
    }

    public void testUpdateGenre() {
        ContentValues genreValues = getGenreContentValues();
        Uri genreInsertUri = mContext
                .getContentResolver()
                .insert(MovieContract.GenreEntry.CONTENT_URI, genreValues);
        long genreRowId = ContentUris.parseId(genreInsertUri);

        // Update values
        ContentValues updatedGenreValues = new ContentValues();
        updatedGenreValues.put(MovieContract.GenreEntry._ID, genreRowId);
        updatedGenreValues.put(MovieContract.GenreEntry.COLUMN_NAME, TEST_UPDATE_GENRE_NAME);

        mContext.getContentResolver().update(
                MovieContract.GenreEntry.CONTENT_URI,
                updatedGenreValues,
                MovieContract.GenreEntry._ID + "= ?",
                new String[]{String.valueOf(genreRowId)}
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.buildGenreUri(genreRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(cursor, updatedGenreValues);
        cursor.close();
    }

    //testUpdateMovie
    public void testUpdateMovie(){
        ContentValues genreContentValues = getGenreContentValues();
        Uri genreInsertUri = mContext.getContentResolver().insert(MovieContract.GenreEntry.CONTENT_URI, genreContentValues);
        long genreRowId = ContentUris.parseId(genreInsertUri);

        ContentValues movieContentValues = getMovieContentValues(genreRowId);
        Uri movieInsertUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieContentValues);
        long movieRowId = ContentUris.parseId(movieInsertUri);

        // Update
        ContentValues updatedMovieContentValues = new ContentValues(movieContentValues);
        updatedMovieContentValues.put(MovieContract.MovieEntry._ID, movieRowId);
        updatedMovieContentValues.put(MovieContract.MovieEntry.COLUMN_NAME, TEST_UPDATE_MOVIE_NAME);
        mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                updatedMovieContentValues,
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(movieRowId)}
        );

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMovieUri(movieRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, updatedMovieContentValues);
        movieCursor.close();
    }

    private ContentValues getGenreContentValues() {
        ContentValues value = new ContentValues();
        value.put(MovieContract.GenreEntry.COLUMN_NAME, TEST_GENRE_NAME);
        return value;
    }

    private ContentValues getMovieContentValues(long genreID){
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_GENRE, genreID);
        values.put(MovieContract.MovieEntry.COLUMN_NAME, TEST_MOVIE_NAME);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, TEST_MOVIE_RELEASE_DATE);
        return values;
    }

    private void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for(Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);

            assertFalse(idx == -1);

            switch (valueCursor.getType(idx)) {
                case Cursor.FIELD_TYPE_FLOAT:
                    assertEquals(entry.getValue(), valueCursor.getDouble(idx));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    assertEquals(Integer.parseInt(entry.getValue().toString()), valueCursor.getInt(idx));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    assertEquals(entry.getValue(), valueCursor.getString(idx));
                    break;
                default:
                    assertEquals(entry.getValue(), valueCursor.getString(idx));
                    break;
            }
        }
        valueCursor.close();
    }

}
