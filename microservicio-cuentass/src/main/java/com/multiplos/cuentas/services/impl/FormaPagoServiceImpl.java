package com.multiplos.cuentas.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.FormaPago;
import com.multiplos.cuentas.repository.FormaPagoRepository;
import com.multiplos.cuentas.services.FormaPagoService;

@Service
public class FormaPagoServiceImpl implements FormaPagoService{

	private FormaPagoRepository repository;
	
	@Autowired
	public FormaPagoServiceImpl(FormaPagoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<FormaPago> findAll(String status) {
		return repository.getAll(status);
				}

	@Override
	@Transactional(readOnly = true)
	public FormaPago consultaFormaPago(Long id) {
		return repository.consultaFormaPago(id);
	}

	@Override
	@Transactional
	public String save(FormaPago entity)throws Exception {
		boolean existe;
		String response;
		existe = this.existeFormaPago(entity.getDescripcion(), entity.getIdFormaPago());
		if(existe ) {
			throw new CuentaException("Forma de pago ya existe");
		}if (entity.getIdFormaPago() != null) {
			response = "Forma de pago actualizado";
		} else {
			response = "Forma de pago agregado con exito";
		}
		try {
			repository.save(entity);
		} catch (Exception e) {
			response = "No se pudo agregar la Forma de pago";
		}
		return response;
	}

	@Override
	@Transactional
	public String delete(Long id) {
		FormaPago formaPago = new FormaPago();
		formaPago = this.consultaFormaPago(id);
		if(formaPago != null) {
			formaPago.setEstado("I");
			repository.save(formaPago);
			return "Forma de pago eliminado con exito";			
		}else {
			return "No se pudo eliminar, Forma de pago no existe";
		}
	}
	
	public boolean existeFormaPago(String formaPago, Long id) {
		boolean existe = false;
		if(repository.existeFormaPago(formaPago,id) > 0) {
			existe = true;
		}
		return existe;
	}

}
