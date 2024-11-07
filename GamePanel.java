package com.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;

public class GamePanel extends JPanel implements ActionListener {
    private class Tile {
        int x;
        int y;

        Tile(int x,int y) {
            this.x = x;
            this.y = y;
        }
    }

    //Game Panel Information
    int SCREEN_WIDTH = 500;
    int SCREEN_HEIGHT = 500;
    static final int UNIT_SIZE = 25;    //Size of a single unit in the game
    static final int DELAY = 75;    //For the timer

    //Snake Infos
    Tile head;
    ArrayList<Tile> body;
    int xVelo;  //Initialize direction to the right
    int yVelo;

    //Apple info
    Tile apple;
    int applesEaten;


    //Misc Utils
    boolean running = false;
    Timer timer;
    Random random;
    private LinkedList<Integer> keysPressed = new LinkedList<>();    //read only one button press at a time

    //Initializes game panel
    GamePanel() {
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        random = new Random();
        head = new Tile(5,5);
        body = new ArrayList<Tile>();
        applesEaten = 0;

        startGame();
    }

    GamePanel(int BoardWidth, int BoardHeight) {
        this.SCREEN_WIDTH = BoardWidth;
        this.SCREEN_HEIGHT = BoardHeight;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        random = new Random();
        head = new Tile(5,5);
        body = new ArrayList<Tile>();
        applesEaten = 0;

        startGame();
    }

    //Sets up before game officially starts
    private void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    //Update graphics
    private void draw(Graphics g) {
        //If game running
        if(running) {
            /*
            g.setColor(Color.WHITE);
            for(int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE;i++){    //Create the grid
                g.drawLine(i * UNIT_SIZE,0, i * UNIT_SIZE,SCREEN_HEIGHT);
                g.drawLine(0,i * UNIT_SIZE, SCREEN_WIDTH,i * UNIT_SIZE);
            }
             */

            //Color in apple
            g.setColor(Color.RED);
            g.fillOval(apple.x * UNIT_SIZE,apple.y * UNIT_SIZE,UNIT_SIZE,UNIT_SIZE);

            //Color in head
            g.setColor(Color.GREEN);
            g.fillRect(head.x * UNIT_SIZE,head.y * UNIT_SIZE,UNIT_SIZE,UNIT_SIZE);

            //Color in body
            for (int i = body.size() - 1; i >= 0; i--) {
                    g.setColor(new Color(45,180,0));
                    g.fillRect(body.get(i).x * UNIT_SIZE,body.get(i).y * UNIT_SIZE,UNIT_SIZE,UNIT_SIZE);
                }

            //Color in score
            g.setColor(Color.RED);
            g.setFont(new Font("MV Boli",Font.BOLD,25));
            FontMetrics metrics = getFontMetrics(g.getFont());
            //Display score on the top of the screen
            g.drawString("Score: "+ applesEaten,(SCREEN_WIDTH - metrics.stringWidth("Score "+ applesEaten))/2,g.getFont().getSize());
        }

        else {
            timer.stop();
            gameOver(g);
        }
    }

    //Generates the coordinate of a new apple
    private void newApple() {
        int X = random.nextInt(SCREEN_WIDTH/UNIT_SIZE);
        int Y = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE);
        apple = new Tile(X,Y);

    }

    private void newApple(int x,int y) {
        apple = new Tile(x,y);
    }

    //Moves the snake
    private void move() {
        //If food eaten
        if(collision(apple,head)) {
            body.add(new Tile(apple.x,apple.y));
            newApple();
            applesEaten++;
        }

        //Increment the body
        for (int i = body.size() - 1; i >= 0; i--) {
            if(i == 0) {    //If right before head
                body.get(i).x = head.x;
                body.get(i).y = head.y;
            } else {
                body.get(i).x = body.get(i - 1).x;
                body.get(i).y = body.get(i - 1).y;
            }
        }
        //Increment the head
        head.x += xVelo;
        head.y += yVelo;

        //Check for collision with body
        for (int i = 0; i < body.size(); i++) {
            if(collision(head, body.get(i))) {
                running = false;
                break;
            }
        }

        //Check for out of bounds
        if (head.x * UNIT_SIZE < 0 || head.x * UNIT_SIZE > SCREEN_WIDTH || //passed left border or right border
                head.y * UNIT_SIZE < 0 || head.y * UNIT_SIZE > SCREEN_HEIGHT) { //passed top border or bottom border
            running = false;
        }

        }

    //Check if two objects occupies the same tile
    private boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    //Processes gameover scenario
    private void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("MV Boli",Font.BOLD,25));
        FontMetrics metrics = getFontMetrics(g.getFont());  //Useful for lining up text to center of screen
        //This setup lets us print to the center of the screen
        g.drawString("Game Over",(SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2,SCREEN_HEIGHT/2);
        FontMetrics metric1 = getFontMetrics(g.getFont());
        //Display score on the top of the screen
        g.drawString("Score "+ applesEaten,(SCREEN_WIDTH - metric1.stringWidth("Score "+ applesEaten))/2,g.getFont().getSize());

    }

    //This method is called whenever DELAY amount of time passes
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            //If a valid key is pressed, change direction
            if(!keysPressed.isEmpty()) {
                //ONly first key pressed is considered
                int key = keysPressed.pop();
                keysPressed.clear();
                switch(key) {
                    case KeyEvent.VK_LEFT:
                        if(xVelo != 1) {  //Prevents going 180 degree backwards
                            xVelo = -1;
                            yVelo = 0;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if(xVelo != -1) {  //Prevents going 180 degree backwards
                            xVelo = 1;
                            yVelo = 0;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if(yVelo != -1) {  //Prevents going 180 degree backwards
                            yVelo = 1;
                            xVelo = 0;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if(yVelo != 1) {  //Prevents going 180 degree backwards
                            yVelo = -1;
                            xVelo = 0;
                        }
                        break;
                }
            }

            move();
        }
        //Update the graphics
        repaint();
    }

    //Recognizes keyboard presses
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            keysPressed.add(e.getKeyCode());
        }
    }
}
