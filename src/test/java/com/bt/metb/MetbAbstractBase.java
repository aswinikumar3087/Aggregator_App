/*
 * Copyright (c) 2019, British Telecom and/or its affiliates. All rights reserved.
 * BT PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.bt.metb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Common functions for Test classes
 *
 * @author Ananya
 * @version 1.0
 */
public abstract class MetbAbstractBase {
    /**
     * @param fileName Relative path to the file.
     * @return A String that contains all the contents of the file.
     * @throws IOException thrown when the file is not present
     */
    protected String getFileContentAsString(String fileName) throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder allLines = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            allLines.append(line);
        }
        return allLines.toString();
    }
}
