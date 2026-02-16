import javax.swing.*;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


public class GamePanel extends JPanel  {
    boolean spawnCircle = true;
    ArrayList<Circle> bodies = new ArrayList<>();
    ArrayList<Rectangle> rectangles = new ArrayList<>();
    public void SpawnRectangles(int n, float x, float y, float vx, float vy, float width, float  height , float mass, float angle){
        Random rand  = new Random();
        for(int i = 0; i< n; i++){
            float value1 = rand.nextFloat() * x;
            float value2 = rand.nextFloat() * y;
            float value3 = rand.nextFloat() * angle;
            rectangles.add(new Rectangle(value1, value2, vx, vy, width,   height , mass, value3));
        }
    }
    public void SpawnBodies(int n , float x , float y , float vx , float vy, float mass){
        Random rand = new Random();

        for(int i = 0; i<n;i++){
            float value1 = rand.nextFloat() * x;
            float value2 = rand.nextFloat() * y;
            float value3  = rand.nextFloat() *40 + 20;
            boolean overlap = false;
            for(int k  = 0; k< bodies.size(); k ++ ){
                
                    float dx = value1 - bodies.get(k).x;
                    float dy = value2 - bodies.get(k).y;
                    float distance  = dx*dx + dy*dy;
                    float mindist = value3 + bodies.get(k).radius;
                    if(distance<(mindist*mindist)){
                         overlap  = true ;
                        
                    }
                }
            if(!overlap) bodies.add(new Circle(value1, value2, vx, vy, mass,value3));
            
            
    

        }
    }
    static float[] checkCollision(Rectangle a, Circle b) {
    // quick distance pre-check
    float quickDx = b.x - a.x;
    float quickDy = b.y - a.y;
    float quickDist = quickDx*quickDx + quickDy*quickDy;
    float maxRange = (float)Math.sqrt(a.width*a.width + a.height*a.height) / 2 + b.radius;
    if(quickDist > maxRange * maxRange) return new float[]{0, 0, 0};

    // transform circle into rectangle's local space
    float dx = b.x - a.x;
    float dy = b.y - a.y;
    float[] axes = a.getAxes();
    float localX = dx * axes[0] + dy * axes[1];
    float localY = dx * axes[2] + dy * axes[3];

    // clamp to rectangle bounds
    float hw = a.width / 2;
    float hh = a.height / 2;
    float closestX = Math.max(-hw, Math.min(localX, hw));
    float closestY = Math.max(-hh, Math.min(localY, hh));

    // distance from circle center to closest point
    float cx = localX - closestX;
    float cy = localY - closestY;
    float distSq = cx*cx + cy*cy;
    float distance = (float)Math.sqrt(distSq);

    if(distance == 0) return new float[]{0, 0, 0};

    // check collision
    float overlap = b.radius - distance;
    if(overlap <= 0) return new float[]{0, 0, 0};

    // normal = direction from rectangle center to circle center
    float len = (float)Math.sqrt(quickDist);
    if(len == 0) return new float[]{0, 0, 0};

    return new float[]{quickDx / len, quickDy / len, overlap};
    }
    public GamePanel(){
        
        setPreferredSize(new Dimension(800,600));
        setBackground(Color.BLACK);
        addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_C) spawnCircle = true;
                    if(e.getKeyCode() == KeyEvent.VK_R) spawnCircle = false;
                }
        });
        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(spawnCircle) {
                    bodies.add(new Circle(e.getX(), e.getY(), 0.0f, 0.0f, 100.0f, 20.0f));
                } else {
                    rectangles.add(new Rectangle(e.getX(), e.getY(), 0.0f, 0.0f, 60.0f, 30.0f, 100.0f, 0.0f));
                }
            }
        });
        Timer timer  = new Timer(16,e->{
            for(Circle  body: bodies){
                float maxSpeed = 300f;
                body.vx = Math.min(Math.abs(body.vx), maxSpeed) * Math.signum(body.vx);
                body.vy = Math.min(Math.abs(body.vy), maxSpeed) * Math.signum(body.vy);
            }
            for(Circle body:bodies){
                body.update(0.016f);
            if(body.x > 800 - 2 * body.radius){
                body.x  =  800 - 2 * body.radius;
                body.vx = -body.vx;
            }else if(body.x<0){
                body.x = body.radius;
                body.vx = -body.vx;
            }else if(body.y<0){
                body.y = body.radius;
                body.vy = -body.vy ;
            }else if(body.y>600  - 2*body.radius ){
                body.y = 600-2*body.radius;
                body.vy = -body.vy;
            }
            }
            for(int i  = 0; i< bodies.size(); i ++ ){
                for(int j = i+1 ;j<bodies.size();j++){
                    float dx = bodies.get(i).x - bodies.get(j).x;
                    float dy = bodies.get(i).y - bodies.get(j).y;
                    float distance  = dx*dx + dy*dy;
                    float mindist = bodies.get(i).radius + bodies.get(j).radius;
                    float dist = (float)Math.sqrt(distance);
                    if(distance < mindist * mindist){

                        float nx = dx / dist;
                        float ny = dy / dist;
                        
                        // positional correction
                        float overlap = mindist - dist;
                        bodies.get(i).x += overlap * nx * 0.5f;
                        bodies.get(i).y += overlap * ny * 0.5f;
                        bodies.get(j).x -= overlap * nx * 0.5f;
                        bodies.get(j).y -= overlap * ny * 0.5f;
                        
                        // relative velocity along normal
                        float dvx = bodies.get(i).vx - bodies.get(j).vx;
                        float dvy = bodies.get(i).vy - bodies.get(j).vy;
                        float relVel = dvx*nx + dvy*ny;
                        
                        // only resolve if approaching
                        if(relVel > 0){
                            float impulse = relVel * 0.8f;
                            bodies.get(i).vx -= impulse * nx;
                            bodies.get(i).vy -= impulse * ny;
                            bodies.get(j).vx += impulse * nx;
                            bodies.get(j).vy += impulse * ny;
                        }
                        
                    }

                }
            }
            for(Rectangle rect : rectangles) {
                rect.update(0.016f);
            }
            for(Rectangle rect : rectangles) {
                if(rect.x > 800 - rect.width/2) {
                    rect.x = 800 - rect.width/2;
                    rect.vx = -rect.vx;
                } else if(rect.x < rect.width/2) {
                    rect.x = rect.width/2;
                    rect.vx = -rect.vx;
                }
                if(rect.y > 600 - rect.height/2) {
                    rect.y = 600 - rect.height/2;
                    rect.vy = -rect.vy;
                } else if(rect.y < rect.height/2) {
                    rect.y = rect.height/2;
                    rect.vy = -rect.vy;
                }
            }
            for(int i = 0; i < rectangles.size();i++){
                for(int j = i+1; j<rectangles.size(); j++){
                    if(Rectangle.checkSAT(rectangles.get(i), rectangles.get(j))) {
                        // push apart along the vector between centers
                        float pdx = rectangles.get(j).x - rectangles.get(i).x;
                        float pdy = rectangles.get(j).y - rectangles.get(i).y;
                        float len = (float)Math.sqrt(pdx*pdx + pdy*pdy);
                        pdx /= len; pdy /= len;
                        
                        rectangles.get(i).x -= pdx * 2.0f;
                        rectangles.get(i).y -= pdy * 2.0f;
                        rectangles.get(j).x += pdx * 2.0f;
                        rectangles.get(j).y += pdy * 2.0f;
                        
                        // velocity response
                        rectangles.get(i).vx = -rectangles.get(i).vx;
                        rectangles.get(i).vy = -rectangles.get(i).vy;
                        rectangles.get(j).vx = -rectangles.get(j).vx;
                        rectangles.get(j).vy = -rectangles.get(j).vy;
                    }
                }
            }
            for(int i = 0; i<rectangles.size();i++){   
                for(int j = 0;j<bodies.size();j++){
                    float[] result = checkCollision(rectangles.get(i), bodies.get(j));
                    if(result[2] > 0) {
                        System.out.println("overlap=" + result[2]); // ← add here
                        // ... rest of collision code
                    }
                    if(result[2] > 0) {
                        // positional correction
                        bodies.get(j).x += result[2] * result[0] * 1.5f;
                        bodies.get(j).y += result[2] * result[1] * 1.5f;
                        rectangles.get(i).x -= result[2] * result[0] * 0.5f;
                        rectangles.get(i).y -= result[2] * result[1] * 0.5f;
                        
                        // velocity response
                        float dvx = bodies.get(j).vx - rectangles.get(i).vx;
                        float dvy = bodies.get(j).vy - rectangles.get(i).vy;
                        float relVel = dvx * result[0] + dvy * result[1];
                        if(relVel < 0) {
                            float impulse = relVel * 0.8f;
                            bodies.get(j).vx -= impulse * result[0];
                            bodies.get(j).vy -= impulse * result[1];
                            rectangles.get(i).vx += impulse * result[0];
                            rectangles.get(i).vy += impulse * result[1];
                        }
                    }
                }
          
            }
            for(Rectangle rect : rectangles) {
                rect.vy += 9.8f;
            }
            for(Circle body: bodies){
                body.vy += 9.8f;
            }

            
            

            repaint();
        
       });
        timer.start();
        
    }
    @Override 
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        for(Circle  body:bodies){
            g.fillOval((int)body.x,(int)body.y, (int)(2*body.radius) , (int)(2*body.radius));
        }
        for(Rectangle rect : rectangles) {
            float[][] corners = rect.getCoordinates();
            int[] xs = new int[4];
            int[] ys = new int[4];
            for(int i = 0; i < 4; i++) {
                xs[i] = (int)corners[i][0];
                ys[i] = (int)corners[i][1];
            }
            g.setColor(Color.RED);
            g.drawPolygon(xs, ys, 4);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Mode: " + (spawnCircle ? "Circle (C)" : "Rectangle (R)"), 10, 20);
        
    }
    
}
