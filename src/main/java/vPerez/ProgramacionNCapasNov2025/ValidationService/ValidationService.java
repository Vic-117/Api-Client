/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vPerez.ProgramacionNCapasNov2025.ValidationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

/**
 *
 * @author digis
 */
@Service
//Service contiene la lógica de negocio
public class ValidationService {
    @Autowired
    private Validator validator; //Porviene de Spring
    
    
    //Metodo para validar los campos usando binding result de Spring
    //Validacion de acuerdo a las anotaciones que tenga el DTO
    public BindingResult validateObjects(Object target){
        DataBinder dataBinder = new DataBinder(target);
        dataBinder.setValidator(validator);
        dataBinder.validate();
        
        return dataBinder.getBindingResult();
    }
    
    
}
