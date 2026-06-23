package com.crystaelix.simurail.api.math;

import org.joml.Vector3d;
import org.joml.Vector3dc;

public record BezierControlPoints(Vector3dc end1, Vector3dc end2, Vector3dc handle1, Vector3dc handle2) {

	public BezierControlPoints(Vector3dc end1, Vector3dc end2, Vector3dc dir1, Vector3dc dir2, double handleLength) {
		this(end1, end2, new Vector3d(dir1).normalize(handleLength).add(end1), new Vector3d(dir2).normalize(handleLength).add(end2));
	}
}
