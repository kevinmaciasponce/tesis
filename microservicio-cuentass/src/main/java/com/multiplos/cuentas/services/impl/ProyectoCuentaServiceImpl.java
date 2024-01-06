package com.multiplos.cuentas.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Parametro;
import com.multiplos.cuentas.models.ProyectoCuenta;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.pojo.proyecto.CuentaHabilResponse;
import com.multiplos.cuentas.repository.ProyectoCuentaRepository;
import com.multiplos.cuentas.services.ParametroService;
import com.multiplos.cuentas.services.ProyectoCuentaService;

@Service
public class ProyectoCuentaServiceImpl implements ProyectoCuentaService {

	private ProyectoCuentaRepository repository;
	private ParametroService parametroservice;
	
	
	@Autowired
	public ProyectoCuentaServiceImpl(ProyectoCuentaRepository repository,ParametroService parametroservice) {
		this.repository = repository;
		this.parametroservice = parametroservice;
	}

	@Override
	@Transactional(readOnly = true)
	public ProyectoCuenta findByIdProyectoCuenta(Long id) {
		return repository.findByIdProyectoCuenta(id);
	}

	@Override
	@Transactional(readOnly = true)
	public CuentaHabilResponse consultaCuentaPorProyecto(String idProyecto) throws Exception{
		CuentaHabilResponse cuentaHabilitada = new CuentaHabilResponse();
		ProyectoCuenta proyectoCuenta = new ProyectoCuenta();
		proyectoCuenta = repository.consultaCuentaPorProyecto(idProyecto);
		ParametroResponse parametro = this.parametroservice.findByParametroCodParametro("CORREOCONTACTO");
		if(proyectoCuenta != null) {
			cuentaHabilitada.setBeneficiaria(proyectoCuenta.getProyecto().getEmpresa().getNombre());
			cuentaHabilitada.setRuc(proyectoCuenta.getProyecto().getEmpresa().getRuc());
			cuentaHabilitada.setBanco(proyectoCuenta.getBanco().getNombre());
			cuentaHabilitada.setTipoCuenta(proyectoCuenta.getTipoCuenta());
			cuentaHabilitada.setNumeroCuenta(proyectoCuenta.getNumeroCuenta());
			cuentaHabilitada.setCorreocontacto(parametro.getValor());
		}else {
			throw new Exception("El proyecto no tiene una cuenta habilitada");
		}
		return cuentaHabilitada;
	}
}
