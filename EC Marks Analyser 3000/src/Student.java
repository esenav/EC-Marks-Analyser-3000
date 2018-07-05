import java.util.*;

/**
 * Created by bhiggs
 */
public class Student {

    private int RegNo;
    private int ExamNo;
    private String Stage;

    Map<String,Double> marks;

    public Student(String reg, String exam,String stage){
        RegNo = Integer.parseInt(reg);
        ExamNo = Integer.parseInt(exam);
        Stage = stage;
        marks = new HashMap<>();
    }

    public int getRegNo(){
        return RegNo;
    }

    public int getExamNo(){
        return ExamNo;
    }

    public String getStage(){
        return Stage;
    }

    public void addMark(String mod,double mark){
        marks.put(mod,mark);
    }

    public String toString(){
        String s ="";
        Iterator it = marks.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry e = (Map.Entry)it.next();
            s+="("+e.getKey()+"|"+e.getValue()+")";
        }
        return(s);
    }
}
