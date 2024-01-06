package com.multiplos.cuentas.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Factura;

public interface FacturaRepository extends JpaRepository<Factura,Long> {

	List<Factura> findByIdCliente(String id);
	
	@Query("select MAX(f.numeroDoc) from Factura f  ")
	String LastNoDoc(PageRequest pageable);
	
	@Query("select f.totalFactura from Factura f where f.codFact=:cod ")
	BigDecimal totalFacturaByCod(String cod);
	
}
