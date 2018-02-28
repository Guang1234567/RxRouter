package com.github.router;

/**
 * @author Guang1234567
 * @date 2018/2/27 11:15
 */

class Request<T> {
    private T mTo;

    public Request(T to) {
        mTo = to;
    }

    public T getTo() {
        return mTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;

        Request that = (Request) o;

        return mTo != null ? mTo.equals(that.mTo) : that.mTo == null;
    }

    @Override
    public int hashCode() {
        return mTo != null ? mTo.hashCode() : 0;
    }
}
