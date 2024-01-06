package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Banco;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.repository.BancoRepository;
import com.multiplos.cuentas.services.BancoService;

@Service
public class BancoServiceImpl implements BancoService {

	private BancoRepository repository;

	@Autowired
	public BancoServiceImpl(BancoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Banco> findAll(String status) {
		List<Banco> bancos = new ArrayList<>();
		bancos = repository.getAll(status);
		return bancos;
	}

	@Override
	@Transactional(readOnly = true)
	public Banco findById(Long id) {
		Optional<Banco> b = null;

		b = repository.findById(id);
		if (b.isEmpty()) {
			throw new NoResultException("Banco no existe con id: " + id);
		}
		Banco banco = b.get();
		if (!banco.getEstado().equals("A")) {
			throw new NoResultException("Banco no se encuentra activo con id: " + id);
		}
		return banco;
	}

	@Override
	@Transactional
	public String save(Banco entity) throws Exception {
		boolean existe;
		String response;
		existe = this.existeBanco(entity.getNombre(),entity.getIdBanco());
		if (existe) {
			throw new CuentaException("Banco ya existe");
		}
		if (entity.getIdBanco() != null) {
			response = "Banco actualizado";
		} else {
			response = "Banco agregado con exito";
		}
		try {
			repository.save(entity);
		} catch (Exception e) {
			response = "No se pudo agregar el Banco";
		}
		return response;
	}

	@Override
	@Transactional
	public String deleteBanco(Long id) {
		Banco b = new Banco();
		Optional<Banco> ciudad = repository.findById(id);
		b = ciudad.get();
		if (b != null) {
			b.setEstado("I");
			repository.save(b);
			return "Banco eliminado con exito";
		} else {
			return "No se pudo eliminar, Banco no existe";
		}
	}

	public boolean existeBanco(String banco,Long id) {
		boolean existe = false;
		if (repository.existeBanco(banco, id) == 1) {
			existe = true;
		}
		return existe;
	}

//	public String putBanco(Banco banco) {
//		if(banco.getIdBanco()==null) {
//			this.findById(banco.getIdBanco());
//		}
//		
//	}
}
