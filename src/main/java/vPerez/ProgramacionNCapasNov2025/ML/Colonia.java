/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vPerez.ProgramacionNCapasNov2025.ML;



/**
 *
 * @author digis
 */
public class Colonia {

    private int idColonia;
    private String nombre;
    private String codigoPostal;
//    Municipio municipio = new Municipio();
    public Municipio municipio;

    public int getIdColonia() {
        return idColonia;
    }

    public void setIdColonia(int idColonia) {
        this.idColonia = idColonia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

}
