package com.multiplos.cuentas.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.CuentaInterno;
import com.multiplos.cuentas.models.RolesInternos;
import com.multiplos.cuentas.repository.CuentaInternoRepository;
import com.multiplos.cuentas.repository.RolesInternosRepository;
import com.multiplos.cuentas.services.CuentaInternoService;

@Service
public class CuentaInternoServiceImpl implements CuentaInternoService{

	private CuentaInternoRepository repository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private RolesInternosRepository rolesRepository;
    @Autowired
    public CuentaInternoServiceImpl(CuentaInternoRepository repository,
    		BCryptPasswordEncoder bCryptPasswordEncoder,
    		RolesInternosRepository rolesRepository) {
        this.repository = repository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.rolesRepository=rolesRepository;
        
    }
    
    public CuentaInterno findById(String id)throws Exception {
    	Optional<CuentaInterno>opt = this.repository.findById(id);
    	if(opt.isEmpty()) {
    		throw new CuentaException("No existe cuenta con id: "+id);
    	}
    	return opt.get();
    }
    
	@Override
	public String save(CuentaInterno usuario) {
		try {
		 usuario.setClave(bCryptPasswordEncoder.encode(usuario.getClave()));
			repository.save(usuario);
			
		} catch (DataAccessException e) {
			e.printStackTrace();
			return "Error";
		}
		return "ok";
	}
	@Override
	public String eliminarRolEmpleado(String idCuenta, Long rol)throws Exception {
		CuentaInterno cuenta = this.findById(idCuenta);
		RolesInternos rolDel=cuenta.getRoles().stream().filter(r -> r.getRol().getIdRol().equals(rol)).findAny().orElseThrow();
		cuenta.getRoles().remove(rolDel);
		this.rolesRepository.delete(rolDel);
		return "Rol Eliminado";
	}

}
