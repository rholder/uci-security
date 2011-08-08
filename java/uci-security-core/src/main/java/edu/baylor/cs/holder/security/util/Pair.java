package edu.baylor.cs.holder.security.util;

/**
 * This is a simple pair utility class.
 */
public class Pair<X, Y> {

    public X x;

    public Y y;

    public Pair() {

    }

    public Pair(X a, Y b) {
        super();
        this.x = a;
        this.y = b;
    }

    public static <A, B> Pair<A, B> makePair(A a, B b) {
        return new Pair<A, B>(a, b);
    }

    public X getX() {
        return x;
    }

    public void setX(X a) {
        this.x = a;
    }

    public Y getY() {
        return y;
    }

    public void setY(Y b) {
        this.y = b;
    }

    @Override
    public String toString() {
        return String.format("<%s,%s>", x, y);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((x == null) ? 0 : x.hashCode());
        result = PRIME * result + ((y == null) ? 0 : y.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Pair other = (Pair) obj;
        if (x == null) {
            if (other.x != null)
                return false;
        } else if (!x.equals(other.x))
            return false;
        if (y == null) {
            if (other.y != null)
                return false;
        } else if (!y.equals(other.y))
            return false;
        return true;
    }
}
