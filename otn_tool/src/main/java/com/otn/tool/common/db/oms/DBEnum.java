package com.otn.tool.common.db.oms;


public enum DBEnum {
	
//	PKT {
//		@Override
//		public String getAlias(String version,int inst) {
//			return "BMML_ALIAS";
//		}
//
//		@Override
//		public String getWalletPath(int inst) {
//			return dirPath+"pkt"+"_"+inst;
//		}
//
//	},
//	PKT_SYS{
//
//		@Override
//		public String getAlias(String version, int inst) {
//			return "SYSTEM_ALIAS";
//		}
//
//		@Override
//		public String getWalletPath(int inst) {
//			return dirPath+"pkt"+"_"+inst;
//		}
//		
//	},
	WDM{
		@Override
		public String getAlias(String version,int inst) {
			return "WDM_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			return dirPath+"otn"+"_"+inst;
		}
	},
	SDH {
		@Override
		public String getAlias(String version,int inst) {
			return "SNML_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			return dirPath+"otn"+"_"+ inst;
		}

	},
	SDH_SYS{
		@Override
		public String getAlias(String version,int inst) {
			return "SYSTEM_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			return dirPath+"otn"+"_"+ inst;
		}
	},
	SNA {
		@Override
		public	String getAlias(String version,int inst) {
			return "SNA_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			return dirPath+"eml"+"_"+inst;
		}
	},
	ETH {
		@Override
		public	String getAlias(String version,int inst) {
			return "ETH"+version+"_"+inst+"_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			return dirPath+"eml"+"_"+inst;
		}
	},
	EML_SYS{
		@Override
		public	String getAlias(String version,int inst) {
			return "SYSTEM_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			return dirPath+"eml"+"_"+inst;
		}
	},
	MPLS {
		@Override
		public	String getAlias(String version,int inst) {
			return "MPLS"+version+"_"+inst+"_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			return dirPath+"eml"+"_"+inst;
		}
	},
	
	EML_ANALOG{
		@Override
		public String getAlias(String version, int inst) {
			return "ANALOG"+version+"_"+inst+"_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			return dirPath+"eml"+"_"+inst;
		}
	},
	
	EML_SDH{
		@Override
		public String getAlias(String version, int inst) {
			return "SDH"+version+"_"+inst+"_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			return dirPath+"eml"+"_"+inst;
		}
	}
	
	;
	
	protected static String dirPath  = System.getenv("ALLUSERSPROFILE")+"/OAMTool/wallet/";
	
	public abstract  String getAlias(String version,int inst);
	public  String getCacheKey(String confName, int inst){
		return confName+"_"+this+"_"+inst;
	}
	
	public abstract String getWalletPath(int inst);
}
