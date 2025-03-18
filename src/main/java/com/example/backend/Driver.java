package com.example.backend;

import org.apache.poi.ss.usermodel.*;
import java.util.ArrayList;

/**
 * The Driver class is the entry point for the degree planner backend.
 * It processes PDF transcripts, generates a planner, and prints student progress.
 */
public class Driver {

    /**
     * The starting column index for course type columns.
     */
    private static final int COL_TYPE_START = 6;
    
    /**
     * The ending column index for course type columns.
     */
    private static final int COL_TYPE_END = 31;
    
    /**
     * Main method to run the degree planner.
     * It processes a PDF transcript and generates a Student object with course progress.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args){

        ArrayList<Concentration> majors = new ArrayList<>();
        majors.add(new Concentration(false, "Computer Science"));

        ArrayList<Concentration> minors = new ArrayList<>();

        // Uncomment the following lines for different transcript processing options
        // generatePlanner("backend\\src\\main\\resources\\LiamSecrist Transcript.xlsx");
        // generatePlanner("src\\main\\resources\\Empty Transcript.xlsx", majors, minors).printProgress();
        // generatePlanner("src\\main\\resources\\Full Transcript.xlsx", majors, minors).printProgress();

        PDFParser.processPDF("src\\main\\resources\\Secrist_Liam_2686252_2_14_2025.pdf");

        generatePlanner("output\\ParsedTranscript.xlsx", majors, minors).printProgress();
    }

    /**
     * Generates a Student planner by reading a transcript from an Excel file.
     * It parses the Excel sheet to create a list of Course objects and then constructs a Student.
     *
     * @param filePath the path to the Excel transcript file
     * @param majors a list of Concentration objects representing the student's majors
     * @param minors a list of Concentration objects representing the student's minors
     * @return a Student object representing the student's academic planner
     */
    public static Student generatePlanner(String filePath, ArrayList<Concentration> majors, ArrayList<Concentration> minors){
        
        Sheet transcript = new SheetGenerator(filePath).getSheet();

        ArrayList<Course> courses = new ArrayList<Course>();

        for (Row row : transcript) {
            if(row.getCell(0).toString().contains("-")){

                Course course;
                String term;
                String year;
                String code;
                String title;
                ArrayList<String> types;
                int credits;
                String grade;

                if (row.getCell(4) != null && row.getCell(4).getCellType() == CellType.BLANK) {
                    year = row.getCell(4).toString().substring(0,2);
                    term = row.getCell(4).toString().substring(3,5);
                } else {
                    year = null;
                    term = null;
                }

                code = row.getCell(0).toString();
                title = row.getCell(1).toString();
                types = new ArrayList<>();

                for(int i = COL_TYPE_START; i <= COL_TYPE_END; i++){
                    if(row.getCell(i) != null && row.getCell(i).getCellType() != CellType.BLANK && row.getCell(i).toString().equals("TRUE")){
                        types.add(transcript.getRow(0).getCell(i).toString());
                    }
                }

                if (row.getCell(5) != null && row.getCell(5).getCellType() != CellType.BLANK) {
                    grade = row.getCell(5).toString().trim();
                } else {
                    grade = "N/A";
                }

                if (row.getCell(3) != null && row.getCell(3).getCellType() == CellType.BLANK) {
                    credits = Integer.parseInt(row.getCell(3).toString());
                } else {
                    credits = 0;
                }

                course = new Course(term, year, code, title, types, credits, grade);

                courses.add(course);
            }
        }

        Student student = new Student(2027, courses, majors, minors);

        return student;
    }
}