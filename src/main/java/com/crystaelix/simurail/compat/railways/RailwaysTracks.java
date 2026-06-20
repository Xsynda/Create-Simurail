package com.crystaelix.simurail.compat.railways;

import com.crystaelix.simurail.api.track.TrackTypeEntries;
import com.crystaelix.simurail.api.track.TrackTypeEntry;
import com.crystaelix.simurail.api.track.TrackTypeOverrides;
import com.crystaelix.simurail.config.SimurailConfig;
import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

import net.minecraft.network.chat.Component;

public class RailwaysTracks {

	public static final TrackTypeEntry
	MONORAIL = new TrackTypeEntry(
			CRTrackType.MONORAIL,
			Component.translatable("simurail_track_type.railways.monorail"),
			Component.translatable("simurail_track_type.railways.monorail.short"),
			true,
			SimurailConfig.SERVER.compat.axleMonorailLateralMaxSpeedFactor::get,
			SimurailConfig.SERVER.compat.axleMonorailVerticalMaxSpeedFactor::get,
			CRBlocks.MONORAIL_TRACK::getDefaultState),
	NARROW = new TrackTypeEntry(
			CRTrackType.NARROW_GAUGE,
			Component.translatable("simurail_track_type.railways.narrow"),
			Component.translatable("simurail_track_type.railways.narrow.short"),
			false,
			SimurailConfig.SERVER.compat.axleNarrowLateralMaxSpeedFactor::get,
			SimurailConfig.SERVER.compat.axleNarrowVerticalMaxSpeedFactor::get,
			() -> CRTrackMaterials.NARROW_GAUGE_ANDESITE.getBlockSupplier().get().defaultBlockState()),
	WIDE = new TrackTypeEntry(
			CRTrackType.WIDE_GAUGE,
			Component.translatable("simurail_track_type.railways.wide"),
			Component.translatable("simurail_track_type.railways.wide.short"),
			false,
			SimurailConfig.SERVER.compat.axleWideLateralMaxSpeedFactor::get,
			SimurailConfig.SERVER.compat.axleWideVerticalMaxSpeedFactor::get,
			() -> CRTrackMaterials.WIDE_GAUGE_ANDESITE.getBlockSupplier().get().defaultBlockState());

	public static void register() {
		TrackTypeEntries.addEntry(MONORAIL);
		TrackTypeEntries.addEntry(NARROW);
		TrackTypeEntries.addEntry(WIDE);

		TrackTypeOverrides.setOverride(CRTrackMaterials.PHANTOM, TrackType.STANDARD);
		TrackTypeOverrides.setUniversal(CRTrackType.UNIVERSAL);
	}
}
