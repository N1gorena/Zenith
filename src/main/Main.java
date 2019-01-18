package main;



import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.jna.NativeLibrary;

import camara.CameraListener;
import help.BattleMaster;
import help.ModelLoadObserver;
import help.X3pCamera;
import shaderBuilder.ProgramLinker;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class Main {
	
	public static LinkedList<ObjectMachine> meshes = new LinkedList<ObjectMachine>();
	public static LinkedList<GuObject> Entities = new LinkedList<GuObject>();
	
	public static ProgramLinker programHolster;
	
	public static long window;
	public static int meshCount = 0;
	
	public static void main(String[] args) {
	//	File videoSrc = new File("src\\files\\IntroMovie.mp4");
		
//			Desktop.getDesktop().open(videoSrc);
		//Runnable and Thread
		ObjectLoadingThread objectBackgroundLoader = new ObjectLoadingThread();
		Thread meshThread = new Thread(objectBackgroundLoader);
		
		//Directory for meshes
		File modelFiles = new File("src\\files\\objectFiles");
		//Runnable observer for mesh progress
		ModelLoadObserver meshProgress = new ModelLoadObserver(modelFiles.listFiles().length);
		objectBackgroundLoader.setFileLocation(modelFiles);
		objectBackgroundLoader.addObserver(meshProgress);
		meshThread.start();
		
		
		JFrame loginFrame = new JFrame();
		loginFrame.setSize(1200, 900);
		loginFrame.setLocationRelativeTo(null);
		
		loginFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//VLCJ Lib to play Video.
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "G:/VLC");
		
		EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		loginFrame.setContentPane(mediaPlayerComponent);
		
		//loginFrame.setVisible(true);
		int x = loginFrame.getLocation().x;
		int y = loginFrame.getLocation().y;
		loginFrame.dispose();
		
		mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				loginFrame.setVisible(false);
				loginFrame.dispose();
				GLFW.glfwShowWindow(window);
			}
		});
		
		
		//mediaPlayerComponent.getMediaPlayer().playMedia("src\\files\\IntroMovie.mp4");
		
		while(!meshProgress.pleaseContinue()) {
			System.out.println("here");
			
		}
		
		GLFW.glfwInit();
		//3d Camera
		CameraListener fpCamera = new CameraListener();
		X3pCamera dim3Camera = new X3pCamera();
		//Window
		GLFW.glfwWindowHint(GLFW_VISIBLE,GLFW_TRUE);
	
		//GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		window = GLFW.glfwCreateWindow(1200, 900, "HI", MemoryUtil.NULL, MemoryUtil.NULL);
		GLFW.glfwSetWindowPos(window, x, y);
		
		GLFW.glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		
		
		
		GLFW.glfwSetKeyCallback(window, dim3Camera);
		//GLFW.glfwSetCursorPosCallback(window,fpCamera.getMause());
		
		
		File setupCharactersFile = new File("src\\files\\Actors.set");
		DocumentBuilderFactory dBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = dBuilderFactory.newDocumentBuilder();
			Document document = docBuilder.parse(setupCharactersFile);
			
			document.getDocumentElement().normalize();
			//Get player character node. Should be single node.
			NodeList pcNode = document.getElementsByTagName("PC");
			Node fpcNode = pcNode.item(0);
			
			if(fpcNode.getNodeType() == Node.ELEMENT_NODE) {
				
				Element playerOneData = (Element) fpcNode;
				
				String meshName = playerOneData.getElementsByTagName("name").item(0).getTextContent();
				//Location String should be in x,y,z format.
				String locationString = playerOneData.getElementsByTagName("location").item(0).getTextContent();
				String[] x2z = locationString.split(",");
				for(ObjectMachine om : meshes) {
					if( om.getName().equals(meshName) ) {
						GuObject nuEnt = new GuObject(om);
						nuEnt.setIsPC();
						nuEnt.setLocation(Float.parseFloat(x2z[0]),Float.parseFloat(x2z[1]),Float.parseFloat(x2z[2]));
						Entities.add(nuEnt);
						break;
					}
				}
			}
			
			NodeList goalNode = document.getElementsByTagName("Goal");
			Node goal = goalNode.item(0);
			if(goal.getNodeType() == Node.ELEMENT_NODE) {
				Element goalData = (Element) goal;
				String meshName = goalData.getElementsByTagName("name").item(0).getTextContent();
				//Location String should be in x,y,z format.
				String locationString = goalData.getElementsByTagName("location").item(0).getTextContent();
				String[] x2z = locationString.split(",");
				for(ObjectMachine om : meshes) {
					if(om.getName().equals(meshName) ) {
						GuObject nuEnt = new GuObject(om);
						nuEnt.setSpeed(0);
						nuEnt.setisContainer();
						nuEnt.setLocation(Float.parseFloat(x2z[0]),Float.parseFloat(x2z[1]),Float.parseFloat(x2z[2]));
						Entities.add(nuEnt);
						break;
					}
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		GL30.glClearColor(0.0f, 0.4f, 0.4f, 1.0f);
		
		programHolster = new ProgramLinker("src\\files\\VertexShader","src\\files\\FragmentShader");
		
		for (GuObject object : Entities) {
			object.setup();
			//1p game i guess....
			if(object.isPC()) {
				dim3Camera.setPlayerFocus(object);
			}
		}
		
		BattleMaster gm = new BattleMaster(Entities);
		
		while(!GLFW.glfwWindowShouldClose(window)) {
			
			GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
			GL30.glUseProgram(programHolster.getProgram());
			
			int camLocation = GL30.glGetUniformLocation(programHolster.getProgram(),"cameraMatrix");
			//GL30.glUniformMatrix4fv(camLocation,false, fpCamera.getCamara());
			
			GL30.glUniformMatrix4fv(camLocation, false, dim3Camera.getCamera());
			
			int translateLocation = GL30.glGetUniformLocation(programHolster.getProgram(), "translateMatrix");
			for (GuObject object : Entities) {
				object.drawSelf(translateLocation);
			}
			//Drawing/\/\/\
			//Game\/\/\/
			
			gm.queryCreatures(window);
			gm.queryContainers(window);
			GLFW.glfwSwapBuffers(window);
			GLFW.glfwPollEvents();
		}
		GLFW.glfwDestroyWindow(window);
		gm.close();
	}

}
