package client.core.graphics.gl;

import lombok.Getter;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32; // For geometry shader support

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Shader {
    @Getter
    private final int programID;
    private int vertexShaderID;
    private int geometryShaderID; // New field for geometry shader
    private int fragmentShaderID;
    private boolean hasGeometryShader; // Track if geometry shader is used

    /**
     * Create a shader program with just vertex and fragment shaders
     */
    public Shader(String vertexFile, String fragmentFile) {
        this(vertexFile, null, fragmentFile);
    }

    /**
     * Create a shader program with optional geometry shader
     */
    public Shader(String vertexFile, String geometryFile, String fragmentFile) {
        vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);

        // Load geometry shader if provided
        hasGeometryShader = geometryFile != null;
        if (hasGeometryShader) {
            geometryShaderID = loadShader(geometryFile, GL32.GL_GEOMETRY_SHADER);
        }

        fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();

        GL20.glAttachShader(programID, vertexShaderID);
        if (hasGeometryShader) {
            GL20.glAttachShader(programID, geometryShaderID);
        }
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
        if (hasGeometryShader) {
            GL20.glDetachShader(programID, geometryShaderID);
        }
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
        if (hasGeometryShader) {
            GL20.glDeleteShader(geometryShaderID);
        }
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

        // Try file system first, then fall back to classpath if file doesn't exist
        File file = new File(resourcePath);

        try {
            if (file.exists()) {
                // Load from file system
                System.out.println("Loading shader from file system: " + file.getAbsolutePath());
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        shaderSource.append(line).append("\n");
                    }
                }
            } else {
                // Fall back to classpath
                System.out.println("Loading shader from classpath: " + resourcePath);
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
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading shader resource: " + resourcePath, e);
        }

        // Rest of the method remains the same...
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