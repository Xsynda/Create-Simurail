package com.crystaelix.simurail.api.math;

import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import dev.ryanhcode.sable.companion.math.Pose3dc;

public interface CubicBezier3dc {

	Vector3dc end1();

	Vector3dc end2();

	Vector3dc handle1();

	Vector3dc handle2();

	CubicBezier3d transformPosition(Pose3dc pose, CubicBezier3d dest);

	CubicBezier3d transformPositionInverse(Pose3dc pose, CubicBezier3d dest);

	CubicBezier3d transformNormal(Pose3dc pose, CubicBezier3d dest);

	CubicBezier3d transformNormalInverse(Pose3dc pose, CubicBezier3d dest);

	CubicBezier3d transformNormal(Quaterniondc quat, CubicBezier3d dest);

	CubicBezier3d transformNormalInverse(Quaterniondc quat, CubicBezier3d dest);

	CubicBezier3d reverse(CubicBezier3d dest);

	CubicBezier3d add(Vector3dc vec, CubicBezier3d dest);

	CubicBezier3d sub(Vector3dc vec, CubicBezier3d dest);

	double length(double t0, double t1);

	Vector3d position(double t, Vector3d dest);

	double position(double t, int component);

	Vector3d velocity(double t, Vector3d dest);

	double velocity(double t, int component);

	Vector3d acceleration(double t, Vector3d dest);

	double acceleration(double t, int component);

	Vector3d curvature(double t, Vector3d dest);

	double[] lengthLUT(double t0, double t1, double[] dest);
}
