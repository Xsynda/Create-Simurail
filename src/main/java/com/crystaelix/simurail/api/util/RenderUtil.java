package com.crystaelix.simurail.api.util;

import org.joml.Vector3dc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class RenderUtil {

	public static void renderSpriteQuad(
			PoseStack poseStack, VertexConsumer vb, TextureAtlasSprite sprite,
			Vector3dc vt0, Vector3dc vt1, Vector3dc vt2, Vector3dc vt3,
			float u, float v, float w, float h,
			int light, int overlay,
			Vector3dc normal) {
		Pose pose = poseStack.last();
		vb.
		addVertex(pose.pose(), (float)vt0.x(), (float)vt0.y(), (float)vt0.z()).
		setColor(-1).
		setUv(sprite.getU(u), sprite.getV(v)).
		setOverlay(overlay).
		setLight(light).
		setNormal(pose, (float)normal.x(), (float)normal.y(), (float)normal.z());
		vb.
		addVertex(pose.pose(), (float)vt1.x(), (float)vt1.y(), (float)vt1.z()).
		setColor(-1).
		setUv(sprite.getU(u), sprite.getV(v + h)).
		setOverlay(overlay).
		setLight(light).
		setNormal(pose, (float)normal.x(), (float)normal.y(), (float)normal.z());
		vb.
		addVertex(pose.pose(), (float)vt2.x(), (float)vt2.y(), (float)vt2.z()).
		setColor(-1).
		setUv(sprite.getU(u + w), sprite.getV(v + h)).
		setOverlay(overlay).
		setLight(light).
		setNormal(pose, (float)normal.x(), (float)normal.y(), (float)normal.z());
		vb.
		addVertex(pose.pose(), (float)vt3.x(), (float)vt3.y(), (float)vt3.z()).
		setColor(-1).
		setUv(sprite.getU(u + w), sprite.getV(v)).
		setOverlay(overlay).
		setLight(light).
		setNormal(pose, (float)normal.x(), (float)normal.y(), (float)normal.z());
	}
}
