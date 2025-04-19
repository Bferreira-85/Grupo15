package com.bd2r.harrypotter;

import java.util.*;

public class AStarPathfinder {
    private final int[][] map;
    private final int rows, cols;

    public AStarPathfinder(int[][] map) {
        this.map = map;
        this.rows = map.length;
        this.cols = map[0].length;
    }

    public List<Node> findPath(int startX, int startY, int endX, int endY) {
        Node startNode = new Node(startX, startY);
        Node endNode = new Node(endX, endY);

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparing(Node::getFCost));
        HashSet<Node> closedSet = new HashSet<>();

        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current.equals(endNode)) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor)) continue;

                float tentativeGCost = current.gCost + 1;

                if (!openSet.contains(neighbor) || tentativeGCost < neighbor.gCost) {
                    neighbor.gCost = tentativeGCost;
                    neighbor.hCost = Math.abs(neighbor.x - endNode.x) + Math.abs(neighbor.y - endNode.y); // distância de Manhattan
                    neighbor.parent = current;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // caminho vazio se não encontrar
    }

    private List<Node> reconstructPath(Node endNode) {
        List<Node> path = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            path.add(current);
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {
            {0, 1},   // cima
            {1, 0},   // direita
            {0, -1},  // baixo
            {-1, 0}   // esquerda
        };

        for (int[] dir : directions) {
            int newX = node.x + dir[0];
            int newY = node.y + dir[1];

            if (newX >= 0 && newX < cols && newY >= 0 && newY < rows && map[newY][newX] == 1) {
                neighbors.add(new Node(newX, newY));
            }
        }

        return neighbors;
    }
}
