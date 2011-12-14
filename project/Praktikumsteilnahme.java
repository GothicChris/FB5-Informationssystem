/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import edu.fhge.gdb.entity.Modul;
import edu.fhge.gdb.entity.Student;

/**
 *
 * @author Christopher
 */
public class Praktikumsteilnahme implements edu.fhge.gdb.entity.Praktikumsteilnahme{

    private Student student;
    
    private Modul modul;
    
    private String semester;
    
    private boolean testat;

    public Praktikumsteilnahme(Student student, Modul modul, String semester,
            boolean testat) {
        this.student = student;
        this.modul = modul;
        this.semester = semester;
        this.testat = testat;
    }
        
    
    @Override
    public Student getStudent() {
        return student;
    }

    @Override
    public Modul getModul() {
        return modul;
    }

    @Override
    public String getSemester() {
        return semester;
    }

    @Override
    public boolean isTestat() {
        return testat;
    }

    @Override
    public void setTestat(boolean testat) {
        this.testat = testat;
    }

    @Override
    public boolean equals(Object obj) {
        
        boolean isEqual = false;
        
        if(obj != null && obj.getClass() == this.getClass()) {
            Praktikumsteilnahme otherPT = (Praktikumsteilnahme) obj;
            
            isEqual = (this.modul.equals(otherPT.modul)
                    && this.semester.equals(otherPT.semester)
                    && this.student.equals(otherPT.student)
                    && this.testat == otherPT.testat);
        }
        
        return isEqual;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.student != null ? this.student.hashCode() : 0);
        hash = 37 * hash + (this.modul != null ? this.modul.hashCode() : 0);
        hash = 37 * hash + (this.semester != null ? this.semester.hashCode() : 0);
        hash = 37 * hash + (this.testat ? 1 : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Praktikumsteilnahme{" + "student=" + student + ", modul="
                + modul + ", semester=" + semester + ", testat=" + testat + '}';
    }
}
