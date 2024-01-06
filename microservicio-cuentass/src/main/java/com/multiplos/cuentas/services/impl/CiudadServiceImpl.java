package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.models.Ciudad;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.pojo.ciudad.CiudadResponse;
import com.multiplos.cuentas.repository.CiudadRepository;
import com.multiplos.cuentas.services.CiudadService;

@Service
public class CiudadServiceImpl implements CiudadService {
	
	private static CiudadRepository repository;
	
	@Autowired
    public CiudadServiceImpl(CiudadRepository repository) {
        this.repository = repository;
    }
	
	@Override
	@Transactional(readOnly = true)
	public List<CiudadResponse> findAll(String status) {
		List<Ciudad> ciudades = new ArrayList<>();
		List<CiudadResponse> ciudadResponse = new ArrayList<>();
		ciudades = repository.all( status);
		
		ciudades.forEach(c->{
			CiudadResponse ciudad = new CiudadResponse();
			ciudad.setId(c.getIdCiudad());
			ciudad.setCiudad(c.getCiudad());
			ciudad.setEstado(c.getEstado());
			ciudadResponse.add(ciudad);
		});
		return ciudadResponse;
	}

	@Override
	
	public String save(Ciudad entity)throws Exception{
		boolean existe;
		String response;
		existe = this.existeCiudad(entity.getCiudad(), entity.getIdCiudad());
		if (existe ) {
			throw new CuentaException("Ciudad ya existe");
		}
		if (entity.getIdCiudad() != null) {
			response = "Ciudad actualizado";
		} else {
			response = "Ciudad agregado con exito";
		}
		try {
			repository.save(entity);
		} catch (Exception e) {
			response = "No se pudo agregar la Ciudad";
		}
		return response;
	}

	@Override
	@Transactional
	public String deleteCiudad(Long id) {
		Ciudad c = new Ciudad();
		Optional<Ciudad> ciudad = repository.findById(id);
		c = ciudad.get();
		if(c != null) {
			c.setEstado("I");
			repository.save(c);
			return "Ciudad eliminada con exito";			
		}else {
			return "No se pudo elimnar, Ciudad no existe";
		}
	}

	@Override
	@Transactional(readOnly = true)
	public CiudadResponse findById(Long id) {
		CiudadResponse c=null;
		try {
			Optional<Ciudad> ciudad = repository.findById(id);
			if(ciudad.get().getEstado().contains("A")) {
				c = new CiudadResponse();
				c.setId(ciudad.get().getIdCiudad());
				c.setCiudad(ciudad.get().getCiudad());
			}
		}catch (Exception e) {
            return c;
        }
		return c;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CiudadResponse> consultaCiudadPorPais(Long idPais) {
		List<Ciudad> ciudades = new ArrayList<>();
		List<CiudadResponse> ciudadResponse = new ArrayList<>();
		ciudades = repository.findByIdPais(idPais)
				.stream()
				.filter(p -> p.getEstado().contains("A"))
				.collect(Collectors.toList());
		
		ciudades.forEach(c->{
			CiudadResponse ciudad = new CiudadResponse();
			ciudad.setId(c.getIdCiudad());
			ciudad.setCiudad(c.getCiudad());
			ciudadResponse.add(ciudad);
		});
		return ciudadResponse;
	}

	public boolean existeCiudad(String ciudad,Long id) {
		boolean existe = false;
		if(repository.existeCiudad(ciudad,id) > 0) {
			existe = true;
		}
		return existe;
	}

	
}
