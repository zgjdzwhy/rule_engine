package com.mobanker.engine.common.sortcollection;

import java.util.Comparator;

public class TimeComparator implements Comparator<TimeCompare> {

	@Override
	public int compare(TimeCompare o1, TimeCompare o2) {
		return o1.getTimeFeature().compareTo(o2.getTimeFeature());
	}

}
