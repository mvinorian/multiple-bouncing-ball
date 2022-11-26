package com.multiplebouncingball;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BallPanel extends JPanel {
    
    private static final int REFRESH_RATE = 120;

    private List<Ball> balls;
    private BallArea box;
    private int areaWidth;
    private int areaHeight;

    public BallPanel(int width, int height) {
        this.areaWidth = width;
        this.areaHeight = height;
        this.setPreferredSize(new Dimension(this.areaWidth, this.areaHeight));
        
        Random rand = new Random();
        this.balls = new ArrayList<Ball>();

        for (int i = 0; i < 5; i++) {
            boolean isCollide = true;
            int x = 0, y = 0, radius = 50, speed = 3, angle = 0;
            while (isCollide) {
                x = rand.nextInt(width - 2*radius - 20) + radius + 10;
                y = rand.nextInt(height - 2*radius - 20) + radius + 10;

                angle = rand.nextInt(360);

                Ball ball = new Ball(x, y, radius, speed, angle, new Color(0, 175, 175));
                
                isCollide = false;
                for (Ball b : balls) {
                    if (!ball.equals(b)) isCollide |= ball.isCollide(b);
                }
            }
            this.balls.add(new Ball(x, y, radius, speed, angle, new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255))));
        }
        this.box = new BallArea(0, 0, width, height, new Color(40, 40, 40), Color.WHITE);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);

                Component c = (Component)e.getComponent();
                Dimension dim = c.getSize();
                areaWidth = dim.width;
                areaHeight = dim.height;
                box.set(0, 0, areaWidth, areaHeight);
            }
        });
        startThread();
    }

    public void startThread() {
        Thread gameThread = new Thread() {
            public void run() {
                while (true) {
                    for (Ball ball : balls) {
                        ball.collide(box);
                        for (Ball b : balls) {
                            if (!ball.equals(b)) ball.collide(b);
                        }
                    }
                    repaint();
                    try {
                        Thread.sleep(1000 / REFRESH_RATE);
                    } catch (InterruptedException e) {}
                }
            }
        };
        gameThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        box.draw(g);
        for (Ball ball : balls) ball.draw(g);
    }

}
