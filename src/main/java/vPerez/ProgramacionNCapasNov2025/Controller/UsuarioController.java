package vPerez.ProgramacionNCapasNov2025.Controller;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
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
        if(imagenInput != null){
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
            
            ResponseEntity<Boolean> response = restTemplate.exchange(url+"/usuarios", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Boolean>() {
            });
             Result resultUsuario = new Result();
             resultUsuario.Correct = response.getBody();
            
//             ResponseEntity<Result<List<Rol>>> responseRol = restTemplate.exchange(url+"rol", HttpMethod.GET, HttpEntity.EMPTY, new ParameterizedTypeReference<Result<List<Rol>>>(){});
//
//             Result resultRol= responseRol.getBody();
//
//
//                model.addAttribute("Roles", resultRol.Objects);
            model.addAttribute("Usuario", usuario);
//                if (result.Correct) {
//                    redirectAttributes.addFlashAttribute("ErroresC", result.Correct);
//                } else {
//                    redirectAttributes.addFlashAttribute("ErroresC", result.Correct);
//                }
            return "redirect:/Usuario";

            //AGREGADO RECIENTEMENTE SOLO EL IF
//                ModelMapper modelMapper = new ModelMapper();
//
//                vPerez.ProgramacionNCapasNov2025.JPA.Usuario usuarioJpa = modelMapper.map(usuario, vPerez.ProgramacionNCapasNov2025.JPA.Usuario.class);
//
//                Result result = usuarioJpaDAOImplementation.add(usuarioJpa);
//                if (!result.Correct) {
//                    model.addAttribute("ErroresC", "Sucedio un error.");
//                    return "UsuarioDireccionForm";
//                }
//                redirectAttributes.addFlashAttribute("ResultAgregar", "El usuario se agregó con exito"); // Agregado
        } else if (usuario.getIdUsuario() > 0 && usuario.direcciones == null) { // editar usuario

            
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Usuario> requestEntity = new  HttpEntity<>(usuario);
            ResponseEntity<Result> response = restTemplate.exchange(url+"/usuarios/"+usuario.getIdUsuario(), 
                    HttpMethod.PUT, 
                    requestEntity, 
                    new ParameterizedTypeReference<Result>(){});
            
            Result result = response.getBody();
//            usuario.setPassword(usuario.getPassword());
            usuario.direcciones = new ArrayList<>();
            usuario.direcciones.add(new Direccion());
            
//            vPerez.ProgramacionNCapasNov2025.JPA.Usuario usuarioEntidad = modelMapper.map(usuario, vPerez.ProgramacionNCapasNov2025.JPA.Usuario.class);
//
//            Result resultUpdateUsuario = usuarioJpaDAOImplementation.update(usuarioEntidad);

//            if (resultUpdateUsuario.Correct) {
//                resultUpdateUsuario.Object = "Exito al actualizar";
//            } else {
//                resultUpdateUsuario.Object = "Error al actualizar";
//            }
//            redirectAttributes.addFlashAttribute("resultadoUpdate", resultUpdateUsuario);
//            return "detalleUsuario";
            return "redirect:/Usuario/detail/" + usuario.getIdUsuario();

        } else if ((usuario.getIdUsuario() > 0 && usuario.direcciones.get(0).getIdDireccion() > 0)) { // editar direccion
             RestTemplate restTemplate = new RestTemplate();
             
             HttpEntity<Direccion> httpEntity = new HttpEntity<>(usuario.direcciones.get(0));
             
             ResponseEntity<Result> response = restTemplate.exchange(url+"/direccion/"+usuario.direcciones.get(0).getIdDireccion(),
                     HttpMethod.PUT, 
                     httpEntity, 
                     new ParameterizedTypeReference<Result>(){});
             Result result = response.getBody();
//            Result resultUpdateDireccion = direccionJpaDAOImplementation.update(usuarioJPA.direcciones.get(0));
            return "redirect:/Usuario/detail/" + usuario.getIdUsuario();

        } else if ((usuario.getIdUsuario() > 0 && usuario.direcciones.get(0).getIdDireccion() == 0)) { // agregar direccion
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Direccion> requestEntity = new HttpEntity<>(usuario.direcciones.get(0));
            
            ResponseEntity<Result> response = restTemplate.exchange(url+"/direccion/agregar/"+usuario.getIdUsuario(), 
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
//
//  
//
//    //Carga la pagina de carga masiva
//    @GetMapping("CargaMasiva")
//    public String CargaMasiva() {
//        return "CargaMasiva";
//    }
//
//    @PostMapping("/CargaMasiva")
//    public String CargaMasiva(@ModelAttribute MultipartFile archivo, Model model, HttpSession sesion) throws IOException {
//
//        //CARGA DE ARCHIVOS
//        //divide el nombre del archivo en 2 partes, una es el nombre y la otra es despues del punto(extension) 
//        //Para revisar que sea la extensión solicitada
//        String extension = archivo.getOriginalFilename().split("\\.")[1];
//
//        //Obteniendo la ruta base la que viene del disco del sistema
//        String ruta = System.getProperty("user.dir");
//
//        // Ruta desde el proyecto
//        String rutaArchivo = "src\\main\\resources\\archivos";
//
//        //Obteniendo la fecha para que sirva de id
//        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//
//        //Esta es la ruta absoluta del archivo(donde se va a guardar en el proyecto)
//        String rutaAbsoluta = ruta + "/" + rutaArchivo + "/" + fecha + archivo.getOriginalFilename();
//
//        archivo.transferTo(new File(rutaAbsoluta));
////        Files.copy(archivo.getInputStream(), Paths.get(rutaAbsoluta));
//        List<Usuario> usuarios = new ArrayList<>();
//
//        //¿Cual archivo debe leer?
//        if (extension.equals("txt")) {
//            usuarios = LeerArchivo(new File(rutaAbsoluta));
//        } else {
//            usuarios = LeerArchivoExcel(new File(rutaAbsoluta));
//        }
//
//        //validacion de archivo
//        List<ErrorCarga> errores = validarDatos(usuarios);
//        model.addAttribute("Errores", errores);
//        if (!errores.isEmpty()) {
//            model.addAttribute("Errores", errores);//Mandando errores
//            model.addAttribute("isError", true);
//
//        } else {
//            model.addAttribute("isError", false);
//            sesion.setAttribute("archivoCargaMasiva", rutaAbsoluta);//Añadiendo atributos a la ruta
//        }
//
//        return "CargaMasiva";
//    }
//
//    public List<Usuario> LeerArchivo(File archivo) {//
//        List<Usuario> usuarios = new ArrayList<>();
//        try (
//                //                InputStream inputStream = archivo.getInputStream(); //inpuStream lee los bytes de un archivo, en este caso el archivo que le estamos indicando
//                //Lee texto desde un archivo de entrada(nuestro input stream):
//                //                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
//
//                BufferedReader bufferedReader = new BufferedReader(new FileReader(archivo))) {
//
//            bufferedReader.readLine(); //solo lee el encabezado que añadimos al txt
//            String linea;
//            while ((linea = bufferedReader.readLine()) != null) {
//                //datos representa cada columna(campo)
//                String[] datos = linea.split("\\|");
//
//                Usuario usuario = new Usuario();
//
//                usuario.setNombre(datos[0].trim());
//                usuario.setApellidoPaterno(datos[1].trim());
//                usuario.setApellidoMaterno(datos[2].trim());
//                usuario.setEmail(datos[3].trim());
//                usuario.setPassword(datos[4].trim());
////                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
//                usuario.setFechaNacimiento(formato.parse(datos[5]));
//                usuario.rol = new Rol();
//                usuario.rol.setIdRol(Integer.valueOf(datos[6].trim())); // le quité lso espacios para que fuese un formato que pueda transformar
//                usuario.setSexo(datos[7].trim());
//                usuario.setTelefono(datos[8].trim());
//                usuario.setCelular(datos[9].trim());
//                usuario.setCurp(datos[10].trim());
//                usuario.direcciones = new ArrayList<>();
//                usuario.direcciones.add(new Direccion());
//                usuario.direcciones.get(0).setCalle(datos[11].toString().trim());
//                usuario.direcciones.get(0).setNumeroInterior(datos[12].toString().trim());
//                usuario.direcciones.get(0).setNumeroExterior(datos[13].toString().trim());
//                usuario.direcciones.get(0).colonia = new Colonia();
//                usuario.direcciones.get(0).colonia.setIdColonia(Integer.valueOf(datos[14].trim()));
//
//                usuarios.add(usuario);
//
//                System.out.println("leyendo datos: " + linea);
//            }
//
//        } catch (Exception ex) {
//            System.out.println(ex.getLocalizedMessage());
//        }
//        return usuarios;
//    }
//
//    public List<Usuario> LeerArchivoExcel(File archivo) {
//        List<Usuario> usuarios = new ArrayList<>();
////Cambió de archivo.getInputStream() a archivo
//        try (XSSFWorkbook workbook = new XSSFWorkbook(archivo)) {
//            XSSFSheet sheet = workbook.getSheetAt(0);
//            for (Row row : sheet) {
////                if (row.getRowNum() == 0) {
////                    System.out.println("Encabezados");
////                } else {
//
//                    Usuario usuario = new Usuario();
//                    usuario.setNombre(row.getCell(0).toString());
//                    usuario.setApellidoPaterno(row.getCell(1).toString());
//                    usuario.setApellidoMaterno(row.getCell(2).toString());
//                    usuario.setEmail(row.getCell(3).toString());
//                    usuario.setPassword(row.getCell(4).toString());
//                    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
//                    usuario.setFechaNacimiento(formatoFecha.parse(row.getCell(5).toString()));
//                    usuario.rol = new Rol();
//                    int IdRol = Integer.parseInt(row.getCell(6).toString());
//                    usuario.rol.setIdRol(IdRol);
//                    //  usuario.rol.setIdRol((Float.valueOf(row.getCell(6).toString().trim())).intValue());
//                    usuario.setSexo(row.getCell(7).toString());  //datos nulos
//                    usuario.setTelefono(row.getCell(8).toString());//
//                    usuario.setCelular(row.getCell(9).toString());
//                    usuario.setCurp(row.getCell(10).toString());
//                    usuario.direcciones = new ArrayList<>();
//                    usuario.direcciones.add(new Direccion());
//                    usuario.direcciones.get(0).setCalle(row.getCell(11).toString());
//                    usuario.direcciones.get(0).setNumeroInterior(row.getCell(12).toString());
//                    usuario.direcciones.get(0).setNumeroExterior(row.getCell(13).toString());
//                    usuario.direcciones.get(0).colonia = new Colonia();
//                    usuario.direcciones.get(0).colonia.setIdColonia(Integer.parseInt(row.getCell(14).toString()));
//                    usuarios.add(usuario);
////                }
//
//            }
//
//        } catch (Exception ex) {
//            System.out.println(ex.getCause() + " :" + ex.getLocalizedMessage());
//        }
//        return usuarios;
//    }
//
//    //Lista de errores (contendrá todos los atributos de la clase)
//    public List<ErrorCarga> validarDatos(List<Usuario> usuarios) {
//        List<ErrorCarga> erroresCarga = new ArrayList<>();//Se almacenarán todos los errores
//        int lineaError = 0;
//
//        //Iterando sobre la lista que le pasamos al metodo como argumento
//        for (Usuario usuario : usuarios) {
//            List<ObjectError> errors = new ArrayList();
//            lineaError++;
//            BindingResult bindingResultUsuario = ValidationService.validateObjects(usuario);//validando cada usuario
//            if (bindingResultUsuario.hasErrors()) {
//                errors.addAll(bindingResultUsuario.getAllErrors());
//            }
//            if (usuario.direcciones.get(0) != null) {
//                BindingResult bindingDireccion = ValidationService.validateObjects(usuario.direcciones.get(0));
//                if (bindingDireccion.hasErrors()) {
//                    errors.addAll(bindingDireccion.getAllErrors());
//                }
//            }
////            List<ObjectError> errores = bindingResult.getAllErrors(); //Obteniendo los errores y guardandolos
//
//            for (ObjectError error : errors) {
//                FieldError fieldError = (FieldError) error;//obteniendo cada error especifico en cada campo(field)
//                ErrorCarga errorCarga = new ErrorCarga();//Instancia de DTO ErrorCarga
//                errorCarga.linea = lineaError;
//                errorCarga.campo = fieldError.getField();//obtiendo el campo del error
//                errorCarga.descripcion = fieldError.getDefaultMessage();//guardando mensaje de error
//                erroresCarga.add(errorCarga); //Guardando cada error en la lista de errores
//            }
//        }
//
////        model.addAttribute("Errores",erroresCarga);
//        return erroresCarga;
//    }
//
//    @GetMapping("/CargaMasiva/Procesar")
//    public String ProcesarArchivo(HttpSession sesion, Model model) {
//        //Obteniendo ruta del archivo que se registró en metodo CargaMasiva()
//        String ruta = sesion.getAttribute("archivoCargaMasiva").toString();
//        String extensionArchivo = new File(ruta).getName().split("\\.")[1];
////        Result result;
//
//        if (extensionArchivo.equals("txt")) {
//            List<Usuario> usuarios = LeerArchivo(new File(ruta));
////            usuarioDaoImplementation.AddMany(usuarios);
//            ModelMapper modelMapper = new ModelMapper();
//            List<vPerez.ProgramacionNCapasNov2025.JPA.Usuario> usuariosJPA = new ArrayList<>();
//            for (Usuario usuario : usuarios) {
//                vPerez.ProgramacionNCapasNov2025.JPA.Usuario usuarioJPA = modelMapper.map(usuario, vPerez.ProgramacionNCapasNov2025.JPA.Usuario.class);
//                usuariosJPA.add(usuarioJPA);
//            }
////            usuarioDaoImplementation.AddMany(usuarios);
//            Result resultCargaMasiva = usuarioJpaDAOImplementation.addMany(usuariosJPA);
//
//        } else {
//            //Guardando usuarios de la lista de usuarios creada con el metodo leer archivo
//            List<Usuario> usuarios = LeerArchivoExcel(new File(ruta));
//            ModelMapper modelMapper = new ModelMapper();
//            List<vPerez.ProgramacionNCapasNov2025.JPA.Usuario> usuariosJPA = new ArrayList<>();
//            for (Usuario usuario : usuarios) {
//                vPerez.ProgramacionNCapasNov2025.JPA.Usuario usuarioJPA = modelMapper.map(usuario, vPerez.ProgramacionNCapasNov2025.JPA.Usuario.class);
//                usuariosJPA.add(usuarioJPA);
//            }
////            usuarioDaoImplementation.AddMany(usuarios);
//            usuarioJpaDAOImplementation.addMany(usuariosJPA);
//
//        }
//        sesion.removeAttribute("archivoCargaMasiva");
////        new File(ruta).delete();//Ya cuando se terminaron las operaciones con el archivo, se elimina de la carpeta
//
//        return "redirect:/Usuario";
//    }
//

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

}
