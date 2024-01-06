package com.multiplos.cuentas.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.multiplos.cuentas.auth.service.JWTService;
import com.multiplos.cuentas.models.Cuenta;
import com.multiplos.cuentas.models.CuentaException;
import com.multiplos.cuentas.models.CuentaInterno;
import com.multiplos.cuentas.models.MenuOperacion;
import com.multiplos.cuentas.models.MenuOperacionInt;
import com.multiplos.cuentas.models.Persona;
import com.multiplos.cuentas.models.Rol;
import com.multiplos.cuentas.models.RolInt;
import com.multiplos.cuentas.models.Roles;
import com.multiplos.cuentas.models.RolesIntKeys;
import com.multiplos.cuentas.models.RolesInternos;
import com.multiplos.cuentas.models.RolesKeys;
import com.multiplos.cuentas.models.Token;
import com.multiplos.cuentas.pojo.cuenta.CuentaRequest;
import com.multiplos.cuentas.pojo.cuenta.CuentaRolResponse;
import com.multiplos.cuentas.pojo.cuenta.MenuResponse;
import com.multiplos.cuentas.pojo.cuenta.OperacionResponse;
import com.multiplos.cuentas.pojo.login.RequestLogin;
import com.multiplos.cuentas.pojo.login.ResponseLogin;
import com.multiplos.cuentas.pojo.login.RolesResponse;
import com.multiplos.cuentas.pojo.parametro.ParametroResponse;
import com.multiplos.cuentas.repository.CuentaInternoRepository;
import com.multiplos.cuentas.repository.CuentaRepository;
import com.multiplos.cuentas.repository.RolRepository;
import com.multiplos.cuentas.repository.TokenRepository;
import com.multiplos.cuentas.services.CuentaService;
import com.multiplos.cuentas.services.DocumentoAceptadoService;
import com.multiplos.cuentas.services.EnvioEmailService;
import com.multiplos.cuentas.services.ParametroService;
import com.multiplos.cuentas.services.TokenService;
import com.multiplos.cuentas.utils.ServicesUtils;

@Service
public class CuentaServiceImpl implements CuentaService {

	private static final Logger LOG = LoggerFactory.getLogger(CuentaServiceImpl.class);
	private static final String usuarioAdmin = "ADMIN";
	private CuentaRepository cuentaRepository;
	private RolRepository rolRepository;
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	private TokenService tokenService;
	private TokenRepository tokenRepository;
	private ServicesUtils utils;
	private ParametroService parametroService;
	private DocumentoAceptadoService documentoAceptadoService;
	private CuentaInternoRepository cuentaUserInternoRepository;
	private EnvioEmailService envioEmailService;
	private JWTService jwtService;
	private Date fechaActual = null;

	@Autowired
	public CuentaServiceImpl(CuentaRepository cuentaRepository, RolRepository rolRepository,
			@Lazy BCryptPasswordEncoder bCryptPasswordEncoder, ServicesUtils utils, TokenService tokenService,
			ParametroService parametroService, DocumentoAceptadoService documentoAceptadoService,
			CuentaInternoRepository cuentaUserInternoRepository, @Lazy EnvioEmailService envioEmailService,
			JWTService jwtService, TokenRepository tokenRepository) {
		this.cuentaRepository = cuentaRepository;
		this.rolRepository = rolRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.tokenService = tokenService;
		this.utils = utils;
		this.parametroService = parametroService;
		this.documentoAceptadoService = documentoAceptadoService;
		this.cuentaUserInternoRepository = cuentaUserInternoRepository;
		this.envioEmailService = envioEmailService;
		this.jwtService = jwtService;
		this.tokenRepository = tokenRepository;
	}

	public String saveCuenta(Cuenta cuenta, Persona persona) {

		return "SU CUENTA FUE CREADA.";
	}

	@Override
	@Transactional
	public void eliminarCuenta() {
		List<Cuenta> cuentas = new ArrayList<>();
		cuentas = this.cuentaRepository.findAll();
		for (Cuenta aux : cuentas) {

//			if (aux.getRol().getIdRol() == 10) {
//				if (!aux.getIdCuenta().equals("0993322954001") && !aux.getIdCuenta().equals("0928480946")
//						&& !aux.getIdCuenta().equals("0478945212") && !aux.getIdCuenta().equals("0587451228001")) {
//					this.cuentaRepository.delete(aux);
//				}
//
//			}
		}

	}

	@Override
	@Transactional
	public String save(CuentaRequest request) throws Exception {
		Boolean existe;
		existe = this.existeCuenta(request.getIdentificacion(), request.getEmail(), request.getEmail().toUpperCase());
		if (existe) {
			throw new CuentaException("Su cuenta ya existe, verifique su cédula o correo");
		}
		if (request.getTipoPersona().contains("NAT")) {
			Integer edad = utils.calculaEdad(request.getFechaNacimiento());
			if (edad < 18) {
				return "El usuario debe ser mayor de edad.";
			}
		}
		Cuenta cuenta = request.getCuenta();

		Rol rol = rolRepository.consultaRol(request.getTipoCliente());
		RolesKeys key = new RolesKeys(rol.getIdRol(),cuenta.getIdCuenta());
		cuenta.getRoles().add(Roles.builder().id(key).rol(rol).cuenta(cuenta).UsuarioCreacion(new CuentaInterno("admin")).build());
		cuenta=	this.cuentaRepository.save(cuenta);
		
		Token confirmationToken = new Token(null,UUID.randomUUID().toString(),new Date(),cuenta);
		tokenService.generaToken(confirmationToken);
		String urlToken= parametroService.findByParametroCodParametro("URL_ACTIVACTA").getValor().concat(confirmationToken.getToken());
		String mensaje = envioEmailService.enviaEmailInvestor("ACTIVA_CUENTA", urlToken, cuenta.getEmail());
		LOG.info("se envio mail");
		//guarda documentos politicos
		if (cuenta.getAceptaPoliticaPrivacidad().contains("S")) {
			documentoAceptadoService.guardaDocumentoGeneralAceptado(cuenta.getPersona().getIdentificacion(),cuenta.getUsuario(), null, 3);
			LOG.info("guardaDocumentoGeneralAceptado");			
		}
		if( cuenta.getAceptaTerminoUso().contains("S")) {
			documentoAceptadoService.guardaDocumentoGeneralAceptado(cuenta.getIdCuenta(),cuenta.getUsuario(), null, 4);
			LOG.info("save: guardaDocumentoGeneralAceptado");
		}
//		
		return mensaje;
		//return "Cuenta creada correctamente, se ha enviado un mail para la activicación y auntenticación";
	}

//	public String save(CuentaRequest cuentaRequest) throws Exception{
//		
//		String user;
//		String userDefault = "YALINAMC";
//		List<Rol> rolList = new ArrayList<>();
//		Cuenta cuenta = new Cuenta();
//		String respuestaEmail;
//		boolean existe;
//		Cuenta cuentaCreada;
//		int edad = 0;
//		Persona persona = new Persona();
//
//		if (cuentaRequest.getTipoPersona().contains("NAT")) {
//			edad = utils.calculaEdad(cuentaRequest.getFechaNacimiento());
//			if (edad < 18) {
//				return "El usuario debe ser mayor de edad.";
//			}
//		}
//		existe = this.existeCuenta(cuentaRequest.getIdentificacion(), cuentaRequest.getEmail(), null);
//		if (existe) {
//			return "Su cuenta ya existe, verifique su cédula o correo";
//		} else {
//			// para setear cuenta
//			cuenta.setIdCuenta(cuentaRequest.getIdentificacion());
//			//
//			cuenta.setEmail(cuentaRequest.getEmail());
//			cuenta.setTipoContacto(cuentaRequest.getTipoContacto());
//			if (cuentaRequest.getTipoContacto().contains("RS") || cuentaRequest.getTipoContacto().contains("PUB")) {
//				cuenta.setUsuarioContacto(userDefault);
//			} else {
//				cuenta.setUsuarioContacto(cuentaRequest.getUsuarioContacto());
//			}
//			cuenta.setAceptaPoliticaPrivacidad(cuentaRequest.getAceptaPoliticaPrivacidad().toUpperCase());
//			cuenta.setAceptaTerminoUso(cuentaRequest.getAceptaTerminoUso().toUpperCase());
//			cuenta.setAceptaRecibirInformacion(cuentaRequest.getAceptaRecibirInformacion().toUpperCase());// nuevo
//			cuenta.setCuentaActiva("N");
//			cuenta.setEstado("A");
//			cuenta.setUsuarioCreacion(usuarioAdmin);
//
//			user = cuentaRequest.getEmail();
//
//			existe = this.existeCuenta(null, null, user.toUpperCase());
//			if (existe) {
//				return "Usuario ya existe, verifique su correo";
//			}
//
//			cuenta.setUsuario(user.toUpperCase());
//			cuenta.setClave(this.encriptaClave(cuentaRequest.getClave()));
//
//			rolList = rolRepository.findAll();
//			CuentaInterno usuarioCreacion =new CuentaInterno();
//			usuarioCreacion.setIdCuenta("admin");
//			rolList.stream().forEach(r -> {
//				if (r.getNombre().contains(cuentaRequest.getTipoCliente())) {
//					Rol rol = new Rol();
//					rol = r;
////					cuenta.setRol(rol);
//					RolesKeys key = new RolesKeys();
//					key.setCuentaKey(cuenta.getIdCuenta());
//					key.setRolKey(rol.getIdRol());
//					cuenta.getRoles().add(Roles.builder().id(key).rol(rol).cuenta(cuenta).UsuarioCreacion(usuarioCreacion).build());
//				}
//			});
//
//			persona.setIdentificacion(cuentaRequest.getIdentificacion());
//			persona.setTipoCliente(cuentaRequest.getTipoCliente());
//			persona.setTipoPersona(cuentaRequest.getTipoPersona());
//			persona.setTipoIdentificacion(cuentaRequest.getTipoIdentificacion());
//			persona.setNacionalidad(cuentaRequest.getNacionalidad());
//			
//			
//			if (cuentaRequest.getTipoPersona().equals("JUR")) {
//				persona.setNombres("");
//				persona.setApellidos("");
//			}
//			if (cuentaRequest.getNombres() != null)
//				persona.setNombres(cuentaRequest.getNombres().toUpperCase());
//			if (cuentaRequest.getApellidos() != null)
//				persona.setApellidos(cuentaRequest.getApellidos().toUpperCase());
//			if (cuentaRequest.getFechaNacimiento() != null)
//				persona.setFechaNacimiento(utils.configuraSumaDia(cuentaRequest.getFechaNacimiento()));
//			if (cuentaRequest.getRazonSocial() != null)
//				persona.setRazonSocial(cuentaRequest.getRazonSocial().toUpperCase());
//
//			if (cuentaRequest.getNombreContacto() != null)
//				persona.setNombreContacto(cuentaRequest.getNombreContacto().toUpperCase());
//
//			if (cuentaRequest.getCargoContacto() != null)
//				persona.setCargoContacto(cuentaRequest.getCargoContacto());
//			if (cuentaRequest.getEmailContacto() != null)
//				persona.setEmailContacto(cuentaRequest.getEmailContacto());
//
//			persona.setNumeroCelular(cuentaRequest.getNumeroCelular());
//			persona.setAnioInicioActividad(cuentaRequest.getAnioInicioActividad());
//
//			persona.setUsuarioCreacion(usuarioAdmin);
//			persona.setEstado("A");
//			cuenta.setPersona(persona);
//
//			try {
//				cuentaCreada = new Cuenta();
//				cuentaCreada = cuentaRepository.save(cuenta);
//				LOG.info("save: cuenta guardada");
//				LOG.wait(3000);
//				if (cuentaCreada != null) {
//					if (persona.getTipoCliente().equals("INVERSIONISTA")
//							|| persona.getTipoCliente().equals("REPRESENTANTE")) {
////						LOG.info("inversionista o repre");
////						LOG.wait(3000);
//						respuestaEmail = this.enviaEmail(cuenta.getEmail());
//						if (respuestaEmail.contains("ok")) {
//							// guardar documentos aceptados - tabla: mult_tipo_documentos
//							// Terminos y condiciones = 3
//							// acuerdo de uso de la plataforma = 4
//							if (cuenta.getAceptaPoliticaPrivacidad().contains("S")) {
//								documentoAceptadoService.guardaDocumentoGeneralAceptado(
//										cuentaCreada.getPersona().getIdentificacion(), cuentaCreada.getUsuario(), null,
//										3);
//								LOG.info("guardaDocumentoGeneralAceptado");
//							}
//							if (cuenta.getAceptaTerminoUso().contains("S")) {
//								documentoAceptadoService.guardaDocumentoGeneralAceptado(
//										cuentaCreada.getPersona().getIdentificacion(), cuentaCreada.getUsuario(), null,
//										4);
//								LOG.info("save: guardaDocumentoGeneralAceptado");
//							}
////							LOG.wait(3000);
//							return "ok";
//						} else {
//							return respuestaEmail;
//						}
//					} else if (persona.getTipoCliente().equals("PROMOTOR")) {
////						LOG.info("es promo");
////						LOG.wait(3000);
//						respuestaEmail = this.enviaEmailPromotor(cuenta.getEmail());
//						return "Es promotor";
//					}
////	        		else if(persona.getTipoCliente().equals("REPRESENTANTE")) {
////	        			respuestaEmail = this.envioEmailService.enviaEmailBeneficiario(cuenta.getEmail());
////		        		return "Es promotor";
////		        	}
//					else {
//						return "";
//					}
//				} else {
//					LOG.error("Error no se guardo la cuenta");
//					return "Error no se guardo la cuenta";
//				}
//
//			} catch (Exception e) {
//				throw new Exception(e.getMessage());
//				// LOG.error("save: Problema al crear la cuenta "+e.getMessage());
//				// return "Error interno al crear la cuenta";
//			}
//
//		}
//	}

	@Override
	@Transactional
	public String activaCuenta(String email) {
		LOG.info("activaCuenta: inicia activacion de cuenta");
		String response = null;
		Cuenta cuenta = new Cuenta();

		cuenta = cuentaRepository.findByCuenta(email);

		if (cuenta != null && cuenta.getEstado().contains("A")) {
			fechaActual = new Date();
			cuenta.setCuentaActiva("S");
			cuenta.setFechaModificacion(fechaActual);
			cuenta.setUsuarioModificacion(usuarioAdmin);
			try {
				cuentaRepository.save(cuenta);
				response = "Activada";
				LOG.info("activaCuenta: Cuenta activada con exito");
			} catch (DataAccessException e) {
				LOG.error("activaCuenta: Problema al activar la cuenta " + e.getMessage());
				return response = "500";
			}
		} else {
			LOG.info("activaCuenta: No se pudo activar la cuenta, usuario no exite");
			response = "No se pudo activar la cuenta, usuario no exite";
		}
		return response;
	}

	public boolean existeCuenta(String identificacion, String email, String usuario) {
		boolean existe = false;
		if (usuario != null) {
			if (cuentaRepository.findByCuentaUsuario(usuario) >= 1) {
				existe = true;
			}
		}
		if (identificacion != null && email != null) {
			if (cuentaRepository.existeCuenta(identificacion, email.toUpperCase()) >= 1) {
				existe = true;
			}
		}
		return existe;
	}

	@Override
	@Transactional(readOnly = true)
	public Cuenta findByCuenta(String email) {
		return cuentaRepository.findByCuenta(email);
	}

	@Override
	@Transactional
	public String confirmarCuenta(String idUsuario) {
		String response;
		try {
			Cuenta cuenta = cuentaRepository.findByCuentaIdCuenta(idUsuario);

			if (cuenta == null) {
				response = "Error no existe cuenta.";
				LOG.info(response + " ID: " + idUsuario);
			} else if (cuenta.getCuentaActiva().contains("S")) {
				response = "El usuario ya tiene la cuenta activada.";
				LOG.info(response + " ID: " + idUsuario);
			} else {
				fechaActual = new Date();

				cuenta.setCuentaActiva("S");
				cuenta.setFechaModificacion(fechaActual);
				cuenta.setUsuarioModificacion(usuarioAdmin);

				cuentaRepository.save(cuenta);
				LOG.info("confirmarCuenta: Su cuenta ha sido verificada exitosamente - ID:" + idUsuario);
				response = "Su cuenta ha sido verificada exitosamente";
			}

		} catch (DataAccessException e) {
			LOG.error("confirmarCuenta: Error confimar cuenta: " + e.getMessage());
			return "No se pudo confirmar la cuenta";
		}
		return response;
	}

	@Override
	@Transactional
	public Cuenta findByCuentaIdCuenta(String idUsuario) throws Exception {
		Cuenta cuenta;
		cuenta = cuentaRepository.findByCuentaIdCuenta(idUsuario);
		if (cuenta == null) {
			throw new Exception("No existe cuenta con id: " + idUsuario);
		}
		return cuenta;
	}

	public String enviaEmail(String email) {
		Cuenta cuenta = new Cuenta();
		// String mailMessage;
		String mensaje = null;
		ParametroResponse paramResponse;
		String urlToken;
		cuenta = this.findByCuenta(email);
		if (cuenta == null) {
			LOG.info("enviaEmail: No existe cuenta");
			mensaje = "No existe cuenta";
		} else if (cuenta.getCuentaActiva().contains("S")) {
			LOG.info("enviaEmail: El usuario ya tiene la cuenta activada.");
			mensaje = "El usuario ya tiene la cuenta activada.";
		} else {
			LOG.info("crear token.");
			try {
				Token confirmationToken = new Token();
				fechaActual = new Date();
				confirmationToken.setToken(UUID.randomUUID().toString());
				confirmationToken.setCuenta(cuenta);
				confirmationToken.setFechaCreacion(fechaActual);
				
				String generoToken = tokenService.generaToken(confirmationToken);
				if (generoToken.contains("ok")) {
					paramResponse = new ParametroResponse();
					LOG.info("token creado.");
					
					if(cuenta.getPersona().getTipoPersona().contains("NAT")) {
						LOG.info("token nat.");
						paramResponse = parametroService.findByParametroCodParametro("URL_ACTIVACTA");
						LOG.info("param.");
						urlToken = paramResponse.getValor().concat(confirmationToken.getToken());
						LOG.info("param set");
						LOG.info("enviando.");
						mensaje = envioEmailService.enviaEmailInvestor("ACTIVA_CUENTA", urlToken, cuenta.getEmail());
						LOG.info("enviado.");
						
						
					}if(cuenta.getPersona().getTipoPersona().contains("JUR")) {
						LOG.info("token jur.");
						paramResponse = parametroService.findByParametroCodParametro("URLFORPROM");
						urlToken = paramResponse.getValor().concat(confirmationToken.getToken());
						mensaje = envioEmailService.enviaEmailInvestor("ACTIVA_CUENTA", urlToken, cuenta.getEmail());
						
					}
					LOG.info("cierro ciclo.");
//					if (mensaje.contains("ok")) {
//						LOG.info("enviaEmail: email enviado");
//						mensaje = "ok";
//					}
					// mailMessage =
					// utils.tipoTextoValidaCuenta("CONFIRMACION",cuenta.getEmail(),confirmationToken.getToken(),paramResponse.getValor());
					// tokenService.envioMail("Validación de cuenta", cuenta.getEmail(),
					// mailMessage);

				} else {
					mensaje = generoToken;
				}
			} catch (Exception e) {
				LOG.error("enviaEmail: Error " + e.getMessage());
			}
		}
		LOG.info("retornando");
		return mensaje;
	}

	@Override
	public String enviaEmailPromotor(String email) {
		Cuenta cuenta = new Cuenta();
		// String mailMessage;
		String mensaje = null;
		ParametroResponse paramResponse;
		String url;

		cuenta = this.findByCuenta(email);

		if (cuenta == null) {
			LOG.info("enviaEmail: No existe cuenta");
			mensaje = "No existe cuenta";
		} else if (cuenta.getCuentaActiva().contains("S")) {
			LOG.info("enviaEmail: El usuario ya tiene la cuenta activada.");
			mensaje = "El usuario ya tiene la cuenta activada.";
		} else {
			try {
				Token confirmationToken = new Token();
				fechaActual = new Date();
				confirmationToken.setToken(UUID.randomUUID().toString());
				confirmationToken.setCuenta(cuenta);
				confirmationToken.setFechaCreacion(fechaActual);

				String generoToken = tokenService.generaToken(confirmationToken);
				if (generoToken.contains("ok")) {
					paramResponse = new ParametroResponse();
					paramResponse = parametroService.findByParametroCodParametro("URLFORPROM");

					// urlToken =
					// paramResponse.getValor().concat("=").concat(confirmationToken.getToken());
					url = paramResponse.getValor().concat(confirmationToken.getToken());

					mensaje = envioEmailService.enviaEmailPromotor("REG_PROM", url, cuenta.getEmail());
					if (mensaje.contains("ok")) {
						LOG.info("enviaEmail: email enviado");
						mensaje = "ok";
					}
					// mailMessage =
					// utils.tipoTextoValidaCuenta("CONFIRMACION",cuenta.getEmail(),confirmationToken.getToken(),paramResponse.getValor());
					// tokenService.envioMail("Validación de cuenta", cuenta.getEmail(),
					// mailMessage);

				} else {
					mensaje = generoToken;
				}
			} catch (Exception e) {
				LOG.error("enviaEmail: Error " + e.getMessage());
			}
		}
		return mensaje;
	}

	// @Override
	// @Transactional(readOnly = true)
	public String encriptaClave(String clave) {
		return bCryptPasswordEncoder.encode(clave);
	}

	@Override
	@Transactional
	public ResponseLogin validaLogin(RequestLogin login) {
		String response;
		Cuenta cuenta;
		ResponseLogin responseLogin = new ResponseLogin();

		try {
			cuenta = new Cuenta();
			LOG.warn("entra al try");
			cuenta = cuentaRepository.findByCuenta(login.getUsuario());

			if (cuenta != null) {
				LOG.warn("si existe");
				if (cuenta.getCuentaActiva().contains("S") && cuenta.getEstado().contains("A")) {
					if (bCryptPasswordEncoder.matches(login.getClave(), cuenta.getClave())) {

						// consulta datos de la cuenta
						responseLogin.setMensaje("ok");
						responseLogin.setUsuarioInterno(cuenta.getUsuario());
						responseLogin.setIdCuenta(cuenta.getIdCuenta());

						responseLogin.setTipoCliente(cuenta.getPersona().getTipoCliente());
						responseLogin.setTipoPersona(cuenta.getPersona().getTipoPersona());
						responseLogin.setIdentificacion(cuenta.getPersona().getIdentificacion());

						if (cuenta.getPersona().getTipoPersona().contains("NAT")) {
							responseLogin.setNombres(cuenta.getPersona().getNombres());
							responseLogin.setApellidos(cuenta.getPersona().getApellidos());
						} else {
							responseLogin.setNombres(cuenta.getPersona().getRazonSocial());
						}

						return responseLogin;
					} else {
						// en caso de error actualiza inicios_erroneos
						response = this.actualizaLoginError(cuenta);
						if (response == "BLOQUEO") {
							response = "Su cuenta ha sido bloqueda por 3 intentos de sesion erroneos, comuníquese con servicio a cliente: "
									+ "servicioalcliente@multiplolenders.com";
						} else {
							response = "Error de autenticación, verifique su usuario o contraseña.";
						}
					}
				} else {
					response = "Su cuenta no se encuentra activa.";
				}
			} else {
				response = "Cuenta no existe.";
			}
			responseLogin.setMensaje(response);
		} catch (Exception e) {
			LOG.error("Error al validar login " + e.getMessage());
			response = "Error en validar login";
			responseLogin.setMensaje(response);
			return responseLogin;
		}

		return responseLogin;
	}

	public String actualizaLoginError(Cuenta cuenta) {
		int cantErrores = 0;
		String response = null;

		try {

			if (cuenta.getCuentaActiva().contains("S")) {
				fechaActual = new Date();
				cantErrores = cuenta.getIniciosErroneos() + 1;

				if (cantErrores == 3) {
					cuenta.setIniciosErroneos(cantErrores);
					cuenta.setEstado("B");
					cuenta.setFechaModificacion(fechaActual);
					cuenta.setUsuarioModificacion(usuarioAdmin);
					response = "BLOQUEO";
				} else {
					cuenta.setIniciosErroneos(cantErrores);
					cuenta.setFechaModificacion(fechaActual);
					cuenta.setUsuarioModificacion(usuarioAdmin);
					response = "OK";
				}
				cuentaRepository.save(cuenta);
			}

		} catch (DataAccessException e) {
			LOG.error("actualizaLoginError: Error actualizar cuenta: " + e.getMessage());
			return "No se pudo actualizar la cuenta";
		}
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Cuenta> consultaCuentasRegistradas() {
		return cuentaRepository.consultaCuentasRegistradas();
	}

	public String loginError(RequestLogin credencial) {
		String response = null;

		Cuenta cuenta = new Cuenta();
		cuenta = cuentaRepository.findByCuenta(credencial.getUsuario());

		if (cuenta != null) {
			if (cuenta.getCuentaActiva().contains("S") && cuenta.getEstado().contains("A")) {
				if (!bCryptPasswordEncoder.matches(credencial.getClave(), cuenta.getClave())) {
					response = this.actualizaLoginError(cuenta);
					if (response == "BLOQUEO") {
						response = "Su cuenta ha sido bloqueda por 3 intentos de sesion erroneos, comuníquese con servicio a cliente: "
								+ "servicioalcliente@multiplolenders.com";
					} else {
						response = "Error de autenticación, verifique su usuario o contraseña.";
					}
				}
			} else {
				response = "Su cuenta no se encuentra activa.";
			}
		} else {
			response = "Cuenta no existe.";
		}
		return response;
	}

	public List<MenuResponse> generarMenu(Rol rol) {
		List<MenuOperacion> listMenu = new ArrayList<>();
		List<MenuResponse> listMenuRes = new ArrayList<>();
		if (!rol.getMenuOperaciones().isEmpty()) {
			rol.getMenuOperaciones().stream().forEach(m -> {

				if (m.getMenu().getIdMenu() == 1 || !m.getMenu().getSubMenu().isEmpty()) {
					MenuOperacion menuOpe = new MenuOperacion();
					MenuResponse menuRes = new MenuResponse();
					menuOpe = m;

					menuRes.setNombre(m.getMenu().getNombre());
					menuRes.setDescripcion(m.getMenu().getDescripcion());
					menuRes.setUrl(m.getMenu().getUrl());
					menuRes.setOrden(m.getMenu().getOrden());
					// LOG.info("->"+menuRes.getNombre()+"-"+m.getRol().getNombre());

					if (!m.getMenu().getSubMenu().isEmpty()) {
						List<MenuResponse> listSubMenuRes = new ArrayList<>();

						m.getMenu().getSubMenu().stream().forEach(subm -> {

							// LOG.info("==>"+subm.getNombre()+"-"+ m.getMenu().getNombre() );
							if (!subm.getMenuOperaciones().isEmpty()) {
								// filtra menu operaciones por rol
								subm.setMenuOperaciones(subm.getMenuOperaciones().stream()
										.filter(op -> op.getRol().getIdRol() == m.getRol().getIdRol())
										.filter(op -> op.getMenu().getIdMenu() == subm.getIdMenu())
										.collect(Collectors.toList()));

								MenuResponse subMenRes = null;
								List<OperacionResponse> listOperacionRes = new ArrayList<>();
								for (MenuOperacion mope : subm.getMenuOperaciones()) {
									subMenRes = new MenuResponse();

									subMenRes.setNombre(mope.getMenu().getNombre());
									subMenRes.setDescripcion(mope.getMenu().getDescripcion());
									subMenRes.setUrl(mope.getMenu().getUrl());
									subMenRes.setOrden(mope.getMenu().getOrden());
									subMenRes.setUrlIcono(mope.getMenu().getUrlIcono());
									// llena las operacione permitidas a la opcion del menu
									OperacionResponse operResponse = new OperacionResponse();
									operResponse.setNombre(mope.getOperacion().getNombre());
									operResponse.setDescripcion(mope.getOperacion().getDescripcion());
									listOperacionRes.add(operResponse);
								}

								if (subMenRes != null) {
									listSubMenuRes.add(subMenRes);
									subMenRes.setOperacion(listOperacionRes);
								}
							}

							menuRes.setSubMenu(listSubMenuRes);
						});
					}
					listMenuRes.add(menuRes);
					listMenu.add(menuOpe);
				}
			});

			rol.setMenuOperaciones(listMenu);
		}

		return listMenuRes;
	}

	public List<MenuResponse> generarMenuInt(RolInt rol) {
		List<MenuOperacionInt> listMenu = new ArrayList<>();
		List<MenuResponse> listMenuRes = new ArrayList<>();
		if (!rol.getMenuOperaciones().isEmpty()) {
			rol.getMenuOperaciones().stream().forEach(m -> {

				if (m.getMenu().getIdMenu() == 1 || !m.getMenu().getSubMenu().isEmpty()) {
					MenuOperacionInt menuOpe = new MenuOperacionInt();
					MenuResponse menuRes = new MenuResponse();
					menuOpe = m;

					menuRes.setNombre(m.getMenu().getNombre());
					menuRes.setDescripcion(m.getMenu().getDescripcion());
					menuRes.setUrl(m.getMenu().getUrl());
					menuRes.setOrden(m.getMenu().getOrden());
					// LOG.info("->"+menuRes.getNombre()+"-"+m.getRol().getNombre());

					if (!m.getMenu().getSubMenu().isEmpty()) {
						List<MenuResponse> listSubMenuRes = new ArrayList<>();

						m.getMenu().getSubMenu().stream().forEach(subm -> {

							// LOG.info("==>"+subm.getNombre()+"-"+ m.getMenu().getNombre() );
							if (!subm.getMenuOperaciones().isEmpty()) {
								// filtra menu operaciones por rol
								subm.setMenuOperaciones(subm.getMenuOperaciones().stream()
										.filter(op -> op.getRol().getIdRol() == m.getRol().getIdRol())
										.filter(op -> op.getMenu().getIdMenu() == subm.getIdMenu())
										.collect(Collectors.toList()));

								MenuResponse subMenRes = null;
								List<OperacionResponse> listOperacionRes = new ArrayList<>();
								for (MenuOperacion mope : subm.getMenuOperaciones()) {
									subMenRes = new MenuResponse();

									subMenRes.setNombre(mope.getMenu().getNombre());
									subMenRes.setDescripcion(mope.getMenu().getDescripcion());
									subMenRes.setUrl(mope.getMenu().getUrl());
									subMenRes.setOrden(mope.getMenu().getOrden());
									subMenRes.setUrlIcono(mope.getMenu().getUrlIcono());
									// llena las operacione permitidas a la opcion del menu
									OperacionResponse operResponse = new OperacionResponse();
									operResponse.setNombre(mope.getOperacion().getNombre());
									operResponse.setDescripcion(mope.getOperacion().getDescripcion());
									listOperacionRes.add(operResponse);
								}

								if (subMenRes != null) {
									listSubMenuRes.add(subMenRes);
									subMenRes.setOperacion(listOperacionRes);
								}
							}

							menuRes.setSubMenu(listSubMenuRes);
						});
					}
					listMenuRes.add(menuRes);
					listMenu.add(menuOpe);
				}
			});

			rol.setMenuOperaciones(listMenu);
		}

		return listMenuRes;
	}

	@Override
	@Transactional(readOnly = true)
	public CuentaRolResponse consultaCuentaRol(String nombreRol, String usuario) {
		// Conforma el response del menu segun el rol, es llamado desde el servcio de
		// login
		CuentaRolResponse cuentaRol = new CuentaRolResponse();
//		Rol rol = new Rol();
//		rol = rolRepository.consultaRol(nombreRol);
//		List<MenuOperacion> listMenu = new ArrayList<>();
//		List<MenuResponse> listMenuRes = new ArrayList<>();

//		if (!rol.getMenuOperaciones().isEmpty()) {
//			rol.getMenuOperaciones().stream().forEach(m -> {
//
//				if (m.getMenu().getIdMenu() == 1 || !m.getMenu().getSubMenu().isEmpty()) {
//					MenuOperacion menuOpe = new MenuOperacion();
//					MenuResponse menuRes = new MenuResponse();
//					menuOpe = m;
//
//					menuRes.setNombre(m.getMenu().getNombre());
//					menuRes.setDescripcion(m.getMenu().getDescripcion());
//					menuRes.setUrl(m.getMenu().getUrl());
//					menuRes.setOrden(m.getMenu().getOrden());
//					// LOG.info("->"+menuRes.getNombre()+"-"+m.getRol().getNombre());
//
//					if (!m.getMenu().getSubMenu().isEmpty()) {
//						List<MenuResponse> listSubMenuRes = new ArrayList<>();
//
//						m.getMenu().getSubMenu().stream().forEach(subm -> {
//
//							// LOG.info("==>"+subm.getNombre()+"-"+ m.getMenu().getNombre() );
//							if (!subm.getMenuOperaciones().isEmpty()) {
//								// filtra menu operaciones por rol
//								subm.setMenuOperaciones(subm.getMenuOperaciones().stream()
//										.filter(op -> op.getRol().getIdRol() == m.getRol().getIdRol())
//										.filter(op -> op.getMenu().getIdMenu() == subm.getIdMenu())
//										.collect(Collectors.toList()));
//
//								MenuResponse subMenRes = null;
//								List<OperacionResponse> listOperacionRes = new ArrayList<>();
//								for (MenuOperacion mope : subm.getMenuOperaciones()) {
//									subMenRes = new MenuResponse();
//
//									subMenRes.setNombre(mope.getMenu().getNombre());
//									subMenRes.setDescripcion(mope.getMenu().getDescripcion());
//									subMenRes.setUrl(mope.getMenu().getUrl());
//									subMenRes.setOrden(mope.getMenu().getOrden());
//									subMenRes.setUrlIcono(mope.getMenu().getUrlIcono());
//									// llena las operacione permitidas a la opcion del menu
//									OperacionResponse operResponse = new OperacionResponse();
//									operResponse.setNombre(mope.getOperacion().getNombre());
//									operResponse.setDescripcion(mope.getOperacion().getDescripcion());
//									listOperacionRes.add(operResponse);
//								}
//
//								if (subMenRes != null) {
//									listSubMenuRes.add(subMenRes);
//									subMenRes.setOperacion(listOperacionRes);
//								}
//							}
//
//							menuRes.setSubMenu(listSubMenuRes);
//						});
//					}
//					listMenuRes.add(menuRes);
//					listMenu.add(menuOpe);
//				}
//			});
//
//			rol.setMenuOperaciones(listMenu);
//		}
		// cuentaRol.setMenu(listMenuRes);

		// 1,4,5,6->id roles internos multiplos;
		CuentaInterno cuentaUserInterno = new CuentaInterno();
		cuentaUserInterno = cuentaUserInternoRepository.findByCuentaInterna(usuario);
		if (cuentaUserInterno != null) {

//			cuentaRol.setRuta(cuentaUserInterno.getRol().getRuta());
			cuentaRol.setIdentificacion(cuentaUserInterno.getIdCuenta());
			cuentaRol.setUsuarioInterno(cuentaUserInterno.getIdCuenta());
			cuentaRol.setNombres(cuentaUserInterno.getPersonalInterno().getNombres() + " "
					+ cuentaUserInterno.getPersonalInterno().getApellidos());
			cuentaRol.setUsuario(usuario);

//			cuentaRol.setTipoCliente(cuentaUserInterno.getRol().getNombre());
			List<RolesResponse> listaRol = new ArrayList<>();
			for (RolesInternos rolAux : cuentaUserInterno.getRoles()) {
				RolesResponse roles = new RolesResponse();
				roles.setNombre(rolAux.getRol().getNombre());
				// roles.setMenuOperaciones(rolAux.getRol().getMenuOperaciones());
				roles.setRuta(rolAux.getRol().getRuta());

				roles.setMenu(this.generarMenuInt(rolAux.getRol()));

				listaRol.add(roles);
			}
			cuentaRol.setRoles(listaRol);
		} else {

			Cuenta cuenta = new Cuenta();
			cuenta = cuentaRepository.findByCuenta(usuario);

			List<RolesResponse> listaRol = new ArrayList<>();
			for (Roles rolAux : cuenta.getRoles()) {
				RolesResponse roles = new RolesResponse();
				roles.setNombre(rolAux.getRol().getNombre());
				// roles.setMenuOperaciones(rolAux.getRol().getMenuOperaciones());
				roles.setMenu(this.generarMenu(rolAux.getRol()));
				roles.setRuta(rolAux.getRol().getRuta());
				listaRol.add(roles);
			}
			cuentaRol.setRoles(listaRol);
			cuentaRol.setIdentificacion(cuenta.getIdCuenta());
//			cuentaRol.setRuta(cuenta.getRol().getRuta());
			String[] nombres = cuenta.getPersona().getNombres().split(" ");
			String[] apes = cuenta.getPersona().getApellidos().split(" ");
			cuentaRol.setUsuarioInterno(cuenta.getUsuario());

			if (cuenta.getPersona().getTipoPersona().contains("NAT")) {
				cuentaRol.setNombres(nombres[0] + " " + apes[0]);
			} else {
				cuentaRol.setNombres(cuenta.getPersona().getRazonSocial());
			}
			cuentaRol.setUsuario(usuario);
			cuentaRol.setTipoCliente(cuenta.getPersona().getTipoCliente());
			cuentaRol.setTipoPersona(cuenta.getPersona().getTipoPersona());
		}

		return cuentaRol;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean verificaCuenta(String email) {
		boolean existe = false;
		if (cuentaRepository.existeCuenta(null, email.toUpperCase()) > 0) {
			existe = true;
		}
		return existe;
	}

	@Override
	@Transactional
	public String forgotPass(String mail, String identificacion) throws Exception {
		ParametroResponse paramUrl = null;
		String urlToken, message;
		Cuenta cuenta = null;
		Token tokenAux = null;
		cuenta = cuentaRepository.findByIdentificacion(identificacion);
		if (cuenta == null) {
			throw new Exception("No existe cuenta con la identificación: " + identificacion);
		}
		if (!cuenta.getEmail().equals(mail)) {
			throw new Exception("El Email no coincide con la identificación: " + identificacion);
		}

		tokenAux = tokenRepository.findByCuenta(cuenta);

		if (tokenAux != null) {
			tokenRepository.delete(tokenAux);
		}

		Token confirmationToken = new Token();
		fechaActual = new Date();
		confirmationToken.setToken(UUID.randomUUID().toString());
		confirmationToken.setCuenta(cuenta);
		confirmationToken.setFechaCreacion(fechaActual);
		String generoToken = tokenService.generaToken(confirmationToken);

		if (!generoToken.contains("ok")) {
			throw new Exception(generoToken);
		}
		paramUrl = new ParametroResponse();
		paramUrl = parametroService.findByParametroCodParametro("SWITCH_PASS");

		// urlToken =
		// paramUrl.getValor().concat("=").concat(confirmationToken.getToken());
		urlToken = paramUrl.getValor().concat(confirmationToken.getToken());

		message = envioEmailService.enviaEmailInvestor("SW_PASS", urlToken, cuenta.getEmail());
		return message;
	}

	@Override
	@Transactional
	public boolean validSwitchPass(String token) throws Exception {
		Token tokenAux = null;
		tokenAux = tokenService.findByToken(token);

		if (tokenAux == null) {
			throw new Exception("La sesion ha expirado");
		}

		// falta validar expiracion del token

		return true;
	}

	@Override
	@Transactional
	public String switchPass(String token, String pass) throws Exception {
		String mensaje = null;
		Token tokenAux = null;
		Cuenta cuenta = null;
		if (token == null && pass == null) {
			throw new Exception("Debe ingresar una contraseña");
		}
		tokenAux = tokenService.findByToken(token);
		if (tokenAux == null) {
			throw new Exception("La sesion ha expirado");
		}

		// falta validar expiracion del token
		try {
			cuenta = tokenAux.getCuenta();
			cuenta.setClave(this.encriptaClave(pass));
			cuentaRepository.save(cuenta);
			// metodo temporal eliminar token para validar
			tokenRepository.delete(tokenAux);
		} catch (Exception e) {
			throw new Exception("Error en base de datos: " + e);
		}
		return "Su clave ha sido cambiada con éxito";
	}

	@Override
	@Transactional
	public Object AsignarRoll(String idcuenta, Long roll) throws Exception {

		Cuenta cuenta = null;
		cuenta = this.findByCuentaIdCuenta(idcuenta);
		String response = null;
		for (Roles roles : cuenta.getRoles()) {
			if (roles.getRol().getIdRol().equals(roll)) {
				throw new CuentaException("El usuario ya tiene el roll asignado");
			}
		}
		Roles rolnuevo = new Roles();
		rolnuevo.setCuenta(cuenta);
		rolnuevo.setRol(Rol.builder().idRol(roll).build());
		cuenta.getRoles().add(rolnuevo);

		return "Rol Asignado Correctamente";
	}

	@Override
	@Transactional
	public Object setearRol() throws Exception {

		List<Cuenta> cuentas = null;
		cuentas = this.cuentaRepository.findAll();

//		for (Cuenta cuenta : cuentas) {
//			if(cuenta.getRoles().isEmpty()) {
//			Roles rol = new Roles();
//			RolesKeys key= new RolesKeys(cuenta.getRol().getIdRol() , cuenta.getIdCuenta());
//			rol.setId(key);
//			rol.setCuenta(cuenta);
////			rol.setCuentaInterna(cuentaInt);
//			
//			
//			
//			rol.setRol(cuenta.getRol());
//			rol.setUsuarioCreacion(new CuentaInterno("admin"));
//			cuenta.getRoles().add(rol);
//			}
//		}

		List<CuentaInterno> cuentaInterno = null;
		cuentaInterno = this.cuentaUserInternoRepository.findAll();

		for (CuentaInterno cuenta : cuentaInterno) {
			if (cuenta.getRoles().isEmpty()) {
				RolesInternos rol = new RolesInternos();
//			RolesIntKeys key = new RolesIntKeys(cuenta.getRolInt().getIdRol(), cuenta.getIdCuenta());
//			rol.setId(key);
				rol.setCuentaInterna(cuenta);
				// rol.setCuenta(new Cuenta("NotValue"));
				// cuenta.setRolInt(new
				// RolInt(cuenta.getRol().getIdRol(),cuenta.getRol().getNombre(),cuenta.getRol().getEstado(),cuenta.getRol().getRuta(),cuenta.getRol().getMenuOperaciones()));
//			rol.setRol(cuenta.getRolInt());
				rol.setUsuarioCreacion(new CuentaInterno("admin"));
				cuenta.getRoles().add(rol);
			}
		}

		return "Rol Asignado Correctamente";
	}

}
