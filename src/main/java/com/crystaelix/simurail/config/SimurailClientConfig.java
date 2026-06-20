package com.crystaelix.simurail.config;

import java.util.function.Supplier;

import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.config.ConfigBase.CValue;
import net.createmod.catnip.config.ConfigBase.ConfigGroup;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;

public class SimurailClientConfig extends SimurailBaseConfig {

	public SimurailClientConfig() {
	}

	@Override
	public String getName() {
		return "client";
	}
}
