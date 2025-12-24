/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vPerez.ProgramacionNCapasNov2025.ML;



/**
 *
 * @author digis
 */
public class Direccion {

    private int IdDireccion;
    private String calle;
    private String numeroInterior;
    private String numeroExterior;
    public Colonia colonia;
    
// public Usuario usuario;//RELACION DEL LADO DE MUCHOS,Muchas direcciones son parte de un Usuario

    public int getIdDireccion() {
        return IdDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        this.IdDireccion = idDireccion;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumeroInterior() {
        return numeroInterior;
    }

    public void setNumeroInterior(String numeroInterior) {
        this.numeroInterior = numeroInterior;
    }

    public String getNumeroExterior() {
        return numeroExterior;

    }

    public void setNumeroExterior(String numeroExterior) {
        this.numeroExterior = numeroExterior;
    }

    public Colonia getColonia() {
        return colonia;
    }

    public void setColonia(Colonia colonia) {
        this.colonia = colonia;
    }

}
