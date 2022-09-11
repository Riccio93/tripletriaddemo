/*INTERNAL DB LIST ID
01 - Leon
02 - Hook
03 - Genie
04 - Simba
05 - Mushu
06 - Aerith
07 - Angel Star
08 - Battle Ship
09 - Behemoth
10 - Clayton
11 - Jasmine
12 - Jiminy
13 - Power Wild
14 - Rabbit
15 - Shadow
16 - Snow White
17 - Soldier
18/... - Custom Cards
*/

package com.ricci.tt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DbHandler extends SQLiteOpenHelper{
    private static final String DB_NAME = "Database";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "Cards";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LVALUE = "lValue";
    private static final String KEY_TVALUE = "tValue";
    private static final String KEY_RVALUE = "rValue";
    private static final String KEY_BVALUE = "bValue";

    public DbHandler (Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    //Stringa di creazione della table
    private static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + " ("
            + KEY_ID+ " integer primary key, "
            + KEY_NAME + " text, "
            + KEY_LVALUE + " integer not null, "
            + KEY_TVALUE + " integer not null, "
            + KEY_RVALUE + " integer not null, "
            + KEY_BVALUE + " integer not null);";

    @Override
    public void onCreate(SQLiteDatabase db){
    }

    public void createDatabase(){
        //Crea tabella del database
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(TABLE_CREATE);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //In caso di upgrade della versione del DB, elimina tabella e la ricrea
        //(Override richiesto da classe madre, metodo non utilizzato)
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

    public void addCard(Card card){
        //Metodo per aggiungere una carta al DB
        SQLiteDatabase db = this.getWritableDatabase();
        //Inserisco tutti i valori della carta in contentValues....
        ContentValues values = new ContentValues();
        values.put(KEY_ID, card.getId());
        values.put(KEY_NAME, card.getName());
        values.put(KEY_LVALUE, card.getlValue());
        values.put(KEY_TVALUE, card.gettValue());
        values.put(KEY_RVALUE, card.getrValue());
        values.put(KEY_BVALUE, card.getbValue());
        //... ed inserisco la carta nel DB
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Card getCard(int id){
        //Metodo che restituisce una carta presente nel DB (Senza rimuoverla)
        SQLiteDatabase db = this.getReadableDatabase();
        //Esegue query al DataBase, impostando come selezione l'uguaglianza dell'id a quello passato al metodo
        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
                        KEY_NAME, KEY_LVALUE, KEY_TVALUE, KEY_RVALUE, KEY_BVALUE}, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        //Se la carta che corrisponde alla condizione della query esiste, cursor va al primo elemento (la carta stessa)
        if (cursor != null)
            cursor.moveToFirst();
        //Inserisce tutti i valori nell'oggetto Carta
        Card card = new Card(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)),
                Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));
        cursor.close();
        db.close();
        //Restituisce oggetto carta richiesto
        return card;
    }

    //Metodo che restituisce un ArrayList contenente tutte le carte presenti nel Database
    public List<Card> getAllCards() {
        List<Card> cardList = new ArrayList<Card>();
        //Testo della query
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Se ci sono elementi...
        if (cursor.moveToFirst()) {
            //Finchè ci sono elementi, aggiungi la carta alla lista e passa alla prossima
            do {
                Card card = new Card();
                card.setId(Integer.parseInt(cursor.getString(0)));
                card.setName(cursor.getString(1));
                card.setlValue(Integer.parseInt(cursor.getString(2)));
                card.settValue(Integer.parseInt(cursor.getString(3)));
                card.setrValue(Integer.parseInt(cursor.getString(4)));
                card.setbValue(Integer.parseInt(cursor.getString(5)));
                cardList.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cardList;
    }

    //Metodo che restituisce il numero di carte presenti nel DB
    public int getCardsCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int c=cursor.getCount();
        cursor.close();
        db.close();
        return c;
    }

    //Metodo che cancella la tabella del Database
    public void dbDelete(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        db.close();
    }
}