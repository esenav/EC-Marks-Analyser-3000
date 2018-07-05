import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import static org.jfree.data.statistics.Regression.getOLSRegression;


/**
 * Created by bhiggs
 */
public class ScatterGraph extends JComponent{
    JComponent panel;
    double[][] marks;
    String mod;
    JFreeChart ScatterGraph;
    double meanX;
    double meanY;
    double totX;
    double totY;
    double totXS;
    double totYS;
    double XY;
    double correlation;
    int n;


    public ScatterGraph(double[][] marks, String mod, String mod1) {
        this.marks = marks;
        this.mod = mod1;
        XYDataset dataset = createDataset();
        ScatterGraph = ChartFactory.createScatterPlot(
                mod + " Against " + mod1 + " As A Scatter Graph",
                mod,//x
                mod1,//y
                dataset
        );
        XYPlot gPlot = ScatterGraph.getXYPlot();

        gPlot.getDomainAxis().setLowerBound(0);
        gPlot.getRangeAxis().setLowerBound(0);
        gPlot.getDomainAxis().setUpperBound(100);
        gPlot.getRangeAxis().setUpperBound(100);

        try {
            double[] ab = getOLSRegression(dataset, 0);
            gPlot.addAnnotation(new XYLineAnnotation(0, ab[0], 100, (ab[0] + 100 * ab[1]), new BasicStroke(), Color.BLUE));
        }catch (IllegalArgumentException e){
            //not enough data
        }

        panel = new ChartPanel(ScatterGraph);
    }

    //create dataset for plot
    private XYDataset createDataset(){
        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries plot1 = new XYSeries(mod);

        totX=0;
        totY=0;
        n=0;

        for(int x=0;x<marks.length;x++){
            plot1.add(marks[x][0],marks[x][1]);//0 = x, 1 = y
            totX+=marks[x][0];
            totY+=marks[x][1];
            totXS+=(marks[x][0]*marks[x][0]);
            totYS+=(marks[x][1]*marks[x][1]);
            XY += (marks[x][0]*marks[x][1]);
            n++;
        }
        String corrstr = "Not enough data for correlation";
        try {
            correlation = getCorr();
            corrstr = "Correlation = "+ new BigDecimal(correlation).setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue();
        }catch(Exception e) {
            //not enough data
        }

        DecimalFormat df = new DecimalFormat("#.00");
        XYSeries plot2 = new XYSeries("Line Of Best Fit");
        XYSeries plot3 = new XYSeries(corrstr);

        dataset.addSeries(plot1);
        dataset.addSeries(plot2);
        dataset.addSeries(plot3);

        meanX = totX/n;
        meanY = totY/n;

        return dataset;
    }


    private double getCorr(){
        double corr =0;
        //Correlation(r) =[ N?XY - (?X)(?Y) / Sqrt([N?X2 - (?X)2][N?Y2 - (?Y)2])]
        corr = (n*XY)-(totX*totY);
        corr = corr / Math.sqrt( ((n*totXS)-(totX*totX)) * ((n*totYS)-(totY*totY))  );
        return corr;
    }

}
