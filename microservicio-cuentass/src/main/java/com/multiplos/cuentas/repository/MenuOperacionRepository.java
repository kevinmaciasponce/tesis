package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.MenuOperacion;
import com.multiplos.cuentas.models.MenuOperacionKey;

public interface MenuOperacionRepository extends JpaRepository<MenuOperacion, MenuOperacionKey> {
	
	@Query("select distinct(mo.menu.idMenu) from MenuOperacion mo where mo.rol.idRol=?1")
	List<Long> consultaMenuPorRol(Long idRol);

}
