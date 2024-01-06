package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.Pais;
import com.multiplos.cuentas.repository.PaisRepository;
import com.multiplos.cuentas.services.PaisService;

@Service
public class PaisServiceImpl implements PaisService {

	private PaisRepository repository;
	
	@Autowired
    public PaisServiceImpl(PaisRepository repository) {
        this.repository = repository;
    }
	
	@Override
	@Transactional(readOnly = true)
	public List<Pais> findAll(String status) {
		List<Pais> paises = new ArrayList<>();
		paises = repository.getAll(status);
		return paises;
	}

	@Override
	@Transactional
	public String save(Pais entity)throws Exception {
		boolean existe;
		String response;
		existe = this.existePais(entity.getPais(),entity.getGentilicio(),entity.getIdNacionalidad());
		if (existe) {
			throw new CuentaException("País ya existe");
		}
		if (entity.getIdNacionalidad() != null) {
			response = "País actualizado";
		} else {
			response = "País agregado con exito";
		}
		try {
			repository.save(entity);
		} catch (Exception e) {
			response = "No se pudo agregar el País";
		}
		return response;
	}

	@Override
	@Transactional
	public String deletePais(Long id) {
		Pais pais = this.findById(id);
		if(pais != null) {
			pais.setEstado("I");
			repository.save(pais);
			return "Pais eliminado con exito";			
		}else {
			return "No se pudo eliminar, pais no existe";
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Pais findById(Long id) {
		Pais p = null;
		try {
			Optional<Pais> pais = repository.findById(id);
			if(pais.get().getEstado().contains("A")) {
				p = new Pais();
				p = pais.get();
			}
		}catch (Exception e) {
            return p;
        }
		return p;
	}
	
	public boolean existePais(String pais, String gentilicio,Long id) {
		boolean existe = false;
		if(repository.existePais(pais, gentilicio,id) == 1) {
			existe = true;
		}
		return existe;
	}

	@Override
	@Transactional(readOnly = true)
	public String consultaPaisPorAbrebiatura(String abreviatura) {
		return repository.consultaPaisPorAbrebiatura(abreviatura);
	}

}
