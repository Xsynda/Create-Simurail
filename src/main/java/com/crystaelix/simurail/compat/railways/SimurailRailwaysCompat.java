package com.crystaelix.simurail.compat.railways;

public class SimurailRailwaysCompat {

	public static void onCommonSetupLate() {
		RailwaysBogeys.register();
		RailwaysTracks.register();
	}
}
