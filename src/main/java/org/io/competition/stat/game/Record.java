package org.io.competition.stat.game;

/**
 * @author Jay Wu
 */
public class Record {

    private Integer amount;
    private String brandName;
    private String date;
    private String location;

    public Integer getAmount() {
        return amount;
    }

    public Record setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public String getBrandName() {
        return brandName;
    }

    public Record setBrandName(String brandName) {
        this.brandName = brandName;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Record setDate(String date) {
        this.date = date;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public Record setLocation(String location) {
        this.location = location;
        return this;
    }
}
