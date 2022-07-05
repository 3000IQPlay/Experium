package dev._3000IQPlay.experium.util;

public class PairUtil<F, S> {
    private F first;
    private S second;

    public PairUtil(F f, S s) {
        this.first = f;
        this.second = s;
    }

    public F getFirst() {
        return this.first;
    }

    public void setFirst(F f) {
        this.first = f;
    }

    public S getSecond() {
        return this.second;
    }

    public void setSecond(S s) {
        this.second = s;
    }
}

