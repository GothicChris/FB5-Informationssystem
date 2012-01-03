/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Christopher
 */
public class Studienverlauf {
    
    private String kategorie;
    
    private int kategorieNr;
    
    private HashMap<Integer, ArrayList<Modul>> semester;
    
    
    public Studienverlauf(int semesterNr, String kategorie) {
        this.kategorie = kategorie;
        this.kategorieNr = semesterNr;
        semester = new HashMap<Integer, ArrayList<Modul>>();
    }
    
    

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public int getKategorieNr() {
        return kategorieNr;
    }

    public void setKategorieNr(int kategorieNr) {
        this.kategorieNr = kategorieNr;
    }

    public HashMap<Integer, ArrayList<Modul>> getSemester() {
        return semester;
    }
    
    public String getSemesterModulText(int semesterNr) {
        String returnString = "";
        ArrayList<Modul> module = new ArrayList<Modul>();
        if(semester.containsKey(semesterNr)) {
            module = semester.get(semesterNr);
            for(int i = 0; i < module.size(); i++) {
                returnString += "\n" + module.get(i).getText();
            }
        } else {
            returnString = "";
        }
        return returnString;
    }

    public Set getSemesterNummern() {
        return semester.keySet();
    }
    public void setSemester(HashMap<Integer, ArrayList<Modul>> semester) {
        this.semester = semester;
    }
    
    public void addSemesterModul(int semesterNr, Modul modul) {
        int sNr = semesterNr;
        ArrayList<Modul> semesterModule = new ArrayList<Modul>();
        if(semester.containsKey(semesterNr)) {
            semesterModule = semester.get(semesterNr);
        }
        semesterModule.add(modul);
        
        semester.put(sNr, semesterModule);
        
    }
    
    public int getWochenStundenVonSemester(int semesterNr) {
        int wochenStunden = 0;
        if(semester.containsKey(semesterNr)) {
            ArrayList<Modul> module = semester.get(semesterNr);
            for(int i = 0; i < module.size(); i++) {
                wochenStunden += module.get(i).getWochenStunden();                
            }
        }
        
        return wochenStunden;
    }

    public int getVerlaufWochenStunden() {
        
        int verlaufStunden = 0;
        for(int i = 1; i <= 6; i++) {
            verlaufStunden += getWochenStundenVonSemester(i);
        }
        return verlaufStunden;
    }
    
    
    @Override
    public String toString() {
        return kategorieNr + " " + kategorie;
    }
    
    
    
    
}
