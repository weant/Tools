package com.hcop.otn.common.db.oms;


import java.io.File;

import static com.hcop.otn.constants.ConfigKey.CONFIGURATION_PATH;

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
			String dir = dirPath+"eml"+"_"+inst;
			File file = new File(dir);
			if(file.exists()) {
				return dir;
			}
			return dirPath+"otn"+"_"+inst;
		}
	},
	ETH {
		@Override
		public	String getAlias(String version,int inst) {
			return "ETH"+version+"_"+inst+"_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			String dir = dirPath+"eml"+"_"+inst;
			File file = new File(dir);
			if(file.exists()) {
				return dir;
			}
			return dirPath+"otn"+"_"+inst;
		}
	},
	EML_SYS{
		@Override
		public	String getAlias(String version,int inst) {
			return "SYSTEM_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			String dir = dirPath+"eml"+"_"+inst;
			File file = new File(dir);
			if(file.exists()) {
				return dir;
			}
			return dirPath+"otn"+"_"+inst;
		}
	},
	MPLS {
		@Override
		public	String getAlias(String version,int inst) {
			return "MPLS"+version+"_"+inst+"_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			String dir = dirPath+"eml"+"_"+inst;
			File file = new File(dir);
			if(file.exists()) {
				return dir;
			}
			return dirPath+"otn"+"_"+inst;
		}
	},
	
	EML_ANALOG{
		@Override
		public String getAlias(String version, int inst) {
			return "ANALOG"+version+"_"+inst+"_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			String dir = dirPath+"eml"+"_"+inst;
			File file = new File(dir);
			if(file.exists()) {
				return dir;
			}
			return dirPath+"otn"+"_"+inst;
		}
	},
	
	EML_SDH{
		@Override
		public String getAlias(String version, int inst) {
			return "SDH"+version+"_"+inst+"_ALIAS";
		}

		@Override
		public String getWalletPath(int inst) {
			String dir = dirPath+"eml"+"_"+inst;
			File file = new File(dir);
			if(file.exists()) {
				return dir;
			}
			return dirPath+"otn"+"_"+inst;
		}
	}
	
	;

	protected static File currentDir = new File(System.getProperty(CONFIGURATION_PATH) == null ? System.getenv(CONFIGURATION_PATH) : System.getProperty(CONFIGURATION_PATH));
	protected static String dirPath  = currentDir.getAbsolutePath() + "/wallet/";
	
	public abstract  String getAlias(String version,int inst);
	public  String getCacheKey(String confName, int inst){
		if(confName.length() > 0) {
			return confName + "_" + this + "_" + inst;
		}
		return this+"_"+inst;
	}
	
	public abstract String getWalletPath(int inst);
}
