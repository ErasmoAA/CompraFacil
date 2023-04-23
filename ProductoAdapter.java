// Código generado por Bing. Puede contener errores o estar incompleto.

// Importar las librerías necesarias
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

// Crear la clase ProductoAdapter que extiende de BaseAdapter
public class ProductoAdapter extends BaseAdapter {

    // Declarar las variables de instancia
    private Context context; // Variable para guardar el contexto de la actividad
    private ArrayList<Producto> listaProductos; // Variable para guardar la lista de productos
    private DecimalFormat df = new DecimalFormat("#.00"); // Formato para mostrar los números con dos decimales

    // Crear el constructor con los parámetros context y listaProductos
    public ProductoAdapter(Context context, ArrayList<Producto> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
    }

    // Sobreescribir el método getCount para devolver el tamaño de la lista de productos
    @Override
    public int getCount() {
        return listaProductos.size();
    }

    // Sobreescribir el método getItem para devolver el producto en la posición indicada
    @Override
    public Producto getItem(int position) {
        return listaProductos.get(position);
    }

    // Sobreescribir el método getItemId para devolver el id del producto en la posición indicada
    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    // Sobreescribir el método getView para devolver la vista personalizada de cada elemento de la lista
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Crear un objeto ViewHolder para guardar las referencias de las vistas del layout
        ViewHolder holder;

        // Comprobar si la vista convertView es nula
        if (convertView == null) {
            // Inflar el layout del elemento de la lista usando el contexto y el LayoutInflater
            convertView = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);

            // Obtener las referencias de las vistas del layout inflado y asignarlas al objeto holder
            holder = new ViewHolder();
            holder.tvNombre = convertView.findViewById(R.id.tvNombre);
            holder.tvPrecio = convertView.findViewById(R.id.tvPrecio);
            holder.cbComprado = convertView.findViewById(R.id.cbComprado);

            // Asignar el objeto holder como tag de la vista convertView
            convertView.setTag(holder);
        } else {
            // Obtener el objeto holder de la tag de la vista convertView
            holder = (ViewHolder) convertView.getTag();
        }

        // Obtener el producto en la posición indicada usando el método getItem
        Producto producto = getItem(position);

        // Asignar los valores del producto a las vistas del objeto holder usando sus métodos getter
        holder.tvNombre.setText(producto.getNombre());
        holder.tvPrecio.setText("$" + df.format(producto.getPrecio()));
        holder.cbComprado.setChecked(producto.isComprado());

        // Devolver la vista convertView
        return convertView;
    }

    // Crear una clase interna estática ViewHolder para guardar las referencias de las vistas del layout
    static class ViewHolder {
        TextView tvNombre; // Texto para mostrar el nombre del producto
        TextView tvPrecio; // Texto para mostrar el precio del producto
        CheckBox cbComprado; // Casilla para marcar si el producto está comprado o no
    }
}
