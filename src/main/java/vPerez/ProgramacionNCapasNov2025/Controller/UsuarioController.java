package vPerez.ProgramacionNCapasNov2025.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Base64;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vPerez.ProgramacionNCapasNov2025.ML.Colonia;
import vPerez.ProgramacionNCapasNov2025.ML.Direccion;
import vPerez.ProgramacionNCapasNov2025.ML.ErrorCarga;
import vPerez.ProgramacionNCapasNov2025.ML.Estado;
import vPerez.ProgramacionNCapasNov2025.ML.Municipio;
import vPerez.ProgramacionNCapasNov2025.ML.Pais;
import vPerez.ProgramacionNCapasNov2025.ML.Result;
import vPerez.ProgramacionNCapasNov2025.ML.Rol;
import vPerez.ProgramacionNCapasNov2025.ML.Usuario;

@Controller // Sirve para mapear interacciones
@RequestMapping("Usuario")
public class UsuarioController {

//    @Autowired
//private ModelMapper modelMapper;
    public static final String url = "http://localhost:8081/api";

    @GetMapping
    public String getAll(Model model) {

        //para consumir el servicio
        RestTemplate restTemplate = new RestTemplate();

        //restTemplate devuelve un response entity(lo que viene del servidor)
        try {
            ResponseEntity<Result<List<Usuario>>> responseEntity = restTemplate.exchange(
                    url + "/usuarios",
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result<List<Usuario>>>() {
            });
            Result resultUsuario = responseEntity.getBody();

            ResponseEntity<Result<List<Rol>>> response = restTemplate.exchange(url + "/rol", HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<Result<List<Rol>>>() {
            });
            Result resultRol = response.getBody();
            model.addAttribute("Usuarios", resultUsuario.Object);
            model.addAttribute("UsuarioBusqueda", new Usuario());
            model.addAttribute("Roles", resultRol.Object);

        } catch (Exception ex) {
            System.out.println(ex.getCause());
        }

        return "Index";
    }

    @GetMapping("UsuarioDireccionForm")
    public String showAlumnoDireccion(Model model, RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();
        //REALIZAR PETICIÓN
        ResponseEntity<Result<List<Pais>>> responseEntity = restTemplate.exchange(url + "/pais",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<List<Pais>>>() {
        });
        //OBTENER EL CUERPO DE LA RESPUESTA
        Result resultPais = responseEntity.getBody();
        //MAndar al usuario el elemento necesario que obtuvimos de la respuesta
        model.addAttribute("Paises", resultPais.Object);

        ResponseEntity<Result<List<Rol>>> responseEntityRol = restTemplate.exchange(url + "/rol", HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<Result<List<Rol>>>() {
        });
        Result result = responseEntityRol.getBody();
////        Result resultPais = paisDaoImplementation.getAll();
//        Result result = rolJpaDAOImplementation.getAll();
//        Result resultPais = paisJpaDAOImplementation.getAll();
        model.addAttribute("Roles", result.Object);
        //MANDAR RESPUESTA A LA VISTA

        model.addAttribute("Usuario", new Usuario());
        return "UsuarioDireccionForm";
    }

    @PostMapping("add")
    public String addUsuarioDireccion(@ModelAttribute("Usuario") Usuario usuario, @ModelAttribute("imagenInput") MultipartFile imagenInput,
            Model model, RedirectAttributes redirectAttributes) throws IOException {
        if (imagenInput != null) {
            long tamañoImagen = imagenInput.getSize();
            if (tamañoImagen > 0) {
                String extension = imagenInput.getOriginalFilename().split("\\.")[1];
                if (extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg")) {
                    usuario.setImagen(Base64.getEncoder().encodeToString(imagenInput.getBytes()));
                }
            }
        }

        if (usuario.getIdUsuario() == 0 && usuario.direcciones.get(0).getIdDireccion() == 0) { // agregar usuario direccion

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Usuario> requestEntity = new HttpEntity<>(usuario);

            ResponseEntity<Boolean> response = restTemplate.exchange(url + "/usuarios", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Boolean>() {
            });
            Result resultUsuario = new Result();
            resultUsuario.Correct = response.getBody();


            model.addAttribute("Usuario", usuario);

            return "redirect:/Usuario";

        } else if (usuario.getIdUsuario() > 0 && usuario.direcciones == null) { // editar usuario

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Usuario> requestEntity = new HttpEntity<>(usuario);
            ResponseEntity<Result> response = restTemplate.exchange(url + "/usuarios/" + usuario.getIdUsuario(),
                    HttpMethod.PUT,
                    requestEntity,
                    new ParameterizedTypeReference<Result>() {
            });

            Result result = response.getBody();
            usuario.direcciones = new ArrayList<>();
            usuario.direcciones.add(new Direccion());

            redirectAttributes.addFlashAttribute("resultadoUpdate", result);
            return "redirect:/Usuario/detail/" + usuario.getIdUsuario();

        } else if ((usuario.getIdUsuario() > 0 && usuario.direcciones.get(0).getIdDireccion() > 0)) { // editar direccion
            
            RestTemplate restTemplate = new RestTemplate();

            HttpEntity<Direccion> httpEntity = new HttpEntity<>(usuario.direcciones.get(0));


            
                ResponseEntity<Result> response = restTemplate.exchange(url + "/direccion/" + usuario.direcciones.get(0).getIdDireccion(),
                        HttpMethod.PUT,
                        httpEntity,
                        new ParameterizedTypeReference<Result>() {
                });
                Result result = response.getBody();
                redirectAttributes.addFlashAttribute("resultadoUpdateDireccion", result);

            return "redirect:/Usuario/detail/" + usuario.getIdUsuario();

            
            
            
        } else if ((usuario.getIdUsuario() > 0 && usuario.direcciones.get(0).getIdDireccion() == 0)) { // agregar direccion
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Direccion> requestEntity = new HttpEntity<>(usuario.direcciones.get(0));

            ResponseEntity<Result> response = restTemplate.exchange(url + "/direccion/agregar/" + usuario.getIdUsuario(),
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Result>() {
            });
            return "redirect:/Usuario/detail/" + usuario.getIdUsuario();
        }

        return "redirect:/Usuario";
    }

    @GetMapping("delete/{idUsuario}")
    public String delete(@PathVariable("idUsuario") int idUsuario, RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();

//           ResponseEntity
        ResponseEntity<Result<List<Usuario>>> response = restTemplate.exchange(url + "/usuarios/" + idUsuario, HttpMethod.DELETE, HttpEntity.EMPTY, new ParameterizedTypeReference<Result<List<Usuario>>>() {
        });
        Result resultDelete = response.getBody();

        if (resultDelete.Correct) {
            resultDelete.Object = "El usuario " + idUsuario + " se eliminó correctamente";
        } else {
            resultDelete.Object = "El usuario  no se pudo eliminar";
        }
        redirectAttributes.addFlashAttribute("resultDelete", resultDelete);
        return "redirect:/Usuario";

    }

//    @GetMapping("softDelete/{idUsuario}/{estatus}")
//    @ResponseBody
//    public Result softDelete(@PathVariable("idUsuario") int idUsuario, @PathVariable("estatus") int estatus, RedirectAttributes redirectAttributes) {
//        Usuario usuario = new Usuario();
//        usuario.setIdUsuario(idUsuario);
//        usuario.setEstatus(estatus);
//        
//        RestTemplate restTemplate = new RestTemplate();
//        
//        ResponseEntity<Result<Usuario>> responseEntity = restTemplate.exchange(url+"usuarios", HttpMethod.PATCH, HttpEntity.EMPTY, new ParameterizedTypeReference<Result<Usuario>>() {
//        });
//        Result result = responseEntity.getBody();
//       
//
//        return result;
//    }
//
    @GetMapping("direccion/delete/{idDireccion}/{idUsuario}")
    public String deleteDireccion(@PathVariable("idDireccion") int idDireccion, @PathVariable("idUsuario") String idUsuario, RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Result<List<Direccion>>> response = restTemplate.exchange(url + "/direccion/" + idDireccion, HttpMethod.DELETE, HttpEntity.EMPTY, new ParameterizedTypeReference<Result<List<Direccion>>>() {
        });

        Result result = response.getBody();
        redirectAttributes.addFlashAttribute("DireccionBorrada", result.Correct);
        return "redirect:/Usuario/detail/" + idUsuario;//Lleva al endpoint
//        return "Index; --- LLeva a una plantilla
    }

    @GetMapping("detail/{idUsuario}")
    public String getUsuario(@PathVariable("idUsuario") int idUsuario, Model model, RedirectAttributes redirectAttributes) {

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Result<Usuario>> response = restTemplate.exchange(url + "/usuarios/" + idUsuario,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<Usuario>>() {
        });

        Result resultUsuario = response.getBody();

        ResponseEntity<Result<List<Rol>>> responseRol = restTemplate.exchange(url + "/rol",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<List<Rol>>>() {
        });

        Result resultRol = responseRol.getBody();

        ResponseEntity<Result<List<Pais>>> reponsePais = restTemplate.exchange(url + "/pais",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<List<Pais>>>() {
        });
        Result resultPais = reponsePais.getBody();

        model.addAttribute("Paises", resultPais.Object);
        model.addAttribute("Roles", resultRol.Object);//Agregado 12/12/2025
        model.addAttribute("Usuario", resultUsuario.Object);

        return "detalleUsuario";
    }

    @GetMapping("direccionForm/{idUsuario}")
    @ResponseBody
    public Result getDireccion(@PathVariable("idUsuario") int idUsuario, Model model, RedirectAttributes redirectAttributes) {

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Result<Usuario>> response = restTemplate.exchange(url + "/usuarios/" + idUsuario,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<Usuario>>() {
        });
        Result result = response.getBody();
        model.addAttribute("UsuarioD", result.Object);
//        model.addAttribute("Paises", resultPais.Object);

        return result;
    }

    @GetMapping("getEstadoByPais/{idPais}")
    @ResponseBody
    public Result getEstadoByPais(@PathVariable int idPais) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Result<List<Estado>>> responseEntity = restTemplate.exchange(url + "/estado/pais/" + idPais, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<Result<List<Estado>>>() {
        });

        Result result = responseEntity.getBody();

        return result;
    }

    @GetMapping("getMunicipioByEstado/{idEstado}")
    @ResponseBody
    public Result getMunicipioByEstado(@PathVariable("idEstado") int idEstado) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Result<List<Municipio>>> response = restTemplate.exchange(url + "/municipio/estado/" + idEstado, HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<Result<List<Municipio>>>() {
        });

        Result result = response.getBody();
//        Result result = municipioDaoImplementation.getByEstado(idEstado);
//        Result result = municipioJpaDAOImplementation.getByEstado(idEstado);
        return result;
    }

    @GetMapping("getColoniaByMunicipio/{idMunicipio}")
    @ResponseBody
    public Result getColoniaByMunicipio(@PathVariable("idMunicipio") int idMunicipio) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Result<List<Colonia>>> response = restTemplate.exchange(url + "/colonia/municipio/" + idMunicipio,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result<List<Colonia>>>() {
        });

        Result result = response.getBody();
        return result;
    }

    //Carga la pagina de carga masiva
    @GetMapping("CargaMasiva")
    public String CargaMasiva() {
        return "CargaMasiva";
    }

    @PostMapping("/CargaMasiva")
    public String CargaMasiva(@ModelAttribute MultipartFile archivo, Model model) throws IOException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);//Indicamos que el tipo de archivo a pasar es un multipart file
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();//Para agrupar las partes del cuerpo de la petición
            // El archivo debe enviarse como un recurso
            body.add("archivo", archivo.getResource());//usamos getResource para que spring maneje correctamente el envio del archivo

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity(body, headers);
            ResponseEntity<Result> response = restTemplate.exchange(url + "/usuarios/CargaMasiva?archivo",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Result>() {
            });

            Result result = response.getBody();
            result.Object = new ArrayList<>();
            if (!result.Objects.isEmpty()) {
//            model.addAttribute("Errores", errores);//Mandando errores
                model.addAttribute("isError", true);
//
            } else {
                model.addAttribute("isError", false);
//            sesion.setAttribute("archivoCargaMasiva", rutaAbsoluta);//Añadiendo atributos a la ruta
            }

            model.addAttribute("token", result.Objects.get(0));

            model.addAttribute("Errores", result.Object);
//            model.addAttribute("validado", true);

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
//        Result result = response.getBody();
        return "CargaMasiva";
    }

    @PostMapping("/CargaMasiva/Procesar")
    public String ProcesarArchivo(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String urlProcesar = url + "/usuarios/CargaMasiva/Procesar/" + token;
        try {
            ResponseEntity<Result> response = restTemplate.exchange(urlProcesar,
                    HttpMethod.POST,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<Result>() {
            });
            Result result = response.getBody();
            model.addAttribute("respuestaCarga", result.Object);
            
                redirectAttributes.addFlashAttribute("mensaje", result.Object);

            

        } catch (HttpClientErrorException httpEx) {
            ObjectMapper mapper = new ObjectMapper();
            Result resultError = mapper.readValue(httpEx.getResponseBodyAsString(), Result.class);
            redirectAttributes.addFlashAttribute("mensaje", resultError.Object);
            return "redirect:/Usuario/CargaMasiva";
        }catch (Exception ex) {
            System.out.println(ex.getCause());
            System.out.println(ex.getLocalizedMessage());
        } 
        return "redirect:/Usuario";
    }

    @PostMapping("/Search")
    public String buscarUsuarios(@ModelAttribute("Usuario") Usuario usuario, Model model) {

        model.addAttribute("UsuarioBusqueda", new Usuario());//creando usuario(vacio) para que pueda mandarse la busqueda
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Usuario> requestEntity = new HttpEntity<>(usuario, headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Result<Usuario>> response = restTemplate.exchange(url + "/usuarios/busqueda",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Result<Usuario>>() {
            });

            Result result = response.getBody();
            model.addAttribute("Usuarios", result.Objects);
            model.addAttribute("usuariosEstatus", result.Objects);//recargar el usuario
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return "Index";

    }

    @PostMapping("/ImagenPerfil")
    public String actualizarImagen(@ModelAttribute("Usuario") Usuario usuario, @ModelAttribute("imagenInput") MultipartFile imagenInput) throws IOException {
        if (imagenInput != null) {
            long tamañoImagen = imagenInput.getSize();
            if (tamañoImagen > 0) {
                String extension = imagenInput.getOriginalFilename().split("\\.")[1];
                if (extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg")) {
                    usuario.setImagen(Base64.getEncoder().encodeToString(imagenInput.getBytes()));
                }
            }
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Usuario> httpEntity = new HttpEntity<>(usuario);

        ResponseEntity<Result> response = restTemplate.exchange(url + "/usuarios/Imagen/" + usuario.getIdUsuario(),
                HttpMethod.POST,
                httpEntity,
                new ParameterizedTypeReference<Result>() {
        });

        Result result = response.getBody();

        return "redirect:/Usuario/detail/" + usuario.getIdUsuario();
    }

}
