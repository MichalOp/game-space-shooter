package com.codingame.game;

public class Vector2d {
    public double x;
    public double y;

    public Vector2d(){
        x = 0;
        y = 0;
    }

    public Vector2d(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double length(){
        return Math.sqrt(x*x + y*y);
    }

    public Vector2d clip(double magnitude){
        double len = this.length();
        if(len > magnitude){
            return mul(magnitude/len);
        }
        else return this;
    }

    public Vector2d mul(double s){
        return new Vector2d(x*s, y*s);
    }

    public Vector2d add(Vector2d v2){
        return new Vector2d(x + v2.x, y + v2.y);
    }

    public static Vector2d zero = new Vector2d(0,0);

    public double distance(Vector2d v2){
        return (add(v2.mul(-1.0))).length();
    }

    @Override
    public String toString(){
        return "position: ("+String.format("%.0f", x)+", "+ String.format("%.0f", y)+")";
    }
}
