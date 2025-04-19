package com.bd2r.harrypotter;

public class Node {
    public int x, y;
    public float gCost, hCost;
    public Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public float getFCost() {
        return gCost + hCost;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node other = (Node) obj;
            return this.x == other.x && this.y == other.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }
}
