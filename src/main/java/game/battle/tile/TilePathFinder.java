package game.battle.tile;

import game.battle.Scene;

import java.util.*;

public class TilePathFinder {
    private final Scene scene;

    public TilePathFinder(Scene scene) {
        this.scene = scene;
    }

    public TilePath findPath(Tile start, Tile end) {
        // Quick check for invalid tiles
        if (!start.isPassable() || !end.isPassable()) {
            return new TilePath();
        }

        // BFS uses a simple queue (FIFO)
        Queue<Node> queue = new LinkedList<>();
        // Track visited tiles
        Set<String> visited = new HashSet<>();

        // Start the search
        Node startNode = new Node(start.getX(), start.getY(), null);
        queue.add(startNode);
        visited.add(startNode.x + "," + startNode.y);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Check if we've reached the destination
            if (current.x == end.getX() && current.y == end.getY()) {
                return new TilePath(reconstructPath(current));
            }

            // Get neighbors and add unvisited ones to the queue
            Tile currentTile = scene.getTile(current.x, current.y);
            List<Tile> neighbors = currentTile.getNeighbors(scene.getTiles()).toList();

            for (Tile neighbor : neighbors) {
                int nx = neighbor.getX();
                int ny = neighbor.getY();
                String key = nx + "," + ny;

                if (neighbor.isPassable() && !entityOccupies(nx, ny) && !visited.contains(key)) {
                    Node neighborNode = new Node(nx, ny, current);
                    queue.add(neighborNode);
                    visited.add(key);
                }
            }
        }

        // No path found
        return new TilePath();
    }

    private List<Tile> reconstructPath(Node end) {
        List<Tile> path = new ArrayList<>();
        Node current = end;

        while (current != null) {
            path.add(scene.getTile(current.x, current.y));
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }

    public boolean entityOccupies(int x, int y) {
//        return false;
        return scene.findEntityByPosition(x, y).isPresent();
    }

    // Simplified Node class - only tracks position and parent
    private static class Node {
        int x;
        int y;
        Node parent;

        public Node(int x, int y, Node parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }
    }
}