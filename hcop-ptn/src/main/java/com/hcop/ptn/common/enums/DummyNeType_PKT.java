
package com.hcop.ptn.common.enums;

import java.util.ArrayList;
import java.util.List;
/**
 * Enum for netAnalysis
 *
 */
public enum DummyNeType_PKT {
	DUMMY_NE_TYPE_NONE, // 0
	DUMMY_NE_TYPE_ISA_PREA_S1P, // 1
	DUMMY_NE_TYPE_ISA_PREA_4ETH, // 2
	DUMMY_NE_TYPE_ISA_PREA_1GBEP, // 3
	DUMMY_NE_TYPE_ISA_E_FE, // 4
	DUMMY_NE_TYPE_ISA_GE_RA, // 5
	DUMMY_NE_TYPE_ISA_GE_TR, // 6
	DUMMY_NE_TYPE_ISA_PR, // 7
	DUMMY_NE_TYPE_ISA_ES1_3FE, // 8
	DUMMY_NE_TYPE_ISA_ES1_8FE, // 9
	DUMMY_NE_TYPE_ISA_ES1_8FX, // 10
	DUMMY_NE_TYPE_ISA_ES4_8FE, // 11
	DUMMY_NE_TYPE_ISA_ES16, // 12
	DUMMY_NE_TYPE_1850TSS_320, // 13
	DUMMY_NE_TYPE_ISA_BCE_ETH, // 14
	DUMMY_NE_TYPE_ISA_BCE_ETHT, // 15
	DUMMY_NE_TYPE_1850TSS_100, // 16
	DUMMY_NE_TYPE_1850TSS_15, // 17
	DUMMY_NE_TYPE_1850TSS_3, // 18
	DUMMY_NE_TYPE_ISA_ES64, // 19
	DUMMY_NE_TYPE_ISA_10GE, // 20
	DUMMY_NE_TYPE_1643_AM, // 21
	DUMMY_NE_TYPE_1643_AMS, // 22
	DUMMY_NE_TYPE_1645_AMC, // 23
	DUMMY_NE_TYPE_1655_AMU, // 24
	DUMMY_NE_TYPE_1663_ADMU, // 25
	DUMMY_NE_TYPE_1675_LU, // 26
	DUMMY_NE_TYPE_ADM_16_1, // 27
	DUMMY_NE_TYPE_ADM_4_1, // 28
	DUMMY_NE_TYPE_ADM_C, // 29
	DUMMY_NE_TYPE_TDM_10G, // 30
	DUMMY_NE_TYPE_1850TSS_160, // 31
	DUMMY_NE_TYPE_ISA_ES4E, // 32
	DUMMY_NE_TYPE_1850TSS_5_EIU_40, // 33
	DUMMY_NE_TYPE_1850TSS_5_EIU_41, // 34
	DUMMY_NE_TYPE_1850TSS_5_EIU_42, // 35
	DUMMY_NE_TYPE_1850TSS_5_EIU_42B, // 36
	DUMMY_NE_TYPE_1850TSS_5_TIU_60, // 37
	DUMMY_NE_TYPE_1850TSS_5_TIU_61, // 38
	DUMMY_NE_TYPE_1850TSS_5_TIU_62, // 39
	DUMMY_NE_TYPE_1850TSS_5_TIU_64, // 40
	DUMMY_NE_TYPE_1850TSS_5_SIU_35, // 41
	DUMMY_NE_TYPE_1850TSS_5_SIU_50, // 42
	DUMMY_NE_TYPE_1850TSS_5_SIU_52, // 43
	DUMMY_NE_TYPE_1850TSS_40, // 44
	DUMMY_NE_TYPE_9500MPR, // 45
	DUMMY_NE_TYPE_1850TSS_10, // 46
	DUMMY_NE_TYPE_1850TSS_160C, // 47
	DUMMY_NE_TYPE_1850TSS_5C, // 48
	DUMMY_NE_TYPE_1646CPE, // 49
	DUMMY_NE_TYPE_1540NT, // 50
	DUMMY_NE_TYPE_1540POP, // 51
	DUMMY_NE_TYPE_1540SDH, // 52
	DUMMY_NE_TYPE_1631FOX, // 53
	DUMMY_NE_TYPE_1640FOX, // 53
	DUMMY_NE_TYPE_1641SM, // 55
	DUMMY_NE_TYPE_1641SMC, // 56
	DUMMY_NE_TYPE_1641SMT, // 57
	DUMMY_NE_TYPE_1641SX, // 58
	DUMMY_NE_TYPE_1642EM, // 59
	DUMMY_NE_TYPE_1642EMC, // 60
	DUMMY_NE_TYPE_1642EMUX, // 61
	DUMMY_NE_TYPE_1650SMC, // 62
	DUMMY_NE_TYPE_1651SM, // 63
	DUMMY_NE_TYPE_1651SMC, // 64
	DUMMY_NE_TYPE_1654SL, // 65
	DUMMY_NE_TYPE_1660SM, // 66
	DUMMY_NE_TYPE_1661SMC, // 67
	DUMMY_NE_TYPE_1664SL, // 68
	DUMMY_NE_TYPE_1664SM, // 69
	DUMMY_NE_TYPE_1664SX, // 70
	DUMMY_NE_TYPE_1665DMX, // 71
	DUMMY_NE_TYPE_1665DMXplore, // 71
	DUMMY_NE_TYPE_1665DMXtend, // 73
	DUMMY_NE_TYPE_1670SM, // 74
	DUMMY_NE_TYPE_1674LG, // 75
	DUMMY_NE_TYPE_1678ADM, // 76
	DUMMY_NE_TYPE_1678MCC, // 77
	DUMMY_NE_TYPE_1680SM, // 78
	DUMMY_NE_TYPE_1662SMC, // 79
	DUMMY_NE_TYPE_1850TSS_5R, // 80
	DUMMY_NE_TYPE_EXTERNAL, // 81
	DUMMY_NE_TYPE_1850TSS_320H,//87
	DUMMY_NE_TYPE_1850TSS_160H;//88

	public static List<DummyNeType_PKT> getDummyNeType_PKT_List(){
	    List<DummyNeType_PKT> typeData = new ArrayList<DummyNeType_PKT>();
	    //添加8,9,10,11,12作为深蓝客户时出现带#号网元
		//2019-2-21,移除9,10,11,12，目前接口不处理这些网元
	    int[] types = {13,16,17,18,31,33,34,35,36,37,38,39,40,41,42,43,44,46,47,48,80,87,88};
	    for(int value:types){
	    	typeData.add(DummyNeType_PKT.fromInt(value));
	    }
	    return typeData;
	}
	
	public static List<DummyNeType_PKT> getDummyNeType_PKT_List_4search(){
	    List<DummyNeType_PKT> typeData = new ArrayList<DummyNeType_PKT>();
	    int[] types = {0,13,16,17,18,31,33,34,35,36,37,38,39,40,41,42,43,44,46,47,48,80,87,88};
	    for(int value:types){
	    	typeData.add(DummyNeType_PKT.fromInt(value));
	    }
	    return typeData;
	}
	
	public static DummyNeType_PKT fromInt(Integer value) {
		switch (value) {
		case 0:
			return DUMMY_NE_TYPE_NONE;
		case 1:
			return DUMMY_NE_TYPE_ISA_PREA_S1P;
		case 2:
			return DUMMY_NE_TYPE_ISA_PREA_4ETH;
		case 3:
			return DUMMY_NE_TYPE_ISA_PREA_1GBEP;
		case 4:
			return DUMMY_NE_TYPE_ISA_E_FE;
		case 5:
			return DUMMY_NE_TYPE_ISA_GE_RA;
		case 6:
			return DUMMY_NE_TYPE_ISA_GE_TR;
		case 7:
			return DUMMY_NE_TYPE_ISA_PR;
		case 8:
			return DUMMY_NE_TYPE_ISA_ES1_3FE;
		case 9:
			return DUMMY_NE_TYPE_ISA_ES1_8FE;
		case 10:
			return DUMMY_NE_TYPE_ISA_ES1_8FX;
		case 11:
			return DUMMY_NE_TYPE_ISA_ES4_8FE;
		case 12:
			return DUMMY_NE_TYPE_ISA_ES16;
		case 13:
			return DUMMY_NE_TYPE_1850TSS_320;
		case 14:
			return DUMMY_NE_TYPE_ISA_BCE_ETH;
		case 15:
			return DUMMY_NE_TYPE_ISA_BCE_ETHT; // 15
		case 16:
			return DUMMY_NE_TYPE_1850TSS_100; // 16
		case 17:
			return DUMMY_NE_TYPE_1850TSS_15; // 17
		case 18:
			return DUMMY_NE_TYPE_1850TSS_3; // 18
		case 19:
			return DUMMY_NE_TYPE_ISA_ES64; // 19
		case 20:
			return DUMMY_NE_TYPE_ISA_10GE; // 20
		case 21:
			return DUMMY_NE_TYPE_1643_AM; // 21
		case 22:
			return DUMMY_NE_TYPE_1643_AMS; // 22
		case 23:
			return DUMMY_NE_TYPE_1645_AMC; // 23
		case 24:
			return DUMMY_NE_TYPE_1655_AMU; // 24
		case 25:
			return DUMMY_NE_TYPE_1663_ADMU; // 25
		case 26:
			return DUMMY_NE_TYPE_1675_LU; // 26
		case 27:
			return DUMMY_NE_TYPE_ADM_16_1; // 27
		case 28:
			return DUMMY_NE_TYPE_ADM_4_1; // 28
		case 29:
			return DUMMY_NE_TYPE_ADM_C; // 29
		case 30:
			return DUMMY_NE_TYPE_TDM_10G; // 30
		case 31:
			return DUMMY_NE_TYPE_1850TSS_160; // 31
		case 32:
			return DUMMY_NE_TYPE_ISA_ES4E; // 32
		case 33:
			return DUMMY_NE_TYPE_1850TSS_5_EIU_40; // 33
		case 34:
			return DUMMY_NE_TYPE_1850TSS_5_EIU_41; // 34
		case 35:
			return DUMMY_NE_TYPE_1850TSS_5_EIU_42; // 35
		case 36:
			return DUMMY_NE_TYPE_1850TSS_5_EIU_42B; // 36
		case 37:
			return DUMMY_NE_TYPE_1850TSS_5_TIU_60; // 37
		case 38:
			return DUMMY_NE_TYPE_1850TSS_5_TIU_61; // 38
		case 39:
			return DUMMY_NE_TYPE_1850TSS_5_TIU_62; // 39
		case 40:
			return DUMMY_NE_TYPE_1850TSS_5_TIU_64; // 40
		case 41:
			return DUMMY_NE_TYPE_1850TSS_5_SIU_35; // 41
		case 42:
			return DUMMY_NE_TYPE_1850TSS_5_SIU_50; // 42
		case 43:
			return DUMMY_NE_TYPE_1850TSS_5_SIU_52; // 43
		case 44:
			return DUMMY_NE_TYPE_1850TSS_40; // 44
		case 45:
			return DUMMY_NE_TYPE_9500MPR; // 45
		case 46:
			return DUMMY_NE_TYPE_1850TSS_10; // 46
		case 47:
			return DUMMY_NE_TYPE_1850TSS_160C; // 47
		case 48:
			return DUMMY_NE_TYPE_1850TSS_5C; // 48
		case 49:
			return DUMMY_NE_TYPE_1646CPE; // 49
		case 50:
			return DUMMY_NE_TYPE_1540NT; // 50
		case 51:
			return DUMMY_NE_TYPE_1540POP; // 51
		case 52:
			return DUMMY_NE_TYPE_1540SDH; // 52
		case 53:
			return DUMMY_NE_TYPE_1631FOX; // 53
		case 54:
			return DUMMY_NE_TYPE_1640FOX; // 53
		case 55:
			return DUMMY_NE_TYPE_1641SM; // 55
		case 56:
			return DUMMY_NE_TYPE_1641SMC; // 56
		case 57:
			return DUMMY_NE_TYPE_1641SMT; // 57
		case 58:
			return DUMMY_NE_TYPE_1641SX; // 58
		case 59:
			return DUMMY_NE_TYPE_1642EM; // 59
		case 60:
			return DUMMY_NE_TYPE_1642EMC; // 60
		case 61:
			return DUMMY_NE_TYPE_1642EMUX; // 61
		case 62:
			return DUMMY_NE_TYPE_1650SMC; // 62
		case 63:
			return DUMMY_NE_TYPE_1651SM; // 63
		case 64:
			return DUMMY_NE_TYPE_1651SMC; // 64
		case 65:
			return DUMMY_NE_TYPE_1654SL; // 65
		case 66:
			return DUMMY_NE_TYPE_1660SM; // 66
		case 67:
			return DUMMY_NE_TYPE_1661SMC; // 67
		case 68:
			return DUMMY_NE_TYPE_1664SL; // 68
		case 69:
			return DUMMY_NE_TYPE_1664SM; // 69
		case 70:
			return DUMMY_NE_TYPE_1664SX; // 70
		case 71:
			return DUMMY_NE_TYPE_1665DMX; // 71
		case 72:
			return DUMMY_NE_TYPE_1665DMXplore; // 72
		case 73:
			return DUMMY_NE_TYPE_1665DMXtend; // 73
		case 74:
			return DUMMY_NE_TYPE_1670SM; // 74
		case 75:
			return DUMMY_NE_TYPE_1674LG; // 75
		case 76:
			return DUMMY_NE_TYPE_1678ADM; // 76
		case 77:
			return DUMMY_NE_TYPE_1678MCC; // 77
		case 78:
			return DUMMY_NE_TYPE_1680SM; // 78
		case 79:
			return DUMMY_NE_TYPE_1662SMC; // 79
		case 80:
			return DUMMY_NE_TYPE_1850TSS_5R; // 80
		case 81:
			return DUMMY_NE_TYPE_EXTERNAL; // 81
		case 87:
			return DUMMY_NE_TYPE_1850TSS_320H;//
		case 88:
			return DUMMY_NE_TYPE_1850TSS_160H;
		}
		return DUMMY_NE_TYPE_NONE;
	}


	
	public int toValue() {
		switch (this) {
		case DUMMY_NE_TYPE_NONE:
			return 0;
		case DUMMY_NE_TYPE_ISA_PREA_S1P:
			return 1;
		case DUMMY_NE_TYPE_ISA_PREA_4ETH:
			return 2;
		case DUMMY_NE_TYPE_ISA_PREA_1GBEP:
			return 3;
		case DUMMY_NE_TYPE_ISA_E_FE:
			return 4;
		case DUMMY_NE_TYPE_ISA_GE_RA:
			return 5;
		case DUMMY_NE_TYPE_ISA_GE_TR:
			return 6;
		case DUMMY_NE_TYPE_ISA_PR:
			return 7;
		case DUMMY_NE_TYPE_ISA_ES1_3FE:
			return 8;
		case DUMMY_NE_TYPE_ISA_ES1_8FE:
			return 9;
		case DUMMY_NE_TYPE_ISA_ES1_8FX:
			return 10;
		case DUMMY_NE_TYPE_ISA_ES4_8FE:
			return 11;
		case DUMMY_NE_TYPE_ISA_ES16:
			return 12;
		case DUMMY_NE_TYPE_1850TSS_320:
			return 13;
		case DUMMY_NE_TYPE_ISA_BCE_ETH:
			return 14;
		case DUMMY_NE_TYPE_ISA_BCE_ETHT:
			return 15; 
		case DUMMY_NE_TYPE_1850TSS_100:
			return 16; 
		case DUMMY_NE_TYPE_1850TSS_15:
			return 17; 
		case DUMMY_NE_TYPE_1850TSS_3:
			return 18; 
		case DUMMY_NE_TYPE_ISA_ES64:
			return 19; 
		case DUMMY_NE_TYPE_ISA_10GE:
			return 20; 
		case DUMMY_NE_TYPE_1643_AM:
			return 21; 
		case DUMMY_NE_TYPE_1643_AMS:
			return 22; 
		case DUMMY_NE_TYPE_1645_AMC:
			return 23; 
		case DUMMY_NE_TYPE_1655_AMU:
			return 24; 
		case DUMMY_NE_TYPE_1663_ADMU:
			return 25; 
		case DUMMY_NE_TYPE_1675_LU:
			return 26; 
		case DUMMY_NE_TYPE_ADM_16_1:
			return 27; 
		case DUMMY_NE_TYPE_ADM_4_1:
			return 28; 
		case DUMMY_NE_TYPE_ADM_C:
			return 29; 
		case DUMMY_NE_TYPE_TDM_10G:
			return 30; 
		case DUMMY_NE_TYPE_1850TSS_160:
			return 31; 
		case DUMMY_NE_TYPE_ISA_ES4E:
			return 32; 
		case DUMMY_NE_TYPE_1850TSS_5_EIU_40:
			return 33; 
		case DUMMY_NE_TYPE_1850TSS_5_EIU_41:
			return 34; 
		case DUMMY_NE_TYPE_1850TSS_5_EIU_42:
			return 35; 
		case DUMMY_NE_TYPE_1850TSS_5_EIU_42B:
			return 36; 
		case DUMMY_NE_TYPE_1850TSS_5_TIU_60:
			return 37; 
		case DUMMY_NE_TYPE_1850TSS_5_TIU_61:
			return 38; 
		case DUMMY_NE_TYPE_1850TSS_5_TIU_62:
			return 39; 
		case DUMMY_NE_TYPE_1850TSS_5_TIU_64:
			return 40; 
		case DUMMY_NE_TYPE_1850TSS_5_SIU_35:
			return 41; 
		case DUMMY_NE_TYPE_1850TSS_5_SIU_50:
			return 42; 
		case DUMMY_NE_TYPE_1850TSS_5_SIU_52:
			return 43; 
		case DUMMY_NE_TYPE_1850TSS_40:
			return 44; 
		case DUMMY_NE_TYPE_9500MPR:
			return 45; 
		case DUMMY_NE_TYPE_1850TSS_10:
			return 46; 
		case DUMMY_NE_TYPE_1850TSS_160C:
			return 47; 
		case DUMMY_NE_TYPE_1850TSS_5C:
			return 48; 
		case DUMMY_NE_TYPE_1646CPE:
			return 49; 
		case DUMMY_NE_TYPE_1540NT:
			return 50; 
		case DUMMY_NE_TYPE_1540POP:
			return 51; 
		case DUMMY_NE_TYPE_1540SDH:
			return 52; 
		case DUMMY_NE_TYPE_1631FOX:
			return 53; 
		case DUMMY_NE_TYPE_1640FOX:
			return 54; 
		case DUMMY_NE_TYPE_1641SM:
			return 55; 
		case DUMMY_NE_TYPE_1641SMC:
			return 56; 
		case DUMMY_NE_TYPE_1641SMT:
			return 57; 
		case DUMMY_NE_TYPE_1641SX:
			return 58; 
		case DUMMY_NE_TYPE_1642EM:
			return 59; 
		case DUMMY_NE_TYPE_1642EMC:
			return 60; 
		case DUMMY_NE_TYPE_1642EMUX:
			return 61; 
		case DUMMY_NE_TYPE_1650SMC:
			return 62; 
		case DUMMY_NE_TYPE_1651SM:
			return 63; 
		case DUMMY_NE_TYPE_1651SMC:
			return 64; 
		case DUMMY_NE_TYPE_1654SL:
			return 65; 
		case DUMMY_NE_TYPE_1660SM:
			return 66; 
		case DUMMY_NE_TYPE_1661SMC:
			return 67; 
		case DUMMY_NE_TYPE_1664SL:
			return 68; 
		case DUMMY_NE_TYPE_1664SM:
			return 69; 
		case DUMMY_NE_TYPE_1664SX:
			return 70; 
		case DUMMY_NE_TYPE_1665DMX:
			return 71; 
		case DUMMY_NE_TYPE_1665DMXplore:
			return 72; 
		case DUMMY_NE_TYPE_1665DMXtend:
			return 73; 
		case DUMMY_NE_TYPE_1670SM:
			return 74; 
		case DUMMY_NE_TYPE_1674LG:
			return 75; 
		case DUMMY_NE_TYPE_1678ADM:
			return 76; 
		case DUMMY_NE_TYPE_1678MCC:
			return 77; 
		case DUMMY_NE_TYPE_1680SM:
			return 78; 
		case DUMMY_NE_TYPE_1662SMC:
			return 79; 
		case DUMMY_NE_TYPE_1850TSS_5R:
			return 80; 
		case DUMMY_NE_TYPE_EXTERNAL:
			return 81; 
		case DUMMY_NE_TYPE_1850TSS_320H:
			return 87;
		case DUMMY_NE_TYPE_1850TSS_160H:
			return 88;
		}
		return -1;
	}

	public String toString() {
		/*
		 * switch(this){ case DUMMY_NE_TYPE_NONE:return
		 * I18N.instance().getString("NONE"); case
		 * DUMMY_NE_TYPE_ISA_PREA_S1P:return
		 * I18N.instance().getString("ISA_PREA_S1P"); case
		 * DUMMY_NE_TYPE_ISA_PREA_4ETH:return
		 * I18N.instance().getString("ISA_PREA_4ETH"); case
		 * DUMMY_NE_TYPE_ISA_PREA_1GBEP:return
		 * I18N.instance().getString("ISA_PREA_1GBEP"); case
		 * DUMMY_NE_TYPE_ISA_E_FE:return I18N.instance().getString("ISA_E_FE");
		 * case DUMMY_NE_TYPE_ISA_GE_RA:return
		 * I18N.instance().getString("ISA_GE_RA"); case
		 * DUMMY_NE_TYPE_ISA_GE_TR:return
		 * I18N.instance().getString("ISA_GE_TR"); case DUMMY_NE_TYPE_ISA_PR:
		 * return I18N.instance().getString("ISA_PR"); case
		 * DUMMY_NE_TYPE_ISA_ES1_3FE: return
		 * I18N.instance().getString("ISA_ES1_3FE"); case
		 * DUMMY_NE_TYPE_ISA_ES1_8FE: return
		 * I18N.instance().getString("ISA_ES1_8FE"); case
		 * DUMMY_NE_TYPE_ISA_ES1_8FX: return
		 * I18N.instance().getString("ISA_ES1_8FX"); case
		 * DUMMY_NE_TYPE_ISA_ES4_8FE: return
		 * I18N.instance().getString("ISA_ES4_8FE"); case
		 * DUMMY_NE_TYPE_ISA_ES16: return I18N.instance().getString("ISA_ES16");
		 * case DUMMY_NE_TYPE_1850TSS_320: return
		 * I18N.instance().getString("1850TSS_320"); case
		 * DUMMY_NE_TYPE_1850TSS_160: return
		 * I18N.instance().getString("1850TSS_160"); case
		 * DUMMY_NE_TYPE_1850TSS_5_EIU_40: return
		 * I18N.instance().getString("1850TSS_5_EIU_40"); case
		 * DUMMY_NE_TYPE_1850TSS_5_EIU_41: return
		 * I18N.instance().getString("1850TSS_5_EIU_41"); case
		 * DUMMY_NE_TYPE_1850TSS_5_EIU_42: return
		 * I18N.instance().getString("1850TSS_5_EIU_42"); case
		 * DUMMY_NE_TYPE_1850TSS_5_EIU_42B: return
		 * I18N.instance().getString("1850TSS_5_EIU_42B"); case
		 * DUMMY_NE_TYPE_1850TSS_5_TIU_60: return
		 * I18N.instance().getString("1850TSS_5_TIU_60"); case
		 * DUMMY_NE_TYPE_1850TSS_5_TIU_61: return
		 * I18N.instance().getString("1850TSS_5_TIU_61"); case
		 * DUMMY_NE_TYPE_1850TSS_5_TIU_62: return
		 * I18N.instance().getString("1850TSS_5_TIU_62"); case
		 * DUMMY_NE_TYPE_1850TSS_5_TIU_64: return
		 * I18N.instance().getString("1850TSS_5_TIU_64"); case
		 * DUMMY_NE_TYPE_1850TSS_160C: return
		 * I18N.instance().getString("1850TSS_160C"); case
		 * DUMMY_NE_TYPE_1850TSS_5C: return
		 * I18N.instance().getString("1850TSS_5C"); case DUMMY_NE_TYPE_EXTERNAL:
		 * return I18N.instance().getString("EXTERNAL"); case
		 * DUMMY_NE_TYPE_TSS5R : return I18N.instance().getString("TSS5R"); }
		 * return "UNKNOWN";
		 */
		if(this == DUMMY_NE_TYPE_NONE){
			return "";
		}
		String value = super.toString();
		String prefix = "DUMMY_NE_TYPE_";
		String i18nString = value.substring(prefix.length());
//		System.out.println(i18nString);
		return i18nString;
	}

}

