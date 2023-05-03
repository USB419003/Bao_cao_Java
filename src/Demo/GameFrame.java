package Demo;

import javax.swing.JFrame;

public class GameFrame  extends JFrame{
	public GameFrame() {
		this.setTitle("Snake");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(new GamePanel());
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);

	}
}
