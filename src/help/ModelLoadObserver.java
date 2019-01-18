package help;

import java.util.Observable;
import java.util.Observer;

public class ModelLoadObserver implements Observer {

	private boolean continu = false;
	private int modelCount = 0;
	
	public ModelLoadObserver(int count) {
		modelCount = count;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if((int)arg1 == modelCount) {
			continu = true;
		}

	}

	public boolean pleaseContinue() {
		
		return continu;
	}

}
