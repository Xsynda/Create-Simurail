package com.crystaelix.simurail.content;

import com.crystaelix.simurail.api.track.TrackTypeEntries;
import com.crystaelix.simurail.api.track.TrackTypeEntry;
import com.crystaelix.simurail.config.SimurailConfig;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import net.minecraft.network.chat.Component;

public class SimurailTracks {

	public static final TrackTypeEntry
	STANDARD = new TrackTypeEntry(
			TrackType.STANDARD,
			Component.translatable("simurail_track_type.create.standard"),
			Component.translatable("simurail_track_type.create.standard.short"),
			false,
			SimurailConfig.SERVER.physics.axleStandardLateralMaxSpeedFactor::get,
			SimurailConfig.SERVER.physics.axleStandardVerticalMaxSpeedFactor::get,
			AllBlocks.TRACK::getDefaultState);

	public static void register() {
		TrackTypeEntries.addEntry(STANDARD);
	}
}
