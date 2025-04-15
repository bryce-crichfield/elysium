package game.state.battle.entity.component;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ComponentDeserializerRegistry {
    private static final Map<String, ComponentDeserializerFunction<?>> deserializers = new HashMap<>();

    public static ComponentDeserializerFunction<?> getDeserializer(String type) {
        return deserializers.get(type);
    }

    public static List<String> getDependencyOrder(Set<String> componentTypes) {
        // Build adjacency list for the dependency graph
        Map<String, Set<String>> graph = new HashMap<>();
        Map<String, Integer> inDegrees = new HashMap<>();

        // Initialize graph
        for (String type : componentTypes) {
            graph.put(type, new HashSet<>());
            inDegrees.put(type, 0);
        }

        // Build the graph edges (dependencies)
        Reflections reflections = new Reflections(
                new org.reflections.util.ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage("game.state"))
                        .setScanners(new MethodAnnotationsScanner())
        );
        Set<Method> deserializerMethods = reflections.getMethodsAnnotatedWith(ComponentDeserializer.class);

        // Create a map of component type name to deserializer method
        Map<String, Method> typeToMethod = new HashMap<>();
        for (Method method : deserializerMethods) {
            ComponentDeserializer annotation = method.getAnnotation(ComponentDeserializer.class);
            String typeName = annotation.type().getSimpleName(); // Or use getName() if you need full class name
            typeToMethod.put(typeName, method);
        }

        // Now build dependencies using the method annotations
        for (String type : componentTypes) {
            Method deserializerMethod = typeToMethod.get(type);
            if (deserializerMethod == null) continue;

            ComponentDeserializer annotation = deserializerMethod.getAnnotation(ComponentDeserializer.class);
            Class<?>[] dependencyClasses = annotation.dependencies();

            // Convert dependency classes to type names
            for (Class<?> dependencyClass : dependencyClasses) {
                String dependencyName = dependencyClass.getSimpleName(); // Or use getName() if needed
                if (componentTypes.contains(dependencyName)) {
                    graph.get(dependencyName).add(type);
                    inDegrees.put(type, inDegrees.get(type) + 1);
                }
            }
        }

        // Perform topological sort using Kahn's algorithm (unchanged)
        List<String> result = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();

        // Add all nodes with no dependencies to the queue
        for (String type : componentTypes) {
            if (inDegrees.get(type) == 0) {
                queue.add(type);
            }
        }

        // Process the queue
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);

            // Remove this node from the graph
            for (String dependent : graph.get(current)) {
                inDegrees.put(dependent, inDegrees.get(dependent) - 1);
                if (inDegrees.get(dependent) == 0) {
                    queue.add(dependent);
                }
            }
        }

        // Check for cycles
        if (result.size() != componentTypes.size()) {
            throw new IllegalStateException("Circular component dependencies detected!");
        }

        return result;
    }

    public static void registerDeserializers() {
        // Uses reflections to find all methods annotated with @ComponentDeserializer
        Reflections reflections = new Reflections(
                new org.reflections.util.ConfigurationBuilder()
                        .setUrls(ClasspathHelper.forPackage("game.state"))
                        .setScanners(new MethodAnnotationsScanner())
        );

        Set<Method> deserializerMethods = reflections.getMethodsAnnotatedWith(ComponentDeserializer.class);

        // Find methods annotated with ComponentDeserializer
        for (Method method : deserializerMethods) {
            try {
                // Ensure the method is static
                if (Modifier.isStatic(method.getModifiers())) {
                    // Get component type from annotation
                    ComponentDeserializer annotation = method.getAnnotation(ComponentDeserializer.class);
                    Class<?> componentType = annotation.type();

                    // Create a ComponentDeserializerFunction that delegates to this method
                    ComponentDeserializerFunction<?> deserializer = createDeserializerFunction(method);
                    ComponentDeserializerRegistry.deserializers.put(annotation.type().getSimpleName(), deserializer);
                    System.out.println("Registered deserializer for: " + componentType.getSimpleName());
                }
            } catch (Exception e) {
                System.err.println("Failed to register deserializer for method: " + method.getName());
                e.printStackTrace();
            }
        }
    }

    private static <T extends Component> ComponentDeserializerFunction<T> createDeserializerFunction(Method method) {
        // This creates a lambda that will invoke the static method
        return (json, entity) -> {
            try {
                @SuppressWarnings("unchecked")
                T result = (T) method.invoke(null, json, entity);
                return result;
            } catch (Exception e) {
                throw new RuntimeException("Error invoking deserializer method: " + method.getName(), e);
            }
        };
    }
}
