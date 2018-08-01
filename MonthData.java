package Bowling_Tracker;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by lukel on 7/31/2018.
 */
public class MonthData {
    private final SimpleStringProperty month;
    private final SimpleIntegerProperty totalScore;
    private final SimpleIntegerProperty totalGames;
    private final SimpleDoubleProperty average;

    public MonthData(String month, int totalScore, int totalGames, double average) {
        this.month = new SimpleStringProperty(month);
        this.totalScore = new SimpleIntegerProperty(totalScore);
        this.totalGames = new SimpleIntegerProperty(totalGames);
        this.average = new SimpleDoubleProperty(average);
    }

    public String getMonth() {
        return month.get();
    }

    public SimpleStringProperty monthProperty() {
        return month;
    }

    public void setMonth(String month) {
        this.month.set(month);
    }

    public int getTotalScore() {
        return totalScore.get();
    }

    public SimpleIntegerProperty totalScoreProperty() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore.set(totalScore);
    }

    public int getTotalGames() {
        return totalGames.get();
    }

    public SimpleIntegerProperty totalGamesProperty() {
        return totalGames;
    }

    public void setTotalGames(int totalGames) {
        this.totalGames.set(totalGames);
    }

    public double getAverage() {
        return average.get();
    }

    public SimpleDoubleProperty averageProperty() {
        return average;
    }

    public void setAverage(double average) {
        this.average.set(average);
    }

    public String toString() {
        return month.getValue() + "|" + totalScore.getValue() + "|" + totalGames.getValue() + "|" + average.getValue() + "|";
    }
}
