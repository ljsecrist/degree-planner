package com.example.backend;

import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

/**
 * The SheetGenerator class is responsible for reading an Excel file and providing access to its first sheet.
 */
public class SheetGenerator {

    /**
     * The Excel sheet read from the file.
     */
    public Sheet sheet;
    
    /**
     * Constructs a SheetGenerator for the specified Excel file.
     *
     * @param xlsx the path to the Excel file
     */
    public SheetGenerator(String filePath) {
        try {
            InputStream fileStream;

            // Check if the file is an output/runtime file (absolute path)
            File file = new File(filePath);
            if (file.exists()) {
                fileStream = new FileInputStream(file);
            } else {
                // Otherwise, try loading it as a classpath resource
                fileStream = getClass().getClassLoader().getResourceAsStream(filePath);
            }

            if (fileStream == null) {
                throw new IOException("File not found: " + filePath);
            }

            Workbook workbook = WorkbookFactory.create(fileStream);
            this.sheet = workbook.getSheetAt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SheetGenerator(InputStream inputStream) {
        try {
            Workbook workbook = WorkbookFactory.create(inputStream);
            this.sheet = workbook.getSheetAt(0); // Adjust if needed
        } catch (Exception e) {
            throw new RuntimeException("Error loading Excel file: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the Excel sheet.
     *
     * @return the Sheet object read from the Excel file
     */
    public Sheet getSheet(){
        return sheet;
    }
}