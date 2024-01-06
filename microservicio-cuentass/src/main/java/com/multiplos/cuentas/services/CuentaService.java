package com.multiplos.cuentas.services;

import java.util.List;

import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.Persona;
import com.multiplos.cuentas.pojo.cuenta.CuentaRequest;
import com.multiplos.cuentas.pojo.cuenta.CuentaRolResponse;
import com.multiplos.cuentas.pojo.login.RequestLogin;
import com.multiplos.cuentas.pojo.login.ResponseLogin;

public interface CuentaService {
	
	String saveCuenta(Cuenta cuenta, Persona persona);
	void eliminarCuenta();
	String save(CuentaRequest cuenta)throws Exception;
	String activaCuenta(String usuario);
	ResponseLogin validaLogin(RequestLogin login);
	String confirmarCuenta(String idCuenta);
	Cuenta findByCuenta(String email);
	Cuenta findByCuentaIdCuenta(String idCuenta)throws Exception;
	
	List<Cuenta> consultaCuentasRegistradas();
	CuentaRolResponse consultaCuentaRol(String nombreRol,String usuario);
	String loginError(RequestLogin credencial);
	boolean verificaCuenta(String email);
	
	String forgotPass(String mail,String identidicacion)throws Exception;
	boolean validSwitchPass(String token)throws Exception;
	String switchPass(String token,String pass)throws Exception;
	
	Object AsignarRoll(String idcuenta, Long roll)throws Exception;
	
	Object setearRol() throws Exception;
	
	String enviaEmailPromotor(String email);
	//String encriptaClave(String clave);//solo pruebas internas 
}
