/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import edu.fhge.gdb.ApplicationException;
import edu.fhge.gdb.entity.Modul;
import edu.fhge.gdb.entity.Praktikumsteilnahme;
import edu.fhge.gdb.entity.Student;
import edu.fhge.gdb.entity.Studienrichtung;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;



/**
 *
 * @author Christopher
 */
public class DataAccessObject implements edu.fhge.gdb.DataAccessObject {
    
    /**
     * 
     */
    Connection connection;

    
    /**
     * 
     */
    private String jdbcURL;

    /**
     * 
     */
    public DataAccessObject() {
        jdbcURL = "jdbc:derby:eqal";
    }

    /**
     * 
     * @param jdbcURL 
     */
    public DataAccessObject(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }
    
    /**
     * 
     * @param sr
     * @return
     * @throws ApplicationException 
     */
    @Override
    public List<List<String>> getStudienverlaufsplan(Studienrichtung sr)
            throws ApplicationException {
        
        List<List<String>> spalten = new ArrayList<List<String>>();
        
        
        ArrayList<String> zeile1 = new ArrayList();
        
        zeile1.add("Studienverlaufplan\n" + sr.toString());
        zeile1.add("1. Semester");
        zeile1.add("2. Semester");
        zeile1.add("3. Semester");
        zeile1.add("4. Semester");
        zeile1.add("5. Semester");
        zeile1.add("6. Semester");
        zeile1.add("");
        
        spalten.add(zeile1);
        
        
        ArrayList<String> zeile2 = new ArrayList();
        
        zeile2.add("Nr  Kategorie");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Summe");
        
        spalten.add(zeile2);
        
        
        ArrayList<Studienverlauf> studienverlauf = new ArrayList();
        
        String sql = "SELECT SR.SKUERZEL, K.LFDNR, K.NAME, "
            + "KU.SEM, KU.MKUERZEL, M.MODULNAME, M.VL, "
            + "M.UB, M.PR, M.CREDITS "
            + "FROM KATEGORIE K, KATEGORIEUMFANG KU, MODUL M, STUDIENRICHTUNG SR " 
            + "WHERE SR.SKUERZEL = '" + sr.getKuerzel() + "' "
            + "AND SR.SKUERZEL = KU.SKUERZEL "
            + "AND SR.SKUERZEL = K.SKUERZEL "
            + "AND K.LFDNR = KU.LFDNR " 
            + "AND M.MKUERZEL = KU.MKUERZEL "
            + "ORDER BY K.LFDNR, KU.SEM";
        
        ResultSet sqlResult = null;
        try {
            
            sqlResult = executeQuery(sql);
            
            Studienverlauf sv = new Studienverlauf(0, "");
            while(sqlResult.next()) {
                
                int vl = sqlResult.getInt("VL");
                int ub = sqlResult.getInt("UB");
                int pr = sqlResult.getInt("PR");
                project.Modul modul = new project.Modul(
                        sqlResult.getString("MKUERZEL"),
                        sqlResult.getString("MODULNAME"),
                        vl,
                        ub,
                        pr, 
                        sqlResult.getInt("CREDITS"));
                if(sv.getKategorieNr() == sqlResult.getInt("LFDNR")) {
                    
                    sv.addSemesterModul(sqlResult.getInt("SEM"), modul);
                } else {
                    
                    sv = new Studienverlauf(sqlResult.getInt("LFDNR"), 
                                                 sqlResult.getString("NAME"));
                    sv.addSemesterModul(sqlResult.getInt("SEM"), modul);
                    studienverlauf.add(sv);
                }
            }
        } catch (SQLException ex) {
            
            System.out.println(ex.getMessage());
        }
        
        
        for(int i = 0; i < studienverlauf.size(); i++) {
            
            ArrayList<String> zeile = new ArrayList();
            Studienverlauf aktVerlauf = studienverlauf.get(i);
            zeile.add(aktVerlauf.getKategorieNr() + "  " + aktVerlauf.getKategorie());
            zeile.add(aktVerlauf.getSemesterModulText(1));
            zeile.add(aktVerlauf.getSemesterModulText(2));
            zeile.add(aktVerlauf.getSemesterModulText(3));
            zeile.add(aktVerlauf.getSemesterModulText(4));
            zeile.add(aktVerlauf.getSemesterModulText(5));
            zeile.add(aktVerlauf.getSemesterModulText(6));
            zeile.add("" + aktVerlauf.getVerlaufWochenStunden());
            spalten.add(zeile);
        }
        
        ArrayList<String> letzteZeile = new ArrayList();
        letzteZeile.add("Summe SWS");
        
        int gesamtsumme = 0;
        
        for(int i = 1; i < 7; i++) {
            
            int zwischensumme = 0;
            for(int j = 0; j < studienverlauf.size(); j++) {
                zwischensumme += studienverlauf.get(j)
                        .getWochenStundenVonSemester(i);
            }
            
            letzteZeile.add("" + zwischensumme);
            gesamtsumme += zwischensumme;
        }
        
        letzteZeile.add("" + gesamtsumme);
        
        spalten.add(letzteZeile);
        
        return spalten;
    }

    /**
     * 
     * @param matrikel
     * @param name
     * @param vorname
     * @param adresse
     * @param srKuerzel
     * @throws ApplicationException 
     */
    @Override
    public void addStudent(String matrikel, String name, String vorname,
            String adresse, String srKuerzel) throws ApplicationException {
        
        
        String sql = "INSERT INTO STUDENT"
                + " (MATRIKEL, NAME, VORNAME, ADRESSE, SKUERZEL)"
                + " VALUES ('" + matrikel + "', '" + name + "', '"
                + vorname + "'," + " '" + adresse + "', '" + srKuerzel + "')";
        
        try {
            
            execute(sql);
            System.out.println("eingetragen");
        } catch (SQLException ex) {
            
            System.out.println(ex.getMessage());
            throw new ApplicationException("Matrikelnummer bereits vergeben.");
        }
    }

    /**
     * 
     * @return 
     */
    @Override
    public Collection<Student> getAllStudent() {
        
        ArrayList<Student> studentList = new ArrayList<Student>();
        
        
        String sql = "SELECT S.*, SR.SKUERZEL AS SRKUERZEL, SR.NAME AS SRNAME"
                + " FROM STUDENT S, STUDIENRICHTUNG SR"
                + " WHERE S.SKUERZEL = SR.SKUERZEL"
                + " ORDER BY S.NAME";
        
        ResultSet sqlResult = null;
        try {
            
            sqlResult = executeQuery(sql);
            
            while(sqlResult.next()) {
                
                Student tempStudent = new project.Student(
                        sqlResult.getString("MATRIKEL"),
                        sqlResult.getString("NAME"),
                        sqlResult.getString("VORNAME"),
                        sqlResult.getString("ADRESSE"),
                        new project.Studienrichtung(
                                sqlResult.getString("SRKUERZEL"),
                                sqlResult.getString("SRNAME")));
                
                studentList.add(tempStudent);
            }
        } catch (SQLException ex) {
            
            System.out.println(ex.getMessage());
        }
        
        
        return studentList;
    }

    /**
     * 
     * @param matrikel
     * @param name
     * @param vorname
     * @param adresse
     * @param srKuerzel
     * @param modul
     * @param semester
     * @return
     * @throws ApplicationException 
     */
    @Override
    public boolean announce(String matrikel, String name, String vorname,
            String adresse, String srKuerzel, Modul modul, String semester)
            throws ApplicationException {
        
        boolean announced = true;
        String sql;
        ResultSet resultSet =  null;
        
        /**
         * Prüfung ob das Modul überhaupt ein Praktikum vorsieht.
         */
        sql = "SELECT PR"
                + " FROM MODUL"
                + " WHERE MKUERZEL = '" + modul.getKuerzel() + "'";
        
        try {
            resultSet = executeQuery(sql);
        
            if(resultSet.next() && resultSet.getInt("PR") == 0) {
                
                throw new ApplicationException("Das Modul sieht kein Praktikum vor");
            }
            
        } catch (SQLException ex) {
            
            throw new ApplicationException(ex.getMessage());
        }
        
        /*
         * Prüfung ob das übergebene Modul nicht Bestandteil der Studienrichtung
         * des anzumeldenden Studenten ist.
         */
        sql = "SELECT *"
                + " FROM KATEGORIEUMFANG"
                + " WHERE MKUERZEL = '" + modul.getKuerzel() + "'"
                + " AND SKUERZEL = '" + srKuerzel + "'";
        
        try {
            resultSet = executeQuery(sql);
        
            if(!resultSet.next()) {
                
                throw new ApplicationException("Der Student hat dieses Modul nicht belegt.");
            }
            
        } catch (SQLException ex) {
            
            throw new ApplicationException(ex.getMessage());
        }
        
        sql = "SELECT NAME"
                + " FROM STUDIENRICHTUNG"
                + " WHERE SKUERZEL = '" + srKuerzel + "'";
        
        try {
            
            resultSet = executeQuery(sql);
        } catch (SQLException ex) {
            
            throw new ApplicationException(ex.getMessage());
        }
        
        project.Studienrichtung targetStudienrichtung = null;
        try {
            
            if(resultSet.next()) {
                
                targetStudienrichtung =
                        new project.Studienrichtung(srKuerzel,
                                resultSet.getString("NAME"));
            } else {
                throw new ApplicationException(
                        "Studienrichtung nicht vorhanden!");
            }
        } catch (SQLException ex) {
            
            throw new ApplicationException(ex.getMessage());
        }
        
        project.Student studentToAnnounce = new project.Student(matrikel, name,
                vorname, adresse, targetStudienrichtung);
        
        /*
         * Zum Vergleich des Studenten, der eingetragen werden muss und dem
         * Studenten der in der Datenbank zu der übergebenen vorliegt
         */
        sql = "SELECT S.*, SR.NAME AS SRNAME"
                + " FROM STUDENT S, STUDIENRICHTUNG SR"
                + " WHERE MATRIKEL = '" + matrikel + "'"
                + " AND S.SKUERZEL = SR.SKUERZEL";
        
        try {
            
            resultSet = executeQuery(sql);
        } catch (SQLException ex) {
            
            throw new ApplicationException(ex.getMessage());
        }
        
        project.Studienrichtung sfdbStudienrichtung = null;
        project.Student studentFromDatabase = null;
        
        try {
            
            if(resultSet.next()) {
                sfdbStudienrichtung = new project.Studienrichtung(srKuerzel,
                        resultSet.getString("SRNAME"));
                studentFromDatabase = new project.Student(
                        resultSet.getString("MATRIKEL"),
                        resultSet.getString("NAME"),
                        resultSet.getString("VORNAME"),
                        resultSet.getString("ADRESSE"),
                        sfdbStudienrichtung);
            }
        } catch (SQLException ex) {
            
            throw new ApplicationException(ex.getMessage());
        }
        
        if(studentFromDatabase == null)
        {
            
            addStudent(studentToAnnounce.getMatrikel(),
                    studentToAnnounce.getName(),
                    studentToAnnounce.getVorname(),
                    studentToAnnounce.getAdresse(),
                    studentToAnnounce.getStudienrichtungKuerzel());
        
            sql = "INSERT INTO PRAKTIKUMSTEILNAHME"
                    + " VALUES('" + studentToAnnounce.getMatrikel() + "',"
                    + " '" + modul.getKuerzel() + "' ,"
                    + " '" + semester + "',"
                    + " 0)";
        } else {
            
            if(studentToAnnounce.equals(studentFromDatabase))
            {
                
                sql = "INSERT INTO PRAKTIKUMSTEILNAHME"
                        + " VALUES('" + studentToAnnounce.getMatrikel() + "',"
                        + " '" + modul.getKuerzel() + "' ,"
                        + " '" + semester + "',"
                        + " 0)";
            } else {
                
                throw new ApplicationException("Student mit anderen Daten bereits im System vorhanden.");
            }
        }
        
        try {

            execute(sql);
        } catch (SQLException ex) {

            announced = false;
        }

        return announced;
        
    }

    @Override
    public void setTestate(Collection<Praktikumsteilnahme> testate) {
        /**
         * Alle Testate Updaten
         */

        for(Praktikumsteilnahme praktikum : testate) {
                
            int testat = (praktikum.isTestat()) ? 1 : 0;
            String sql =" UPDATE PRAKTIKUMSTEILNAHME SET TESTAT = " + testat
                        + " WHERE MATRIKEL = '" + praktikum.getStudent().getMatrikel() + "'"
                        + " AND MKUERZEL = '" + praktikum.getModul().getKuerzel() + "'"
                        + " AND SEMESTER = '" + praktikum.getSemester() + "'";
            try {
                execute(sql);
            } catch (SQLException ex) {
                System.out.println("Fehler beim setzen der Testate\n"
                        + ex.getMessage());
            }
            
        }
    }

    @Override
    public JPanel getChart(int type, Object parameter1, Object parameter2)
            throws ApplicationException {
        // TODO Methode ausfertigen
        
        
        JFreeChart chart = null;
        
        switch(type) {
            case VISUALISIERUNG_ANTEIL_TESTATABNAHMEN: {
                
                /* Parameter 1 muss String sein, Parameter 2 muss NULL sein.*/
                if(!(parameter1 instanceof String && parameter2 == null)) {
                    throw new ApplicationException("Die übergebenen Basisparameter passen nicht zum Visualisierungstyp.");
                }
                
                String semester = (String) parameter1;
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                
                /* Iteration über alle Studienrichtungen. */
                for(Studienrichtung studr : this.getAllStudienrichtung()) {
                    
                    /* Anzahl der Studenten die sich für ein Praktikum eines 
                      Moduls in einem bestimmten Semester angemeldet haben. */
                    String sql = "SELECT M.MKUERZEL AS Modul, Count(*) AS Anmeldungen"
                               + " FROM MODUL M, PRAKTIKUMSTEILNAHME P, STUDENT S" 
                               + " WHERE P.MATRIKEL = S.MATRIKEL"
                               + " AND M.MKUERZEL = P.MKUERZEL"
                               + " AND M.PR > 0"
                               + " AND P.SEMESTER = '" + semester + "'"
                               + " AND S.SKUERZEL = '" + studr.getKuerzel() + "'"
                               + " GROUP BY M.MKUERZEL";
                    
                    ResultSet resultSet = null;
                    try {
                        
                        resultSet = executeQuery(sql);
                        while(resultSet.next()) {
                            /* Speichern der Anzahl der ANmeldungen zu einem 
                             * Modul */
                            String modul = resultSet.getString("Modul");
                            int anmeldungen = resultSet.getInt("Anmeldungen");
                            
                            /* Anzahl der Bestandenen Praktikas eines Moduls
                             in einem bestimmten Semester*/
                            sql = "SELECT M.MKUERZEL AS Modul, Count(*) AS Bestanden"
                                    + " FROM MODUL M, PRAKTIKUMSTEILNAHME P, STUDENT S" 
                                    + " WHERE P.MATRIKEL = S.MATRIKEL"
                                    + " AND M.MKUERZEL = P.MKUERZEL"
                                    + " AND M.MKUERZEL = '" + modul + "'"
                                    + " AND M.PR > 0"
                                    + " AND P.SEMESTER = '" + semester + "'"
                                    + " AND S.SKUERZEL = '" + studr.getKuerzel() + "'"
                                    + " AND P.TESTAT > 0"
                                    + " GROUP BY M.MKUERZEL";
                            
                            int bestanden = 0;
                            ResultSet resultSet1 = executeQuery(sql);
                            if(resultSet1.next()) {
                                bestanden = resultSet1.getInt("Bestanden");
                            }
                            
                            /* Berechnung des Prozentualen Anteils von 
                             bestanden zu teilgenommen */
                            double wert = (bestanden == 0) 
                                    ? 0 
                                    : (double) bestanden / (double) anmeldungen * 100;
                            
                            /* Dataset die Werte übergeben
                              % - Wert, Modulname, Studienrichtung */
                            dataset.addValue(wert, modul, studr.getKuerzel());
                        }

                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                    
                    
                }
                /* Neues Balken Diagramm erzeugen mit folgenden Werte:
                 * Titel, X-Achse Beschreibung, Y-Achse Beschreibung, Daten,
                 * Vertical/Horizontal, Legende, Tooltip, URL. 
                 */
                chart = ChartFactory.createBarChart(semester,
                        "Praktikumsmodule nach Studienrichtung",
                        "Erfolgreiche Teilnahme in %",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false);

            } 
            break;
            
            case VISUALISIERUNG_AUFTEILUNG_ANMELDUNGEN: {
                
                if(!(parameter1 instanceof Studienrichtung
                    && parameter2 instanceof String)) {
                    throw new ApplicationException("Die übergebenen Basisparameter passen nicht zum Visualisierungstyp.");
                }
                
                Studienrichtung studienrichtung = (Studienrichtung) parameter1;
                String semester = (String) parameter2;
                
                DefaultPieDataset dataset = new DefaultPieDataset();
                
                String sql = "SELECT M.MKUERZEL AS Modul, Count(*) AS Anmeldungen"
                               + " FROM MODUL M, PRAKTIKUMSTEILNAHME P, STUDENT S" 
                               + " WHERE P.MATRIKEL = S.MATRIKEL"
                               + " AND M.MKUERZEL = P.MKUERZEL"
                               + " AND M.PR > 0"
                               + " AND P.SEMESTER = '" + semester + "'"
                               + " AND S.SKUERZEL = '" + studienrichtung.getKuerzel() + "'"
                               + " GROUP BY M.MKUERZEL";
                try {
                    ResultSet resultSet = executeQuery(sql);

                    int i = 0;
                    
                    while(resultSet.next()) {

                        int tmpAnmelungen = resultSet.getInt("Anmeldungen");
                        String tmpModul = resultSet.getString("Modul");
                        
                        /* SchlüsselText zusammenbauen */
                        String key = tmpModul + ": " + tmpAnmelungen + " Anmeldungen";
                        
                        dataset.insertValue(i, key, tmpAnmelungen);
                        i++;
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                
                /* PieChart erstellen: Titel, Daten, Legende, ToolTip, URL */
                chart = ChartFactory.createPieChart(
                        studienrichtung.toString() + " (" + semester + ")", 
                        dataset, 
                        true, 
                        true, 
                        false);
                
            } 
            break;
            
            case VISUALISIERUNG_ENTWICKLUNG_ANMELDUNGEN: {
                if(!(parameter1 instanceof Modul
                    && parameter2 == null)) {
                    throw new ApplicationException("Die übergebenen Basisparameter passen nicht zum Visualisierungstyp.");
                }
                
                Modul modul = (Modul) parameter1;
                
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                
                /* Anmeldungen/Testate zu einem Modul in allen Semestern*/
                String sql = "SELECT count(*) AS Anmeldungen, P.SEMESTER, SUM(P.TESTAT) AS Testatvergaben"
                            + " FROM PRAKTIKUMSTEILNAHME P" 
                            + " WHERE P.MKUERZEL = '" + modul.getKuerzel() + "'"
                            + " GROUP BY P.SEMESTER";
                try {
                    ResultSet resultSet = executeQuery(sql);
                    
                    while(resultSet.next()) {
                        String semester = resultSet.getString("Semester");
                        int anmeldungen = resultSet.getInt("Anmeldungen");
                        int testate = resultSet.getInt("Testatvergaben");
                        dataset.addValue(anmeldungen, "Anmeldungen", semester);
                        dataset.addValue(testate, "Testatvergaben", semester);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                
               chart = ChartFactory.createLineChart(
                        modul.getName() + " (" + modul.getKuerzel() + ")", 
                        "Semester", 
                        "Studierende", 
                        dataset, 
                        PlotOrientation.VERTICAL, 
                        true, 
                        true, 
                        false);
                
            } 
            break;
            
            case VISUALISIERUNG_ANMELDUNGEN_TESTATE: {
                if(!(parameter1 instanceof Studienrichtung
                    && parameter2 instanceof String)) {
                    throw new ApplicationException("Die übergebenen Basisparameter passen nicht zum Visualisierungstyp.");
                }
                
                Studienrichtung studienrichtung = (Studienrichtung) parameter1;
                String semester = (String) parameter2;
                
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                
                String sql = "SELECT count(*) AS Anmeldungen, SUM(P.TESTAT) AS Testatvergaben, P.MKUERZEL AS Modul"
                            + " FROM PRAKTIKUMSTEILNAHME P, KATEGORIEUMFANG K"
                            + " WHERE P.SEMESTER = '" + semester + "'"
                            + " AND P.MKUERZEL = K.MKUERZEL"
                            + " AND K.SKUERZEL = '" + studienrichtung.getKuerzel() + "'"
                            + " GROUP BY P.MKUERZEL";
                try {
                    ResultSet resultSet = executeQuery(sql);

                    while(resultSet.next()) {
                        String modulName = resultSet.getString("Modul");
                        int anmeldungen = resultSet.getInt("Anmeldungen");
                        int testatvergaben = resultSet.getInt("Testatvergaben");
                        
                        dataset.addValue(anmeldungen, "Anmeldungen", modulName);
                        dataset.addValue(testatvergaben, "Testatvergaben", modulName);
                    }

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                
                chart = ChartFactory.createBarChart(
                        studienrichtung.getName() + " (" + studienrichtung.getKuerzel() + ")", 
                        "Modul", 
                        "Studierende", 
                        dataset, 
                        PlotOrientation.VERTICAL, 
                        true, 
                        true, 
                        false);
            } 
            break;
            
            default: {
                
                throw new ApplicationException("Der Visualisierungstyp ist nicht definiert.");
            }
        }
        
        
        return new ChartPanel(chart);
            
        
        
    }

    /**
     * PDF Creator
     * @param target
     * @param semester 
     */
    @Override
    public void exportAnnouncementList(OutputStream target, String semester) {
        // TODO Methode ausfertigen
        try {
            Document pdfDocument = new Document(PageSize.A4);
            pdfDocument.setMargins(40, 40, 40, 40);
            PdfWriter.getInstance(pdfDocument, target);
            pdfDocument.open();
            
            String sql = "SELECT P.MKUERZEL, M.MODULNAME, P.SEMESTER, SR.SKUERZEL, S.MATRIKEL, S.VORNAME, S.NAME"
                        + " FROM PRAKTIKUMSTEILNAHME P, STUDIENRICHTUNG SR, KATEGORIEUMFANG KU, STUDENT S, MODUL M"
                        + " WHERE P.SEMESTER = '" + semester + "'"
                        + " AND P.MKUERZEL = KU.MKUERZEL"
                        + " AND KU.SKUERZEL = SR.SKUERZEL"
                        + " AND P.MATRIKEL = S.MATRIKEL"
                        + " AND S.SKUERZEL = SR.SKUERZEL"
                        + " AND P.MKUERZEL = M.MKUERZEL"
                        + " GROUP BY P.MKUERZEL, M.MODULNAME, P.SEMESTER, SR.SKUERZEL, S.MATRIKEL, S.VORNAME, S.NAME" 
                        + " ORDER BY MKUERZEL, S.NAME";

            /* Modul: SKuerzel, List: */
            HashMap<project.Modul, HashMap<String, Set>> modulTabelle = new HashMap<project.Modul, HashMap<String, Set>>();
            
            ResultSet resultSet = executeQuery(sql);
            
            while(resultSet.next()) {
                String mkuerzel = resultSet.getString("MKUERZEL");
                String modulname = resultSet.getString("MODULNAME");
                String skuerzel = resultSet.getString("SKUERZEL");
                String matrikel = resultSet.getString("MATRIKEL");
                String vorname = resultSet.getString("VORNAME");
                String name = resultSet.getString("NAME");
                
                project.Modul tmpModul = new project.Modul();
                tmpModul.setKuerzel(mkuerzel);
                tmpModul.setName(modulname);
                
                
                if(modulTabelle.containsKey(tmpModul)) {
                    
                    HashMap<String, Set> inhalt = modulTabelle.get(tmpModul);
                    
                    inhalt.get("Studienrichtung").add(skuerzel);
                    
                    project.Student student = new project.Student(matrikel, name, vorname, null, null);
                    inhalt.get("Student").add(student);
                } else {
                    
                    HashMap<String, Set> inhalt = new HashMap<String, Set>();
                    
                    Set studienrichtungen = new HashSet();
                    studienrichtungen.add(skuerzel);
                    
                    project.Student student = new project.Student(matrikel, name, vorname, null, null);
                    Set<project.Student> studenten = new HashSet<project.Student>();
                    studenten.add(student);
                    
                    inhalt.put("Studienrichtung", studienrichtungen);
                    inhalt.put("Student", studenten);
                    
                    modulTabelle.put(tmpModul, inhalt);
                }
            }
            
            /*
             * Liste der Modultabllen ist erstellt. Jetzt muss das Dokument erstellt werden.
             */
            Set<project.Modul> modulSet = modulTabelle.keySet();
            ArrayList<project.Modul> modulListe = new ArrayList<project.Modul>(modulSet);
            Collections.sort(modulListe);
            Iterator<project.Modul> iterator = modulListe.iterator();

            while(iterator.hasNext()) {
                project.Modul aktuellesModul = iterator.next();
                Set<String> studienrichtungen = modulTabelle.get(aktuellesModul)
                        .get("Studienrichtung");
                Set<Student> studenten = modulTabelle.get(aktuellesModul)
                        .get("Student");

                pdfDocument.newPage();

                PdfPTable tabelle = new PdfPTable(3);

//                Rectangle tablesize = PageSize.A4;
//                
//                tablesize.setBottom(tablesize.getBottom() - 40.0F);
//                tablesize.setTop(tablesize.getTop() + 40.0F);
//                tablesize.setLeft(tablesize.getLeft() + 40.0F);
//                tablesize.setRight(tablesize.getRight() - 40.0F);
                
//                float spaltenBreiten[] = {30.0F, 20.0F, 10.0F};
//                tabelle.setWidthPercentage(spaltenBreiten, PageSize.A4);
                PdfPCell zelle = new PdfPCell(new Phrase(aktuellesModul.toString()));
                zelle.setColspan(2);

                tabelle.addCell(zelle);

                tabelle.addCell(semester);

                String studienrichtungenString = "";

                for (String skuerzel : studienrichtungen) {
                    studienrichtungenString += (skuerzel + "/");
                }

                studienrichtungenString = studienrichtungenString.substring(0, studienrichtungenString.length() - 1);

                zelle = new PdfPCell(new Phrase(studienrichtungenString));
                zelle.setColspan(3);
                tabelle.addCell(zelle);

                tabelle.addCell("Name, Vorname");

                zelle = new PdfPCell(new Phrase("Matrikelnumer"));
                zelle.setColspan(2);

                tabelle.addCell(zelle);

                for (Iterator<Student> it = studenten.iterator(); it.hasNext();) {
                    Student student = it.next();

                    tabelle.addCell(student.getName() + ", " + student.getVorname());
                    zelle = new PdfPCell(new Phrase(student.getMatrikel()));
                    zelle.setColspan(2);

                    tabelle.addCell(zelle);
                }

                pdfDocument.add(tabelle);
            }

            pdfDocument.close();
        } catch (DocumentException ex) {
            System.out.println(ex.getMessage());
        } catch (SQLException sqlE) {
            System.out.println(sqlE.getMessage());
        }
    }

    @Override
    public Collection<Studienrichtung> getAllStudienrichtung() {
        
        ArrayList<Studienrichtung> studienrichtungList =
                new ArrayList<Studienrichtung>();
        ResultSet sqlResult = null;
        
        
        String sql = "SELECT * FROM STUDIENRICHTUNG ORDER BY SKUERZEL";
        
        try {
            
            sqlResult = executeQuery(sql);
            
            while(sqlResult.next()) {
                
                Studienrichtung tempStudienrichtung =
                        new project.Studienrichtung(
                                sqlResult.getString("SKUERZEL"),
                                sqlResult.getString("NAME"));
                
                studienrichtungList.add(tempStudienrichtung);
            }
        } catch (SQLException ex) {
            
            System.out.println(ex.getMessage());
        }
        
        
        return studienrichtungList;
    }

    
    /**
     * Gibt alle Module in Sortierten Reihenfolge zurück.
     * @return Collection von allen Modulen.
     */
    @Override
    public Collection<Modul> getAllModul() {
        
        ArrayList<Modul> modulList =
                new ArrayList<Modul>();
        
        
        String sql = "SELECT * FROM MODUL ORDER BY MODULNAME ASC";
        
        ResultSet sqlResult = null;
        try {
            
            sqlResult = executeQuery(sql);
            
            while(sqlResult.next()) {
                
                Modul tempModul =
                        new project.Modul(
                                sqlResult.getString("MKUERZEL"),
                                sqlResult.getString("MODULNAME"),
                                sqlResult.getInt("VL"),
                                sqlResult.getInt("UB"),
                                sqlResult.getInt("PR"),
                                sqlResult.getInt("CREDITS"));
                
                modulList.add(tempModul);
            }
        } catch (SQLException ex) {
            
            System.out.println(ex.getMessage());
        }
        
        return modulList;
    }

    
    /**
     * Methode zum aktualisieren aller Praktikumsteilnahmen von einem bestimmten Modul
     * in einem bestimmten Semester.
     * @param modul
     * @param semester
     * @return 
     */
    @Override
    public Collection<Praktikumsteilnahme> getAllPraktikumsteilnahme(
            Modul modul, String semester) {
        // TODO Methode ausfertigen
        
        ArrayList<Praktikumsteilnahme> praktikumlist = 
                new ArrayList<Praktikumsteilnahme>();
        
        /* Falls kein Semester oder kein Modul gewählt wurde, alles überspringen */
        if(!semester.isEmpty() && modul != null) {
            
            /* SELECT Abfrage: Filtern nach Studentdaten und Praktikumsdaten
             zu einem bestimmten Modul und Semester */
            String sql = "SELECT P.MATRIKEL, P.TESTAT, S.NAME, S.VORNAME, S.ADRESSE, S.SKUERZEL, SR.NAME AS SRNAME "
                    + "FROM PRAKTIKUMSTEILNAHME P, STUDENT S, STUDIENRICHTUNG SR "
                    + "WHERE P.SEMESTER = '" + semester + "' "
                    + "AND P.MKUERZEL = '" + modul.getKuerzel() + "' "
                    + "AND P.MATRIKEL = S.MATRIKEL "
                    + "AND S.SKUERZEL = SR.SKUERZEL";


            ResultSet sqlResult = null;
            try {

                sqlResult = executeQuery(sql);


                while(sqlResult.next()) {

                    project.Studienrichtung tmpStudienrichtung = 
                                         new project.Studienrichtung(
                                                 sqlResult.getString("SKUERZEL"), 
                                                 sqlResult.getString("SRNAME"));

                    project.Student tmpStudent = 
                                         new project.Student(
                                            sqlResult.getString("MATRIKEL"), 
                                            sqlResult.getString("NAME"), 
                                            sqlResult.getString("VORNAME"), 
                                            sqlResult.getString("ADRESSE"), 
                                            tmpStudienrichtung);


                    project.Praktikumsteilnahme tempPraktikum =
                                        new project.Praktikumsteilnahme(
                                                tmpStudent, 
                                                modul, 
                                                semester, 
                                                sqlResult.getInt("TESTAT") > 0);

                    praktikumlist.add(tempPraktikum);
                }
            } catch (SQLException ex) {

                System.out.println(ex.getMessage());
            }
        }
        
        
        return praktikumlist;
        
    }
    
    /**
     * Verbindet dieses Object mit der Datenbank und gibt <code>true</code> 
     * zurück, wenn die Verbindung erfolgreich aufgebaut wurde, sonst ein
     * <code>false</code>.
     * 
     * @return <code>true</code> wenn die Verbindung erfolgreich aufgebaut
     * wurde, sonst <code>false</code>.
     */
    public boolean connect() {
        
        boolean isConnected = true;
        
        Driver embeddedDriver = new org.apache.derby.jdbc.EmbeddedDriver();
        
        try {
            
            DriverManager.registerDriver(embeddedDriver);
        } catch (SQLException ex) {
            
            System.out.println(ex.getMessage());
        }
        
        try {
            
            connection = DriverManager.getConnection(jdbcURL);
        } catch(SQLException ex) {
            
            isConnected = false;
            System.out.println("Connection could not be established.");
        }
        
        return isConnected;
    }

    /**
     * Schließt die Verbindung dieses DataAccessObjects zur Datenbank.
     * 
     * @throws ApplicationException wenn die Verbindung nicht geschlossen werden
     * konnte.
     */
    @Override
    public void close() throws ApplicationException {
        
        try {
            
            connection.close();
            connection = null;
            System.out.println("Connection successfully closed.");
        } catch (SQLException ex) {
            
            throw new ApplicationException(
                    "unable to close database connection");
        }
    }
    
    /**
     * Führt einen SQL-Query mit der Datenbank aus. Gibt das Abfrageergebnis
     * als ResultSet zurück.
     * 
     * @param sql Der SQL-Befehl, der an die Datenbank geschickt werden soll.
     * @return Das Ergebnis der Abfrage als ResultSet.
     * @throws SQLException wenn bei der SQL-Abfrage ein SQL-Fehler auftritt.
     */
    private ResultSet executeQuery(String sql) throws SQLException {
        
        Statement sqlStatement = null;
        sqlStatement = connection.createStatement();
        
        
        ResultSet sqlResult = null;
        sqlResult = sqlStatement.executeQuery(sql);
        
        return sqlResult;
    }
    
    /**
     * Führt einen SQL-Befehl auf der Datenbank aus. Bei den Befehlen kann es
     * sich um Befehle handeln, die keine Zeilen als Rückgabeergebnis erwarten.
     * D.h. z.B. INSERT, UPDATE usw.
     * 
     * @param sql Der SQL-Befehl, der an die Datenbank gesendet werden soll.
     * @return <code>true</code> falls der Befehl erfolgreich ausgeführt wurde,
     * sonst <code>false</code>
     * @throws SQLException wenn bei der SQL-Abfrage ein SQL-Fehler auftritt.
     */
    private boolean execute(String sql) throws SQLException {
        
        Statement sqlStatement = null;
        sqlStatement = connection.createStatement();
        
        return sqlStatement.execute(sql);
    }
}
