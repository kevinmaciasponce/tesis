package com.multiplos.cuentas.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Menu;
import com.multiplos.cuentas.repository.MenuRepository;
import com.multiplos.cuentas.services.MenuService;

@Service
public class MenuServiceImpl implements MenuService {

	private MenuRepository repository;
	
	@Autowired
	public MenuServiceImpl(MenuRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Menu> findAll() {
		return repository.findAll();//.stream().filter(p->!p.getSubMenu().isEmpty()).collect(Collectors.toList());
		
	}

	@Override
	@Transactional(readOnly = true)
	public Menu findById(Long id) {
		return repository.findByIdMenu(id);
	}

}
