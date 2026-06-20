package com.crystaelix.simurail.api.track;

import java.util.HashMap;
import java.util.Map;

import com.crystaelix.simurail.content.SimurailTracks;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;

public class TrackTypeEntries {

	private static final Map<TrackType, TrackTypeEntry> TRACK_TYPES = new HashMap<>();

	public static void addEntry(TrackTypeEntry entry) {
		TRACK_TYPES.put(entry.trackType(), entry);
	}

	public static TrackTypeEntry getEntry(TrackType trackType) {
		if(TRACK_TYPES.containsKey(trackType)) {
			return TRACK_TYPES.get(trackType);
		}
		return SimurailTracks.STANDARD;
	}

	public static TrackTypeEntry getEntry(TrackMaterial trackMaterial) {
		return getEntry(TrackTypeOverrides.getTrackType(trackMaterial));
	}
}
