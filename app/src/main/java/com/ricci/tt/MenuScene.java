package com.ricci.tt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class MenuScene implements Scene{

    private Paint paint = new Paint();
    private Bitmap back;
    private Bitmap logo;
    private Bitmap playLogo;
    private Bitmap optionsLogo;
    private Bitmap createLogo;
    private Rect logoRect;
    private Rect playRect;
    private Rect optionsRect;
    private Rect createRect;

    public MenuScene(){
        new BitmapFactory();
        Sound.startSound(1);

        this.back = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.menu_background);
        this.logo = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.menu_logo);
        this.logoRect = new Rect(-550 + Constants.SCREEN_WIDTH / 2, -200 + Constants.SCREEN_HEIGHT /5, 550 + Constants.SCREEN_WIDTH / 2, 200 + Constants.SCREEN_HEIGHT /5);
        this.playLogo = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.play_button);
        this.playRect = new Rect(-500 + Constants.SCREEN_WIDTH / 2, -90 + 4 * Constants.SCREEN_HEIGHT / 5, -50 + Constants.SCREEN_WIDTH / 2, 90 + 4 * Constants.SCREEN_HEIGHT / 5);
        this.optionsLogo = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.options_button);
        this.optionsRect = new Rect(-250 + Constants.SCREEN_WIDTH / 2, -50 + 9 * Constants.SCREEN_HEIGHT / 10, 250 + Constants.SCREEN_WIDTH / 2, 130 + 9 * Constants.SCREEN_HEIGHT / 10);
        this.createLogo = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.create_button);
        this.createRect = new Rect(50 + Constants.SCREEN_WIDTH / 2, -90 + 4 * Constants.SCREEN_HEIGHT / 5, 500 + Constants.SCREEN_WIDTH / 2, 90 + 4 * Constants.SCREEN_HEIGHT / 5);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.back, null, new Rect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT), paint);
        canvas.drawBitmap(this.logo, null, logoRect, paint);
        canvas.drawBitmap(this.playLogo, null, playRect, paint);
        canvas.drawBitmap(this.optionsLogo, null, optionsRect, paint);
        canvas.drawBitmap(this.createLogo, null, createRect, paint);
    }

    @Override
    public void update() {
        //Quando nel menù principale, elimino tutte le scene tranne quella corrente
        for (int i = 1; i < SceneManager.getSize(); i++) {
            SceneManager.removeScene(i);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Gestisco risposta al tocco
        if((event.getAction() == 0) && (this.playRect.contains((int)event.getX(), (int)event.getY()))){
            Sound.media.release();
            Sound.startSound(2);
            SceneManager.addScene(new CardSelectScene());
            SceneManager.ACTIVE_SCENE = 1;
        }
        if((event.getAction() == 0) && (this.optionsRect.contains((int)event.getX(), (int)event.getY()))){
            Sound.media.release();
            Sound.startSound(2);
            SceneManager.addScene(new OptionsScene());
            SceneManager.ACTIVE_SCENE = 1;
        }
        //Con la pressione sul bottone Crea, l'applicazione passa alla seconda activity (CamActivity)
        if((event.getAction()==0) && (this.createRect.contains((int)event.getX(), (int)event.getY()))){
            Sound.media.release();
            Sound.startSound(2);
            Intent intent = new Intent(Constants.CURRENT_CONTEXT, CamActivity.class);
            Constants.CURRENT_CONTEXT.startActivity(intent);
        }
        return true;
    }
}
