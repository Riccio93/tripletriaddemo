package com.ricci.tt;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{
    private MainThread thread;
    private SceneManager manager;

    public GamePanel(Context context){
        super(context);
        //SurfaceHolder permette di controllare le dimensioni e il formato della superficie, di editare i singoli pixel
        //e di monitorare i singoli cambiamenti sulla superficie.
        getHolder().addCallback(this);
        Constants.CURRENT_CONTEXT = context;
        //Inizializza Thread
        this.thread = new MainThread(getHolder(), this);
        //Inizializza SceneManager
        this.manager = new SceneManager();
        //Necessario per il tocco
        setFocusable(true);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.manager.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.manager.onTouchEvent(event);
        return true;
    }

    public void update(){
        this.manager.update();
    }

    @Override
    //Metodo non utilizzato, richiesto dalla classe padre
    public void surfaceChanged(SurfaceHolder surfaceHolder, int int1, int int2, int int3){}

    @Override
    //Al momento della creazione della superficie, istanzia il Main Thread, passando come parametri l'holder e il GamePanel stesso
    public void surfaceCreated(SurfaceHolder surfaceHolder){
        this.thread = new MainThread(getHolder(), this);
        this.thread.setRunning(true);
        this.thread.start();
    }

    @Override
    //Quando la superficie è distrutta, chiude il Main Thread
    public void surfaceDestroyed(SurfaceHolder surfaceHolder){
        boolean retry = true;
        while(retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch(Exception e) {e.printStackTrace();}
            retry = false;
        }
    }
}
