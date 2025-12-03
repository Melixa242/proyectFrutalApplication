package com.example.proyectfrutalapplication;

import org.json.JSONException;
import org.json.JSONObject;

public class FruitAPI {
    private String name;
    private int id;
    private String family;
    private String order;
    private String genus;
    private Nutritions nutritions;

    public FruitAPI() {}

    public FruitAPI(JSONObject json) throws JSONException {
        this.name = json.optString("name", "");
        this.id = json.optInt("id", 0);
        this.family = json.optString("family", "");
        this.order = json.optString("order", "");
        this.genus = json.optString("genus", "");

        if (json.has("nutritions")) {
            this.nutritions = new Nutritions(json.getJSONObject("nutritions"));
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFamily() { return family; }
    public void setFamily(String family) { this.family = family; }

    public String getOrder() { return order; }
    public void setOrder(String order) { this.order = order; }

    public String getGenus() { return genus; }
    public void setGenus(String genus) { this.genus = genus; }

    public Nutritions getNutritions() { return nutritions; }
    public void setNutritions(Nutritions nutritions) { this.nutritions = nutritions; }

    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("✨ *Información Nutricional de ").append(getName()).append("* ✨\n\n");

        if (getGenus() != null && !getGenus().isEmpty()) {
            sb.append("Familia: ").append(getFamily()).append("\n");
        }
        if (getOrder() != null && !getOrder().isEmpty()) {
            sb.append("Orden: ").append(getOrder()).append("\n\n");
        }

        sb.append("▪️ Calorías: ").append(getNutritions().getCalories()).append(" kcal\n");
        sb.append("▪️ Grasas: ").append(getNutritions().getFat()).append(" g\n");
        sb.append("▪️ Azúcar: ").append(getNutritions().getSugar()).append(" g\n");
        sb.append("▪️ Carbohidratos: ").append(getNutritions().getCarbohydrates()).append(" g\n");
        sb.append("▪️ Proteína: ").append(getNutritions().getProtein()).append(" g\n");

        return sb.toString();
    }



    public String getShortDescription() {
        if (nutritions != null) {
            return String.format("%s - %.0f cal", name, nutritions.getCalories());
        }
        return name;
    }
}