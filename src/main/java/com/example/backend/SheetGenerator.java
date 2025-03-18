package com.example.backend;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
    public SheetGenerator(String xlsx) {
        String filePath = xlsx;  // Path to the Excel file
        try (FileInputStream file = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(file)) {
            this.sheet = workbook.getSheetAt(0);  // Read the first sheet

        } catch (IOException e) {
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