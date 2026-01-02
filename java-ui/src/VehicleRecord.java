// VehicleRecord.java
public class VehicleRecord {
    public String name;
    public String cnic;
    public int age;
    public String vehicleNumber;
    public String licenseNumber;

    public VehicleRecord(String name, String cnic, int age, String vehicleNumber, String licenseNumber) {
        this.name = name;
        this.cnic = cnic;
        this.age = age;
        this.vehicleNumber = vehicleNumber;
        this.licenseNumber = licenseNumber;
    }

    public String toCSVLine() {
        // escape commas simply by replacing (you can improve if needed)
        return String.format("%s,%s,%d,%s,%s", name.replace(",", " "), cnic, age, vehicleNumber.replace(",", " "), licenseNumber.replace(",", " "));
    }

    public static VehicleRecord fromCSV(String line) {
        String[] p = line.split(",", -1);
        if (p.length < 5) return null;
        try {
            return new VehicleRecord(p[0].trim(), p[1].trim(), Integer.parseInt(p[2].trim()), p[3].trim(), p[4].trim());
        } catch (Exception ex) { return null; }
    }
}
