package com.example.backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;

/**
 * REST controller for handling file uploads and student-related API endpoints.
 * It provides endpoints to upload transcript PDFs, retrieve dropdown options,
 * submit concentration selections, and get student progress.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FileUploadController {

    /**
     * The current Student object generated after processing the uploaded transcript.
     */
    private static Student currentStudent;

    /**
     * Handles the upload of a PDF file, saves it temporarily, and processes it using PDFParser.
     *
     * @param file the uploaded MultipartFile
     * @return a message indicating whether the file was processed successfully or an error occurred
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            // Save the file temporarily
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs(); // Create directory if not exists

            File savedFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(savedFile);

            // Process the uploaded PDF using PDFParser
            PDFParser.processPDF(savedFile.getAbsolutePath());

            return "File processed successfully: " + file.getOriginalFilename();
        } catch (IOException e) {
            return "File upload failed: " + e.getMessage();
        } catch (Exception e) {
            return "Error processing file: " + e.getMessage();
        }
    }
    
    /**
     * Retrieves dropdown options for majors and minors from Excel files.
     *
     * @return a map containing two lists: one for majors ("dropdown1") and one for minors ("dropdown2")
     */
    @GetMapping("/dropdown-options")
    public Map<String, List<String>> getDropdownOptions() {
        InputStream majorStream = getClass().getClassLoader().getResourceAsStream("Major-List.xlsx");
        InputStream minorStream = getClass().getClassLoader().getResourceAsStream("Minor-List.xlsx");

        if (majorStream == null || minorStream == null) {
            throw new RuntimeException("Excel files not found in resources folder!");
        }

        Sheet majors = new SheetGenerator(majorStream).getSheet();
        Sheet minors = new SheetGenerator(minorStream).getSheet();


        ArrayList<String> majorList = new ArrayList<>();

        for(Row row : majors) {
            majorList.add(row.getCell(0).toString());
        }

        ArrayList<String> minorList = new ArrayList<>();

        for(Row row : minors) {
            minorList.add(row.getCell(0).toString());
        }
    
        Map<String, List<String>> options = new HashMap<>();
        options.put("dropdown1", majorList);
        options.put("dropdown2", minorList);
    
        return options;
    }

    /**
     * Handles the submission of selected major and minor concentrations.
     * It creates Concentration objects based on the selections and generates a Student planner.
     *
     * @param selections a map containing lists of selected majors ("dropdown1") and minors ("dropdown2")
     * @return a confirmation message indicating successful receipt of selections
     */
    @PostMapping("/submit-selections")
    public String handleSelections(@RequestBody Map<String, List<String>> selections) {
        // Get the lists from the JSON payload
        List<String> selectedDropdown1 = selections.get("dropdown1");
        List<String> selectedDropdown2 = selections.get("dropdown2");

        // Treat missing keys as empty selections to prevent a NullPointerException.
        if (selectedDropdown1 == null) {
            selectedDropdown1 = new ArrayList<>();
        }
        if (selectedDropdown2 == null) {
            selectedDropdown2 = new ArrayList<>();
        }

        System.out.println("Received selections:");
        System.out.println("Dropdown 1: " + selectedDropdown1);
        System.out.println("Dropdown 2: " + selectedDropdown2);

        ArrayList<Concentration> majors = new ArrayList<>();
        for (String str : selectedDropdown1) {
            majors.add(new Concentration(false, str));
        }

        ArrayList<Concentration> minors = new ArrayList<>();
        for (String str : selectedDropdown2) {
            minors.add(new Concentration(true, str));
        }

        // Store the generated Student object

        String outputPath = System.getProperty("user.dir") + File.separator + "output" + File.separator + "ParsedTranscript.xlsx";

        // Ensure the file exists before trying to open it
        File parsedFile = new File(outputPath);
        if (!parsedFile.exists()) {
            return "Error: Parsed transcript file not found!";
        }

        // Now safely load the file


        currentStudent = Driver.generatePlanner(outputPath, majors, minors);

        return "Selections received successfully!";
    }

    /**
     * Retrieves the student's academic progress.
     *
     * @return a string representing the student's progress, or a message if no student data is available
     */
    @GetMapping("/student-progress")
    public String getStudentProgress() {
        // Assuming the Student object was created in handleSelections()
        if (currentStudent == null) {
            return "No student data available. Please submit your selections first.";
        }

        return currentStudent.getProgressString();
    }

    @GetMapping("/debug-file")
    public String checkFileExists() {
        URL majorUrl = getClass().getClassLoader().getResource("Major-List.xlsx");
        URL minorUrl = getClass().getClassLoader().getResource("Minor-List.xlsx");

        if (majorUrl == null || minorUrl == null) {
            return "One or both Excel files are missing! Major: " + majorUrl + ", Minor: " + minorUrl;
        } else {
            return "Files found! Major: " + majorUrl.getPath() + ", Minor: " + minorUrl.getPath();
        }
    }
}