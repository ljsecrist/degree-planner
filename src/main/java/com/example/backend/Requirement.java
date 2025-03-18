package com.example.backend;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Requirement class represents an academic requirement for a concentration.
 * It may consist of a list of course groups or sequences of sub-requirements.
 */
public class Requirement {
    private String title;
    private ArrayList<String> courseGroups;
    private ArrayList<Requirement> sequences;
    private int numNeeded;
    private String numberRequirements;
    private String typeRequirements;
    private String gradeRequirement;
    private ArrayList<String> numReqs;
    private ArrayList<String> typeReqs;

    /**
     * Creates a Requirement based on course groups.
     *
     * @param title the title of the requirement
     * @param courseGroups a list of course codes or groups
     * @param numNeeded the number of courses needed to fulfill the requirement
     * @param numberRequirements additional numerical constraints
     * @param typeRequirements additional type constraints
     * @param gradeRequirement the minimum grade requirement
     * @return a Requirement object created from course groups
     */
    public static Requirement fromCourseGroups(String title, ArrayList<String> courseGroups, int numNeeded, String numberRequirements, String typeRequirements, String gradeRequirement) {
        return new Requirement(title, courseGroups, numNeeded, numberRequirements, typeRequirements, gradeRequirement, null);
    }
    
    /**
     * Creates a Requirement based on sequences of sub-requirements.
     *
     * @param title the title of the requirement
     * @param sequences a list of sub-requirement sequences
     * @param numNeeded the number of sequences needed to fulfill the requirement
     * @param gradeRequirement the minimum grade requirement for the sequences
     * @return a Requirement object created from sequences
     */
    public static Requirement fromSequences(String title, ArrayList<Requirement> sequences, int numNeeded, String gradeRequirement) {
        return new Requirement(title, null, numNeeded, null, null, gradeRequirement, sequences);
    }
    
    /**
     * Private constructor for Requirement.
     *
     * @param title the title of the requirement
     * @param courseGroups a list of course groups
     * @param numNeeded the number of courses or sequences needed
     * @param numberRequirements additional numerical requirements
     * @param typeRequirements additional type requirements
     * @param gradeRequirement the grade requirement
     * @param sequences a list of sub-requirement sequences
     */
    private Requirement(String title, ArrayList<String> courseGroups, int numNeeded, String numberRequirements, String typeRequirements, String gradeRequirement, ArrayList<Requirement> sequences) {
        this.title = title;
        this.courseGroups = courseGroups;
        this.numNeeded = numNeeded;
        this.numberRequirements = numberRequirements;
        this.typeRequirements = typeRequirements;
        this.gradeRequirement = gradeRequirement;
        this.numReqs = createNumReqs();
        this.typeReqs = createTypeReqs();
        this.sequences = sequences;
    }
    
    /**
     * Determines if the requirement is based on sequences.
     *
     * @return true if the requirement is a sequence, false otherwise
     */
    public boolean isSeq() {
        return courseGroups == null;
    }
    
    /**
     * Returns the title of the requirement.
     *
     * @return the requirement title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Creates a list of numerical requirements from the numberRequirements string.
     *
     * @return an ArrayList of numerical requirement strings, or null if not applicable
     */
    private ArrayList<String> createNumReqs(){
        if (numberRequirements != null) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(numberRequirements.toString().split(",(?![^()]*\\))")));
            list.replaceAll(String::trim);
            return list;
        } else {
            return null;
        }
    }

    /**
     * Creates a list of type requirements from the typeRequirements string.
     *
     * @return an ArrayList of type requirement strings, or null if not applicable
     */
    private ArrayList<String> createTypeReqs(){
        if (typeRequirements != null) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(typeRequirements.toString().split(",(?![^()]*\\))")));
            list.replaceAll(String::trim);
            return list;
        } else {
            return null;
        }
    }

    /**
     * Returns the list of numerical requirements.
     *
     * @return an ArrayList of numerical requirement strings
     */
    public ArrayList<String> getNumReqs(){
        return numReqs;
    }

    /**
     * Returns the list of type requirements.
     *
     * @return an ArrayList of type requirement strings
     */
    public ArrayList<String> getTypeReqs(){
        return typeReqs;
    }

    /**
     * Returns the list of course groups.
     *
     * @return an ArrayList of course groups
     */
    public ArrayList<String> getCourseGroups() {
        return courseGroups;
    }

    /**
     * Returns the list of sub-requirement sequences.
     *
     * @return an ArrayList of Requirement sequences
     */
    public ArrayList<Requirement> getSequences() {
        return sequences;
    }

    /**
     * Returns the number of courses or sequences needed to fulfill the requirement.
     *
     * @return the number needed
     */
    public int getNumNeeded() {
        return numNeeded;
    }

    /**
     * Returns the numerical requirements string.
     *
     * @return the numerical requirements
     */
    public String getNumberRequirements() {
        return numberRequirements;
    }

    /**
     * Returns the type requirements string.
     *
     * @return the type requirements
     */
    public String getTypeRequirements() {
        return typeRequirements;
    }

    /**
     * Returns the grade requirement.
     *
     * @return the grade requirement
     */
    public String getGradeRequirement(){
        return gradeRequirement;
    }

    /**
     * Determines if the requirement is an elective.
     * A requirement is considered elective if it has a single course group without a dash.
     *
     * @return true if the requirement is elective, false otherwise
     */
    public boolean isElective() {
        return courseGroups.size() == 1 && !courseGroups.get(0).contains("-");
    }

    /**
     * Returns a string representation of the requirement.
     *
     * @return a string detailing the requirement title, number needed, course groups, and any additional requirements
     */
    @Override
    public String toString() {
        return title + " (" + numNeeded + " needed): " + String.join(" | ", courseGroups) +
                (numberRequirements.isEmpty() ? "" : " [" + numberRequirements + "]") +
                (typeRequirements.isEmpty() ? "" : " [" + typeRequirements + "]");
    }
}