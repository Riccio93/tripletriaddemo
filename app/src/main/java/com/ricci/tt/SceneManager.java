package com.ricci.tt;

import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.ArrayList;

public class SceneManager {
    public static int ACTIVE_SCENE;
    private static ArrayList<Scene> scenes = new ArrayList();

    //Costruttore, aggiungo scena del menù
    public SceneManager(){
        ACTIVE_SCENE = 0;
        scenes.add(new MenuScene());
    }

    public static void addScene(Scene scene){
        scenes.add(scene);
    }

    public static void removeScene(int int1){
        if(scenes.get(int1)!=null)
            scenes.remove(int1);
    }

    //Rimuove tutte le scene tranne quella del menù, che rende la scena corrente
    public static void terminate(){
        ACTIVE_SCENE = 0;
        for (int i = 1; i < SceneManager.getSize(); i++) {
            SceneManager.removeScene(i);
        }
        update();
    }

    public static int getSize()
    {
        return scenes.size();
    }

    //In base ad ACTIVE_SCENE, esegue i metodi draw, update e onTouchEvent della scena adeguata
    public static void update()
    {
        (scenes.get(ACTIVE_SCENE)).update();
    }
    public void draw(Canvas canvas)
    {
        (scenes.get(ACTIVE_SCENE)).draw(canvas);
    }
    public void onTouchEvent(MotionEvent event)
    {
        (scenes.get(ACTIVE_SCENE)).onTouchEvent(event);
    }
}
