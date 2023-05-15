package main;

import javax.swing.JFrame;

public class AnimalApp extends JFrame {
	private static final long serialVersionUID = 6457792220456140992L;

	public AnimalApp(String title) {
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1920,1080);
		
		GardenPanel panel = new GardenPanel(this.getSize());
		
		this.add(panel);
		
		this.setVisible(true);
	}

	public static void main(String[] args) {
		new AnimalApp("My Interactive Bee Garden");
	}
}
