package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multiplos.cuentas.models.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long>{
	Menu findByIdMenu(Long id);
}
