package com.example.backend;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.poi.ss.usermodel.*;

/**
 * Represents an academic concentration (major or minor) with its associated requirements.
 * 
 * Depending on whether the concentration is a major or a minor (indicated by the {@code majmin} flag),
 * the appropriate requirements are loaded from an Excel file.
 * 
 */
public class Concentration {
    
    /**
     * Flag indicating if the concentration is a minor.
     * 
     * {@code false} indicates a major concentration; {@code true} indicates a minor concentration.
     * 
     */
    private boolean majmin;
    
    /**
     * The name of the concentration.
     */
    private String name;
    
    /**
     * The list of requirements associated with this concentration.
     */
    private ArrayList<Requirement> reqs;

    /**
     * Constructs a Concentration object and loads the appropriate requirements based on the concentration type.
     *
     * @param majmin {@code false} for a major concentration; {@code true} for a minor concentration.
     * @param name   the name of the concentration.
     */
    public Concentration(boolean majmin, String name) {
        this.majmin = majmin;
        this.name = name;
        this.reqs = majmin ? getMinorRequirements() : getMajorRequirements();
    }

    /**
     * Loads the major concentration requirements from the corresponding Excel sheet.
     *
     * @return an {@code ArrayList} of {@code Requirement} objects for the major concentration.
     */
    private ArrayList<Requirement> getMajorRequirements() {
        return loadRequirementsFromSheet("src\\main\\resources\\Major-Requirements.xlsx");
    }

    /**
     * Loads the minor concentration requirements from the corresponding Excel sheet.
     *
     * @return an {@code ArrayList} of {@code Requirement} objects for the minor concentration.
     */
    private ArrayList<Requirement> getMinorRequirements() {
        return loadRequirementsFromSheet("src\\main\\resources\\Minor-Requirements.xlsx");
    }

    /**
     * Loads requirements from the specified Excel sheet.
     * 
     * This method reads the Excel file at the given file path, iterates through its rows, and extracts requirement
     * information if the row matches the current concentration name.
     *
     *
     * @param filePath the path to the Excel file containing the requirements.
     * @return an {@code ArrayList} of {@code Requirement} objects loaded from the sheet.
     */
    private ArrayList<Requirement> loadRequirementsFromSheet(String filePath) {
        ArrayList<Requirement> requirements = new ArrayList<>();
            
        Sheet reqsSheet = new SheetGenerator(filePath).getSheet();
            
        for (Row row : reqsSheet) {
            if (row.getRowNum() == 0) continue; // Skip header row
            
            String majorName = row.getCell(0).getStringCellValue().trim();
            if (!majorName.equalsIgnoreCase(this.name)) continue; // Skip rows not related to this concentration
            
            String courses = row.getCell(1).getStringCellValue().trim();
            int numNeeded = (int) row.getCell(2).getNumericCellValue();
            String title = row.getCell(3).getStringCellValue().trim();
            String numberRequirements = row.getCell(4) != null && row.getCell(4).getCellType() != CellType.BLANK 
                ? row.getCell(4).getStringCellValue().trim() : "";
            String typeRequirements = row.getCell(5) != null && row.getCell(5).getCellType() != CellType.BLANK 
                ? row.getCell(5).getStringCellValue().trim() : "";
            String gradeRequirement = row.getCell(6) != null && row.getCell(6).getCellType() != CellType.BLANK 
                ? row.getCell(6).getStringCellValue().trim() : "";

            if (courses.contains(";")) {
                requirements.add(Requirement.fromSequences(title, parseSequences(courses, title, gradeRequirement), numNeeded, gradeRequirement));
            } else {
                requirements.add(Requirement.fromCourseGroups(title, parseCourseList(courses), numNeeded, numberRequirements, typeRequirements, gradeRequirement));
            }
        }
        
        return requirements;
    }

    /**
     * Parses a comma-separated list of courses into an {@code ArrayList} of course names.
     *
     * @param courses a {@code String} containing courses separated by commas.
     * @return an {@code ArrayList} of individual course names.
     */
    private ArrayList<String> parseCourseList(String courses) {
        return new ArrayList<>(Arrays.asList(courses.split("\\s*,\\s*")));
    }

    /**
     * Parses sequences of course requirements formatted as semicolon-separated groups.
     * 
     * Each group is expected to be in the format: {@code (course1, course2, ... | numNeeded)}.
     * Parentheses are removed and the group is split into a list of courses and the required number.
     * 
     *
     * @param seqs             a {@code String} containing the sequences.
     * @param title            the title to assign to each requirement group.
     * @param gradeRequirement the grade requirement applicable to the sequence.
     * @return an {@code ArrayList} of {@code Requirement} objects created from the sequences.
     */
    private ArrayList<Requirement> parseSequences(String seqs, String title, String gradeRequirement) {
        ArrayList<Requirement> requirements = new ArrayList<>();

        // Split the input by semicolons to separate different requirement groups
        String[] groups = seqs.split("\\s*;\\s*");

        for (String group : groups) {
            // Remove parentheses and trim whitespace
            group = group.replaceAll("[()]", "").trim();

            // Split into course list and number needed
            String[] parts = group.split("\\s*\\|\\s*");
            if (parts.length != 2) continue; // Ensure valid format

            // Extract course list and number of courses needed
            ArrayList<String> courseList = parseCourseList(parts[0]);
            int numNeeded = Integer.parseInt(parts[1].trim());

            // Create and add a Requirement object (Title can be adjusted as needed)
            requirements.add(Requirement.fromCourseGroups(title, courseList, numNeeded, "", "", gradeRequirement));
        }

        return requirements;
    }

    /**
     * Returns whether the concentration is a minor.
     *
     * @return {@code true} if it is a minor concentration; {@code false} if it is a major concentration.
     */
    public boolean isMajmin() {
        return majmin;
    }

    /**
     * Gets the name of the concentration.
     *
     * @return the concentration name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of requirements for the concentration.
     *
     * @return an {@code ArrayList} of {@code Requirement} objects.
     */
    public ArrayList<Requirement> getReqs() {
        return reqs;
    }

    /**
     * Returns a {@code String} representation of the concentration, including its type and associated requirements.
     *
     * @return a formatted string detailing the concentration and its requirements.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(majmin ? "Minor: " : "Major: ").append(name).append("\n");
        for (Requirement req : reqs) {
            sb.append(req.toString()).append("\n");
        }
        return sb.toString();
    }
}
