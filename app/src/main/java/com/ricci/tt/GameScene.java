package com.ricci.tt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import java.util.Random;

public class GameScene implements Scene{

    DbHandler db = new DbHandler(Constants.CURRENT_CONTEXT);

    private Bitmap fullBack;
    private Bitmap youWin;
    private Bitmap youLose;
    private Bitmap draw;
    private Bitmap return_to_menu;
    private Bitmap backCard;

    //Entrambe le versioni delle carte, sia del giocatore sia dell'avversario
    private Bitmap card1;
    private Bitmap card2;
    private Bitmap card3;
    private Bitmap card4;
    private Bitmap card5;
    private Bitmap card1r;
    private Bitmap card2r;
    private Bitmap card3r;
    private Bitmap card4r;
    private Bitmap card5r;
    private Bitmap advCard1;
    private Bitmap advCard2;
    private Bitmap advCard3;
    private Bitmap advCard4;
    private Bitmap advCard5;
    private Bitmap advCard1b;
    private Bitmap advCard2b;
    private Bitmap advCard3b;
    private Bitmap advCard4b;
    private Bitmap advCard5b;

    private Rect tableRect;
    //Creo gli oggetti GameSpace per entrambe le mani e la tavola da gioco
    private GameSpace[][] matrix = new GameSpace[3][3];
    private GameSpace[] hand = new GameSpace[5];
    private GameSpace[] advHand = new GameSpace[5];

    //Variabili booleane che identificano se una carta del giocatore è trascinata al momento
    private boolean lock1 = false;
    private boolean lock2 = false;
    private boolean lock3 = false;
    private boolean lock4 = false;
    private boolean lock5 = false;

    //Posizione originaria delle carte nella mano del giocatore, utilizzato se il giocatore sposta la carta in una posizione non valida
    private int nativeX;
    private int nativeY;

    //Variabili utili al calcolo della mossa dell'avversario
    private int maxTurnedCards;
    private int maxFreeSpots;

    //Conta punti
    private int bluePoints = 5;
    private int redPoints = 5;

    //Numero di mosse
    private int moves = 0;
    //Boolean che indica se la partita è finita
    private boolean gameOver = false;

    //animating : Sto eseguendo animazione
    //animationDone : Ho terminato l'animazione
    private boolean animating = false;
    private boolean animationDone = false;

    //Variabili che indicano di quanto le carte dell'avversario devono essere spostate durante l'animazione
    private int moveOfX;
    private int moveOfY;

    //Dopo i calcoli del turno dell'avversario: sposta la carta thisMovei nella mano nella posizione thisMovea x thisMoveb della griglia di gioco
    private int thisMovei=0;
    private int thisMovea=0;
    private int thisMoveb=0;

    //Boolean che indica se è in corso il turno del giocatore
    private boolean playerTurn;
    //Tempo di attesa del Thread prima del turno dell'avversario
    private long waiting = 1500;

    //FileInputStream, necessario per accedere alla memoria interna, dove si trovano i Bitmap delle carte custom
    private java.io.FileInputStream input;

    private Rect noRect = new Rect(0,0,0,0);

    //Costruttore
    public GameScene(Card paramHand0, Card paramHand1, Card paramHand2, Card paramHand3, Card paramHand4, Card paramAdvHand1, Card paramAdvHand2, Card paramAdvHand3, Card paramAdvHand4, Card paramAdvHand5){
        new BitmapFactory();
        Sound.media.release();
        Sound.startSound(4);

        //Il primo giocatore della partita è casuale
        Random random = new Random();
        playerTurn = random.nextBoolean();

        //Inizializzo array (Sia della tavola, sia della mano), argomento di GameSpace indica l'owner (0 = nessuno, 1 = Giocatore, 2 = Avversario)
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                matrix[i][j] = new GameSpace(0);
        for(int i=0;i<5;i++){
            hand[i]=new GameSpace(1);
            advHand[i]=new GameSpace(2);
        }

        this.fullBack = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.game_full_background);
        this.backCard = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.icon);
        this.youWin = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.won_button);
        this.youLose = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.lost_button);
        this.draw = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.pari_button);
        this.return_to_menu = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), R.drawable.press_to_menu_button);

        //Assegno le carte in mano di entrambi i giocatori
        hand[0].setCard(db.getCard(paramHand0.getId()));
        hand[1].setCard(db.getCard(paramHand1.getId()));
        hand[2].setCard(db.getCard(paramHand2.getId()));
        hand[3].setCard(db.getCard(paramHand3.getId()));
        hand[4].setCard(db.getCard(paramHand4.getId()));
        advHand[0].setCard(db.getCard((paramAdvHand1.getId())));
        advHand[1].setCard(db.getCard((paramAdvHand2.getId())));
        advHand[2].setCard(db.getCard((paramAdvHand3.getId())));
        advHand[3].setCard(db.getCard((paramAdvHand4.getId())));
        advHand[4].setCard(db.getCard((paramAdvHand5.getId())));

        //Costruisco l'identifier dei Bitmap a partire dall'id della carta
        String advImageName1 = "r"+Integer.toString(advHand[0].getCard().getId());
        String advImageName2 = "r"+Integer.toString(advHand[1].getCard().getId());
        String advImageName3 = "r"+Integer.toString(advHand[2].getCard().getId());
        String advImageName4 = "r"+Integer.toString(advHand[3].getCard().getId());
        String advImageName5 = "r"+Integer.toString(advHand[4].getCard().getId());
        String advImageName1b = "b"+Integer.toString(advHand[0].getCard().getId());
        String advImageName2b = "b"+Integer.toString(advHand[1].getCard().getId());
        String advImageName3b = "b"+Integer.toString(advHand[2].getCard().getId());
        String advImageName4b = "b"+Integer.toString(advHand[3].getCard().getId());
        String advImageName5b = "b"+Integer.toString(advHand[4].getCard().getId());
        int advImageId1 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(advImageName1, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
        int advImageId2 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(advImageName2, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
        int advImageId3 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(advImageName3, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
        int advImageId4 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(advImageName4, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
        int advImageId5 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(advImageName5, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
        int advImageId1b = Constants.CURRENT_CONTEXT.getResources().getIdentifier(advImageName1b, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
        int advImageId2b = Constants.CURRENT_CONTEXT.getResources().getIdentifier(advImageName2b, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
        int advImageId3b = Constants.CURRENT_CONTEXT.getResources().getIdentifier(advImageName3b, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
        int advImageId4b = Constants.CURRENT_CONTEXT.getResources().getIdentifier(advImageName4b, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
        int advImageId5b = Constants.CURRENT_CONTEXT.getResources().getIdentifier(advImageName5b, "drawable", Constants.CURRENT_CONTEXT.getPackageName());

        //Bitmap delle carte avversarie
        this.advCard1 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), advImageId1);
        this.advCard2 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), advImageId2);
        this.advCard3 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), advImageId3);
        this.advCard4 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), advImageId4);
        this.advCard5 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), advImageId5);
        this.advCard1b = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), advImageId1b);
        this.advCard2b = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), advImageId2b);
        this.advCard3b = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), advImageId3b);
        this.advCard4b = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), advImageId4b);
        this.advCard5b = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), advImageId5b);

        try{
            //Per ogni carta nella mano, se è predefinita carica Bitmap da Drawable, se è custom carica Bitmap dalla memoria interna
            if((hand[0].getCard().getId())<=17){
                //Ricavo identifier del Bitmap a partire dall'id della carta, poi carico Bitmap
                String imageName1 = "b"+Integer.toString(hand[0].getCard().getId());
                int imageId1 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(imageName1, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                this.card1 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(),imageId1);
                String imageName1r = "r"+Integer.toString(hand[0].getCard().getId());
                int imageId1r = Constants.CURRENT_CONTEXT.getResources().getIdentifier(imageName1r, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                this.card1r = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), imageId1r);
            }
            else{
                //Bitmap da memoria interna tramite un fileInputStream
                input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(hand[0].getCard().getId())+"b.png");
                this.card1 = BitmapFactory.decodeStream(input);
                input.close();
                input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(hand[0].getCard().getId())+"r.png");
                this.card1r = BitmapFactory.decodeStream(input);
                input.close();
            }

            if((hand[1].getCard().getId())<=17){
                String imageName2 = "b"+Integer.toString(hand[1].getCard().getId());
                int imageId2 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(imageName2, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                this.card2 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(),imageId2);
                String imageName2r = "r"+Integer.toString(hand[1].getCard().getId());
                int imageId2r = Constants.CURRENT_CONTEXT.getResources().getIdentifier(imageName2r, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                this.card2r = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), imageId2r);
            }
            else{
                input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(hand[1].getCard().getId())+"b.png");
                this.card2 = BitmapFactory.decodeStream(input);
                input.close();
                input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(hand[1].getCard().getId())+"r.png");
                this.card2r = BitmapFactory.decodeStream(input);
                input.close();
            }
            if((hand[2].getCard().getId())<=17){
                String imageName3 = "b"+Integer.toString(hand[2].getCard().getId());
                int imageId3 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(imageName3, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                this.card3 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(),imageId3);
                String imageName3r = "r"+Integer.toString(hand[2].getCard().getId());
                int imageId3r = Constants.CURRENT_CONTEXT.getResources().getIdentifier(imageName3r, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                this.card3r = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), imageId3r);
            }
            else{
                input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(hand[2].getCard().getId())+"b.png");
                this.card3 = BitmapFactory.decodeStream(input);
                input.close();
                input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(hand[2].getCard().getId())+"r.png");
                this.card3r = BitmapFactory.decodeStream(input);
                input.close();
            }
            if((hand[3].getCard().getId())<=17){
                String imageName4 = "b"+Integer.toString(hand[3].getCard().getId());
                int imageId4 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(imageName4, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                this.card4 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(),imageId4);
                String imageName4r = "r"+Integer.toString(hand[3].getCard().getId());
                int imageId4r = Constants.CURRENT_CONTEXT.getResources().getIdentifier(imageName4r, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                this.card4r = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), imageId4r);
            }
            else{
                input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(hand[3].getCard().getId())+"b.png");
                this.card4 = BitmapFactory.decodeStream(input);
                input.close();
                input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(hand[3].getCard().getId())+"r.png");
                this.card4r = BitmapFactory.decodeStream(input);
                input.close();
            }
            if((hand[4].getCard().getId())<=17){
                String imageName5 = "b"+Integer.toString(hand[4].getCard().getId());
                int imageId5 = Constants.CURRENT_CONTEXT.getResources().getIdentifier(imageName5, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                this.card5 = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(),imageId5);
                String imageName5r = "r"+Integer.toString(hand[4].getCard().getId());
                int imageId5r = Constants.CURRENT_CONTEXT.getResources().getIdentifier(imageName5r, "drawable", Constants.CURRENT_CONTEXT.getPackageName());
                this.card5r = BitmapFactory.decodeResource(Constants.CURRENT_CONTEXT.getResources(), imageId5r);
            }
            else{
                input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(hand[4].getCard().getId())+"b.png");
                this.card5 = BitmapFactory.decodeStream(input);
                input.close();
                input = Constants.CURRENT_CONTEXT.openFileInput(Integer.toString(hand[4].getCard().getId())+"r.png");
                this.card5r = BitmapFactory.decodeStream(input);
                input.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //Creo i Rect, per tavola e mani

        this.tableRect = new Rect(134, 330, 946, 1590);
        matrix[0][0].setRect(new Rect(134, 330, 404, 750));
        matrix[0][1].setRect(new Rect(405, 330, 675, 750));
        matrix[0][2].setRect(new Rect(676, 330, 946, 750));
        matrix[1][0].setRect(new Rect(134, 750, 404, 1170));
        matrix[1][1].setRect(new Rect(405, 750, 675, 1170));
        matrix[1][2].setRect(new Rect(676, 750, 946, 1170));
        matrix[2][0].setRect(new Rect(134, 1170, 404, 1590));
        matrix[2][1].setRect(new Rect(405, 1170, 675, 1590));
        matrix[2][2].setRect(new Rect(676, 1170, 946, 1590));

        hand[0].setRect(new Rect(50, 1700, 320, 2120));
        hand[1].setRect(new Rect(230, 1700, 500, 2120));
        hand[2].setRect(new Rect(410, 1700, 680, 2120));
        hand[3].setRect(new Rect(590, 1700, 860, 2120));
        hand[4].setRect(new Rect(770, 1700, 1040, 2120));

        advHand[0].setRect(new Rect(50, -195, 320, 215));
        advHand[1].setRect(new Rect(230, -195, 500, 215));
        advHand[2].setRect(new Rect(410, -195, 680, 215));
        advHand[3].setRect(new Rect(590, -195, 860, 215));
        advHand[4].setRect(new Rect(770, -195, 1040, 215));

        //Setto immagini alle carte in entrambe le mani, insieme al nome del Bitmap(per utilizzo nel metodo invertCard())
        hand[0].setImage(card1);
        hand[0].setImageName("card1");
        hand[1].setImage(card2);
        hand[1].setImageName("card2");
        hand[2].setImage(card3);
        hand[2].setImageName("card3");
        hand[3].setImage(card4);
        hand[3].setImageName("card4");
        hand[4].setImage(card5);
        hand[4].setImageName("card5");
        advHand[0].setImage(advCard1);
        advHand[0].setImageName("advCard1");
        advHand[1].setImage(advCard2);
        advHand[1].setImageName("advCard2");
        advHand[2].setImage(advCard3);
        advHand[2].setImageName("advCard3");
        advHand[3].setImage(advCard4);
        advHand[3].setImageName("advCard4");
        advHand[4].setImage(advCard5);
        advHand[4].setImageName("advCard5");
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();

        //Definisce paint per il punteggio (scorePaint) ed il paint per l'ombra del font (blackPaint)
        Paint scorePaint = new Paint();
        Typeface typeface = Typeface.create("arial", Typeface.BOLD_ITALIC);
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(80);
        scorePaint.setTypeface(typeface);
        Paint blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);
        blackPaint.setTextSize(80);
        blackPaint.setTypeface(typeface);

        //Disegna sfondo
        canvas.drawBitmap(this.fullBack, null, new Rect(-100, 0, Constants.SCREEN_WIDTH + 100, Constants.SCREEN_HEIGHT), paint);

        //Disegna tutte le carte in entrambe le mani, se le carte avversarie non sono ancora state giocate si mostra l'immagine della carta coperta
        for (int i = 0; i < 5; i++) {
            if (hand[i].getImage() != null)
                canvas.drawBitmap(hand[i].getImage(), null, hand[i].getRect(), paint);
            if (advHand[i].getImage() != null) {
                if (advHand[i].getCard().isPlayed()) {
                    canvas.drawBitmap(advHand[i].getImage(), null, advHand[i].getRect(), paint);
                } else {
                    canvas.drawBitmap(backCard, null, advHand[i].getRect(), paint);
                }
            }
        }

        //Si disegnano le carte già giocate(e quindi nel GameSpace di una casella della tavola)
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (matrix[i][j].getImage() != null)
                    canvas.drawBitmap(matrix[i][j].getImage(), null, matrix[i][j].getRect(), paint);

        //Scrive punteggio, scritta bianca + ombra nera
        canvas.drawText("Avversario: "+redPoints, 303, 313, blackPaint);
        canvas.drawText("Avversario: "+redPoints, 300, 310, scorePaint);
        canvas.drawText("Giocatore: "+bluePoints, 303, Constants.SCREEN_HEIGHT-258, blackPaint);
        canvas.drawText("Giocatore: "+bluePoints, 300, Constants.SCREEN_HEIGHT-255, scorePaint);


        //Refresh della carta quando è spostata dal giocatore
        if (lock1)
            canvas.drawBitmap(this.card1, null, hand[0].getRect(), paint);
        if (lock2)
            canvas.drawBitmap(this.card2, null, hand[1].getRect(), paint);
        if (lock3)
            canvas.drawBitmap(this.card3, null, hand[2].getRect(), paint);
        if (lock4)
            canvas.drawBitmap(this.card4, null, hand[3].getRect(), paint);
        if (lock5)
            canvas.drawBitmap(this.card5, null, hand[4].getRect(), paint);

        //Se la partita è finita, mostra chi ha vinto e un bottone che incita a premere per tornare al menù
        if (gameOver) {
            if (bluePoints > redPoints) {
                canvas.drawBitmap(youWin, null, new Rect(((Constants.SCREEN_WIDTH / 2) - 400), ((Constants.SCREEN_HEIGHT / 4) - 120), ((Constants.SCREEN_WIDTH / 2) + 400), ((Constants.SCREEN_HEIGHT / 4) + 120)), paint);
            } else if (redPoints > bluePoints) {
                canvas.drawBitmap(youLose, null, new Rect(((Constants.SCREEN_WIDTH / 2) - 400), ((Constants.SCREEN_HEIGHT / 4) - 120), ((Constants.SCREEN_WIDTH / 2) + 400), ((Constants.SCREEN_HEIGHT / 4) + 120)), paint);
            } else if (bluePoints == redPoints) {
                canvas.drawBitmap(draw, null, new Rect(((Constants.SCREEN_WIDTH / 2) - 400), ((Constants.SCREEN_HEIGHT / 4) - 120), ((Constants.SCREEN_WIDTH / 2) + 400), ((Constants.SCREEN_HEIGHT / 4) + 120)), paint);
            }
            canvas.drawBitmap(return_to_menu, null, new Rect(((Constants.SCREEN_WIDTH / 2) - 500), ((3*Constants.SCREEN_HEIGHT / 4) - 90), ((Constants.SCREEN_WIDTH / 2) + 500), ((3*Constants.SCREEN_HEIGHT / 4) + 90)), paint);
        }

        //Se sono in un animazione...
        if(animating){
            //... se la carta dell'avversario si trova a sinistra della casella destinazione, sposta di
            //1/12 di percorso ogni ciclo del draw, e in più ridisegna il Bitmap della carta in movimento (per essere in primo piano)
            if(advHand[thisMovei].getRect().left < matrix[thisMovea][thisMoveb].getRect().left){
                advHand[thisMovei].getRect().offset(moveOfX/12, 0);
                canvas.drawBitmap(backCard, null, advHand[thisMovei].getRect(), paint);
            }
            //Se è a destra
            else if(advHand[thisMovei].getRect().left > matrix[thisMovea][thisMoveb].getRect().left){
                advHand[thisMovei].getRect().offset(-moveOfX/12, 0);
                canvas.drawBitmap(backCard, null, advHand[thisMovei].getRect(), paint);
            }
            //Se è sopra
            if(advHand[thisMovei].getRect().top < matrix[thisMovea][thisMoveb].getRect().top){
                advHand[thisMovei].getRect().offset(0, moveOfY/12);
                canvas.drawBitmap(backCard, null, advHand[thisMovei].getRect(), paint);
            }
            //Se è sotto
            else if(advHand[thisMovei].getRect().top > matrix[thisMovea][thisMoveb].getRect().top){
                advHand[thisMovei].getRect().offset(0, -moveOfY/12);
                canvas.drawBitmap(backCard, null, advHand[thisMovei].getRect(), paint);
            }
            //Se la carta della mano e la casella destinazione hanno le stesse coordinate, l'animazione è terminata e i rect vengono sovrapposti
            if((Math.abs(advHand[thisMovei].getRect().left - matrix[thisMovea][thisMoveb].getRect().left) <= moveOfX/12 || (Math.abs(advHand[thisMovei].getRect().left - matrix[thisMovea][thisMoveb].getRect().left) <= 6) &&
                    (Math.abs(advHand[thisMovei].getRect().top - matrix[thisMovea][thisMoveb].getRect().top) <= moveOfY/12) || (Math.abs(advHand[thisMovei].getRect().top - matrix[thisMovea][thisMoveb].getRect().top) <= 6))){
                advHand[thisMovei].setRect(matrix[thisMovea][thisMoveb].getRect());
                animationDone = true;
                animating=false;
            }
        }
    }

    @Override
    public void update() {
        //Se è in corso un'animazione, non fare nulla
        while(animating){return;}

        //Inizio del turno dell'avversario
        int thisMoveTurned;
        int thisSpotBest;

        if(!playerTurn)
        {
            maxTurnedCards=0;
            maxFreeSpots=0;
            //Per ogni carta in mano non giocata, per ogni casella libera sulla tavola (owner = 0)...
            for(int i=0; i<5; i++)
                if(!(advHand[i].getCard().isPlayed()))
                    for(int a=0; a<3; a++)
                        for(int b=0; b<3; b++)
                            if(matrix[a][b].getOwner()==0){
                                //...Valuto quante carte riesco a girare con questa mossa...
                                thisMoveTurned=calcTurnedCards(a, b, i);
                                //...Se sono di più di tutte le mosse valutate in precedenza, questa è la mossa migliore
                                if(thisMoveTurned>maxTurnedCards){
                                    maxTurnedCards=thisMoveTurned;
                                    maxFreeSpots=calcBestFreeSum(a, b, i);
                                    thisMovei=i;
                                    thisMovea=a;
                                    thisMoveb=b;
                                }
                                //...Se sono uguali valuto se questa mossa mi lascia con i valori più alti possibili sui lati adiacenti a caselle vuote
                                else if(thisMoveTurned==maxTurnedCards && moves!=0){
                                    thisSpotBest=calcBestFreeSum(a, b, i);
                                    if(thisSpotBest>=maxFreeSpots){
                                        maxFreeSpots=thisSpotBest;
                                        thisMovei=i;
                                        thisMovea=a;
                                        thisMoveb=b;
                                    }
                                }
                                //Come il precedente, ma si applica alla prima mossa, e tende a non far
                                //posizionare la carta al centro dalla AI (Mossa conveniente in ogni caso)
                                else if(thisMoveTurned==maxTurnedCards && moves == 0){
                                    thisSpotBest=calcFirstTurnPos(a, b, i);
                                    if(thisSpotBest>=maxFreeSpots){
                                        maxFreeSpots=thisSpotBest;
                                        thisMovei=i;
                                        thisMovea=a;
                                        thisMoveb=b;
                                    }
                                }
                            }

            //Calcolo quanto spostamento và effettuato per portare la carta dell'avversario sulla casella destinazione
            moveOfX = Math.abs(advHand[thisMovei].getRect().left - matrix[thisMovea][thisMoveb].getRect().left);
            moveOfY = Math.abs(advHand[thisMovei].getRect().top - matrix[thisMovea][thisMoveb].getRect().top);
            //Inizio ad animare, non entrerà più nell'update finchè il metodo draw non ha terminato l'animazione
            animating = true;

            //Se l'animazione è stata terminata...
            if(animationDone) {
                //Setta la carta nel GameSpace corrispondente alla cella della tavola
                matrix[thisMovea][thisMoveb].setCard(advHand[thisMovei].getCard());
                matrix[thisMovea][thisMoveb].setOwner(2);
                matrix[thisMovea][thisMoveb].setImage(advHand[thisMovei].getImage());
                matrix[thisMovea][thisMoveb].setImageName(advHand[thisMovei].getImageName());
                //Elimino Rect ed immagine del GameSpace in mano (non più esistente, la carta non è più in mano)
                advHand[thisMovei].setRect(noRect);
                advHand[thisMovei].setImage(null);
                //Setto la carta come giocata
                advHand[thisMovei].getCard().setPlayed();
                //Giro le carte adiacenti (se la carta avversaria vince
                turnCards(thisMovea, thisMoveb);
                moves++;
                //Se sono 9 mosse, la partta è finita
                if (moves == 9)
                    gameOver = true;

                //Rimetto a false in attesa della prossima mossa dell'avversario (e quindi la prossima animazione)
                animationDone = false;
                animating = false;

                //Passo il turno all'avversario
                playerTurn = true;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Se è il turno dell'avversario, il giocatore non ha controllo sul tocco
        if(!playerTurn)
            return true;
        switch (event.getAction()) {
            //Se il giocatore tocca...
            case MotionEvent.ACTION_DOWN:
                //Se la partita è finita, torna al menù
                if(gameOver){
                    Sound.startSound(2);
                    Sound.media.release();
                    SceneManager manager = new SceneManager();
                    manager.terminate();
                }

                //...Permette spostamento della carta in mano solo se non è stata già giocata
                if (hand[4].getRect().contains((int) event.getX(), (int) event.getY()) && !hand[4].getCard().isPlayed()) {
                    //La carta è in movimento, setto lock a true
                    lock5 = true;
                    //Se la carta non è lasciata nel campo da gioco...
                    if(!tableRect.contains((int)event.getX(),(int)event.getY())){
                        //torna alle coordinate originarie
                        nativeX=hand[4].getRect().centerX();
                        nativeY=hand[4].getRect().centerY();
                    }
                }
                else if (hand[3].getRect().contains((int) event.getX(), (int) event.getY()) && !hand[3].getCard().isPlayed()){
                    lock4 = true;
                    if(!tableRect.contains((int)event.getX(),(int)event.getY())){
                        nativeX=hand[3].getRect().centerX();
                        nativeY=hand[3].getRect().centerY();
                    }
                }
                else if (hand[2].getRect().contains((int) event.getX(), (int) event.getY()) && !hand[2].getCard().isPlayed()){
                    lock3 = true;
                    if(!tableRect.contains((int)event.getX(),(int)event.getY())){
                        nativeX=hand[2].getRect().centerX();
                        nativeY=hand[2].getRect().centerY();
                    }
                }
                else if (hand[1].getRect().contains((int) event.getX(), (int) event.getY()) && !hand[1].getCard().isPlayed()){
                    lock2 = true;
                    if(!tableRect.contains((int)event.getX(),(int)event.getY())){
                        nativeX=hand[1].getRect().centerX();
                        nativeY=hand[1].getRect().centerY();
                    }
                }
                else if (hand[0].getRect().contains((int) event.getX(), (int) event.getY()) && !hand[0].getCard().isPlayed()){
                    lock1 = true;
                    if(!tableRect.contains((int)event.getX(),(int)event.getY())){
                        nativeX=hand[0].getRect().centerX();
                        nativeY=hand[0].getRect().centerY();
                    }
                }
            case MotionEvent.ACTION_MOVE:
                //Sposto al carta, il centro della carta è sotto il dito del giocatore
                if (hand[4].getRect().contains((int) event.getX(), (int) event.getY()) && lock5  && !hand[4].getCard().isPlayed()) {
                    hand[4].getRect().set((int) event.getX() - hand[4].getRect().width() / 2, (int) event.getY() - hand[4].getRect().height() / 2, (int) event.getX() + hand[4].getRect().width() / 2, (int) event.getY() + hand[4].getRect().height() / 2);
                }
                if (hand[3].getRect().contains((int) event.getX(), (int) event.getY()) && lock4 && !hand[3].getCard().isPlayed()) {
                    hand[3].getRect().set((int) event.getX() - hand[3].getRect().width() / 2, (int) event.getY() - hand[3].getRect().height() / 2, (int) event.getX() + hand[3].getRect().width() / 2, (int) event.getY() + hand[3].getRect().height() / 2);
                }
                if (hand[2].getRect().contains((int) event.getX(), (int) event.getY()) && lock3 && !hand[2].getCard().isPlayed()) {
                    hand[2].getRect().set((int) event.getX() - hand[2].getRect().width() / 2, (int) event.getY() - hand[2].getRect().height() / 2, (int) event.getX() + hand[2].getRect().width() / 2, (int) event.getY() + hand[2].getRect().height() / 2);
                }
                if (hand[1].getRect().contains((int) event.getX(), (int) event.getY()) && lock2 && !hand[1].getCard().isPlayed()) {
                    hand[1].getRect().set((int) event.getX() - hand[1].getRect().width() / 2, (int) event.getY() - hand[1].getRect().height() / 2, (int) event.getX() + hand[1].getRect().width() / 2, (int) event.getY() + hand[1].getRect().height() / 2);
                }
                if (hand[0].getRect().contains((int) event.getX(), (int) event.getY()) && lock1 && !hand[0].getCard().isPlayed()) {
                    hand[0].getRect().set((int) event.getX() - hand[0].getRect().width() / 2, (int) event.getY() - hand[0].getRect().height() / 2, (int) event.getX() + hand[0].getRect().width() / 2, (int) event.getY() + hand[0].getRect().height() / 2);
                }
                break;
            case MotionEvent.ACTION_UP:
                //Se sto muovendo una carta...
                if (lock1 || lock2 || lock3 || lock4 || lock5) {
                    //Se lascio all'interno della tavola...
                    if (tableRect.contains((int) event.getX(), (int) event.getY())){
                        if(lock1){
                            //Controllo se il movimento termina su uno dei 9 spazi sulla tavola
                            for(int i=0; i<3; i++){
                                for(int j=0; j<3; j++){
                                    if(matrix[i][j].getRect().contains((int)event.getX(), (int)event.getY())){
                                        //Se la casella della tavola non contiene già una carta...
                                        if(matrix[i][j].getOwner()==0){
                                            //Setto la carta nel GameSpace della casella sulla tavola
                                            matrix[i][j].setCard(db.getCard(hand[0].getCard().getId()));
                                            matrix[i][j].setOwner(1);
                                            matrix[i][j].setImage(hand[0].getImage());
                                            matrix[i][j].setImageName(hand[0].getImageName());
                                            //Elimino Rect e immagine del GameSpace in mano (la carta non è più presente in mano)
                                            hand[0].setImage(null);
                                            hand[0].setRect(noRect);
                                            //Setto la carta a giocata
                                            hand[0].getCard().setPlayed();
                                            //Giro le carte adiacenti (se vince la carta del giocatore)
                                            this.turnCards(i, j);
                                            moves++;
                                            //Se sono 9 mosse, la partita finisce
                                            if(moves==9)
                                                gameOver=true;
                                            //Se la partita non è finita, il thread attende 1500 milliecondi per simulare il tempo di
                                            //reazione dell'AI alla mossa del giocatore
                                            if(!gameOver){
                                                try {
                                                    MainThread.sleep(waiting);
                                                }catch(Exception e){e.printStackTrace();}
                                                //Termina turno del giocatore
                                                playerTurn=false;
                                            }

                                        }
                                        //Se la casella è già occupata da una carta, torna alla posizione originaria
                                        else{
                                            hand[0].setRect(new Rect(nativeX - hand[0].getRect().width() / 2, nativeY - hand[0].getRect().height() / 2, nativeX + hand[0].getRect().width() / 2, nativeY + hand[0].getRect().height() / 2));
                                        }
                                    }
                                }
                            }
                        }
                        else if(lock2){
                            for(int i=0; i<3; i++){
                                for(int j=0; j<3; j++){
                                    if(matrix[i][j].getRect().contains((int)event.getX(), (int)event.getY())){
                                        if(matrix[i][j].getOwner()==0){
                                            matrix[i][j].setCard(db.getCard(hand[1].getCard().getId()));
                                            matrix[i][j].setOwner(1);
                                            matrix[i][j].setImage(hand[1].getImage());
                                            matrix[i][j].setImageName(hand[1].getImageName());
                                            hand[1].setImage(null);
                                            hand[1].setRect(noRect);
                                            hand[1].getCard().setPlayed();
                                            this.turnCards(i, j);
                                            moves++;
                                            if(moves==9)
                                                gameOver=true;
                                            if(!gameOver){
                                                try {
                                                    MainThread.sleep(waiting);
                                                }catch(Exception e){e.printStackTrace();}
                                                playerTurn=false;
                                            }
                                        } else {
                                            hand[1].setRect(new Rect(nativeX - hand[1].getRect().width() / 2, nativeY - hand[1].getRect().height() / 2, nativeX + hand[1].getRect().width() / 2, nativeY + hand[1].getRect().height() / 2));
                                        }
                                    }
                                }
                            }
                        }
                        else if(lock3){
                            for(int i=0; i<3; i++){
                                for(int j=0; j<3; j++){
                                    if(matrix[i][j].getRect().contains((int)event.getX(), (int)event.getY())){
                                        if(matrix[i][j].getOwner()==0)
                                        {
                                            matrix[i][j].setCard(db.getCard(hand[2].getCard().getId()));
                                            matrix[i][j].setOwner(1);
                                            matrix[i][j].setImage(hand[2].getImage());
                                            matrix[i][j].setImageName(hand[2].getImageName());
                                            hand[2].setImage(null);
                                            hand[2].setRect(noRect);
                                            hand[2].getCard().setPlayed();
                                            this.turnCards(i, j);
                                            moves++;
                                            if(moves==9)
                                                gameOver=true;
                                            if(!gameOver){
                                                try {
                                                    MainThread.sleep(waiting);
                                                }catch(Exception e){e.printStackTrace();}
                                                playerTurn=false;
                                            }
                                        } else {
                                            hand[2].setRect(new Rect(nativeX - hand[2].getRect().width() / 2, nativeY - hand[0].getRect().height() / 2, nativeX + hand[2].getRect().width() / 2, nativeY + hand[2].getRect().height() / 2));
                                        }
                                    }
                                }
                            }
                        }
                        else if(lock4){
                            for(int i=0; i<3; i++){
                                for(int j=0; j<3; j++){
                                    if(matrix[i][j].getRect().contains((int)event.getX(), (int)event.getY())){
                                        if(matrix[i][j].getOwner()==0){
                                            matrix[i][j].setCard(db.getCard(hand[3].getCard().getId()));
                                            matrix[i][j].setOwner(1);
                                            matrix[i][j].setImage(hand[3].getImage());
                                            matrix[i][j].setImageName(hand[3].getImageName());
                                            hand[3].setImage(null);
                                            hand[3].setRect(noRect);
                                            hand[3].getCard().setPlayed();
                                            this.turnCards(i, j);
                                            moves++;
                                            if(moves==9)
                                                gameOver=true;
                                            if(!gameOver){
                                                try {
                                                    MainThread.sleep(waiting);
                                                }catch(Exception e){e.printStackTrace();}
                                                playerTurn=false;
                                            }
                                        } else {
                                            hand[3].setRect(new Rect(nativeX - hand[3].getRect().width() / 2, nativeY - hand[3].getRect().height() / 2, nativeX + hand[3].getRect().width() / 2, nativeY + hand[3].getRect().height() / 2));

                                        }
                                    }
                                }
                            }
                        }
                        else if(lock5){
                            for(int i=0; i<3; i++){
                                for(int j=0; j<3; j++){
                                    if(matrix[i][j].getRect().contains((int)event.getX(), (int)event.getY())){
                                        if(matrix[i][j].getOwner()==0){
                                            matrix[i][j].setCard(db.getCard(hand[4].getCard().getId()));
                                            matrix[i][j].setOwner(1);
                                            matrix[i][j].setImage(hand[4].getImage());
                                            matrix[i][j].setImageName(hand[4].getImageName());
                                            hand[4].setRect(noRect);
                                            hand[4].setImage(null);
                                            hand[4].getCard().setPlayed();
                                            this.turnCards(i, j);
                                            moves++;
                                            if(moves==9)
                                                gameOver=true;
                                            if(!gameOver){
                                                try {
                                                    MainThread.sleep(waiting);
                                                }catch(Exception e){e.printStackTrace();}
                                                playerTurn=false;
                                            }
                                        } else {
                                            hand[4].setRect(new Rect(nativeX - hand[4].getRect().width() / 2, nativeY - hand[4].getRect().height() / 2, nativeX + hand[4].getRect().width() / 2, nativeY + hand[4].getRect().height() / 2));
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        //Destinazione non valida, riporta la carta alla posizione originaria
                        if (lock1)
                            hand[0].getRect().set(nativeX - hand[0].getRect().width() / 2, nativeY - hand[0].getRect().height() / 2, nativeX + hand[0].getRect().width() / 2, nativeY + hand[0].getRect().height() / 2);
                        else if (lock2)
                            hand[1].getRect().set(nativeX - hand[1].getRect().width() / 2, nativeY - hand[1].getRect().height() / 2, nativeX + hand[1].getRect().width() / 2, nativeY + hand[1].getRect().height() / 2);
                        else if (lock3)
                            hand[2].getRect().set(nativeX - hand[2].getRect().width() / 2, nativeY - hand[2].getRect().height() / 2, nativeX + hand[2].getRect().width() / 2, nativeY + hand[2].getRect().height() / 2);
                        else if (lock4)
                            hand[3].getRect().set(nativeX - hand[3].getRect().width() / 2, nativeY - hand[3].getRect().height() / 2, nativeX + hand[3].getRect().width() / 2, nativeY + hand[3].getRect().height() / 2);
                        else if (lock5)
                            hand[4].getRect().set(nativeX - hand[4].getRect().width() / 2, nativeY - hand[4].getRect().height() / 2, nativeX + hand[4].getRect().width() / 2, nativeY + hand[4].getRect().height() / 2);
                    }
                }
                //Ho finito di spostare la carta, setto il lock a false
                lock1 = false;
                lock2 = false;
                lock3 = false;
                lock4 = false;
                lock5 = false;
        }
        return true;
    }

    //Metodo che valuta se girare le carte avversarie
    private void turnCards(int i, int j){
        //Se la carta non è sul bordo sinistro...
        if((j-1)>=0)
            //... se lo spazio a sinistra non è vuoto...
            if ((matrix[i][j-1].getOwner()) != 0)
                //...se la carta a sinistra non è già dello stesso giocatore...
                if ((matrix[i][j-1].getOwner()) != (matrix[i][j].getOwner()))
                    //...se il valore sinistro della carta posizionata è maggiore del valore destro della carta nello spazio a sinistra...
                    if (matrix[i][j-1].getCard().getrValue() < matrix[i][j].getCard().getlValue()) {
                        //CARTA POSIZIONATA VINCE: cambia owner della carta a sinistra
                        matrix[i][j-1].setOwner(matrix[i][j].getOwner());
                        //Aggiorna il punteggio in base a quale giocatore ha mangiato
                        if(matrix[i][j].getOwner()==1){
                            bluePoints++;
                            redPoints--;
                        }
                        else if(matrix[i][j].getOwner()==2){
                            bluePoints--;
                            redPoints++;
                        }
                        //Cambia il colore del Bitmap della carta (da rosso a blu e viceversa)
                        invertCard(i, j-1);
                        //Effetto sonoro della carta che "flippa"
                        Sound.startSound(5);
                    }
        if((i-1)>=0)
            if((matrix[i-1][j].getOwner()) != 0)
                if((matrix[i-1][j].getOwner()) != (matrix[i][j].getOwner()))
                    if(matrix[i-1][j].getCard().getbValue()<matrix[i][j].getCard().gettValue()){
                        matrix[i-1][j].setOwner(matrix[i][j].getOwner());
                        if(matrix[i][j].getOwner()==1){
                            bluePoints++;
                            redPoints--;
                        }
                        else if(matrix[i][j].getOwner()==2){
                            bluePoints--;
                            redPoints++;
                        }
                        invertCard(i-1, j);
                        Sound.startSound(5);
                    }
        if((j+1)<=2)
            if ((matrix[i][j+1].getOwner()) != 0)
                if ((matrix[i][j+1].getOwner()) != (matrix[i][j].getOwner()))
                    if (matrix[i][j+1].getCard().getlValue() < matrix[i][j].getCard().getrValue()) {
                        matrix[i][j+1].setOwner(matrix[i][j].getOwner());
                        if(matrix[i][j].getOwner()==1){
                            bluePoints++;
                            redPoints--;
                        }
                        else if(matrix[i][j].getOwner()==2){
                            bluePoints--;
                            redPoints++;
                        }
                        invertCard(i, j + 1);
                        Sound.startSound(5);
                    }
        if((i+1)<=2)
            if((matrix[i+1][j].getOwner()) != 0)
                if((matrix[i+1][j].getOwner()) != (matrix[i][j].getOwner()))
                    if(matrix[i+1][j].getCard().gettValue()<matrix[i][j].getCard().getbValue()){
                        matrix[i+1][j].setOwner(matrix[i][j].getOwner());
                        if(matrix[i][j].getOwner()==1){
                            bluePoints++;
                            redPoints--;
                        }
                        else if(matrix[i][j].getOwner()==2){
                            bluePoints--;
                            redPoints++;
                        }
                        invertCard(i+1, j);
                        Sound.startSound(5);
                    }
    }

    //Metodo che calcola quante carte adiacenti verrebbero girate dalla mossa corrente
    private int calcTurnedCards(int i, int j, int adv){
        int turned = 0;

        //Se la carta non è sul bordo sinistro...
        if((j-1)>=0)
            //...se lo spazio a sinistra non è vuoto...
            if ((matrix[i][j-1].getOwner()) != 0)
                //...se la carta a sinistra non è già del giocatore che sta compiendo la mossa...
                if ((matrix[i][j-1].getOwner()) != (advHand[adv].getOwner()))
                    //...se il valore destro della carta a sinistra è minore del valore sinistro della carta posizionata...
                    if (matrix[i][j-1].getCard().getrValue() < advHand[adv].getCard().getlValue()) {
                        //La carta verrebbe mangiata, aggiungi 1 al contatore
                        turned++;
                    }
        if((i-1)>=0)
            if((matrix[i-1][j].getOwner()) != 0)
                if((matrix[i-1][j].getOwner()) != (advHand[adv].getOwner()))
                    if(matrix[i-1][j].getCard().getbValue()<advHand[adv].getCard().gettValue()){
                        turned++;
                    }
        if((j+1)<=2)
            if ((matrix[i][j+1].getOwner()) != 0)
                if ((matrix[i][j+1].getOwner()) != (advHand[adv].getOwner()))
                    if (matrix[i][j+1].getCard().getlValue() < advHand[adv].getCard().getrValue()) {
                        turned++;
                    }
        if((i+1)<=2)
            if((matrix[i+1][j].getOwner()) != 0)
                if((matrix[i+1][j].getOwner()) != (advHand[adv].getOwner()))
                    if(matrix[i+1][j].getCard().gettValue()<advHand[adv].getCard().getbValue()){
                        turned++;
                    }
                    return turned;
    }

    //Metodo per calcolare che i valori sui lati lsciati liberi siano i più alti possibili
    private int calcBestFreeSum(int i, int j, int adv){
        int freeSum = 0;
        //Se la carta non è sul bordo sinistro...
        if(j-1>=0)
            //Se lo spazio a sinistra non è vuoto...
            if(matrix[i][j-1].getOwner()==0)
                //Aggiungi il valore sinistro della carta alla somma
                freeSum+=advHand[adv].getCard().getlValue();
        if((i-1)>=0)
            if(matrix[i-1][j].getOwner()==0)
                freeSum+=advHand[adv].getCard().gettValue();
        if((j+1)<=2)
            if(matrix[i][j+1].getOwner()==0)
                freeSum+=advHand[adv].getCard().getrValue();
        if((i+1)<=2)
            if(matrix[i+1][j].getOwner()==0)
                freeSum+=advHand[adv].getCard().getbValue();
        return freeSum;
    }

    //Metodo equivalente al precedente, ma considera i bordi come 10 (tende a posizionare la carta
    //sui bordi e non al centro), metodo utilizzato nel primo turno
    private int calcFirstTurnPos(int i, int j, int adv){
        int freeSum = 0;
        if(j-1>=0){
            if(matrix[i][j-1].getOwner()==0)
                freeSum+=advHand[adv].getCard().getlValue();}
        else freeSum+=10;
        if((i-1)>=0){
            if(matrix[i-1][j].getOwner()==0)
                freeSum+=advHand[adv].getCard().gettValue();}
        else freeSum+=10;
        if((j+1)<=2){
            if(matrix[i][j+1].getOwner()==0)
                freeSum+=advHand[adv].getCard().getrValue();}
        else freeSum+=10;
        if((i+1)<=2){
            if(matrix[i+1][j].getOwner()==0)
                freeSum+=advHand[adv].getCard().getbValue();}
        else freeSum+=10;
        return freeSum;
    }

    //Metodo per cambiare Bitmap della carta
    private void invertCard(int i, int j) {
        //Prende nome dell'immagine attualmente impostata, se è blu sostituisce con versione rossa e viceversa
        String name = matrix[i][j].getImageName();
        switch(name){
            case "card1":
                matrix[i][j].setImage(card1r);
                matrix[i][j].setImageName("card1r");
                break;
            case "card2":
                matrix[i][j].setImage(card2r);
                matrix[i][j].setImageName("card2r");
                break;
            case "card3":
                matrix[i][j].setImage(card3r);
                matrix[i][j].setImageName("card3r");
                break;
            case "card4":
                matrix[i][j].setImage(card4r);
                matrix[i][j].setImageName("card4r");
                break;
            case "card5":
                matrix[i][j].setImage(card5r);
                matrix[i][j].setImageName("card5r");
                break;
            case "advCard1":
                matrix[i][j].setImage(advCard1b);
                matrix[i][j].setImageName("advCard1b");
                break;
            case "advCard2":
                matrix[i][j].setImage(advCard2b);
                matrix[i][j].setImageName("advCard2b");
                break;
            case "advCard3":
                matrix[i][j].setImage(advCard3b);
                matrix[i][j].setImageName("advCard3b");
                break;
            case "advCard4":
                matrix[i][j].setImage(advCard4b);
                matrix[i][j].setImageName("advCard4b");
                break;
            case "advCard5":
                matrix[i][j].setImage(advCard5b);
                matrix[i][j].setImageName("advCard5b");
                break;
            case "card1r":
                matrix[i][j].setImage(card1);
                matrix[i][j].setImageName("card1");
                break;
            case "card2r":
                matrix[i][j].setImage(card2);
                matrix[i][j].setImageName("card2");
                break;
            case "card3r":
                matrix[i][j].setImage(card3);
                matrix[i][j].setImageName("card3");
                break;
            case "card4r":
                matrix[i][j].setImage(card4);
                matrix[i][j].setImageName("card4");
                break;
            case "card5r":
                matrix[i][j].setImage(card5);
                matrix[i][j].setImageName("card5");
                break;
            case "advCard1b":
                matrix[i][j].setImage(advCard1);
                matrix[i][j].setImageName("advCard1");
                break;
            case "advCard2b":
                matrix[i][j].setImage(advCard2);
                matrix[i][j].setImageName("advCard2");
                break;
            case "advCard3b":
                matrix[i][j].setImage(advCard3);
                matrix[i][j].setImageName("advCard3");
                break;
            case "advCard4b":
                matrix[i][j].setImage(advCard4);
                matrix[i][j].setImageName("advCard4");
                break;
            case "advCard5b":
                matrix[i][j].setImage(advCard5);
                matrix[i][j].setImageName("advCard5");
                break;
            default:
                matrix[i][j].setImage(null);
                matrix[i][j].setImageName("null");
        }
    }
}