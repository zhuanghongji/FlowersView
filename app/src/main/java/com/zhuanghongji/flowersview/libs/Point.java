package com.zhuanghongji.flowersview.libs;

/**
 * 绘制点
 *
 * @author zhuanghongji
 */
public class Point {

    float x;

    float y;

    Point() {}

    Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    int getX() {
        return (int) x;
    }

    int getY() {
        return (int) y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
