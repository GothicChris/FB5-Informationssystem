/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

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
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Christopher
 */
public class DataAccessObject implements edu.fhge.gdb.DataAccessObject {
    
    Connection connection;

    private final String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    
    private String jdbcURL;

    public DataAccessObject() {
        jdbcURL = "jdbc:derby:eqal";
    }

    public DataAccessObject(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }
    
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
        
        ArrayList<String> zeile2 = new ArrayList();
        
        zeile2.add("Nr  Kategorie");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Mod  V  Ü  P  Cr");
        zeile2.add("Summe");
        
        spalten.add(zeile1);
        spalten.add(zeile2);
        
        
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

        ArrayList<Studienverlauf> studienverlauf = new ArrayList();
        
        Statement sqlStatement = null;
        ResultSet sqlResult = null;
        try {
            sqlStatement = connection.createStatement();
            sqlResult = sqlStatement.executeQuery(sql);
            
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
            
//            System.out.println(studienverlauf);
            
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

    @Override
    public void addStudent(String matrikel, String name, String vorname,
            String adresse, String srKuerzel) throws ApplicationException {
        
        
        String sql = "INSERT INTO STUDENT (MATRIKEL, NAME, VORNAME,"
                + " ADRESSE, SKUERZEL)"
                + " VALUES ('" + matrikel + "', '" + name + "', '"
                + vorname + "',"
                + " '" + adresse + "', '" + srKuerzel + "')";
        
        
        Statement sqlStatement = null;
        try {
            sqlStatement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        
        try {
            sqlStatement.execute(sql);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw new ApplicationException("Matrikelnummer bereits vergeben.");
        }
    }

    @Override
    public Collection<Student> getAllStudent() {
        
        String sql = "SELECT S.*, SR.SKUERZEL, SR.NAME AS SRNAME"
                + " FROM STUDENT S, STUDIENRICHTUNG SR"
                + " WHERE S.STUDIENRICHTUNG = SR.SKUERZEL"
                + " ORDER BY S.NAME";
        
        
        Statement sqlStatement = null;
        try {
            sqlStatement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        
        ResultSet sqlResult = null;
        try {
            sqlResult = sqlStatement.executeQuery(sql);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        
        ArrayList<Student> studentList = new ArrayList<Student>();
        
        try {
            while(sqlResult.next()) {
                
                Student tempStudent = new project.Student(
                        sqlResult.getString("MARTIKELNUMMER"),
                        sqlResult.getString("NAME"),
                        sqlResult.getString("VORNAME"),
                        sqlResult.getString("ADRESSE"),
                        new project.Studienrichtung(
                                sqlResult.getString("SKUERZEL"),
                                sqlResult.getString("SRNAME")));
                
                studentList.add(tempStudent);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        return studentList;
    }

    @Override
    public boolean announce(String matrikel, String name, String vorname,
            String adresse, String srKuerzel, Modul modul, String semester)
            throws ApplicationException {
        
        
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setTestate(Collection<Praktikumsteilnahme> testate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JPanel getChart(int type, Object parameter1, Object parameter2) throws ApplicationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void exportAnnouncementList(OutputStream target, String semester) {
        System.out.println("exportAnnouncementList");
        
    }

    @Override
    public Collection<Studienrichtung> getAllStudienrichtung() {
        
        String sql = "SELECT * FROM STUDIENRICHTUNG ORDER BY SKUERZEL";
        
        
        Statement sqlStatement = null;
        try {
            sqlStatement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        
        ResultSet sqlResult = null;
        try {
            sqlResult = sqlStatement.executeQuery(sql);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        
        ArrayList<Studienrichtung> studienrichtungList =
                new ArrayList<Studienrichtung>();
        
        try {
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

    @Override
    public Collection<Modul> getAllModul() {
        
        String sql = "SELECT * FROM MODUL";
        
        
        Statement sqlStatement = null;
        try {
            sqlStatement = connection.createStatement();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        
        ResultSet sqlResult = null;
        try {
            sqlResult = sqlStatement.executeQuery(sql);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        
        ArrayList<Modul> modulList =
                new ArrayList<Modul>();
        
        try {
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

    @Override
    public Collection<Praktikumsteilnahme> getAllPraktikumsteilnahme(
            Modul modul, String semester) {
        
        
        return null;
    }
    
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

    @Override
    public void close() throws ApplicationException {
        try {
            connection.close();
            System.out.println("Connection successfully closed.");
        } catch (SQLException ex) {
            System.out.println("Could not close connection.");
        }
    }
    
}
