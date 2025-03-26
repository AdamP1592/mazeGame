package gamepkg;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Arrays;
import java.util.Random;

import java.util.List;
import java.util.ArrayList;


class GameWindow {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Maze Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.startGameLoop();  // optional, if you want animation
    }
}

class mazeGenerator{
    private final int rows, cols;
    private final Entity[][] map;
    private final Random rand = new Random();

    public mazeGenerator(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        this.map = new Entity[rows][cols];
        primsGenerator();
    }
    public Entity[][] getMaze(){
        return map;
    }
    private void primsGenerator(){
        for (int y = 0; y < rows; y++){
            for(int x = 0; x < cols; x++){
                map[y][x] = new Wall(x, y, 1, 1);
            }
        }
        //ensure even starting point
        int startX = rand.nextInt(cols/2) * 2;
        int startY = rand.nextInt(rows/2) * 2;
        List<int[]> frontier = new ArrayList<>();

        
    }
    private void addFrontier(int x, int y, List<int[]> frontier){
        for (int[] d : new int[][]{{-2, 0}, {2, 0}, {0, -2}, {0, 2}}) {
            int newX = x + d[0];
            int newY = y + d[0];
            if(inBounds(newX, newY) && map[newX][newY] instanceof Wall){
                frontier.add(new int[]{newX, newY});
            }
    }
    private boolean inBounds(int x, int y) {
        return x >= 0 && x < cols && y >= 0 && y < rows;
    }

}
class GamePanel extends JPanel implements KeyListener {
    final int TILE_SIZE = 16;
    final int ROWS = 50;
    final int COLS = 75;

    Random random = new Random();
    Entity[][] map = new Entity[ROWS][COLS];
    Player p;

    public GamePanel() {
        this.setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        initMap();
    }
    private void primsInit(){



    }
    private void initMap() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                map[y][x] = new Floor(x, y, 1, 1);
            }
        }

        // add walls
        map[3][3] = new Wall(3, 3, 1, 1);
        map[4][4] = new TrappedFloor(4, 4, "fire");

        // add p
        p = new Player(1, 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        // draw map
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                Color color = map[y][x].color;
                g.setColor(color);
                
                g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // draw player body
        g.setColor(p.color);
        g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE * p.width, TILE_SIZE * p.height);

        // draw direction indicator (small square just beyond the bounding box)
        g.setColor(Color.CYAN);

        int[] movementVector = p.getMovement();

        int dx = movementVector[0];
        int dy = movementVector[1];

        // Calculate nose tile (just beyond the current bounding box)
        int noseX = p.x + dx;
        int noseY = p.y + dy;

        // Optional: clamp within grid
        if (noseX >= 0 && noseX < COLS && noseY >= 0 && noseY < ROWS) {
            int indicatorSize = TILE_SIZE / 4;
            int centerX = noseX * TILE_SIZE + TILE_SIZE / 2 - indicatorSize / 2;
            int centerY = noseY * TILE_SIZE + TILE_SIZE / 2 - indicatorSize / 2;
            g.fillRect(centerX, centerY, indicatorSize, indicatorSize);
        }
    }

    public void startGameLoop() {
        Timer timer = new Timer(100, e -> repaint());
        timer.start();
    }

    // === Key Handling ===
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        int backingUpModifier = (code == KeyEvent.VK_S) ? -1 : 1;

        if (code == KeyEvent.VK_Q) p.turn("left");
        if (code == KeyEvent.VK_E) p.turn("right");

        int[] movementVector = Arrays.copyOf(p.getMovement(), 2);
        int newX = (movementVector[0] * backingUpModifier) + p.x;
        int newY = (movementVector[1] * backingUpModifier) + p.y;

        if(canMoveTo(newX, newY)){
            if(map[newY][newX].passable){
                if (code == KeyEvent.VK_W)  p.move(movementVector);
                if (code == KeyEvent.VK_S){
                    movementVector[0] *= -1;
                    movementVector[1] *= -1;
                    p.move(movementVector);
                }
            }
            if(map[p.y][p.x] instanceof TrappedFloor){
                System.out.println("Applying effect");
                map[newY][newX].onStep(p);
            }
            
        }
        repaint();
    }
    private boolean canMoveTo(int newX, int newY){
        return newX >= 0 && newX < COLS && newY >= 0 
            && newY < ROWS && 
            map[newY][newX].passable;
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
