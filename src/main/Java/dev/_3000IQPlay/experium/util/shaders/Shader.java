package dev._3000IQPlay.experium.util.shaders;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Shader {
    protected Map<String, Integer> uniformsMap;
    protected int program;

    public void updateUniforms(float f) {
    }

    public void setUniform(String string, int n) {
        this.uniformsMap.put(string, n);
    }

    public void startShader(float f) {
        GL11.glPushMatrix();
        GL20.glUseProgram((int)this.program);
        if (this.uniformsMap == null) {
            this.uniformsMap = new HashMap<String, Integer>();
            this.setupUniforms();
        }
        this.updateUniforms(f);
    }

    public void stopShader() {
        GL20.glUseProgram((int)0);
        GL11.glPopMatrix();
    }

    public void setupUniform(String string) {
        this.setUniform(string, GL20.glGetUniformLocation((int)this.program, (CharSequence)string));
    }

    public Shader(String string) {
        int n;
        int n2;
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/assets/minecraft/shaders/vertex.vert");
            n2 = this.createShader(IOUtils.toString((InputStream)inputStream), 35633);
            IOUtils.closeQuietly((InputStream)inputStream);
            InputStream inputStream2 = this.getClass().getResourceAsStream(String.valueOf(new StringBuilder().append("/assets/minecraft/shaders/fragment/").append(string)));
            n = this.createShader(IOUtils.toString((InputStream)inputStream2), 35632);
            IOUtils.closeQuietly((InputStream)inputStream2);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        if (n2 == 0 || n == 0) {
            return;
        }
        this.program = ARBShaderObjects.glCreateProgramObjectARB();
        if (this.program == 0) {
            return;
        }
        ARBShaderObjects.glAttachObjectARB((int)this.program, (int)n2);
        ARBShaderObjects.glAttachObjectARB((int)this.program, (int)n);
        ARBShaderObjects.glLinkProgramARB((int)this.program);
        ARBShaderObjects.glValidateProgramARB((int)this.program);
    }

    public void setupUniforms() {
    }

    private int createShader(String string, int n) {
        int n2 = 0;
        try {
            n2 = ARBShaderObjects.glCreateShaderObjectARB((int)n);
            if (n2 == 0) {
                return 0;
            }
            ARBShaderObjects.glShaderSourceARB((int)n2, (CharSequence)string);
            ARBShaderObjects.glCompileShaderARB((int)n2);
            if (ARBShaderObjects.glGetObjectParameteriARB((int)n2, (int)35713) == 0) {
                throw new RuntimeException(String.valueOf(new StringBuilder().append("Error creating shader: ").append(this.getLogInfo(n2))));
            }
            return n2;
        }
        catch (Exception exception) {
            ARBShaderObjects.glDeleteObjectARB((int)n2);
            throw exception;
        }
    }

    private String getLogInfo(int n) {
        return ARBShaderObjects.glGetInfoLogARB((int)n, (int)ARBShaderObjects.glGetObjectParameteriARB((int)n, (int)35716));
    }

    public int getUniform(String string) {
        return this.uniformsMap.get(string);
    }
}