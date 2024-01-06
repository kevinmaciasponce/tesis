package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.Parametro;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.repository.ParametroRepository;
import com.multiplos.cuentas.services.ParametroService;
import com.multiplos.cuentas.utils.ServicesUtils;

@Service
public class ParametroServiceImpl implements ParametroService {

	private ParametroRepository repository;
	private ServicesUtils serviceUtils;
	private static final Logger LOG = LoggerFactory.getLogger(ParametroServiceImpl.class);
    @Autowired
    public ParametroServiceImpl(ParametroRepository repository, ServicesUtils serviceUtils) {
        this.repository = repository;
        this.serviceUtils = serviceUtils;
    }
    
	@Override
	@Transactional(readOnly = true)
	public List<ParametroResponse> findByParametro(String parametro) {
		List<Parametro> list = new ArrayList<>();
		List<ParametroResponse> listResponse = new ArrayList<>();
		
		list = repository.findByParametro(parametro.toUpperCase()).stream()
				.filter(c -> c.getEstado().contains("A"))
				.collect(Collectors.toList());
		
		listResponse = serviceUtils.getParametroResponse(list);
				
		return listResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public ParametroResponse findByParametroCodParametro(String codParametro)  {
		Parametro param ;
		ParametroResponse responseParam = null;
		
		param = repository.findByParametroCodParametro(codParametro);
		
		if(param != null) {
			responseParam = new ParametroResponse();
			LOG.warn(""+param.getIdParametro());
			responseParam.setIdParametro(param.getIdParametro());
			responseParam.setCodParametro(param.getCodParametro());
			responseParam.setValor(param.getValor());
			responseParam.setDescripcion(param.getDescripcion());
		}	else {
			
		}
		return responseParam;
	}
	@Override
	@Transactional(readOnly = true)
	public ParametroResponse findByParametroCodParametroThrow(String codParametro)throws Exception  {
		Parametro param ;
		ParametroResponse responseParam = null;
		
		param = repository.findByParametroCodParametro(codParametro);
		
		if(param != null) {
			responseParam = new ParametroResponse();
			LOG.warn(""+param.getIdParametro());
			responseParam.setIdParametro(param.getIdParametro());
			responseParam.setCodParametro(param.getCodParametro());
			responseParam.setValor(param.getValor());
			responseParam.setDescripcion(param.getDescripcion());
		}	else {
			throw new CuentaException("No existe parametro para el codigo: "+codParametro);
		}
		return responseParam;
	}

	@Override
	@Transactional(readOnly = true)
	public String consultaParametroDescripcion(String valor) {
		return repository.consultaParametroDescripcion(valor);
	}

}
