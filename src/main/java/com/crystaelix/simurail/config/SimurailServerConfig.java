package com.crystaelix.simurail.config;

import java.util.function.Supplier;

import net.createmod.catnip.config.ConfigBase;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

public class SimurailServerConfig extends SimurailBaseConfig {

	public final SimurailBlocksConfig blocks;
	public final SimurailPhysicsConfig physics;
	public final SimurailCompatConfig compat;

	public SimurailServerConfig() {
		blocks = nested(0, SimurailBlocksConfig::new, Comments.blocks);
		physics = nested(0, SimurailPhysicsConfig::new, Comments.physics);
		compat = nested(0, SimurailCompatConfig::new, Comments.compat);
	}

	@Override
	public String getName() {
		return "server";
	}

	static class Comments {
		static String blocks = "Parameters of Simurail block behaviors";
		static String physics = "Parameters of Simurail physics behaviors";
		static String compat = "Parameters of Simurail compat behaviors";
	}
}
