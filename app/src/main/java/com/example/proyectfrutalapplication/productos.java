package com.example.proyectfrutalapplication;

public class productos {
    private String codigo;
    private String nombre;
    private String contenido;
    private String cantidad;
    private String precio;
    private String fecha_producccion;
    private String fecha_vancimiento;

    public productos(String codigo, String nombre, String contenido, String cantidad, String precio, String fecha_producccion, String fecha_vancimiento) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.contenido = contenido;
        this.cantidad = cantidad;
        this.precio = precio;
        this.fecha_producccion = fecha_producccion;
        this.fecha_vancimiento = fecha_vancimiento;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getFecha_producccion() {
        return fecha_producccion;
    }

    public void setFecha_producccion(String fecha_producccion) {
        this.fecha_producccion = fecha_producccion;
    }

    public String getFecha_vancimiento() {
        return fecha_vancimiento;
    }

    public void setFecha_vancimiento(String fecha_vancimiento) {
        this.fecha_vancimiento = fecha_vancimiento;
    }

    @Override
    public String toString() {
        return "productos{" +
                "codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", contenido='" + contenido + '\'' +
                ", cantidad='" + cantidad + '\'' +
                ", precio='" + precio + '\'' +
                ", fecha_producccion='" + fecha_producccion + '\'' +
                ", fecha_vancimiento='" + fecha_vancimiento + '\'' +
                '}';
    }
}
