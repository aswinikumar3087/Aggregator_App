package com.bt.metb.aggregator.util;

import com.bt.metb.aggregator.constants.WMGConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.Map;

public class FileUtil 
{
    private FileUtil() {
    }
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	

    public static final String[] getFileNamesArray(final String fileBaseFolder, final String fileExtension) {
		File root = new File(fileBaseFolder);
		String[] filesArray = root.list((root1, name) -> name.endsWith(fileExtension));
		return filesArray;
    }

    public static final void loadAllXSLs(String baseFolder, Map<String,Templates> loadDestination) {
        logger.debug("FileUtil.loadAllXSLs() loadAllXSLs BASE_FOLDER {}", baseFolder);

        String[] xslFiles = getFileNamesArray(WMGConstant.XSL_FOLDER_PATH, WMGConstant.XSL_EXTENSION);
        logger.debug("Size xslFiles {}", xslFiles.length);
		for (int i = 0; i < xslFiles.length; i++) {
			try {
				TransformerFactory transFact = TransformerFactory.newInstance();
				Templates cachedXSLT =transFact.newTemplates(new StreamSource(WMGConstant.XSL_FOLDER_PATH + xslFiles[i]));
				loadDestination.put(xslFiles[i], cachedXSLT);
			} 
			catch (TransformerConfigurationException exception) {
                throw new RuntimeException("Error wile loading XSL", exception);
			}
		}
        logger.debug("Size loadDestination {}", loadDestination.size());
    }
}
