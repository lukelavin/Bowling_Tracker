/**
 * Created by Luke Lavin 7/30/18
 */

package Bowling_Tracker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Controller of the App. Handles data collection and
 * statistic calculation logic.
 */
public class Controller {
    //fields for adding data points
    public DatePicker datePlayedField; //field for the date played
    public TextField dailyScoreField; //field for the total daily score
    public TextField gamesPlayedField; //field for the # of games played that day

    public DatePicker datePickerFrom; //date picker for analyzing data past a specific date
    public DatePicker datePickerTo; //date picker for analyzing data from a single date

    public TextArea rawDataArea; //text area for viewing and editing the raw data used by the program
    public Button editDataButton; //button to confirm changes to the raw data
    public Label dataEditMessage; //confirmation message after data has been edited
    public CheckBox viewRawDataCheckbox; //checkbox to enable the text area and button for the raw data

    //fields for displaying the calculated stats
    public TextField statsTotalPins; //text field to clearly display total pins over the background
    public TextField statsTotalGames; //text field to clearly display total games over the background
    public TextField statsAverageScore; //text field to clearly display average score over the background
    public TextField statsDate; //text field to clearly display the date from which data were analyzed over the background

    public TableView monthTable;
    public TableColumn monthColumn; //the table column displaying month
    public TableColumn averageColumn; //the table column displaying the average for the month
    public TableColumn scoreColumn; //the table column displaying the total score for the month
    public TableColumn gamesColumn; //the table column displaying the total games for the month
    public ChoiceBox yearSelector;

    public void initialize() throws IOException {
        yearSelector.getItems().remove(0, yearSelector.getItems().size());
        yearSelector.getItems().addAll((Object[]) getYearsFromData());
    }

    private String[] getYearsFromData() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/Bowling_Tracker/data.txt"));

        String line;
        ArrayList<String> allLines = new ArrayList<String>();
        while((line = bufferedReader.readLine()) != null) {
            allLines.add(line);
        }

        String[] strs = allLines.toArray(new String[allLines.size()]);
        strs = sortByDate(strs);
        for(int i = 0; i < strs.length; i++) {
            strs[i] = strs[i].substring(0, 4);
        }
        System.out.println("Sorted: " + Arrays.toString(strs));
        strs = removeDuplicates(strs);
        System.out.println("Duplicates Removed: " + Arrays.toString(strs));
        return strs;
    }

    private String[] removeDuplicates(String[] strings) {
        int uniqueCount = 0;
        String[] copy = new String[strings.length];
        for(int i = 0; i < copy.length; i++) {
            copy[i] = strings[i];
        }

        String[] temp = new String[copy.length];
        for(int i = 0; i < copy.length; i++) {
            if(!contains(temp, copy[i])) {
                temp[uniqueCount] = copy[i];
                uniqueCount++;
            }
        }

        copy = new String[uniqueCount];
        for(int i = 0; i < copy.length; i++) {
            copy[i] = temp[i];
        }

        return copy;
    }

    private boolean contains(String[] array, String target) {
        for(String str : array) {
            if(str != null && str.equals(target)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Formats the data from the 3 entry fields and inputs it into the src/Bowling_Tracker/data.txt file.
     */
    public void addData(ActionEvent actionEvent) throws IOException {
        //take the input from the 3 entry fields and properly format it
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(datePlayedField.getValue()+ ",");
        stringBuilder.append(dailyScoreField.getText() + ",");
        stringBuilder.append(gamesPlayedField.getText() + "\n");
        System.out.println(stringBuilder);

        //use FileWriter to add the data to the text file without replacing previous data
        FileWriter fileWriter = new FileWriter("src/Bowling_Tracker/data.txt", true);
        fileWriter.write(stringBuilder.toString());
        fileWriter.close();

        //clear the data fields when done
        datePlayedField.getEditor().setText("");
        dailyScoreField.setText("");
        gamesPlayedField.setText("");
    }

    /**
     * Reads from the src/Bowling_Tracker/data.txt file to iterate through the data and sum
     * the total pins scored and total games played. Calls updateStats() to
     * calculate average and update the statistics in the proper fields.
     */
    public void calculateAverage(ActionEvent actionEvent) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/Bowling_Tracker/data.txt"));
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


    public void calculateAverageFromDate(ActionEvent actionEvent) throws IOException {
        String sinceDate = datePickerFrom.getValue().toString();
        String toDate = datePickerTo.getValue().toString();

        if(sinceDate.equals(toDate))
            calculateSingleDay(sinceDate);
        else
            calculateDateRange(sinceDate, toDate);
    }

    /**
     * Iterates through the src/Bowling_Tracker/data.txt file to each entry from the date
     * specified in the DatePicker. Then passes the total pins and
     * total games into updateStats() to calculate average and update
     * statistics in the proper fields.
     *
     * @param sinceDate    the date to use for calculations
     */
    private void calculateSingleDay(String sinceDate) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/Bowling_Tracker/data.txt"));
        String line;
        int scoreSum = 0;
        int gamesPlayed = 0;
        while((line = bufferedReader.readLine()) != null) {
            if(!line.equals("") && line.substring(0, 10).equals(sinceDate)) {
                String[] lineData = line.split(",");
                scoreSum += Integer.parseInt(lineData[1]);
                gamesPlayed += Integer.parseInt(lineData[2]);
            }
        }
        bufferedReader.close();

        updateStats(scoreSum, gamesPlayed, sinceDate);
    }

    /**
     * Sorts the src/Bowling_Tracker/data.txt file by date, and then iterates through it
     * to sum the total pins and total games from the date specified
     * in the fromDatePicker currently to the date specified in the
     * toDatePicker currently. Then calls updateStats() to calculate
     * average and update the statistics in the proper fields.
     */
    private void calculateDateRange(String sinceDate, String toDate) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/Bowling_Tracker/data.txt"));

        String line;
        ArrayList<String> allLines = new ArrayList<String>();
        while((line = bufferedReader.readLine()) != null) {
            allLines.add(line);
        }

        String[] strs = allLines.toArray(new String[allLines.size()]);
        strs = sortByDate(strs);

        int scoreSum = 0;
        int gamesPlayed = 0;

        for(String str : strs) {
            if(!str.isEmpty()) {
                String[] lineData = str.split(",");
                if(lineData[0].compareTo(toDate) > 0) {
                    break;
                }
                if (lineData[0].compareTo(sinceDate) >= 0) {
                    scoreSum += Integer.parseInt(lineData[1]);
                    gamesPlayed += Integer.parseInt(lineData[2]);
                }
            }
        }
        bufferedReader.close();

        updateStats(scoreSum, gamesPlayed, sinceDate);
    }

    /**
     * Overwrites the src/Bowling_Tracker/data.txt file entirely with what is currently in
     * the TextArea. A message is displayed when completed.
     */
    public void directEditData(ActionEvent actionEvent) throws IOException {
        FileWriter fileWriter = new FileWriter("src/Bowling_Tracker/data.txt" );
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
            BufferedReader bufferedReader = new BufferedReader(new FileReader("src/Bowling_Tracker/data.txt"));
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
     * Sorts the src/Bowling_Tracker/data.txt file to find the earliest dated data, then passes along
     * the totalScore, totalGames, and the newly found date to the proper updateStats()
     * method.
     *
     * @param totalScore    the total score of the data set being analyzed
     * @param totalGames    the total # of games in the data set being analyzed
     */
    private void updateStats(int totalScore, int totalGames) throws IOException {
        //find the earliest date
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/Bowling_Tracker/data.txt"));

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

    public void updateMonthlyStats(ActionEvent actionEvent) throws IOException {
        monthColumn.setCellValueFactory(
                new PropertyValueFactory<MonthData, String>("month")
        );
        scoreColumn.setCellValueFactory(
                new PropertyValueFactory<MonthData, Integer>("totalScore")
        );
        gamesColumn.setCellValueFactory(
                new PropertyValueFactory<MonthData, Integer>("totalGames")
        );
        averageColumn.setCellValueFactory(
                new PropertyValueFactory<MonthData, Double>("average")
        );

        String year = yearSelector.getValue().toString();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        MonthData[] monthDatas = new MonthData[12];
        for(int i = 0; i < monthDatas.length; i++) {
            String prefix = year + "-";
            if(i < 10){
                prefix += "0";
            }
            prefix += i + 1;

            BufferedReader bufferedReader = new BufferedReader(new FileReader("src/Bowling_Tracker/data.txt"));
            String line;
            int scoreSum = 0;
            int gamesPlayed = 0;
            while((line = bufferedReader.readLine()) != null) {
                if(!line.equals("") && line.substring(0, 7).equals(prefix)) {
                    String[] lineData = line.split(",");
                    scoreSum += Integer.parseInt(lineData[1]);
                    gamesPlayed += Integer.parseInt(lineData[2]);
                }
            }
            bufferedReader.close();

            monthDatas[i] = new MonthData(months[i], scoreSum, gamesPlayed, (1.0 * scoreSum) / gamesPlayed);
        }

        System.out.println(Arrays.toString(monthDatas));
        ObservableList<MonthData> data = FXCollections.observableArrayList(monthDatas);
        monthTable.setItems(data);
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
