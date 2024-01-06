package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.CuentaInterno;
import com.multiplos.cuentas.models.PersonaInterna;
import com.multiplos.cuentas.models.RolInt;
import com.multiplos.cuentas.models.RolesIntKeys;
import com.multiplos.cuentas.models.RolesInternos;
import com.multiplos.cuentas.pojo.empleado.EmpleadoCuentaRequest;
import com.multiplos.cuentas.pojo.empleado.EmpleadoRequest;
import com.multiplos.cuentas.pojo.empleado.EmpleadoResponse;
import com.multiplos.cuentas.pojo.empleado.PersonaInternaResponse;
import com.multiplos.cuentas.pojo.empleado.PersonalDetalleResponse;
import com.multiplos.cuentas.pojo.persona.PersonaRequest;
import com.multiplos.cuentas.repository.PersonaInternaRepository;
import com.multiplos.cuentas.repository.RolInternoRepository;
import com.multiplos.cuentas.services.PersonaInternaService;

@Service
public class PersonaInternaServiceImpl implements PersonaInternaService {

	private static final Logger LOG = LoggerFactory.getLogger(PersonaInternaServiceImpl.class);
	private PersonaInternaRepository empleadoRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private RolInternoRepository rolIntRepo;
	private static final int idJefeAnalistaVentas = 2;

	@Autowired
	public PersonaInternaServiceImpl(PersonaInternaRepository empleadoRepository,
			@Lazy BCryptPasswordEncoder bCryptPasswordEncoder, RolInternoRepository rolIntRepo) {
		this.empleadoRepository = empleadoRepository;
		this.rolIntRepo = rolIntRepo;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public PersonaInterna findById(String id) throws Exception {
		Optional<PersonaInterna> empleado;
		empleado = this.empleadoRepository.findById(id);
		if (empleado.isEmpty()) {
			throw new CuentaException("No existe empleado con identificacion: " + id);
		}
		PersonaInterna empleadoRes = empleado.get();
		return empleadoRes;
	}

	public CuentaInterno findCuentabyUser(String user) throws Exception {
		CuentaInterno cuenta = null;
		cuenta = this.empleadoRepository.consultacuentaEmpleado(user);
		if (cuenta == null) {
			throw new CuentaException("No existe cuenta con identificacion: " + user);
		}

		return cuenta;
	}

	public RolInt findRolId(Long id) throws Exception {
		Optional<RolInt> rol;
		rol = this.rolIntRepo.findById(id);
		if (rol.isEmpty()) {
			throw new CuentaException("No existe empleado con identificacion: " + id);
		}
		RolInt RolRes = rol.get();
		return RolRes;
	}

	public String encriptaClave(String clave) {
		return bCryptPasswordEncoder.encode(clave);
	}

	public String save(PersonaInterna empleado) throws Exception {
		try {
			this.empleadoRepository.save(empleado);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return "Guardado correctacmente";
	}

	public String saveCuenta(CuentaInterno cuenta) throws Exception {
		try {
			LOG.warn("r");
			PersonaInterna empleado = this.findById(cuenta.getPersonalInterno().getIdPersInterno());
			LOG.warn(""+empleado.getApellidos());
			empleado.setCuenta(cuenta);
			LOG.warn("t");
			this.empleadoRepository.save(empleado);
			LOG.warn("u");
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return "Guardado correctacmente";
	}

	public String isMail(String email) {
		String mail = this.empleadoRepository.isMail(email);
		if (mail == null) {
			mail = "";
		}
		return mail;
	}

	@Override
	@Transactional
	public String putEmpleado(EmpleadoRequest empleado) throws Exception {
		// empleado.getCuenta().setIdCuenta("@mtp"+"_"+empleado.getIniciales()+"_"+empleado.getIdPersInterno().substring(6));
		// empleado.getCuenta().setPersonalInterno(empleado);
		String response;
		PersonaInterna persona;

		Optional<PersonaInterna> aux= this.empleadoRepository.findById(empleado.getIdPersInterno());

		if (aux.isEmpty()) {
			persona = new PersonaInterna();
			persona.setIdPersInterno(empleado.getIdPersInterno());
			persona.setUsuarioCreacion(empleado.getUsuarioCreacion());
			response = "Empleado agregado";
		} else {
			persona= aux.get();
			persona.setUsuarioModificacion(empleado.getUsuarioCreacion());
			response = "Empleado actualizado";
		}
		persona.setApellidos(empleado.getApellidos());
		persona.setNombres(empleado.getNombres());
		persona.setIniciales(empleado.getIniciales());

		try {
			this.save(persona);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		return response;
	}

	@Override
	public Object putCuentaEmpleado(EmpleadoCuentaRequest rqst) throws Exception {
		String response;
		CuentaInterno cuenta = null;
		String isMail = this.isMail(rqst.getEmail());
		PersonaInterna empleado = this.findById(rqst.getPersonalInterno());
		if (rqst.getIdCuenta() != null) {	
			cuenta = this.findCuentabyUser(rqst.getIdCuenta());
			if (!isMail.equals(cuenta.getEmail()) && !isMail.equals("")) {
				throw new CuentaException("El correo ya se encuentra registrado");
			}
			if (!rqst.getClave().equals("Multiplo.1")) {
				cuenta.setClave(rqst.getClave());
				cuenta.encriptaClave();
				LOG.warn("clave  actualizada");
			}
			response = "Cuenta Actualizada";
		} else {
		
			if (empleado.getCuenta() != null) {
				throw new CuentaException("El empleado ya tiene una cuenta");
			}
			if (!isMail.equals("")) {
				throw new CuentaException("El correo ya se encuentra registrado");
			}
			cuenta = new CuentaInterno();
			cuenta.setIdCuenta("@MULT" + "_" + empleado.getIniciales().toUpperCase() + "_"
					+ empleado.getIdPersInterno().substring(6).toUpperCase());
			cuenta.setClave(rqst.getClave());
			cuenta.setPersonalInterno(empleado);
			
			response = "Cuenta agregada";
		}
		cuenta.setCuentaActiva(rqst.getCuentaActiva());
		cuenta.setEmail(rqst.getEmail());
		cuenta.setUsuarioCreacion(rqst.getUsuarioCreacion());
		empleado.setCuenta(cuenta);

//		return  cuenta;
		try {
			this.saveCuenta(cuenta);	
		} catch (Exception e) {
			//return  cuenta;
			throw new Exception(e.getMessage());
		}
		return response;
	}

	@Override
	public String putCuentaEmpleadoRoles(RolesIntKeys key, String userCompose) throws Exception {
		CuentaInterno cuenta = findCuentabyUser(key.getCuentaInternaKey());
		CuentaInterno cuentaCompose = findCuentabyUser(userCompose);
		RolInt rol = this.findRolId(key.getRolKey());
		RolesInternos rolCuenta = new RolesInternos();
		rolCuenta.setId(key);
		rolCuenta.setCuentaInterna(cuenta);
		rolCuenta.setRol(rol);
		rolCuenta.setUsuarioCreacion(cuentaCompose);
		cuenta.getRoles().add(rolCuenta);
		try {
			this.saveCuenta(cuenta);
		} catch (CuentaException e) {
			throw new CuentaException(e.getMessage());
		}
		return "Rol agregada correctamente a la cuenta: " + cuenta.getIdCuenta();
	}

	@Override
	public List<PersonaInternaResponse> getEmpleado(PersonaRequest filter) {
		List<PersonaInternaResponse> response = null;
		
		response = this.empleadoRepository.findByFilter(filter.getIdentificacion());
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmpleadoResponse> findByAnalistasVentas() {
		List<EmpleadoResponse> listResponse = new ArrayList<>();
		List<PersonaInterna> listEmp = new ArrayList<>();

		listEmp = empleadoRepository.findByAnalistasOperacionesPorJefe((long) idJefeAnalistaVentas);
		for (PersonaInterna emp : listEmp) {
			EmpleadoResponse response = new EmpleadoResponse();

			response.setIdEmpleado(emp.getIdPersInterno());
			response.setIniciales(emp.getIniciales());
			response.setNombres(emp.getNombres() + " " + emp.getApellidos());
			response.setUsuario(emp.getCuenta().getIdCuenta());
			response.setIdJefe(emp.getIdJefe());
			listResponse.add(response);

		}
		listEmp.clear();
		return listResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public PersonalDetalleResponse consultaEmpleado(String usuario) {
		PersonalDetalleResponse pers = new PersonalDetalleResponse();
		PersonaInterna emp = new PersonaInterna();
		emp = empleadoRepository.consultaEmpleado(usuario);
		if (emp != null) {
			pers.setNombres(emp.getNombres());
			pers.setApellidos(emp.getApellidos());
			pers.setEmail(emp.getCuenta().getEmail());
			pers.setUsuario(emp.getCuenta().getIdCuenta());
			pers.setIdJefe(emp.getIdJefe());
//			pers.setIdRol(emp.getCuenta().getRol().getIdRol());
		} else {
			pers = null;
		}
		return pers;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PersonalDetalleResponse> consultaEmpleadosPorRol(Long idRol) {
		List<PersonalDetalleResponse> listPers = new ArrayList<>();
		List<PersonaInterna> listPersonaInterna = new ArrayList<>();

//		listPersonaInterna = empleadoRepository.consultaEmpleadosPorRol(idRol);
		if (!listPersonaInterna.isEmpty()) {
			listPersonaInterna.forEach(p -> {
				PersonalDetalleResponse persResponse = new PersonalDetalleResponse();
				persResponse.setNombres(p.getNombres());
				persResponse.setApellidos(p.getApellidos());
				persResponse.setUsuario(p.getCuenta().getIdCuenta());
				persResponse.setEmail(p.getCuenta().getEmail());
				persResponse.setIdJefe(p.getIdJefe());
//				persResponse.setIdRol(p.getCuenta().getRol().getIdRol());
				listPers.add(persResponse);
			});

			listPersonaInterna.clear();
		} else {
			listPers.clear();
		}
		return listPers;
	}

}
