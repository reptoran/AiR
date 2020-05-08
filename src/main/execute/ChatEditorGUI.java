package main.execute;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import main.presentation.chateditor.ChatEditorPanel;

public class ChatEditorGUI
{
	private static JFrame frame;
	private static ChatEditorPanel editorPanel;
	
	private static final int FRAME_WIDTH = 900;
	private static final int FRAME_HEIGHT = 810;
	
	public static void main(String[] args)
	{
		frame = new JFrame("AiR Chat Editor");
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		frame.setResizable(false);
		
		editorPanel = new ChatEditorPanel(FRAME_HEIGHT, FRAME_WIDTH);
		
		frame.setContentPane(editorPanel);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
