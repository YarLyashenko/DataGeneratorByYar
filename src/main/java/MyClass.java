import datagenerator.DataGenerator;
import org.json.JSONException;

/**
 * Created by ylyashenko on 1/12/2015.
 */
public class MyClass {
    public static void main(String args[]){
        DataGenerator generator = new DataGenerator();
        try {
            System.out.println("json string with two defects with all fields and Russian charset: "+generator.generateStringInJsonFormatForAllFields(2,"Defect","Russian"));
            System.out.println("json string with two defects with required fields and Russian charset: "+generator.generateStringInJsonFormatForRequiredOlnyFields(2, "Defect", "Russian"));
            System.out.println("json string with two defects with all fields and English charset: "+generator.generateStringInJsonFormatForAllFields(2, "Defect", "English"));
            System.out.println("json string with two defects with required fields and English charset: "+generator.generateStringInJsonFormatForRequiredOlnyFields(2,"Defect","English"));
            System.out.println("json string with one defect with required fields and German charset: "+generator.generateStringInJsonFormatForRequiredOlnyFields(1,"Defect","German"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("");
        System.out.println("list with maps of two defects with all fields and Russian charset: "+generator.generateListWithMapOfAllFields(2,"Defect","Russian"));
        System.out.println("list with maps of two defects with required fields and Russian charset: "+generator.generateListWithMapOfRequiredFields(2,"Defect","Russian" ));
        System.out.println("list with maps of two defects with all fields and English charset: "+generator.generateListWithMapOfAllFields(2, "Defect","English"));
        System.out.println("list with maps of two defects with required fields and English charset: "+generator.generateListWithMapOfRequiredFields(2, "Defect","English"));


    }

}
