package com.multiplos.cuentas.services.impl;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.multiplos.cuentas.models.ConciliacionXls;
import com.multiplos.cuentas.models.HistorialConciliacion;
import com.multiplos.cuentas.repository.HistorialConciliacionRepository;
import com.multiplos.cuentas.repository.ConciliaXlsRepository;
import com.multiplos.cuentas.services.HistorialConciliacionService;


@Service
public class HistorialConciliacionServiceImp implements HistorialConciliacionService {

	private HistorialConciliacionRepository  hcRepository;
	private ConciliaXlsRepository conciliaRepository;
	
	@Autowired
    public HistorialConciliacionServiceImp(HistorialConciliacionRepository  historyConcialiacion,ConciliaXlsRepository conciliaRepository) {
        this.hcRepository  =historyConcialiacion;
        this.conciliaRepository =conciliaRepository;
    }
	
	@Override
	@Transactional
	public String save(HistorialConciliacion hc)throws IOException {
		String response=null;	
		try {
		ConciliacionXls cabecera = conciliaRepository.findActive();
		hc.setIdFile(cabecera);
			if(hc!=null) {
				hcRepository.save(hc);
				response="CONCILIACION TERMINADA, SE GENERÓ HISTORIAL";
				}else
					response ="ERROR CON LA CONCILIACION";
		}catch (Exception e) {	
			throw new IOException(e.getMessage());
		}
	return response;
	}

}