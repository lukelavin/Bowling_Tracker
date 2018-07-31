/**
 * Created by Luke Lavin 7/30/18
 */

package Bowling_Tracker;

import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.io.*;
import java.util.ArrayList;
/**
 * The Controller of the App. Handles data collection and
 * statistic calculation logic.
 */
public class Controller {
    //fields for adding data points
    public DatePicker datePlayedField; //field for the date played
    public TextField dailyScoreField; //field for the total daily score
    public TextField gamesPlayedField; //field for the # of games played that day

    public DatePicker datePickerForCalc; //date picker for analyzing data past a specific date
    public DatePicker singleDatePicker; //date picker for analyzing data from a single date

    public TextArea rawDataArea; //text area for viewing and editing the raw data used by the program
    public Button editDataButton; //button to confirm changes to the raw data
    public Label dataEditMessage; //confirmation message after data has been edited
    public CheckBox viewRawDataCheckbox; //checkbox to enable the text area and button for the raw data
    private boolean dataEdited; //flag to see if the confirmation message was just shown

    //fields for displaying the calculated stats
    public TextField statsTotalPins; //text field to clearly display total pins over the background
    public TextField statsTotalGames; //text field to clearly display total games over the background
    public TextField statsAverageScore; //text field to clearly display average score over the background
    public TextField statsDate; //text field to clearly display the date from which data were analyzed over the background

    /**
     * Formats the data from the 3 entry fields and inputs it into the data.txt file.
     */
    public void addData(ActionEvent actionEvent) throws IOException {
        //take the input from the 3 entry fields and properly format it
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(datePlayedField.getValue()+ ",");
        stringBuilder.append(dailyScoreField.getText() + ",");
        stringBuilder.append(gamesPlayedField.getText() + "\n");
        System.out.println(stringBuilder);

        //use FileWriter to add the data to the text file without replacing previous data
        FileWriter fileWriter = new FileWriter("data.txt", true);
        fileWriter.write(stringBuilder.toString());
        fileWriter.close();

        //clear the data fields when done
        datePlayedField.getEditor().setText("");
        dailyScoreField.setText("");
        gamesPlayedField.setText("");
    }

    /**
     * Reads from the data.txt file to iterate through the data and sum
     * the total pins scored and total games played. Calls updateStats() to
     * calculate average and update the statistics in the proper fields.
     */
    public void calculateAverage(ActionEvent actionEvent) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("data.txt"));
        String line;
        int scoreSum = 0;
        int gamesPlayed = 0;
        while((line = bufferedReader.readLine()) != null) {
            if(!line.equals("")) {
                String[] lineData = line.split(",");
                scoreSum += Integer.parseInt(lineData[1]);
                gamesPlayed += Integer.parseInt(lineData[2]);
            }
        }
        bufferedReader.close();

        updateStats(scoreSum, gamesPlayed);
    }

    /**
     * Sorts the data.txt file by date, and then iterates through it
     * to sum the total pins and total games from the date specified
     * in the DatePicker currently. Then calls updateStats() to
     * calculate average and update the statistics in the proper fields.
     */
    public void calculateAverageFromDate(ActionEvent actionEvent) throws IOException {
        System.out.println("test");
        BufferedReader bufferedReader = new BufferedReader(new FileReader("data.txt"));

        String line;
        ArrayList<String> allLines = new ArrayList<String>();
        while((line = bufferedReader.readLine()) != null) {
            allLines.add(line);
        }

        String[] strs = allLines.toArray(new String[allLines.size()]);
        strs = sortByDate(strs);

        int scoreSum = 0;
        int gamesPlayed = 0;
        boolean sinceFlag = false; //flags true if the while loop has reached the target date
        String sinceDate = datePickerForCalc.getValue().toString();
        System.out.println(sinceDate);
        for(String str : strs) {
            if(!str.isEmpty()) {
                String[] lineData = str.split(",");
                if (lineData[0].compareTo(sinceDate) >= 0) {
                    sinceFlag = true;
                }
                if (sinceFlag) {
                    scoreSum += Integer.parseInt(lineData[1]);
                    gamesPlayed += Integer.parseInt(lineData[2]);
                }
            }
        }
        bufferedReader.close();

        updateStats(scoreSum, gamesPlayed, sinceDate);
    }

    /**
     * Iterates through the data.txt file to each entry from the date
     * specified in the DatePicker. Then passes the total pins and
     * total games into updateStats() to calculate average and update
     * statistics in the proper fields.
     */
    public void calculateSingleDay(ActionEvent actionEvent) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("data.txt"));
        String line;
        int scoreSum = 0;
        int gamesPlayed = 0;
        while((line = bufferedReader.readLine()) != null) {
            if(!line.equals("") && line.substring(0, 10).equals(singleDatePicker.getValue().toString())) {
                String[] lineData = line.split(",");
                scoreSum += Integer.parseInt(lineData[1]);
                gamesPlayed += Integer.parseInt(lineData[2]);
            }
        }
        bufferedReader.close();

        updateStats(scoreSum, gamesPlayed, singleDatePicker.getValue().toString());
    }

    /**
     * Overwrites the data.txt file entirely with what is currently in
     * the TextArea. A message is displayed when completed.
     */
    public void directEditData(ActionEvent actionEvent) throws IOException {
        FileWriter fileWriter = new FileWriter("data.txt" );
        fileWriter.write(rawDataArea.getText());
        fileWriter.close();

        dataEditMessage.setText("Data edited.");

        toggleRawData();
        viewRawDataCheckbox.setSelected(false);
    }

    /**
     * Public method to call toggleRawData()
     */
    public void toggleRawDataDisable(ActionEvent actionEvent) throws IOException {
        toggleRawData();
        dataEditMessage.setText("");
    }

    /**
     * Toggles the disable for the TextArea and Button handling
     * the raw data as an added measure of safety against accidental
     * loss of data.
     */
    private void toggleRawData() throws IOException {
        //disables the raw data text and button if enabled. enables them if disabled
        rawDataArea.setDisable(!rawDataArea.isDisabled());
        editDataButton.setDisable(!editDataButton.isDisabled());

        //updates the raw data text if being enabled
        if(!rawDataArea.isDisabled()){
            String rawDataText = "";
            BufferedReader bufferedReader = new BufferedReader(new FileReader("data.txt"));
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                rawDataText += line + "\n";
            }
            bufferedReader.close();
            rawDataArea.setText(rawDataText);
        }
    }

    /**
     * Sorts the data.txt file to find the earliest dated data, then passes along
     * the totalScore, totalGames, and the newly found date to the proper updateStats()
     * method.
     *
     * @param totalScore    the total score of the data set being analyzed
     * @param totalGames    the total # of games in the data set being analyzed
     */
    private void updateStats(int totalScore, int totalGames) throws IOException {
        //find the earliest date
        BufferedReader bufferedReader = new BufferedReader(new FileReader("data.txt"));

        String line;
        ArrayList<String> allLines = new ArrayList<String>();
        while((line = bufferedReader.readLine()) != null) {
            allLines.add(line);
        }

        String[] strs = allLines.toArray(new String[allLines.size()]);
        strs = sortByDate(strs);
        String[] firstLineSplit = strs[0].split(",");

        //pass the info (including the newly found earliest date) to the more comprehensive method
        updateStats(totalScore, totalGames, firstLineSplit[0]);
    }

    /**
     * Sets the statistics text fields to the proper values based on the analyzed data
     * and enables them.
     *
     * @param totalScore    the total score of the data set being analyzed
     * @param totalGames    the total # of games in the data set being analyzed
     * @param date    the date of the earliest entry in the data set
     */
    private void updateStats(int totalScore, int totalGames, String date) {
        //calculate the average given the sums of total pins and games
        double average = (totalScore * 1.0) / totalGames;

        //enable and update the fields to display pins, games, average, and date properly
        statsTotalPins.setDisable(false);
        statsTotalPins.setText("Total Score: " + totalScore);
        statsTotalGames.setDisable(false);
        statsTotalGames.setText("Total Game: " + totalGames);
        statsAverageScore.setDisable(false);
        statsAverageScore.setText("Average: " + average);
        statsDate.setDisable(false);
        statsDate.setText("Analyzed from: " + date);

        System.out.println("Stats Updated");
    }
    /**
     * Sorts the string array alphabetically using selection sort.
     *
     * @param strings   the array to be sorted
     */
    private String[] sortByDate(String[] strings){
        String[] output = new String[strings.length];

        for(int i = 0; i < strings.length; i++){
            output[i] = strings[i];
        }

        for(int i = 0; i < output.length - 1; i++){
            for(int j = i + 1; j < output.length; j++){
                if(output[i].compareTo(output[j]) >= 0){
                    String temp = output[i];
                    output[i] = output[j];
                    output[j] = temp;
                }
            }
        }
        return output;
    }
}
