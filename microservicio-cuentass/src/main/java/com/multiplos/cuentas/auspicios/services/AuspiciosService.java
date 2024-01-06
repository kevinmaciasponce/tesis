package com.multiplos.cuentas.auspicios.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.multiplos.cuentas.auspicios.models.Auspicio;
import com.multiplos.cuentas.auspicios.models.AuspicioFilter;
import com.multiplos.cuentas.auspicios.models.AuspicioRecompensa;
import com.multiplos.cuentas.auspicios.models.AuspicioRequest;
import com.multiplos.cuentas.auspicios.models.AuspicioResponse;
import com.multiplos.cuentas.auspicios.models.AuspicioTorneo;
import com.multiplos.cuentas.auspicios.models.Beneficiario;
import com.multiplos.cuentas.auspicios.models.BeneficiarioFilter;
import com.multiplos.cuentas.auspicios.models.BeneficiarioFormRequest;
import com.multiplos.cuentas.auspicios.models.BeneficiarioResponse;
import com.multiplos.cuentas.auspicios.models.Categorias;
import com.multiplos.cuentas.auspicios.models.Deportista;
import com.multiplos.cuentas.auspicios.models.Disciplina;
import com.multiplos.cuentas.auspicios.models.Modalidad;
import com.multiplos.cuentas.auspicios.models.RecompensasAllow;
import com.multiplos.cuentas.auspicios.models.TitulosRequest;
import com.multiplos.cuentas.auspicios.models.TorneosAllow;
import com.multiplos.cuentas.auspicios.models.ValoracionRequest;



public interface AuspiciosService {

	List<Categorias> listarCategorias(Boolean status);
	String putCategoria(Categorias cat);
	List<Disciplina> listarDisciplina(Boolean status);
	String putDisciplina(Disciplina dis);
	List<Modalidad> listarModalidad(Boolean status);
	String putModalidad(Modalidad mod);
	String fomularioBeneficiario(BeneficiarioFormRequest form)throws Exception;
	String agregarTitulosBeneficiario(TitulosRequest request) throws Exception;
	String agregarFotosBeneficiario(String identificacion, String idRepre, MultipartFile file, MultipartFile file2) throws Exception;
	Object setearPersonaAcuentaBancaria();
	Object aggValoracion( ValoracionRequest rq, MultipartFile file)throws Exception;
	boolean validateValoracion(String id)throws Exception;
	
	String aggAuspicio( AuspicioRequest request,String estado )throws Exception;
	
	String aggTorneos(TorneosAllow torneo)throws Exception;
	String aggRecompensas(RecompensasAllow recompensas)throws Exception;
	
	
	
	Object EnviarAuspicio( Long id)throws Exception;
	AuspicioResponse Consultar(String id);
	
	
	
	void eliminarBene(String id);
	Object beneByPredicates(String identificacion,String representante);
	Object consultaBeneForAgg(BeneficiarioFilter filter)throws Exception;
	
	Object consultarBeneByRepre(BeneficiarioFilter filter)throws Exception;
	List<?> consultarTitulos(String identificacion)throws Exception;
	
	Object consultarAuspicioByBeneficiario(AuspicioFilter filter)throws Exception;
	Object consultarAuspicioById(Long id)throws Exception;
	
	Object consultarValoracion(String identificacion)throws Exception;
	Object consultarRecompensas(Long id)throws Exception;
	Object consultarTorneos(Long id)throws Exception;
	List<?>consultarAuspiciosPorEstado(AuspicioFilter filter)throws Exception;
	
	List<?> consultarAuspiciosByRepre(String ideRepre)throws Exception;
	
	
	
	
	Object consultarAuspicioFilter(AuspicioFilter filter)throws Exception;
	
	
	//servicios para analista
	Object actualizaEstadoAuspicio(Long id, String usuario,String status,String observacion)throws Exception;
	Object anularAuspicioPorConfirmar(Long id, String usuario,String observacion)throws Exception;
	
	
	
	
	
	
	
}
