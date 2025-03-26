package gamepkg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


import java.util.Arrays;
import java.util.Random;



class GameWindow {
    public static void main(String[] args) {
        startWindow();
    }
    public static void startWindow(){
    // === Build input dialog ===
        JTextField rowsField = new JTextField("20");
        JTextField colsField = new JTextField("20");
        JTextField seedField = new JTextField("5124");

        JPanel inputPanel = new JPanel(new GridLayout(3, 3));
        inputPanel.add(new JLabel("Rows:"));
        inputPanel.add(rowsField);
        inputPanel.add(new JLabel("Columns:"));
        inputPanel.add(colsField);
        inputPanel.add(new JLabel("Seed:"));
        inputPanel.add(seedField);

        int result = JOptionPane.showConfirmDialog(
            null,
            inputPanel,
            "Enter Maze Size",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            System.exit(0); // user cancelled
        }

        int rows, cols, seed;
        try {
            rows = Integer.parseInt(rowsField.getText());
            cols = Integer.parseInt(colsField.getText());
            seed = Integer.parseInt(seedField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid input. Please enter integers.");
            return;
        }

        JFrame frame = new JFrame("Maze Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        GamePanel panel = new GamePanel(rows, cols, seed, frame);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.startGameLoop();  // optional, if you want animation
    }
}


class GamePanel extends JPanel implements KeyListener {
    final int TILE_SIZE = 16;
    private int ROWS;
    private int COLS;

    //Random random = new Random();
    Entity[][] map;
    Player p;
    JFrame frame;
    int randomSeed = 0;

    public GamePanel(int rows, int cols, JFrame frame) {
        this(rows, cols, 0, frame);
        
    }
    public GamePanel(int rows, int cols, int seed, JFrame frame) {
        this.frame = frame;
        this.ROWS = rows;
        this.COLS = cols;
        this.randomSeed = seed;
        map = new Entity[ROWS][COLS];
        this.setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        primsInit();
    }
    

    private void primsInit(){

        mazeGenerator m = new mazeGenerator(ROWS, COLS, randomSeed);
        map = m.getMaze();
        p = new Player(m.startX, m.startY);

    }
    private void demoMapInit() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                map[y][x] = new Floor(x, y, 1, 1);
            }
        }

        // add walls
        map[3][4] = new Wall(3, 3, 1, 1);

        TrappedFloor tf = new TrappedFloor(4, 3, "heal");
        map[5][5] = new TrappedFloor(4, 3, "stick");
        map[6][5] = new TrappedFloor(4, 3, "fire");
        map[2][2] = new Goal(2, 2);
        
        map[4][4] = tf;

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

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, TILE_SIZE-1));
        g.drawString(String.valueOf(p.health), p.x * TILE_SIZE, p.y * TILE_SIZE + TILE_SIZE - 2);
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
            if(map[p.y][p.x] instanceof Goal){
                String message = "Congratulations, you've solved the puzzle!";
                String title = "Puzzle Solved";

                Object[] options = {"Start New Puzzle", "Close"};

                int choice = JOptionPane.showOptionDialog(
                    null,
                    message,
                    title,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]  // default button
                );

                if (choice == 0) {
                    frame.dispose();  // Closes the current window
                    GameWindow.startWindow();  // Starts a new game
                } else {
                    System.exit(0);  // Exit the whole program
                }
            }
            
        }
        p.iterateEffects();

        if(p.isDead){
            String message = "YOU DIED :(";
            String title = "Death";

            Object[] options = {"Start New Puzzle", "Close"};

            int choice = JOptionPane.showOptionDialog(
                null,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]  // default button
            );

            if (choice == 0) {
                frame.dispose();  // Closes the current window
                GameWindow.startWindow();  // Starts a new game
            } else {
                System.exit(0);  // Exit the whole program
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
