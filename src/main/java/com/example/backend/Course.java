package com.example.backend;
import java.util.ArrayList;

/**
 * Represents a course with details such as term, year, code, title, types, credits, AP status, and grade.
 */
public class Course {
    /**
     * The term during which the course is offered.
     */
    private String term;
    
    /**
     * The year the course is offered.
     */
    private String year;
    
    /**
     * The code identifying the course.
     */
    private String code;
    
    /**
     * The title of the course.
     */
    private String title;
    
    /**
     * A list of course types.
     */
    private ArrayList<String> types;
    
    /**
     * The number of credits for the course.
     */
    private int credits;
    
    /**
     * Indicates if the course is an Advanced Placement (AP) course.
     */
    private boolean isAP;
    
    /**
     * The grade received for the course.
     */
    private String grade;

    /**
     * Constructs a Course object with the specified details.
     *
     * @param term the term during which the course is offered
     * @param year the year the course is offered
     * @param code the code identifying the course
     * @param title the title of the course
     * @param types a list of course types
     * @param credits the number of credits for the course
     * @param grade the grade received for the course
     */
    public Course(String term, String year, String code, String title, ArrayList<String> types, int credits, String grade){
        this.term = term;
        this.year = year;
        this.code = code;
        this.title = title;
        this.types = types;
        this.credits = credits;
        this.isAP = false;
        this.grade = grade;
    }

    /**
     * Constructs an Advanced Placement (AP) Course object with the specified code and credits.
     * Term, year, title, and types are set to null, and grade is set to "N/A".
     *
     * @param code the code identifying the course
     * @param credits the number of credits for the course
     */
    public Course(String code, int credits){
        this(null, null, code, null, null, credits, "N/A");
        this.isAP = true;
    }

    /**
     * Returns the term during which the course is offered.
     *
     * @return the course term
     */
    public String getTerm(){
        return term;
    }

    /**
     * Returns the department code, which is the first four characters of the course code.
     *
     * @return the department code
     */
    public String getDept() {
        return code.substring(0,4);
    }

    /**
     * Returns the year the course is offered.
     *
     * @return the course year
     */
    public String getYear(){
        return year;
    }

    /**
     * Returns the code identifying the course.
     *
     * @return the course code
     */
    public String getCode(){
        return code;
    }

    /**
     * Returns the title of the course.
     *
     * @return the course title
     */
    public String getTitle(){
        return title;
    }

    /**
     * Returns the list of course types.
     *
     * @return an ArrayList of course types
     */
    public ArrayList<String> getTypes(){
        return types;
    }

    /**
     * Returns the number of credits for the course.
     *
     * @return the course credits
     */
    public int getCredits(){
        return credits;
    }

    /**
     * Indicates whether the course is an Advanced Placement (AP) course.
     *
     * @return true if the course is AP, false otherwise
     */
    public boolean isAP(){
        return isAP;
    }

    /**
     * Returns the grade received for the course.
     *
     * @return the course grade
     */
    public String getGrade(){
        return grade;
    }

    /**
     * Returns a string representation of the course, including code, title, types, term, year, and credits.
     *
     * @return a string representation of the course
     */
    public String toString(){
        return code + "; " + title + "; " + types + "; " + term + "; " + year + "; " + credits;
    }
}