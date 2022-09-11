package com.ricci.tt;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread{
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;
    private static Canvas canvas;

    //La vista SurfaceView permette di utilizzare un thread secondario (questo) per aggiornare il suo contenuto

    //Costruttore
    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel){
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    public void run(){
        long startTime;
        long timeMillisecond;
        long waitTime;
        int frameCount = 0;
        long totalTime = 0;
        long targetTime = 1000 / Constants.MAX_FPS;

        while(running){
            startTime = System.nanoTime();
            canvas = null;
            //Lock del canvas, chiama i metodi update e draw di GamePanel (che chiamano a loro volta il manager
            //delle scene che esegue il metodo della scena corrente)
            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                //Se il canvas non è vuoto, toglie il lock e visualizza il canvas sullo schermo
                if(canvas != null){
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }

            timeMillisecond = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillisecond;
            //Se i Frames Per Second sono maggiori del massimo, il thread aspetta
            try{
                if(waitTime > 0)
                    this.sleep(waitTime);
            }catch(Exception e){
                e.printStackTrace();
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == Constants.MAX_FPS){
                //AVERAGE_FPS non utilizzato, solo per comando di debug di visualizzazione sul log
                Constants.AVERAGE_FPS = (int)(1000/((totalTime / frameCount)/1000000));
                frameCount = 0;
                totalTime = 0;
            }
            //DEBUG: Stampa gli fps sul log
            //System.out.println("Average FPS: "+Constants.AVERAGE_FPS);
        }
    }

    //Metodo che fa partire il thread
    public void setRunning(boolean paramBoolean)
    {
        this.running = paramBoolean;
    }

}



