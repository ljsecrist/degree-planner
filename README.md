# Degree Planner Web Application

## Overview
The Degree Planner is a web-based application designed to assist students in planning their academic journey efficiently. Users can upload their transcript PDFs, select their desired majors and minors, and view personalized progress towards fulfilling academic requirements.

## Features
- **Transcript Upload**: Users upload their academic transcripts as PDF files.
- **Major & Minor Selection**: An intuitive interface allows users to select up to two majors and minors through searchable dropdown menus.
- **Personalized Degree Progress**: The app provides a clear view of completed and remaining requirements for the selected majors/minors.

## Technology Stack
- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Java, Spring Boot
- **APIs**: RESTful APIs for seamless frontend-backend communication
- **Deployment**: Render platform (Docker container)

## Usage
1. Upload your PDF transcript.
2. Select majors/minors from dropdowns.
3. Submit selections to generate your personalized academic progress overview.

## API Endpoints
- `GET /api/dropdown-options`: Retrieves available majors and minors.
- `POST /api/upload`: Handles PDF transcript uploads.
- `POST /api/submit-selections`: Receives major/minor selections and processes transcript.
- `GET /api/student-progress`: Provides the student's progress based on uploaded transcript and selections.

## Deployment
Deployed via Render:
- Backend hosted at: `https://degree-planner-backend.onrender.com`

## Objects & Their Functions
The following objects are used in this project to structure and process data:

### **Backend Objects**
- **`Student`**: Represents a student, storing their academic data, selected majors/minors, and progress.
- **`Concentration`**: Represents a major or minor, containing relevant requirements and coursework.
- **`Requirement`**: Defines a specific academic requirement (e.g., core courses, electives) needed for a major or minor. These may take the form of a list of courses, or multiple sequences of courses.
- **`Course`**: Represents an individual course that a student has taken or needs to take.
- **`PDFParser`**: Reads and processes the uploaded transcript file, extracting course data.
- **`SheetGenerator`**: Loads Excel sheets containing major and minor requirements for comparison against a student’s progress.
- **`FileUploadController`**: Handles API endpoints for uploading transcripts, selecting majors/minors, and retrieving progress.

### **Frontend Objects**
- **`upload.html`**: The main webpage allowing users to upload transcripts, select concentrations, and view progress.
- **`script.js`**:
  - Handles fetching API data.
  - Manages dropdown selections.
  - Stores user-uploaded file information.
  - Submits user data to the backend.
- **`styles.css`**: Defines the visual styling for the web application.
