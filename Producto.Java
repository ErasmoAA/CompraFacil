// Código generado por Bing. Puede contener errores o estar incompleto.

// Crear la clase Producto que implementa la interfaz Serializable
public class Producto implements Serializable {

    // Declarar las variables de instancia
    private int id; // Variable para guardar el identificador del producto en la base de datos
    private String nombre; // Variable para guardar el nombre del producto
    private double precio; // Variable para guardar el precio del producto
    private boolean comprado; // Variable para guardar el estado de comprado del producto

    // Crear el constructor con todos los parámetros
    public Producto(int id, String nombre, double precio, boolean comprado) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.comprado = comprado;
    }

    // Crear el constructor con todos los parámetros excepto el id
    public Producto(String nombre, double precio, boolean comprado) {
        this.nombre = nombre;
        this.precio = precio;
        this.comprado = comprado;
    }

    // Crear los métodos getter y setter para cada variable de instancia
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isComprado() {
        return comprado;
    }

    public void setComprado(boolean comprado) {
        this.comprado = comprado;
    }
}
