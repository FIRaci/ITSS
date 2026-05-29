package com.itss;

/**
 * DTO đại diện cho một mặt hàng trong Danh mục Hàng hóa.
 */
public class Merchandise {
    private String code;       // Mã hàng (Primary Key)
    private String name;       // Tên hàng
    private String unit;       // Đơn vị tính mặc định
    private String description; // Mô tả

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

    /** Trả về chuỗi hiển thị trong danh sách gợi ý: "[code] - [name]" */
    @Override
    public String toString() {
        return code + " - " + name;
    }
}
