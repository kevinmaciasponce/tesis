package com.multiplos.cuentas.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Rol;
import com.multiplos.cuentas.repository.RolRepository;
import com.multiplos.cuentas.services.RolService;

@Service
public class RolServiceImpl implements RolService {

	private RolRepository repository;
	
	@Autowired
	public RolServiceImpl(RolRepository repository) {
		this.repository = repository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Rol> findAll() {
		return repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Rol findByIdRol(Long idRol) {
		Rol rol = new Rol();
		Optional<Rol> r = repository.findById(idRol);
		rol = r.get();
		return rol;
	}

	//solo pruebas
	@Override
	@Transactional(readOnly = true)
	public Rol consultaRol(String nombreRol) {
		Rol rol = new Rol();
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
