package com.hcop.ptn.common.internal.xos.response.extractors;

import com.alu.tools.basic.NullUtil;
import com.alu.tools.basic.exception.TransformException;
import com.alu.tools.basic.function.ITransformer;
import com.alu.tools.basic.function.TransformerUtil;
import com.hcop.ptn.common.internal.xos.request.GetAllPMPRequest;
import com.hcop.ptn.common.internal.xos.request.XosConstants;
import com.hcop.ptn.common.internal.xos.request.PMP;
import com.hcop.ptn.common.internal.xos.request.PMP.Location;
import com.hcop.ptn.common.internal.xos.request.PMP.Direction;
import com.hcop.ptn.common.internal.xos.request.PMP.Granularity;
import com.lucent.oms.xml.naInterface.*;
import com.lucent.oms.xml.naInterface.types.DsPMdata_PMDirection_T;
import com.lucent.oms.xml.naInterface.types.DsPMdata_PMLocation_T;
import com.lucent.oms.xml.naInterface.types.GranularityDefs_Granularity_T;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GetAllPMPExtractor extends
		AbstractListDataExtractor<PMP, DsPMdata_PMTPSelectList_T> {

	private int groupId;

	private int emlId;

	private String neName;

	private final static String DELIMITER_COMMA = "$";
	
	private static Log log = LogFactory.getLog(GetAllPMPExtractor.class);

	public GetAllPMPExtractor(int groupId, int emlId, String neName) {
		this.groupId = groupId;
		this.emlId = emlId;
		this.neName = neName;
	}

	@Override
	protected DsPMdata_PMTPSelectList_T getList(Message_T response) {
		log.debug("++++Response:" + response.toString());
		log.debug("++++OpName:" +response.getHdr().getOpName());
		if (response.getHdr().getOpName().equals(GetAllPMPRequest.REQ_OP_NAME)) {
			return response.getPerfMgr_I_GetAllPMTPresp().getPMTPList();
		}
		if (response.getHdr().getOpName()
				.equals(GetAllPMPRequest.NEXT_REQ_OP_NAME))
			return response.getPerfMgr_I_GetAllPMTPnext_Nresp().getPmDataList();
		return null;
	}

	@Override
	protected PMP[] getDataArray(DsPMdata_PMTPSelectList_T list) {
		log.debug("++++DsPMdata_PMTPSelectList_T size:" + list.getDsPMdata_PMTPSelectList_TelemCount());
		return TransformerUtil.transform(list
				.getDsPMdata_PMTPSelectList_Telem(), new PmTpConverter(groupId,
				emlId, neName), PMP.class);
	}

	private static class PmTpConverter implements
			ITransformer<DsPMdata_PMTPselect_T, PMP> {
		private int groupId;
		private int emlId;
		private String neName;

		public PmTpConverter(int groupId, int emlId, String neName) {
			this.groupId = groupId;
			this.emlId = emlId;
			this.neName = neName;
		}

		@Override
		public PMP transform(DsPMdata_PMTPselect_T pmTp)
				throws TransformException {
			PMP pmp = new PMP(groupId, emlId, neName, getPtpName(pmTp),
					getFtpName(pmTp), getCtpName(pmTp), getValue(
							pmTp.getAdditionalInfo(),
							XosConstants.PMP.PMTP_NATIVE_NAME),
					getLayerRate(pmTp), getLocation(pmTp), getDirection(pmTp),
					getGranularity(pmTp));
			pmp.setTcmName(pmTp.getName().getTcmName() == null ? "" : pmTp
					.getName().getTcmName());
			log.debug("-----++++PMP:" + pmp.toString());
			return pmp;
		}

		private String getValue(GlobalDefs_NVSList_T list, String name) {
			for (GlobalDefs_NameAndValueString_T item : list
					.getGlobalDefs_NVSList_Telem()) {
				if (item.getName().equals(name))
					return item.getValue();
			}
			return null;
		}

		private String getPtpName(DsPMdata_PMTPselect_T pmTp) {
			return NullUtil.nullToEmpty(pmTp.getName().getPtpName());
		}

		private String getFtpName(DsPMdata_PMTPselect_T pmTp) {
			return NullUtil.nullToEmpty(pmTp.getName().getFtpName());
		}

		private String getCtpName(DsPMdata_PMTPselect_T pmTp) {
			return NullUtil.nullToEmpty(pmTp.getName().getCtpName());
		}

		private short getLayerRate(DsPMdata_PMTPselect_T pmTp) {
			if (pmTp.getLayerRateList().getLayerDefs_LayerRateList_TelemCount() == 0)
				return -1;
			return pmTp.getLayerRateList().getLayerDefs_LayerRateList_Telem(0);
		}

		private Location getLocation(DsPMdata_PMTPselect_T pmTp) {
			if (pmTp.getPmLocationList()
					.getDsPMdata_PMLocationList_TelemCount() == 0)
				return Location.PML_NA;
			return toLocation(pmTp.getPmLocationList()
					.getDsPMdata_PMLocationList_Telem(0));
		}

		private Location toLocation(DsPMdata_PMLocation_T location) {
			switch (location.getType()) {
			case DsPMdata_PMLocation_T.PML_NEAR_END_TYPE:
				return Location.PML_NEAR_END;
			case DsPMdata_PMLocation_T.PML_NEAR_END_RX_TYPE:
				return Location.PML_NEAR_END_Rx;
			case DsPMdata_PMLocation_T.PML_FAR_END_TYPE:
				return Location.PML_FAR_END;
			case DsPMdata_PMLocation_T.PML_FAR_END_RX_TYPE:
				return Location.PML_FAR_END_Rx;
			case DsPMdata_PMLocation_T.PML_BIDIRECTIONAL_TYPE:
				return Location.PML_BIDIRECTIONAL;
			case DsPMdata_PMLocation_T.PML_BACKWARD_TYPE:
				return Location.PML_BACKWARD;
			case DsPMdata_PMLocation_T.PML_FORWARD_TYPE:
				return Location.PML_FORWARD;
			default:
				return Location.PML_NA;
			}
		}

		private Direction getDirection(DsPMdata_PMTPselect_T pmTp) {
			if (pmTp.getPmDirectionList()
					.getDsPMdata_PMDirectionList_TelemCount() == 0)
				return Direction.PMD_NA;
			return toDirection(pmTp.getPmDirectionList()
					.getDsPMdata_PMDirectionList_Telem(0));
		}

		private Direction toDirection(DsPMdata_PMDirection_T direction) {
			switch (direction.getType()) {
			case DsPMdata_PMDirection_T.PMD_RVC_TYPE:
				return Direction.PMD_RVC;
			case DsPMdata_PMDirection_T.PMD_TRMT_TYPE:
				return Direction.PMD_TRMT;
			default:
				return Direction.PMD_NA;
			}
		}

		private Granularity getGranularity(DsPMdata_PMTPselect_T pmTp) {
			if (pmTp.getGranularityList()
					.getDsPMdata_GranularityList_TelemCount() == 0)
				return Granularity.GRN_NA;
			return toGranularity(pmTp.getGranularityList()
					.getDsPMdata_GranularityList_Telem(0));
		}

		private Granularity toGranularity(
				GranularityDefs_Granularity_T granularity) {
			switch (granularity.getType()) {
			case GranularityDefs_Granularity_T.GRN_15M_TYPE:
				return Granularity.GRN_15M;
			case GranularityDefs_Granularity_T.GRN_24H_TYPE:
				return Granularity.GRN_24H;
			default:
				return Granularity.GRN_NA;
			}
		}

	}

}
