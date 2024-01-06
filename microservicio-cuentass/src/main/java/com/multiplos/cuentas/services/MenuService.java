package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.Menu;

public interface MenuService {

	List<Menu> findAll();
	Menu findById(Long id);
	
}
