package com.example.backend;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

    /**
     * Returns the Excel sheet.
     *
     * @return the Sheet object read from the Excel file
     */
    public Sheet getSheet(){
        return sheet;
    }
}