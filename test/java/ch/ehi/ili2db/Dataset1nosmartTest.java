package ch.ehi.ili2db;

import java.io.File;

import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.ili2db.base.DbUrlConverter;
import ch.ehi.ili2db.base.Ili2db;
import ch.ehi.ili2db.base.Ili2dbException;
import ch.ehi.ili2db.gui.Config;
import ch.ehi.ili2db.mapping.NameMapping;

public class Dataset1nosmartTest {
	private static final String DBSCHEMA = "Dataset1nosmart";
	private static final String DATASETNAME_A = "Testset1_a";
	private static final String DATASETNAME_B = "Testset1_b";
	String dbhost=null;
	String dbport=null;
	String dbname="ili2db";
	String dbuser="postgres";
	String dbpwd="ola2011";

	public Config initConfig(String xtfFilename,String dbschema,String logfile) {
		Config config=new Config();
		new ch.ehi.ili2pg.PgMain().initConfig(config);
		
		
		config.setDbhost(dbhost);
		config.setDbport(dbport);
		config.setDbdatabase(dbname);
		config.setDbusr(dbuser);
		config.setDbpwd(dbpwd);
		if(dbschema!=null){
			config.setDbschema(dbschema);
		}
		if(logfile!=null){
			config.setLogfile(logfile);
		}
		config.setDburl(getDbUrlConverter().makeUrl(config));


		config.setXtffile(xtfFilename);
		if(xtfFilename!=null && Ili2db.isItfFilename(xtfFilename)){
			config.setItfTransferfile(true);
		}
		return config;
		
	}
	protected DbUrlConverter getDbUrlConverter() {
		return new DbUrlConverter(){
			public String makeUrl(Config config) {
				/*
				    * jdbc:postgresql:database
				    * jdbc:postgresql://host/database
				    * jdbc:postgresql://host:port/database
				    */
				if(config.getDbdatabase()!=null){
					if(config.getDbhost()!=null){
						if(config.getDbport()!=null){
							return "jdbc:postgresql://"+config.getDbhost()+":"+config.getDbport()+"/"+config.getDbdatabase();
						}
						return "jdbc:postgresql://"+config.getDbhost()+"/"+config.getDbdatabase();
					}
					return "jdbc:postgresql:"+config.getDbdatabase();
				}
				return null;
			}
		};
	}

	//config.setDeleteMode(Config.DELETE_DATA);
	//EhiLogger.getInstance().setTraceFilter(false); 
	// --skipPolygonBuilding
	//config.setDoItfLineTables(true);
	//config.setAreaRef(config.AREA_REF_KEEP);
	// --importTid
	//config.setTidHandling(config.TID_HANDLING_PROPERTY);
	
	@Test
	public void importDataset() throws Ili2dbException
	{
		{
			File data=new File("test/data/Dataset1a1.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName(DATASETNAME_A);
			config.setFunction(Config.FC_IMPORT);
			config.setCreateFk(config.CREATE_FK_YES);
			config.setBasketHandling(config.BASKET_HANDLING_READWRITE);
			config.setCatalogueRefTrafo(null);
			config.setMultiSurfaceTrafo(null);
			config.setMultilingualTrafo(null);
			config.setInheritanceTrafo(null);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}
		{
			File data=new File("test/data/Dataset1b1.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName(DATASETNAME_B);
			config.setFunction(Config.FC_IMPORT);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}
	}
	@Test
	public void deleteDataset() throws Ili2dbException
	{
		{
			Config config=initConfig(null,DBSCHEMA,"test/data/Dataset1b1-out.xtf"+".log");
			config.setDatasetName(DATASETNAME_B);
			config.setFunction(Config.FC_DELETE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}
	}
	@Test
	public void replaceDataset() throws Ili2dbException
	{
		{
			File data=new File("test/data/Dataset1a2.xtf");
			Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
			config.setDatasetName(DATASETNAME_A);
			config.setFunction(Config.FC_REPLACE);
			Ili2db.readSettingsFromDb(config);
			Ili2db.run(config,null);
		}
	}
	
	@Test
	public void exportDataset() throws Ili2dbException
	{
		File data=new File("test/data/Dataset1-out.xtf");
		Config config=initConfig(data.getPath(),DBSCHEMA,data.getPath()+".log");
		config.setDatasetName(DATASETNAME_A);
		config.setFunction(Config.FC_EXPORT);
		Ili2db.readSettingsFromDb(config);
		Ili2db.run(config,null);
	}
}