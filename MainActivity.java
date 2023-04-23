// Código generado por Bing. Puede contener errores o estar incompleto.

// Importar las librerías necesarias
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;

// Crear la clase principal de la app que extiende de AppCompatActivity
public class MainActivity extends AppCompatActivity {

    // Declarar las variables globales
    private EditText etNombre, etPrecio; // Campos de texto para introducir el nombre y el precio del producto
    private Button btnAgregar, btnCompartir; // Botones para agregar un producto y compartir la lista
    private ListView lvProductos; // Lista para mostrar los productos
    private TextView tvPresupuesto, tvProgreso, tvTotal; // Textos para mostrar el presupuesto, el progreso y el total
    private ArrayList<Producto> listaProductos; // ArrayList para almacenar los objetos Producto
    private ProductoAdapter adapter; // Adaptador personalizado para mostrar los productos en la lista
    private double presupuesto = 100.0; // Variable para guardar el presupuesto máximo
    private double total = 0.0; // Variable para guardar el total gastado
    private DecimalFormat df = new DecimalFormat("#.00"); // Formato para mostrar los números con dos decimales

    // Crear la base de datos y la tabla de productos
    private SQLiteDatabase db;
    private static final String CREATE_TABLE_PRODUCTOS = "CREATE TABLE IF NOT EXISTS productos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombre TEXT NOT NULL," +
            "precio REAL NOT NULL," +
            "comprado INTEGER NOT NULL" +
            ")";

    // Método que se ejecuta al crear la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener las referencias de las vistas del layout
        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnCompartir = findViewById(R.id.btnCompartir);
        lvProductos = findViewById(R.id.lvProductos);
        tvPresupuesto = findViewById(R.id.tvPresupuesto);
        tvProgreso = findViewById(R.id.tvProgreso);
        tvTotal = findViewById(R.id.tvTotal);

        // Inicializar el ArrayList y el adaptador
        listaProductos = new ArrayList<>();
        adapter = new ProductoAdapter(this, listaProductos);

        // Asignar el adaptador a la lista
        lvProductos.setAdapter(adapter);

        // Abrir o crear la base de datos en modo escritura
        db = openOrCreateDatabase("ListaCompras.db", MODE_PRIVATE, null);

        // Ejecutar la sentencia SQL para crear la tabla de productos si no existe
        db.execSQL(CREATE_TABLE_PRODUCTOS);

        // Cargar los datos de la tabla en el ArrayList
        cargarDatos();

        // Mostrar el presupuesto y el total en los textos correspondientes
        tvPresupuesto.setText("Presupuesto: $" + df.format(presupuesto));
        tvTotal.setText("Total: $" + df.format(total));

        // Calcular y mostrar el progreso del presupuesto en porcentaje
        calcularProgreso();

        // Establecer un listener para el botón agregar
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores introducidos por el usuario en los campos de texto
                String nombre = etNombre.getText().toString().trim();
                String precio = etPrecio.getText().toString().trim();

                // Comprobar que no estén vacíos
                if (nombre.isEmpty() || precio.isEmpty()) {
                    // Mostrar un mensaje de error
                    Toast.makeText(MainActivity.this, "Debes introducir un nombre y un precio", Toast.LENGTH_SHORT).show();
                } else {
                    // Convertir el precio a un número real
                    double precioNum = Double.parseDouble(precio);

                    // Crear un objeto Producto con los valores introducidos y comprado en false
                    Producto producto = new Producto(nombre, precioNum, false);
                    
                    // Insertar el producto en la tabla de la base de datos
                    insertarProducto(producto);

                    // Añadir el producto al ArrayList
                    listaProductos.add(producto);

                    // Notificar al adaptador que los datos han cambiado
                    adapter.notifyDataSetChanged();

                    // Actualizar el total y el progreso
                    total += precioNum;
                    tvTotal.setText("Total: $" + df.format(total));
                    calcularProgreso();

                    // Limpiar los campos de texto
                    etNombre.setText("");
                    etPrecio.setText("");
                }
            }
        });

        // Establecer un listener para el botón compartir
        btnCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un intent para enviar un mensaje de texto
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                // Crear un StringBuilder para construir el mensaje con la lista de productos y el total
                StringBuilder sb = new StringBuilder();
                sb.append("Lista de compras:\n\n");
                for (Producto p : listaProductos) {
                    sb.append("- " + p.getNombre() + ": $" + df.format(p.getPrecio()) + "\n");
                }
                sb.append("\nTotal: $" + df.format(total));

                // Poner el mensaje como extra del intent
                intent.putExtra(Intent.EXTRA_TEXT, sb.toString());

                // Iniciar la actividad para enviar el mensaje, mostrando las opciones disponibles
                startActivity(Intent.createChooser(intent, "Compartir lista de compras"));
            }
        });

        // Establecer un listener para la lista de productos
        lvProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el producto que se ha pulsado en la lista
                Producto producto = listaProductos.get(position);

                // Cambiar el estado de comprado a su opuesto
                producto.setComprado(!producto.isComprado());

                // Actualizar el producto en la tabla de la base de datos
                actualizarProducto(producto);

                // Notificar al adaptador que los datos han cambiado
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Método para cargar los datos de la tabla de productos en el ArrayList
    private void cargarDatos() {
        // Crear un cursor para consultar todos los registros de la tabla
        Cursor cursor = db.rawQuery("SELECT * FROM productos", null);

        // Mover el cursor al primer registro
        if (cursor.moveToFirst()) {
            // Recorrer el cursor hasta el último registro
            do {
                // Obtener los valores de cada campo del registro actual
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                double precio = cursor.getDouble(2);
                boolean comprado = cursor.getInt(3) == 1;

                // Crear un objeto Producto con los valores obtenidos
                Producto producto = new Producto(id, nombre, precio, comprado);

                // Añadir el producto al ArrayList
                listaProductos.add(producto);

                // Sumar el precio al total si el producto está comprado
                if (comprado) {
                    total += precio;
                }
            } while (cursor.moveToNext()); // Mover el cursor al siguiente registro
        }

        // Cerrar el cursor
        cursor.close();
    }

    // Método para insertar un producto en la tabla de productos
    private void insertarProducto(Producto producto) {
// Crear una sentencia SQL para insertar el producto con sus valores
        String sql = "INSERT INTO productos (nombre, precio, comprado) VALUES (" +
                "'" + producto.getNombre() + "'," +
                producto.getPrecio() + "," +
                (producto.isComprado() ? 1 : 0) +
                ")";

        // Ejecutar la sentencia SQL
        db.execSQL(sql);
    }

    // Método para actualizar un producto en la tabla de productos
    private void actualizarProducto(Producto producto) {
       
    // Crear una sentencia SQL para actualizar el producto con sus valores
        String sql = "UPDATE productos SET " +
                "nombre = '" + producto.getNombre() + "'," +
                "precio = " + producto.getPrecio() + "," +
                "comprado = " + (producto.isComprado() ? 1 : 0) +
                " WHERE id = " + producto.getId();

        // Ejecutar la sentencia SQL
        db.execSQL(sql);
    }

    // Método para calcular y mostrar el progreso del presupuesto en porcentaje
    private void calcularProgreso() {
        // Calcular el porcentaje del total respecto al presupuesto
        double porcentaje = (total / presupuesto) * 100;

        // Mostrar el porcentaje en el texto correspondiente
        tvProgreso.setText(df.format(porcentaje) + "%");

        // Comprobar si el porcentaje es mayor o igual a 100
        if (porcentaje >= 100) {
            // Mostrar un mensaje de alerta al usuario
            Toast.makeText(this, "Has sobrepasado el presupuesto", Toast.LENGTH_LONG).show();
        }
    }

    // Método que se ejecuta al destruir la actividad
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cerrar la base de datos
        db.close();
    }
}
