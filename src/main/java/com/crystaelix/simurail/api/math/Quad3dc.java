package com.crystaelix.simurail.api.math;

import org.joml.Quaterniondc;
import org.joml.Vector3dc;

import dev.ryanhcode.sable.companion.math.Pose3dc;

public interface Quad3dc {

	Vector3dc v0();

	Vector3dc v1();

	Vector3dc v2();

	Vector3dc v3();

	Quad3d transformPosition(Pose3dc pose, Quad3d dest);

	Quad3d transformPositionInverse(Pose3dc pose, Quad3d dest);

	Quad3d transformNormal(Pose3dc pose, Quad3d dest);

	Quad3d transformNormalInverse(Pose3dc pose, Quad3d dest);

	Quad3d transformNormal(Quaterniondc quat, Quad3d dest);

	Quad3d transformNormalInverse(Quaterniondc quat, Quad3d dest);

	Quad3d add(Vector3dc vec, Quad3d dest);

	Quad3d add(double x, double y, double z, Quad3d dest);

	Quad3d sub(Vector3dc vec, Quad3d dest);

	Quad3d sub(double x, double y, double z, Quad3d dest);
}
