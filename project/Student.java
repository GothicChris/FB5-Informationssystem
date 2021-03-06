/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

/**
 *
 * @author Christopher
 */
public class Student implements edu.fhge.gdb.entity.Student{
    
    private String matrikelnummer;
    
    private String name;
    
    private String vorname;
    
    private String adresse;
    
    private Studienrichtung studienrichtung;

    public Student(String matrikelnummer, String name, String vorname,
            String adresse, Studienrichtung studienrichtung) {
        this.matrikelnummer = matrikelnummer;
        this.name = name;
        this.vorname = vorname;
        this.adresse = adresse;
        this.studienrichtung = studienrichtung;
    }

    @Override
    public String getMatrikel() {
        return matrikelnummer;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVorname() {
        return vorname;
    }

    @Override
    public String getAdresse() {
        return adresse;
    }

    @Override
    public String getStudienrichtungKuerzel() {
        return studienrichtung.getKuerzel();
    }

    @Override
    public String toString() {
        return name + ", " + vorname + " (" + matrikelnummer + ")";
    }
    
    public String getText() {
        return "Matrikelnummer=" + matrikelnummer 
                + ", Name=" + name 
                + ", Vorname=" + vorname 
                + ", Adresse=" + adresse 
                + ", Studienrichtung=" + studienrichtung;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj != null && getClass() == obj.getClass()) {
            Student student = (Student) obj;
            equal = this.matrikelnummer.equals(student.matrikelnummer) 
                    && this.name.toLowerCase().equals(student.name.toLowerCase()) 
                    && this.vorname.toLowerCase().equals(student.vorname.toLowerCase()) 
                    && this.adresse.replaceAll("[\\w]*[\n]*", "").toLowerCase().equals(student.adresse.replaceAll("[\\w]*[\n]*", "").toLowerCase()) 
                    && this.studienrichtung.equals(studienrichtung);
        }
        
        return equal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.matrikelnummer != null ? this.matrikelnummer.hashCode() : 0);
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 47 * hash + (this.vorname != null ? this.vorname.hashCode() : 0);
        hash = 47 * hash + (this.adresse != null ? this.adresse.hashCode() : 0);
        hash = 47 * hash + (this.studienrichtung != null ? this.studienrichtung.hashCode() : 0);
        return hash;
    }
    
    
    
    
    
}
