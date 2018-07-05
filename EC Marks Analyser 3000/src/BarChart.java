import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.text.NumberFormat;

/**
 * Created by bhiggs
 */
public class BarChart extends JComponent{
    JComponent panel;
    static double[] marks;
    String mod;
    JFreeChart BarGraph;

    public BarChart(double[] marks, String mod){
        this.marks = marks;
        this.mod = mod;
        BarGraph = ChartFactory.createBarChart(
                mod + " Bar Chart ",
                mod,//x
                "Students",//y
                createDataset());

        CategoryPlot cat = BarGraph.getCategoryPlot();
        BarRenderer br =  (BarRenderer)cat.getRenderer();
        br.setItemMargin(-3);

        br.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance()));
        br.setBaseItemLabelsVisible(true);



        panel = new ChartPanel(BarGraph);
    }

    //create dataset for plot
    private static CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int[] grades = {0,0,0,0,0};

        for(double m:marks){
            if(m<40){
                grades[0]++;
            }
            else if(m<50){
                grades[1]++;
            }
            else if(m<60){
                grades[2]++;
            }
            else if(m<70){
                grades[3]++;
            }
            else if(m>70){
                grades[4]++;
            }
        }

        dataset.addValue(grades[0], "<40% ", "Fail");
        dataset.addValue(grades[1], "40%-50% ", "3rd");
        dataset.addValue(grades[2], "50%-60% ", "2nd lower");
        dataset.addValue(grades[3], "60%-70% ", "2nd upper");
        dataset.addValue(grades[4], "70+% ", "1st");

        return dataset;
    }
}
