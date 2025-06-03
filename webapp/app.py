from flask import Flask, render_template, request, redirect, url_for
import sqlite3
from pathlib import Path

app = Flask(__name__)
DATABASE = Path(__file__).resolve().parent / 'productos.db'

# Ensure database and table exist
conn = sqlite3.connect(DATABASE)
conn.execute(
    'CREATE TABLE IF NOT EXISTS productos (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, precio REAL NOT NULL, comprado INTEGER NOT NULL)'
)
conn.close()

PRESUPUESTO = 100.0


def query_db(query, args=(), commit=False):
    conn = sqlite3.connect(DATABASE)
    cur = conn.execute(query, args)
    data = cur.fetchall()
    cur.close()
    if commit:
        conn.commit()
    conn.close()
    return data


def calcular_total():
    res = query_db('SELECT SUM(precio) FROM productos WHERE comprado=1')
    return res[0][0] or 0.0


@app.route('/')
def index():
    productos = query_db('SELECT id, nombre, precio, comprado FROM productos')
    total = calcular_total()
    porcentaje = total / PRESUPUESTO * 100
    return render_template('index.html', productos=productos, total=total, presupuesto=PRESUPUESTO, porcentaje=porcentaje)


@app.route('/add', methods=['POST'])
def add():
    nombre = request.form['nombre'].strip()
    precio = request.form['precio'].strip()
    if nombre and precio:
        try:
            precio_val = float(precio)
        except ValueError:
            precio_val = 0.0
        query_db('INSERT INTO productos (nombre, precio, comprado) VALUES (?, ?, 0)', (nombre, precio_val), commit=True)
    return redirect(url_for('index'))


@app.route('/toggle/<int:pid>', methods=['POST'])
def toggle(pid):
    prod = query_db('SELECT comprado FROM productos WHERE id=?', (pid,))
    if prod:
        nuevo = 0 if prod[0][0] else 1
        query_db('UPDATE productos SET comprado=? WHERE id=?', (nuevo, pid), commit=True)
    return redirect(url_for('index'))


@app.route('/share')
def share():
    productos = query_db('SELECT nombre, precio FROM productos')
    lines = [f"- {nombre}: ${precio:.2f}" for nombre, precio in productos]
    total = calcular_total()
    texto = 'Lista de compras:\n' + '\n'.join(lines) + f"\n\nTotal: ${total:.2f}"
    return texto, 200, {'Content-Type': 'text/plain; charset=utf-8'}


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0')
