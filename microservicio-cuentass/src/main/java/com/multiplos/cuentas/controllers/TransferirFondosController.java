package com.multiplos.cuentas.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.multiplos.cuentas.services.PorcentajeSolicitudAprobadaService;
import com.multiplos.cuentas.services.SolicitudService;
import com.multiplos.cuentas.utils.ServicesUtils;

public class TransferirFondosController {
	private SolicitudService service;
	private ServicesUtils utilService;
	@Autowired
	public TransferirFondosController(SolicitudService service, ServicesUtils utilService) {
		this.service = service;
		this.utilService = utilService;
	}
}
