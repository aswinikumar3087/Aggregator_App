package com.bt.metb.aggregator.util;


import com.bt.metb.MetbAbstractBase;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.transform.Templates;
import java.io.File;
import java.util.HashMap;


@RunWith(JMockit.class)
public class FileUtilTest extends MetbAbstractBase {

    @Test
    public void testSuccessGetFileNamesArray() {

		File file = new File("test_data");

		String path=file.getAbsolutePath();
		path =path.replace("application", "application\\src\\test\\resources");
		try {
    	String[] result =FileUtil.getFileNamesArray(path, ".properties");
		}catch(Exception e){
			
		}
 
    }
    
    @Test
    public void testSuccessLoadAllXSLs() {

		File file = new File("test_data");

		String path=file.getAbsolutePath();
		path =path.replace("application", "application\\src\\test\\resources");
		path =path.replace("test_data", "test_data\\");
		HashMap<String, Templates> map = new HashMap<String, Templates>();
	try {
		FileUtil.loadAllXSLs(path, map);
	} catch(Exception e){
	}
    }
    

}
