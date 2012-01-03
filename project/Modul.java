/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

/**
 *
 * @author Christopher
 */
public class Modul implements edu.fhge.gdb.entity.Modul {
    
    private String kuerzel;
    
    private String name;
    
    private int anzVorlesung;
    
    private int anzUebung;
    
    private int anzPraktikum;
    
    private int credits;

    public Modul(String kuerzel, String name, int anzVorlesung, int anzUebung,
            int anzPraktikum, int credits) {
        this.kuerzel = kuerzel;
        this.name = name;
        this.anzVorlesung = anzVorlesung;
        this.anzUebung = anzUebung;
        this.anzPraktikum = anzPraktikum;
        this.credits = credits;
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
    public int getVorlesung() {
        return anzVorlesung;
    }

    @Override
    public int getUebung() {
        return anzUebung;
    }

    @Override
    public int getPraktikum() {
        return anzPraktikum;
    }

    @Override
    public int getCredits() {
        return credits;
    }

    @Override
    public int hashCode() {
        return kuerzel.hashCode() * 7;
    }

    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if(obj != null && obj.getClass() == this.getClass()) {
            Modul modul = (Modul) obj;
            equal = this.kuerzel.equals(modul.kuerzel) &&
                    this.name.equals(modul.name) &&
                    this.anzPraktikum == modul.anzPraktikum &&
                    this.anzUebung == modul.anzUebung &&
                    this.anzVorlesung == modul.anzVorlesung &&
                    this.credits == modul.credits;
        }
        return equal;
    }

    @Override
    public String toString() {
        
        return name + " (" + kuerzel + ")";
    }
    
    public String getText() {
        String newKuerzel = this.kuerzel;
        
        switch (newKuerzel.length()) {
            case 2:
                newKuerzel = newKuerzel.concat("   ");
                break;
            case 3: newKuerzel = newKuerzel.concat("  ");
                break;
            case 4: newKuerzel = newKuerzel.concat(" ");
                break;
        }
        
        return newKuerzel + this.anzVorlesung + "  " 
               + this.anzUebung + "  " + this.anzPraktikum 
               + "  " + this.credits;
    }
    
    
    public int getWochenStunden() {
        return anzPraktikum + anzUebung + anzVorlesung;
    }
    
    
    
}
