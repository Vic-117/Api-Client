/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vPerez.ProgramacionNCapasNov2025.ML;

/**
 *
 * @author digis
 */
public class Estado {
    private int idEstado;
    private String nombre;
    public Pais pais;
    
    public int getIdEstado(){
        return idEstado;
    }
    public void setIdEstado(int idEstado){
        this.idEstado = idEstado;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }
    
    
}
