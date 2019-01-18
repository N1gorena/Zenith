package help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class PlayerListener implements ActionListener {

	private String dirChoice = "";
	private JFrame actionChoiceFrame = new JFrame();
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(((JButton)e.getSource()).getText().equals("Submit")) {
			if(!dirChoice.equals(""))
			actionChoiceFrame.dispose();
		}
		else {
			dirChoice = ((JButton)e.getSource()).getText();
			//test
		}

	}

	public void setFrame(JFrame optionFrame) {
		actionChoiceFrame = optionFrame;
		
	}

	public String getDir() {
		// TODO Auto-generated method stub
		return dirChoice;
	}

}
