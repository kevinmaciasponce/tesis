package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Rol;
import com.multiplos.cuentas.models.RolInt;
import com.multiplos.cuentas.pojo.empleado.RolInternoResponse;
import com.multiplos.cuentas.repository.RolInternoRepository;
import com.multiplos.cuentas.repository.RolRepository;
import com.multiplos.cuentas.repository.RolesInternosRepository;
import com.multiplos.cuentas.services.RolInternoService;
import com.multiplos.cuentas.services.RolService;

@Service
public class RolInternoServiceImpl implements RolInternoService {

	private RolInternoRepository repository;
	private RolesInternosRepository rolesRepositoy;

	@Autowired
	public RolInternoServiceImpl(RolInternoRepository repository,
			RolesInternosRepository rolesRepositoy) {
		this.repository = repository;
		this.rolesRepositoy=rolesRepositoy;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<RolInt> findAll() {
		return repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public RolInt findByIdRol(Long idRol) {
		RolInt rol = new RolInt();
		Optional<RolInt> r = repository.findById(idRol);
		rol = r.get();
		return rol;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RolInternoResponse> consultarRolesInternos() {
		 List<RolInternoResponse> listaRol = new ArrayList<>();
		 listaRol = repository.consultarRolesInternos();
	
		return listaRol;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<RolInternoResponse> consultarRolesInternosByEmpleado(String idCuenta) {
		 List<RolInternoResponse> listaRol = new ArrayList<>();
		 listaRol = repository.consultarRolesInternosByempleado(idCuenta);
	
		return listaRol;
	}
	
	@Override
	@Transactional(readOnly = true)
	public String  eliminarRolEmpleado(String idCuenta,Long rol) {
		
		rolesRepositoy.delete(null);
		return "";
	}
	//solo pruebas
	@Override
	@Transactional(readOnly = true)
	public RolInt consultaRol(String nombreRol) {
		RolInt rol = new RolInt();
		rol = repository.consultaRol(nombreRol);
		/*List<MenuOperacion> menu = new ArrayList<>();
		if(!rol.getMenuOperaciones().isEmpty()) {
			//List<MenuOperacion> menu = rol.getMenuOperaciones().stream().filter(m->!m.getMenu().getSubMenu().isEmpty()).collect(Collectors.toList());
			
			rol.getMenuOperaciones().stream().forEach(m->{
				if(m.getMenu().getIdMenu()==1 || !m.getMenu().getSubMenu().isEmpty()) {
					MenuOperacion menuOpe = new MenuOperacion();
					menuOpe = m;
					menu.add(menuOpe);
				}
			});
			//falta ajustar para las operaciones de cada submenu por rol
			rol.setMenuOperaciones(menu);
		}*/
		return rol;
	}

}
