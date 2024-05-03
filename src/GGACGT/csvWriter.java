package GGACGT;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class csvWriter {

    public static void writeToExcelFile(ArrayList<Solution> population) {
        String[] headers = {"Solution ID", "Fitness Value"};
        Double[][] data = new Double[population.size()][2];
        int k=0;
        for (Solution sol : population){
            data[k][0] = (double) sol.solutionID;
            data[k][1] = sol.fitnessValue;
            k++;
        }

        String csvFilePath = "C:\\Users\\Manoharan\\Desktop\\SolutionDatamix4.csv";

        try (FileWriter csvWriter = new FileWriter(csvFilePath)) {
            // Write headers
            for (int i = 0; i < headers.length; i++) {
                csvWriter.append(headers[i]);
                if (i < headers.length - 1) {
                    csvWriter.append(",");
                }
            }
            csvWriter.append("\n");

            // Write data
            for (Object[] row : data) {
                for (int i = 0; i < row.length; i++) {
                    csvWriter.append(String.valueOf(row[i]));
                    if (i < row.length - 1) {
                        csvWriter.append(",");
                    }
                }
                csvWriter.append("\n");
            }

            System.out.println("CSV file has been created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
