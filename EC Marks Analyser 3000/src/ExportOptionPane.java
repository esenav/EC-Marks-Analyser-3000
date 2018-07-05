import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by bhiggs
 */
public class ExportOptionPane extends JFrame{
    String[] mods;
    ArrayList<JCheckBox> boxes = new ArrayList<>();
    ArrayList<String> chosen = new ArrayList<>();
    ArrayList<JCheckBox> graphs = new ArrayList<>();
    ArrayList<String> chosengraphs = new ArrayList<>();
    Exports export = new Exports();

    public ExportOptionPane(String[] mods){
        setSize(500,300);
        setDefaultCloseOperation(ExportOptionPane.HIDE_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        setTitle("Export: Detailed Options");

        this.mods = mods;

        setLayout(new BorderLayout(3,0));

        JPanel title = new JPanel();
        JPanel options = new JPanel();
        JPanel buttons = new JPanel();

        JButton PDF = new JButton("Export to PDF");
        PDF.addActionListener(new PDFExportButtonListener());
        JButton DOC = new JButton("Export DOC");
        DOC.addActionListener(new DOCExportButtonListener());
        JButton HTML = new JButton("Export HTML");
        HTML.addActionListener(new HTMLExportButtonListener());
        JButton TXT = new JButton("TXT for Website");
        TXT.addActionListener(new TXTExportButtonListener());

        buttons.add(PDF);
        buttons.add(DOC);
        buttons.add(HTML);
        buttons.add(TXT);

        JPanel moduleOptions = new JPanel();
        moduleOptions.setLayout(new FlowLayout());
        boxes.add(new JCheckBox("Average"));
        for (String mod:mods){
            boxes.add(new JCheckBox(mod));
        }
        for(JCheckBox box:boxes){
            moduleOptions.add(box);
        }

        moduleOptions.setBorder(BorderFactory.createTitledBorder("Pick the modules you wish to export: "));

        JPanel graphOptions = new JPanel();
        graphOptions.setBorder(BorderFactory.createTitledBorder("Select the graphs: "));
        graphOptions.setLayout(new FlowLayout());
        graphs.add(new JCheckBox("Bar Chart"));
        graphs.add(new JCheckBox("Pie Chart"));
        graphs.add(new JCheckBox("Scatter Graph"));
        for(JCheckBox box:graphs){
            graphOptions.add(box);
        }


        options.setLayout(new BorderLayout());
        options.add(graphOptions,BorderLayout.NORTH);
        options.add(moduleOptions,BorderLayout.CENTER);

        add(title, BorderLayout.NORTH);
        add(options,BorderLayout.CENTER);
        add(buttons,BorderLayout.SOUTH);

    }


    //listener to PDF export button, allows user to choose output directory only.
    class PDFExportButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            String directory = "";
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Select a directory and file name");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        String filename = f.getName().toLowerCase();
                        return filename.endsWith(".pdf");
                    }
                }

                public String getDescription() {
                    return "PDF files only (*.pdf)";
                }
            });

            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(GUI.window) == JFileChooser.APPROVE_OPTION) {
                directory = chooser.getSelectedFile().getAbsolutePath();
            }

            if(directory!="") {
                chosen.clear();
                for (JCheckBox box : boxes) {
                    if (box.isSelected()) {
                        chosen.add(box.getText());
                    }
                }

                chosengraphs.clear();
                for (JCheckBox box : graphs) {
                    if (box.isSelected()) {
                        chosengraphs.add(box.getText());
                    }
                }

                boolean exported = export.createPDF(directory, chosen.toArray(new String[0]), chosengraphs.toArray(new String[0]));

                if (exported) {
                    JOptionPane.showMessageDialog(GUI.window, "Exported successfully!", "Export message", JOptionPane.INFORMATION_MESSAGE);
                    GUI.changeScreen("exported", "");
                    GUI.progressBar.setValue(100);
                } else {
                    JOptionPane.showMessageDialog(GUI.window, "Export failed!\nTry again", "Export message", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    //listener to doc export button, allows user to choose output directory only.
    class DOCExportButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            String directory = "";
            chooser.setDialogTitle("Select a directory and file name");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        String filename = f.getName().toLowerCase();
                        return filename.endsWith(".docx");
                    }
                }

                public String getDescription() {
                    return "DOC files only (*.docx)";
                }
            });
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(GUI.window) == JFileChooser.APPROVE_OPTION) {
                directory = chooser.getSelectedFile().getAbsolutePath();
            }

            if(directory!="") {
                chosen.clear();
                for (JCheckBox box : boxes) {
                    if (box.isSelected()) {
                        chosen.add(box.getText());
                    }
                }

                chosengraphs.clear();
                for (JCheckBox box : graphs) {
                    if (box.isSelected()) {
                        chosengraphs.add(box.getText());
                    }
                }

                boolean exported = export.createDOC(directory, chosen.toArray(new String[0]), chosengraphs.toArray(new String[0]));

                if (exported) {
                    JOptionPane.showMessageDialog(GUI.window, "Exported successfully!", "Export message", JOptionPane.INFORMATION_MESSAGE);
                    GUI.changeScreen("exported", "");
                    GUI.progressBar.setValue(100);
                } else {
                    JOptionPane.showMessageDialog(GUI.window, "Export failed!\nTry again", "Export message", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    //listener to html export button, allows user to choose output directory only.
    class HTMLExportButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            String directory = "";
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Select a directory and file name");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        String filename = f.getName().toLowerCase();
                        return filename.endsWith(".html");
                    }
                }
                public String getDescription() {
                    return "HTML files only (*.html)";
                }
            });

            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(GUI.window) == JFileChooser.APPROVE_OPTION) {
                directory = chooser.getSelectedFile().getAbsolutePath();
            }

            if(directory!="") {
                chosen.clear();
                for (JCheckBox box : boxes) {
                    if (box.isSelected()) {
                        chosen.add(box.getText());
                    }
                }

                chosengraphs.clear();
                for (JCheckBox box : graphs) {
                    if (box.isSelected()) {
                        chosengraphs.add(box.getText());
                    }
                }

                boolean exported = export.createHTML(directory, chosen.toArray(new String[0]), chosengraphs.toArray(new String[0]));

                if (exported) {
                    JOptionPane.showMessageDialog(GUI.window, "Exported successfully!", "Export message", JOptionPane.INFORMATION_MESSAGE);
                    GUI.changeScreen("exported", "");
                    GUI.progressBar.setValue(100);
                } else {
                    JOptionPane.showMessageDialog(GUI.window, "Export failed!\nTry again", "Export message", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    //listener to html export button, allows user to choose output directory only.
    class TXTExportButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            String directory = "";
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Select a directory and file name");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory()) {
                        return true;
                    } else {
                        String filename = f.getName().toLowerCase();
                        return filename.endsWith(".txt");
                    }
                }
                public String getDescription() {
                    return "TXT files only (*.txt)";
                }
            });

            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(GUI.window) == JFileChooser.APPROVE_OPTION) {
                directory = chooser.getSelectedFile().getAbsolutePath();
            }

            if(directory!="") {
                boolean exported = export.createTXT(directory);
                if (exported) {
                    JOptionPane.showMessageDialog(GUI.window, "Exported successfully!", "Export message", JOptionPane.INFORMATION_MESSAGE);
                    GUI.changeScreen("exported", "");
                    GUI.progressBar.setValue(100);
                } else {
                    JOptionPane.showMessageDialog(GUI.window, "Export failed!\nTry again", "Export message", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}
