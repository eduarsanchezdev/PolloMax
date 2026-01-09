package com.example.pollomax;

public class Corral {
    private int id;
    private String nombre;
    private int cantidadPollos;
    private double precioPollos;
    private String fechaCreacion;


    public Corral(int id, String nombre, int cantidadPollos, double precioPollos, String fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.cantidadPollos = cantidadPollos;
        this.precioPollos = precioPollos;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getCantidadPollos() { return cantidadPollos; }
    public double getPrecioPollos() { return precioPollos; }
    public String getFechaCreacion() { return fechaCreacion; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCantidadPollos(int cantidadPollos) { this.cantidadPollos = cantidadPollos; }
    public void setPrecioPollos(double precioPollos) { this.precioPollos = precioPollos; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
