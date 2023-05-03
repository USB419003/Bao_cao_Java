package Demo;

import java.awt.EventQueue;

public class SnakeMain {
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> { 
			GamePanel game = new GamePanel();
			game.newGame();
		});	
	}
}
