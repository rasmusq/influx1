package com.ralleq.influx.math;

import android.graphics.RectF;

import androidx.annotation.NonNull;

public class Vector {
	
	double x, y;
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public Vector() {
		x = 0;
		y = 0;
	}

	public Vector add(Vector other) {
		return new Vector(getX() + other.getX(), getY() + other.getY());
	}
	public Vector subtract(Vector other) {
		return new Vector(getX() - other.getX(), getY() - other.getY());
	}
	public double getLength() {
		return (float) Math.sqrt(x*x+y*y);
	}
	public Vector forceLength(double newLength) {
		double scalar = newLength/getLength();
		return scaleLength(scalar);
	}
	public Vector scaleLength(double scalar) {
		return new Vector(x*scalar, y*scalar);
	}
	public Vector invert() {
		return new Vector(-x, -y);
	}
	public double dotProduct(Vector other) {
		return getX()*other.getX() + getY()*other.getY();
	}
	public double determinant(Vector other) {
		return getX()*other.getY() - getX()*other.getY();
	}
	
	public boolean isInsideRect(RectF rect) {
		return rect.contains((float)x, (float)y);
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public void set(Vector other) {
		this.x = other.getX();
		this.y = other.getY();
	}

	@NonNull
	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
