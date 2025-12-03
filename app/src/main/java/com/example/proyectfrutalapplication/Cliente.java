package com.example.proyectfrutalapplication;

public class Cliente {
    private int id;
    private String nombre;
    private String telefono;
    private String direccion;
    private String email;
    private String notas;
    private boolean activo;

    // Constructor completo
    public Cliente(int id, String nombre, String telefono, String direccion,
                   String email, String notas, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.notas = notas;
        this.activo = activo;
    }

    // Constructor sin ID (para nuevos clientes)
    public Cliente(String nombre, String telefono, String direccion,
                   String email, String notas) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.email = email;
        this.notas = notas;
        this.activo = true;
    }

    // Getters y Setters
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return nombre;
    }
}