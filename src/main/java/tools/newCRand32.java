/*
 * Decompiled with CFR 0.152.
 */
package tools;

import tools.Randomizer;

public final class newCRand32 {
    private int seed1;
    private int seed2;
    private int seed3;
    private int counter;

    public newCRand32() {
        int nextInt = Randomizer.nextInt();
        this.seed(nextInt, nextInt, nextInt);
    }

    public long random() {
        ++this.counter;
        int v5 = this.seed3;
        int v6 = this.seed2;
        int part1 = v5 & 0x7FFC0;
        int part2 = v6 & 0x1FFFFF00;
        int combined = v6 ^ (this.seed1 ^ (this.seed1 >> 2 ^ this.seed1 & 0x3F800000) >> 4);
        int mixed = (part2 ^ combined >> 8) >> 3;
        int v7 = (part1 ^ mixed) >> 2 >> 6;
        int v8 = ((v6 & 0xFFFFFFF0) << 13 ^ (this.seed1 ^ (v5 & 0xFFFFFFFE) << 8)) & 0xFFFFFFF8;
        int result = 16 * v8 ^ v7;
        return (long)result & 0xFFFFFFFFL;
    }

    public void seed(int n, int n2, int n3) {
        this.seed1 = n | 0x100000;
        this.seed2 = n2 | 0x1000;
        this.seed3 = n3 | 0x10;
        this.counter = 0;
    }
}

