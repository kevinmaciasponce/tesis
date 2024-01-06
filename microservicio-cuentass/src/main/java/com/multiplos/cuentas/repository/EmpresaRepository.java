package com.multiplos.cuentas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.DocumentosFinancierosPromotor;
import com.multiplos.cuentas.models.DocumentosJuridicosPromotor;
import com.multiplos.cuentas.models.Empresa;
import com.multiplos.cuentas.models.Solicitud;
import com.multiplos.cuentas.pojo.promotor.DocumentosFinancierosResponse;
import com.multiplos.cuentas.pojo.promotor.DocumentosJuridicosResponse;
import com.multiplos.cuentas.pojo.promotor.EmpresaResponse;
import com.multiplos.cuentas.pojo.promotor.PromotorResponse;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
	
	@Query(value = "select e from Empresa e, Proyecto p where p.empresa.idEmpresa = e.idEmpresa and p.idProyecto=:codProyecto")
	Empresa consultaSolicitud(String codProyecto);
	
	
	@Query("select new com.multiplos.cuentas.pojo.promotor.EmpresaResponse("
			+ " p.idEmpresa, "
			+ " p.nombre, "
			+ " p.actividad.nombre,"
			+ " p.ruc, "
			+ " p.pais.pais ,"
			+ " p.direccion, "
			+ " p.ciudad, "
			+ " CASE  WHEN  p.datosAnualActual is null  THEN null ELSE (select d.margenContribucion from EmpresaDatosAnuales d where d.empresa.idEmpresa = :id and d.activo=true) END, "
			+ " CASE  WHEN  p.datosAnualActual is null  THEN null ELSE (select d.ventasTotales from EmpresaDatosAnuales d where d.empresa.idEmpresa = :id and d.activo=true) END, "
			+ " p.descripcionProducto, "
			+ " p.antecedente,"
			+ " p.ventajaCompetitiva)"
			+ " from Empresa p "
			+ " where p.idEmpresa =:id "
			+ " and (:cuenta is null or :cuenta= p.cuenta.idCuenta) ")
	EmpresaResponse getByFilter(Long id, String cuenta);
	
	
	@Query("select new com.multiplos.cuentas.pojo.promotor.PromotorResponse("
			+ " e.cuenta.persona.razonSocial, e.cuenta.persona.identificacion, e.cuenta.persona.emailContacto, e.cuenta.persona.cargoContacto,"
			+ " e.cuenta.persona.nombreContacto, e.cuenta.persona.numeroCelular, e.cuenta.persona.nacionalidad,e.cuenta.persona.anioInicioActividad, e.cuenta.persona.anioInicioActividad, e.idEmpresa ) "
			+ " from Empresa e where"
			+ " :id= e.cuenta.idCuenta")
	PromotorResponse getPromotor(String id);
	
	@Query("select new com.multiplos.cuentas.pojo.promotor.PromotorResponse("
			+ " e.persona.razonSocial, e.persona.identificacion, e.persona.emailContacto, e.persona.cargoContacto,"
			+ " e.persona.nombreContacto, e.persona.numeroCelular, e.persona.nacionalidad,e.persona.anioInicioActividad, e.persona.anioInicioActividad ) "
			+ " from Cuenta e where"
			+ " :id= e.idCuenta")
	PromotorResponse findPromotor(String id);
	
	@Query("select d from DocumentosJuridicosPromotor d where d.empresa.idEmpresa=:idEmpresa and d.empresa.cuenta.idCuenta=:cuenta and d.activo=true")
	DocumentosJuridicosPromotor getDocJur(Long idEmpresa, String cuenta);
	
	@Query("select d from DocumentosFinancierosPromotor d where d.empresa.idEmpresa=:idEmpresa and d.empresa.cuenta.idCuenta=:cuenta and d.activo=true")
	DocumentosFinancierosPromotor getDocFin(Long idEmpresa, String cuenta);
	
	
	@Query("select new com.multiplos.cuentas.pojo.promotor.DocumentosJuridicosResponse(d.escritura,d.estatutosVigentes,d.rucVigente,d.nombramientoRl,d.cedulaRl,d.nominaAccionista,d.identificacionesAccionista) from DocumentosJuridicosPromotor d where d.empresa.idEmpresa=:idEmpresa and d.empresa.cuenta.idCuenta=:cuenta and d.activo=true")
	DocumentosJuridicosResponse getDocJurRes(Long idEmpresa, String cuenta);
	
	@Query("select new com.multiplos.cuentas.pojo.promotor.DocumentosFinancierosResponse(d.impuestoRentaAnioAnterior,d.estadoFinancieroAnioAnterior,d.estadoFinancieroActuales,d.anexoCtsCobrar) from DocumentosFinancierosPromotor d where d.empresa.idEmpresa=:idEmpresa and d.empresa.cuenta.idCuenta=:cuenta and d.activo=true")
	DocumentosFinancierosResponse getDocFinRes(Long idEmpresa, String cuenta);
}
