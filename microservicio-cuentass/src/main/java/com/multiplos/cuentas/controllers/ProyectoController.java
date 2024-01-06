package com.multiplos.cuentas.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.multiplos.cuentas.models.BadResponse;
import com.multiplos.cuentas.models.HistorialDeProyecto;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.models.Response;
import com.multiplos.cuentas.models.TipoEstado;
import com.multiplos.cuentas.pojo.proyecto.CuentaHabilResponse;
import com.multiplos.cuentas.pojo.proyecto.ProyectoRequest;
import com.multiplos.cuentas.pojo.proyecto.ProyectoResponse;
import com.multiplos.cuentas.pojo.proyecto.proyectosResponse;
import com.multiplos.cuentas.pojo.proyecto.filter.FilterEmpresa;
import com.multiplos.cuentas.services.ProyectoCuentaService;
import com.multiplos.cuentas.services.ProyectoService;
import com.multiplos.cuentas.utils.ServicesUtils;

@RestController
@RequestMapping("${route.service.contextPath}")
public class ProyectoController {
	
	private ProyectoService proyectoService;
	private ProyectoCuentaService proyectoCuentaService;
	private ServicesUtils utilService;
	private static final Logger LOG = LoggerFactory.getLogger(ProyectoController.class);

	@Autowired
    public ProyectoController(ProyectoService proyectoService,ProyectoCuentaService proyectoCuentaService,ServicesUtils utilService) {
        this.proyectoService = proyectoService;
        this.proyectoCuentaService = proyectoCuentaService;
        this.utilService = utilService;
    }
    
    @PostMapping(value = "/proyectos")
    public ResponseEntity<?> consultaProyectos(@RequestParam int partitions,@RequestParam int group)throws Exception {
    	List<ProyectoResponse> proyectos = new ArrayList<>();
    	
    	try {
    	proyectos = proyectoService.consultaProyectos(partitions,group);
    	}catch(IllegalArgumentException i) {
    		return ResponseEntity.internalServerError().body(new BadResponse(i.getMessage()));
    	}
    	if(proyectos.isEmpty()) {
    		return ResponseEntity.noContent().build();
    	}
        return ResponseEntity.ok(proyectos);
    }
    
    @PostMapping(value = "/proyectos/cuenta-habilitada")
    public ResponseEntity<?> consultaCuentaHabilitada(@RequestBody ProyectoRequest proyecto) {
    	CuentaHabilResponse cuenta = new CuentaHabilResponse();
    	try {
			cuenta = proyectoCuentaService.consultaCuentaPorProyecto(proyecto.getCodigoProyecto());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new Response(e.getMessage()));
		}
    	return ResponseEntity.ok(cuenta);
    }
    
    //filtro para consultas en las ventanas de inversionistas
    @GetMapping("/proyectos/filter")
    public ResponseEntity<List<FilterEmpresa>> filterEmpresas() {
		List<FilterEmpresa> listSolicitudResponse = new ArrayList<>();
		listSolicitudResponse = proyectoService.filterEmpresas();
		if(listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listSolicitudResponse);
    }
    
    @PostMapping("/proyectos/filter/estado")
    public ResponseEntity<List<FilterEmpresa>> filterEmpresasxEstado(@RequestParam String estado) {
		List<FilterEmpresa> listSolicitudResponse = new ArrayList<>();
		TipoEstado t = new TipoEstado();
		t.setIdEstado(estado);
		listSolicitudResponse = proyectoService.filterEmpresasxEstado(t);
		if(listSolicitudResponse.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
        return ResponseEntity.ok(listSolicitudResponse);
    }
    
    @PostMapping("/proyectos/update")
    public ResponseEntity<?> save(@Valid @RequestBody HistorialDeProyecto history, BindingResult result) {
    	Map<String, Object> mensaje = new HashMap<>();
    	if(result.hasErrors()) {
			return utilService.validar(result);
		}
		String Response = proyectoService.updateProyecto(history);
		mensaje.put("mensaje", Response);
		return ResponseEntity.ok(mensaje);
    }
    
    
	//////////////CONSULTAS/////////////////
	//@PostMapping("/loquesea2")

    
//    @GetMapping("/proyectos/consulta/firmaDeContrato")
//    public ResponseEntity<List<proyectosResponse>> firmaDeContrato() {
//    	List<proyectosResponse> proyectos = new ArrayList<>();
//    	proyectos = proyectoService.consultasPorEstado("FC");
//    	if(proyectos.isEmpty()) {
//    		return ResponseEntity.noContent().build();
//    	}
//        return ResponseEntity.ok(proyectos);
//    }

//    @GetMapping("/proyectos/consulta/vigente")
//    public ResponseEntity<List<proyectosResponse>> vigente() {
//    	List<proyectosResponse> proyectos = new ArrayList<>();
//    	proyectos = proyectoService.consultasPorEstado("EXI");
//    	if(proyectos.isEmpty()) {
//    		return ResponseEntity.noContent().build();
//    	}
//        return ResponseEntity.ok(proyectos);
//    }


}
