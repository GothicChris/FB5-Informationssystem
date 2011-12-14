/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package project;

import edu.fhge.gdb.GUIFactory;
import javax.swing.JFrame;

/**
 *
 * @author Christopher
 */
public class Informationssystem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        String database = "jdbc:derby:C:\\gdb-praktikum";
        
        DataAccessObject dao = new DataAccessObject(database);
        
        dao.connect();
        
        JFrame gui = GUIFactory.createMainFrame("test", dao);
        gui.setVisible(true);
        
        // TODO code application logic here
    }
}
