/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

/**
 *
 * @author Christopher
 */
public class Studienrichtung implements edu.fhge.gdb.entity.Studienrichtung{

    private String kuerzel;
    
    private String name;

    public Studienrichtung(String kuerzel, String name) {
        this.kuerzel = kuerzel;
        this.name = name;
    }
    
    @Override
    public String getKuerzel() {
        return kuerzel;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj != null && getClass() == obj.getClass()) {
         Studienrichtung studienrichtung = (Studienrichtung) obj;
         equal = this.kuerzel.equals(studienrichtung.kuerzel) &&
                 this.name.equals(studienrichtung.name);
        }
        return equal;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.kuerzel != null ? this.kuerzel.hashCode() : 0);
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return name + " (" + kuerzel + ")";
    }
    
    
}
