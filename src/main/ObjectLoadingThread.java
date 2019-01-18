package main;

import java.io.File;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Scanner;


import javax.swing.JFrame;

import com.sun.jna.NativeLibrary;

import help.ModelLoadObserver;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class ObjectLoadingThread extends Observable implements Runnable{
	private LinkedList<ObjectMachine> target = null;

	private File dirLoc = null;

	public ObjectLoadingThread() {
		super();
		target = Main.meshes;
		
	}

	@Override
	public void run() {
		
		File modelFiles = dirLoc;
		int fNum = 0;
		for(File f : modelFiles.listFiles()) {
			fNum++;
			ObjectMachine logicalObject = ObjectMachine.prepModel(f);
			logicalObject.setObjectName(f.getName().split("\\.")[0]);
			target.add(logicalObject);
			setChanged();
			notifyObservers(fNum);
		}
		
	}

	public void setFileLocation(File fileDirectory) {
		dirLoc = fileDirectory;
		
	}
}
