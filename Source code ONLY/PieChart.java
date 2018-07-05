import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.text.DecimalFormat;

/**
 * Created by bhiggs
 */
public class PieChart extends JComponent{
    JComponent panel;
    static double[] marks;
    String mod;
    JFreeChart PieChart;

    public PieChart(double[] marks, String mod){
        this.marks = marks;
        this.mod = mod;
        PieChart = ChartFactory.createPieChart(
                mod + " Pie Chart ",
                createDataset(),
                true,
                true,
                false);

        PiePlot plot = (PiePlot) PieChart.getPlot();
        PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator(
                "{0} = {2}", new DecimalFormat("0"), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(generator);




        panel = new ChartPanel(PieChart);
    }

    //create dataset for plot
    private static DefaultPieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
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


        dataset.setValue("Fail",grades[0]);
        dataset.setValue("3rd ",grades[1]);
        dataset.setValue("2nd lower",grades[2]);
        dataset.setValue("2nd upper",grades[3]);
        dataset.setValue(" 1st",grades[4]);

        return dataset;
    }
}
