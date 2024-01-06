package com.multiplos.cuentas.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.models.Empresa;
import com.multiplos.cuentas.models.Proyecto;
import com.multiplos.cuentas.pojo.promotor.EmpresaFormulario;
import com.multiplos.cuentas.pojo.promotor.EmpresaRequest;
import com.multiplos.cuentas.pojo.promotor.EmpresaResponse;
import com.multiplos.cuentas.pojo.promotor.PromotorResponse;

public interface EmpresaService {

	List<Empresa> consultaEmpresas();

	EmpresaResponse getByfilter(EmpresaRequest filter) throws Exception;

	Empresa findById(Long id)throws Exception;
	
	Object putEmpresa(EmpresaFormulario empresa) throws Exception;

	Object CargarDocumentosJuridicos(Long idEmpresa, List<MultipartFile> file, String userCompose) throws Exception;

	Object CargarDocumentosFinancieros(Long idEmpresa, List<MultipartFile> file, String userCompose) throws Exception;

	Object enviarMailPagoFactura1(Proyecto proyecto, String codFact,String numDoc,String idPlantilla) throws Exception;

	Object consultarFacturaPorCliente(String cliente)throws Exception;
	
	Object getDocumentosJurResponse(Long idEmpresa, String cuenta) throws Exception;

	Object getDocumentosFinResponse(Long idEmpresa, String cuenta) throws Exception;

	PromotorResponse getPromotor(String id) throws Exception;
	 PromotorResponse findPromotor(String id) throws Exception;

}
