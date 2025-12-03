package com.example.proyectfrutalapplication;

import org.json.JSONException;
import org.json.JSONObject;

public class Nutritions {
    private double calories;
    private double fat;
    private double sugar;
    private double carbohydrates;
    private double protein;

    public Nutritions() {}

    public Nutritions(JSONObject json) throws JSONException {
        this.calories = json.optDouble("calories", 0);
        this.fat = json.optDouble("fat", 0);
        this.sugar = json.optDouble("sugar", 0);
        this.carbohydrates = json.optDouble("carbohydrates", 0);
        this.protein = json.optDouble("protein", 0);
    }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getFat() { return fat; }
    public void setFat(double fat) { this.fat = fat; }

    public double getSugar() { return sugar; }
    public void setSugar(double sugar) { this.sugar = sugar; }

    public double getCarbohydrates() { return carbohydrates; }
    public void setCarbohydrates(double carbohydrates) { this.carbohydrates = carbohydrates; }

    public double getProtein() { return protein; }
    public void setProtein(double protein) { this.protein = protein; }

    @Override
    public String toString() {
        return String.format("Cal: %.0f | Grasas: %.1fg | Azúcar: %.1fg | Carboh: %.1fg | Prot: %.1fg",
                calories, fat, sugar, carbohydrates, protein);
    }
}