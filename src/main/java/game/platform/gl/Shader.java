package game.platform.gl;

import lombok.Getter;
import org.lwjgl.opengl.GL20;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Shader {
    @Getter
    private final int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    public Shader(String vertexFile, String fragmentFile) {
        vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();

        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);

        GL20.glLinkProgram(programID);
        if (GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
            System.err.println("Program linking error: " + GL20.glGetProgramInfoLog(programID));
            System.exit(-1);
        }

        GL20.glValidateProgram(programID);
        if (GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == GL20.GL_FALSE) {
            System.err.println("Program validation error: " + GL20.glGetProgramInfoLog(programID));
            System.exit(-1);
        }

        // Detach shaders after linking (best practice)
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
    }

    public void start() {
        GL20.glUseProgram(programID);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void dispose() {
        stop();
        GL20.glDeleteProgram(programID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
    }

    public int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(programID, uniformName);
    }

    public void setInt(String uniformName, int value) {
        GL20.glUniform1i(getUniformLocation(uniformName), value);
    }

    public void setMatrix4f(String uniformName, float[] matrix) {
        GL20.glUniformMatrix4fv(getUniformLocation(uniformName), false, matrix);
    }

    private static int loadShader(String resourcePath, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            InputStream inputStream = Shader.class.getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                throw new RuntimeException("Shader resource not found: " + resourcePath);
            }

            var streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            try (BufferedReader reader = new BufferedReader(streamReader)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    shaderSource.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading shader resource: " + resourcePath, e);
        }

        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shaderID);
            GL20.glDeleteShader(shaderID);
            throw new RuntimeException("Shader compilation error for " + resourcePath + ":\n" + log);
        }

        return shaderID;
    }
}
