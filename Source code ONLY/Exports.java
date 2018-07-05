import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.javadocx.CreateDocx;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;

/**
 * Created by bhiggs and jsbloo
 */
public class Exports {

    public String checkFileExtension(String fileDest,String type){
        switch(type){
            case "pdf":
                if(fileDest.substring(fileDest.length()-4,fileDest.length())!=".pdf"){
                    return fileDest+".pdf";
                }
                else{
                    return fileDest;
                }
            case "doc":
                if(fileDest.substring(fileDest.length()-5,fileDest.length())!=".docx"){
                    return fileDest+".docx";
                }
                else{
                    return fileDest;
                }
            case "html":
                if(fileDest.substring(fileDest.length()-5,fileDest.length())!=".html"){
                    return fileDest+".html";
                }
                else{
                    return fileDest;
                }
            case "txt":
                if(fileDest.substring(fileDest.length()-4,fileDest.length())!=".txt"){
                    return fileDest+".txt";
                }
                else{
                    return fileDest;
                }
        }
        return "";
    }


    public boolean createPDF(String directory, String[] modlist, String[] graphlist) {
        Document document = null;
        PdfWriter writer = null;
        int width = 700;
        int height = 500;
        int swidth = 500;
        int sheight = 350;
        directory = checkFileExtension(directory,"pdf");
        try {
            //instantiate document and writer
            OutputStream outputStream = new FileOutputStream(new File(directory));
            document = new Document();
            writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            String errstr="";
            if(GUI.importValueErr){
                errstr = "There was one or more erroneous data items found during import. These were corrected, and averages recalculated. The resulting data set may be incomplete.";
                sheight-=30;
            }
            document.add(new Paragraph("EC Marks Analyser 3000 PDF Export\n"+errstr));
            //add image
            for (String graph : graphlist) {
                //for each graph type.
                for (String module : modlist) {
                    //for each module, add the graph of that module
                    if (graph == "Bar Chart") {
                        JFreeChart chart;
                        if (module == "Average") {
                            chart = new BarChart(GUI.getStudMarks("TC_MK"), module).BarGraph;
                        } else {
                            chart = new BarChart(GUI.getStudMarks(module), module).BarGraph;
                        }
                        BufferedImage bufferedImage = chart.createBufferedImage(width, height);
                        Image image = Image.getInstance(writer, bufferedImage, 1.0f);
                        image.scaleAbsolute(swidth,sheight);
                        document.add(image);
                    } else if (graph == "Pie Chart") {
                        JFreeChart chart;
                        if (module == "Average") {
                            chart = new PieChart(GUI.getStudMarks("TC_MK"), module).PieChart;
                        } else {
                            chart = new PieChart(GUI.getStudMarks(module), module).PieChart;
                        }
                        BufferedImage bufferedImage = chart.createBufferedImage(width, height);
                        Image image = Image.getInstance(writer, bufferedImage, 1.0f);
                        image.scaleAbsolute(swidth,sheight);
                        document.add(image);
                    } else if (graph == "Scatter Graph") {
                        JFreeChart chart;
                        if (module == "Average") {
                            chart = new ScatterGraph(GUI.getStudMarks("TC_MK", "TC_MK"), "Students", module).ScatterGraph;
                        } else {
                            chart = new ScatterGraph(GUI.getStudMarks("TC_MK", module), "Average Mark", module).ScatterGraph;
                        }
                        BufferedImage bufferedImage = chart.createBufferedImage(width, height);
                        Image image = Image.getInstance(writer, bufferedImage, 1.0f);
                        image.scaleAbsolute(swidth,sheight);
                        document.add(image);
                    }
                }
            }
            document.close();
            writer.close();
        } catch (Exception e) {
            return false;
        } finally {
            //release resources
            if (null != document) {
                try {
                    document.close();
                } catch (Exception ex) {
                    return false;
                }
            }
            if (null != writer) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    return false;
                }
            }
        }
        return true;
    }


    public boolean createDOC(String directory, String[] modlist, String[] graphlist){
        int width = 700;
        int height = 450;
        String filename = directory;
        File out = new File(filename);
        directory = out.getParent()+"\\temp";

        try{
            CreateDocx outDoc = new CreateDocx("docx");
            new File (directory).mkdirs();

            String errstr="";
            if(GUI.importValueErr){
                errstr = "There was one or more erroneous data items found during import. These were corrected, and averages recalculated. The resulting data set may be incomplete.";
                height-=30;
            }
            HashMap paramsHeader = new HashMap();
            paramsHeader.put("font", "Arial");
            outDoc.addHeader("EC Marks Analyser 3000 DOC Export", paramsHeader);
            HashMap paramsFooter = new HashMap();
            paramsHeader.put("font", "Arial");
            outDoc.addFooter(errstr,paramsFooter);

            for(String graph:graphlist){
                for(String module:modlist){
                    if (graph == "Bar Chart") {
                        JFreeChart chart;
                        if (module == "Average") {
                            chart = new BarChart(GUI.getStudMarks("TC_MK"), module).BarGraph;
                        } else {
                            chart = new BarChart(GUI.getStudMarks(module), module).BarGraph;
                        }
                        File pic = new File(directory+"\\"+module+graph+".jpeg");
                        ChartUtilities.saveChartAsJPEG(pic, chart,width,height);
                        HashMap paramsImage = new HashMap();
                        paramsImage.put("name",pic.getAbsolutePath());
                        paramsImage.put("scaling", "1");
                        outDoc.addImage(paramsImage);

                    } else if (graph == "Pie Chart") {
                        JFreeChart chart;
                        if (module == "Average") {
                            chart = new PieChart(GUI.getStudMarks("TC_MK"), module).PieChart;
                        } else {
                            chart = new PieChart(GUI.getStudMarks(module), module).PieChart;
                        }
                        File pic = new File(directory+"\\"+module+graph+".jpeg");
                        ChartUtilities.saveChartAsJPEG(pic, chart,width,height);
                        HashMap paramsImage = new HashMap();
                        paramsImage.put("name",pic.getAbsolutePath());
                        paramsImage.put("scaling", "1");
                        outDoc.addImage(paramsImage);

                    } else if (graph == "Scatter Graph") {
                        JFreeChart chart;
                        if (module == "Average") {
                            chart = new ScatterGraph(GUI.getStudMarks("TC_MK", "TC_MK"), "Students", module).ScatterGraph;
                        } else {
                            chart = new ScatterGraph(GUI.getStudMarks("TC_MK", module), "Average Mark", module).ScatterGraph;
                        }
                        File pic = new File(directory+"\\"+module+graph+".jpeg");
                        ChartUtilities.saveChartAsJPEG(pic, chart,width,height);
                        HashMap paramsImage = new HashMap();
                        paramsImage.put("name",pic.getAbsolutePath());
                        paramsImage.put("scaling", "1");
                        outDoc.addImage(paramsImage);
                    }
                }
            }
            HashMap pageSettings = new HashMap();
            pageSettings.put("orient", "normal");
            pageSettings.put("top", "1000");
            pageSettings.put("bottom", "1000");
            pageSettings.put("right", "1000");
            pageSettings.put("left", "1000");
            outDoc.createDocx(filename, pageSettings);
            org.apache.commons.io.FileUtils.deleteDirectory(new File(directory));
        } catch(Exception e){return false;}
        return true;
    }


    public boolean createHTML(String directory, String[] modlist, String[] graphlist){
        int width = 1000;
        int height = 800;
        String filename = checkFileExtension(directory,"html");
        File out = new File(filename);
        directory = out.getParent()+"\\images";

        try{
            new File (directory).mkdirs();
            FileWriter htmlwriter = new FileWriter(filename);
            BufferedWriter writer = new BufferedWriter(htmlwriter);
            String errstr="";
            if(GUI.importValueErr){
                errstr = "There was one or more erroneous data items found during import. These were corrected, and averages recalculated. The resulting data set may be incomplete.";
                height-=30;
            }
            writer.write("<html>\n<head>\n<title>EC Marks Analysis</title>\n</head>\n");
            writer.write("<body leftmargin=36.0 rightmargin=36.0 topmargin=36.0 bottommargin=36.0>\n");
            writer.write("<h1 style='font-family:Arial;'>Welcome to the EC Marks Analyser 3000 HTML Export</h1>");
            writer.write("<h4 style='font-family:Arial; color:red;'>"+errstr+"</h4>");

            for(String graph:graphlist){
                for(String module:modlist){
                    if (graph == "Bar Chart") {
                        JFreeChart chart;
                        if (module == "Average") {
                            chart = new BarChart(GUI.getStudMarks("TC_MK"), module).BarGraph;
                        } else {
                            chart = new BarChart(GUI.getStudMarks(module), module).BarGraph;
                        }
                        File pic = new File(directory+"\\"+module+graph+".jpeg");
                        ChartUtilities.saveChartAsJPEG(pic, chart,width,height);
                        writer.write("<img src='"+(directory+"\\"+module+graph+".jpeg")+"' width='800.0' height='600.0' /></img></br>\n");

                    } else if (graph == "Pie Chart") {
                        JFreeChart chart;
                        if (module == "Average") {
                            chart = new PieChart(GUI.getStudMarks("TC_MK"), module).PieChart;
                        } else {
                            chart = new PieChart(GUI.getStudMarks(module), module).PieChart;
                        }
                        File pic = new File(directory+"\\"+module+graph+".jpeg");
                        ChartUtilities.saveChartAsJPEG(pic, chart,width,height);
                        writer.write("<img src='"+(directory+"\\"+module+graph+".jpeg")+"' width='800.0' height='600.0' /></img></br>\n");

                    } else if (graph == "Scatter Graph") {
                        JFreeChart chart;
                        if (module == "Average") {
                            chart = new ScatterGraph(GUI.getStudMarks("TC_MK", "TC_MK"), "Students", module).ScatterGraph;
                        } else {
                            chart = new ScatterGraph(GUI.getStudMarks("TC_MK", module), "Average Mark", module).ScatterGraph;
                        }
                        File pic = new File(directory+"\\"+module+graph+".jpeg");
                        ChartUtilities.saveChartAsJPEG(pic, chart,width,height);
                        writer.write("<img src='"+(directory+"\\"+module+graph+".jpeg")+"' width='800.0' height='600.0' /></img></br>\n");
                    }
                }
            }
            writer.write("</body>");
            writer.write("</html>");
            writer.close();
        } catch(Exception e){return false;}
        return true;
    }


    public boolean createTXT(String directory){
        String filename = checkFileExtension(directory,"txt");
        try{
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            for (Student x : GUI.students) {
                writer.println(x);
            }
            writer.close();

        }catch (Exception e){}

        return true;
    }
}


