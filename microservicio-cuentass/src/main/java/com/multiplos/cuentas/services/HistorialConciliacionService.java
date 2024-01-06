package com.multiplos.cuentas.services;
import java.io.IOException;

import com.multiplos.cuentas.models.HistorialConciliacion;

public interface HistorialConciliacionService {
	
	String save(HistorialConciliacion historialConciliacion)throws IOException;

}