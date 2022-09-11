package com.ricci.tt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.widget.Toast;

public class OptionsScene implements Scene{
    private Paint paint = new Paint();
    private Paint blackPaint = new Paint();
    private Bitmap back;
    private Bitmap optionsBack;
    private Bitmap music;
    private Bitmap sfx;
    private Bitmap difficulty;
    private Bitmap reset;

    private Rect optionsRect;
    private Rect blackRect;
    private Rect musicRect;
    private Rect sfxRect;
    private Rect difficultyRect;
    private Rect resetRect;

    DbHandler db = new DbHandler(Constants.CURRENT_CONTEXT);

    public OptionsScene(){
        new BitmapFactory();
        blackPaint.setColor(Color.BLACK);
        //Definisco tutti i Bitmap che verranno usati nel draw ed i relativi Rect
        this.back = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.menu_background);
        this.optionsBack = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.options_background_portrait);
        this.optionsRect = new Rect(Constants.SCREEN_WIDTH / 10, Constants.SCREEN_HEIGHT / 15, 9 * Constants.SCREEN_WIDTH / 10, 14 * Constants.SCREEN_HEIGHT / 15);
        this.blackRect = new Rect(Constants.SCREEN_WIDTH / 10 - 20, Constants.SCREEN_HEIGHT / 15 - 20, 9 * Constants.SCREEN_WIDTH / 10 + 20, 14 * Constants.SCREEN_HEIGHT / 15 + 20);
        this.music = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.music_button);
        this.sfx = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.sfx_button);
        this.difficulty = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.difficulty_button);
        this.reset = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.reset_button);
        this.musicRect = new Rect(-400 + Constants.SCREEN_WIDTH / 2,-80 + 2 * Constants.SCREEN_HEIGHT / 15, 400 + Constants.SCREEN_WIDTH / 2, 80 + 2 * Constants.SCREEN_HEIGHT/15);
        this.sfxRect = new Rect(-400 + Constants.SCREEN_WIDTH / 2,-80 + 5 * Constants.SCREEN_HEIGHT / 15, 400 + Constants.SCREEN_WIDTH / 2, 80 + 5 * Constants.SCREEN_HEIGHT/15);
        this.difficultyRect = new Rect(-400 + Constants.SCREEN_WIDTH / 2,-80 + 8 * Constants.SCREEN_HEIGHT / 15, 400 + Constants.SCREEN_WIDTH / 2, 80 + 8 * Constants.SCREEN_HEIGHT/15);
        this.resetRect = new Rect(-400 + Constants.SCREEN_WIDTH / 2,-80 + 11 * Constants.SCREEN_HEIGHT / 15, 400 + Constants.SCREEN_WIDTH / 2, 80 + 11 * Constants.SCREEN_HEIGHT/15);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.back, null, new Rect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT), paint);
        //blackRect è il contorno dello sfondo "interno" spesso pochi millimetri
        canvas.drawRect(blackRect, blackPaint);
        canvas.drawBitmap(this.optionsBack, null, optionsRect, paint);
        canvas.drawBitmap(this.music, null, musicRect, paint);
        canvas.drawBitmap(this.sfx, null, sfxRect, paint);
        canvas.drawBitmap(this.difficulty, null, difficultyRect, paint);
        canvas.drawBitmap(this.reset, null, resetRect, paint);
    }

    @Override
    public void update() {}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==0){
            //Se procedo al tocco...
            if(musicRect.contains((int)event.getX(), (int)event.getY())){
                Sound.startSound(2);
                if(Constants.MUSIC_FLAG){
                    //Se le flag sono a true assegno false e viceversa
                    Constants.MUSIC_FLAG = false;
                    Toast toast = Toast.makeText(Constants.CURRENT_CONTEXT, "La musica è stata disattivata", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    Constants.MUSIC_FLAG = true;
                    Toast toast = Toast.makeText(Constants.CURRENT_CONTEXT, "La musica è stata attivata", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            if(sfxRect.contains((int)event.getX(), (int)event.getY())){
                Sound.startSound(2);
                    if(Constants.SFX_FLAG){
                        Constants.SFX_FLAG = false;
                        Toast toast = Toast.makeText(Constants.CURRENT_CONTEXT, "Gli effetti sonori sono stati disattivati", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else{
                        Constants.SFX_FLAG = true;
                        Toast toast = Toast.makeText(Constants.CURRENT_CONTEXT, "Gli effetti sonori sono stati attivati", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                //La difficoltà cicla fra 3 valori (0 = Facile, 1 = Medio, 2 = Difficile)
            if(difficultyRect.contains((int)event.getX(), (int)event.getY())){
                Sound.startSound(2);
                if(Constants.difficulty == 0){
                    Constants.difficulty = 1;
                    Toast toast = Toast.makeText(Constants.CURRENT_CONTEXT, "Difficoltà: MEDIA", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(Constants.difficulty == 1){
                    Constants.difficulty = 2;
                    Toast toast = Toast.makeText(Constants.CURRENT_CONTEXT, "Difficoltà: DIFFICILE", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else{
                    Constants.difficulty = 0;
                    Toast toast = Toast.makeText(Constants.CURRENT_CONTEXT, "Difficoltà: FACILE", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            //Metodo che cancella il database ed esce dall'applicazione
            if(resetRect.contains((int)event.getX(), (int)event.getY())){
                db.dbDelete();
                System.exit(0);
            }
        }
        return true;
    }
}
