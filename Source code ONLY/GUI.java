/**
 * Created by bhiggs
 */

import com.opencsv.CSVReader;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;


public class GUI {
    static String [] titles;
    static ArrayList<Student> students;
    static File filein;
    final static String LOOKANDFEEL = "System";
    static JFrame window;
    static JProgressBar progressBar;
    static Boolean imported = false;
    static JLabel graphs;
    static boolean importValueErr = false;
    static JPanel exports;
    static JLabel importValueErrLabel = new JLabel("Erroneous data has been corrected.");

    public static void init(){
        JPanel tabs;


        //set look and feel of gui
        initLookAndFeel();
        JFrame.setDefaultLookAndFeelDecorated(true);

        //set defaults for frame
        window = new JFrame("EC Marks Analyser 3000");
        window.setLayout(new BorderLayout());

        //Buttons
        JButton BarChartButton = new JButton(new ImageIcon("Columns.png"));
        BarChartButton.setPreferredSize(new Dimension(150, 75));
        BarChartButton.setFocusPainted(false);
        BarChartButton.addActionListener(new BarChartListener());

        JButton PieChartButton = new JButton(new ImageIcon("Piechart.png"));
        PieChartButton.setPreferredSize(new Dimension(150, 75));
        PieChartButton.setFocusPainted(false);
        PieChartButton.addActionListener(new PieChartListener());

        JButton ScatterGraphButton = new JButton(new ImageIcon("Scatter2.png"));
        ScatterGraphButton.setPreferredSize(new Dimension(150, 75));
        ScatterGraphButton.setFocusPainted(false);
        ScatterGraphButton.addActionListener(new ScatterGraphListener());

        JButton DataViewButton = new JButton(new ImageIcon("Data.png"));
        DataViewButton.setPreferredSize(new Dimension(150, 75));
        DataViewButton.setFocusPainted(false);
        DataViewButton.addActionListener(new DataViewListener());

        //top bar
        tabs = new JPanel();
        tabs.setBackground(Color.DARK_GRAY); // new Color(0,76,152) one of the options
        tabs.add(BarChartButton);
        tabs.add(PieChartButton);
        tabs.add(ScatterGraphButton);
        tabs.add(DataViewButton);

        //graphs frame
        graphs = new JLabel();
        graphs.setIcon(new ImageIcon("Data1.png"));
        graphs.setLayout(new BorderLayout());

       /* graphs = new JPanel();*/
        graphs.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        /*graphs.setBackground(Color.WHITE);*/

        //export bar
        exports = new JPanel(new FlowLayout(FlowLayout.CENTER,50,0));
        JPanel progress = new JPanel();
        progress.setBackground(Color.WHITE);
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progress.add(new JTextArea("Progress:"));
        progress.add(progressBar);
        progress.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY));
        exports.setBackground(Color.WHITE);

        JButton fileopen = new JButton("Import CSV");
        fileopen.addActionListener(new FileChooserListener());

        JButton export = new JButton("Export Options");
        export.addActionListener(new ExportButtonListener());

        exports.add(fileopen);
        exports.add(progress);
        exports.add(export);

        //add to gui
        window.add(tabs,BorderLayout.NORTH);
        window.add(graphs,BorderLayout.CENTER);
        window.add(exports,BorderLayout.SOUTH);

        //gui setup and display features
        window.setPreferredSize(new Dimension(1000,700));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.pack();
        window.setLocationRelativeTo(null);

    }

    //set the GUI look and feel to metal theme
    private static void initLookAndFeel(){
        String lookAndFeel = null;
        if(LOOKANDFEEL != null){
            if(LOOKANDFEEL.equals("Metal")){
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }
            else if(LOOKANDFEEL.equals("System")){
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            }
            try{
                UIManager.setLookAndFeel(lookAndFeel);
            }
            catch (Exception e){
                System.err.println("");
            }
        }
    }



    //parse data into Student object
    private static boolean extractData() throws Exception {
        boolean imp = true;
        while(imp) {
            students = new ArrayList<Student>();
            CSVReader in = new CSVReader(new FileReader(filein));
            String[] nextLine;
            titles = in.readNext();

            //check for errors in CSV structure
            if (titles[0].compareTo("Student RegNo")==1 || titles[1].compareTo("ExNo")==1 || titles[2].compareTo("Stage")==1 ){
                JOptionPane.showMessageDialog(window, "Error with CSV structure.\nTry again with the correct CSV.", "CSV Error Notification", JOptionPane.ERROR_MESSAGE);
                titles=null;
                return false;
            }

            //for each input number...
            while ((nextLine = in.readNext()) != null) {
                //create new student object
                Student s = new Student(nextLine[0], nextLine[1], nextLine[2]);

                //only add modules to the student object if they have a recorded mark.
                double tot=0.0;
                int count=0;

                //calculate our errors
                for (int x = 3; x < nextLine.length-1; x++) {
                    if (nextLine[x].length() > 0) {
                        //check
                        if(Double.parseDouble(nextLine[x])>100.0 || Double.parseDouble(nextLine[x])<0.0){
                            importValueErr = true;
                        }
                        else {
                            s.addMark(titles[x], Double.parseDouble(nextLine[x]));
                            tot+=Double.parseDouble(nextLine[x]);
                            count++;
                        }
                    }
                }
                //import the average as our own calculated version. This is more accurate and is conscious of errors
                s.addMark(titles[titles.length-1],new BigDecimal((tot/count)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue());

                students.add(s);
            }
            imp = false;
        }
        if(importValueErr){
            JOptionPane.showMessageDialog(window, "Error with one or more student marks.\nError was rectified during import\nand mark was not imported.", "Import Error Notification", JOptionPane.WARNING_MESSAGE);
        }
        return true;
    }



    //main calls init
    public static void main(String args[]) {
        init();
    }


    //update main screen
    public static void changeScreen(String option, String mod) {
        graphs.removeAll();
        switch(option){

            //if chosen graph is bar
            case "bar":
                BarChart barchart = null;
                //plot average marks
                if(mod=="Average Marks"){
                    barchart = new BarChart(getStudMarks("TC_MK"),mod);
                }
                //plot another module
                else {
                    mod = mod.substring(0,10);
                    barchart = new BarChart(getStudMarks(mod),mod);
                }
                graphs.add(barchart.panel);
                progressBar.setValue(75);
                break;


            //if chosen graph is scattter
            case "scatter":
                ScatterGraph scatterchart = null;
                //if passed module is the average mark... plot average vs average scatter
                if(mod=="Average Marks"){
                    scatterchart = new ScatterGraph(getStudMarks("TC_MK","TC_MK"),"Students",mod);
                }
                //if passed module is "Plot average against other module" ... plot average vs chosen module scatter
                else {
                    mod = mod.substring(0,10);
                    scatterchart = new ScatterGraph(getStudMarks("TC_MK",mod),"Average Mark",mod);
                }
                graphs.add(scatterchart.panel);
                progressBar.setValue(75);
                break;


            //if chosen graph is pie
            case "pie":
                PieChart piechart = null;
                //plot average marks
                if(mod=="Average Marks"){
                    piechart = new PieChart(getStudMarks("TC_MK"),mod);
                }
                //plot another module
                else {
                    mod = mod.substring(0,10);
                    piechart = new PieChart(getStudMarks(mod),mod);
                }
                graphs.add(piechart.panel);
                progressBar.setValue(75);
                break;


            //if data view is chosen..
            case "dataview":
                Object[][]studs = new Object[students.size()][titles.length];
                for(int l=0;l<students.size();l++) {
                    studs[l][0] = students.get(l).getRegNo();
                    studs[l][1] = students.get(l).getExamNo();
                    studs[l][2] = students.get(l).getStage();
                    for (int m=3;m<titles.length;m++){
                        studs[l][m] = students.get(l).marks.get(titles[m]);
                    }
                }
                JTable table = new JTable(studs,titles);
                JScrollPane tableScreen = new JScrollPane(table);
                tableScreen.setBounds(1,1,graphs.getWidth(),graphs.getHeight());
                table.setBounds(1, 1, graphs.getWidth(), graphs.getHeight());
                table.setVisible(true);
                tableScreen.setVisible(true);
                graphs.add(tableScreen, BorderLayout.CENTER);
                break;
        }

        graphs.revalidate();
        graphs.repaint();
    }


    //get student marks
    //two arguement operator, used for scatter graphs
    public static double[][] getStudMarks(String mod, String mod1){
        //marks is an arraylist so it can be added to..
        ArrayList marks = new ArrayList();

        //if modules are different
        for (Student x : students) {
            if (x.marks.get(mod1) != null) {
                marks.add(new double[]{x.marks.get(mod), x.marks.get(mod1)});
            }
        }

        //convert arraylist to array
        double[][] markArray = new double[marks.size()][2];
        for (int m = 0; m < marks.size(); m++) {
            markArray[m] = (double[]) marks.get(m);
        }
        //return array
        return markArray;
    }



    //one arguement operator, used for bar charts
    public static double[] getStudMarks(String mod){
        //marks is an arraylist so it can be added to..
        ArrayList marks = new ArrayList();

        for (Student x : students) {
            if(x.marks.get(mod)!=null){
                marks.add((double) x.marks.get(mod));
            }
        }
        //convert arraylist to array
        double[] markArray = new double[marks.size()];
        for (int m = 0; m < marks.size(); m++) {
            markArray[m] = (double) marks.get(m);
        }
        //return array
        return markArray;
    }




    //listener to input button, allows user to choose the file exactly and only accepts CSVs
    static class BarChartListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!imported) {
                JOptionPane.showMessageDialog(window, "Import the CSV first", "Export failed", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    String[] choices = new String[titles.length - 4];
                    choices[0] = "Average Marks";
                    int pos = 1;
                    for (String m : Arrays.copyOfRange(titles, 3, titles.length - 2)) {
                        choices[pos] = m + " grades";
                        pos++;
                    }
                    String mod = (String) JOptionPane.showInputDialog(new JOptionPane(), "What bar chart do you wish to view:", "Bar Chart Options", JOptionPane.PLAIN_MESSAGE, null, choices, null);
                    changeScreen("bar", mod);
                }catch (NullPointerException exc){}
            }
        }
    }

    static class PieChartListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!imported) {
                JOptionPane.showMessageDialog(window, "Import the CSV first", "Export failed", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    String[] choices = new String[titles.length - 4];
                    choices[0] = "Average Marks";
                    int pos = 1;
                    for (String m : Arrays.copyOfRange(titles, 3, titles.length - 2)) {
                        choices[pos] = m + " grades";
                        pos++;
                    }
                    String mod = (String) JOptionPane.showInputDialog(new JOptionPane(), "What pie chart do you wish to view:", "Pie Chart Options", JOptionPane.PLAIN_MESSAGE, null, choices, null);
                    changeScreen("pie", mod);
                }catch (NullPointerException exc){}
            }
        }
    }

    static class ScatterGraphListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            if (!imported) {
                JOptionPane.showMessageDialog(window, "Import the CSV first", "Export failed", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    String[] choices = new String[titles.length - 4];
                    choices[0] = "Average Marks";
                    int pos = 1;
                    for (String m : Arrays.copyOfRange(titles, 3, titles.length - 2)) {
                        choices[pos] = m + " grades";
                        pos++;
                    }
                    String mod = (String) JOptionPane.showInputDialog(new JOptionPane(), "What module do you wish to plot against the average grade:", "Scatter Graph Options", JOptionPane.PLAIN_MESSAGE, null, choices, null);
                    changeScreen("scatter", mod);
                }catch(NullPointerException exc){}
            }
        }
    }

    static class DataViewListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!imported) {
                JOptionPane.showMessageDialog(window, "Import the CSV first", "Export failed", JOptionPane.ERROR_MESSAGE);
            } else {
                changeScreen("dataview","");
            }
        }
    }

    //listener to input button, allows user to choose the file exactly and only accepts CSVs
    static class FileChooserListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser  fileDialog = new JFileChooser();
            fileDialog.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        String filename = f.getName().toLowerCase();
                        return filename.endsWith(".csv");
                    }
                }
                public String getDescription() {
                    return "CSV files only (*.csv)";
                }
            });
            if ( fileDialog.showOpenDialog(fileDialog) == JFileChooser.APPROVE_OPTION) {
                filein = fileDialog.getSelectedFile();
                try {
                    //if data extract is good, then continue, if not, do not continue and user has to re-import
                    if(extractData()) {
                        imported = true;
                        JOptionPane.showMessageDialog(window, "Imported successfully!", "Import Message", JOptionPane.INFORMATION_MESSAGE);
                        progressBar.setValue(50);
                        //if errors fixed, show notice, otherwise remove it if possible
                        if(importValueErr){
                            exports.add(importValueErrLabel);
                            exports.setVisible(true);
                            window.pack();
                        }
                        else {
                            exports.remove(importValueErrLabel);
                            exports.repaint();
                        }
                    }

                }catch (Exception ex){}
            }
        }
    }


    //listener to export button, allows user to choose output directory only.
    static class ExportButtonListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            if (!imported) {
                JOptionPane.showMessageDialog(window, "Import the CSV first", "Export failed", JOptionPane.ERROR_MESSAGE);
            } else {
                JFrame options = new ExportOptionPane(Arrays.copyOfRange(titles, 3, titles.length - 2));

            }
        }
    }
}