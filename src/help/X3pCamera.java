package help;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import main.GuObject;

public class X3pCamera implements GLFWKeyCallbackI {
	
	private boolean[] keysDown = new boolean[512];
	private GuObject cameraTarget = null;
	private Vector3f up = new Vector3f(0.0f,1.0f,0.0f);
	double yaw = 0.0f; 
	
	public X3pCamera() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if(key == GLFW_KEY_ESCAPE) {
			GLFW.glfwSetWindowShouldClose(window, true);
		}
		else if(action == GLFW_PRESS) {
			keysDown[key] = true;
		}
		else if(action == GLFW_RELEASE) {
			keysDown[key] = false;
		}

	}

	public FloatBuffer getCamera() {
		if(keysDown[GLFW.GLFW_KEY_A]) {
			yaw -= 0.5f;
		}
		if(keysDown[GLFW.GLFW_KEY_D]) {
			yaw += 0.5f;
		}
		FloatBuffer camBuffer = BufferUtils.createFloatBuffer(16);
		
		Vector3f targetPos = cameraTarget.getLocationVec();
		Vector3f camPos = new Vector3f();
		Vector3f camOffset = new Vector3f(4.0f*(float)Math.cos(Math.toRadians(yaw)) ,9.0f,4.0f*(float)Math.sin(Math.toRadians(yaw)));
		targetPos.add(camOffset, camPos);
		
		
		Matrix4f camMat = new Matrix4f().perspective((float)Math.toRadians(45.0f), 1.0f, 0.01f, 100.0f).lookAt(camPos,targetPos,up);
		camMat.get(camBuffer);
		return camBuffer;
	}

	public void setPlayerFocus(GuObject object) {
		cameraTarget = object;
		
	}

}
