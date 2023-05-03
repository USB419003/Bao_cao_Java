package Demo;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.swing.*;

import java.util.Random;
import java.util.TimerTask;

public final class GamePanel extends JPanel implements ActionListener{
	JPanel panel = new JPanel();
	JFrame frame = new JFrame();
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 600;
	private static final int UNIT = 25;
	private static final int SCREEN = 20;
	private static final int TILE = WIDTH/SCREEN;
	private static final int GAME = (WIDTH*HEIGHT)/UNIT;
	final int x[]= new int [GAME];
	final int y[]= new int [GAME];
	int DELAY = 65;
	int max = 0;
	int SleepTimer = 5000;
	int body = 6;
	int eaten;
	int FoodX;
	int FoodY;
	int score = 0;
	int seconds = 0;
	int minutes = 0;
	int hours = 0;
	int elapseTime = 0;
	char direction = 'D';
	boolean RUNNING = false;
	String seconds_String = String.format("%02d",seconds);
	String minutes_String = String.format("%02d",minutes);
	String hours_String = String.format("%02d",hours); 
	String score_String = String.format("%1d",score);
	Timer timer1;
	Random random;
	

	
	public GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		this.setBackground(Color.darkGray);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		start();
		try {
			String DB_URL = "jdbc:mysql://localhost:3306/Highscore";
			String USER = "root";
			String PASSWORD = "Brrocoins2003";
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			String selectSql = "SELECT MAX(Score) FROM Scores";
			java.sql.Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(selectSql);
			if (resultSet.next()) {
				eaten = resultSet.getInt(1);
				
			}
			conn.close();
		} 
		catch (SQLException e) {
			System.err.println("Error fetching max score: " + e.getMessage());
		
		}
	}
	
	public void newGame() {
		frame.setTitle("Snake");
		panel.add(new GamePanel());
		frame.add(panel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		try {
			String DB_URL = "jdbc:mysql://localhost:3307/quanlydiem";
			String USER = "root";
			String PASSWORD = "12345678m";
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			String selectSql = "SELECT MAX(score) FROM scores";
			java.sql.Statement statement = conn.createStatement();
			ResultSet resultSet = statement.executeQuery(selectSql);
			if (resultSet.next()) {
				eaten = resultSet.getInt(1);
				
			}
			conn.close();
		} 
		catch (SQLException e) {
			System.err.println("Error fetching max score: " + e.getMessage());
		}
	}
	

	public void start() {
		Food();
		RUNNING = true;
		timer1 = new Timer(DELAY,this);
		timer1.start();	
			
	}
	public void Timer() {
		elapseTime = elapseTime + 1000;
		hours = (elapseTime / 3600000);
		minutes = (elapseTime / 60000) % 60;
		seconds = (elapseTime / 1000) % 60;
		seconds_String = String.format("%02d",seconds);
		minutes_String = String.format("%02d",minutes);
		hours_String = String.format("%02d",hours);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw (Graphics g) {	
		if (RUNNING) {
			
			//Draw lines
			for(int i = 0; i < HEIGHT/UNIT; i++) {
				g.drawLine(i*UNIT, 0, i*UNIT, HEIGHT);
				g.drawLine(0, i*UNIT, WIDTH, i*UNIT);
			}
			
			//Draw Food
			g.setColor(Color.red);
			g.fillOval(FoodX, FoodY, UNIT, UNIT);
			
			//Draw Snake
			for(int i = 0; i < body; i++) {
				if(i==0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT,UNIT);
				}
				else {
					g.setColor(new Color(45,180,0));
					g.fillRect(x[i], y[i], UNIT,UNIT);
				}
			}
			
			//Score
			g.setColor(Color.orange);
			g.setFont(new Font("CALIBRE", Font.BOLD, 25));
			FontMetrics metrics1 = getFontMetrics(g.getFont());
			g.drawString("SCORE: "+eaten,(WIDTH-metrics1.stringWidth("SCORE: "+eaten))/2,g.getFont().getSize());
			
			// Timer
			g.setColor(Color.orange);
			g.setFont(new Font("CALIBRE", Font.BOLD, 25));
			FontMetrics metrics0 = getFontMetrics(g.getFont());
			g.drawString(hours_String+":"+minutes_String+":"+seconds_String,(WIDTH-metrics0.stringWidth(hours_String+":"+minutes_String+":"+seconds_String))/2,g.getFont().getSize()*2);
			
			try {
				String DB_URL = "jdbc:mysql://localhost:3307/quanlydiem";
				String USER = "root";
				String PASSWORD = "12345678m";
				Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
				String insertSql = "INSERT INTO scores (player_name, score, levels) VALUES (?, ?, ?)";
				PreparedStatement preparedStatement = conn.prepareStatement(insertSql);
				preparedStatement.setInt(2, eaten);
				preparedStatement.setInt(3, elapseTime);
				preparedStatement.executeUpdate();
				conn.close();
			} catch (SQLException e) {
				System.err.println("Error" + e.getMessage());
			}
		}
		else {
			GameOver(g);
		}
	}
	public void move() {
		for(int i = body; i>0 ; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
			
		switch (direction) {
		case 'U':
			y[0] = y[0] - UNIT; break;
		case 'D':
			y[0] = y[0] + UNIT; break;
		case 'L':
			x[0] = x[0] - UNIT; break;
		case 'R':
			x[0] = x[0] + UNIT; break;

		}
	}
	
	public void Food() {
		FoodX= random.nextInt((int)(WIDTH/UNIT))*UNIT;
		FoodY= random.nextInt((int)(HEIGHT/UNIT))*UNIT;
	}
	
	
	
	public void checkFood() {
		if ((x[0]==FoodX)&&(y[0]==FoodY)){
			body++;
			eaten++;
			Food();
		}	
	}
	public void checkScore() {
		if(eaten>max) {
			max = eaten;
		}
	}
		
	public void checkCollision(){
		
		//checking self-damage
		for(int i = body; i>0; i--) {
			if((x[0]==x[i]) && (y[0]==y[i])){
				RUNNING= false;
			}
			
		}
		//no more border-damage
		if (x[0]<0){
			x[0] = WIDTH ;
		}
		else if(x[0]>WIDTH){
			x[0] = 0;
		}
		else if(y[0]<0){
			y[0] = HEIGHT;
		}
		else if (y[0]>HEIGHT){
			y[0] = 0;
		}
		if(!RUNNING){
			timer1.stop();
		}
	}
	
	public void GameOver(Graphics g) {
		//Score
		g.setColor(Color.orange);
		g.setFont(new Font("CALIBRE", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("SCORE: "+eaten,(WIDTH-metrics1.stringWidth("SCORE: "+eaten))/2,g.getFont().getSize());
		
		//High-score
		g.setColor(Color.orange);
		g.setFont(new Font("CALIBRE", Font.BOLD, 40));
		FontMetrics metrics4 = getFontMetrics(g.getFont());
		g.drawString("HIGH-SCORE: "+max,(WIDTH-metrics4.stringWidth("HIGH-SCORE: "+max))/2,g.getFont().getSize()*4);
		
		//Timer
		g.setColor(Color.orange);
		g.setFont(new Font("CALIBRE", Font.BOLD, 40));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString(hours_String+":"+minutes_String+":"+seconds_String,(WIDTH-metrics2.stringWidth(hours_String+":"+minutes_String+":"+seconds_String))/2,g.getFont().getSize()*2);
		
		//Game Over
		g.setColor(Color.orange);
		g.setFont(new Font("CALIBRE", Font.BOLD, 75));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("GAME OVER",(WIDTH-metrics3.stringWidth("GAME OVER"))/2, HEIGHT/2);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(RUNNING) {
			move();
			checkFood();
			checkCollision();
			checkScore();
		}
		repaint();
		Timer();
	}
	
	
	public void pause() {
		try {
				Thread.sleep(SleepTimer);
			} 
			catch (InterruptedException e) {
				e.getStackTrace();
			}
		}
	
	public void resume() {
		Thread.currentThread().interrupt();
	}
	
	public void CloseGame(KeyEvent e) {
		JComponent comp = (JComponent) e.getSource();
		Window win = SwingUtilities.getWindowAncestor(comp);
		win.dispose();
	}
	
	public void reset(){
		EventQueue.invokeLater(()->
		{
			GamePanel game = new GamePanel();
			game.newGame();
		});
	}
	
	public class MyKeyAdapter extends KeyAdapter{
	@Override
		public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A:
					if (direction!='R') { 
						direction = 'L';
					} break;
					
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D:
					if (direction!='L') {
						direction = 'R';
					} break;
					
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W:
					if (direction!='D') {
						direction = 'U';
					} break;
					
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S:
					if (direction!='U') {
						direction = 'D';
					} break;
					
				case KeyEvent.VK_SPACE:
					CloseGame(e);
					reset();
					break;
					
				case KeyEvent.VK_P:
					pause();
					break;
				
				case KeyEvent.VK_ENTER:
					resume();
					break;
				}
			}
	}

}
	
