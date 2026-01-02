// Vehicle.java
public class Vehicle {
    private final String name;
    private final String cnic;
    private final int age;
    private final String vehicleNumber;
    private final String licenseNumber;

    public Vehicle(String name, String cnic, int age, String vehicleNumber, String licenseNumber) {
        this.name = name;
        this.cnic = cnic;
        this.age = age;
        this.vehicleNumber = vehicleNumber;
        this.licenseNumber = licenseNumber;
    }

    public String getName() { return name; }
    public String getCnic() { return cnic; }
    public int getAge() { return age; }
    public String getVehicleNumber() { return vehicleNumber; }
    public String getLicenseNumber() { return licenseNumber; }

    // CSV friendly
    public String toCSV() {
        return escape(name) + "," + escape(cnic) + "," + age + "," + escape(vehicleNumber) + "," + escape(licenseNumber);
    }
    private String escape(String s) {
        return s == null ? "" : s.replace(",", " ").trim();
    }

    public static Vehicle fromCSV(String line) {
        if (line == null) return null;
        String[] p = line.split(",");
        if (p.length < 5) return null;
        String name = p[0].trim();
        String cnic = p[1].trim();
        int age = 0;
        try { age = Integer.parseInt(p[2].trim()); } catch (Exception ex) {}
        String vnum = p[3].trim();
        String lnum = p[4].trim();
        return new Vehicle(name, cnic, age, vnum, lnum);
    }
}
