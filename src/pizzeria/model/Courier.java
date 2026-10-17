package pizzeria.model;

public class Courier {
    private final String id;
    private final String name;
    private final String phone;
    private boolean available;

    public Courier(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.available = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
