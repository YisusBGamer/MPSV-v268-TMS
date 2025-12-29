/*
 * Decompiled with CFR 0.152.
 */
package tools;

import java.io.Serializable;

public class Quadruple<E, F, G, H>
implements Serializable {
    private static final long serialVersionUID = 9179541993413749999L;
    public final E one;
    public final F two;
    public final G three;
    public final H four;

    public Quadruple(E one, F two, G three, H four) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    public E getOne() {
        return this.one;
    }

    public F getTwo() {
        return this.two;
    }

    public G getThree() {
        return this.three;
    }

    public H getFour() {
        return this.four;
    }

    public String toString() {
        return this.one.toString() + ":" + this.two.toString() + ":" + this.three.toString() + ":" + this.four.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.one == null ? 0 : this.one.hashCode());
        result = 31 * result + (this.two == null ? 0 : this.two.hashCode());
        result = 31 * result + (this.three == null ? 0 : this.three.hashCode());
        result = 31 * result + (this.four == null ? 0 : this.four.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Quadruple other = (Quadruple)obj;
        if (this.one == null ? other.one != null : !this.one.equals(other.one)) {
            return false;
        }
        if (this.two == null ? other.two != null : !this.two.equals(other.two)) {
            return false;
        }
        if (this.three == null ? other.three != null : !this.three.equals(other.three)) {
            return false;
        }
        if (this.four == null) {
            return other.four == null;
        }
        return this.four.equals(other.four);
    }
}

