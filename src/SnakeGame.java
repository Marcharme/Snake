import java.awt.*;
import java.awt.event.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile{
        int x;
        int y;

        Tile(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    private class PauseButton {
        int x, y, width, height;
        String label = "PAUSE";

        PauseButton(int x, int y, int width, int height){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    
    boolean isClicked(int mouseX, int mouseY){
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    void draw(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(Color.GRAY);
    g2d.fillRect(x, y, width, height);
    g2d.setColor(Color.BLACK);
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRect(x, y, width, height);
    g2d.setColor(Color.BLACK);
    g2d.setFont(new Font("arial", Font.BOLD, 14));
    FontMetrics fm = g2d.getFontMetrics();
    int textX = x + (width - fm.stringWidth(label)) / 2;
    int textY = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
    g2d.drawString(label, textX, textY);
}
    }
    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    //snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    //comida
    Tile food;
    Random random;

    //logica del juego
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;
    boolean isPaused = false;
    PauseButton pauseButton;

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        addMouseListener(new MouseAdapter(){
            @Override
           public void mousePressed(MouseEvent e) {
                if (pauseButton.isClicked(e.getX(), e.getY()) && !gameOver) {
                    isPaused = !isPaused;
                }
            }
        });
        setFocusable(true);

        snakeHead = new Tile(5,5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10,10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        pauseButton = new PauseButton(boardWidth - 120, 10, 100, 30);

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //Grid
       // for (int i = 0; i < boardWidth / tileSize; i++) {
         //   g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
           // g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        //}

        //comida
        g.setColor(Color.red);
      //  g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        //cabeza
        g.setColor(Color.green);
       // g.fillRect(snakeHead.x * tileSize,snakeHead.y * tileSize, tileSize, tileSize);
        g.fill3DRect(snakeHead.x * tileSize,snakeHead.y * tileSize, tileSize, tileSize, true);
       
        //cuerpo
        for (int i = 0; i < snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
           // g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
             g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize,true);
        }
        //puntaje
        g.setFont(new Font("arial", Font.PLAIN, 16));
        if (gameOver){
          g.setColor(Color.red);
          g.drawString("Game Over: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
        }
        else{
            g.drawString("Score: " + String.valueOf(snakeBody.size()), tileSize -16, tileSize);

        }
    //boton de pausa
    if (isPaused && !gameOver) {
        g.setColor(new Color(0,0,0, 150));
        g.fillRect(0,0, boardWidth, boardHeight);
        g.setColor(Color.WHITE);
        g.setFont(new Font("arial", Font.BOLD, 40));
        String pauseText = "PAUSA";
        FontMetrics fm = g.getFontMetrics();
        int x = (boardWidth - fm.stringWidth(pauseText)) / 2;
        int y = (boardHeight - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(pauseText, x, y);
    }
    }
    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize);
        food.y = random.nextInt(boardHeight / tileSize);
    }

    public boolean collision(Tile tile1, Tile tile2){
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

public void move() {

    //comer comida
    if (collision(snakeHead, food)){
        snakeBody.add(new Tile(food.x, food.y));
        placeFood();
    }

    //mover cuerpo
    for (int i = snakeBody.size()-1; i >= 0; i--){
        Tile snakePart = snakeBody.get(i);
        if (i == 0){
            snakePart.x = snakeHead.x;
            snakePart.y = snakeHead.y;
        }
        else {
            Tile prevSnakePart = snakeBody.get(i-1);
            snakePart.x = prevSnakePart.x;
            snakePart.y = prevSnakePart.y;
        }
    }

    //cabeza
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;
    //game over condiciones
        for (int i = 0; i < snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
    //chocar con la cabeza
            if (collision(snakeHead, snakePart)){
                gameOver = true;
            }
        }
        if (snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth || 
            snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight
    ){gameOver = true;}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            gameLoop.stop();
        }
    }

   @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE){
            isPaused = !isPaused;
        }
        else if (!isPaused && !gameOver){
            if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1){
                velocityX = 0;
                velocityY = -1;
            }
             else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1){
                velocityX = 0;
                velocityY = 1;
            }
            else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1){
                velocityX = -1;
                velocityY = 0;
            }
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1){
                velocityX = 1;
                velocityY = 0;
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1){
            velocityX = 0;
            velocityY = -1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1){
            velocityX = 0;
            velocityY = 1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1){
            velocityX = -1;
            velocityY = 0;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1){
            velocityX = 1;
            velocityY = 0;
        }
    }

//no necesario
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}