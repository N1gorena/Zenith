package help;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;

import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import main.GuObject;

public class BattleMaster {

	private LinkedList<GuObject> rumblers = new LinkedList<GuObject>();
	private boolean packageAsk = false;
	private PlayerListener pcActionListener = null;
	private JFrame askFrame = null;
		
	public BattleMaster(LinkedList<GuObject> combatants) {
		rumblers = combatants;
		Collections.sort(rumblers);
		
	}


	public void queryCreatures(long window) {
	
		for (GuObject guObject : rumblers) {
			
			if(guObject.isPC() && !packageAsk) {
				
				packageAsk = true;
				askFrame = new JFrame("Move?");
				askFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				askFrame.setAlwaysOnTop(true);
				askFrame.setVisible(true);
				
				JPanel rootPane = new JPanel();
				rootPane.setLayout(new BoxLayout(rootPane,BoxLayout.PAGE_AXIS));
				
				pcActionListener = new PlayerListener();
				pcActionListener.setFrame(askFrame);
				
				JButton up = new JButton("Up");
				up.addActionListener(pcActionListener);
				JButton down = new JButton("Down");
				down.addActionListener(pcActionListener);
				JButton left = new JButton("Left");
				left.addActionListener(pcActionListener);
				JButton right = new JButton("Right");
				right.addActionListener(pcActionListener);
				JButton submitButton = new JButton("Submit");
				submitButton.addActionListener(pcActionListener);
				
				JPanel buttonHolder = new JPanel();
				buttonHolder.setLayout(new FlowLayout());
				
				buttonHolder.add(up);
				buttonHolder.add(down);
				buttonHolder.add(left);
				buttonHolder.add(right);
				
				JPanel submitHolder = new JPanel();
				submitHolder.add(submitButton);
				
				rootPane.add(buttonHolder);
				rootPane.add(submitHolder);
				
				askFrame.add(rootPane);
				askFrame.pack();
				
				GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
				
				askFrame.addWindowListener(new WindowListener() {

					@Override
					public void windowActivated(WindowEvent arg0) {}
					@Override
					public void windowClosed(WindowEvent arg0) {
						GLFW.glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
						String moveDir = pcActionListener.getDir();
						guObject.move(moveDir);
						packageAsk = false;
					}
					@Override
					public void windowClosing(WindowEvent arg0) {
						// TODO Auto-generated method stub	
					}
					@Override
					public void windowDeactivated(WindowEvent arg0) {}
					@Override
					public void windowDeiconified(WindowEvent arg0) {}
					@Override
					public void windowIconified(WindowEvent arg0) {}
					@Override
					public void windowOpened(WindowEvent arg0) {}
					
				});
				
				
			}
		}
		
	}


	public void close() {
		askFrame.dispose();		
	}


	public void queryContainers(long window) {
		for (GuObject guObject : rumblers) {
			
			if(guObject.isPC()){
				for( GuObject guContainer :rumblers) {
					if(guContainer.isContainer()) {
						Vector3f diff = new Vector3f();
						guObject.getLocationVec().sub(guContainer.getLocationVec(),diff);
						float len = diff.length();
						if(len <= 0.5f) {
							int state = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F);
							if(state == GLFW.GLFW_PRESS) {
								System.out.println("Winner");
								guObject.setLocation(0.0f, 0.0f, 0.0f);
							}
						}
					}
				}
			}
		}
		
	}

}
