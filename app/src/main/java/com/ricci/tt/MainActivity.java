package com.ricci.tt;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inserisce in Constants le dimensioni dello schermo
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Constants.SCREEN_HEIGHT = displayMetrics.heightPixels;
        Constants.SCREEN_WIDTH = displayMetrics.widthPixels;

        //Prende controllo dell'intero schermo (eliminando anche la barra del titolo e la status bar)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Setta il layout dell'activity alla surfaceview Gamepanel
        setContentView((new GamePanel(this)));

        //Crea un oggetto DbHandler e crea il database
        DbHandler db = new DbHandler(this);
        db.createDatabase();

        //Inserisce le carte predefinite nel database se non sono già presenti
        if(db.getCardsCount() == 0){
            Log.d("Insert:", "Inserting cards...");
            db.addCard(new Card(1,"Leon", 9, 5, 4, 7));
            db.addCard(new Card(2,"Hook",  9, 4, 9, 4));
            db.addCard(new Card(3,"Genie", 6, 10, 6, 5));
            db.addCard(new Card(4,"Simba", 3, 10, 5, 7));
            db.addCard(new Card(5,"Mushu", 7, 2, 7, 10));
            db.addCard(new Card(6,"Aerith", 7, 4, 6, 4));
            db.addCard(new Card(7,"Angel Star", 3, 5, 4, 4));
            db.addCard(new Card(8,"Battle Ship", 6, 3, 6, 3));
            db.addCard(new Card(9,"Behemoth", 6, 4, 8, 5));
            db.addCard(new Card(10,"Clayton", 8, 3, 3, 8));
            db.addCard(new Card(11,"Jasmine", 3, 7, 7, 3));
            db.addCard(new Card(12,"Jiminy", 6, 7, 1, 6));
            db.addCard(new Card(13,"Power Wild", 6, 5, 2, 1));
            db.addCard(new Card(14,"Rabbit", 3, 4, 3, 5));
            db.addCard(new Card(15,"Shadow", 2, 3, 5, 3));
            db.addCard(new Card(16,"Snow White", 7, 6, 4, 4));
            db.addCard(new Card(17,"Soldier", 4, 5, 1, 2));
        }

        /* DEBUG: Stampa le carte presenti nel database sul log
        Log.d("Reading:", "Reading all cards...");
        List<Card> cards = db.getAllCards();
        for (Card card : cards) {
            String log = "Id: " + card.getId() + " ,Name: " + card.getName() + " ,lValue " + card.getlValue() + " ,tValue " + card.gettValue() +
                    " ,rValue " + card.getrValue() + " ,bValue " + card.getbValue();
            Log.d("Card: : ", log);
        }
        */
    }

    @Override
    public void onBackPressed() {
        //Se l'utente preme il tasto indietro, torna alla scena del menù e svuota il manager di tutte le scene
        Sound.startSound(0);
        SceneManager manager = new SceneManager();
        manager.terminate();
    }
}
