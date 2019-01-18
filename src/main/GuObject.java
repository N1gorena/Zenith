package main;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

public class GuObject implements Comparable<GuObject> {
	private final int VBO = 0;
	private final int EBO = 1;
	private int[] modelBuffers = new int[2];
	private int[] VAO = new int[1];
	private String referenceName = null;
	private ObjectMachine modelData = null;
	private Vector3f location = new Vector3f();
	private int speed = 1;
	
	private boolean isPC = false;
	private boolean isContainer = false;
	
	public GuObject() {
		
	}

	public GuObject(ObjectMachine prepModel) {
		modelData = prepModel;
	}

	public void setObjectName(String name) {
		referenceName = name;
		
	}

	public void setLocation(float x, float y, float z) {
		location.x = x;
		location.y = y;
		location.z = z;
	}

	public void setup() {
		GL30.glGenVertexArrays(VAO);
		GL30.glGenBuffers(modelBuffers);
		
		GL30.glBindVertexArray(VAO[0]);
		
		
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, modelBuffers[VBO]);
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, modelData.getTriangles(), GL30.GL_STATIC_DRAW);
		
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, modelBuffers[EBO]);
		GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, modelData.getIndices(), GL30.GL_STATIC_DRAW);
		
		GL30.glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
		GL30.glEnableVertexAttribArray(0);
		
		GL30.glBindVertexArray(0);
		
	}

	public void drawSelf(int translateLocation) {
		Matrix4f locationMatrix = new Matrix4f().translate(location.x, location.y, location.z);
		FloatBuffer locationBuffer = BufferUtils.createFloatBuffer(16);
		locationMatrix.get(locationBuffer);
		
		GL30.glBindVertexArray(VAO[0]);
		GL30.glUniformMatrix4fv(translateLocation, false, locationBuffer);
		//TODO optimize the length get.
		GL30.glDrawElements(GL30.GL_TRIANGLES, modelData.getIndices().length, GL30.GL_UNSIGNED_INT,0);
		
		GL30.glBindVertexArray(0);
	}

	public boolean isPC() {
		return isPC;
		
	}

	public void setIsPC() {
		speed = 3;
		isPC = true;
	}

	public Vector3f getLocationVec() {
		
		return location;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if (!(obj instanceof GuObject)) return false;
		
		GuObject thatObject = (GuObject)obj;
		return this.speed == thatObject.speed;
	}
	
	
	@Override
	public int compareTo(GuObject arg0) {
		if(arg0.speed > this.speed)
			return -1;
		if(arg0.speed == this.speed)
			return 0;
		if(arg0.speed < this.speed)
			return 1;
		return 0;
	}

	public void setSpeed(int newSpeed) {
		speed = newSpeed;
	}

	public void move(String moveDir) {
		switch(moveDir) {
			case "Up": 
				this.location.z += 1.0f;
			break;
			case "Down": 
				this.location.z -= 1.0f;
			break;
			case "Left": 
				this.location.x += 1.0f;
			break;
			case "Right": 
				this.location.x -= 1.0f;	
			break;
		}
	}

	public void setisContainer() {
		isContainer = true;
		
	}

	public boolean isContainer() {
		
		return isContainer;
	}
}
