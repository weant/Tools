package com.hcop.otn.common.internal.xos.request;

import com.lucent.oms.xml.naInterface.types.DsPMdata_PMDirection_T;
import com.lucent.oms.xml.naInterface.types.DsPMdata_PMLocation_T;
import com.lucent.oms.xml.naInterface.types.GranularityDefs_Granularity_T;

public class PmpPropertyConvert {

	public static DsPMdata_PMLocation_T toXosLocation(int pmpLocation) {

		return toXosLocation(PMP.Location.values()[pmpLocation]);
	}

	public static DsPMdata_PMLocation_T toXosLocation(PMP.Location pmpLocation) {

		if (pmpLocation.equals(PMP.Location.PML_NEAR_END_Rx))
			return DsPMdata_PMLocation_T.PML_NEAR_END_RX;
		else if (pmpLocation.equals(PMP.Location.PML_FAR_END_Rx))
			return DsPMdata_PMLocation_T.PML_FAR_END_RX;
		else if (pmpLocation.equals(PMP.Location.PML_FORWARD))
			return DsPMdata_PMLocation_T.PML_FORWARD;
		else if (pmpLocation.equals(PMP.Location.PML_BACKWARD))
			return DsPMdata_PMLocation_T.PML_BACKWARD;
		else if (pmpLocation.equals(PMP.Location.PML_BIDIRECTIONAL))
			return DsPMdata_PMLocation_T.PML_BIDIRECTIONAL;
		else if (pmpLocation.equals(PMP.Location.PML_NEAR_END))
			return DsPMdata_PMLocation_T.PML_NEAR_END;
		else if (pmpLocation.equals(PMP.Location.PML_FAR_END))
			return DsPMdata_PMLocation_T.PML_FAR_END;
		else if (pmpLocation.equals(PMP.Location.PML_ALL))
			return DsPMdata_PMLocation_T.PML_ALL;
		else
			return DsPMdata_PMLocation_T.PML_NA;
	}

	public static GranularityDefs_Granularity_T toXosGranularity(int granularity) {

		return toXosGranularity(PMP.Granularity.values()[granularity]);
	}

	public static GranularityDefs_Granularity_T toXosGranularity(
			PMP.Granularity granularity) {

		if (granularity.equals(PMP.Granularity.GRN_15M))
			return GranularityDefs_Granularity_T.GRN_15M;
		// else if(granularity.equals(PMP.Granularity.GRN_1H))
		// return GranularityDefs_Granularity_T.GRN_1H;
		else if (granularity.equals(PMP.Granularity.GRN_24H))
			return GranularityDefs_Granularity_T.GRN_24H;
		else if (granularity.equals(PMP.Granularity.GRN_BOTH))
			return GranularityDefs_Granularity_T.GRN_BOTH;
		else
			return GranularityDefs_Granularity_T.GRN_NA;
	}

	public static DsPMdata_PMDirection_T toXosDirectionList(int direction) {

		return toXosDirectionList(PMP.Direction.values()[direction]);
	}

	public static DsPMdata_PMDirection_T toXosDirectionList(
			PMP.Direction direction) {

		if (direction.equals(PMP.Direction.PMD_RVC))
			return DsPMdata_PMDirection_T.PMD_RVC;
		else if (direction.equals(PMP.Direction.PMD_TRMT))
			return DsPMdata_PMDirection_T.PMD_TRMT;
		else if (direction.equals(PMP.Direction.PMD_ALL))
			return DsPMdata_PMDirection_T.PMD_ALL;
		else
			return DsPMdata_PMDirection_T.PMD_NA;
	}
}
