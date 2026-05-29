package entity.chung;

public class Merchandise {
    private String code;
    private String name;
    private String unit;
    private String description;

    public Merchandise() {}

    public Merchandise(String code, String name, String unit, String description) {
        this.code = code;
        this.name = name;
        this.unit = unit;
        this.description = description;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() { return code + " - " + name; }
}
