package com.example.backend;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The Student class represents a student with a graduation year, a list of completed courses,
 * and selected major and minor concentrations.
 */
public class Student {

    private int gradYr;
    private ArrayList<Course> courses;
    private ArrayList<Concentration> majors;
    private ArrayList<Concentration> minors;

    /**
     * Constructs a Student with the specified graduation year, courses, majors, and minors.
     *
     * @param gradYr the graduation year
     * @param courses the list of completed courses
     * @param majors the list of major concentrations
     * @param minors the list of minor concentrations
     */
    public Student(int gradYr, ArrayList<Course> courses, ArrayList<Concentration> majors, ArrayList<Concentration> minors) {
        this.gradYr = gradYr;
        this.courses = courses;
        this.majors = majors;
        this.minors = minors;
    }

    /**
     * Returns the graduation year.
     *
     * @return the graduation year
     */
    public int getGradYr() {
        return gradYr;
    }

    /**
     * Returns the list of completed courses.
     *
     * @return an ArrayList of Course objects
     */
    public ArrayList<Course> getCourses() {
        return courses;
    }

    /**
     * Returns the list of major concentrations.
     *
     * @return an ArrayList of Concentration objects for majors
     */
    public ArrayList<Concentration> getMajors() {
        return majors;
    }

    /**
     * Returns the list of minor concentrations.
     *
     * @return an ArrayList of Concentration objects for minors
     */
    public ArrayList<Concentration> getMinors() {
        return minors;
    }

    /**
     * Prints the student's progress, including graduation year and progress for each major and minor.
     */
    public void printProgress() {
        System.out.println("\n======== Student Progress ========");
        System.out.println("Graduation Year: " + gradYr);
        System.out.println("==================================\n");

        // Process majors
        for (Concentration major : majors) {
            System.out.println("Major: " + major.getName());
            printConcentrationProgress(major);
        }

        // Process minors
        for (Concentration minor : minors) {
            System.out.println("Minor: " + minor.getName());
            printConcentrationProgress(minor);
        }
    }

    /**
     * Processes each requirement for the given concentration.
     * A copy of the student's courses is made so that each requirement "consumes" courses as they are used.
     *
     * @param concentration the concentration (major or minor) to process
     */
    private void printConcentrationProgress(Concentration concentration) {
        List<Course> coursesLeft = new ArrayList<>(courses);
        for (Requirement req : concentration.getReqs()) {
            if (req.isSeq()) {
                System.out.println(processSequenceRequirement(req, coursesLeft));
            } else {
                System.out.println(processNonSequenceRequirement(req, coursesLeft));
            }
        }
    }

    /**
     * Processes a requirement that is made up of sequences (sub-requirements).
     *
     * @param req the requirement to process
     * @param coursesLeft the list of courses available for fulfilling the requirement
     * @return a string representing the progress on the sequence requirement
     */
    private String processSequenceRequirement(Requirement req, List<Course> coursesLeft) {
        StringBuilder result = new StringBuilder();
        int seqsFulfilled = 0;
        List<String[]> fulfilledCourseLists = new ArrayList<>();
        List<String[]> sequences = new ArrayList<>();

        // Build a list of sequence arrays from each sub-requirement.
        for (Requirement subReq : req.getSequences()) {
            String[] seq = new String[subReq.getNumNeeded()];
            for (int j = 0; j < subReq.getNumNeeded(); j++) {
                seq[j] = subReq.getCourseGroups().get(j);
            }
            sequences.add(seq);
        }

        int counter = 0;
        for (Requirement subReq : req.getSequences()) {
            int numFulfilled = 0;
            String[] fulfilledCourses = new String[subReq.getNumNeeded()];
            for (String course : subReq.getCourseGroups()) {
                List<Course> usedCourses = new ArrayList<>();
                for (Course myCourse : coursesLeft) {
                    if (course.equals(myCourse.getCode())
                            && (subReq.getGradeRequirement().equals("") || compareGrades(myCourse.getGrade(), subReq.getGradeRequirement()))) {
                        fulfilledCourses[numFulfilled] = course;
                        usedCourses.add(myCourse);
                        numFulfilled++;
                        if (numFulfilled >= subReq.getNumNeeded()) {
                            seqsFulfilled++;
                            break;
                        }
                    }
                }
                removeUsedCourses(coursesLeft, usedCourses);
                if (numFulfilled >= subReq.getNumNeeded()) {
                    // Remove the corresponding sequence since it has been completed
                    sequences.remove(counter);
                    counter--; // Adjust counter because of removal
                    break;
                }
            }
            fulfilledCourseLists.add(fulfilledCourses);
            counter++;
            if (seqsFulfilled >= req.getNumNeeded()) {
                break;
            }
        }

        // Print out progress based on how many sequences were fulfilled.
        if (seqsFulfilled >= req.getNumNeeded()) {
            result.append("[X] " + req.getTitle() + " (Completed)\n");
            String fulfilledStr = fulfilledCourseLists.stream()
                    .sorted((a, b) -> Double.compare(
                            (double) Arrays.stream(b).filter(Objects::nonNull).count() / b.length,
                            (double) Arrays.stream(a).filter(Objects::nonNull).count() / a.length))
                    .limit(req.getNumNeeded())
                    .map(arr -> Arrays.stream(arr).filter(Objects::nonNull).collect(Collectors.joining(", ")))
                    .collect(Collectors.joining("; "));
            result.append("   Fulfilled by: " + fulfilledStr + "\n");
        } else if (seqsFulfilled > 0) {
            result.append("[~] " + req.getTitle() + " (Partially Completed)\n");
            String completedSequences = fulfilledCourseLists.stream()
                    .filter(arr -> Arrays.stream(arr).noneMatch(Objects::isNull))
                    .map(arr -> "(" + String.join(", ", arr) + ")")
                    .collect(Collectors.joining("; "));
            result.append("   Sequences Completed: " + completedSequences + "\n");
            String sequencesLeftStr = formatSequences(sequences);
            result.append("   Sequences Left: " + (req.getNumNeeded() - seqsFulfilled)
                    + " of: " + sequencesLeftStr);
            result.append(gradeRequirementSuffix(req.getGradeRequirement()) + "\n");
        } else {
            result.append("[ ] " + req.getTitle() + " (Not Completed)\n");
            String sequencesLeftStr = formatSequences(sequences);
            result.append("   Sequences Left: " + (req.getNumNeeded() - seqsFulfilled)
                    + " of: " + sequencesLeftStr);
            result.append(gradeRequirementSuffix(req.getGradeRequirement()) + "\n");
        }
        result.append("\n");

        return result.toString();
    }

    /**
     * Processes a requirement that is not based on sequences.
     * This method has been refactored to use helper methods for clarity.
     *
     * @param req the requirement to process
     * @param coursesLeft the list of courses available for fulfilling the requirement
     * @return a string representing the progress on the non-sequence requirement
     */
    private String processNonSequenceRequirement(Requirement req, List<Course> coursesLeft) {
        StringBuilder result = new StringBuilder();
        int numFulfilled = 0;
        String[] fulfilledCourses = new String[req.getNumNeeded()];
        ArrayList<int[]> numRequirements = new ArrayList<>();
        ArrayList<Map.Entry<Integer, String>> typeRequirements = new ArrayList<>();
        ArrayList<String> courseList = new ArrayList<>(req.getCourseGroups());
        
        boolean hasNumReq = !req.getNumberRequirements().equals("");
        boolean hasTypeReq = !req.getTypeRequirements().equals("");

        if (hasNumReq) {
            for (String pair : req.getNumReqs()) {
                numRequirements.add(new int[]{
                    Integer.parseInt(pair.substring(0, 2).replaceAll("[^0-9]", "")),
                    Integer.parseInt(pair.substring(2).replaceAll("[^0-9]", ""))
                });
            }
        }
        if (hasTypeReq) {
            for (String pair : req.getTypeReqs()) {
                typeRequirements.add(new AbstractMap.SimpleEntry<>(
                    Integer.parseInt(pair.substring(0, 2).replaceAll("[^0-9]", "")),
                    pair.replaceAll(".*\\((.*?)\\).*", "$1")));
            }
        }

        for (String reqCourse : req.getCourseGroups()) {
            List<Course> usedCourses = new ArrayList<>();
            for (Course myCourse : coursesLeft) {
                if (courseMatchesRequirement(reqCourse, myCourse, req.getGradeRequirement())) {
                    boolean qualifies = false;
                    int remainingNeeded = req.getNumNeeded() - numFulfilled;
                    if (!hasNumReq && !hasTypeReq) {
                        qualifies = processNoAdditionalRequirements(reqCourse, myCourse, courseList);
                    } else if (hasNumReq && !hasTypeReq) {
                        qualifies = processNumberRequirements(myCourse, numRequirements, reqCourse, remainingNeeded, courseList);
                    } else if (!hasNumReq && hasTypeReq) {
                        qualifies = processTypeRequirements(myCourse, typeRequirements, reqCourse, remainingNeeded, courseList);
                    } else { // both exist
                        qualifies = processCombinedRequirements(myCourse, numRequirements, typeRequirements, reqCourse, remainingNeeded, courseList);
                    }
                    if (qualifies) {
                        fulfilledCourses[numFulfilled] = myCourse.getCode();
                        usedCourses.add(myCourse);
                        numFulfilled++;
                        if (numFulfilled >= req.getNumNeeded()) {
                            break;
                        }
                    }
                }
            }
            removeUsedCourses(coursesLeft, usedCourses);
            if (numFulfilled >= req.getNumNeeded()) {
                break;
            }
        }

        // Build the result string based on fulfillment
        if (numFulfilled >= req.getNumNeeded()) {
            result.append("[X] " + req.getTitle() + " (Completed)\n");
            result.append("   Fulfilled by: " + String.join(", ", fulfilledCourses) + "\n");
        } else if (numFulfilled > 0) {
            result.append("[~] " + req.getTitle() + " (Partially Completed)\n");
            String completed = Arrays.stream(fulfilledCourses)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
            result.append("   Completed: " + completed + "\n");
            if (courseList.get(0).contains("XXX")){
                result.append("   Still Needed: " + (req.getNumNeeded() - numFulfilled)
                    + " of " + String.join("or ", courseList));
            } else {
                if (req.getNumNeeded() == courseList.size()) {
                    result.append("   Still Needed: " + " All of: \n                    * " + String.join("\n                    * ", courseList));
                } else {
                    result.append("   Still Needed: " + (req.getNumNeeded() - numFulfilled)
                            + " of: \n                    * " + String.join("\n                    * ", courseList));
                }
            }
            result.append(formatRequirements(numRequirements, req.getNumberRequirements(), typeRequirements, req.getTypeRequirements()));
            result.append(gradeRequirementSuffix(req.getGradeRequirement()) + "\n");
        } else {
            result.append("[ ] " + req.getTitle() + " (Not Completed)\n");
            if (courseList.get(0).contains("XXX")){
                result.append("   Still Needed: " + (req.getNumNeeded() - numFulfilled)
                    + " of " + String.join("or ", courseList));
            } else {
                if (req.getNumNeeded() == courseList.size()) {
                    result.append("   Still Needed: " + " All of: \n                    * " + String.join("\n                    * ", courseList));
                } else {
                    result.append("   Still Needed: " + (req.getNumNeeded() - numFulfilled)
                            + " of: \n                    * " + String.join("\n                    * ", courseList));
                }
            }
            result.append(formatRequirements(numRequirements, req.getNumberRequirements(), typeRequirements, req.getTypeRequirements()));
            result.append(gradeRequirementSuffix(req.getGradeRequirement()) + "\n");
        }
        result.append("\n");

        return result.toString();
    }

    /**
     * Checks if a course from the requirement matches the given Course based on basic criteria.
     *
     * @param reqCourse the course code (or pattern) from the requirement
     * @param myCourse the Course object to evaluate
     * @param gradeRequirement the minimum grade requirement as a string
     * @return true if the course matches the requirement; false otherwise
     */
    private boolean courseMatchesRequirement(String reqCourse, Course myCourse, String gradeRequirement) {
        boolean basicMatch;
        if (reqCourse.contains("XXX")) {
            String prefix = reqCourse.substring(0, 3);
            basicMatch = myCourse.getCode().contains(prefix)
                    && !myCourse.getCode().contains("295H")
                    && !myCourse.getCode().contains("296H")
                    && !myCourse.getCode().contains("297H");
        } else {
            basicMatch = reqCourse.equals(myCourse.getCode());
        }
        boolean gradeOk = gradeRequirement.equals("") || compareGrades(myCourse.getGrade(), gradeRequirement);
        return basicMatch && gradeOk && !myCourse.getGrade().equals("W");
    }

    /**
     * Helper method for processing a course when no additional number or type requirements exist.
     *
     * @param reqCourse the course code from the requirement
     * @param myCourse the Course object being evaluated
     * @param courseList the list of courses still needed for the requirement
     * @return true (the course qualifies)
     */
    private boolean processNoAdditionalRequirements(String reqCourse, Course myCourse, List<String> courseList) {
        if (!reqCourse.contains("XXX")) {
            courseList.remove(reqCourse);
        }
        return true;
    }

    /**
     * Helper method for processing a course based on number requirements only.
     *
     * @param myCourse the Course object being evaluated
     * @param numRequirements a list of number requirement pairs [count, threshold]
     * @param reqCourse the course code from the requirement
     * @param remainingNeeded the number of courses still needed
     * @param courseList the list of courses still needed for the requirement
     * @return true if the course meets the number requirements; false otherwise
     */
    private boolean processNumberRequirements(Course myCourse, List<int[]> numRequirements, String reqCourse, int remainingNeeded, List<String> courseList) {
        boolean qualifies = false;
        for (int[] pair : numRequirements) {
            int threshold = pair[1];
            int countReq = pair[0];
            int courseNumber = Integer.parseInt(myCourse.getCode().substring(4, 7));
            if (courseNumber >= threshold) {
                if (remainingNeeded >= countReq) {
                    qualifies = true;
                    if (!reqCourse.contains("XXX")) {
                        courseList.remove(reqCourse);
                    }
                    if (pair[0] > 0) {
                        pair[0] = pair[0] - 1;
                    }
                }
            }
        }
        return qualifies;
    }

    /**
     * Helper method for processing a course based on type requirements only.
     *
     * @param myCourse the Course object being evaluated
     * @param typeRequirements a list of type requirement entries (count and type)
     * @param reqCourse the course code from the requirement
     * @param remainingNeeded the number of courses still needed
     * @param courseList the list of courses still needed for the requirement
     * @return true if the course meets the type requirements; false otherwise
     */
    private boolean processTypeRequirements(Course myCourse, List<Map.Entry<Integer, String>> typeRequirements, String reqCourse, int remainingNeeded, List<String> courseList) {
        boolean qualifies = false;
        for (int i = 0; i < typeRequirements.size(); i++) {
            Map.Entry<Integer, String> entry = typeRequirements.get(i);
            for (String type : myCourse.getTypes()) {
                if (entry.getValue().contains(type)) {
                    if (remainingNeeded >= entry.getKey()) {
                        qualifies = true;
                        if (!reqCourse.contains("XXX")) {
                            courseList.remove(reqCourse);
                        }
                        if (entry.getKey() > 0) {
                            typeRequirements.set(i, Map.entry(entry.getKey() - 1, entry.getValue()));
                        }
                    }
                }
            }
        }
        return qualifies;
    }

    /**
     * Helper method for processing a course based on combined number and type requirements.
     *
     * @param myCourse the Course object being evaluated
     * @param numRequirements a list of number requirement pairs [count, threshold]
     * @param typeRequirements a list of type requirement entries (count and type)
     * @param reqCourse the course code from the requirement
     * @param remainingNeeded the number of courses still needed
     * @param courseList the list of courses still needed for the requirement
     * @return true if the course meets the combined requirements; false otherwise
     */
    private boolean processCombinedRequirements(Course myCourse, List<int[]> numRequirements, List<Map.Entry<Integer, String>> typeRequirements, String reqCourse, int remainingNeeded, List<String> courseList) {
        boolean qualifies = false;
        for (int[] pair : numRequirements) {
            int threshold = pair[1];
            int countReq = pair[0];
            int courseNumber = Integer.parseInt(myCourse.getCode().substring(4, 7));
            if (courseNumber >= threshold) {
                if (remainingNeeded >= countReq) {
                    qualifies = true;
                    if (pair[0] > 0) {
                        pair[0] = pair[0] - 1;
                        for (int i = 0; i < typeRequirements.size(); i++) {
                            Map.Entry<Integer, String> entry = typeRequirements.get(i);
                            for (String type : myCourse.getTypes()) {
                                if (entry.getValue().contains(type)) {
                                    if (remainingNeeded >= entry.getKey()) {
                                        qualifies = true;
                                        if (entry.getKey() > 0) {
                                            typeRequirements.set(i, Map.entry(entry.getKey() - 1, entry.getValue()));
                                        }
                                    } else {
                                        qualifies = false;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Process type requirements if course number condition is not met.
                for (int i = 0; i < typeRequirements.size(); i++) {
                    Map.Entry<Integer, String> entry = typeRequirements.get(i);
                    for (String type : myCourse.getTypes()) {
                        if (type.equals(entry.getValue())) {
                            if (remainingNeeded >= entry.getKey()) {
                                qualifies = true;
                                if (entry.getKey() > 0) {
                                    typeRequirements.set(i, Map.entry(entry.getKey() - 1, entry.getValue()));
                                }
                            } else {
                                qualifies = false;
                            }
                        }
                    }
                }
            }
        }
        if (qualifies && !reqCourse.contains("XXX")) {
            courseList.remove(reqCourse);
        }
        return qualifies;
    }

    /**
     * Returns a suffix string to append when a grade requirement exists.
     *
     * @param gradeRequirement the minimum required grade
     * @return a string representing the grade requirement suffix
     */
    private String gradeRequirementSuffix(String gradeRequirement) {
        if (gradeRequirement.equals("")) {
            return "";
        }
        return " with a minimum grade of " + gradeRequirement;
    }

    /**
     * Formats a list of sequences (each represented as an array) into a string.
     *
     * @param sequences a list of course sequence arrays
     * @return a formatted string representing the sequences
     */
    private String formatSequences(List<String[]> sequences) {
        return sequences.stream()
                .map(arr -> "\n                    * (" + String.join(", ", arr) + ")")
                .collect(Collectors.joining(""));
    }

    /**
     * Combines the formatting for both number and type requirements.
     *
     * @param numRequirements a list of integer arrays representing number requirements
     * @param numberRequirements the original number requirements string
     * @param typeReqs a list of type requirement entries
     * @param typeRequirements the original type requirements string
     * @return a formatted string representing both number and type requirements
     */
    private String formatRequirements(ArrayList<int[]> numRequirements, String numberRequirements, 
            ArrayList<Map.Entry<Integer, String>> typeReqs, String typeRequirements) {

        if (numberRequirements.equals("") && typeRequirements.equals("")){
            return "";
        }

        StringBuilder result = new StringBuilder(" consisting of:");

        // First, check for special type requirements (WAC or WAC-R).
        int specialCount = 0;
        ArrayList<String> specialTypes = new ArrayList<>();
        Iterator<Map.Entry<Integer, String>> iter = typeReqs.iterator();
        while (iter.hasNext()) {
            Map.Entry<Integer, String> entry = iter.next();
            String type = entry.getValue();
            if (type.equals("WAC") || type.equals("WAC-R")) {
                specialCount += entry.getKey();
                if (!specialTypes.contains(type)) {
                    specialTypes.add(type);
                }
                iter.remove();
            }
        }
        String specialTypeStr = "";
        if (!specialTypes.isEmpty()) {
            if (specialTypes.size() == 1) {
                specialTypeStr = specialTypes.get(0);
            } else {
                specialTypeStr = String.join(" or ", specialTypes);
            }
        }

        if (!numberRequirements.equals("")) {
            StringBuilder sb = new StringBuilder("\n");
            for (int i = 0; i < numRequirements.size(); i++) {
                int[] pair = numRequirements.get(i);
                int count = pair[0];
                if (i < numRequirements.size() - 1) {
                    count -= numRequirements.get(i + 1)[0];
                }
                if (count > 0) {
                    if (pair[1] >= 300 && specialCount > 0) {
                        int generalOnlyCount = count - specialCount;
                        if (generalOnlyCount > 0) {
                            sb.append("                    * ")
                              .append(generalOnlyCount)
                              .append(" course(s) numbered")
                              .append(" >=")
                              .append(pair[1])
                              .append("\n");
                        }
                        sb.append("                    * ")
                          .append(specialCount)
                          .append(" course(s) numbered")
                          .append(" >=")
                          .append(pair[1])
                          .append(" and is also a ")
                          .append(specialTypeStr)
                          .append("\n");
                    } else {
                        sb.append("                    * ")
                          .append(count)
                          .append(" course(s) numbered")
                          .append(" >=")
                          .append(pair[1])
                          .append("\n");
                    }
                }
            }
            if (sb.length() > " consisting of:".length()) {
                sb.setLength(sb.length() - 1);
                result.append(sb);
            }
        }

        if (!typeRequirements.equals("")) {
            StringBuilder sb = new StringBuilder("\n");
            for (Map.Entry<Integer, String> pair : typeReqs) {
                if (pair.getKey() > 0) {
                    sb.append("                    * ")
                      .append(pair.getKey())
                      .append(" course(s) must be typed ")
                      .append(pair.getValue())
                      .append(", ");
                }
            }
            if (sb.length() > 1) {
                sb.setLength(sb.length() - 2);
                result.append(sb);
            }
        }

        return result.toString();
    }

    /**
     * Removes all courses from coursesLeft that appear in usedCourses.
     *
     * @param coursesLeft the list of courses that have not yet been used to fulfill requirements
     * @param usedCourses the list of courses that were used to fulfill a requirement
     */
    private void removeUsedCourses(List<Course> coursesLeft, List<Course> usedCourses) {
        coursesLeft.removeAll(usedCourses);
    }

    /**
     * Compares two grades based on a predefined grading scale.
     *
     * @param grade1 the first grade to compare
     * @param grade2 the second grade to compare
     * @return true if grade1 is greater than or equal to grade2, false otherwise
     */
    public static boolean compareGrades(String grade1, String grade2) {
        Map<String, Double> gradeScale = new HashMap<>(Map.ofEntries(
                Map.entry("CIP", 4.3), Map.entry("N/A", 4.3), Map.entry("T", 4.3),
                Map.entry("A", 4.0), Map.entry("A-", 3.7),
                Map.entry("B+", 3.3), Map.entry("B", 3.0), Map.entry("B-", 2.7),
                Map.entry("C+", 2.3), Map.entry("C", 2.0), Map.entry("C-", 1.7),
                Map.entry("D+", 1.3), Map.entry("D", 1.0), Map.entry("D-", 0.7),
                Map.entry("F", 0.0)
        ));

        return gradeScale.getOrDefault(grade1.trim(), -1.0) >= gradeScale.getOrDefault(grade2, -1.0);
    }

    /**
     * Returns a string representation of the student's academic data.
     *
     * @return a string containing graduation year, courses, majors, and minors
     */
    @Override
    public String toString() {
        return "gradYr: " + gradYr + "\ncourses: " + courses + "\nmajors: " + majors + "\nminors: " + minors;
    }

    /**
     * Generates a progress string representing the student's academic progress.
     *
     * @return a string detailing the student's graduation year and progress in majors and minors
     */
    public String getProgressString() {
        StringBuilder progress = new StringBuilder();
        progress.append("\n======== Student Progress ========\n");
        progress.append("Graduation Year: ").append(gradYr).append("\n");
        progress.append("==================================\n\n");
    
        // Process majors
        for (Concentration major : majors) {
            progress.append("Major: ").append(major.getName()).append("\n");
            progress.append(getConcentrationProgress(major));
        }
    
        // Process minors
        for (Concentration minor : minors) {
            progress.append("Minor: ").append(minor.getName()).append("\n");
            progress.append(getConcentrationProgress(minor));
        }
    
        return progress.toString();
    }
    
    /**
     * Returns the progress string for a specific concentration by processing its requirements.
     *
     * @param concentration the concentration (major or minor) to process
     * @return a string detailing the progress for the concentration
     */
    private String getConcentrationProgress(Concentration concentration) {
        StringBuilder result = new StringBuilder();
        List<Course> coursesLeft = new ArrayList<>(courses);
    
        for (Requirement req : concentration.getReqs()) {
            if (req.isSeq()) {
                result.append(processSequenceRequirement(req, coursesLeft));
            } else {
                result.append(processNonSequenceRequirement(req, coursesLeft));
            }
        }
    
        return result.toString();
    }
}