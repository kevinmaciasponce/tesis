PGDMP     9    0                 |            potencia    13.11    15.1 5              0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    68028    potencia    DATABASE     }   CREATE DATABASE potencia WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Spanish_Ecuador.1252';
    DROP DATABASE potencia;
                postgres    false                        2615    68029 	   auspicios    SCHEMA        CREATE SCHEMA auspicios;
    DROP SCHEMA auspicios;
                postgres    false                        2615    68030    cuenta    SCHEMA        CREATE SCHEMA cuenta;
    DROP SCHEMA cuenta;
                postgres    false                        2615    68031    emails    SCHEMA        CREATE SCHEMA emails;
    DROP SCHEMA emails;
                postgres    false            	            2615    68032    footer    SCHEMA        CREATE SCHEMA footer;
    DROP SCHEMA footer;
                postgres    false            
            2615    68033 
   historicas    SCHEMA        CREATE SCHEMA historicas;
    DROP SCHEMA historicas;
                postgres    false                        2615    68034 	   inversion    SCHEMA        CREATE SCHEMA inversion;
    DROP SCHEMA inversion;
                postgres    false                        2615    68035    maestras    SCHEMA        CREATE SCHEMA maestras;
    DROP SCHEMA maestras;
                postgres    false                        2615    68036    maestras_auspicios    SCHEMA     "   CREATE SCHEMA maestras_auspicios;
     DROP SCHEMA maestras_auspicios;
                postgres    false                        2615    68037    multiplo    SCHEMA        CREATE SCHEMA multiplo;
    DROP SCHEMA multiplo;
                postgres    false                        2615    68038    multiplo_documentos    SCHEMA     #   CREATE SCHEMA multiplo_documentos;
 !   DROP SCHEMA multiplo_documentos;
                postgres    false                        2615    68039    negocio    SCHEMA        CREATE SCHEMA negocio;
    DROP SCHEMA negocio;
                postgres    false                        2615    68040    parametrizacion    SCHEMA        CREATE SCHEMA parametrizacion;
    DROP SCHEMA parametrizacion;
                postgres    false                        2615    68041    promotor    SCHEMA        CREATE SCHEMA promotor;
    DROP SCHEMA promotor;
                postgres    false                        2615    2200    public    SCHEMA     2   -- *not* creating schema, since initdb creates it
 2   -- *not* dropping schema, since initdb creates it
                postgres    false                       0    0    SCHEMA public    ACL     Q   REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;
                   postgres    false    4            �           1255    68042 >   pro_regenera_tbl_amortizacion(character varying, bigint, date) 	   PROCEDURE     :  CREATE PROCEDURE inversion.pro_regenera_tbl_amortizacion(cod_proyecto character varying, tipo_tabla bigint, f_efetctiva date)
    LANGUAGE plpgsql
    AS $$
declare
	c_solicitudes cursor(ccod_proyecto varchar) is
	 select s.numero_solicitud , s.tabla_amortizacion
	 	from inversion.mult_solicitudes s
	 where s.codigo_proyecto = ccod_proyecto
	 
	 and s.estado_actual = 'SATF';
	
	c_tbl_detalle cursor(num_solicitud bigint) is
	 select d.id_det_amortizacion, d.cuota, d.fecha_estimacion
		from inversion.mult_solicitudes s,
		 inversion.mult_tabla_amortizacion t,
		 inversion.mult_detalle_amortizacion d
	where s.numero_solicitud = num_solicitud
	
	and s.estado_actual = 'SATF'
	and t.id_tbl_amortizacion = s.tabla_amortizacion
	and t.id_tipo_tabla = tipo_tabla
	and t.estado = 'A'
	and d.id_tbl_amortizacion = t.id_tbl_amortizacion
	and d.estado = 'A'
	order by cuota asc;

	--consulta la tabla amortizacion global del proyecto
	c_tbl_total_detalle cursor(id_amortizacion bigint) is
	 select d.id_det_amortizacion, d.cuota, d.fecha_estimacion
		from inversion.mult_tabla_amortizacion t,
		 inversion.mult_detalle_amortizacion d
	where t.id_tbl_amortizacion= id_amortizacion
	and t.id_tipo_tabla = tipo_tabla
	and t.estado = 'A'
	and d.id_tbl_amortizacion = t.id_tbl_amortizacion
	and d.estado = 'A'
	order by cuota asc;
    
    --consulta la tabla amortizacion global del proyecto
	c_proyecto cursor(ccod_proyecto varchar) is
	 select p.tabla_amortizacion
		from promotor.mult_proyectos p
	where p.id_proyecto= ccod_proyecto;

    cnt integer := 0;
    fecha_cobro_aux date;
    id_tbl bigint;
	--msj_error varchar(200);
begin

	if tipo_tabla = 3 then
		cnt := 0;
		fecha_cobro_aux := f_efetctiva;
        for py in c_proyecto (cod_proyecto)loop
        id_tbl= py.tabla_amortizacion;
        end loop;
          for dt in c_tbl_total_detalle(id_tbl) loop

              if dt.cuota > 0 then
                  cnt := cnt + 1;
                  fecha_cobro_aux := fecha_cobro_aux + 30;
                   if date_part('dow',fecha_cobro_aux) = 6 then 
                      fecha_cobro_aux := fecha_cobro_aux + 2;
                   end if;
                   if date_part('dow',fecha_cobro_aux) = 0 then 
                      fecha_cobro_aux := fecha_cobro_aux + 1;
                   end if;
                  --raise notice 'fecha_cobro_aux: %',fecha_cobro_aux;

                  begin
                      update inversion.mult_detalle_amortizacion
                          set fecha_estimacion = fecha_cobro_aux
                      where id_det_amortizacion = dt.id_det_amortizacion
                      and estado = 'A';

                  exception when others then
                      --msj_error := msj_error || ' manejar los errores';
                      --guardar en la tabla => historicas.mult_bitacora_procesos
                  end;
                  if cnt = 24 then
                      commit;
                  end if;
              end if;
            end loop;
		
		--actualiza la fechaEfectiva cabecera
		update inversion.mult_tabla_amortizacion
			set fecha_efectiva = f_efetctiva
		where id_tbl_amortizacion = id_tbl
		and id_tipo_tabla = tipo_tabla
		and estado = 'A';
		
	elsif tipo_tabla in (1,2)  then
		--consulta todas las solicitudes por proyecto
		for sol in c_solicitudes(cod_proyecto) loop
			fecha_cobro_aux := f_efetctiva;
			--se consultar la tabla de amortizacion por numero de solicitud	y tipo tabla
			for d in c_tbl_detalle(sol.numero_solicitud) loop
				if d.cuota > 0 then
					cnt := cnt + 1;
					fecha_cobro_aux := fecha_cobro_aux + 30;
                     if date_part('dow',fecha_cobro_aux) = 6 then 
                        fecha_cobro_aux := fecha_cobro_aux + 2;
                     end if;
                     if date_part('dow',fecha_cobro_aux) = 0 then 
                        fecha_cobro_aux := fecha_cobro_aux + 1;
                     end if;
					--raise notice 'fecha_cobro_aux: %',fecha_cobro_aux;

					begin
						update inversion.mult_detalle_amortizacion
							set fecha_estimacion = fecha_cobro_aux
						where id_det_amortizacion = d.id_det_amortizacion
						and estado = 'A';

					exception when others then
						--msj_error := msj_error || ' manejar los errores';
						--guardar en la tabla => historicas.mult_bitacora_procesos
					end;
					if cnt = 24 then
						--commit;
					end if;
				end if;
			end loop;
			--actualiza la fechaEfectiva cabecera
			update inversion.mult_tabla_amortizacion
				set fecha_efectiva = f_efetctiva
			where id_tbl_amortizacion = sol.tabla_amortizacion
			and id_tipo_tabla = tipo_tabla
			and estado = 'A';

		end loop;
		
	end if;
end;$$;
 }   DROP PROCEDURE inversion.pro_regenera_tbl_amortizacion(cod_proyecto character varying, tipo_tabla bigint, f_efetctiva date);
    	   inversion          postgres    false    11            �            1259    68043    mult_auspicios    TABLE     �  CREATE TABLE auspicios.mult_auspicios (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    fecha_generacion timestamp without time zone,
    observacion character varying(100),
    presupuesto_recaudado numeric(12,2) NOT NULL,
    presupuesto_solicitado numeric(12,2) NOT NULL,
    beneficiario character varying(255) NOT NULL,
    estado character varying(5) NOT NULL,
    id_valoracion bigint NOT NULL
);
 %   DROP TABLE auspicios.mult_auspicios;
    	   auspicios         heap    postgres    false    6            �            1259    68046    mult_auspicios_estados    TABLE     �   CREATE TABLE auspicios.mult_auspicios_estados (
    id_estado character varying(5) NOT NULL,
    descripcion character varying(50) NOT NULL,
    detalle character varying(100) NOT NULL,
    estado character varying(1) NOT NULL
);
 -   DROP TABLE auspicios.mult_auspicios_estados;
    	   auspicios         heap    postgres    false    6            �            1259    68049    mult_auspicios_id_seq    SEQUENCE     �   ALTER TABLE auspicios.mult_auspicios ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME auspicios.mult_auspicios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   auspicios          postgres    false    213    6            �            1259    68051    mult_auspicios_recompesas    TABLE     �   CREATE TABLE auspicios.mult_auspicios_recompesas (
    id bigint NOT NULL,
    categoria character varying(20) NOT NULL,
    detalle character varying(200) NOT NULL,
    porcentaje character varying(20) NOT NULL,
    auspicio bigint NOT NULL
);
 0   DROP TABLE auspicios.mult_auspicios_recompesas;
    	   auspicios         heap    postgres    false    6            �            1259    68054     mult_auspicios_recompesas_id_seq    SEQUENCE     �   ALTER TABLE auspicios.mult_auspicios_recompesas ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME auspicios.mult_auspicios_recompesas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   auspicios          postgres    false    216    6            �            1259    68056    mult_auspicios_torneos    TABLE     �   CREATE TABLE auspicios.mult_auspicios_torneos (
    id bigint NOT NULL,
    fecha date,
    nombre_torneo character varying(255),
    auspicio bigint NOT NULL,
    pais bigint
);
 -   DROP TABLE auspicios.mult_auspicios_torneos;
    	   auspicios         heap    postgres    false    6            �            1259    68059    mult_auspicios_torneos_id_seq    SEQUENCE     �   ALTER TABLE auspicios.mult_auspicios_torneos ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME auspicios.mult_auspicios_torneos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   auspicios          postgres    false    218    6            �            1259    68061    mult_auspicios_valoracion    TABLE       CREATE TABLE auspicios.mult_auspicios_valoracion (
    id bigint NOT NULL,
    activo boolean,
    anio integer NOT NULL,
    bianual boolean NOT NULL,
    calificacion character varying(50) NOT NULL,
    fecha_caducidad date NOT NULL,
    fecha_calificacion date NOT NULL,
    presupuesto_aprobado numeric(12,2) NOT NULL,
    presupuesto_recaudado numeric(12,2) NOT NULL,
    presupuesto_restante numeric(12,2) NOT NULL,
    ruta_documento character varying(300) NOT NULL,
    beneficiario character varying(255) NOT NULL
);
 0   DROP TABLE auspicios.mult_auspicios_valoracion;
    	   auspicios         heap    postgres    false    6            �            1259    68067     mult_auspicios_valoracion_id_seq    SEQUENCE     �   ALTER TABLE auspicios.mult_auspicios_valoracion ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME auspicios.mult_auspicios_valoracion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   auspicios          postgres    false    6    220            �            1259    68069    mult_beneficiario    TABLE       CREATE TABLE auspicios.mult_beneficiario (
    id character varying(255) NOT NULL,
    ruta_foto1 character varying(300),
    ruta_foto2 character varying(300),
    categoria bigint NOT NULL,
    disciplina bigint NOT NULL,
    modalidad bigint NOT NULL,
    id_persona character varying(13) NOT NULL,
    id_representante character varying(13),
    activo boolean,
    correo character varying(255) NOT NULL,
    perfil character varying(700) NOT NULL,
    titulo_actual character varying(50),
    cuenta_bancaria bigint
);
 (   DROP TABLE auspicios.mult_beneficiario;
    	   auspicios         heap    postgres    false    6            �            1259    68075    mult_titulos_deportivos    TABLE     @  CREATE TABLE auspicios.mult_titulos_deportivos (
    id bigint NOT NULL,
    anio_titulo integer,
    nombre_competencia character varying,
    otros character varying,
    ranking_internacional character varying,
    ranking_nacional character varying,
    beneficiario character varying(255),
    disciplina bigint
);
 .   DROP TABLE auspicios.mult_titulos_deportivos;
    	   auspicios         heap    postgres    false    6            �            1259    68081    mult_titulos_deportivos_id_seq    SEQUENCE     �   ALTER TABLE auspicios.mult_titulos_deportivos ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME auspicios.mult_titulos_deportivos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   auspicios          postgres    false    6    223            x           1259    85950    ptn_auspicios    TABLE     �  CREATE TABLE auspicios.ptn_auspicios (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    fecha_generacion timestamp without time zone,
    observacion character varying(100),
    presupuesto_recaudado numeric(12,2) NOT NULL,
    presupuesto_solicitado numeric(12,2) NOT NULL,
    beneficiario character varying(255) NOT NULL,
    estado character varying(5) NOT NULL,
    id_valoracion bigint NOT NULL
);
 $   DROP TABLE auspicios.ptn_auspicios;
    	   auspicios         heap    postgres    false    6            y           1259    85955    ptn_auspicios_estados    TABLE     �   CREATE TABLE auspicios.ptn_auspicios_estados (
    id_estado character varying(5) NOT NULL,
    descripcion character varying(50) NOT NULL,
    detalle character varying(100) NOT NULL,
    estado character varying(1) NOT NULL
);
 ,   DROP TABLE auspicios.ptn_auspicios_estados;
    	   auspicios         heap    postgres    false    6            w           1259    85948    ptn_auspicios_id_seq    SEQUENCE     �   ALTER TABLE auspicios.ptn_auspicios ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME auspicios.ptn_auspicios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   auspicios          postgres    false    376    6            {           1259    85962    ptn_auspicios_recompesas    TABLE     �   CREATE TABLE auspicios.ptn_auspicios_recompesas (
    id bigint NOT NULL,
    categoria character varying(20),
    detalle character varying(200),
    porcentaje character varying(20) NOT NULL,
    auspicio bigint NOT NULL
);
 /   DROP TABLE auspicios.ptn_auspicios_recompesas;
    	   auspicios         heap    postgres    false    6            z           1259    85960    ptn_auspicios_recompesas_id_seq    SEQUENCE     �   ALTER TABLE auspicios.ptn_auspicios_recompesas ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME auspicios.ptn_auspicios_recompesas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   auspicios          postgres    false    379    6            }           1259    85969    ptn_auspicios_torneos    TABLE     �   CREATE TABLE auspicios.ptn_auspicios_torneos (
    id bigint NOT NULL,
    fecha date,
    nombre_torneo character varying(50),
    auspicio bigint NOT NULL,
    pais bigint
);
 ,   DROP TABLE auspicios.ptn_auspicios_torneos;
    	   auspicios         heap    postgres    false    6            |           1259    85967    ptn_auspicios_torneos_id_seq    SEQUENCE     �   ALTER TABLE auspicios.ptn_auspicios_torneos ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME auspicios.ptn_auspicios_torneos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   auspicios          postgres    false    381    6                       1259    85976    ptn_auspicios_valoracion    TABLE       CREATE TABLE auspicios.ptn_auspicios_valoracion (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    anio integer NOT NULL,
    bianual boolean NOT NULL,
    calificacion character varying(50) NOT NULL,
    fecha_caducidad date NOT NULL,
    fecha_calificacion date NOT NULL,
    presupuesto_aprobado numeric(12,2) NOT NULL,
    presupuesto_recaudado numeric(12,2) NOT NULL,
    presupuesto_restante numeric(12,2) NOT NULL,
    ruta_documento character varying(300) NOT NULL,
    beneficiario character varying(255) NOT NULL
);
 /   DROP TABLE auspicios.ptn_auspicios_valoracion;
    	   auspicios         heap    postgres    false    6            ~           1259    85974    ptn_auspicios_valoracion_id_seq    SEQUENCE     �   ALTER TABLE auspicios.ptn_auspicios_valoracion ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME auspicios.ptn_auspicios_valoracion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   auspicios          postgres    false    383    6            �           1259    85984    ptn_beneficiario    TABLE       CREATE TABLE auspicios.ptn_beneficiario (
    id character varying(255) NOT NULL,
    activo boolean,
    correo character varying(255) NOT NULL,
    perfil character varying(700) NOT NULL,
    ruta_foto1 character varying(300),
    ruta_foto2 character varying(300),
    titulo_actual character varying(50),
    categoria bigint NOT NULL,
    cuenta_bancaria bigint,
    disciplina bigint NOT NULL,
    modalidad bigint NOT NULL,
    id_persona character varying(13) NOT NULL,
    id_representante character varying(13)
);
 '   DROP TABLE auspicios.ptn_beneficiario;
    	   auspicios         heap    postgres    false    6            �           1259    85994    ptn_titulos_deportivos    TABLE     O  CREATE TABLE auspicios.ptn_titulos_deportivos (
    id bigint NOT NULL,
    anio_titulo integer,
    nombre_competencia character varying(50),
    otros character varying(50),
    ranking_internacional character varying(50),
    ranking_nacional character varying(50),
    beneficiario character varying(255),
    disciplina bigint
);
 -   DROP TABLE auspicios.ptn_titulos_deportivos;
    	   auspicios         heap    postgres    false    6            �           1259    85992    ptn_titulos_deportivos_id_seq    SEQUENCE     �   ALTER TABLE auspicios.ptn_titulos_deportivos ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME auspicios.ptn_titulos_deportivos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   auspicios          postgres    false    386    6            �            1259    68083    mult_cuentas    TABLE     V  CREATE TABLE cuenta.mult_cuentas (
    id_cuenta character varying(50) NOT NULL,
    acepta_politica_privacidad character varying(1) NOT NULL,
    acepta_recibir_informacion character varying(1) NOT NULL,
    acepta_termino_uso character varying(1) NOT NULL,
    clave character varying(100) NOT NULL,
    cuenta_activa character varying(1),
    email character varying(100) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    inicios_erroneos integer,
    tipo_contacto character varying(10) NOT NULL,
    usuario character varying(50) NOT NULL,
    usuario_contacto character varying(50) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    id_persona character varying(13)
);
     DROP TABLE cuenta.mult_cuentas;
       cuenta         heap    postgres    false    7            �            1259    68086 	   mult_menu    TABLE     u  CREATE TABLE cuenta.mult_menu (
    id_menu bigint NOT NULL,
    descripcion character varying(150) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    nombre character varying(50) NOT NULL,
    orden integer NOT NULL,
    url character varying(250),
    id_padre bigint,
    url_icono character varying(250)
);
    DROP TABLE cuenta.mult_menu;
       cuenta         heap    postgres    false    7            �            1259    68092    mult_menu_id_menu_seq    SEQUENCE     �   ALTER TABLE cuenta.mult_menu ALTER COLUMN id_menu ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_menu_id_menu_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    226            �            1259    68094    mult_menu_operacion    TABLE     �   CREATE TABLE cuenta.mult_menu_operacion (
    id_menu bigint NOT NULL,
    id_operacion bigint NOT NULL,
    id_rol bigint NOT NULL
);
 '   DROP TABLE cuenta.mult_menu_operacion;
       cuenta         heap    postgres    false    7            �            1259    68097    mult_operaciones    TABLE     
  CREATE TABLE cuenta.mult_operaciones (
    id_operacion bigint NOT NULL,
    descripcion character varying(150) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    nombre character varying(50) NOT NULL
);
 $   DROP TABLE cuenta.mult_operaciones;
       cuenta         heap    postgres    false    7            �            1259    68100 !   mult_operaciones_id_operacion_seq    SEQUENCE     �   ALTER TABLE cuenta.mult_operaciones ALTER COLUMN id_operacion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_operaciones_id_operacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    229            �            1259    68102    mult_pers_cuentas    TABLE     H  CREATE TABLE cuenta.mult_pers_cuentas (
    id_pers_cuenta bigint NOT NULL,
    estado character varying(1) NOT NULL,
    numero_cuenta character varying(20) NOT NULL,
    tipo_cuenta character varying(10) NOT NULL,
    titular character varying(100) NOT NULL,
    id_banco bigint NOT NULL,
    persona character varying(13)
);
 %   DROP TABLE cuenta.mult_pers_cuentas;
       cuenta         heap    postgres    false    7            �            1259    68105 $   mult_pers_cuentas_id_pers_cuenta_seq    SEQUENCE     �   ALTER TABLE cuenta.mult_pers_cuentas ALTER COLUMN id_pers_cuenta ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_pers_cuentas_id_pers_cuenta_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    231            �            1259    68107    mult_pers_documentos    TABLE     =  CREATE TABLE cuenta.mult_pers_documentos (
    id_documento bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    nombre character varying(100) NOT NULL,
    nombre_post character varying(100),
    ruta character varying(200) NOT NULL,
    ruta_post character varying(200),
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    id_tipo_documento bigint NOT NULL,
    id_info_adicional bigint NOT NULL
);
 (   DROP TABLE cuenta.mult_pers_documentos;
       cuenta         heap    postgres    false    7            �            1259    68113 %   mult_pers_documentos_id_documento_seq    SEQUENCE     �   ALTER TABLE cuenta.mult_pers_documentos ALTER COLUMN id_documento ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_pers_documentos_id_documento_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    233            �            1259    68115    mult_pers_domicilios    TABLE     <  CREATE TABLE cuenta.mult_pers_domicilios (
    id_domicilio bigint NOT NULL,
    direccion character varying(200) NOT NULL,
    estado character varying(1) NOT NULL,
    numero_domicilio character varying(10) NOT NULL,
    sector character varying(100),
    id_ciudad bigint NOT NULL,
    id_pais bigint NOT NULL
);
 (   DROP TABLE cuenta.mult_pers_domicilios;
       cuenta         heap    postgres    false    7            �            1259    68118 %   mult_pers_domicilios_id_domicilio_seq    SEQUENCE     �   ALTER TABLE cuenta.mult_pers_domicilios ALTER COLUMN id_domicilio ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_pers_domicilios_id_domicilio_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    235    7            �            1259    68120    mult_pers_est_finan    TABLE     J  CREATE TABLE cuenta.mult_pers_est_finan (
    id_est_finan bigint NOT NULL,
    egreso_anual numeric(12,2) NOT NULL,
    estado character varying(1) NOT NULL,
    ingreso_anual numeric(12,2) NOT NULL,
    total_activo numeric(12,2) NOT NULL,
    total_pasivo numeric(12,2) NOT NULL,
    total_patrimonio numeric(12,2) NOT NULL
);
 '   DROP TABLE cuenta.mult_pers_est_finan;
       cuenta         heap    postgres    false    7            �            1259    68123 $   mult_pers_est_finan_id_est_finan_seq    SEQUENCE     �   ALTER TABLE cuenta.mult_pers_est_finan ALTER COLUMN id_est_finan ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_pers_est_finan_id_est_finan_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    237            �            1259    68125    mult_pers_firmas    TABLE       CREATE TABLE cuenta.mult_pers_firmas (
    id_firma bigint NOT NULL,
    email character varying(100) NOT NULL,
    estado character varying(1) NOT NULL,
    identificacion character varying(13) NOT NULL,
    nombres_completos character varying(100) NOT NULL
);
 $   DROP TABLE cuenta.mult_pers_firmas;
       cuenta         heap    postgres    false    7            �            1259    68128    mult_pers_firmas_id_firma_seq    SEQUENCE     �   ALTER TABLE cuenta.mult_pers_firmas ALTER COLUMN id_firma ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_pers_firmas_id_firma_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    239            �            1259    68130    mult_pers_info_adicional    TABLE     �  CREATE TABLE cuenta.mult_pers_info_adicional (
    id_info_adicional bigint NOT NULL,
    actividad_economica character varying(100),
    cargo_persona character varying(15),
    estado character varying(1) NOT NULL,
    estado_civil character varying(15),
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    fecha_registro date NOT NULL,
    fuente_ingresos character varying(15) NOT NULL,
    numero_telefono character varying(10),
    pais_domicilio_fiscal bigint,
    residente_domicilio_fiscal character varying(1) NOT NULL,
    sexo character varying(1),
    id_domicilio bigint NOT NULL,
    id_est_finan_jur bigint,
    id_firma_jur bigint,
    id_repre_legal_jur bigint,
    id_tipo_cuenta bigint NOT NULL,
    id_persona character varying(13) NOT NULL,
    id_doc_identificacion bigint,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50)
);
 ,   DROP TABLE cuenta.mult_pers_info_adicional;
       cuenta         heap    postgres    false    7            �            1259    68133 .   mult_pers_info_adicional_id_info_adicional_seq    SEQUENCE       ALTER TABLE cuenta.mult_pers_info_adicional ALTER COLUMN id_info_adicional ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_pers_info_adicional_id_info_adicional_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    241            �            1259    68135    mult_pers_repre_legal    TABLE     s  CREATE TABLE cuenta.mult_pers_repre_legal (
    id_repre_legal bigint NOT NULL,
    apellidos character varying(50) NOT NULL,
    cargo character varying(15) NOT NULL,
    direccion_domicilio character varying(200) NOT NULL,
    email character varying(100) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_inicio_actividad date NOT NULL,
    identificacion character varying(13) NOT NULL,
    nombres character varying(50) NOT NULL,
    numero_celular character varying(10) NOT NULL,
    numero_domicilio character varying(10) NOT NULL,
    telefono character varying(10) NOT NULL,
    id_pais bigint NOT NULL
);
 )   DROP TABLE cuenta.mult_pers_repre_legal;
       cuenta         heap    postgres    false    7            �            1259    68138 (   mult_pers_repre_legal_id_repre_legal_seq    SEQUENCE       ALTER TABLE cuenta.mult_pers_repre_legal ALTER COLUMN id_repre_legal ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_pers_repre_legal_id_repre_legal_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    243            �            1259    68140    mult_personas    TABLE     �  CREATE TABLE cuenta.mult_personas (
    identificacion character varying(13) NOT NULL,
    anio_inicio_actividad integer,
    apellidos character varying(50),
    cargo_contacto character varying(50),
    email_contacto character varying(100),
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    fecha_nacimiento date,
    nacionalidad character varying(2),
    nombre_contacto character varying(100),
    nombres character varying(50),
    numero_celular character varying(14) NOT NULL,
    razon_social character varying(100),
    tipo_cliente character varying(15),
    tipo_identificacion character varying(3) NOT NULL,
    tipo_persona character varying(3) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    genero character varying(1)
);
 !   DROP TABLE cuenta.mult_personas;
       cuenta         heap    postgres    false    7            �            1259    68146 
   mult_roles    TABLE     �   CREATE TABLE cuenta.mult_roles (
    id_rol bigint NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(50) NOT NULL,
    ruta character varying(250)
);
    DROP TABLE cuenta.mult_roles;
       cuenta         heap    postgres    false    7            �            1259    68149    mult_roles_cuentas    TABLE       CREATE TABLE cuenta.mult_roles_cuentas (
    id_rol bigint NOT NULL,
    fecha_creacion timestamp without time zone,
    usuari_creacion character varying(50) NOT NULL,
    cuenta character varying(50),
    cuenta_interna character varying(50),
    rol bigint NOT NULL
);
 &   DROP TABLE cuenta.mult_roles_cuentas;
       cuenta         heap    postgres    false    7            �            1259    68152    mult_roles_cuentas_id_rol_seq    SEQUENCE     �   ALTER TABLE cuenta.mult_roles_cuentas ALTER COLUMN id_rol ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_roles_cuentas_id_rol_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    247            �            1259    68154    mult_roles_id_rol_seq    SEQUENCE     �   ALTER TABLE cuenta.mult_roles ALTER COLUMN id_rol ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_roles_id_rol_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    246            �            1259    68156 
   mult_token    TABLE     �   CREATE TABLE cuenta.mult_token (
    id_token bigint NOT NULL,
    fecha_creacion timestamp without time zone,
    token character varying(255),
    id_cuenta character varying(50) NOT NULL
);
    DROP TABLE cuenta.mult_token;
       cuenta         heap    postgres    false    7            �            1259    68159    mult_token_id_token_seq    SEQUENCE     �   ALTER TABLE cuenta.mult_token ALTER COLUMN id_token ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.mult_token_id_token_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    250    7            �           1259    86003    ptn_cuentas    TABLE     U  CREATE TABLE cuenta.ptn_cuentas (
    id_cuenta character varying(50) NOT NULL,
    acepta_politica_privacidad character varying(1) NOT NULL,
    acepta_recibir_informacion character varying(1) NOT NULL,
    acepta_termino_uso character varying(1) NOT NULL,
    clave character varying(100) NOT NULL,
    cuenta_activa character varying(1),
    email character varying(100) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    inicios_erroneos integer,
    tipo_contacto character varying(10) NOT NULL,
    usuario character varying(50) NOT NULL,
    usuario_contacto character varying(50) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    id_persona character varying(13)
);
    DROP TABLE cuenta.ptn_cuentas;
       cuenta         heap    postgres    false    7            �           1259    86010    ptn_menu    TABLE     t  CREATE TABLE cuenta.ptn_menu (
    id_menu bigint NOT NULL,
    descripcion character varying(150) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    nombre character varying(50) NOT NULL,
    orden integer NOT NULL,
    url character varying(250),
    url_icono character varying(250),
    id_padre bigint
);
    DROP TABLE cuenta.ptn_menu;
       cuenta         heap    postgres    false    7            �           1259    86008    ptn_menu_id_menu_seq    SEQUENCE     �   ALTER TABLE cuenta.ptn_menu ALTER COLUMN id_menu ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.ptn_menu_id_menu_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    389    7            �           1259    86018    ptn_menu_operacion    TABLE     �   CREATE TABLE cuenta.ptn_menu_operacion (
    id_menu bigint NOT NULL,
    id_operacion bigint NOT NULL,
    id_rol bigint NOT NULL
);
 &   DROP TABLE cuenta.ptn_menu_operacion;
       cuenta         heap    postgres    false    7            �           1259    86025    ptn_operaciones    TABLE     	  CREATE TABLE cuenta.ptn_operaciones (
    id_operacion bigint NOT NULL,
    descripcion character varying(150) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    nombre character varying(50) NOT NULL
);
 #   DROP TABLE cuenta.ptn_operaciones;
       cuenta         heap    postgres    false    7            �           1259    86023     ptn_operaciones_id_operacion_seq    SEQUENCE     �   ALTER TABLE cuenta.ptn_operaciones ALTER COLUMN id_operacion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.ptn_operaciones_id_operacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    392    7            �           1259    86032    ptn_pers_cuentas    TABLE     P  CREATE TABLE cuenta.ptn_pers_cuentas (
    id_pers_cuenta bigint NOT NULL,
    estado character varying(1) NOT NULL,
    numero_cuenta character varying(20) NOT NULL,
    tipo_cuenta character varying(10) NOT NULL,
    titular character varying(100) NOT NULL,
    id_banco bigint NOT NULL,
    persona character varying(13) NOT NULL
);
 $   DROP TABLE cuenta.ptn_pers_cuentas;
       cuenta         heap    postgres    false    7            �           1259    86030 #   ptn_pers_cuentas_id_pers_cuenta_seq    SEQUENCE     �   ALTER TABLE cuenta.ptn_pers_cuentas ALTER COLUMN id_pers_cuenta ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.ptn_pers_cuentas_id_pers_cuenta_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    394            �           1259    86039    ptn_pers_documentos    TABLE     <  CREATE TABLE cuenta.ptn_pers_documentos (
    id_documento bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    nombre character varying(100) NOT NULL,
    nombre_post character varying(100),
    ruta character varying(200) NOT NULL,
    ruta_post character varying(200),
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    id_tipo_documento bigint NOT NULL,
    id_info_adicional bigint NOT NULL
);
 '   DROP TABLE cuenta.ptn_pers_documentos;
       cuenta         heap    postgres    false    7            �           1259    86037 $   ptn_pers_documentos_id_documento_seq    SEQUENCE     �   ALTER TABLE cuenta.ptn_pers_documentos ALTER COLUMN id_documento ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.ptn_pers_documentos_id_documento_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    396            �           1259    86049    ptn_pers_domicilios    TABLE     ;  CREATE TABLE cuenta.ptn_pers_domicilios (
    id_domicilio bigint NOT NULL,
    direccion character varying(200) NOT NULL,
    estado character varying(1) NOT NULL,
    numero_domicilio character varying(10) NOT NULL,
    sector character varying(100),
    id_ciudad bigint NOT NULL,
    id_pais bigint NOT NULL
);
 '   DROP TABLE cuenta.ptn_pers_domicilios;
       cuenta         heap    postgres    false    7            �           1259    86047 $   ptn_pers_domicilios_id_domicilio_seq    SEQUENCE     �   ALTER TABLE cuenta.ptn_pers_domicilios ALTER COLUMN id_domicilio ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.ptn_pers_domicilios_id_domicilio_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    7    398            �           1259    86056    ptn_pers_est_finan    TABLE     I  CREATE TABLE cuenta.ptn_pers_est_finan (
    id_est_finan bigint NOT NULL,
    egreso_anual numeric(12,2) NOT NULL,
    estado character varying(1) NOT NULL,
    ingreso_anual numeric(12,2) NOT NULL,
    total_activo numeric(12,2) NOT NULL,
    total_pasivo numeric(12,2) NOT NULL,
    total_patrimonio numeric(12,2) NOT NULL
);
 &   DROP TABLE cuenta.ptn_pers_est_finan;
       cuenta         heap    postgres    false    7            �           1259    86054 #   ptn_pers_est_finan_id_est_finan_seq    SEQUENCE     �   ALTER TABLE cuenta.ptn_pers_est_finan ALTER COLUMN id_est_finan ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.ptn_pers_est_finan_id_est_finan_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    400    7            �           1259    86063    ptn_pers_firmas    TABLE       CREATE TABLE cuenta.ptn_pers_firmas (
    id_firma bigint NOT NULL,
    email character varying(100) NOT NULL,
    estado character varying(1) NOT NULL,
    identificacion character varying(13) NOT NULL,
    nombres_completos character varying(100) NOT NULL
);
 #   DROP TABLE cuenta.ptn_pers_firmas;
       cuenta         heap    postgres    false    7            �           1259    86061    ptn_pers_firmas_id_firma_seq    SEQUENCE     �   ALTER TABLE cuenta.ptn_pers_firmas ALTER COLUMN id_firma ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.ptn_pers_firmas_id_firma_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    402    7            �           1259    86070    ptn_pers_info_adicional    TABLE     �  CREATE TABLE cuenta.ptn_pers_info_adicional (
    id_info_adicional bigint NOT NULL,
    actividad_economica character varying(100),
    cargo_persona character varying(15),
    estado character varying(1) NOT NULL,
    estado_civil character varying(15),
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    fecha_registro date NOT NULL,
    fuente_ingresos character varying(15) NOT NULL,
    numero_telefono character varying(10),
    pais_domicilio_fiscal bigint,
    residente_domicilio_fiscal character varying(1) NOT NULL,
    sexo character varying(1),
    id_domicilio bigint NOT NULL,
    id_est_finan_jur bigint,
    id_firma_jur bigint,
    id_repre_legal_jur bigint,
    id_tipo_cuenta bigint NOT NULL,
    id_persona character varying(13) NOT NULL,
    id_doc_identificacion bigint,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50)
);
 +   DROP TABLE cuenta.ptn_pers_info_adicional;
       cuenta         heap    postgres    false    7            �           1259    86068 -   ptn_pers_info_adicional_id_info_adicional_seq    SEQUENCE       ALTER TABLE cuenta.ptn_pers_info_adicional ALTER COLUMN id_info_adicional ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.ptn_pers_info_adicional_id_info_adicional_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    404    7            �           1259    86077    ptn_pers_repre_legal    TABLE     r  CREATE TABLE cuenta.ptn_pers_repre_legal (
    id_repre_legal bigint NOT NULL,
    apellidos character varying(50) NOT NULL,
    cargo character varying(15) NOT NULL,
    direccion_domicilio character varying(200) NOT NULL,
    email character varying(100) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_inicio_actividad date NOT NULL,
    identificacion character varying(13) NOT NULL,
    nombres character varying(50) NOT NULL,
    numero_celular character varying(10) NOT NULL,
    numero_domicilio character varying(10) NOT NULL,
    telefono character varying(10) NOT NULL,
    id_pais bigint NOT NULL
);
 (   DROP TABLE cuenta.ptn_pers_repre_legal;
       cuenta         heap    postgres    false    7            �           1259    86075 '   ptn_pers_repre_legal_id_repre_legal_seq    SEQUENCE     �   ALTER TABLE cuenta.ptn_pers_repre_legal ALTER COLUMN id_repre_legal ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.ptn_pers_repre_legal_id_repre_legal_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    406    7            �           1259    86082    ptn_personas    TABLE     �  CREATE TABLE cuenta.ptn_personas (
    identificacion character varying(13) NOT NULL,
    anio_inicio_actividad integer,
    apellidos character varying(50),
    cargo_contacto character varying(50),
    email_contacto character varying(100),
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    fecha_nacimiento date,
    genero character varying(1),
    nacionalidad character varying(2) NOT NULL,
    nombre_contacto character varying(100),
    nombres character varying(50),
    numero_celular character varying(14) NOT NULL,
    razon_social character varying(100),
    tipo_cliente character varying(15),
    tipo_identificacion character varying(3) NOT NULL,
    tipo_persona character varying(3) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50)
);
     DROP TABLE cuenta.ptn_personas;
       cuenta         heap    postgres    false    7            �           1259    86092 	   ptn_roles    TABLE     �   CREATE TABLE cuenta.ptn_roles (
    id_rol bigint NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(50) NOT NULL,
    ruta character varying(250)
);
    DROP TABLE cuenta.ptn_roles;
       cuenta         heap    postgres    false    7            �           1259    86097    ptn_roles_cuentas    TABLE     �   CREATE TABLE cuenta.ptn_roles_cuentas (
    cuenta character varying(255) NOT NULL,
    rol bigint NOT NULL,
    fecha_creacion timestamp without time zone,
    usuari_creacion character varying(50) NOT NULL
);
 %   DROP TABLE cuenta.ptn_roles_cuentas;
       cuenta         heap    postgres    false    7            �           1259    86090    ptn_roles_id_rol_seq    SEQUENCE     �   ALTER TABLE cuenta.ptn_roles ALTER COLUMN id_rol ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.ptn_roles_id_rol_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    409    7            �           1259    86104 	   ptn_token    TABLE     �   CREATE TABLE cuenta.ptn_token (
    id_token bigint NOT NULL,
    fecha_creacion timestamp without time zone,
    token character varying(255),
    id_cuenta character varying(50) NOT NULL
);
    DROP TABLE cuenta.ptn_token;
       cuenta         heap    postgres    false    7            �           1259    86102    ptn_token_id_token_seq    SEQUENCE     �   ALTER TABLE cuenta.ptn_token ALTER COLUMN id_token ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME cuenta.ptn_token_id_token_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            cuenta          postgres    false    412    7            �            1259    68161    mult_info_emails    TABLE     �   CREATE TABLE emails.mult_info_emails (
    id_email bigint NOT NULL,
    email character varying(100) NOT NULL,
    enviado character varying(1),
    estado character varying(1) NOT NULL
);
 $   DROP TABLE emails.mult_info_emails;
       emails         heap    postgres    false    8            �            1259    68164    mult_info_emails_id_email_seq    SEQUENCE     �   ALTER TABLE emails.mult_info_emails ALTER COLUMN id_email ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME emails.mult_info_emails_id_email_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            emails          postgres    false    252    8            �            1259    68166    mult_plantillas_emails    TABLE     �   CREATE TABLE emails.mult_plantillas_emails (
    id_plantilla character varying(20) NOT NULL,
    asunto character varying(100) NOT NULL,
    cuerpo json,
    estado character varying(1) NOT NULL,
    nombre character varying(100) NOT NULL
);
 *   DROP TABLE emails.mult_plantillas_emails;
       emails         heap    postgres    false    8            �           1259    86127    ptn_info_emails    TABLE     �   CREATE TABLE emails.ptn_info_emails (
    id_email bigint NOT NULL,
    email character varying(100) NOT NULL,
    enviado character varying(1),
    estado character varying(1) NOT NULL
);
 #   DROP TABLE emails.ptn_info_emails;
       emails         heap    postgres    false    8            �           1259    86125    ptn_info_emails_id_email_seq    SEQUENCE     �   ALTER TABLE emails.ptn_info_emails ALTER COLUMN id_email ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME emails.ptn_info_emails_id_email_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            emails          postgres    false    414    8            �           1259    86132    ptn_plantillas_emails    TABLE     �   CREATE TABLE emails.ptn_plantillas_emails (
    id_plantilla character varying(20) NOT NULL,
    asunto character varying(100) NOT NULL,
    cuerpo json,
    estado character varying(1) NOT NULL,
    nombre character varying(100) NOT NULL
);
 )   DROP TABLE emails.ptn_plantillas_emails;
       emails         heap    postgres    false    8            �            1259    68172    mult_form_contact    TABLE     �  CREATE TABLE footer.mult_form_contact (
    id bigint NOT NULL,
    ciudad character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    identificacion character varying(13) NOT NULL,
    mensaje character varying(200) NOT NULL,
    motivo character varying(50) NOT NULL,
    nombres character varying(50) NOT NULL,
    telefono character varying(10) NOT NULL,
    estado_actual character varying(5) NOT NULL,
    estado_anterior character varying(5)
);
 %   DROP TABLE footer.mult_form_contact;
       footer         heap    postgres    false    9                        1259    68175    mult_form_contact_id_seq    SEQUENCE     �   ALTER TABLE footer.mult_form_contact ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME footer.mult_form_contact_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            footer          postgres    false    255    9            �           1259    86146    ptn_form_contact    TABLE     �  CREATE TABLE footer.ptn_form_contact (
    id bigint NOT NULL,
    ciudad character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    identificacion character varying(13) NOT NULL,
    mensaje character varying(200) NOT NULL,
    motivo character varying(50) NOT NULL,
    nombres character varying(50) NOT NULL,
    telefono character varying(10) NOT NULL,
    estado_actual character varying(5) NOT NULL,
    estado_anterior character varying(5)
);
 $   DROP TABLE footer.ptn_form_contact;
       footer         heap    postgres    false    9            �           1259    86144    ptn_form_contact_id_seq    SEQUENCE     �   ALTER TABLE footer.ptn_form_contact ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME footer.ptn_form_contact_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            footer          postgres    false    417    9                       1259    68177    mult_bitacora_procesos    TABLE     )  CREATE TABLE historicas.mult_bitacora_procesos (
    id bigint NOT NULL,
    descripcion character varying(400) NOT NULL,
    fecha timestamp without time zone NOT NULL,
    identificador character varying(25),
    proceso character varying(50) NOT NULL,
    tipo character varying(5) NOT NULL
);
 .   DROP TABLE historicas.mult_bitacora_procesos;
    
   historicas         heap    postgres    false    10                       1259    68180    mult_bitacora_procesos_id_seq    SEQUENCE     �   ALTER TABLE historicas.mult_bitacora_procesos ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.mult_bitacora_procesos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    10    257                       1259    68182    mult_hist_proyecto    TABLE     �  CREATE TABLE historicas.mult_hist_proyecto (
    id bigint NOT NULL,
    comprobante_ruta character varying(200),
    fecha_historial timestamp without time zone,
    observacion character varying(200) NOT NULL,
    tabla_cambiar character varying(255) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    valor_actual character varying(255) NOT NULL,
    valor_anterior character varying(255) NOT NULL,
    id_proyecto character varying(50) NOT NULL
);
 *   DROP TABLE historicas.mult_hist_proyecto;
    
   historicas         heap    postgres    false    10                       1259    68188    mult_hist_proyecto_id_seq    SEQUENCE     �   ALTER TABLE historicas.mult_hist_proyecto ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.mult_hist_proyecto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    10    259                       1259    68190    mult_hist_solicitud    TABLE     �  CREATE TABLE historicas.mult_hist_solicitud (
    id bigint NOT NULL,
    comprobante_ruta character varying(200),
    fecha_historial timestamp without time zone,
    observacion character varying(200) NOT NULL,
    tabla_cambiar character varying(100) NOT NULL,
    valor_actual character varying(100) NOT NULL,
    valor_anterior character varying(100) NOT NULL,
    solicitud bigint,
    usuario_modificacion character varying(50),
    usuario_modificacion_interno character varying(50)
);
 +   DROP TABLE historicas.mult_hist_solicitud;
    
   historicas         heap    postgres    false    10                       1259    68196    mult_hist_solicitud_id_seq    SEQUENCE     �   ALTER TABLE historicas.mult_hist_solicitud ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.mult_hist_solicitud_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    261    10                       1259    68198    mult_historial_conciliacion    TABLE     �   CREATE TABLE historicas.mult_historial_conciliacion (
    id bigint NOT NULL,
    fecha timestamp without time zone NOT NULL,
    usuario character varying(50) NOT NULL,
    id_file bigint NOT NULL
);
 3   DROP TABLE historicas.mult_historial_conciliacion;
    
   historicas         heap    postgres    false    10                       1259    68201 #   mult_historial_conciliacion_detalle    TABLE     r  CREATE TABLE historicas.mult_historial_conciliacion_detalle (
    id bigint NOT NULL,
    comprobante character varying(50) NOT NULL,
    fecha_file date NOT NULL,
    fecha_transaccion date NOT NULL,
    monto_file numeric(19,2) NOT NULL,
    monto_transaccion numeric(19,2) NOT NULL,
    observacion character varying(50) NOT NULL,
    id_historial bigint NOT NULL
);
 ;   DROP TABLE historicas.mult_historial_conciliacion_detalle;
    
   historicas         heap    postgres    false    10            	           1259    68204 *   mult_historial_conciliacion_detalle_id_seq    SEQUENCE       ALTER TABLE historicas.mult_historial_conciliacion_detalle ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.mult_historial_conciliacion_detalle_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    264    10            
           1259    68206 "   mult_historial_conciliacion_id_seq    SEQUENCE     �   ALTER TABLE historicas.mult_historial_conciliacion ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.mult_historial_conciliacion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    263    10                       1259    68208    mult_sol_x_identifiacion    TABLE     �   CREATE TABLE historicas.mult_sol_x_identifiacion (
    id bigint NOT NULL,
    documento bytea,
    fecha_creacion date NOT NULL,
    solicitud bytea,
    usuario_creacion bytea
);
 0   DROP TABLE historicas.mult_sol_x_identifiacion;
    
   historicas         heap    postgres    false    10                       1259    68214    mult_sol_x_identifiacion_id_seq    SEQUENCE     �   ALTER TABLE historicas.mult_sol_x_identifiacion ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.mult_sol_x_identifiacion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    10    267            �           1259    86153    ptn_bitacora_procesos    TABLE     (  CREATE TABLE historicas.ptn_bitacora_procesos (
    id bigint NOT NULL,
    descripcion character varying(400) NOT NULL,
    fecha timestamp without time zone NOT NULL,
    identificador character varying(25),
    proceso character varying(50) NOT NULL,
    tipo character varying(5) NOT NULL
);
 -   DROP TABLE historicas.ptn_bitacora_procesos;
    
   historicas         heap    postgres    false    10            �           1259    86151    ptn_bitacora_procesos_id_seq    SEQUENCE     �   ALTER TABLE historicas.ptn_bitacora_procesos ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.ptn_bitacora_procesos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    419    10            �           1259    86160    ptn_hist_proyecto    TABLE     �  CREATE TABLE historicas.ptn_hist_proyecto (
    id bigint NOT NULL,
    comprobante_ruta character varying(200),
    fecha_historial timestamp without time zone,
    observacion character varying(200) NOT NULL,
    tabla_cambiar character varying(255) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    valor_actual character varying(255) NOT NULL,
    valor_anterior character varying(255) NOT NULL,
    id_proyecto character varying(50) NOT NULL
);
 )   DROP TABLE historicas.ptn_hist_proyecto;
    
   historicas         heap    postgres    false    10            �           1259    86158    ptn_hist_proyecto_id_seq    SEQUENCE     �   ALTER TABLE historicas.ptn_hist_proyecto ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.ptn_hist_proyecto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    421    10            �           1259    86170    ptn_hist_solicitud    TABLE     �  CREATE TABLE historicas.ptn_hist_solicitud (
    id bigint NOT NULL,
    comprobante_ruta character varying(200),
    fecha_historial timestamp without time zone,
    observacion character varying(200) NOT NULL,
    tabla_cambiar character varying(100) NOT NULL,
    valor_actual character varying(100) NOT NULL,
    valor_anterior character varying(100) NOT NULL,
    solicitud bigint NOT NULL,
    usuario_modificacion character varying(50),
    usuario_modificacion_interno character varying(50)
);
 *   DROP TABLE historicas.ptn_hist_solicitud;
    
   historicas         heap    postgres    false    10            �           1259    86168    ptn_hist_solicitud_id_seq    SEQUENCE     �   ALTER TABLE historicas.ptn_hist_solicitud ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.ptn_hist_solicitud_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    10    423            �           1259    86180    ptn_historial_conciliacion    TABLE     �   CREATE TABLE historicas.ptn_historial_conciliacion (
    id bigint NOT NULL,
    fecha timestamp without time zone NOT NULL,
    usuario character varying(50) NOT NULL,
    id_file bigint NOT NULL
);
 2   DROP TABLE historicas.ptn_historial_conciliacion;
    
   historicas         heap    postgres    false    10            �           1259    86187 "   ptn_historial_conciliacion_detalle    TABLE     q  CREATE TABLE historicas.ptn_historial_conciliacion_detalle (
    id bigint NOT NULL,
    comprobante character varying(50) NOT NULL,
    fecha_file date NOT NULL,
    fecha_transaccion date NOT NULL,
    monto_file numeric(19,2) NOT NULL,
    monto_transaccion numeric(19,2) NOT NULL,
    observacion character varying(50) NOT NULL,
    id_historial bigint NOT NULL
);
 :   DROP TABLE historicas.ptn_historial_conciliacion_detalle;
    
   historicas         heap    postgres    false    10            �           1259    86185 )   ptn_historial_conciliacion_detalle_id_seq    SEQUENCE       ALTER TABLE historicas.ptn_historial_conciliacion_detalle ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.ptn_historial_conciliacion_detalle_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    427    10            �           1259    86178 !   ptn_historial_conciliacion_id_seq    SEQUENCE     �   ALTER TABLE historicas.ptn_historial_conciliacion ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.ptn_historial_conciliacion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    425    10            �           1259    86194    ptn_sol_x_identifiacion    TABLE     �   CREATE TABLE historicas.ptn_sol_x_identifiacion (
    id bigint NOT NULL,
    documento bytea,
    fecha_creacion date NOT NULL,
    solicitud bytea,
    usuario_creacion bytea
);
 /   DROP TABLE historicas.ptn_sol_x_identifiacion;
    
   historicas         heap    postgres    false    10            �           1259    86192    ptn_sol_x_identifiacion_id_seq    SEQUENCE     �   ALTER TABLE historicas.ptn_sol_x_identifiacion ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME historicas.ptn_sol_x_identifiacion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         
   historicas          postgres    false    10    429                       1259    68216    mult_datos_inversion    TABLE     �   CREATE TABLE inversion.mult_datos_inversion (
    id_dato bigint NOT NULL,
    documentacion boolean,
    formulario boolean,
    pago boolean,
    tabla_amortizacion boolean,
    solicitud bigint
);
 +   DROP TABLE inversion.mult_datos_inversion;
    	   inversion         heap    postgres    false    11                       1259    68219     mult_datos_inversion_id_dato_seq    SEQUENCE     �   ALTER TABLE inversion.mult_datos_inversion ALTER COLUMN id_dato ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.mult_datos_inversion_id_dato_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    269    11                       1259    68221    mult_detalle_amortizacion    TABLE     %  CREATE TABLE inversion.mult_detalle_amortizacion (
    id_det_amortizacion bigint NOT NULL,
    cobros_capital numeric(12,2),
    cuota integer NOT NULL,
    detalle_cobro character varying(50) NOT NULL,
    estado character varying(1) NOT NULL,
    estado_pago character varying(30),
    fecha_estimacion date,
    fecha_realizada date,
    rendimiento_mensual numeric(12,2),
    ruta_pago character varying(300),
    saldo_capital numeric(12,2),
    total_recibir numeric(12,2),
    id_tbl_amortizacion bigint NOT NULL,
    fecha_registro date
);
 0   DROP TABLE inversion.mult_detalle_amortizacion;
    	   inversion         heap    postgres    false    11                       1259    68224 1   mult_detalle_amortizacion_id_det_amortizacion_seq    SEQUENCE       ALTER TABLE inversion.mult_detalle_amortizacion ALTER COLUMN id_det_amortizacion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.mult_detalle_amortizacion_id_det_amortizacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    11    271                       1259    68226    mult_doc_aceptados    TABLE     k  CREATE TABLE inversion.mult_doc_aceptados (
    id_doc_aceptado bigint NOT NULL,
    estado character varying(10) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    habilitado boolean NOT NULL,
    nombre character varying(100) NOT NULL,
    numero_solicitud character varying(20),
    ruta character varying(200) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    version integer NOT NULL,
    id_tipo_documento bigint NOT NULL,
    identificacion character varying(13) NOT NULL
);
 )   DROP TABLE inversion.mult_doc_aceptados;
    	   inversion         heap    postgres    false    11                       1259    68229 &   mult_doc_aceptados_id_doc_aceptado_seq    SEQUENCE       ALTER TABLE inversion.mult_doc_aceptados ALTER COLUMN id_doc_aceptado ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.mult_doc_aceptados_id_doc_aceptado_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    11    273                       1259    68231    mult_solicitudes    TABLE     �  CREATE TABLE inversion.mult_solicitudes (
    numero_solicitud bigint NOT NULL,
    acepta_informacion_correcta character varying(1),
    acepta_ingresar_info_vigente character varying(1),
    acepta_licitud_fondos character varying(1),
    fecha_generacion timestamp without time zone NOT NULL,
    observacion character varying(100),
    tabla_amortizacion bigint,
    documentos bigint,
    estado_actual character varying(5) NOT NULL,
    ultimo_historial bigint,
    id_inversionista character varying(50) NOT NULL,
    pagare bigint,
    codigo_proyecto character varying(50) NOT NULL,
    id_tipo_solicitud bigint,
    usuario_creacion character varying(50) NOT NULL,
    activo boolean
);
 '   DROP TABLE inversion.mult_solicitudes;
    	   inversion         heap    postgres    false    11                       1259    68234    mult_solicitudes_documentos    TABLE     �  CREATE TABLE inversion.mult_solicitudes_documentos (
    id_sol_documentos bigint NOT NULL,
    acuerdo_uso character varying(300) NOT NULL,
    contrato_prenda character varying(300) NOT NULL,
    datos_inversionista character varying(300) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    modelo_contrato character varying(300) NOT NULL,
    observacion character varying(300) NOT NULL,
    pagare character varying(300) NOT NULL,
    tabla_amortizacion character varying(300) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    solicitud bigint
);
 2   DROP TABLE inversion.mult_solicitudes_documentos;
    	   inversion         heap    postgres    false    11                       1259    68240 1   mult_solicitudes_documentos_id_sol_documentos_seq    SEQUENCE       ALTER TABLE inversion.mult_solicitudes_documentos ALTER COLUMN id_sol_documentos ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.mult_solicitudes_documentos_id_sol_documentos_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    11    276                       1259    68242 %   mult_solicitudes_numero_solicitud_seq    SEQUENCE       ALTER TABLE inversion.mult_solicitudes ALTER COLUMN numero_solicitud ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.mult_solicitudes_numero_solicitud_seq
    START WITH 14
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    275    11                       1259    68244 $   mult_solicitudes_numerosolicitud_seq    SEQUENCE     �   CREATE SEQUENCE inversion.mult_solicitudes_numerosolicitud_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 9999999999
    CACHE 1;
 >   DROP SEQUENCE inversion.mult_solicitudes_numerosolicitud_seq;
    	   inversion          postgres    false    11                       1259    68246    mult_tabla_amortizacion    TABLE     �  CREATE TABLE inversion.mult_tabla_amortizacion (
    id_tbl_amortizacion bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_efectiva date,
    monto_inversion numeric(12,2) NOT NULL,
    plazo integer NOT NULL,
    rendimiento_neto numeric(12,2) NOT NULL,
    rendimiento_total_inversion numeric(12,2),
    total_recibir numeric(12,2),
    id_tipo_tabla bigint NOT NULL,
    usuario_creacion character varying(50)
);
 .   DROP TABLE inversion.mult_tabla_amortizacion;
    	   inversion         heap    postgres    false    11                       1259    68249 /   mult_tabla_amortizacion_id_tbl_amortizacion_seq    SEQUENCE       ALTER TABLE inversion.mult_tabla_amortizacion ALTER COLUMN id_tbl_amortizacion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.mult_tabla_amortizacion_id_tbl_amortizacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    280    11                       1259    68251    mult_tipo_estados    TABLE     �   CREATE TABLE inversion.mult_tipo_estados (
    id_estado character varying(5) NOT NULL,
    descripcion character varying(50) NOT NULL,
    detalle character varying(200) NOT NULL,
    estado character varying(1) NOT NULL
);
 (   DROP TABLE inversion.mult_tipo_estados;
    	   inversion         heap    postgres    false    11                       1259    68254    mult_transacciones    TABLE     �  CREATE TABLE inversion.mult_transacciones (
    id_doc_transaccion bigint NOT NULL,
    conciliado character varying(1),
    depositante character varying(255),
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_transaccion date NOT NULL,
    monto numeric(12,2) NOT NULL,
    nombre_documento character varying(100),
    numero_comprobante character varying(20) NOT NULL,
    ruta_comprobante character varying(200),
    id_tipo_documento bigint NOT NULL,
    id_forma_pago bigint NOT NULL,
    numero_solicitud bigint,
    usuario_creacion character varying(50),
    proyecto character varying(50)
);
 )   DROP TABLE inversion.mult_transacciones;
    	   inversion         heap    postgres    false    11                       1259    68260 )   mult_transacciones_id_doc_transaccion_seq    SEQUENCE     	  ALTER TABLE inversion.mult_transacciones ALTER COLUMN id_doc_transaccion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.mult_transacciones_id_doc_transaccion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    283    11            �           1259    86204    ptn_datos_inversion    TABLE     �   CREATE TABLE inversion.ptn_datos_inversion (
    id_dato bigint NOT NULL,
    documentacion boolean,
    formulario boolean,
    pago boolean,
    tabla_amortizacion boolean,
    solicitud bigint
);
 *   DROP TABLE inversion.ptn_datos_inversion;
    	   inversion         heap    postgres    false    11            �           1259    86202    ptn_datos_inversion_id_dato_seq    SEQUENCE     �   ALTER TABLE inversion.ptn_datos_inversion ALTER COLUMN id_dato ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.ptn_datos_inversion_id_dato_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    11    431            �           1259    86211    ptn_detalle_amortizacion    TABLE     $  CREATE TABLE inversion.ptn_detalle_amortizacion (
    id_det_amortizacion bigint NOT NULL,
    cobros_capital numeric(12,2),
    cuota integer NOT NULL,
    detalle_cobro character varying(50) NOT NULL,
    estado character varying(1) NOT NULL,
    estado_pago character varying(30),
    fecha_estimacion date,
    fecha_realizada date,
    fecha_registro date,
    rendimiento_mensual numeric(12,2),
    ruta_pago character varying(300),
    saldo_capital numeric(12,2),
    total_recibir numeric(12,2),
    id_tbl_amortizacion bigint NOT NULL
);
 /   DROP TABLE inversion.ptn_detalle_amortizacion;
    	   inversion         heap    postgres    false    11            �           1259    86209 0   ptn_detalle_amortizacion_id_det_amortizacion_seq    SEQUENCE       ALTER TABLE inversion.ptn_detalle_amortizacion ALTER COLUMN id_det_amortizacion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.ptn_detalle_amortizacion_id_det_amortizacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    11    433            �           1259    86218    ptn_doc_aceptados    TABLE     j  CREATE TABLE inversion.ptn_doc_aceptados (
    id_doc_aceptado bigint NOT NULL,
    estado character varying(10) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    habilitado boolean NOT NULL,
    nombre character varying(100) NOT NULL,
    numero_solicitud character varying(20),
    ruta character varying(200) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    version integer NOT NULL,
    id_tipo_documento bigint NOT NULL,
    identificacion character varying(13) NOT NULL
);
 (   DROP TABLE inversion.ptn_doc_aceptados;
    	   inversion         heap    postgres    false    11            �           1259    86216 %   ptn_doc_aceptados_id_doc_aceptado_seq    SEQUENCE       ALTER TABLE inversion.ptn_doc_aceptados ALTER COLUMN id_doc_aceptado ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.ptn_doc_aceptados_id_doc_aceptado_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    11    435            �           1259    86225    ptn_solicitudes    TABLE     �  CREATE TABLE inversion.ptn_solicitudes (
    numero_solicitud bigint NOT NULL,
    acepta_informacion_correcta character varying(1),
    acepta_ingresar_info_vigente character varying(1),
    acepta_licitud_fondos character varying(1),
    activo boolean NOT NULL,
    fecha_generacion timestamp without time zone NOT NULL,
    observacion character varying(100),
    tabla_amortizacion bigint NOT NULL,
    documentos bigint,
    estado_actual character varying(5) NOT NULL,
    ultimo_historial bigint,
    id_inversionista character varying(50) NOT NULL,
    pagare bigint,
    codigo_proyecto character varying(50) NOT NULL,
    id_tipo_solicitud bigint,
    usuario_creacion character varying(50) NOT NULL
);
 &   DROP TABLE inversion.ptn_solicitudes;
    	   inversion         heap    postgres    false    11            �           1259    86232    ptn_solicitudes_documentos    TABLE     �  CREATE TABLE inversion.ptn_solicitudes_documentos (
    id_sol_documentos bigint NOT NULL,
    acuerdo_uso character varying(300) NOT NULL,
    contrato_prenda character varying(300) NOT NULL,
    datos_inversionista character varying(300) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    modelo_contrato character varying(300) NOT NULL,
    observacion character varying(300) NOT NULL,
    pagare character varying(300) NOT NULL,
    tabla_amortizacion character varying(300) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    solicitud bigint
);
 1   DROP TABLE inversion.ptn_solicitudes_documentos;
    	   inversion         heap    postgres    false    11            �           1259    86230 0   ptn_solicitudes_documentos_id_sol_documentos_seq    SEQUENCE       ALTER TABLE inversion.ptn_solicitudes_documentos ALTER COLUMN id_sol_documentos ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.ptn_solicitudes_documentos_id_sol_documentos_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    11    439            �           1259    86223 $   ptn_solicitudes_numero_solicitud_seq    SEQUENCE     �   ALTER TABLE inversion.ptn_solicitudes ALTER COLUMN numero_solicitud ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.ptn_solicitudes_numero_solicitud_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    437    11            �           1259    86242    ptn_tabla_amortizacion    TABLE     �  CREATE TABLE inversion.ptn_tabla_amortizacion (
    id_tbl_amortizacion bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_efectiva date,
    monto_inversion numeric(12,2) NOT NULL,
    plazo integer NOT NULL,
    rendimiento_neto numeric(12,2) NOT NULL,
    rendimiento_total_inversion numeric(12,2),
    total_recibir numeric(12,2),
    id_tipo_tabla bigint NOT NULL,
    usuario_creacion character varying(50)
);
 -   DROP TABLE inversion.ptn_tabla_amortizacion;
    	   inversion         heap    postgres    false    11            �           1259    86240 .   ptn_tabla_amortizacion_id_tbl_amortizacion_seq    SEQUENCE       ALTER TABLE inversion.ptn_tabla_amortizacion ALTER COLUMN id_tbl_amortizacion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.ptn_tabla_amortizacion_id_tbl_amortizacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    441    11            �           1259    86247    ptn_tipo_estados    TABLE     �   CREATE TABLE inversion.ptn_tipo_estados (
    id_estado character varying(5) NOT NULL,
    descripcion character varying(50) NOT NULL,
    detalle character varying(100) NOT NULL,
    estado character varying(1) NOT NULL
);
 '   DROP TABLE inversion.ptn_tipo_estados;
    	   inversion         heap    postgres    false    11            �           1259    86254    ptn_transacciones    TABLE     �  CREATE TABLE inversion.ptn_transacciones (
    id_doc_transaccion bigint NOT NULL,
    conciliado character varying(1),
    depositante character varying(255),
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_transaccion date NOT NULL,
    monto numeric(12,2) NOT NULL,
    nombre_documento character varying(100),
    numero_comprobante character varying(20) NOT NULL,
    ruta_comprobante character varying(200),
    id_tipo_documento bigint NOT NULL,
    id_forma_pago bigint NOT NULL,
    proyecto character varying(50),
    numero_solicitud bigint,
    usuario_creacion character varying(50)
);
 (   DROP TABLE inversion.ptn_transacciones;
    	   inversion         heap    postgres    false    11            �           1259    86252 (   ptn_transacciones_id_doc_transaccion_seq    SEQUENCE       ALTER TABLE inversion.ptn_transacciones ALTER COLUMN id_doc_transaccion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME inversion.ptn_transacciones_id_doc_transaccion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
         	   inversion          postgres    false    11    444                       1259    68262    mult_bancos    TABLE     �   CREATE TABLE maestras.mult_bancos (
    id_banco bigint NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(100) NOT NULL
);
 !   DROP TABLE maestras.mult_bancos;
       maestras         heap    postgres    false    12                       1259    68265    mult_bancos_id_banco_seq    SEQUENCE     �   ALTER TABLE maestras.mult_bancos ALTER COLUMN id_banco ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.mult_bancos_id_banco_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    285    12                       1259    68267    mult_ciudades    TABLE     �   CREATE TABLE maestras.mult_ciudades (
    id_ciudad bigint NOT NULL,
    ciudad character varying(100) NOT NULL,
    estado character varying(1) NOT NULL,
    id_pais bigint NOT NULL
);
 #   DROP TABLE maestras.mult_ciudades;
       maestras         heap    postgres    false    12                        1259    68270    mult_ciudades_id_ciudad_seq    SEQUENCE     �   ALTER TABLE maestras.mult_ciudades ALTER COLUMN id_ciudad ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.mult_ciudades_id_ciudad_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    287    12            !           1259    68272    mult_forma_pago    TABLE     �   CREATE TABLE maestras.mult_forma_pago (
    id_forma_pago bigint NOT NULL,
    descripcion character varying(20) NOT NULL,
    estado character varying(1) NOT NULL
);
 %   DROP TABLE maestras.mult_forma_pago;
       maestras         heap    postgres    false    12            "           1259    68275 !   mult_forma_pago_id_forma_pago_seq    SEQUENCE     �   ALTER TABLE maestras.mult_forma_pago ALTER COLUMN id_forma_pago ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.mult_forma_pago_id_forma_pago_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    289    12            #           1259    68277    mult_nacionalidades    TABLE     �   CREATE TABLE maestras.mult_nacionalidades (
    id_nacionalidad bigint NOT NULL,
    estado character varying(1) NOT NULL,
    gentilicio character varying(100) NOT NULL,
    iso character varying(3) NOT NULL,
    pais character varying(100) NOT NULL
);
 )   DROP TABLE maestras.mult_nacionalidades;
       maestras         heap    postgres    false    12            $           1259    68280 '   mult_nacionalidades_id_nacionalidad_seq    SEQUENCE       ALTER TABLE maestras.mult_nacionalidades ALTER COLUMN id_nacionalidad ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.mult_nacionalidades_id_nacionalidad_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    291    12            %           1259    68282    mult_rango_pago    TABLE     �   CREATE TABLE maestras.mult_rango_pago (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    max numeric(12,2) NOT NULL,
    min numeric(12,2) NOT NULL,
    usuario_creacion character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL
);
 %   DROP TABLE maestras.mult_rango_pago;
       maestras         heap    postgres    false    12            &           1259    68285    mult_rango_pago_id_seq    SEQUENCE     �   ALTER TABLE maestras.mult_rango_pago ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.mult_rango_pago_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    12    293            '           1259    68287    mult_tipo_documentos    TABLE     �   CREATE TABLE maestras.mult_tipo_documentos (
    id_tipo_documento bigint NOT NULL,
    documento character varying(50) NOT NULL,
    estado character varying(1) NOT NULL
);
 *   DROP TABLE maestras.mult_tipo_documentos;
       maestras         heap    postgres    false    12            (           1259    68290 *   mult_tipo_documentos_id_tipo_documento_seq    SEQUENCE     	  ALTER TABLE maestras.mult_tipo_documentos ALTER COLUMN id_tipo_documento ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.mult_tipo_documentos_id_tipo_documento_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    295    12            )           1259    68292    mult_tipo_solicitud    TABLE     �   CREATE TABLE maestras.mult_tipo_solicitud (
    id bigint NOT NULL,
    descripcion character varying(20) NOT NULL,
    estado character varying(1) NOT NULL
);
 )   DROP TABLE maestras.mult_tipo_solicitud;
       maestras         heap    postgres    false    12            *           1259    68295    mult_tipo_solicitud_id_seq    SEQUENCE     �   ALTER TABLE maestras.mult_tipo_solicitud ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.mult_tipo_solicitud_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    12    297            +           1259    68297    mult_tipo_tablas    TABLE     �   CREATE TABLE maestras.mult_tipo_tablas (
    id_tipo_tabla bigint NOT NULL,
    descripcion character varying(200) NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(100) NOT NULL
);
 &   DROP TABLE maestras.mult_tipo_tablas;
       maestras         heap    postgres    false    12            ,           1259    68300 "   mult_tipo_tablas_id_tipo_tabla_seq    SEQUENCE     �   ALTER TABLE maestras.mult_tipo_tablas ALTER COLUMN id_tipo_tabla ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.mult_tipo_tablas_id_tipo_tabla_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    12    299            -           1259    68302    mult_tipo_transacciones    TABLE     �   CREATE TABLE maestras.mult_tipo_transacciones (
    id_tipo_transaccion bigint NOT NULL,
    descripcion character varying(20) NOT NULL,
    estado character varying(1) NOT NULL
);
 -   DROP TABLE maestras.mult_tipo_transacciones;
       maestras         heap    postgres    false    12            .           1259    68305 /   mult_tipo_transacciones_id_tipo_transaccion_seq    SEQUENCE       ALTER TABLE maestras.mult_tipo_transacciones ALTER COLUMN id_tipo_transaccion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.mult_tipo_transacciones_id_tipo_transaccion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    301    12            �           1259    86266 
   ptn_bancos    TABLE     �   CREATE TABLE maestras.ptn_bancos (
    id_banco bigint NOT NULL,
    estado character varying(100) NOT NULL,
    nombre character varying(100) NOT NULL
);
     DROP TABLE maestras.ptn_bancos;
       maestras         heap    postgres    false    12            �           1259    86264    ptn_bancos_id_banco_seq    SEQUENCE     �   ALTER TABLE maestras.ptn_bancos ALTER COLUMN id_banco ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.ptn_bancos_id_banco_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    12    446            �           1259    86273    ptn_ciudades    TABLE     �   CREATE TABLE maestras.ptn_ciudades (
    id_ciudad bigint NOT NULL,
    ciudad character varying(100) NOT NULL,
    estado character varying(100) NOT NULL,
    id_pais bigint NOT NULL
);
 "   DROP TABLE maestras.ptn_ciudades;
       maestras         heap    postgres    false    12            �           1259    86271    ptn_ciudades_id_ciudad_seq    SEQUENCE     �   ALTER TABLE maestras.ptn_ciudades ALTER COLUMN id_ciudad ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.ptn_ciudades_id_ciudad_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    12    448            �           1259    86280    ptn_forma_pago    TABLE     �   CREATE TABLE maestras.ptn_forma_pago (
    id_forma_pago bigint NOT NULL,
    descripcion character varying(20) NOT NULL,
    estado character varying(1) NOT NULL
);
 $   DROP TABLE maestras.ptn_forma_pago;
       maestras         heap    postgres    false    12            �           1259    86278     ptn_forma_pago_id_forma_pago_seq    SEQUENCE     �   ALTER TABLE maestras.ptn_forma_pago ALTER COLUMN id_forma_pago ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.ptn_forma_pago_id_forma_pago_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    450    12            �           1259    86287    ptn_nacionalidades    TABLE     �   CREATE TABLE maestras.ptn_nacionalidades (
    id_nacionalidad bigint NOT NULL,
    estado character varying(100) NOT NULL,
    gentilicio character varying(100) NOT NULL,
    iso character varying(3) NOT NULL,
    pais character varying(100) NOT NULL
);
 (   DROP TABLE maestras.ptn_nacionalidades;
       maestras         heap    postgres    false    12            �           1259    86285 &   ptn_nacionalidades_id_nacionalidad_seq    SEQUENCE       ALTER TABLE maestras.ptn_nacionalidades ALTER COLUMN id_nacionalidad ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.ptn_nacionalidades_id_nacionalidad_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    452    12            �           1259    86294    ptn_rango_pago    TABLE     �   CREATE TABLE maestras.ptn_rango_pago (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    max numeric(12,2) NOT NULL,
    min numeric(12,2) NOT NULL,
    usuario_creacion character varying(20) NOT NULL,
    valor numeric(12,2) NOT NULL
);
 $   DROP TABLE maestras.ptn_rango_pago;
       maestras         heap    postgres    false    12            �           1259    86292    ptn_rango_pago_id_seq    SEQUENCE     �   ALTER TABLE maestras.ptn_rango_pago ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.ptn_rango_pago_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    454    12            �           1259    86301    ptn_tipo_documentos    TABLE     �   CREATE TABLE maestras.ptn_tipo_documentos (
    id_tipo_documento bigint NOT NULL,
    documento character varying(50) NOT NULL,
    estado character varying(1) NOT NULL
);
 )   DROP TABLE maestras.ptn_tipo_documentos;
       maestras         heap    postgres    false    12            �           1259    86299 )   ptn_tipo_documentos_id_tipo_documento_seq    SEQUENCE       ALTER TABLE maestras.ptn_tipo_documentos ALTER COLUMN id_tipo_documento ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.ptn_tipo_documentos_id_tipo_documento_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    456    12            �           1259    86308    ptn_tipo_solicitud    TABLE     �   CREATE TABLE maestras.ptn_tipo_solicitud (
    id bigint NOT NULL,
    descripcion character varying(20) NOT NULL,
    estado character varying(1) NOT NULL
);
 (   DROP TABLE maestras.ptn_tipo_solicitud;
       maestras         heap    postgres    false    12            �           1259    86306    ptn_tipo_solicitud_id_seq    SEQUENCE     �   ALTER TABLE maestras.ptn_tipo_solicitud ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.ptn_tipo_solicitud_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    458    12            �           1259    86315    ptn_tipo_tablas    TABLE     �   CREATE TABLE maestras.ptn_tipo_tablas (
    id_tipo_tabla bigint NOT NULL,
    descripcion character varying(200) NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(100) NOT NULL
);
 %   DROP TABLE maestras.ptn_tipo_tablas;
       maestras         heap    postgres    false    12            �           1259    86313 !   ptn_tipo_tablas_id_tipo_tabla_seq    SEQUENCE     �   ALTER TABLE maestras.ptn_tipo_tablas ALTER COLUMN id_tipo_tabla ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras.ptn_tipo_tablas_id_tipo_tabla_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras          postgres    false    12    460            /           1259    68307    mult_deport_categorias    TABLE     �   CREATE TABLE maestras_auspicios.mult_deport_categorias (
    id bigint NOT NULL,
    descripcion character varying(50),
    nombre character varying(25),
    activo boolean
);
 6   DROP TABLE maestras_auspicios.mult_deport_categorias;
       maestras_auspicios         heap    postgres    false    13            0           1259    68310    mult_deport_categorias_id_seq    SEQUENCE       ALTER TABLE maestras_auspicios.mult_deport_categorias ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras_auspicios.mult_deport_categorias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras_auspicios          postgres    false    303    13            1           1259    68312    mult_deport_disciplina    TABLE     �   CREATE TABLE maestras_auspicios.mult_deport_disciplina (
    id bigint NOT NULL,
    descripcion character varying(50),
    nombre character varying(20),
    activo boolean
);
 6   DROP TABLE maestras_auspicios.mult_deport_disciplina;
       maestras_auspicios         heap    postgres    false    13            2           1259    68315    mult_deport_disciplina_id_seq    SEQUENCE       ALTER TABLE maestras_auspicios.mult_deport_disciplina ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras_auspicios.mult_deport_disciplina_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras_auspicios          postgres    false    305    13            3           1259    68317    mult_deport_modalidad    TABLE     �   CREATE TABLE maestras_auspicios.mult_deport_modalidad (
    id bigint NOT NULL,
    descripcion character varying(50),
    nombre character varying(25),
    activo boolean
);
 5   DROP TABLE maestras_auspicios.mult_deport_modalidad;
       maestras_auspicios         heap    postgres    false    13            4           1259    68320    mult_deport_modalidad_id_seq    SEQUENCE       ALTER TABLE maestras_auspicios.mult_deport_modalidad ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras_auspicios.mult_deport_modalidad_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras_auspicios          postgres    false    307    13            �           1259    86329    ptn_deport_categorias    TABLE     �   CREATE TABLE maestras_auspicios.ptn_deport_categorias (
    id bigint NOT NULL,
    activo boolean,
    descripcion character varying(50) NOT NULL,
    nombre character varying(25) NOT NULL
);
 5   DROP TABLE maestras_auspicios.ptn_deport_categorias;
       maestras_auspicios         heap    postgres    false    13            �           1259    86327    ptn_deport_categorias_id_seq    SEQUENCE       ALTER TABLE maestras_auspicios.ptn_deport_categorias ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras_auspicios.ptn_deport_categorias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras_auspicios          postgres    false    13    462            �           1259    86336    ptn_deport_disciplina    TABLE     �   CREATE TABLE maestras_auspicios.ptn_deport_disciplina (
    id bigint NOT NULL,
    activo boolean,
    descripcion character varying(50) NOT NULL,
    nombre character varying(15) NOT NULL
);
 5   DROP TABLE maestras_auspicios.ptn_deport_disciplina;
       maestras_auspicios         heap    postgres    false    13            �           1259    86334    ptn_deport_disciplina_id_seq    SEQUENCE       ALTER TABLE maestras_auspicios.ptn_deport_disciplina ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras_auspicios.ptn_deport_disciplina_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras_auspicios          postgres    false    464    13            �           1259    86343    ptn_deport_modalidad    TABLE     �   CREATE TABLE maestras_auspicios.ptn_deport_modalidad (
    id bigint NOT NULL,
    activo boolean,
    descripcion character varying(50) NOT NULL,
    nombre character varying(25) NOT NULL
);
 4   DROP TABLE maestras_auspicios.ptn_deport_modalidad;
       maestras_auspicios         heap    postgres    false    13            �           1259    86341    ptn_deport_modalidad_id_seq    SEQUENCE     �   ALTER TABLE maestras_auspicios.ptn_deport_modalidad ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME maestras_auspicios.ptn_deport_modalidad_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            maestras_auspicios          postgres    false    13    466            5           1259    68322    mult_cuentas_internas    TABLE     &  CREATE TABLE multiplo.mult_cuentas_internas (
    id_cuenta character varying(50) NOT NULL,
    clave character varying(100) NOT NULL,
    cuenta_activa character varying(1),
    email character varying(100) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    inicios_erroneos integer,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    id_persona character varying(255) NOT NULL
);
 +   DROP TABLE multiplo.mult_cuentas_internas;
       multiplo         heap    postgres    false    14            6           1259    68328    mult_menu_int    TABLE     {  CREATE TABLE multiplo.mult_menu_int (
    id_menu bigint NOT NULL,
    descripcion character varying(150) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    nombre character varying(50) NOT NULL,
    orden integer NOT NULL,
    url character varying(250),
    url_icono character varying(250),
    id_padre bigint
);
 #   DROP TABLE multiplo.mult_menu_int;
       multiplo         heap    postgres    false    14            7           1259    68334    mult_menu_int_id_menu_seq    SEQUENCE     �   ALTER TABLE multiplo.mult_menu_int ALTER COLUMN id_menu ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME multiplo.mult_menu_int_id_menu_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            multiplo          postgres    false    310    14            8           1259    68336    mult_menu_operacion_int    TABLE     �   CREATE TABLE multiplo.mult_menu_operacion_int (
    id_menu bigint NOT NULL,
    id_operacion bigint NOT NULL,
    id_rol_int bigint NOT NULL
);
 -   DROP TABLE multiplo.mult_menu_operacion_int;
       multiplo         heap    postgres    false    14            9           1259    68339    mult_operaciones_int    TABLE       CREATE TABLE multiplo.mult_operaciones_int (
    id_operacion bigint NOT NULL,
    descripcion character varying(150) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    nombre character varying(50) NOT NULL
);
 *   DROP TABLE multiplo.mult_operaciones_int;
       multiplo         heap    postgres    false    14            :           1259    68342 %   mult_operaciones_int_id_operacion_seq    SEQUENCE     �   ALTER TABLE multiplo.mult_operaciones_int ALTER COLUMN id_operacion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME multiplo.mult_operaciones_int_id_operacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            multiplo          postgres    false    14    313            ;           1259    68344    mult_personal_interno    TABLE     &  CREATE TABLE multiplo.mult_personal_interno (
    id_pers_interno character varying(255) NOT NULL,
    apellidos character varying(50) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    id_jefe bigint,
    iniciales character varying(5) NOT NULL,
    nombres character varying(50) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    cuenta_interna character varying(50)
);
 +   DROP TABLE multiplo.mult_personal_interno;
       multiplo         heap    postgres    false    14            <           1259    68350    mult_roles_cuentas_internas    TABLE     �   CREATE TABLE multiplo.mult_roles_cuentas_internas (
    cuenta_interna character varying(255) NOT NULL,
    rol bigint NOT NULL,
    fecha_creacion timestamp without time zone,
    usuari_creacion character varying(50) NOT NULL
);
 1   DROP TABLE multiplo.mult_roles_cuentas_internas;
       multiplo         heap    postgres    false    14            =           1259    68353    mult_roles_int    TABLE     �   CREATE TABLE multiplo.mult_roles_int (
    id_rol bigint NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(50) NOT NULL,
    ruta character varying(250)
);
 $   DROP TABLE multiplo.mult_roles_int;
       multiplo         heap    postgres    false    14            >           1259    68356    mult_roles_int_id_rol_seq    SEQUENCE     �   ALTER TABLE multiplo.mult_roles_int ALTER COLUMN id_rol ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME multiplo.mult_roles_int_id_rol_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            multiplo          postgres    false    317    14            ?           1259    68358    mult_documentos    TABLE     �  CREATE TABLE multiplo_documentos.mult_documentos (
    id_documento bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    nombre character varying(100) NOT NULL,
    ruta character varying(200) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    version integer NOT NULL,
    id_tipo_documento bigint NOT NULL
);
 0   DROP TABLE multiplo_documentos.mult_documentos;
       multiplo_documentos         heap    postgres    false    15            @           1259    68361    mult_documentos_facturas    TABLE     �  CREATE TABLE multiplo_documentos.mult_documentos_facturas (
    id bigint NOT NULL,
    codigo_factura character varying(255) NOT NULL,
    estado character varying(255) NOT NULL,
    fecha_emision timestamp without time zone NOT NULL,
    id_cliente character varying(255) NOT NULL,
    numero_documento character varying(255) NOT NULL,
    numero_establecimiento character varying(255) NOT NULL,
    numero_facturero character varying(255) NOT NULL,
    total_factura numeric(12,2) NOT NULL
);
 9   DROP TABLE multiplo_documentos.mult_documentos_facturas;
       multiplo_documentos         heap    postgres    false    15            A           1259    68367    mult_documentos_facturas_id_seq    SEQUENCE     	  ALTER TABLE multiplo_documentos.mult_documentos_facturas ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME multiplo_documentos.mult_documentos_facturas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            multiplo_documentos          postgres    false    15    320            B           1259    68369     mult_documentos_id_documento_seq    SEQUENCE       ALTER TABLE multiplo_documentos.mult_documentos ALTER COLUMN id_documento ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME multiplo_documentos.mult_documentos_id_documento_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            multiplo_documentos          postgres    false    15    319            C           1259    68371    mult_plantillas_documentos    TABLE     �   CREATE TABLE multiplo_documentos.mult_plantillas_documentos (
    id_plantilla character varying(20) NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(100) NOT NULL,
    plantilla json
);
 ;   DROP TABLE multiplo_documentos.mult_plantillas_documentos;
       multiplo_documentos         heap    postgres    false    15            D           1259    68377    mult_conciliacion_aprobada    TABLE     �   CREATE TABLE negocio.mult_conciliacion_aprobada (
    id bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha timestamp without time zone NOT NULL,
    monto_conciliado numeric(12,2) NOT NULL,
    usuario character varying(50) NOT NULL
);
 /   DROP TABLE negocio.mult_conciliacion_aprobada;
       negocio         heap    postgres    false    16            E           1259    68380 "   mult_conciliacion_aprobada_detalle    TABLE       CREATE TABLE negocio.mult_conciliacion_aprobada_detalle (
    id bigint NOT NULL,
    concepto character varying(100) NOT NULL,
    fecha_efectivo date NOT NULL,
    lugar character varying(100) NOT NULL,
    monto numeric(12,2) NOT NULL,
    nombre_banco character varying(20) NOT NULL,
    numero_comprobante character varying(20) NOT NULL,
    numero_cuenta character varying(50) NOT NULL,
    observacion character varying(100) NOT NULL,
    tipo_transaccion character varying(20) NOT NULL,
    id_conciliacion bigint NOT NULL
);
 7   DROP TABLE negocio.mult_conciliacion_aprobada_detalle;
       negocio         heap    postgres    false    16            F           1259    68383 )   mult_conciliacion_aprobada_detalle_id_seq    SEQUENCE       ALTER TABLE negocio.mult_conciliacion_aprobada_detalle ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.mult_conciliacion_aprobada_detalle_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    16    325            G           1259    68385 !   mult_conciliacion_aprobada_id_seq    SEQUENCE     �   ALTER TABLE negocio.mult_conciliacion_aprobada ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.mult_conciliacion_aprobada_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    16    324            H           1259    68387    mult_conciliacion_detalle_xls    TABLE       CREATE TABLE negocio.mult_conciliacion_detalle_xls (
    id bigint NOT NULL,
    concepto character varying(100) NOT NULL,
    fecha_efectivo date NOT NULL,
    lugar character varying(100) NOT NULL,
    monto numeric(12,2) NOT NULL,
    nombre_banco character varying(20) NOT NULL,
    numero_comprobante character varying(20) NOT NULL,
    numero_cuenta character varying(50) NOT NULL,
    observacion character varying(100) NOT NULL,
    tipo_transaccion character varying(20) NOT NULL,
    id_conciliacion bigint NOT NULL
);
 2   DROP TABLE negocio.mult_conciliacion_detalle_xls;
       negocio         heap    postgres    false    16            I           1259    68390 $   mult_conciliacion_detalle_xls_id_seq    SEQUENCE     �   ALTER TABLE negocio.mult_conciliacion_detalle_xls ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.mult_conciliacion_detalle_xls_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    16    328            J           1259    68392    mult_conciliacion_xls    TABLE     "  CREATE TABLE negocio.mult_conciliacion_xls (
    id bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha timestamp without time zone NOT NULL,
    monto_conciliado numeric(12,2) NOT NULL,
    monto_total numeric(12,2) NOT NULL,
    usuario character varying(50) NOT NULL
);
 *   DROP TABLE negocio.mult_conciliacion_xls;
       negocio         heap    postgres    false    16            K           1259    68395    mult_conciliacion_xls_id_seq    SEQUENCE     �   ALTER TABLE negocio.mult_conciliacion_xls ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.mult_conciliacion_xls_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    16    330            L           1259    68397    mult_fecha_gen_tbl_amort    TABLE     �   CREATE TABLE negocio.mult_fecha_gen_tbl_amort (
    id bigint NOT NULL,
    fecha_creacion date NOT NULL,
    fecha_generacion date NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    id_proyecto character varying(50) NOT NULL
);
 -   DROP TABLE negocio.mult_fecha_gen_tbl_amort;
       negocio         heap    postgres    false    16            M           1259    68400    mult_fecha_gen_tbl_amort_id_seq    SEQUENCE     �   ALTER TABLE negocio.mult_fecha_gen_tbl_amort ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.mult_fecha_gen_tbl_amort_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    16    332            N           1259    68402    mult_porc_interes_tbl_proyecto    TABLE     �  CREATE TABLE negocio.mult_porc_interes_tbl_proyecto (
    id bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    porcentaje_interes numeric(5,2) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    codigo_proyecto character varying(50) NOT NULL,
    id_tipo_tabla bigint NOT NULL
);
 3   DROP TABLE negocio.mult_porc_interes_tbl_proyecto;
       negocio         heap    postgres    false    16            O           1259    68405 %   mult_porc_interes_tbl_proyecto_id_seq    SEQUENCE     �   ALTER TABLE negocio.mult_porc_interes_tbl_proyecto ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.mult_porc_interes_tbl_proyecto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    16    334            �           1259    86350    ptn_conciliacion_aprobada    TABLE     �   CREATE TABLE negocio.ptn_conciliacion_aprobada (
    id bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha timestamp without time zone NOT NULL,
    monto_conciliado numeric(12,2) NOT NULL,
    usuario character varying(50) NOT NULL
);
 .   DROP TABLE negocio.ptn_conciliacion_aprobada;
       negocio         heap    postgres    false    16            �           1259    86357 !   ptn_conciliacion_aprobada_detalle    TABLE       CREATE TABLE negocio.ptn_conciliacion_aprobada_detalle (
    id bigint NOT NULL,
    concepto character varying(100) NOT NULL,
    fecha_efectivo date NOT NULL,
    lugar character varying(100) NOT NULL,
    monto numeric(12,2) NOT NULL,
    nombre_banco character varying(20) NOT NULL,
    numero_comprobante character varying(20) NOT NULL,
    numero_cuenta character varying(50) NOT NULL,
    observacion character varying(100) NOT NULL,
    tipo_transaccion character varying(20) NOT NULL,
    id_conciliacion bigint NOT NULL
);
 6   DROP TABLE negocio.ptn_conciliacion_aprobada_detalle;
       negocio         heap    postgres    false    16            �           1259    86355 (   ptn_conciliacion_aprobada_detalle_id_seq    SEQUENCE       ALTER TABLE negocio.ptn_conciliacion_aprobada_detalle ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.ptn_conciliacion_aprobada_detalle_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    16    470            �           1259    86348     ptn_conciliacion_aprobada_id_seq    SEQUENCE     �   ALTER TABLE negocio.ptn_conciliacion_aprobada ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.ptn_conciliacion_aprobada_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    16    468            �           1259    86364    ptn_conciliacion_detalle_xls    TABLE       CREATE TABLE negocio.ptn_conciliacion_detalle_xls (
    id bigint NOT NULL,
    concepto character varying(100) NOT NULL,
    fecha_efectivo date NOT NULL,
    lugar character varying(100) NOT NULL,
    monto numeric(12,2) NOT NULL,
    nombre_banco character varying(20) NOT NULL,
    numero_comprobante character varying(20) NOT NULL,
    numero_cuenta character varying(50) NOT NULL,
    observacion character varying(100) NOT NULL,
    tipo_transaccion character varying(20) NOT NULL,
    id_conciliacion bigint NOT NULL
);
 1   DROP TABLE negocio.ptn_conciliacion_detalle_xls;
       negocio         heap    postgres    false    16            �           1259    86362 #   ptn_conciliacion_detalle_xls_id_seq    SEQUENCE     �   ALTER TABLE negocio.ptn_conciliacion_detalle_xls ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.ptn_conciliacion_detalle_xls_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    472    16            �           1259    86371    ptn_conciliacion_xls    TABLE     !  CREATE TABLE negocio.ptn_conciliacion_xls (
    id bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha timestamp without time zone NOT NULL,
    monto_conciliado numeric(12,2) NOT NULL,
    monto_total numeric(12,2) NOT NULL,
    usuario character varying(50) NOT NULL
);
 )   DROP TABLE negocio.ptn_conciliacion_xls;
       negocio         heap    postgres    false    16            �           1259    86369    ptn_conciliacion_xls_id_seq    SEQUENCE     �   ALTER TABLE negocio.ptn_conciliacion_xls ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.ptn_conciliacion_xls_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    474    16            �           1259    86378    ptn_fecha_gen_tbl_amort    TABLE     �   CREATE TABLE negocio.ptn_fecha_gen_tbl_amort (
    id bigint NOT NULL,
    fecha_creacion date NOT NULL,
    fecha_generacion date NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    id_proyecto character varying(50) NOT NULL
);
 ,   DROP TABLE negocio.ptn_fecha_gen_tbl_amort;
       negocio         heap    postgres    false    16            �           1259    86376    ptn_fecha_gen_tbl_amort_id_seq    SEQUENCE     �   ALTER TABLE negocio.ptn_fecha_gen_tbl_amort ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.ptn_fecha_gen_tbl_amort_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    476    16            �           1259    86385    ptn_porc_interes_tbl_proyecto    TABLE     �  CREATE TABLE negocio.ptn_porc_interes_tbl_proyecto (
    id bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    porcentaje_interes numeric(5,2) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    codigo_proyecto character varying(50) NOT NULL,
    id_tipo_tabla bigint NOT NULL
);
 2   DROP TABLE negocio.ptn_porc_interes_tbl_proyecto;
       negocio         heap    postgres    false    16            �           1259    86383 $   ptn_porc_interes_tbl_proyecto_id_seq    SEQUENCE     �   ALTER TABLE negocio.ptn_porc_interes_tbl_proyecto ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME negocio.ptn_porc_interes_tbl_proyecto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            negocio          postgres    false    478    16            P           1259    68407    mult_parametros    TABLE       CREATE TABLE parametrizacion.mult_parametros (
    id_parametro bigint NOT NULL,
    cod_parametro character varying(20) NOT NULL,
    descripcion character varying(200) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    parametro character varying(15) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    valor character varying(200) NOT NULL
);
 ,   DROP TABLE parametrizacion.mult_parametros;
       parametrizacion         heap    postgres    false    17            Q           1259    68413     mult_parametros_id_parametro_seq    SEQUENCE       ALTER TABLE parametrizacion.mult_parametros ALTER COLUMN id_parametro ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME parametrizacion.mult_parametros_id_parametro_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            parametrizacion          postgres    false    336    17            �           1259    86394    ptn_parametros    TABLE     
  CREATE TABLE parametrizacion.ptn_parametros (
    id_parametro bigint NOT NULL,
    cod_parametro character varying(20) NOT NULL,
    descripcion character varying(200) NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    parametro character varying(15) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    valor character varying(100) NOT NULL
);
 +   DROP TABLE parametrizacion.ptn_parametros;
       parametrizacion         heap    postgres    false    17            �           1259    86392    ptn_parametros_id_parametro_seq    SEQUENCE       ALTER TABLE parametrizacion.ptn_parametros ALTER COLUMN id_parametro ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME parametrizacion.ptn_parametros_id_parametro_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            parametrizacion          postgres    false    480    17            R           1259    68415    mult_detalle_porc_sol_aprobadas    TABLE       CREATE TABLE promotor.mult_detalle_porc_sol_aprobadas (
    id bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    numero_solicitud character varying(20) NOT NULL,
    id_porc_sol_aprobada bigint NOT NULL
);
 5   DROP TABLE promotor.mult_detalle_porc_sol_aprobadas;
       promotor         heap    postgres    false    18            S           1259    68418 &   mult_detalle_porc_sol_aprobadas_id_seq    SEQUENCE       ALTER TABLE promotor.mult_detalle_porc_sol_aprobadas ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_detalle_porc_sol_aprobadas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    338    18            T           1259    68420    mult_documentos_financieros    TABLE     �  CREATE TABLE promotor.mult_documentos_financieros (
    id bigint NOT NULL,
    activo boolean,
    anexo_cts_cobrar character varying(255),
    estado_financiero_actual character varying(255),
    estado_financiero_aa character varying(255),
    fecha_creacion timestamp without time zone,
    impuesto_renta_aa character varying(255),
    usuario_creacion character varying(255),
    empresa bigint
);
 1   DROP TABLE promotor.mult_documentos_financieros;
       promotor         heap    postgres    false    18            U           1259    68426 "   mult_documentos_financieros_id_seq    SEQUENCE     �   ALTER TABLE promotor.mult_documentos_financieros ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_documentos_financieros_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    340            V           1259    68428    mult_documentos_juridicos    TABLE       CREATE TABLE promotor.mult_documentos_juridicos (
    id bigint NOT NULL,
    activo boolean,
    cedula_rl character varying(255),
    escritura character varying(255),
    estatutos_vigentes character varying(255),
    fecha_creacion timestamp without time zone,
    identificaciones_accionista character varying(255),
    nombramiento_rl character varying(255),
    nomina_accionista character varying(255),
    ruc_vigente character varying(255),
    usuario_creacion character varying(255),
    empresa bigint
);
 /   DROP TABLE promotor.mult_documentos_juridicos;
       promotor         heap    postgres    false    18            W           1259    68434     mult_documentos_juridicos_id_seq    SEQUENCE     �   ALTER TABLE promotor.mult_documentos_juridicos ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_documentos_juridicos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    342    18            X           1259    68436    mult_empresa_datos_anuales    TABLE     [  CREATE TABLE promotor.mult_empresa_datos_anuales (
    id bigint NOT NULL,
    activo boolean,
    anio integer NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    margen_contribucion numeric(12,2),
    usuario_creacion character varying(20) NOT NULL,
    venta_totales numeric(12,2) NOT NULL,
    id_empresa bigint NOT NULL
);
 0   DROP TABLE promotor.mult_empresa_datos_anuales;
       promotor         heap    postgres    false    18            Y           1259    68439 !   mult_empresa_datos_anuales_id_seq    SEQUENCE     �   ALTER TABLE promotor.mult_empresa_datos_anuales ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_empresa_datos_anuales_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    344    18            Z           1259    68441    mult_empresas    TABLE     �  CREATE TABLE promotor.mult_empresas (
    id_empresa bigint NOT NULL,
    antecedente json NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    nombre character varying(100) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    ventaja_competitiva json NOT NULL,
    actividad bigint NOT NULL,
    pais bigint NOT NULL,
    ruc character varying(13),
    ciudad character varying(20),
    descripcion_producto character varying(200),
    direccion character varying(100),
    cuenta character varying(50),
    dato_anual_actual bigint
);
 #   DROP TABLE promotor.mult_empresas;
       promotor         heap    postgres    false    18            [           1259    68447    mult_empresas_id_empresa_seq    SEQUENCE     �   ALTER TABLE promotor.mult_empresas ALTER COLUMN id_empresa ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_empresas_id_empresa_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    346    18            \           1259    68449    mult_indicadores    TABLE     �  CREATE TABLE promotor.mult_indicadores (
    id_indicador bigint NOT NULL,
    anio bigint NOT NULL,
    estado character varying(1) NOT NULL,
    garantia text NOT NULL,
    liquidez text NOT NULL,
    porcentaje_garantia double precision NOT NULL,
    porcentaje_liquidez double precision NOT NULL,
    porcentaje_retorno_capital double precision NOT NULL,
    porcentaje_solvencia double precision NOT NULL,
    retorno_capital text NOT NULL,
    solvencia text NOT NULL
);
 &   DROP TABLE promotor.mult_indicadores;
       promotor         heap    postgres    false    18            ]           1259    68455 !   mult_indicadores_id_indicador_seq    SEQUENCE     �   ALTER TABLE promotor.mult_indicadores ALTER COLUMN id_indicador ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_indicadores_id_indicador_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    348            ^           1259    68457    mult_porc_sol_aprobadas    TABLE     �  CREATE TABLE promotor.mult_porc_sol_aprobadas (
    id bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_aprobacion date NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    monto_aprobado numeric(12,2),
    monto_solicitado numeric(12,2),
    observacion character varying(200) NOT NULL,
    porcentaje_aprobado numeric(5,2) NOT NULL,
    usuario_aprobacion character varying(50) NOT NULL,
    codigo_proyecto character varying(50) NOT NULL
);
 -   DROP TABLE promotor.mult_porc_sol_aprobadas;
       promotor         heap    postgres    false    18            _           1259    68460    mult_porc_sol_aprobadas_id_seq    SEQUENCE     �   ALTER TABLE promotor.mult_porc_sol_aprobadas ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_porc_sol_aprobadas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    350            `           1259    68462    mult_proyectos    TABLE     �  CREATE TABLE promotor.mult_proyectos (
    id_proyecto character varying(50) NOT NULL,
    destino_financiamiento character varying(100) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_inicio_inversion date,
    fecha_limite_inversion date,
    fecha_modificacion timestamp without time zone,
    monto_solicitado numeric(12,2) NOT NULL,
    pago_capital character varying(50) NOT NULL,
    pago_interes character varying(15) NOT NULL,
    plazo integer NOT NULL,
    tasa_efectiva_anual numeric(5,2) NOT NULL,
    tipo_inversion character varying(50) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    calificacion_interna bigint,
    id_empresa bigint NOT NULL,
    estado_actual character varying(5) NOT NULL,
    estado_anterior character varying(5),
    id_indicador bigint,
    monto_recaudado numeric(12,2),
    tabla_amortizacion bigint,
    ultimo_historial bigint,
    ronda integer,
    periodo_pago integer,
    acepta_informacion_correcta character varying(1),
    acepta_ingresar_info_vigente character varying(1),
    acepta_licitud_fondos character varying(1)
);
 $   DROP TABLE promotor.mult_proyectos;
       promotor         heap    postgres    false    18            a           1259    68465    mult_proyectos_cuentas    TABLE       CREATE TABLE promotor.mult_proyectos_cuentas (
    id_proyecto_cuenta bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    numero_cuenta character varying(20) NOT NULL,
    tipo_cuenta character varying(10) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    id_banco bigint NOT NULL,
    id_proyecto character varying(50) NOT NULL
);
 ,   DROP TABLE promotor.mult_proyectos_cuentas;
       promotor         heap    postgres    false    18            b           1259    68468 -   mult_proyectos_cuentas_id_proyecto_cuenta_seq    SEQUENCE       ALTER TABLE promotor.mult_proyectos_cuentas ALTER COLUMN id_proyecto_cuenta ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_proyectos_cuentas_id_proyecto_cuenta_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    353            c           1259    68470    mult_proyectos_rutas    TABLE       CREATE TABLE promotor.mult_proyectos_rutas (
    id_proyecto_ruta bigint NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(50) NOT NULL,
    ruta character varying(200) NOT NULL,
    id_proyecto character varying(50) NOT NULL
);
 *   DROP TABLE promotor.mult_proyectos_rutas;
       promotor         heap    postgres    false    18            d           1259    68473 )   mult_proyectos_rutas_id_proyecto_ruta_seq    SEQUENCE       ALTER TABLE promotor.mult_proyectos_rutas ALTER COLUMN id_proyecto_ruta ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_proyectos_rutas_id_proyecto_ruta_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    355            e           1259    68475    mult_rango_pagos    TABLE     �   CREATE TABLE promotor.mult_rango_pagos (
    id bigint NOT NULL,
    activo boolean NOT NULL,
    max numeric(12,2) NOT NULL,
    min numeric(12,2) NOT NULL,
    usuario_creacion character varying(255) NOT NULL,
    valor numeric(12,2) NOT NULL
);
 &   DROP TABLE promotor.mult_rango_pagos;
       promotor         heap    postgres    false    18            f           1259    68478    mult_rango_pagos_id_seq    SEQUENCE     �   ALTER TABLE promotor.mult_rango_pagos ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_rango_pagos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    357            g           1259    68480    mult_tipo_actividades    TABLE     �   CREATE TABLE promotor.mult_tipo_actividades (
    id_actividad bigint NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(100) NOT NULL
);
 +   DROP TABLE promotor.mult_tipo_actividades;
       promotor         heap    postgres    false    18            h           1259    68483 &   mult_tipo_actividades_id_actividad_seq    SEQUENCE       ALTER TABLE promotor.mult_tipo_actividades ALTER COLUMN id_actividad ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_tipo_actividades_id_actividad_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    359            i           1259    68485    mult_tipo_calificaciones    TABLE     �   CREATE TABLE promotor.mult_tipo_calificaciones (
    id_tipo_calificacion bigint NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(50) NOT NULL
);
 .   DROP TABLE promotor.mult_tipo_calificaciones;
       promotor         heap    postgres    false    18            j           1259    68488 1   mult_tipo_calificaciones_id_tipo_calificacion_seq    SEQUENCE       ALTER TABLE promotor.mult_tipo_calificaciones ALTER COLUMN id_tipo_calificacion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.mult_tipo_calificaciones_id_tipo_calificacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    361            �           1259    86401    ptn_detalle_porc_sol_aprobadas    TABLE       CREATE TABLE promotor.ptn_detalle_porc_sol_aprobadas (
    id bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    numero_solicitud character varying(20) NOT NULL,
    id_porc_sol_aprobada bigint NOT NULL
);
 4   DROP TABLE promotor.ptn_detalle_porc_sol_aprobadas;
       promotor         heap    postgres    false    18            �           1259    86399 %   ptn_detalle_porc_sol_aprobadas_id_seq    SEQUENCE     �   ALTER TABLE promotor.ptn_detalle_porc_sol_aprobadas ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.ptn_detalle_porc_sol_aprobadas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    482    18            �           1259    86408    ptn_documentos_financieros    TABLE     �  CREATE TABLE promotor.ptn_documentos_financieros (
    id bigint NOT NULL,
    activo boolean,
    anexo_cts_cobrar character varying(255),
    estado_financiero_actual character varying(255),
    estado_financiero_aa character varying(255),
    fecha_creacion timestamp without time zone,
    impuesto_renta_aa character varying(255),
    usuario_creacion character varying(255),
    empresa bigint
);
 0   DROP TABLE promotor.ptn_documentos_financieros;
       promotor         heap    postgres    false    18            �           1259    86406 !   ptn_documentos_financieros_id_seq    SEQUENCE     �   ALTER TABLE promotor.ptn_documentos_financieros ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.ptn_documentos_financieros_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    484            �           1259    86418    ptn_documentos_juridicos    TABLE       CREATE TABLE promotor.ptn_documentos_juridicos (
    id bigint NOT NULL,
    activo boolean,
    cedula_rl character varying(255),
    escritura character varying(255),
    estatutos_vigentes character varying(255),
    fecha_creacion timestamp without time zone,
    identificaciones_accionista character varying(255),
    nombramiento_rl character varying(255),
    nomina_accionista character varying(255),
    ruc_vigente character varying(255),
    usuario_creacion character varying(255),
    empresa bigint
);
 .   DROP TABLE promotor.ptn_documentos_juridicos;
       promotor         heap    postgres    false    18            �           1259    86416    ptn_documentos_juridicos_id_seq    SEQUENCE     �   ALTER TABLE promotor.ptn_documentos_juridicos ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.ptn_documentos_juridicos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    486            �           1259    86428    ptn_empresa_datos_anuales    TABLE     Z  CREATE TABLE promotor.ptn_empresa_datos_anuales (
    id bigint NOT NULL,
    activo boolean,
    anio integer NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    margen_contribucion numeric(12,2),
    usuario_creacion character varying(20) NOT NULL,
    venta_totales numeric(12,2) NOT NULL,
    id_empresa bigint NOT NULL
);
 /   DROP TABLE promotor.ptn_empresa_datos_anuales;
       promotor         heap    postgres    false    18            �           1259    86426     ptn_empresa_datos_anuales_id_seq    SEQUENCE     �   ALTER TABLE promotor.ptn_empresa_datos_anuales ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.ptn_empresa_datos_anuales_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    488    18            �           1259    86435    ptn_empresas    TABLE     �  CREATE TABLE promotor.ptn_empresas (
    id_empresa bigint NOT NULL,
    antecedente json NOT NULL,
    ciudad character varying(20),
    descripcion_producto character varying(200),
    direccion character varying(100),
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    nombre character varying(100) NOT NULL,
    ruc character varying(13) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    ventaja_competitiva json NOT NULL,
    actividad bigint NOT NULL,
    cuenta character varying(50),
    dato_anual_actual bigint,
    pais bigint NOT NULL
);
 "   DROP TABLE promotor.ptn_empresas;
       promotor         heap    postgres    false    18            �           1259    86433    ptn_empresas_id_empresa_seq    SEQUENCE     �   ALTER TABLE promotor.ptn_empresas ALTER COLUMN id_empresa ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.ptn_empresas_id_empresa_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    490    18            �           1259    86445    ptn_indicadores    TABLE     �  CREATE TABLE promotor.ptn_indicadores (
    id_indicador bigint NOT NULL,
    anio bigint NOT NULL,
    estado character varying(1) NOT NULL,
    garantia text NOT NULL,
    liquidez text NOT NULL,
    porcentaje_garantia double precision NOT NULL,
    porcentaje_liquidez double precision NOT NULL,
    porcentaje_retorno_capital double precision NOT NULL,
    porcentaje_solvencia double precision NOT NULL,
    retorno_capital text NOT NULL,
    solvencia text NOT NULL
);
 %   DROP TABLE promotor.ptn_indicadores;
       promotor         heap    postgres    false    18            �           1259    86443     ptn_indicadores_id_indicador_seq    SEQUENCE     �   ALTER TABLE promotor.ptn_indicadores ALTER COLUMN id_indicador ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.ptn_indicadores_id_indicador_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    492            �           1259    86455    ptn_porc_sol_aprobadas    TABLE     �  CREATE TABLE promotor.ptn_porc_sol_aprobadas (
    id bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_aprobacion date NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    monto_aprobado numeric(12,2),
    monto_solicitado numeric(12,2),
    observacion character varying(200) NOT NULL,
    porcentaje_aprobado numeric(5,2) NOT NULL,
    usuario_aprobacion character varying(50) NOT NULL,
    codigo_proyecto character varying(50) NOT NULL
);
 ,   DROP TABLE promotor.ptn_porc_sol_aprobadas;
       promotor         heap    postgres    false    18            �           1259    86453    ptn_porc_sol_aprobadas_id_seq    SEQUENCE     �   ALTER TABLE promotor.ptn_porc_sol_aprobadas ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.ptn_porc_sol_aprobadas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    494            �           1259    86460    ptn_proyectos    TABLE     �  CREATE TABLE promotor.ptn_proyectos (
    id_proyecto character varying(50) NOT NULL,
    acepta_informacion_correcta character varying(1),
    acepta_ingresar_info_vigente character varying(1),
    acepta_licitud_fondos character varying(1),
    destino_financiamiento character varying(100) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_inicio_inversion date,
    fecha_limite_inversion date,
    fecha_modificacion timestamp without time zone,
    monto_recaudado numeric(12,2),
    monto_solicitado numeric(12,2) NOT NULL,
    pago_capital character varying(50) NOT NULL,
    pago_interes character varying(15) NOT NULL,
    periodo_pago integer NOT NULL,
    plazo integer NOT NULL,
    ronda integer NOT NULL,
    tasa_efectiva_anual numeric(5,2) NOT NULL,
    tipo_inversion character varying(50) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    tabla_amortizacion bigint,
    calificacion_interna bigint,
    id_empresa bigint NOT NULL,
    estado_actual character varying(5) NOT NULL,
    estado_anterior character varying(5),
    ultimo_historial bigint,
    id_indicador bigint
);
 #   DROP TABLE promotor.ptn_proyectos;
       promotor         heap    postgres    false    18            �           1259    86467    ptn_proyectos_cuentas    TABLE       CREATE TABLE promotor.ptn_proyectos_cuentas (
    id_proyecto_cuenta bigint NOT NULL,
    estado character varying(1) NOT NULL,
    fecha_creacion timestamp without time zone NOT NULL,
    fecha_modificacion timestamp without time zone,
    numero_cuenta character varying(20) NOT NULL,
    tipo_cuenta character varying(10) NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    usuario_modificacion character varying(50),
    id_banco bigint NOT NULL,
    id_proyecto character varying(50) NOT NULL
);
 +   DROP TABLE promotor.ptn_proyectos_cuentas;
       promotor         heap    postgres    false    18            �           1259    86465 ,   ptn_proyectos_cuentas_id_proyecto_cuenta_seq    SEQUENCE       ALTER TABLE promotor.ptn_proyectos_cuentas ALTER COLUMN id_proyecto_cuenta ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.ptn_proyectos_cuentas_id_proyecto_cuenta_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    497            �           1259    86474    ptn_proyectos_rutas    TABLE       CREATE TABLE promotor.ptn_proyectos_rutas (
    id_proyecto_ruta bigint NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(50) NOT NULL,
    ruta character varying(200) NOT NULL,
    id_proyecto character varying(50) NOT NULL
);
 )   DROP TABLE promotor.ptn_proyectos_rutas;
       promotor         heap    postgres    false    18            �           1259    86472 (   ptn_proyectos_rutas_id_proyecto_ruta_seq    SEQUENCE       ALTER TABLE promotor.ptn_proyectos_rutas ALTER COLUMN id_proyecto_ruta ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.ptn_proyectos_rutas_id_proyecto_ruta_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    499            �           1259    86481    ptn_tipo_actividades    TABLE     �   CREATE TABLE promotor.ptn_tipo_actividades (
    id_actividad bigint NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(100) NOT NULL
);
 *   DROP TABLE promotor.ptn_tipo_actividades;
       promotor         heap    postgres    false    18            �           1259    86479 %   ptn_tipo_actividades_id_actividad_seq    SEQUENCE     �   ALTER TABLE promotor.ptn_tipo_actividades ALTER COLUMN id_actividad ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.ptn_tipo_actividades_id_actividad_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    501            �           1259    86488    ptn_tipo_calificaciones    TABLE     �   CREATE TABLE promotor.ptn_tipo_calificaciones (
    id_tipo_calificacion bigint NOT NULL,
    estado character varying(1) NOT NULL,
    nombre character varying(50) NOT NULL
);
 -   DROP TABLE promotor.ptn_tipo_calificaciones;
       promotor         heap    postgres    false    18            �           1259    86486 0   ptn_tipo_calificaciones_id_tipo_calificacion_seq    SEQUENCE       ALTER TABLE promotor.ptn_tipo_calificaciones ALTER COLUMN id_tipo_calificacion ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME promotor.ptn_tipo_calificaciones_id_tipo_calificacion_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);
            promotor          postgres    false    18    503            k           1259    68490    hibernate_sequence    SEQUENCE     {   CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 )   DROP SEQUENCE public.hibernate_sequence;
       public          postgres    false    4            l           1259    68492    mult_cuentas    TABLE     �  CREATE TABLE public.mult_cuentas (
    id_cuenta integer NOT NULL,
    id_rol integer NOT NULL,
    usuario character varying(50) NOT NULL,
    identificacion character varying(13) NOT NULL,
    email character varying(100) NOT NULL,
    clave character varying(100) NOT NULL,
    tipo_contacto character varying(10) NOT NULL,
    usuario_contacto character varying(50),
    acepta_politica_privacidad character(1),
    acepta_termino_uso character(1),
    cuenta_activa character(1),
    inicios_erroneos integer,
    fecha_creacion timestamp without time zone NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    fecha_modificacion timestamp without time zone,
    usuario_modificacion character varying(50),
    estado character(1) NOT NULL
);
     DROP TABLE public.mult_cuentas;
       public         heap    postgres    false    4            m           1259    68495    mult_cuentas_id_cuenta_seq    SEQUENCE     �   CREATE SEQUENCE public.mult_cuentas_id_cuenta_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 1   DROP SEQUENCE public.mult_cuentas_id_cuenta_seq;
       public          postgres    false    4    364                       0    0    mult_cuentas_id_cuenta_seq    SEQUENCE OWNED BY     Y   ALTER SEQUENCE public.mult_cuentas_id_cuenta_seq OWNED BY public.mult_cuentas.id_cuenta;
          public          postgres    false    365            n           1259    68497    mult_empleados    TABLE       CREATE TABLE public.mult_empleados (
    id_empleado integer NOT NULL,
    usuario character varying(50) NOT NULL,
    nombres_completos character varying(100) NOT NULL,
    iniciales character varying(4) NOT NULL,
    email character varying(100) NOT NULL,
    id_jefe integer,
    fecha_creacion timestamp without time zone NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    fecha_modificacion timestamp without time zone,
    usuario_modificacion character varying(50),
    estado character(1) NOT NULL
);
 "   DROP TABLE public.mult_empleados;
       public         heap    postgres    false    4            o           1259    68500    mult_empleados_id_empleado_seq    SEQUENCE     �   CREATE SEQUENCE public.mult_empleados_id_empleado_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 5   DROP SEQUENCE public.mult_empleados_id_empleado_seq;
       public          postgres    false    4    366                       0    0    mult_empleados_id_empleado_seq    SEQUENCE OWNED BY     a   ALTER SEQUENCE public.mult_empleados_id_empleado_seq OWNED BY public.mult_empleados.id_empleado;
          public          postgres    false    367            p           1259    68502    mult_personas    TABLE     �  CREATE TABLE public.mult_personas (
    id_persona integer NOT NULL,
    identificacion character varying(13) NOT NULL,
    id_cuenta integer NOT NULL,
    tipo_cliente character varying(15) NOT NULL,
    tipo_persona character varying(3) NOT NULL,
    tipo_identificacion character varying(3) NOT NULL,
    nacionalidad character varying(2) NOT NULL,
    nombres character varying(50),
    apellidos character varying(50),
    fecha_nacimiento date,
    email character varying(100) NOT NULL,
    numero_celular character varying(10) NOT NULL,
    razon_social character varying(100),
    nombre_contacto character varying(100),
    cargo_contacto character varying(50),
    email_contacto character varying(100),
    anio_inicio_actividad integer,
    fecha_vigencia date,
    fecha_creacion timestamp without time zone NOT NULL,
    usuario_creacion character varying(50) NOT NULL,
    fecha_modificacion timestamp without time zone,
    usuario_modificacion character varying(50),
    estado character(1) NOT NULL
);
 !   DROP TABLE public.mult_personas;
       public         heap    postgres    false    4            q           1259    68508    mult_personas_idpersona_seq    SEQUENCE     �   CREATE SEQUENCE public.mult_personas_idpersona_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 9999999999
    CACHE 1;
 2   DROP SEQUENCE public.mult_personas_idpersona_seq;
       public          postgres    false    4            r           1259    68510 
   mult_roles    TABLE     k   CREATE TABLE public.mult_roles (
    id_rol integer NOT NULL,
    nombre character varying(30) NOT NULL
);
    DROP TABLE public.mult_roles;
       public         heap    postgres    false    4            s           1259    68513    mult_roles_id_rol_seq    SEQUENCE     �   CREATE SEQUENCE public.mult_roles_id_rol_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 ,   DROP SEQUENCE public.mult_roles_id_rol_seq;
       public          postgres    false    4    370                       0    0    mult_roles_id_rol_seq    SEQUENCE OWNED BY     O   ALTER SEQUENCE public.mult_roles_id_rol_seq OWNED BY public.mult_roles.id_rol;
          public          postgres    false    371            t           1259    68515 
   mult_token    TABLE     �   CREATE TABLE public.mult_token (
    id_token integer NOT NULL,
    token character varying(255),
    fecha_creacion timestamp without time zone,
    id_cuenta integer NOT NULL
);
    DROP TABLE public.mult_token;
       public         heap    postgres    false    4            u           1259    68518    mult_token_id_token_seq    SEQUENCE     �   CREATE SEQUENCE public.mult_token_id_token_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 .   DROP SEQUENCE public.mult_token_id_token_seq;
       public          postgres    false    372    4                        0    0    mult_token_id_token_seq    SEQUENCE OWNED BY     S   ALTER SEQUENCE public.mult_token_id_token_seq OWNED BY public.mult_token.id_token;
          public          postgres    false    373            v           1259    68520    mult_usuarios    TABLE     �   CREATE TABLE public.mult_usuarios (
    id_usuario character varying(50) NOT NULL,
    clave character varying(100) NOT NULL,
    id_rol integer NOT NULL
);
 !   DROP TABLE public.mult_usuarios;
       public         heap    postgres    false    4            �           2604    68523    mult_cuentas id_cuenta    DEFAULT     �   ALTER TABLE ONLY public.mult_cuentas ALTER COLUMN id_cuenta SET DEFAULT nextval('public.mult_cuentas_id_cuenta_seq'::regclass);
 E   ALTER TABLE public.mult_cuentas ALTER COLUMN id_cuenta DROP DEFAULT;
       public          postgres    false    365    364            �           2604    68524    mult_empleados id_empleado    DEFAULT     �   ALTER TABLE ONLY public.mult_empleados ALTER COLUMN id_empleado SET DEFAULT nextval('public.mult_empleados_id_empleado_seq'::regclass);
 I   ALTER TABLE public.mult_empleados ALTER COLUMN id_empleado DROP DEFAULT;
       public          postgres    false    367    366            �           2604    68525    mult_roles id_rol    DEFAULT     v   ALTER TABLE ONLY public.mult_roles ALTER COLUMN id_rol SET DEFAULT nextval('public.mult_roles_id_rol_seq'::regclass);
 @   ALTER TABLE public.mult_roles ALTER COLUMN id_rol DROP DEFAULT;
       public          postgres    false    371    370            �           2604    68526    mult_token id_token    DEFAULT     z   ALTER TABLE ONLY public.mult_token ALTER COLUMN id_token SET DEFAULT nextval('public.mult_token_id_token_seq'::regclass);
 B   ALTER TABLE public.mult_token ALTER COLUMN id_token DROP DEFAULT;
       public          postgres    false    373    372            �          0    68043    mult_auspicios 
   TABLE DATA           �   COPY auspicios.mult_auspicios (id, activo, fecha_generacion, observacion, presupuesto_recaudado, presupuesto_solicitado, beneficiario, estado, id_valoracion) FROM stdin;
 	   auspicios          postgres    false    213   ��      �          0    68046    mult_auspicios_estados 
   TABLE DATA           \   COPY auspicios.mult_auspicios_estados (id_estado, descripcion, detalle, estado) FROM stdin;
 	   auspicios          postgres    false    214   ͗      �          0    68051    mult_auspicios_recompesas 
   TABLE DATA           d   COPY auspicios.mult_auspicios_recompesas (id, categoria, detalle, porcentaje, auspicio) FROM stdin;
 	   auspicios          postgres    false    216   l�      �          0    68056    mult_auspicios_torneos 
   TABLE DATA           ]   COPY auspicios.mult_auspicios_torneos (id, fecha, nombre_torneo, auspicio, pais) FROM stdin;
 	   auspicios          postgres    false    218   &�      �          0    68061    mult_auspicios_valoracion 
   TABLE DATA           �   COPY auspicios.mult_auspicios_valoracion (id, activo, anio, bianual, calificacion, fecha_caducidad, fecha_calificacion, presupuesto_aprobado, presupuesto_recaudado, presupuesto_restante, ruta_documento, beneficiario) FROM stdin;
 	   auspicios          postgres    false    220   ��      �          0    68069    mult_beneficiario 
   TABLE DATA           �   COPY auspicios.mult_beneficiario (id, ruta_foto1, ruta_foto2, categoria, disciplina, modalidad, id_persona, id_representante, activo, correo, perfil, titulo_actual, cuenta_bancaria) FROM stdin;
 	   auspicios          postgres    false    222   ��      �          0    68075    mult_titulos_deportivos 
   TABLE DATA           �   COPY auspicios.mult_titulos_deportivos (id, anio_titulo, nombre_competencia, otros, ranking_internacional, ranking_nacional, beneficiario, disciplina) FROM stdin;
 	   auspicios          postgres    false    223   �      �          0    85950    ptn_auspicios 
   TABLE DATA           �   COPY auspicios.ptn_auspicios (id, activo, fecha_generacion, observacion, presupuesto_recaudado, presupuesto_solicitado, beneficiario, estado, id_valoracion) FROM stdin;
 	   auspicios          postgres    false    376   ��      �          0    85955    ptn_auspicios_estados 
   TABLE DATA           [   COPY auspicios.ptn_auspicios_estados (id_estado, descripcion, detalle, estado) FROM stdin;
 	   auspicios          postgres    false    377   ��      �          0    85962    ptn_auspicios_recompesas 
   TABLE DATA           c   COPY auspicios.ptn_auspicios_recompesas (id, categoria, detalle, porcentaje, auspicio) FROM stdin;
 	   auspicios          postgres    false    379   ��      �          0    85969    ptn_auspicios_torneos 
   TABLE DATA           \   COPY auspicios.ptn_auspicios_torneos (id, fecha, nombre_torneo, auspicio, pais) FROM stdin;
 	   auspicios          postgres    false    381   �      �          0    85976    ptn_auspicios_valoracion 
   TABLE DATA           �   COPY auspicios.ptn_auspicios_valoracion (id, activo, anio, bianual, calificacion, fecha_caducidad, fecha_calificacion, presupuesto_aprobado, presupuesto_recaudado, presupuesto_restante, ruta_documento, beneficiario) FROM stdin;
 	   auspicios          postgres    false    383   7�      �          0    85984    ptn_beneficiario 
   TABLE DATA           �   COPY auspicios.ptn_beneficiario (id, activo, correo, perfil, ruta_foto1, ruta_foto2, titulo_actual, categoria, cuenta_bancaria, disciplina, modalidad, id_persona, id_representante) FROM stdin;
 	   auspicios          postgres    false    384   T�      �          0    85994    ptn_titulos_deportivos 
   TABLE DATA           �   COPY auspicios.ptn_titulos_deportivos (id, anio_titulo, nombre_competencia, otros, ranking_internacional, ranking_nacional, beneficiario, disciplina) FROM stdin;
 	   auspicios          postgres    false    386   q�      �          0    68083    mult_cuentas 
   TABLE DATA           6  COPY cuenta.mult_cuentas (id_cuenta, acepta_politica_privacidad, acepta_recibir_informacion, acepta_termino_uso, clave, cuenta_activa, email, estado, fecha_creacion, fecha_modificacion, inicios_erroneos, tipo_contacto, usuario, usuario_contacto, usuario_creacion, usuario_modificacion, id_persona) FROM stdin;
    cuenta          postgres    false    225   ��                 0    68086 	   mult_menu 
   TABLE DATA           z   COPY cuenta.mult_menu (id_menu, descripcion, estado, fecha_creacion, nombre, orden, url, id_padre, url_icono) FROM stdin;
    cuenta          postgres    false    226   s�                0    68094    mult_menu_operacion 
   TABLE DATA           L   COPY cuenta.mult_menu_operacion (id_menu, id_operacion, id_rol) FROM stdin;
    cuenta          postgres    false    228   ��                0    68097    mult_operaciones 
   TABLE DATA           e   COPY cuenta.mult_operaciones (id_operacion, descripcion, estado, fecha_creacion, nombre) FROM stdin;
    cuenta          postgres    false    229   C�                0    68102    mult_pers_cuentas 
   TABLE DATA           {   COPY cuenta.mult_pers_cuentas (id_pers_cuenta, estado, numero_cuenta, tipo_cuenta, titular, id_banco, persona) FROM stdin;
    cuenta          postgres    false    231   ��                0    68107    mult_pers_documentos 
   TABLE DATA           �   COPY cuenta.mult_pers_documentos (id_documento, estado, fecha_creacion, fecha_modificacion, nombre, nombre_post, ruta, ruta_post, usuario_creacion, usuario_modificacion, id_tipo_documento, id_info_adicional) FROM stdin;
    cuenta          postgres    false    233   ��      	          0    68115    mult_pers_domicilios 
   TABLE DATA           }   COPY cuenta.mult_pers_domicilios (id_domicilio, direccion, estado, numero_domicilio, sector, id_ciudad, id_pais) FROM stdin;
    cuenta          postgres    false    235   9�                0    68120    mult_pers_est_finan 
   TABLE DATA           �   COPY cuenta.mult_pers_est_finan (id_est_finan, egreso_anual, estado, ingreso_anual, total_activo, total_pasivo, total_patrimonio) FROM stdin;
    cuenta          postgres    false    237   ��                0    68125    mult_pers_firmas 
   TABLE DATA           f   COPY cuenta.mult_pers_firmas (id_firma, email, estado, identificacion, nombres_completos) FROM stdin;
    cuenta          postgres    false    239   ��                0    68130    mult_pers_info_adicional 
   TABLE DATA           �  COPY cuenta.mult_pers_info_adicional (id_info_adicional, actividad_economica, cargo_persona, estado, estado_civil, fecha_creacion, fecha_modificacion, fecha_registro, fuente_ingresos, numero_telefono, pais_domicilio_fiscal, residente_domicilio_fiscal, sexo, id_domicilio, id_est_finan_jur, id_firma_jur, id_repre_legal_jur, id_tipo_cuenta, id_persona, id_doc_identificacion, usuario_creacion, usuario_modificacion) FROM stdin;
    cuenta          postgres    false    241   ��                0    68135    mult_pers_repre_legal 
   TABLE DATA           �   COPY cuenta.mult_pers_repre_legal (id_repre_legal, apellidos, cargo, direccion_domicilio, email, estado, fecha_inicio_actividad, identificacion, nombres, numero_celular, numero_domicilio, telefono, id_pais) FROM stdin;
    cuenta          postgres    false    243   ��                0    68140    mult_personas 
   TABLE DATA           ^  COPY cuenta.mult_personas (identificacion, anio_inicio_actividad, apellidos, cargo_contacto, email_contacto, estado, fecha_creacion, fecha_modificacion, fecha_nacimiento, nacionalidad, nombre_contacto, nombres, numero_celular, razon_social, tipo_cliente, tipo_identificacion, tipo_persona, usuario_creacion, usuario_modificacion, genero) FROM stdin;
    cuenta          postgres    false    245   ��                0    68146 
   mult_roles 
   TABLE DATA           B   COPY cuenta.mult_roles (id_rol, estado, nombre, ruta) FROM stdin;
    cuenta          postgres    false    246   ��                0    68149    mult_roles_cuentas 
   TABLE DATA           r   COPY cuenta.mult_roles_cuentas (id_rol, fecha_creacion, usuari_creacion, cuenta, cuenta_interna, rol) FROM stdin;
    cuenta          postgres    false    247   ��                0    68156 
   mult_token 
   TABLE DATA           P   COPY cuenta.mult_token (id_token, fecha_creacion, token, id_cuenta) FROM stdin;
    cuenta          postgres    false    250   M�      �          0    86003    ptn_cuentas 
   TABLE DATA           5  COPY cuenta.ptn_cuentas (id_cuenta, acepta_politica_privacidad, acepta_recibir_informacion, acepta_termino_uso, clave, cuenta_activa, email, estado, fecha_creacion, fecha_modificacion, inicios_erroneos, tipo_contacto, usuario, usuario_contacto, usuario_creacion, usuario_modificacion, id_persona) FROM stdin;
    cuenta          postgres    false    387   i�      �          0    86010    ptn_menu 
   TABLE DATA           y   COPY cuenta.ptn_menu (id_menu, descripcion, estado, fecha_creacion, nombre, orden, url, url_icono, id_padre) FROM stdin;
    cuenta          postgres    false    389   ��      �          0    86018    ptn_menu_operacion 
   TABLE DATA           K   COPY cuenta.ptn_menu_operacion (id_menu, id_operacion, id_rol) FROM stdin;
    cuenta          postgres    false    390   ��      �          0    86025    ptn_operaciones 
   TABLE DATA           d   COPY cuenta.ptn_operaciones (id_operacion, descripcion, estado, fecha_creacion, nombre) FROM stdin;
    cuenta          postgres    false    392   ��      �          0    86032    ptn_pers_cuentas 
   TABLE DATA           z   COPY cuenta.ptn_pers_cuentas (id_pers_cuenta, estado, numero_cuenta, tipo_cuenta, titular, id_banco, persona) FROM stdin;
    cuenta          postgres    false    394   ��      �          0    86039    ptn_pers_documentos 
   TABLE DATA           �   COPY cuenta.ptn_pers_documentos (id_documento, estado, fecha_creacion, fecha_modificacion, nombre, nombre_post, ruta, ruta_post, usuario_creacion, usuario_modificacion, id_tipo_documento, id_info_adicional) FROM stdin;
    cuenta          postgres    false    396   ��      �          0    86049    ptn_pers_domicilios 
   TABLE DATA           |   COPY cuenta.ptn_pers_domicilios (id_domicilio, direccion, estado, numero_domicilio, sector, id_ciudad, id_pais) FROM stdin;
    cuenta          postgres    false    398   �      �          0    86056    ptn_pers_est_finan 
   TABLE DATA           �   COPY cuenta.ptn_pers_est_finan (id_est_finan, egreso_anual, estado, ingreso_anual, total_activo, total_pasivo, total_patrimonio) FROM stdin;
    cuenta          postgres    false    400   4�      �          0    86063    ptn_pers_firmas 
   TABLE DATA           e   COPY cuenta.ptn_pers_firmas (id_firma, email, estado, identificacion, nombres_completos) FROM stdin;
    cuenta          postgres    false    402   Q�      �          0    86070    ptn_pers_info_adicional 
   TABLE DATA           �  COPY cuenta.ptn_pers_info_adicional (id_info_adicional, actividad_economica, cargo_persona, estado, estado_civil, fecha_creacion, fecha_modificacion, fecha_registro, fuente_ingresos, numero_telefono, pais_domicilio_fiscal, residente_domicilio_fiscal, sexo, id_domicilio, id_est_finan_jur, id_firma_jur, id_repre_legal_jur, id_tipo_cuenta, id_persona, id_doc_identificacion, usuario_creacion, usuario_modificacion) FROM stdin;
    cuenta          postgres    false    404   n�      �          0    86077    ptn_pers_repre_legal 
   TABLE DATA           �   COPY cuenta.ptn_pers_repre_legal (id_repre_legal, apellidos, cargo, direccion_domicilio, email, estado, fecha_inicio_actividad, identificacion, nombres, numero_celular, numero_domicilio, telefono, id_pais) FROM stdin;
    cuenta          postgres    false    406   ��      �          0    86082    ptn_personas 
   TABLE DATA           ]  COPY cuenta.ptn_personas (identificacion, anio_inicio_actividad, apellidos, cargo_contacto, email_contacto, estado, fecha_creacion, fecha_modificacion, fecha_nacimiento, genero, nacionalidad, nombre_contacto, nombres, numero_celular, razon_social, tipo_cliente, tipo_identificacion, tipo_persona, usuario_creacion, usuario_modificacion) FROM stdin;
    cuenta          postgres    false    407   ��      �          0    86092 	   ptn_roles 
   TABLE DATA           A   COPY cuenta.ptn_roles (id_rol, estado, nombre, ruta) FROM stdin;
    cuenta          postgres    false    409   ��      �          0    86097    ptn_roles_cuentas 
   TABLE DATA           Y   COPY cuenta.ptn_roles_cuentas (cuenta, rol, fecha_creacion, usuari_creacion) FROM stdin;
    cuenta          postgres    false    410   ��      �          0    86104 	   ptn_token 
   TABLE DATA           O   COPY cuenta.ptn_token (id_token, fecha_creacion, token, id_cuenta) FROM stdin;
    cuenta          postgres    false    412   ��                0    68161    mult_info_emails 
   TABLE DATA           L   COPY emails.mult_info_emails (id_email, email, enviado, estado) FROM stdin;
    emails          postgres    false    252                    0    68166    mult_plantillas_emails 
   TABLE DATA           ^   COPY emails.mult_plantillas_emails (id_plantilla, asunto, cuerpo, estado, nombre) FROM stdin;
    emails          postgres    false    254   �      �          0    86127    ptn_info_emails 
   TABLE DATA           K   COPY emails.ptn_info_emails (id_email, email, enviado, estado) FROM stdin;
    emails          postgres    false    414   2      �          0    86132    ptn_plantillas_emails 
   TABLE DATA           ]   COPY emails.ptn_plantillas_emails (id_plantilla, asunto, cuerpo, estado, nombre) FROM stdin;
    emails          postgres    false    415   O                0    68172    mult_form_contact 
   TABLE DATA           �   COPY footer.mult_form_contact (id, ciudad, email, identificacion, mensaje, motivo, nombres, telefono, estado_actual, estado_anterior) FROM stdin;
    footer          postgres    false    255   l      �          0    86146    ptn_form_contact 
   TABLE DATA           �   COPY footer.ptn_form_contact (id, ciudad, email, identificacion, mensaje, motivo, nombres, telefono, estado_actual, estado_anterior) FROM stdin;
    footer          postgres    false    417   4                0    68177    mult_bitacora_procesos 
   TABLE DATA           j   COPY historicas.mult_bitacora_procesos (id, descripcion, fecha, identificador, proceso, tipo) FROM stdin;
 
   historicas          postgres    false    257   Q      !          0    68182    mult_hist_proyecto 
   TABLE DATA           �   COPY historicas.mult_hist_proyecto (id, comprobante_ruta, fecha_historial, observacion, tabla_cambiar, usuario_creacion, valor_actual, valor_anterior, id_proyecto) FROM stdin;
 
   historicas          postgres    false    259   n      #          0    68190    mult_hist_solicitud 
   TABLE DATA           �   COPY historicas.mult_hist_solicitud (id, comprobante_ruta, fecha_historial, observacion, tabla_cambiar, valor_actual, valor_anterior, solicitud, usuario_modificacion, usuario_modificacion_interno) FROM stdin;
 
   historicas          postgres    false    261          %          0    68198    mult_historial_conciliacion 
   TABLE DATA           V   COPY historicas.mult_historial_conciliacion (id, fecha, usuario, id_file) FROM stdin;
 
   historicas          postgres    false    263   L)      &          0    68201 #   mult_historial_conciliacion_detalle 
   TABLE DATA           �   COPY historicas.mult_historial_conciliacion_detalle (id, comprobante, fecha_file, fecha_transaccion, monto_file, monto_transaccion, observacion, id_historial) FROM stdin;
 
   historicas          postgres    false    264   �*      )          0    68208    mult_sol_x_identifiacion 
   TABLE DATA           r   COPY historicas.mult_sol_x_identifiacion (id, documento, fecha_creacion, solicitud, usuario_creacion) FROM stdin;
 
   historicas          postgres    false    267   8,      �          0    86153    ptn_bitacora_procesos 
   TABLE DATA           i   COPY historicas.ptn_bitacora_procesos (id, descripcion, fecha, identificador, proceso, tipo) FROM stdin;
 
   historicas          postgres    false    419   U,      �          0    86160    ptn_hist_proyecto 
   TABLE DATA           �   COPY historicas.ptn_hist_proyecto (id, comprobante_ruta, fecha_historial, observacion, tabla_cambiar, usuario_creacion, valor_actual, valor_anterior, id_proyecto) FROM stdin;
 
   historicas          postgres    false    421   r,      �          0    86170    ptn_hist_solicitud 
   TABLE DATA           �   COPY historicas.ptn_hist_solicitud (id, comprobante_ruta, fecha_historial, observacion, tabla_cambiar, valor_actual, valor_anterior, solicitud, usuario_modificacion, usuario_modificacion_interno) FROM stdin;
 
   historicas          postgres    false    423   �,      �          0    86180    ptn_historial_conciliacion 
   TABLE DATA           U   COPY historicas.ptn_historial_conciliacion (id, fecha, usuario, id_file) FROM stdin;
 
   historicas          postgres    false    425   �,      �          0    86187 "   ptn_historial_conciliacion_detalle 
   TABLE DATA           �   COPY historicas.ptn_historial_conciliacion_detalle (id, comprobante, fecha_file, fecha_transaccion, monto_file, monto_transaccion, observacion, id_historial) FROM stdin;
 
   historicas          postgres    false    427   �,      �          0    86194    ptn_sol_x_identifiacion 
   TABLE DATA           q   COPY historicas.ptn_sol_x_identifiacion (id, documento, fecha_creacion, solicitud, usuario_creacion) FROM stdin;
 
   historicas          postgres    false    429   �,      +          0    68216    mult_datos_inversion 
   TABLE DATA           z   COPY inversion.mult_datos_inversion (id_dato, documentacion, formulario, pago, tabla_amortizacion, solicitud) FROM stdin;
 	   inversion          postgres    false    269   -      -          0    68221    mult_detalle_amortizacion 
   TABLE DATA             COPY inversion.mult_detalle_amortizacion (id_det_amortizacion, cobros_capital, cuota, detalle_cobro, estado, estado_pago, fecha_estimacion, fecha_realizada, rendimiento_mensual, ruta_pago, saldo_capital, total_recibir, id_tbl_amortizacion, fecha_registro) FROM stdin;
 	   inversion          postgres    false    271   �-      /          0    68226    mult_doc_aceptados 
   TABLE DATA           �   COPY inversion.mult_doc_aceptados (id_doc_aceptado, estado, fecha_creacion, fecha_modificacion, habilitado, nombre, numero_solicitud, ruta, usuario_creacion, usuario_modificacion, version, id_tipo_documento, identificacion) FROM stdin;
 	   inversion          postgres    false    273   4G      1          0    68231    mult_solicitudes 
   TABLE DATA           I  COPY inversion.mult_solicitudes (numero_solicitud, acepta_informacion_correcta, acepta_ingresar_info_vigente, acepta_licitud_fondos, fecha_generacion, observacion, tabla_amortizacion, documentos, estado_actual, ultimo_historial, id_inversionista, pagare, codigo_proyecto, id_tipo_solicitud, usuario_creacion, activo) FROM stdin;
 	   inversion          postgres    false    275   �J      2          0    68234    mult_solicitudes_documentos 
   TABLE DATA             COPY inversion.mult_solicitudes_documentos (id_sol_documentos, acuerdo_uso, contrato_prenda, datos_inversionista, estado, fecha_creacion, fecha_modificacion, modelo_contrato, observacion, pagare, tabla_amortizacion, usuario_creacion, usuario_modificacion, solicitud) FROM stdin;
 	   inversion          postgres    false    276   N      6          0    68246    mult_tabla_amortizacion 
   TABLE DATA           �   COPY inversion.mult_tabla_amortizacion (id_tbl_amortizacion, estado, fecha_creacion, fecha_efectiva, monto_inversion, plazo, rendimiento_neto, rendimiento_total_inversion, total_recibir, id_tipo_tabla, usuario_creacion) FROM stdin;
 	   inversion          postgres    false    280   �R      8          0    68251    mult_tipo_estados 
   TABLE DATA           W   COPY inversion.mult_tipo_estados (id_estado, descripcion, detalle, estado) FROM stdin;
 	   inversion          postgres    false    282   WW      9          0    68254    mult_transacciones 
   TABLE DATA             COPY inversion.mult_transacciones (id_doc_transaccion, conciliado, depositante, estado, fecha_creacion, fecha_transaccion, monto, nombre_documento, numero_comprobante, ruta_comprobante, id_tipo_documento, id_forma_pago, numero_solicitud, usuario_creacion, proyecto) FROM stdin;
 	   inversion          postgres    false    283   .Z      �          0    86204    ptn_datos_inversion 
   TABLE DATA           y   COPY inversion.ptn_datos_inversion (id_dato, documentacion, formulario, pago, tabla_amortizacion, solicitud) FROM stdin;
 	   inversion          postgres    false    431   N^      �          0    86211    ptn_detalle_amortizacion 
   TABLE DATA             COPY inversion.ptn_detalle_amortizacion (id_det_amortizacion, cobros_capital, cuota, detalle_cobro, estado, estado_pago, fecha_estimacion, fecha_realizada, fecha_registro, rendimiento_mensual, ruta_pago, saldo_capital, total_recibir, id_tbl_amortizacion) FROM stdin;
 	   inversion          postgres    false    433   k^      �          0    86218    ptn_doc_aceptados 
   TABLE DATA           �   COPY inversion.ptn_doc_aceptados (id_doc_aceptado, estado, fecha_creacion, fecha_modificacion, habilitado, nombre, numero_solicitud, ruta, usuario_creacion, usuario_modificacion, version, id_tipo_documento, identificacion) FROM stdin;
 	   inversion          postgres    false    435   �^      �          0    86225    ptn_solicitudes 
   TABLE DATA           H  COPY inversion.ptn_solicitudes (numero_solicitud, acepta_informacion_correcta, acepta_ingresar_info_vigente, acepta_licitud_fondos, activo, fecha_generacion, observacion, tabla_amortizacion, documentos, estado_actual, ultimo_historial, id_inversionista, pagare, codigo_proyecto, id_tipo_solicitud, usuario_creacion) FROM stdin;
 	   inversion          postgres    false    437   �^      �          0    86232    ptn_solicitudes_documentos 
   TABLE DATA             COPY inversion.ptn_solicitudes_documentos (id_sol_documentos, acuerdo_uso, contrato_prenda, datos_inversionista, estado, fecha_creacion, fecha_modificacion, modelo_contrato, observacion, pagare, tabla_amortizacion, usuario_creacion, usuario_modificacion, solicitud) FROM stdin;
 	   inversion          postgres    false    439   �^      �          0    86242    ptn_tabla_amortizacion 
   TABLE DATA           �   COPY inversion.ptn_tabla_amortizacion (id_tbl_amortizacion, estado, fecha_creacion, fecha_efectiva, monto_inversion, plazo, rendimiento_neto, rendimiento_total_inversion, total_recibir, id_tipo_tabla, usuario_creacion) FROM stdin;
 	   inversion          postgres    false    441   �^      �          0    86247    ptn_tipo_estados 
   TABLE DATA           V   COPY inversion.ptn_tipo_estados (id_estado, descripcion, detalle, estado) FROM stdin;
 	   inversion          postgres    false    442   �^      �          0    86254    ptn_transacciones 
   TABLE DATA             COPY inversion.ptn_transacciones (id_doc_transaccion, conciliado, depositante, estado, fecha_creacion, fecha_transaccion, monto, nombre_documento, numero_comprobante, ruta_comprobante, id_tipo_documento, id_forma_pago, proyecto, numero_solicitud, usuario_creacion) FROM stdin;
 	   inversion          postgres    false    444   _      ;          0    68262    mult_bancos 
   TABLE DATA           A   COPY maestras.mult_bancos (id_banco, estado, nombre) FROM stdin;
    maestras          postgres    false    285   6_      =          0    68267    mult_ciudades 
   TABLE DATA           M   COPY maestras.mult_ciudades (id_ciudad, ciudad, estado, id_pais) FROM stdin;
    maestras          postgres    false    287   �`      ?          0    68272    mult_forma_pago 
   TABLE DATA           O   COPY maestras.mult_forma_pago (id_forma_pago, descripcion, estado) FROM stdin;
    maestras          postgres    false    289   �g      A          0    68277    mult_nacionalidades 
   TABLE DATA           _   COPY maestras.mult_nacionalidades (id_nacionalidad, estado, gentilicio, iso, pais) FROM stdin;
    maestras          postgres    false    291    h      C          0    68282    mult_rango_pago 
   TABLE DATA           Z   COPY maestras.mult_rango_pago (id, activo, max, min, usuario_creacion, valor) FROM stdin;
    maestras          postgres    false    293   .q      E          0    68287    mult_tipo_documentos 
   TABLE DATA           V   COPY maestras.mult_tipo_documentos (id_tipo_documento, documento, estado) FROM stdin;
    maestras          postgres    false    295   q      G          0    68292    mult_tipo_solicitud 
   TABLE DATA           H   COPY maestras.mult_tipo_solicitud (id, descripcion, estado) FROM stdin;
    maestras          postgres    false    297   �q      I          0    68297    mult_tipo_tablas 
   TABLE DATA           X   COPY maestras.mult_tipo_tablas (id_tipo_tabla, descripcion, estado, nombre) FROM stdin;
    maestras          postgres    false    299   r      K          0    68302    mult_tipo_transacciones 
   TABLE DATA           ]   COPY maestras.mult_tipo_transacciones (id_tipo_transaccion, descripcion, estado) FROM stdin;
    maestras          postgres    false    301   �r      �          0    86266 
   ptn_bancos 
   TABLE DATA           @   COPY maestras.ptn_bancos (id_banco, estado, nombre) FROM stdin;
    maestras          postgres    false    446   �r      �          0    86273    ptn_ciudades 
   TABLE DATA           L   COPY maestras.ptn_ciudades (id_ciudad, ciudad, estado, id_pais) FROM stdin;
    maestras          postgres    false    448   s      �          0    86280    ptn_forma_pago 
   TABLE DATA           N   COPY maestras.ptn_forma_pago (id_forma_pago, descripcion, estado) FROM stdin;
    maestras          postgres    false    450   6s      �          0    86287    ptn_nacionalidades 
   TABLE DATA           ^   COPY maestras.ptn_nacionalidades (id_nacionalidad, estado, gentilicio, iso, pais) FROM stdin;
    maestras          postgres    false    452   Ss      �          0    86294    ptn_rango_pago 
   TABLE DATA           Y   COPY maestras.ptn_rango_pago (id, activo, max, min, usuario_creacion, valor) FROM stdin;
    maestras          postgres    false    454   ps      �          0    86301    ptn_tipo_documentos 
   TABLE DATA           U   COPY maestras.ptn_tipo_documentos (id_tipo_documento, documento, estado) FROM stdin;
    maestras          postgres    false    456   �s      �          0    86308    ptn_tipo_solicitud 
   TABLE DATA           G   COPY maestras.ptn_tipo_solicitud (id, descripcion, estado) FROM stdin;
    maestras          postgres    false    458   �s      �          0    86315    ptn_tipo_tablas 
   TABLE DATA           W   COPY maestras.ptn_tipo_tablas (id_tipo_tabla, descripcion, estado, nombre) FROM stdin;
    maestras          postgres    false    460   �s      M          0    68307    mult_deport_categorias 
   TABLE DATA           ]   COPY maestras_auspicios.mult_deport_categorias (id, descripcion, nombre, activo) FROM stdin;
    maestras_auspicios          postgres    false    303   �s      O          0    68312    mult_deport_disciplina 
   TABLE DATA           ]   COPY maestras_auspicios.mult_deport_disciplina (id, descripcion, nombre, activo) FROM stdin;
    maestras_auspicios          postgres    false    305   �v      Q          0    68317    mult_deport_modalidad 
   TABLE DATA           \   COPY maestras_auspicios.mult_deport_modalidad (id, descripcion, nombre, activo) FROM stdin;
    maestras_auspicios          postgres    false    307   �w      �          0    86329    ptn_deport_categorias 
   TABLE DATA           \   COPY maestras_auspicios.ptn_deport_categorias (id, activo, descripcion, nombre) FROM stdin;
    maestras_auspicios          postgres    false    462   Mx      �          0    86336    ptn_deport_disciplina 
   TABLE DATA           \   COPY maestras_auspicios.ptn_deport_disciplina (id, activo, descripcion, nombre) FROM stdin;
    maestras_auspicios          postgres    false    464   jx      �          0    86343    ptn_deport_modalidad 
   TABLE DATA           [   COPY maestras_auspicios.ptn_deport_modalidad (id, activo, descripcion, nombre) FROM stdin;
    maestras_auspicios          postgres    false    466   �x      S          0    68322    mult_cuentas_internas 
   TABLE DATA           �   COPY multiplo.mult_cuentas_internas (id_cuenta, clave, cuenta_activa, email, estado, fecha_creacion, fecha_modificacion, inicios_erroneos, usuario_creacion, usuario_modificacion, id_persona) FROM stdin;
    multiplo          postgres    false    309   �x      T          0    68328    mult_menu_int 
   TABLE DATA           �   COPY multiplo.mult_menu_int (id_menu, descripcion, estado, fecha_creacion, nombre, orden, url, url_icono, id_padre) FROM stdin;
    multiplo          postgres    false    310   lz      V          0    68336    mult_menu_operacion_int 
   TABLE DATA           V   COPY multiplo.mult_menu_operacion_int (id_menu, id_operacion, id_rol_int) FROM stdin;
    multiplo          postgres    false    312   3{      W          0    68339    mult_operaciones_int 
   TABLE DATA           k   COPY multiplo.mult_operaciones_int (id_operacion, descripcion, estado, fecha_creacion, nombre) FROM stdin;
    multiplo          postgres    false    313   �{      Y          0    68344    mult_personal_interno 
   TABLE DATA           �   COPY multiplo.mult_personal_interno (id_pers_interno, apellidos, estado, fecha_creacion, fecha_modificacion, id_jefe, iniciales, nombres, usuario_creacion, usuario_modificacion, cuenta_interna) FROM stdin;
    multiplo          postgres    false    315   |      Z          0    68350    mult_roles_cuentas_internas 
   TABLE DATA           m   COPY multiplo.mult_roles_cuentas_internas (cuenta_interna, rol, fecha_creacion, usuari_creacion) FROM stdin;
    multiplo          postgres    false    316   �}      [          0    68353    mult_roles_int 
   TABLE DATA           H   COPY multiplo.mult_roles_int (id_rol, estado, nombre, ruta) FROM stdin;
    multiplo          postgres    false    317   n~      ]          0    68358    mult_documentos 
   TABLE DATA           �   COPY multiplo_documentos.mult_documentos (id_documento, estado, fecha_creacion, fecha_modificacion, nombre, ruta, usuario_creacion, usuario_modificacion, version, id_tipo_documento) FROM stdin;
    multiplo_documentos          postgres    false    319   "      ^          0    68361    mult_documentos_facturas 
   TABLE DATA           �   COPY multiplo_documentos.mult_documentos_facturas (id, codigo_factura, estado, fecha_emision, id_cliente, numero_documento, numero_establecimiento, numero_facturero, total_factura) FROM stdin;
    multiplo_documentos          postgres    false    320   �      a          0    68371    mult_plantillas_documentos 
   TABLE DATA           j   COPY multiplo_documentos.mult_plantillas_documentos (id_plantilla, estado, nombre, plantilla) FROM stdin;
    multiplo_documentos          postgres    false    323   y�      b          0    68377    mult_conciliacion_aprobada 
   TABLE DATA           c   COPY negocio.mult_conciliacion_aprobada (id, estado, fecha, monto_conciliado, usuario) FROM stdin;
    negocio          postgres    false    324   ��      c          0    68380 "   mult_conciliacion_aprobada_detalle 
   TABLE DATA           �   COPY negocio.mult_conciliacion_aprobada_detalle (id, concepto, fecha_efectivo, lugar, monto, nombre_banco, numero_comprobante, numero_cuenta, observacion, tipo_transaccion, id_conciliacion) FROM stdin;
    negocio          postgres    false    325   ~�      f          0    68387    mult_conciliacion_detalle_xls 
   TABLE DATA           �   COPY negocio.mult_conciliacion_detalle_xls (id, concepto, fecha_efectivo, lugar, monto, nombre_banco, numero_comprobante, numero_cuenta, observacion, tipo_transaccion, id_conciliacion) FROM stdin;
    negocio          postgres    false    328   "�      h          0    68392    mult_conciliacion_xls 
   TABLE DATA           k   COPY negocio.mult_conciliacion_xls (id, estado, fecha, monto_conciliado, monto_total, usuario) FROM stdin;
    negocio          postgres    false    330   ��      j          0    68397    mult_fecha_gen_tbl_amort 
   TABLE DATA           x   COPY negocio.mult_fecha_gen_tbl_amort (id, fecha_creacion, fecha_generacion, usuario_creacion, id_proyecto) FROM stdin;
    negocio          postgres    false    332   i�      l          0    68402    mult_porc_interes_tbl_proyecto 
   TABLE DATA           �   COPY negocio.mult_porc_interes_tbl_proyecto (id, estado, fecha_creacion, fecha_modificacion, porcentaje_interes, usuario_creacion, usuario_modificacion, codigo_proyecto, id_tipo_tabla) FROM stdin;
    negocio          postgres    false    334   	�      �          0    86350    ptn_conciliacion_aprobada 
   TABLE DATA           b   COPY negocio.ptn_conciliacion_aprobada (id, estado, fecha, monto_conciliado, usuario) FROM stdin;
    negocio          postgres    false    468   ��      �          0    86357 !   ptn_conciliacion_aprobada_detalle 
   TABLE DATA           �   COPY negocio.ptn_conciliacion_aprobada_detalle (id, concepto, fecha_efectivo, lugar, monto, nombre_banco, numero_comprobante, numero_cuenta, observacion, tipo_transaccion, id_conciliacion) FROM stdin;
    negocio          postgres    false    470   ۈ      �          0    86364    ptn_conciliacion_detalle_xls 
   TABLE DATA           �   COPY negocio.ptn_conciliacion_detalle_xls (id, concepto, fecha_efectivo, lugar, monto, nombre_banco, numero_comprobante, numero_cuenta, observacion, tipo_transaccion, id_conciliacion) FROM stdin;
    negocio          postgres    false    472   ��      �          0    86371    ptn_conciliacion_xls 
   TABLE DATA           j   COPY negocio.ptn_conciliacion_xls (id, estado, fecha, monto_conciliado, monto_total, usuario) FROM stdin;
    negocio          postgres    false    474   �      �          0    86378    ptn_fecha_gen_tbl_amort 
   TABLE DATA           w   COPY negocio.ptn_fecha_gen_tbl_amort (id, fecha_creacion, fecha_generacion, usuario_creacion, id_proyecto) FROM stdin;
    negocio          postgres    false    476   2�      �          0    86385    ptn_porc_interes_tbl_proyecto 
   TABLE DATA           �   COPY negocio.ptn_porc_interes_tbl_proyecto (id, estado, fecha_creacion, fecha_modificacion, porcentaje_interes, usuario_creacion, usuario_modificacion, codigo_proyecto, id_tipo_tabla) FROM stdin;
    negocio          postgres    false    478   O�      n          0    68407    mult_parametros 
   TABLE DATA           �   COPY parametrizacion.mult_parametros (id_parametro, cod_parametro, descripcion, estado, fecha_creacion, fecha_modificacion, parametro, usuario_creacion, usuario_modificacion, valor) FROM stdin;
    parametrizacion          postgres    false    336   l�      �          0    86394    ptn_parametros 
   TABLE DATA           �   COPY parametrizacion.ptn_parametros (id_parametro, cod_parametro, descripcion, estado, fecha_creacion, fecha_modificacion, parametro, usuario_creacion, usuario_modificacion, valor) FROM stdin;
    parametrizacion          postgres    false    480   �      p          0    68415    mult_detalle_porc_sol_aprobadas 
   TABLE DATA              COPY promotor.mult_detalle_porc_sol_aprobadas (id, estado, fecha_creacion, numero_solicitud, id_porc_sol_aprobada) FROM stdin;
    promotor          postgres    false    338   ;�      r          0    68420    mult_documentos_financieros 
   TABLE DATA           �   COPY promotor.mult_documentos_financieros (id, activo, anexo_cts_cobrar, estado_financiero_actual, estado_financiero_aa, fecha_creacion, impuesto_renta_aa, usuario_creacion, empresa) FROM stdin;
    promotor          postgres    false    340   �      t          0    68428    mult_documentos_juridicos 
   TABLE DATA           �   COPY promotor.mult_documentos_juridicos (id, activo, cedula_rl, escritura, estatutos_vigentes, fecha_creacion, identificaciones_accionista, nombramiento_rl, nomina_accionista, ruc_vigente, usuario_creacion, empresa) FROM stdin;
    promotor          postgres    false    342   @�      v          0    68436    mult_empresa_datos_anuales 
   TABLE DATA           �   COPY promotor.mult_empresa_datos_anuales (id, activo, anio, fecha_creacion, margen_contribucion, usuario_creacion, venta_totales, id_empresa) FROM stdin;
    promotor          postgres    false    344   ��      x          0    68441    mult_empresas 
   TABLE DATA             COPY promotor.mult_empresas (id_empresa, antecedente, estado, fecha_creacion, fecha_modificacion, nombre, usuario_creacion, usuario_modificacion, ventaja_competitiva, actividad, pais, ruc, ciudad, descripcion_producto, direccion, cuenta, dato_anual_actual) FROM stdin;
    promotor          postgres    false    346   �      z          0    68449    mult_indicadores 
   TABLE DATA           �   COPY promotor.mult_indicadores (id_indicador, anio, estado, garantia, liquidez, porcentaje_garantia, porcentaje_liquidez, porcentaje_retorno_capital, porcentaje_solvencia, retorno_capital, solvencia) FROM stdin;
    promotor          postgres    false    348   `�      |          0    68457    mult_porc_sol_aprobadas 
   TABLE DATA           �   COPY promotor.mult_porc_sol_aprobadas (id, estado, fecha_aprobacion, fecha_creacion, monto_aprobado, monto_solicitado, observacion, porcentaje_aprobado, usuario_aprobacion, codigo_proyecto) FROM stdin;
    promotor          postgres    false    350   -�      ~          0    68462    mult_proyectos 
   TABLE DATA             COPY promotor.mult_proyectos (id_proyecto, destino_financiamiento, fecha_creacion, fecha_inicio_inversion, fecha_limite_inversion, fecha_modificacion, monto_solicitado, pago_capital, pago_interes, plazo, tasa_efectiva_anual, tipo_inversion, usuario_creacion, usuario_modificacion, calificacion_interna, id_empresa, estado_actual, estado_anterior, id_indicador, monto_recaudado, tabla_amortizacion, ultimo_historial, ronda, periodo_pago, acepta_informacion_correcta, acepta_ingresar_info_vigente, acepta_licitud_fondos) FROM stdin;
    promotor          postgres    false    352   a�                0    68465    mult_proyectos_cuentas 
   TABLE DATA           �   COPY promotor.mult_proyectos_cuentas (id_proyecto_cuenta, estado, fecha_creacion, fecha_modificacion, numero_cuenta, tipo_cuenta, usuario_creacion, usuario_modificacion, id_banco, id_proyecto) FROM stdin;
    promotor          postgres    false    353   9�      �          0    68470    mult_proyectos_rutas 
   TABLE DATA           e   COPY promotor.mult_proyectos_rutas (id_proyecto_ruta, estado, nombre, ruta, id_proyecto) FROM stdin;
    promotor          postgres    false    355   �      �          0    68475    mult_rango_pagos 
   TABLE DATA           [   COPY promotor.mult_rango_pagos (id, activo, max, min, usuario_creacion, valor) FROM stdin;
    promotor          postgres    false    357   ��      �          0    68480    mult_tipo_actividades 
   TABLE DATA           O   COPY promotor.mult_tipo_actividades (id_actividad, estado, nombre) FROM stdin;
    promotor          postgres    false    359   ��      �          0    68485    mult_tipo_calificaciones 
   TABLE DATA           Z   COPY promotor.mult_tipo_calificaciones (id_tipo_calificacion, estado, nombre) FROM stdin;
    promotor          postgres    false    361   N�                 0    86401    ptn_detalle_porc_sol_aprobadas 
   TABLE DATA           ~   COPY promotor.ptn_detalle_porc_sol_aprobadas (id, estado, fecha_creacion, numero_solicitud, id_porc_sol_aprobada) FROM stdin;
    promotor          postgres    false    482   w�                0    86408    ptn_documentos_financieros 
   TABLE DATA           �   COPY promotor.ptn_documentos_financieros (id, activo, anexo_cts_cobrar, estado_financiero_actual, estado_financiero_aa, fecha_creacion, impuesto_renta_aa, usuario_creacion, empresa) FROM stdin;
    promotor          postgres    false    484   ��                0    86418    ptn_documentos_juridicos 
   TABLE DATA           �   COPY promotor.ptn_documentos_juridicos (id, activo, cedula_rl, escritura, estatutos_vigentes, fecha_creacion, identificaciones_accionista, nombramiento_rl, nomina_accionista, ruc_vigente, usuario_creacion, empresa) FROM stdin;
    promotor          postgres    false    486   ��                0    86428    ptn_empresa_datos_anuales 
   TABLE DATA           �   COPY promotor.ptn_empresa_datos_anuales (id, activo, anio, fecha_creacion, margen_contribucion, usuario_creacion, venta_totales, id_empresa) FROM stdin;
    promotor          postgres    false    488   Χ                0    86435    ptn_empresas 
   TABLE DATA             COPY promotor.ptn_empresas (id_empresa, antecedente, ciudad, descripcion_producto, direccion, estado, fecha_creacion, fecha_modificacion, nombre, ruc, usuario_creacion, usuario_modificacion, ventaja_competitiva, actividad, cuenta, dato_anual_actual, pais) FROM stdin;
    promotor          postgres    false    490   �      
          0    86445    ptn_indicadores 
   TABLE DATA           �   COPY promotor.ptn_indicadores (id_indicador, anio, estado, garantia, liquidez, porcentaje_garantia, porcentaje_liquidez, porcentaje_retorno_capital, porcentaje_solvencia, retorno_capital, solvencia) FROM stdin;
    promotor          postgres    false    492   �                0    86455    ptn_porc_sol_aprobadas 
   TABLE DATA           �   COPY promotor.ptn_porc_sol_aprobadas (id, estado, fecha_aprobacion, fecha_creacion, monto_aprobado, monto_solicitado, observacion, porcentaje_aprobado, usuario_aprobacion, codigo_proyecto) FROM stdin;
    promotor          postgres    false    494   %�                0    86460    ptn_proyectos 
   TABLE DATA             COPY promotor.ptn_proyectos (id_proyecto, acepta_informacion_correcta, acepta_ingresar_info_vigente, acepta_licitud_fondos, destino_financiamiento, fecha_creacion, fecha_inicio_inversion, fecha_limite_inversion, fecha_modificacion, monto_recaudado, monto_solicitado, pago_capital, pago_interes, periodo_pago, plazo, ronda, tasa_efectiva_anual, tipo_inversion, usuario_creacion, usuario_modificacion, tabla_amortizacion, calificacion_interna, id_empresa, estado_actual, estado_anterior, ultimo_historial, id_indicador) FROM stdin;
    promotor          postgres    false    495   B�                0    86467    ptn_proyectos_cuentas 
   TABLE DATA           �   COPY promotor.ptn_proyectos_cuentas (id_proyecto_cuenta, estado, fecha_creacion, fecha_modificacion, numero_cuenta, tipo_cuenta, usuario_creacion, usuario_modificacion, id_banco, id_proyecto) FROM stdin;
    promotor          postgres    false    497   _�                0    86474    ptn_proyectos_rutas 
   TABLE DATA           d   COPY promotor.ptn_proyectos_rutas (id_proyecto_ruta, estado, nombre, ruta, id_proyecto) FROM stdin;
    promotor          postgres    false    499   |�                0    86481    ptn_tipo_actividades 
   TABLE DATA           N   COPY promotor.ptn_tipo_actividades (id_actividad, estado, nombre) FROM stdin;
    promotor          postgres    false    501   ��                0    86488    ptn_tipo_calificaciones 
   TABLE DATA           Y   COPY promotor.ptn_tipo_calificaciones (id_tipo_calificacion, estado, nombre) FROM stdin;
    promotor          postgres    false    503   ��      �          0    68492    mult_cuentas 
   TABLE DATA           &  COPY public.mult_cuentas (id_cuenta, id_rol, usuario, identificacion, email, clave, tipo_contacto, usuario_contacto, acepta_politica_privacidad, acepta_termino_uso, cuenta_activa, inicios_erroneos, fecha_creacion, usuario_creacion, fecha_modificacion, usuario_modificacion, estado) FROM stdin;
    public          postgres    false    364   Ө      �          0    68497    mult_empleados 
   TABLE DATA           �   COPY public.mult_empleados (id_empleado, usuario, nombres_completos, iniciales, email, id_jefe, fecha_creacion, usuario_creacion, fecha_modificacion, usuario_modificacion, estado) FROM stdin;
    public          postgres    false    366   `�      �          0    68502    mult_personas 
   TABLE DATA           �  COPY public.mult_personas (id_persona, identificacion, id_cuenta, tipo_cliente, tipo_persona, tipo_identificacion, nacionalidad, nombres, apellidos, fecha_nacimiento, email, numero_celular, razon_social, nombre_contacto, cargo_contacto, email_contacto, anio_inicio_actividad, fecha_vigencia, fecha_creacion, usuario_creacion, fecha_modificacion, usuario_modificacion, estado) FROM stdin;
    public          postgres    false    368   
�      �          0    68510 
   mult_roles 
   TABLE DATA           4   COPY public.mult_roles (id_rol, nombre) FROM stdin;
    public          postgres    false    370   z�      �          0    68515 
   mult_token 
   TABLE DATA           P   COPY public.mult_token (id_token, token, fecha_creacion, id_cuenta) FROM stdin;
    public          postgres    false    372   ҷ      �          0    68520    mult_usuarios 
   TABLE DATA           B   COPY public.mult_usuarios (id_usuario, clave, id_rol) FROM stdin;
    public          postgres    false    374   ��      !           0    0    mult_auspicios_id_seq    SEQUENCE SET     G   SELECT pg_catalog.setval('auspicios.mult_auspicios_id_seq', 82, true);
       	   auspicios          postgres    false    215            "           0    0     mult_auspicios_recompesas_id_seq    SEQUENCE SET     S   SELECT pg_catalog.setval('auspicios.mult_auspicios_recompesas_id_seq', 720, true);
       	   auspicios          postgres    false    217            #           0    0    mult_auspicios_torneos_id_seq    SEQUENCE SET     P   SELECT pg_catalog.setval('auspicios.mult_auspicios_torneos_id_seq', 629, true);
       	   auspicios          postgres    false    219            $           0    0     mult_auspicios_valoracion_id_seq    SEQUENCE SET     R   SELECT pg_catalog.setval('auspicios.mult_auspicios_valoracion_id_seq', 49, true);
       	   auspicios          postgres    false    221            %           0    0    mult_titulos_deportivos_id_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('auspicios.mult_titulos_deportivos_id_seq', 383, true);
       	   auspicios          postgres    false    224            &           0    0    ptn_auspicios_id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('auspicios.ptn_auspicios_id_seq', 1, false);
       	   auspicios          postgres    false    375            '           0    0    ptn_auspicios_recompesas_id_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('auspicios.ptn_auspicios_recompesas_id_seq', 1, false);
       	   auspicios          postgres    false    378            (           0    0    ptn_auspicios_torneos_id_seq    SEQUENCE SET     N   SELECT pg_catalog.setval('auspicios.ptn_auspicios_torneos_id_seq', 1, false);
       	   auspicios          postgres    false    380            )           0    0    ptn_auspicios_valoracion_id_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('auspicios.ptn_auspicios_valoracion_id_seq', 1, false);
       	   auspicios          postgres    false    382            *           0    0    ptn_titulos_deportivos_id_seq    SEQUENCE SET     O   SELECT pg_catalog.setval('auspicios.ptn_titulos_deportivos_id_seq', 1, false);
       	   auspicios          postgres    false    385            +           0    0    mult_menu_id_menu_seq    SEQUENCE SET     D   SELECT pg_catalog.setval('cuenta.mult_menu_id_menu_seq', 10, true);
          cuenta          postgres    false    227            ,           0    0 !   mult_operaciones_id_operacion_seq    SEQUENCE SET     O   SELECT pg_catalog.setval('cuenta.mult_operaciones_id_operacion_seq', 4, true);
          cuenta          postgres    false    230            -           0    0 $   mult_pers_cuentas_id_pers_cuenta_seq    SEQUENCE SET     S   SELECT pg_catalog.setval('cuenta.mult_pers_cuentas_id_pers_cuenta_seq', 68, true);
          cuenta          postgres    false    232            .           0    0 %   mult_pers_documentos_id_documento_seq    SEQUENCE SET     T   SELECT pg_catalog.setval('cuenta.mult_pers_documentos_id_documento_seq', 13, true);
          cuenta          postgres    false    234            /           0    0 %   mult_pers_domicilios_id_domicilio_seq    SEQUENCE SET     T   SELECT pg_catalog.setval('cuenta.mult_pers_domicilios_id_domicilio_seq', 24, true);
          cuenta          postgres    false    236            0           0    0 $   mult_pers_est_finan_id_est_finan_seq    SEQUENCE SET     S   SELECT pg_catalog.setval('cuenta.mult_pers_est_finan_id_est_finan_seq', 1, false);
          cuenta          postgres    false    238            1           0    0    mult_pers_firmas_id_firma_seq    SEQUENCE SET     L   SELECT pg_catalog.setval('cuenta.mult_pers_firmas_id_firma_seq', 1, false);
          cuenta          postgres    false    240            2           0    0 .   mult_pers_info_adicional_id_info_adicional_seq    SEQUENCE SET     ]   SELECT pg_catalog.setval('cuenta.mult_pers_info_adicional_id_info_adicional_seq', 16, true);
          cuenta          postgres    false    242            3           0    0 (   mult_pers_repre_legal_id_repre_legal_seq    SEQUENCE SET     W   SELECT pg_catalog.setval('cuenta.mult_pers_repre_legal_id_repre_legal_seq', 1, false);
          cuenta          postgres    false    244            4           0    0    mult_roles_cuentas_id_rol_seq    SEQUENCE SET     L   SELECT pg_catalog.setval('cuenta.mult_roles_cuentas_id_rol_seq', 64, true);
          cuenta          postgres    false    248            5           0    0    mult_roles_id_rol_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('cuenta.mult_roles_id_rol_seq', 7, true);
          cuenta          postgres    false    249            6           0    0    mult_token_id_token_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('cuenta.mult_token_id_token_seq', 78, true);
          cuenta          postgres    false    251            7           0    0    ptn_menu_id_menu_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('cuenta.ptn_menu_id_menu_seq', 1, false);
          cuenta          postgres    false    388            8           0    0     ptn_operaciones_id_operacion_seq    SEQUENCE SET     O   SELECT pg_catalog.setval('cuenta.ptn_operaciones_id_operacion_seq', 1, false);
          cuenta          postgres    false    391            9           0    0 #   ptn_pers_cuentas_id_pers_cuenta_seq    SEQUENCE SET     R   SELECT pg_catalog.setval('cuenta.ptn_pers_cuentas_id_pers_cuenta_seq', 1, false);
          cuenta          postgres    false    393            :           0    0 $   ptn_pers_documentos_id_documento_seq    SEQUENCE SET     S   SELECT pg_catalog.setval('cuenta.ptn_pers_documentos_id_documento_seq', 1, false);
          cuenta          postgres    false    395            ;           0    0 $   ptn_pers_domicilios_id_domicilio_seq    SEQUENCE SET     S   SELECT pg_catalog.setval('cuenta.ptn_pers_domicilios_id_domicilio_seq', 1, false);
          cuenta          postgres    false    397            <           0    0 #   ptn_pers_est_finan_id_est_finan_seq    SEQUENCE SET     R   SELECT pg_catalog.setval('cuenta.ptn_pers_est_finan_id_est_finan_seq', 1, false);
          cuenta          postgres    false    399            =           0    0    ptn_pers_firmas_id_firma_seq    SEQUENCE SET     K   SELECT pg_catalog.setval('cuenta.ptn_pers_firmas_id_firma_seq', 1, false);
          cuenta          postgres    false    401            >           0    0 -   ptn_pers_info_adicional_id_info_adicional_seq    SEQUENCE SET     \   SELECT pg_catalog.setval('cuenta.ptn_pers_info_adicional_id_info_adicional_seq', 1, false);
          cuenta          postgres    false    403            ?           0    0 '   ptn_pers_repre_legal_id_repre_legal_seq    SEQUENCE SET     V   SELECT pg_catalog.setval('cuenta.ptn_pers_repre_legal_id_repre_legal_seq', 1, false);
          cuenta          postgres    false    405            @           0    0    ptn_roles_id_rol_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('cuenta.ptn_roles_id_rol_seq', 1, false);
          cuenta          postgres    false    408            A           0    0    ptn_token_id_token_seq    SEQUENCE SET     E   SELECT pg_catalog.setval('cuenta.ptn_token_id_token_seq', 1, false);
          cuenta          postgres    false    411            B           0    0    mult_info_emails_id_email_seq    SEQUENCE SET     M   SELECT pg_catalog.setval('emails.mult_info_emails_id_email_seq', 129, true);
          emails          postgres    false    253            C           0    0    ptn_info_emails_id_email_seq    SEQUENCE SET     K   SELECT pg_catalog.setval('emails.ptn_info_emails_id_email_seq', 1, false);
          emails          postgres    false    413            D           0    0    mult_form_contact_id_seq    SEQUENCE SET     G   SELECT pg_catalog.setval('footer.mult_form_contact_id_seq', 16, true);
          footer          postgres    false    256            E           0    0    ptn_form_contact_id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('footer.ptn_form_contact_id_seq', 1, false);
          footer          postgres    false    416            F           0    0    mult_bitacora_procesos_id_seq    SEQUENCE SET     P   SELECT pg_catalog.setval('historicas.mult_bitacora_procesos_id_seq', 1, false);
       
   historicas          postgres    false    258            G           0    0    mult_hist_proyecto_id_seq    SEQUENCE SET     L   SELECT pg_catalog.setval('historicas.mult_hist_proyecto_id_seq', 18, true);
       
   historicas          postgres    false    260            H           0    0    mult_hist_solicitud_id_seq    SEQUENCE SET     N   SELECT pg_catalog.setval('historicas.mult_hist_solicitud_id_seq', 164, true);
       
   historicas          postgres    false    262            I           0    0 *   mult_historial_conciliacion_detalle_id_seq    SEQUENCE SET     ]   SELECT pg_catalog.setval('historicas.mult_historial_conciliacion_detalle_id_seq', 27, true);
       
   historicas          postgres    false    265            J           0    0 "   mult_historial_conciliacion_id_seq    SEQUENCE SET     U   SELECT pg_catalog.setval('historicas.mult_historial_conciliacion_id_seq', 24, true);
       
   historicas          postgres    false    266            K           0    0    mult_sol_x_identifiacion_id_seq    SEQUENCE SET     R   SELECT pg_catalog.setval('historicas.mult_sol_x_identifiacion_id_seq', 1, false);
       
   historicas          postgres    false    268            L           0    0    ptn_bitacora_procesos_id_seq    SEQUENCE SET     O   SELECT pg_catalog.setval('historicas.ptn_bitacora_procesos_id_seq', 1, false);
       
   historicas          postgres    false    418            M           0    0    ptn_hist_proyecto_id_seq    SEQUENCE SET     K   SELECT pg_catalog.setval('historicas.ptn_hist_proyecto_id_seq', 1, false);
       
   historicas          postgres    false    420            N           0    0    ptn_hist_solicitud_id_seq    SEQUENCE SET     L   SELECT pg_catalog.setval('historicas.ptn_hist_solicitud_id_seq', 1, false);
       
   historicas          postgres    false    422            O           0    0 )   ptn_historial_conciliacion_detalle_id_seq    SEQUENCE SET     \   SELECT pg_catalog.setval('historicas.ptn_historial_conciliacion_detalle_id_seq', 1, false);
       
   historicas          postgres    false    426            P           0    0 !   ptn_historial_conciliacion_id_seq    SEQUENCE SET     T   SELECT pg_catalog.setval('historicas.ptn_historial_conciliacion_id_seq', 1, false);
       
   historicas          postgres    false    424            Q           0    0    ptn_sol_x_identifiacion_id_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('historicas.ptn_sol_x_identifiacion_id_seq', 1, false);
       
   historicas          postgres    false    428            R           0    0     mult_datos_inversion_id_dato_seq    SEQUENCE SET     R   SELECT pg_catalog.setval('inversion.mult_datos_inversion_id_dato_seq', 32, true);
       	   inversion          postgres    false    270            S           0    0 1   mult_detalle_amortizacion_id_det_amortizacion_seq    SEQUENCE SET     d   SELECT pg_catalog.setval('inversion.mult_detalle_amortizacion_id_det_amortizacion_seq', 705, true);
       	   inversion          postgres    false    272            T           0    0 &   mult_doc_aceptados_id_doc_aceptado_seq    SEQUENCE SET     X   SELECT pg_catalog.setval('inversion.mult_doc_aceptados_id_doc_aceptado_seq', 38, true);
       	   inversion          postgres    false    274            U           0    0 1   mult_solicitudes_documentos_id_sol_documentos_seq    SEQUENCE SET     c   SELECT pg_catalog.setval('inversion.mult_solicitudes_documentos_id_sol_documentos_seq', 15, true);
       	   inversion          postgres    false    277            V           0    0 %   mult_solicitudes_numero_solicitud_seq    SEQUENCE SET     W   SELECT pg_catalog.setval('inversion.mult_solicitudes_numero_solicitud_seq', 31, true);
       	   inversion          postgres    false    278            W           0    0 $   mult_solicitudes_numerosolicitud_seq    SEQUENCE SET     V   SELECT pg_catalog.setval('inversion.mult_solicitudes_numerosolicitud_seq', 44, true);
       	   inversion          postgres    false    279            X           0    0 /   mult_tabla_amortizacion_id_tbl_amortizacion_seq    SEQUENCE SET     a   SELECT pg_catalog.setval('inversion.mult_tabla_amortizacion_id_tbl_amortizacion_seq', 58, true);
       	   inversion          postgres    false    281            Y           0    0 )   mult_transacciones_id_doc_transaccion_seq    SEQUENCE SET     [   SELECT pg_catalog.setval('inversion.mult_transacciones_id_doc_transaccion_seq', 20, true);
       	   inversion          postgres    false    284            Z           0    0    ptn_datos_inversion_id_dato_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('inversion.ptn_datos_inversion_id_dato_seq', 1, false);
       	   inversion          postgres    false    430            [           0    0 0   ptn_detalle_amortizacion_id_det_amortizacion_seq    SEQUENCE SET     b   SELECT pg_catalog.setval('inversion.ptn_detalle_amortizacion_id_det_amortizacion_seq', 1, false);
       	   inversion          postgres    false    432            \           0    0 %   ptn_doc_aceptados_id_doc_aceptado_seq    SEQUENCE SET     W   SELECT pg_catalog.setval('inversion.ptn_doc_aceptados_id_doc_aceptado_seq', 1, false);
       	   inversion          postgres    false    434            ]           0    0 0   ptn_solicitudes_documentos_id_sol_documentos_seq    SEQUENCE SET     b   SELECT pg_catalog.setval('inversion.ptn_solicitudes_documentos_id_sol_documentos_seq', 1, false);
       	   inversion          postgres    false    438            ^           0    0 $   ptn_solicitudes_numero_solicitud_seq    SEQUENCE SET     V   SELECT pg_catalog.setval('inversion.ptn_solicitudes_numero_solicitud_seq', 1, false);
       	   inversion          postgres    false    436            _           0    0 .   ptn_tabla_amortizacion_id_tbl_amortizacion_seq    SEQUENCE SET     `   SELECT pg_catalog.setval('inversion.ptn_tabla_amortizacion_id_tbl_amortizacion_seq', 1, false);
       	   inversion          postgres    false    440            `           0    0 (   ptn_transacciones_id_doc_transaccion_seq    SEQUENCE SET     Z   SELECT pg_catalog.setval('inversion.ptn_transacciones_id_doc_transaccion_seq', 1, false);
       	   inversion          postgres    false    443            a           0    0    mult_bancos_id_banco_seq    SEQUENCE SET     I   SELECT pg_catalog.setval('maestras.mult_bancos_id_banco_seq', 26, true);
          maestras          postgres    false    286            b           0    0    mult_ciudades_id_ciudad_seq    SEQUENCE SET     M   SELECT pg_catalog.setval('maestras.mult_ciudades_id_ciudad_seq', 221, true);
          maestras          postgres    false    288            c           0    0 !   mult_forma_pago_id_forma_pago_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('maestras.mult_forma_pago_id_forma_pago_seq', 2, true);
          maestras          postgres    false    290            d           0    0 '   mult_nacionalidades_id_nacionalidad_seq    SEQUENCE SET     Y   SELECT pg_catalog.setval('maestras.mult_nacionalidades_id_nacionalidad_seq', 185, true);
          maestras          postgres    false    292            e           0    0    mult_rango_pago_id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('maestras.mult_rango_pago_id_seq', 4, true);
          maestras          postgres    false    294            f           0    0 *   mult_tipo_documentos_id_tipo_documento_seq    SEQUENCE SET     Z   SELECT pg_catalog.setval('maestras.mult_tipo_documentos_id_tipo_documento_seq', 5, true);
          maestras          postgres    false    296            g           0    0    mult_tipo_solicitud_id_seq    SEQUENCE SET     K   SELECT pg_catalog.setval('maestras.mult_tipo_solicitud_id_seq', 1, false);
          maestras          postgres    false    298            h           0    0 "   mult_tipo_tablas_id_tipo_tabla_seq    SEQUENCE SET     R   SELECT pg_catalog.setval('maestras.mult_tipo_tablas_id_tipo_tabla_seq', 3, true);
          maestras          postgres    false    300            i           0    0 /   mult_tipo_transacciones_id_tipo_transaccion_seq    SEQUENCE SET     _   SELECT pg_catalog.setval('maestras.mult_tipo_transacciones_id_tipo_transaccion_seq', 2, true);
          maestras          postgres    false    302            j           0    0    ptn_bancos_id_banco_seq    SEQUENCE SET     H   SELECT pg_catalog.setval('maestras.ptn_bancos_id_banco_seq', 1, false);
          maestras          postgres    false    445            k           0    0    ptn_ciudades_id_ciudad_seq    SEQUENCE SET     K   SELECT pg_catalog.setval('maestras.ptn_ciudades_id_ciudad_seq', 1, false);
          maestras          postgres    false    447            l           0    0     ptn_forma_pago_id_forma_pago_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('maestras.ptn_forma_pago_id_forma_pago_seq', 1, false);
          maestras          postgres    false    449            m           0    0 &   ptn_nacionalidades_id_nacionalidad_seq    SEQUENCE SET     W   SELECT pg_catalog.setval('maestras.ptn_nacionalidades_id_nacionalidad_seq', 1, false);
          maestras          postgres    false    451            n           0    0    ptn_rango_pago_id_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('maestras.ptn_rango_pago_id_seq', 1, false);
          maestras          postgres    false    453            o           0    0 )   ptn_tipo_documentos_id_tipo_documento_seq    SEQUENCE SET     Z   SELECT pg_catalog.setval('maestras.ptn_tipo_documentos_id_tipo_documento_seq', 1, false);
          maestras          postgres    false    455            p           0    0    ptn_tipo_solicitud_id_seq    SEQUENCE SET     J   SELECT pg_catalog.setval('maestras.ptn_tipo_solicitud_id_seq', 1, false);
          maestras          postgres    false    457            q           0    0 !   ptn_tipo_tablas_id_tipo_tabla_seq    SEQUENCE SET     R   SELECT pg_catalog.setval('maestras.ptn_tipo_tablas_id_tipo_tabla_seq', 1, false);
          maestras          postgres    false    459            r           0    0    mult_deport_categorias_id_seq    SEQUENCE SET     W   SELECT pg_catalog.setval('maestras_auspicios.mult_deport_categorias_id_seq', 1, true);
          maestras_auspicios          postgres    false    304            s           0    0    mult_deport_disciplina_id_seq    SEQUENCE SET     X   SELECT pg_catalog.setval('maestras_auspicios.mult_deport_disciplina_id_seq', 21, true);
          maestras_auspicios          postgres    false    306            t           0    0    mult_deport_modalidad_id_seq    SEQUENCE SET     V   SELECT pg_catalog.setval('maestras_auspicios.mult_deport_modalidad_id_seq', 1, true);
          maestras_auspicios          postgres    false    308            u           0    0    ptn_deport_categorias_id_seq    SEQUENCE SET     W   SELECT pg_catalog.setval('maestras_auspicios.ptn_deport_categorias_id_seq', 1, false);
          maestras_auspicios          postgres    false    461            v           0    0    ptn_deport_disciplina_id_seq    SEQUENCE SET     W   SELECT pg_catalog.setval('maestras_auspicios.ptn_deport_disciplina_id_seq', 1, false);
          maestras_auspicios          postgres    false    463            w           0    0    ptn_deport_modalidad_id_seq    SEQUENCE SET     V   SELECT pg_catalog.setval('maestras_auspicios.ptn_deport_modalidad_id_seq', 1, false);
          maestras_auspicios          postgres    false    465            x           0    0    mult_menu_int_id_menu_seq    SEQUENCE SET     J   SELECT pg_catalog.setval('multiplo.mult_menu_int_id_menu_seq', 1, false);
          multiplo          postgres    false    311            y           0    0 %   mult_operaciones_int_id_operacion_seq    SEQUENCE SET     V   SELECT pg_catalog.setval('multiplo.mult_operaciones_int_id_operacion_seq', 1, false);
          multiplo          postgres    false    314            z           0    0    mult_roles_int_id_rol_seq    SEQUENCE SET     J   SELECT pg_catalog.setval('multiplo.mult_roles_int_id_rol_seq', 1, false);
          multiplo          postgres    false    318            {           0    0    mult_documentos_facturas_id_seq    SEQUENCE SET     Z   SELECT pg_catalog.setval('multiplo_documentos.mult_documentos_facturas_id_seq', 9, true);
          multiplo_documentos          postgres    false    321            |           0    0     mult_documentos_id_documento_seq    SEQUENCE SET     [   SELECT pg_catalog.setval('multiplo_documentos.mult_documentos_id_documento_seq', 2, true);
          multiplo_documentos          postgres    false    322            }           0    0 )   mult_conciliacion_aprobada_detalle_id_seq    SEQUENCE SET     Y   SELECT pg_catalog.setval('negocio.mult_conciliacion_aprobada_detalle_id_seq', 15, true);
          negocio          postgres    false    326            ~           0    0 !   mult_conciliacion_aprobada_id_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('negocio.mult_conciliacion_aprobada_id_seq', 11, true);
          negocio          postgres    false    327                       0    0 $   mult_conciliacion_detalle_xls_id_seq    SEQUENCE SET     T   SELECT pg_catalog.setval('negocio.mult_conciliacion_detalle_xls_id_seq', 48, true);
          negocio          postgres    false    329            �           0    0    mult_conciliacion_xls_id_seq    SEQUENCE SET     L   SELECT pg_catalog.setval('negocio.mult_conciliacion_xls_id_seq', 22, true);
          negocio          postgres    false    331            �           0    0    mult_fecha_gen_tbl_amort_id_seq    SEQUENCE SET     N   SELECT pg_catalog.setval('negocio.mult_fecha_gen_tbl_amort_id_seq', 6, true);
          negocio          postgres    false    333            �           0    0 %   mult_porc_interes_tbl_proyecto_id_seq    SEQUENCE SET     U   SELECT pg_catalog.setval('negocio.mult_porc_interes_tbl_proyecto_id_seq', 1, false);
          negocio          postgres    false    335            �           0    0 (   ptn_conciliacion_aprobada_detalle_id_seq    SEQUENCE SET     X   SELECT pg_catalog.setval('negocio.ptn_conciliacion_aprobada_detalle_id_seq', 1, false);
          negocio          postgres    false    469            �           0    0     ptn_conciliacion_aprobada_id_seq    SEQUENCE SET     P   SELECT pg_catalog.setval('negocio.ptn_conciliacion_aprobada_id_seq', 1, false);
          negocio          postgres    false    467            �           0    0 #   ptn_conciliacion_detalle_xls_id_seq    SEQUENCE SET     S   SELECT pg_catalog.setval('negocio.ptn_conciliacion_detalle_xls_id_seq', 1, false);
          negocio          postgres    false    471            �           0    0    ptn_conciliacion_xls_id_seq    SEQUENCE SET     K   SELECT pg_catalog.setval('negocio.ptn_conciliacion_xls_id_seq', 1, false);
          negocio          postgres    false    473            �           0    0    ptn_fecha_gen_tbl_amort_id_seq    SEQUENCE SET     N   SELECT pg_catalog.setval('negocio.ptn_fecha_gen_tbl_amort_id_seq', 1, false);
          negocio          postgres    false    475            �           0    0 $   ptn_porc_interes_tbl_proyecto_id_seq    SEQUENCE SET     T   SELECT pg_catalog.setval('negocio.ptn_porc_interes_tbl_proyecto_id_seq', 1, false);
          negocio          postgres    false    477            �           0    0     mult_parametros_id_parametro_seq    SEQUENCE SET     X   SELECT pg_catalog.setval('parametrizacion.mult_parametros_id_parametro_seq', 48, true);
          parametrizacion          postgres    false    337            �           0    0    ptn_parametros_id_parametro_seq    SEQUENCE SET     W   SELECT pg_catalog.setval('parametrizacion.ptn_parametros_id_parametro_seq', 1, false);
          parametrizacion          postgres    false    479            �           0    0 &   mult_detalle_porc_sol_aprobadas_id_seq    SEQUENCE SET     W   SELECT pg_catalog.setval('promotor.mult_detalle_porc_sol_aprobadas_id_seq', 15, true);
          promotor          postgres    false    339            �           0    0 "   mult_documentos_financieros_id_seq    SEQUENCE SET     R   SELECT pg_catalog.setval('promotor.mult_documentos_financieros_id_seq', 3, true);
          promotor          postgres    false    341            �           0    0     mult_documentos_juridicos_id_seq    SEQUENCE SET     P   SELECT pg_catalog.setval('promotor.mult_documentos_juridicos_id_seq', 3, true);
          promotor          postgres    false    343            �           0    0 !   mult_empresa_datos_anuales_id_seq    SEQUENCE SET     R   SELECT pg_catalog.setval('promotor.mult_empresa_datos_anuales_id_seq', 23, true);
          promotor          postgres    false    345            �           0    0    mult_empresas_id_empresa_seq    SEQUENCE SET     L   SELECT pg_catalog.setval('promotor.mult_empresas_id_empresa_seq', 8, true);
          promotor          postgres    false    347            �           0    0 !   mult_indicadores_id_indicador_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('promotor.mult_indicadores_id_indicador_seq', 4, true);
          promotor          postgres    false    349            �           0    0    mult_porc_sol_aprobadas_id_seq    SEQUENCE SET     N   SELECT pg_catalog.setval('promotor.mult_porc_sol_aprobadas_id_seq', 6, true);
          promotor          postgres    false    351            �           0    0 -   mult_proyectos_cuentas_id_proyecto_cuenta_seq    SEQUENCE SET     ]   SELECT pg_catalog.setval('promotor.mult_proyectos_cuentas_id_proyecto_cuenta_seq', 6, true);
          promotor          postgres    false    354            �           0    0 )   mult_proyectos_rutas_id_proyecto_ruta_seq    SEQUENCE SET     Z   SELECT pg_catalog.setval('promotor.mult_proyectos_rutas_id_proyecto_ruta_seq', 18, true);
          promotor          postgres    false    356            �           0    0    mult_rango_pagos_id_seq    SEQUENCE SET     H   SELECT pg_catalog.setval('promotor.mult_rango_pagos_id_seq', 1, false);
          promotor          postgres    false    358            �           0    0 &   mult_tipo_actividades_id_actividad_seq    SEQUENCE SET     V   SELECT pg_catalog.setval('promotor.mult_tipo_actividades_id_actividad_seq', 4, true);
          promotor          postgres    false    360            �           0    0 1   mult_tipo_calificaciones_id_tipo_calificacion_seq    SEQUENCE SET     a   SELECT pg_catalog.setval('promotor.mult_tipo_calificaciones_id_tipo_calificacion_seq', 1, true);
          promotor          postgres    false    362            �           0    0 %   ptn_detalle_porc_sol_aprobadas_id_seq    SEQUENCE SET     V   SELECT pg_catalog.setval('promotor.ptn_detalle_porc_sol_aprobadas_id_seq', 1, false);
          promotor          postgres    false    481            �           0    0 !   ptn_documentos_financieros_id_seq    SEQUENCE SET     R   SELECT pg_catalog.setval('promotor.ptn_documentos_financieros_id_seq', 1, false);
          promotor          postgres    false    483            �           0    0    ptn_documentos_juridicos_id_seq    SEQUENCE SET     P   SELECT pg_catalog.setval('promotor.ptn_documentos_juridicos_id_seq', 1, false);
          promotor          postgres    false    485            �           0    0     ptn_empresa_datos_anuales_id_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('promotor.ptn_empresa_datos_anuales_id_seq', 1, false);
          promotor          postgres    false    487            �           0    0    ptn_empresas_id_empresa_seq    SEQUENCE SET     L   SELECT pg_catalog.setval('promotor.ptn_empresas_id_empresa_seq', 1, false);
          promotor          postgres    false    489            �           0    0     ptn_indicadores_id_indicador_seq    SEQUENCE SET     Q   SELECT pg_catalog.setval('promotor.ptn_indicadores_id_indicador_seq', 1, false);
          promotor          postgres    false    491            �           0    0    ptn_porc_sol_aprobadas_id_seq    SEQUENCE SET     N   SELECT pg_catalog.setval('promotor.ptn_porc_sol_aprobadas_id_seq', 1, false);
          promotor          postgres    false    493            �           0    0 ,   ptn_proyectos_cuentas_id_proyecto_cuenta_seq    SEQUENCE SET     ]   SELECT pg_catalog.setval('promotor.ptn_proyectos_cuentas_id_proyecto_cuenta_seq', 1, false);
          promotor          postgres    false    496            �           0    0 (   ptn_proyectos_rutas_id_proyecto_ruta_seq    SEQUENCE SET     Y   SELECT pg_catalog.setval('promotor.ptn_proyectos_rutas_id_proyecto_ruta_seq', 1, false);
          promotor          postgres    false    498            �           0    0 %   ptn_tipo_actividades_id_actividad_seq    SEQUENCE SET     V   SELECT pg_catalog.setval('promotor.ptn_tipo_actividades_id_actividad_seq', 1, false);
          promotor          postgres    false    500            �           0    0 0   ptn_tipo_calificaciones_id_tipo_calificacion_seq    SEQUENCE SET     a   SELECT pg_catalog.setval('promotor.ptn_tipo_calificaciones_id_tipo_calificacion_seq', 1, false);
          promotor          postgres    false    502            �           0    0    hibernate_sequence    SEQUENCE SET     A   SELECT pg_catalog.setval('public.hibernate_sequence', 1, false);
          public          postgres    false    363            �           0    0    mult_cuentas_id_cuenta_seq    SEQUENCE SET     I   SELECT pg_catalog.setval('public.mult_cuentas_id_cuenta_seq', 43, true);
          public          postgres    false    365            �           0    0    mult_empleados_id_empleado_seq    SEQUENCE SET     L   SELECT pg_catalog.setval('public.mult_empleados_id_empleado_seq', 8, true);
          public          postgres    false    367            �           0    0    mult_personas_idpersona_seq    SEQUENCE SET     J   SELECT pg_catalog.setval('public.mult_personas_idpersona_seq', 38, true);
          public          postgres    false    369            �           0    0    mult_roles_id_rol_seq    SEQUENCE SET     C   SELECT pg_catalog.setval('public.mult_roles_id_rol_seq', 5, true);
          public          postgres    false    371            �           0    0    mult_token_id_token_seq    SEQUENCE SET     F   SELECT pg_catalog.setval('public.mult_token_id_token_seq', 38, true);
          public          postgres    false    373            �           2606    68543 2   mult_auspicios_estados mult_auspicios_estados_pkey 
   CONSTRAINT     z   ALTER TABLE ONLY auspicios.mult_auspicios_estados
    ADD CONSTRAINT mult_auspicios_estados_pkey PRIMARY KEY (id_estado);
 _   ALTER TABLE ONLY auspicios.mult_auspicios_estados DROP CONSTRAINT mult_auspicios_estados_pkey;
    	   auspicios            postgres    false    214            �           2606    68545 "   mult_auspicios mult_auspicios_pkey 
   CONSTRAINT     c   ALTER TABLE ONLY auspicios.mult_auspicios
    ADD CONSTRAINT mult_auspicios_pkey PRIMARY KEY (id);
 O   ALTER TABLE ONLY auspicios.mult_auspicios DROP CONSTRAINT mult_auspicios_pkey;
    	   auspicios            postgres    false    213            �           2606    68547 8   mult_auspicios_recompesas mult_auspicios_recompesas_pkey 
   CONSTRAINT     y   ALTER TABLE ONLY auspicios.mult_auspicios_recompesas
    ADD CONSTRAINT mult_auspicios_recompesas_pkey PRIMARY KEY (id);
 e   ALTER TABLE ONLY auspicios.mult_auspicios_recompesas DROP CONSTRAINT mult_auspicios_recompesas_pkey;
    	   auspicios            postgres    false    216            �           2606    68549 2   mult_auspicios_torneos mult_auspicios_torneos_pkey 
   CONSTRAINT     s   ALTER TABLE ONLY auspicios.mult_auspicios_torneos
    ADD CONSTRAINT mult_auspicios_torneos_pkey PRIMARY KEY (id);
 _   ALTER TABLE ONLY auspicios.mult_auspicios_torneos DROP CONSTRAINT mult_auspicios_torneos_pkey;
    	   auspicios            postgres    false    218            �           2606    68551 8   mult_auspicios_valoracion mult_auspicios_valoracion_pkey 
   CONSTRAINT     y   ALTER TABLE ONLY auspicios.mult_auspicios_valoracion
    ADD CONSTRAINT mult_auspicios_valoracion_pkey PRIMARY KEY (id);
 e   ALTER TABLE ONLY auspicios.mult_auspicios_valoracion DROP CONSTRAINT mult_auspicios_valoracion_pkey;
    	   auspicios            postgres    false    220            �           2606    68553 (   mult_beneficiario mult_beneficiario_pkey 
   CONSTRAINT     i   ALTER TABLE ONLY auspicios.mult_beneficiario
    ADD CONSTRAINT mult_beneficiario_pkey PRIMARY KEY (id);
 U   ALTER TABLE ONLY auspicios.mult_beneficiario DROP CONSTRAINT mult_beneficiario_pkey;
    	   auspicios            postgres    false    222            �           2606    68555 4   mult_titulos_deportivos mult_titulos_deportivos_pkey 
   CONSTRAINT     u   ALTER TABLE ONLY auspicios.mult_titulos_deportivos
    ADD CONSTRAINT mult_titulos_deportivos_pkey PRIMARY KEY (id);
 a   ALTER TABLE ONLY auspicios.mult_titulos_deportivos DROP CONSTRAINT mult_titulos_deportivos_pkey;
    	   auspicios            postgres    false    223            �           2606    85959 0   ptn_auspicios_estados ptn_auspicios_estados_pkey 
   CONSTRAINT     x   ALTER TABLE ONLY auspicios.ptn_auspicios_estados
    ADD CONSTRAINT ptn_auspicios_estados_pkey PRIMARY KEY (id_estado);
 ]   ALTER TABLE ONLY auspicios.ptn_auspicios_estados DROP CONSTRAINT ptn_auspicios_estados_pkey;
    	   auspicios            postgres    false    377            �           2606    85954     ptn_auspicios ptn_auspicios_pkey 
   CONSTRAINT     a   ALTER TABLE ONLY auspicios.ptn_auspicios
    ADD CONSTRAINT ptn_auspicios_pkey PRIMARY KEY (id);
 M   ALTER TABLE ONLY auspicios.ptn_auspicios DROP CONSTRAINT ptn_auspicios_pkey;
    	   auspicios            postgres    false    376            �           2606    85966 6   ptn_auspicios_recompesas ptn_auspicios_recompesas_pkey 
   CONSTRAINT     w   ALTER TABLE ONLY auspicios.ptn_auspicios_recompesas
    ADD CONSTRAINT ptn_auspicios_recompesas_pkey PRIMARY KEY (id);
 c   ALTER TABLE ONLY auspicios.ptn_auspicios_recompesas DROP CONSTRAINT ptn_auspicios_recompesas_pkey;
    	   auspicios            postgres    false    379            �           2606    85973 0   ptn_auspicios_torneos ptn_auspicios_torneos_pkey 
   CONSTRAINT     q   ALTER TABLE ONLY auspicios.ptn_auspicios_torneos
    ADD CONSTRAINT ptn_auspicios_torneos_pkey PRIMARY KEY (id);
 ]   ALTER TABLE ONLY auspicios.ptn_auspicios_torneos DROP CONSTRAINT ptn_auspicios_torneos_pkey;
    	   auspicios            postgres    false    381                       2606    85983 6   ptn_auspicios_valoracion ptn_auspicios_valoracion_pkey 
   CONSTRAINT     w   ALTER TABLE ONLY auspicios.ptn_auspicios_valoracion
    ADD CONSTRAINT ptn_auspicios_valoracion_pkey PRIMARY KEY (id);
 c   ALTER TABLE ONLY auspicios.ptn_auspicios_valoracion DROP CONSTRAINT ptn_auspicios_valoracion_pkey;
    	   auspicios            postgres    false    383                       2606    85991 &   ptn_beneficiario ptn_beneficiario_pkey 
   CONSTRAINT     g   ALTER TABLE ONLY auspicios.ptn_beneficiario
    ADD CONSTRAINT ptn_beneficiario_pkey PRIMARY KEY (id);
 S   ALTER TABLE ONLY auspicios.ptn_beneficiario DROP CONSTRAINT ptn_beneficiario_pkey;
    	   auspicios            postgres    false    384                       2606    85998 2   ptn_titulos_deportivos ptn_titulos_deportivos_pkey 
   CONSTRAINT     s   ALTER TABLE ONLY auspicios.ptn_titulos_deportivos
    ADD CONSTRAINT ptn_titulos_deportivos_pkey PRIMARY KEY (id);
 _   ALTER TABLE ONLY auspicios.ptn_titulos_deportivos DROP CONSTRAINT ptn_titulos_deportivos_pkey;
    	   auspicios            postgres    false    386                       2606    86002 -   ptn_beneficiario uk_2ue17sstkfpx2cjoqdmqqj51q 
   CONSTRAINT     q   ALTER TABLE ONLY auspicios.ptn_beneficiario
    ADD CONSTRAINT uk_2ue17sstkfpx2cjoqdmqqj51q UNIQUE (id_persona);
 Z   ALTER TABLE ONLY auspicios.ptn_beneficiario DROP CONSTRAINT uk_2ue17sstkfpx2cjoqdmqqj51q;
    	   auspicios            postgres    false    384            �           2606    68557 3   mult_auspicios_estados uk_8q924y05kf3n2pscexwsi8xra 
   CONSTRAINT     x   ALTER TABLE ONLY auspicios.mult_auspicios_estados
    ADD CONSTRAINT uk_8q924y05kf3n2pscexwsi8xra UNIQUE (descripcion);
 `   ALTER TABLE ONLY auspicios.mult_auspicios_estados DROP CONSTRAINT uk_8q924y05kf3n2pscexwsi8xra;
    	   auspicios            postgres    false    214            �           2606    68559 .   mult_beneficiario uk_i5s4rtwle5wtiynvt3l5r8d7e 
   CONSTRAINT     r   ALTER TABLE ONLY auspicios.mult_beneficiario
    ADD CONSTRAINT uk_i5s4rtwle5wtiynvt3l5r8d7e UNIQUE (id_persona);
 [   ALTER TABLE ONLY auspicios.mult_beneficiario DROP CONSTRAINT uk_i5s4rtwle5wtiynvt3l5r8d7e;
    	   auspicios            postgres    false    222            �           2606    86000 2   ptn_auspicios_estados uk_r6hx6qeuofmvhdc69ivhjab1n 
   CONSTRAINT     w   ALTER TABLE ONLY auspicios.ptn_auspicios_estados
    ADD CONSTRAINT uk_r6hx6qeuofmvhdc69ivhjab1n UNIQUE (descripcion);
 _   ALTER TABLE ONLY auspicios.ptn_auspicios_estados DROP CONSTRAINT uk_r6hx6qeuofmvhdc69ivhjab1n;
    	   auspicios            postgres    false    377            �           2606    68561    mult_cuentas mult_cuentas_pkey 
   CONSTRAINT     c   ALTER TABLE ONLY cuenta.mult_cuentas
    ADD CONSTRAINT mult_cuentas_pkey PRIMARY KEY (id_cuenta);
 H   ALTER TABLE ONLY cuenta.mult_cuentas DROP CONSTRAINT mult_cuentas_pkey;
       cuenta            postgres    false    225            �           2606    68563 ,   mult_menu_operacion mult_menu_operacion_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_menu_operacion
    ADD CONSTRAINT mult_menu_operacion_pkey PRIMARY KEY (id_menu, id_operacion, id_rol);
 V   ALTER TABLE ONLY cuenta.mult_menu_operacion DROP CONSTRAINT mult_menu_operacion_pkey;
       cuenta            postgres    false    228    228    228            �           2606    68565    mult_menu mult_menu_pkey 
   CONSTRAINT     [   ALTER TABLE ONLY cuenta.mult_menu
    ADD CONSTRAINT mult_menu_pkey PRIMARY KEY (id_menu);
 B   ALTER TABLE ONLY cuenta.mult_menu DROP CONSTRAINT mult_menu_pkey;
       cuenta            postgres    false    226            �           2606    68567 &   mult_operaciones mult_operaciones_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY cuenta.mult_operaciones
    ADD CONSTRAINT mult_operaciones_pkey PRIMARY KEY (id_operacion);
 P   ALTER TABLE ONLY cuenta.mult_operaciones DROP CONSTRAINT mult_operaciones_pkey;
       cuenta            postgres    false    229            �           2606    68569 (   mult_pers_cuentas mult_pers_cuentas_pkey 
   CONSTRAINT     r   ALTER TABLE ONLY cuenta.mult_pers_cuentas
    ADD CONSTRAINT mult_pers_cuentas_pkey PRIMARY KEY (id_pers_cuenta);
 R   ALTER TABLE ONLY cuenta.mult_pers_cuentas DROP CONSTRAINT mult_pers_cuentas_pkey;
       cuenta            postgres    false    231                        2606    68571 .   mult_pers_documentos mult_pers_documentos_pkey 
   CONSTRAINT     v   ALTER TABLE ONLY cuenta.mult_pers_documentos
    ADD CONSTRAINT mult_pers_documentos_pkey PRIMARY KEY (id_documento);
 X   ALTER TABLE ONLY cuenta.mult_pers_documentos DROP CONSTRAINT mult_pers_documentos_pkey;
       cuenta            postgres    false    233                       2606    68573 .   mult_pers_domicilios mult_pers_domicilios_pkey 
   CONSTRAINT     v   ALTER TABLE ONLY cuenta.mult_pers_domicilios
    ADD CONSTRAINT mult_pers_domicilios_pkey PRIMARY KEY (id_domicilio);
 X   ALTER TABLE ONLY cuenta.mult_pers_domicilios DROP CONSTRAINT mult_pers_domicilios_pkey;
       cuenta            postgres    false    235                       2606    68575 ,   mult_pers_est_finan mult_pers_est_finan_pkey 
   CONSTRAINT     t   ALTER TABLE ONLY cuenta.mult_pers_est_finan
    ADD CONSTRAINT mult_pers_est_finan_pkey PRIMARY KEY (id_est_finan);
 V   ALTER TABLE ONLY cuenta.mult_pers_est_finan DROP CONSTRAINT mult_pers_est_finan_pkey;
       cuenta            postgres    false    237                       2606    68577 &   mult_pers_firmas mult_pers_firmas_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY cuenta.mult_pers_firmas
    ADD CONSTRAINT mult_pers_firmas_pkey PRIMARY KEY (id_firma);
 P   ALTER TABLE ONLY cuenta.mult_pers_firmas DROP CONSTRAINT mult_pers_firmas_pkey;
       cuenta            postgres    false    239                       2606    68579 6   mult_pers_info_adicional mult_pers_info_adicional_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_info_adicional
    ADD CONSTRAINT mult_pers_info_adicional_pkey PRIMARY KEY (id_info_adicional);
 `   ALTER TABLE ONLY cuenta.mult_pers_info_adicional DROP CONSTRAINT mult_pers_info_adicional_pkey;
       cuenta            postgres    false    241                       2606    68581 0   mult_pers_repre_legal mult_pers_repre_legal_pkey 
   CONSTRAINT     z   ALTER TABLE ONLY cuenta.mult_pers_repre_legal
    ADD CONSTRAINT mult_pers_repre_legal_pkey PRIMARY KEY (id_repre_legal);
 Z   ALTER TABLE ONLY cuenta.mult_pers_repre_legal DROP CONSTRAINT mult_pers_repre_legal_pkey;
       cuenta            postgres    false    243                       2606    68583     mult_personas mult_personas_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY cuenta.mult_personas
    ADD CONSTRAINT mult_personas_pkey PRIMARY KEY (identificacion);
 J   ALTER TABLE ONLY cuenta.mult_personas DROP CONSTRAINT mult_personas_pkey;
       cuenta            postgres    false    245                       2606    68585 *   mult_roles_cuentas mult_roles_cuentas_pkey 
   CONSTRAINT     l   ALTER TABLE ONLY cuenta.mult_roles_cuentas
    ADD CONSTRAINT mult_roles_cuentas_pkey PRIMARY KEY (id_rol);
 T   ALTER TABLE ONLY cuenta.mult_roles_cuentas DROP CONSTRAINT mult_roles_cuentas_pkey;
       cuenta            postgres    false    247                       2606    68587    mult_roles mult_roles_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY cuenta.mult_roles
    ADD CONSTRAINT mult_roles_pkey PRIMARY KEY (id_rol);
 D   ALTER TABLE ONLY cuenta.mult_roles DROP CONSTRAINT mult_roles_pkey;
       cuenta            postgres    false    246                       2606    68589    mult_token mult_token_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY cuenta.mult_token
    ADD CONSTRAINT mult_token_pkey PRIMARY KEY (id_token);
 D   ALTER TABLE ONLY cuenta.mult_token DROP CONSTRAINT mult_token_pkey;
       cuenta            postgres    false    250            	           2606    86007    ptn_cuentas ptn_cuentas_pkey 
   CONSTRAINT     a   ALTER TABLE ONLY cuenta.ptn_cuentas
    ADD CONSTRAINT ptn_cuentas_pkey PRIMARY KEY (id_cuenta);
 F   ALTER TABLE ONLY cuenta.ptn_cuentas DROP CONSTRAINT ptn_cuentas_pkey;
       cuenta            postgres    false    387                       2606    86022 *   ptn_menu_operacion ptn_menu_operacion_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_menu_operacion
    ADD CONSTRAINT ptn_menu_operacion_pkey PRIMARY KEY (id_menu, id_operacion, id_rol);
 T   ALTER TABLE ONLY cuenta.ptn_menu_operacion DROP CONSTRAINT ptn_menu_operacion_pkey;
       cuenta            postgres    false    390    390    390                       2606    86017    ptn_menu ptn_menu_pkey 
   CONSTRAINT     Y   ALTER TABLE ONLY cuenta.ptn_menu
    ADD CONSTRAINT ptn_menu_pkey PRIMARY KEY (id_menu);
 @   ALTER TABLE ONLY cuenta.ptn_menu DROP CONSTRAINT ptn_menu_pkey;
       cuenta            postgres    false    389                       2606    86029 $   ptn_operaciones ptn_operaciones_pkey 
   CONSTRAINT     l   ALTER TABLE ONLY cuenta.ptn_operaciones
    ADD CONSTRAINT ptn_operaciones_pkey PRIMARY KEY (id_operacion);
 N   ALTER TABLE ONLY cuenta.ptn_operaciones DROP CONSTRAINT ptn_operaciones_pkey;
       cuenta            postgres    false    392                       2606    86036 &   ptn_pers_cuentas ptn_pers_cuentas_pkey 
   CONSTRAINT     p   ALTER TABLE ONLY cuenta.ptn_pers_cuentas
    ADD CONSTRAINT ptn_pers_cuentas_pkey PRIMARY KEY (id_pers_cuenta);
 P   ALTER TABLE ONLY cuenta.ptn_pers_cuentas DROP CONSTRAINT ptn_pers_cuentas_pkey;
       cuenta            postgres    false    394                       2606    86046 ,   ptn_pers_documentos ptn_pers_documentos_pkey 
   CONSTRAINT     t   ALTER TABLE ONLY cuenta.ptn_pers_documentos
    ADD CONSTRAINT ptn_pers_documentos_pkey PRIMARY KEY (id_documento);
 V   ALTER TABLE ONLY cuenta.ptn_pers_documentos DROP CONSTRAINT ptn_pers_documentos_pkey;
       cuenta            postgres    false    396                       2606    86053 ,   ptn_pers_domicilios ptn_pers_domicilios_pkey 
   CONSTRAINT     t   ALTER TABLE ONLY cuenta.ptn_pers_domicilios
    ADD CONSTRAINT ptn_pers_domicilios_pkey PRIMARY KEY (id_domicilio);
 V   ALTER TABLE ONLY cuenta.ptn_pers_domicilios DROP CONSTRAINT ptn_pers_domicilios_pkey;
       cuenta            postgres    false    398                       2606    86060 *   ptn_pers_est_finan ptn_pers_est_finan_pkey 
   CONSTRAINT     r   ALTER TABLE ONLY cuenta.ptn_pers_est_finan
    ADD CONSTRAINT ptn_pers_est_finan_pkey PRIMARY KEY (id_est_finan);
 T   ALTER TABLE ONLY cuenta.ptn_pers_est_finan DROP CONSTRAINT ptn_pers_est_finan_pkey;
       cuenta            postgres    false    400            !           2606    86067 $   ptn_pers_firmas ptn_pers_firmas_pkey 
   CONSTRAINT     h   ALTER TABLE ONLY cuenta.ptn_pers_firmas
    ADD CONSTRAINT ptn_pers_firmas_pkey PRIMARY KEY (id_firma);
 N   ALTER TABLE ONLY cuenta.ptn_pers_firmas DROP CONSTRAINT ptn_pers_firmas_pkey;
       cuenta            postgres    false    402            %           2606    86074 4   ptn_pers_info_adicional ptn_pers_info_adicional_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional
    ADD CONSTRAINT ptn_pers_info_adicional_pkey PRIMARY KEY (id_info_adicional);
 ^   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional DROP CONSTRAINT ptn_pers_info_adicional_pkey;
       cuenta            postgres    false    404            )           2606    86081 .   ptn_pers_repre_legal ptn_pers_repre_legal_pkey 
   CONSTRAINT     x   ALTER TABLE ONLY cuenta.ptn_pers_repre_legal
    ADD CONSTRAINT ptn_pers_repre_legal_pkey PRIMARY KEY (id_repre_legal);
 X   ALTER TABLE ONLY cuenta.ptn_pers_repre_legal DROP CONSTRAINT ptn_pers_repre_legal_pkey;
       cuenta            postgres    false    406            -           2606    86089    ptn_personas ptn_personas_pkey 
   CONSTRAINT     h   ALTER TABLE ONLY cuenta.ptn_personas
    ADD CONSTRAINT ptn_personas_pkey PRIMARY KEY (identificacion);
 H   ALTER TABLE ONLY cuenta.ptn_personas DROP CONSTRAINT ptn_personas_pkey;
       cuenta            postgres    false    407            3           2606    86101 (   ptn_roles_cuentas ptn_roles_cuentas_pkey 
   CONSTRAINT     o   ALTER TABLE ONLY cuenta.ptn_roles_cuentas
    ADD CONSTRAINT ptn_roles_cuentas_pkey PRIMARY KEY (cuenta, rol);
 R   ALTER TABLE ONLY cuenta.ptn_roles_cuentas DROP CONSTRAINT ptn_roles_cuentas_pkey;
       cuenta            postgres    false    410    410            /           2606    86096    ptn_roles ptn_roles_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY cuenta.ptn_roles
    ADD CONSTRAINT ptn_roles_pkey PRIMARY KEY (id_rol);
 B   ALTER TABLE ONLY cuenta.ptn_roles DROP CONSTRAINT ptn_roles_pkey;
       cuenta            postgres    false    409            5           2606    86108    ptn_token ptn_token_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY cuenta.ptn_token
    ADD CONSTRAINT ptn_token_pkey PRIMARY KEY (id_token);
 B   ALTER TABLE ONLY cuenta.ptn_token DROP CONSTRAINT ptn_token_pkey;
       cuenta            postgres    false    412                       2606    86110 (   ptn_cuentas uk_1bos3t9qu01tcxpa7auqx3guu 
   CONSTRAINT     d   ALTER TABLE ONLY cuenta.ptn_cuentas
    ADD CONSTRAINT uk_1bos3t9qu01tcxpa7auqx3guu UNIQUE (email);
 R   ALTER TABLE ONLY cuenta.ptn_cuentas DROP CONSTRAINT uk_1bos3t9qu01tcxpa7auqx3guu;
       cuenta            postgres    false    387            �           2606    68591 )   mult_cuentas uk_1tm1sw1pvdoml3ddnkc6qhc0k 
   CONSTRAINT     e   ALTER TABLE ONLY cuenta.mult_cuentas
    ADD CONSTRAINT uk_1tm1sw1pvdoml3ddnkc6qhc0k UNIQUE (email);
 S   ALTER TABLE ONLY cuenta.mult_cuentas DROP CONSTRAINT uk_1tm1sw1pvdoml3ddnkc6qhc0k;
       cuenta            postgres    false    225            1           2606    86124 &   ptn_roles uk_281xlt3xin3gwvy8x0evhj0dd 
   CONSTRAINT     c   ALTER TABLE ONLY cuenta.ptn_roles
    ADD CONSTRAINT uk_281xlt3xin3gwvy8x0evhj0dd UNIQUE (nombre);
 P   ALTER TABLE ONLY cuenta.ptn_roles DROP CONSTRAINT uk_281xlt3xin3gwvy8x0evhj0dd;
       cuenta            postgres    false    409                       2606    86116 -   ptn_pers_cuentas uk_827lbqw3iumfk50dcg5e8a91q 
   CONSTRAINT     k   ALTER TABLE ONLY cuenta.ptn_pers_cuentas
    ADD CONSTRAINT uk_827lbqw3iumfk50dcg5e8a91q UNIQUE (persona);
 W   ALTER TABLE ONLY cuenta.ptn_pers_cuentas DROP CONSTRAINT uk_827lbqw3iumfk50dcg5e8a91q;
       cuenta            postgres    false    394            '           2606    86120 4   ptn_pers_info_adicional uk_9ahkwgexmkcduwbvm2a6of5wv 
   CONSTRAINT     u   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional
    ADD CONSTRAINT uk_9ahkwgexmkcduwbvm2a6of5wv UNIQUE (id_persona);
 ^   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional DROP CONSTRAINT uk_9ahkwgexmkcduwbvm2a6of5wv;
       cuenta            postgres    false    404                       2606    86114 %   ptn_menu uk_dhy25ai4dgtxyejhxs3k0cxic 
   CONSTRAINT     b   ALTER TABLE ONLY cuenta.ptn_menu
    ADD CONSTRAINT uk_dhy25ai4dgtxyejhxs3k0cxic UNIQUE (nombre);
 O   ALTER TABLE ONLY cuenta.ptn_menu DROP CONSTRAINT uk_dhy25ai4dgtxyejhxs3k0cxic;
       cuenta            postgres    false    389                       2606    68593 2   mult_pers_repre_legal uk_drmjmva867nbciq97i3g7o990 
   CONSTRAINT     n   ALTER TABLE ONLY cuenta.mult_pers_repre_legal
    ADD CONSTRAINT uk_drmjmva867nbciq97i3g7o990 UNIQUE (email);
 \   ALTER TABLE ONLY cuenta.mult_pers_repre_legal DROP CONSTRAINT uk_drmjmva867nbciq97i3g7o990;
       cuenta            postgres    false    243                       2606    86112 (   ptn_cuentas uk_hhofs7oeadcm3wnl7vby6tkk4 
   CONSTRAINT     f   ALTER TABLE ONLY cuenta.ptn_cuentas
    ADD CONSTRAINT uk_hhofs7oeadcm3wnl7vby6tkk4 UNIQUE (usuario);
 R   ALTER TABLE ONLY cuenta.ptn_cuentas DROP CONSTRAINT uk_hhofs7oeadcm3wnl7vby6tkk4;
       cuenta            postgres    false    387            #           2606    86118 +   ptn_pers_firmas uk_ikm038igjlpdnh7xfpwkdbpa 
   CONSTRAINT     g   ALTER TABLE ONLY cuenta.ptn_pers_firmas
    ADD CONSTRAINT uk_ikm038igjlpdnh7xfpwkdbpa UNIQUE (email);
 U   ALTER TABLE ONLY cuenta.ptn_pers_firmas DROP CONSTRAINT uk_ikm038igjlpdnh7xfpwkdbpa;
       cuenta            postgres    false    402            +           2606    86122 1   ptn_pers_repre_legal uk_kdct77spva2bulu0c37jp4crf 
   CONSTRAINT     m   ALTER TABLE ONLY cuenta.ptn_pers_repre_legal
    ADD CONSTRAINT uk_kdct77spva2bulu0c37jp4crf UNIQUE (email);
 [   ALTER TABLE ONLY cuenta.ptn_pers_repre_legal DROP CONSTRAINT uk_kdct77spva2bulu0c37jp4crf;
       cuenta            postgres    false    406            �           2606    68595 &   mult_menu uk_l5aujhro0heg8qdndps5fex9u 
   CONSTRAINT     c   ALTER TABLE ONLY cuenta.mult_menu
    ADD CONSTRAINT uk_l5aujhro0heg8qdndps5fex9u UNIQUE (nombre);
 P   ALTER TABLE ONLY cuenta.mult_menu DROP CONSTRAINT uk_l5aujhro0heg8qdndps5fex9u;
       cuenta            postgres    false    226                       2606    68597 -   mult_pers_firmas uk_lv4jn8x2h3emx1uorpqon91tf 
   CONSTRAINT     i   ALTER TABLE ONLY cuenta.mult_pers_firmas
    ADD CONSTRAINT uk_lv4jn8x2h3emx1uorpqon91tf UNIQUE (email);
 W   ALTER TABLE ONLY cuenta.mult_pers_firmas DROP CONSTRAINT uk_lv4jn8x2h3emx1uorpqon91tf;
       cuenta            postgres    false    239            �           2606    68599 )   mult_cuentas uk_py7bvarqs7euh6jcmkey58lbj 
   CONSTRAINT     g   ALTER TABLE ONLY cuenta.mult_cuentas
    ADD CONSTRAINT uk_py7bvarqs7euh6jcmkey58lbj UNIQUE (usuario);
 S   ALTER TABLE ONLY cuenta.mult_cuentas DROP CONSTRAINT uk_py7bvarqs7euh6jcmkey58lbj;
       cuenta            postgres    false    225                       2606    68601 '   mult_roles uk_sw2dbf4134dklfxkg65xwjii4 
   CONSTRAINT     d   ALTER TABLE ONLY cuenta.mult_roles
    ADD CONSTRAINT uk_sw2dbf4134dklfxkg65xwjii4 UNIQUE (nombre);
 Q   ALTER TABLE ONLY cuenta.mult_roles DROP CONSTRAINT uk_sw2dbf4134dklfxkg65xwjii4;
       cuenta            postgres    false    246                       2606    68603 &   mult_info_emails mult_info_emails_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY emails.mult_info_emails
    ADD CONSTRAINT mult_info_emails_pkey PRIMARY KEY (id_email);
 P   ALTER TABLE ONLY emails.mult_info_emails DROP CONSTRAINT mult_info_emails_pkey;
       emails            postgres    false    252                        2606    68605 2   mult_plantillas_emails mult_plantillas_emails_pkey 
   CONSTRAINT     z   ALTER TABLE ONLY emails.mult_plantillas_emails
    ADD CONSTRAINT mult_plantillas_emails_pkey PRIMARY KEY (id_plantilla);
 \   ALTER TABLE ONLY emails.mult_plantillas_emails DROP CONSTRAINT mult_plantillas_emails_pkey;
       emails            postgres    false    254            7           2606    86131 $   ptn_info_emails ptn_info_emails_pkey 
   CONSTRAINT     h   ALTER TABLE ONLY emails.ptn_info_emails
    ADD CONSTRAINT ptn_info_emails_pkey PRIMARY KEY (id_email);
 N   ALTER TABLE ONLY emails.ptn_info_emails DROP CONSTRAINT ptn_info_emails_pkey;
       emails            postgres    false    414            ;           2606    86139 0   ptn_plantillas_emails ptn_plantillas_emails_pkey 
   CONSTRAINT     x   ALTER TABLE ONLY emails.ptn_plantillas_emails
    ADD CONSTRAINT ptn_plantillas_emails_pkey PRIMARY KEY (id_plantilla);
 Z   ALTER TABLE ONLY emails.ptn_plantillas_emails DROP CONSTRAINT ptn_plantillas_emails_pkey;
       emails            postgres    false    415            9           2606    86141 ,   ptn_info_emails uk_ckq29lrh1donty8m2hgyap34b 
   CONSTRAINT     h   ALTER TABLE ONLY emails.ptn_info_emails
    ADD CONSTRAINT uk_ckq29lrh1donty8m2hgyap34b UNIQUE (email);
 V   ALTER TABLE ONLY emails.ptn_info_emails DROP CONSTRAINT uk_ckq29lrh1donty8m2hgyap34b;
       emails            postgres    false    414            =           2606    86143 2   ptn_plantillas_emails uk_qpwwsvpv6y34qelyoy7mf4s8x 
   CONSTRAINT     o   ALTER TABLE ONLY emails.ptn_plantillas_emails
    ADD CONSTRAINT uk_qpwwsvpv6y34qelyoy7mf4s8x UNIQUE (nombre);
 \   ALTER TABLE ONLY emails.ptn_plantillas_emails DROP CONSTRAINT uk_qpwwsvpv6y34qelyoy7mf4s8x;
       emails            postgres    false    415            "           2606    68607 (   mult_form_contact mult_form_contact_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY footer.mult_form_contact
    ADD CONSTRAINT mult_form_contact_pkey PRIMARY KEY (id);
 R   ALTER TABLE ONLY footer.mult_form_contact DROP CONSTRAINT mult_form_contact_pkey;
       footer            postgres    false    255            ?           2606    86150 &   ptn_form_contact ptn_form_contact_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY footer.ptn_form_contact
    ADD CONSTRAINT ptn_form_contact_pkey PRIMARY KEY (id);
 P   ALTER TABLE ONLY footer.ptn_form_contact DROP CONSTRAINT ptn_form_contact_pkey;
       footer            postgres    false    417            $           2606    68609 2   mult_bitacora_procesos mult_bitacora_procesos_pkey 
   CONSTRAINT     t   ALTER TABLE ONLY historicas.mult_bitacora_procesos
    ADD CONSTRAINT mult_bitacora_procesos_pkey PRIMARY KEY (id);
 `   ALTER TABLE ONLY historicas.mult_bitacora_procesos DROP CONSTRAINT mult_bitacora_procesos_pkey;
    
   historicas            postgres    false    257            &           2606    68611 *   mult_hist_proyecto mult_hist_proyecto_pkey 
   CONSTRAINT     l   ALTER TABLE ONLY historicas.mult_hist_proyecto
    ADD CONSTRAINT mult_hist_proyecto_pkey PRIMARY KEY (id);
 X   ALTER TABLE ONLY historicas.mult_hist_proyecto DROP CONSTRAINT mult_hist_proyecto_pkey;
    
   historicas            postgres    false    259            (           2606    68613 ,   mult_hist_solicitud mult_hist_solicitud_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY historicas.mult_hist_solicitud
    ADD CONSTRAINT mult_hist_solicitud_pkey PRIMARY KEY (id);
 Z   ALTER TABLE ONLY historicas.mult_hist_solicitud DROP CONSTRAINT mult_hist_solicitud_pkey;
    
   historicas            postgres    false    261            ,           2606    68615 L   mult_historial_conciliacion_detalle mult_historial_conciliacion_detalle_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY historicas.mult_historial_conciliacion_detalle
    ADD CONSTRAINT mult_historial_conciliacion_detalle_pkey PRIMARY KEY (id);
 z   ALTER TABLE ONLY historicas.mult_historial_conciliacion_detalle DROP CONSTRAINT mult_historial_conciliacion_detalle_pkey;
    
   historicas            postgres    false    264            *           2606    68617 <   mult_historial_conciliacion mult_historial_conciliacion_pkey 
   CONSTRAINT     ~   ALTER TABLE ONLY historicas.mult_historial_conciliacion
    ADD CONSTRAINT mult_historial_conciliacion_pkey PRIMARY KEY (id);
 j   ALTER TABLE ONLY historicas.mult_historial_conciliacion DROP CONSTRAINT mult_historial_conciliacion_pkey;
    
   historicas            postgres    false    263            /           2606    68619 6   mult_sol_x_identifiacion mult_sol_x_identifiacion_pkey 
   CONSTRAINT     x   ALTER TABLE ONLY historicas.mult_sol_x_identifiacion
    ADD CONSTRAINT mult_sol_x_identifiacion_pkey PRIMARY KEY (id);
 d   ALTER TABLE ONLY historicas.mult_sol_x_identifiacion DROP CONSTRAINT mult_sol_x_identifiacion_pkey;
    
   historicas            postgres    false    267            A           2606    86157 0   ptn_bitacora_procesos ptn_bitacora_procesos_pkey 
   CONSTRAINT     r   ALTER TABLE ONLY historicas.ptn_bitacora_procesos
    ADD CONSTRAINT ptn_bitacora_procesos_pkey PRIMARY KEY (id);
 ^   ALTER TABLE ONLY historicas.ptn_bitacora_procesos DROP CONSTRAINT ptn_bitacora_procesos_pkey;
    
   historicas            postgres    false    419            C           2606    86167 (   ptn_hist_proyecto ptn_hist_proyecto_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY historicas.ptn_hist_proyecto
    ADD CONSTRAINT ptn_hist_proyecto_pkey PRIMARY KEY (id);
 V   ALTER TABLE ONLY historicas.ptn_hist_proyecto DROP CONSTRAINT ptn_hist_proyecto_pkey;
    
   historicas            postgres    false    421            E           2606    86177 *   ptn_hist_solicitud ptn_hist_solicitud_pkey 
   CONSTRAINT     l   ALTER TABLE ONLY historicas.ptn_hist_solicitud
    ADD CONSTRAINT ptn_hist_solicitud_pkey PRIMARY KEY (id);
 X   ALTER TABLE ONLY historicas.ptn_hist_solicitud DROP CONSTRAINT ptn_hist_solicitud_pkey;
    
   historicas            postgres    false    423            I           2606    86191 J   ptn_historial_conciliacion_detalle ptn_historial_conciliacion_detalle_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY historicas.ptn_historial_conciliacion_detalle
    ADD CONSTRAINT ptn_historial_conciliacion_detalle_pkey PRIMARY KEY (id);
 x   ALTER TABLE ONLY historicas.ptn_historial_conciliacion_detalle DROP CONSTRAINT ptn_historial_conciliacion_detalle_pkey;
    
   historicas            postgres    false    427            G           2606    86184 :   ptn_historial_conciliacion ptn_historial_conciliacion_pkey 
   CONSTRAINT     |   ALTER TABLE ONLY historicas.ptn_historial_conciliacion
    ADD CONSTRAINT ptn_historial_conciliacion_pkey PRIMARY KEY (id);
 h   ALTER TABLE ONLY historicas.ptn_historial_conciliacion DROP CONSTRAINT ptn_historial_conciliacion_pkey;
    
   historicas            postgres    false    425            K           2606    86201 4   ptn_sol_x_identifiacion ptn_sol_x_identifiacion_pkey 
   CONSTRAINT     v   ALTER TABLE ONLY historicas.ptn_sol_x_identifiacion
    ADD CONSTRAINT ptn_sol_x_identifiacion_pkey PRIMARY KEY (id);
 b   ALTER TABLE ONLY historicas.ptn_sol_x_identifiacion DROP CONSTRAINT ptn_sol_x_identifiacion_pkey;
    
   historicas            postgres    false    429            1           2606    68621 .   mult_datos_inversion mult_datos_inversion_pkey 
   CONSTRAINT     t   ALTER TABLE ONLY inversion.mult_datos_inversion
    ADD CONSTRAINT mult_datos_inversion_pkey PRIMARY KEY (id_dato);
 [   ALTER TABLE ONLY inversion.mult_datos_inversion DROP CONSTRAINT mult_datos_inversion_pkey;
    	   inversion            postgres    false    269            4           2606    68623 8   mult_detalle_amortizacion mult_detalle_amortizacion_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_detalle_amortizacion
    ADD CONSTRAINT mult_detalle_amortizacion_pkey PRIMARY KEY (id_det_amortizacion);
 e   ALTER TABLE ONLY inversion.mult_detalle_amortizacion DROP CONSTRAINT mult_detalle_amortizacion_pkey;
    	   inversion            postgres    false    271            7           2606    68625 *   mult_doc_aceptados mult_doc_aceptados_pkey 
   CONSTRAINT     x   ALTER TABLE ONLY inversion.mult_doc_aceptados
    ADD CONSTRAINT mult_doc_aceptados_pkey PRIMARY KEY (id_doc_aceptado);
 W   ALTER TABLE ONLY inversion.mult_doc_aceptados DROP CONSTRAINT mult_doc_aceptados_pkey;
    	   inversion            postgres    false    273            D           2606    68627 <   mult_solicitudes_documentos mult_solicitudes_documentos_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_solicitudes_documentos
    ADD CONSTRAINT mult_solicitudes_documentos_pkey PRIMARY KEY (id_sol_documentos);
 i   ALTER TABLE ONLY inversion.mult_solicitudes_documentos DROP CONSTRAINT mult_solicitudes_documentos_pkey;
    	   inversion            postgres    false    276            @           2606    68629 &   mult_solicitudes mult_solicitudes_pkey 
   CONSTRAINT     u   ALTER TABLE ONLY inversion.mult_solicitudes
    ADD CONSTRAINT mult_solicitudes_pkey PRIMARY KEY (numero_solicitud);
 S   ALTER TABLE ONLY inversion.mult_solicitudes DROP CONSTRAINT mult_solicitudes_pkey;
    	   inversion            postgres    false    275            F           2606    68631 4   mult_tabla_amortizacion mult_tabla_amortizacion_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_tabla_amortizacion
    ADD CONSTRAINT mult_tabla_amortizacion_pkey PRIMARY KEY (id_tbl_amortizacion);
 a   ALTER TABLE ONLY inversion.mult_tabla_amortizacion DROP CONSTRAINT mult_tabla_amortizacion_pkey;
    	   inversion            postgres    false    280            H           2606    68633 (   mult_tipo_estados mult_tipo_estados_pkey 
   CONSTRAINT     p   ALTER TABLE ONLY inversion.mult_tipo_estados
    ADD CONSTRAINT mult_tipo_estados_pkey PRIMARY KEY (id_estado);
 U   ALTER TABLE ONLY inversion.mult_tipo_estados DROP CONSTRAINT mult_tipo_estados_pkey;
    	   inversion            postgres    false    282            P           2606    68635 *   mult_transacciones mult_transacciones_pkey 
   CONSTRAINT     {   ALTER TABLE ONLY inversion.mult_transacciones
    ADD CONSTRAINT mult_transacciones_pkey PRIMARY KEY (id_doc_transaccion);
 W   ALTER TABLE ONLY inversion.mult_transacciones DROP CONSTRAINT mult_transacciones_pkey;
    	   inversion            postgres    false    283            M           2606    86208 ,   ptn_datos_inversion ptn_datos_inversion_pkey 
   CONSTRAINT     r   ALTER TABLE ONLY inversion.ptn_datos_inversion
    ADD CONSTRAINT ptn_datos_inversion_pkey PRIMARY KEY (id_dato);
 Y   ALTER TABLE ONLY inversion.ptn_datos_inversion DROP CONSTRAINT ptn_datos_inversion_pkey;
    	   inversion            postgres    false    431            O           2606    86215 6   ptn_detalle_amortizacion ptn_detalle_amortizacion_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_detalle_amortizacion
    ADD CONSTRAINT ptn_detalle_amortizacion_pkey PRIMARY KEY (id_det_amortizacion);
 c   ALTER TABLE ONLY inversion.ptn_detalle_amortizacion DROP CONSTRAINT ptn_detalle_amortizacion_pkey;
    	   inversion            postgres    false    433            Q           2606    86222 (   ptn_doc_aceptados ptn_doc_aceptados_pkey 
   CONSTRAINT     v   ALTER TABLE ONLY inversion.ptn_doc_aceptados
    ADD CONSTRAINT ptn_doc_aceptados_pkey PRIMARY KEY (id_doc_aceptado);
 U   ALTER TABLE ONLY inversion.ptn_doc_aceptados DROP CONSTRAINT ptn_doc_aceptados_pkey;
    	   inversion            postgres    false    435            U           2606    86239 :   ptn_solicitudes_documentos ptn_solicitudes_documentos_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_solicitudes_documentos
    ADD CONSTRAINT ptn_solicitudes_documentos_pkey PRIMARY KEY (id_sol_documentos);
 g   ALTER TABLE ONLY inversion.ptn_solicitudes_documentos DROP CONSTRAINT ptn_solicitudes_documentos_pkey;
    	   inversion            postgres    false    439            S           2606    86229 $   ptn_solicitudes ptn_solicitudes_pkey 
   CONSTRAINT     s   ALTER TABLE ONLY inversion.ptn_solicitudes
    ADD CONSTRAINT ptn_solicitudes_pkey PRIMARY KEY (numero_solicitud);
 Q   ALTER TABLE ONLY inversion.ptn_solicitudes DROP CONSTRAINT ptn_solicitudes_pkey;
    	   inversion            postgres    false    437            W           2606    86246 2   ptn_tabla_amortizacion ptn_tabla_amortizacion_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_tabla_amortizacion
    ADD CONSTRAINT ptn_tabla_amortizacion_pkey PRIMARY KEY (id_tbl_amortizacion);
 _   ALTER TABLE ONLY inversion.ptn_tabla_amortizacion DROP CONSTRAINT ptn_tabla_amortizacion_pkey;
    	   inversion            postgres    false    441            Y           2606    86251 &   ptn_tipo_estados ptn_tipo_estados_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY inversion.ptn_tipo_estados
    ADD CONSTRAINT ptn_tipo_estados_pkey PRIMARY KEY (id_estado);
 S   ALTER TABLE ONLY inversion.ptn_tipo_estados DROP CONSTRAINT ptn_tipo_estados_pkey;
    	   inversion            postgres    false    442            ]           2606    86261 (   ptn_transacciones ptn_transacciones_pkey 
   CONSTRAINT     y   ALTER TABLE ONLY inversion.ptn_transacciones
    ADD CONSTRAINT ptn_transacciones_pkey PRIMARY KEY (id_doc_transaccion);
 U   ALTER TABLE ONLY inversion.ptn_transacciones DROP CONSTRAINT ptn_transacciones_pkey;
    	   inversion            postgres    false    444            [           2606    86263 -   ptn_tipo_estados uk_5yrk1bfousj09xb43b51rtu1l 
   CONSTRAINT     r   ALTER TABLE ONLY inversion.ptn_tipo_estados
    ADD CONSTRAINT uk_5yrk1bfousj09xb43b51rtu1l UNIQUE (descripcion);
 Z   ALTER TABLE ONLY inversion.ptn_tipo_estados DROP CONSTRAINT uk_5yrk1bfousj09xb43b51rtu1l;
    	   inversion            postgres    false    442            J           2606    68637 .   mult_tipo_estados uk_i56l87aocarj64hoh6wuhxbmt 
   CONSTRAINT     s   ALTER TABLE ONLY inversion.mult_tipo_estados
    ADD CONSTRAINT uk_i56l87aocarj64hoh6wuhxbmt UNIQUE (descripcion);
 [   ALTER TABLE ONLY inversion.mult_tipo_estados DROP CONSTRAINT uk_i56l87aocarj64hoh6wuhxbmt;
    	   inversion            postgres    false    282            R           2606    68639    mult_bancos mult_bancos_pkey 
   CONSTRAINT     b   ALTER TABLE ONLY maestras.mult_bancos
    ADD CONSTRAINT mult_bancos_pkey PRIMARY KEY (id_banco);
 H   ALTER TABLE ONLY maestras.mult_bancos DROP CONSTRAINT mult_bancos_pkey;
       maestras            postgres    false    285            T           2606    68641     mult_ciudades mult_ciudades_pkey 
   CONSTRAINT     g   ALTER TABLE ONLY maestras.mult_ciudades
    ADD CONSTRAINT mult_ciudades_pkey PRIMARY KEY (id_ciudad);
 L   ALTER TABLE ONLY maestras.mult_ciudades DROP CONSTRAINT mult_ciudades_pkey;
       maestras            postgres    false    287            V           2606    68643 $   mult_forma_pago mult_forma_pago_pkey 
   CONSTRAINT     o   ALTER TABLE ONLY maestras.mult_forma_pago
    ADD CONSTRAINT mult_forma_pago_pkey PRIMARY KEY (id_forma_pago);
 P   ALTER TABLE ONLY maestras.mult_forma_pago DROP CONSTRAINT mult_forma_pago_pkey;
       maestras            postgres    false    289            \           2606    68645 ,   mult_nacionalidades mult_nacionalidades_pkey 
   CONSTRAINT     y   ALTER TABLE ONLY maestras.mult_nacionalidades
    ADD CONSTRAINT mult_nacionalidades_pkey PRIMARY KEY (id_nacionalidad);
 X   ALTER TABLE ONLY maestras.mult_nacionalidades DROP CONSTRAINT mult_nacionalidades_pkey;
       maestras            postgres    false    291            ^           2606    68647 $   mult_rango_pago mult_rango_pago_pkey 
   CONSTRAINT     d   ALTER TABLE ONLY maestras.mult_rango_pago
    ADD CONSTRAINT mult_rango_pago_pkey PRIMARY KEY (id);
 P   ALTER TABLE ONLY maestras.mult_rango_pago DROP CONSTRAINT mult_rango_pago_pkey;
       maestras            postgres    false    293            `           2606    68649 .   mult_tipo_documentos mult_tipo_documentos_pkey 
   CONSTRAINT     }   ALTER TABLE ONLY maestras.mult_tipo_documentos
    ADD CONSTRAINT mult_tipo_documentos_pkey PRIMARY KEY (id_tipo_documento);
 Z   ALTER TABLE ONLY maestras.mult_tipo_documentos DROP CONSTRAINT mult_tipo_documentos_pkey;
       maestras            postgres    false    295            b           2606    68651 ,   mult_tipo_solicitud mult_tipo_solicitud_pkey 
   CONSTRAINT     l   ALTER TABLE ONLY maestras.mult_tipo_solicitud
    ADD CONSTRAINT mult_tipo_solicitud_pkey PRIMARY KEY (id);
 X   ALTER TABLE ONLY maestras.mult_tipo_solicitud DROP CONSTRAINT mult_tipo_solicitud_pkey;
       maestras            postgres    false    297            f           2606    68653 &   mult_tipo_tablas mult_tipo_tablas_pkey 
   CONSTRAINT     q   ALTER TABLE ONLY maestras.mult_tipo_tablas
    ADD CONSTRAINT mult_tipo_tablas_pkey PRIMARY KEY (id_tipo_tabla);
 R   ALTER TABLE ONLY maestras.mult_tipo_tablas DROP CONSTRAINT mult_tipo_tablas_pkey;
       maestras            postgres    false    299            j           2606    68655 4   mult_tipo_transacciones mult_tipo_transacciones_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY maestras.mult_tipo_transacciones
    ADD CONSTRAINT mult_tipo_transacciones_pkey PRIMARY KEY (id_tipo_transaccion);
 `   ALTER TABLE ONLY maestras.mult_tipo_transacciones DROP CONSTRAINT mult_tipo_transacciones_pkey;
       maestras            postgres    false    301            _           2606    86270    ptn_bancos ptn_bancos_pkey 
   CONSTRAINT     `   ALTER TABLE ONLY maestras.ptn_bancos
    ADD CONSTRAINT ptn_bancos_pkey PRIMARY KEY (id_banco);
 F   ALTER TABLE ONLY maestras.ptn_bancos DROP CONSTRAINT ptn_bancos_pkey;
       maestras            postgres    false    446            a           2606    86277    ptn_ciudades ptn_ciudades_pkey 
   CONSTRAINT     e   ALTER TABLE ONLY maestras.ptn_ciudades
    ADD CONSTRAINT ptn_ciudades_pkey PRIMARY KEY (id_ciudad);
 J   ALTER TABLE ONLY maestras.ptn_ciudades DROP CONSTRAINT ptn_ciudades_pkey;
       maestras            postgres    false    448            c           2606    86284 "   ptn_forma_pago ptn_forma_pago_pkey 
   CONSTRAINT     m   ALTER TABLE ONLY maestras.ptn_forma_pago
    ADD CONSTRAINT ptn_forma_pago_pkey PRIMARY KEY (id_forma_pago);
 N   ALTER TABLE ONLY maestras.ptn_forma_pago DROP CONSTRAINT ptn_forma_pago_pkey;
       maestras            postgres    false    450            g           2606    86291 *   ptn_nacionalidades ptn_nacionalidades_pkey 
   CONSTRAINT     w   ALTER TABLE ONLY maestras.ptn_nacionalidades
    ADD CONSTRAINT ptn_nacionalidades_pkey PRIMARY KEY (id_nacionalidad);
 V   ALTER TABLE ONLY maestras.ptn_nacionalidades DROP CONSTRAINT ptn_nacionalidades_pkey;
       maestras            postgres    false    452            j           2606    86298 "   ptn_rango_pago ptn_rango_pago_pkey 
   CONSTRAINT     b   ALTER TABLE ONLY maestras.ptn_rango_pago
    ADD CONSTRAINT ptn_rango_pago_pkey PRIMARY KEY (id);
 N   ALTER TABLE ONLY maestras.ptn_rango_pago DROP CONSTRAINT ptn_rango_pago_pkey;
       maestras            postgres    false    454            l           2606    86305 ,   ptn_tipo_documentos ptn_tipo_documentos_pkey 
   CONSTRAINT     {   ALTER TABLE ONLY maestras.ptn_tipo_documentos
    ADD CONSTRAINT ptn_tipo_documentos_pkey PRIMARY KEY (id_tipo_documento);
 X   ALTER TABLE ONLY maestras.ptn_tipo_documentos DROP CONSTRAINT ptn_tipo_documentos_pkey;
       maestras            postgres    false    456            n           2606    86312 *   ptn_tipo_solicitud ptn_tipo_solicitud_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY maestras.ptn_tipo_solicitud
    ADD CONSTRAINT ptn_tipo_solicitud_pkey PRIMARY KEY (id);
 V   ALTER TABLE ONLY maestras.ptn_tipo_solicitud DROP CONSTRAINT ptn_tipo_solicitud_pkey;
       maestras            postgres    false    458            r           2606    86319 $   ptn_tipo_tablas ptn_tipo_tablas_pkey 
   CONSTRAINT     o   ALTER TABLE ONLY maestras.ptn_tipo_tablas
    ADD CONSTRAINT ptn_tipo_tablas_pkey PRIMARY KEY (id_tipo_tabla);
 P   ALTER TABLE ONLY maestras.ptn_tipo_tablas DROP CONSTRAINT ptn_tipo_tablas_pkey;
       maestras            postgres    false    460            d           2606    68657 0   mult_tipo_solicitud uk_367c5v5i9n816yq47rf3d99vo 
   CONSTRAINT     t   ALTER TABLE ONLY maestras.mult_tipo_solicitud
    ADD CONSTRAINT uk_367c5v5i9n816yq47rf3d99vo UNIQUE (descripcion);
 \   ALTER TABLE ONLY maestras.mult_tipo_solicitud DROP CONSTRAINT uk_367c5v5i9n816yq47rf3d99vo;
       maestras            postgres    false    297            t           2606    86326 ,   ptn_tipo_tablas uk_4x5qbwg6tm00hscor0d57u865 
   CONSTRAINT     k   ALTER TABLE ONLY maestras.ptn_tipo_tablas
    ADD CONSTRAINT uk_4x5qbwg6tm00hscor0d57u865 UNIQUE (nombre);
 X   ALTER TABLE ONLY maestras.ptn_tipo_tablas DROP CONSTRAINT uk_4x5qbwg6tm00hscor0d57u865;
       maestras            postgres    false    460            p           2606    86324 -   ptn_tipo_solicitud uk_58wueo2mxbrsereubxr7rfx 
   CONSTRAINT     q   ALTER TABLE ONLY maestras.ptn_tipo_solicitud
    ADD CONSTRAINT uk_58wueo2mxbrsereubxr7rfx UNIQUE (descripcion);
 Y   ALTER TABLE ONLY maestras.ptn_tipo_solicitud DROP CONSTRAINT uk_58wueo2mxbrsereubxr7rfx;
       maestras            postgres    false    458            e           2606    86321 +   ptn_forma_pago uk_6hjwbmhoendctlavamk746jy4 
   CONSTRAINT     o   ALTER TABLE ONLY maestras.ptn_forma_pago
    ADD CONSTRAINT uk_6hjwbmhoendctlavamk746jy4 UNIQUE (descripcion);
 W   ALTER TABLE ONLY maestras.ptn_forma_pago DROP CONSTRAINT uk_6hjwbmhoendctlavamk746jy4;
       maestras            postgres    false    450            l           2606    68659 3   mult_tipo_transacciones uk_gii3v7o9s9k7lskcp5aiuuqc 
   CONSTRAINT     w   ALTER TABLE ONLY maestras.mult_tipo_transacciones
    ADD CONSTRAINT uk_gii3v7o9s9k7lskcp5aiuuqc UNIQUE (descripcion);
 _   ALTER TABLE ONLY maestras.mult_tipo_transacciones DROP CONSTRAINT uk_gii3v7o9s9k7lskcp5aiuuqc;
       maestras            postgres    false    301            X           2606    68661 ,   mult_forma_pago uk_hcjsl7mmknk2b8d6o7orimwog 
   CONSTRAINT     p   ALTER TABLE ONLY maestras.mult_forma_pago
    ADD CONSTRAINT uk_hcjsl7mmknk2b8d6o7orimwog UNIQUE (descripcion);
 X   ALTER TABLE ONLY maestras.mult_forma_pago DROP CONSTRAINT uk_hcjsl7mmknk2b8d6o7orimwog;
       maestras            postgres    false    289            h           2606    68663 -   mult_tipo_tablas uk_lypxo4a5n4mt0pjkgq61ki3ui 
   CONSTRAINT     l   ALTER TABLE ONLY maestras.mult_tipo_tablas
    ADD CONSTRAINT uk_lypxo4a5n4mt0pjkgq61ki3ui UNIQUE (nombre);
 Y   ALTER TABLE ONLY maestras.mult_tipo_tablas DROP CONSTRAINT uk_lypxo4a5n4mt0pjkgq61ki3ui;
       maestras            postgres    false    299            n           2606    68665 2   mult_deport_categorias mult_deport_categorias_pkey 
   CONSTRAINT     |   ALTER TABLE ONLY maestras_auspicios.mult_deport_categorias
    ADD CONSTRAINT mult_deport_categorias_pkey PRIMARY KEY (id);
 h   ALTER TABLE ONLY maestras_auspicios.mult_deport_categorias DROP CONSTRAINT mult_deport_categorias_pkey;
       maestras_auspicios            postgres    false    303            p           2606    68667 2   mult_deport_disciplina mult_deport_disciplina_pkey 
   CONSTRAINT     |   ALTER TABLE ONLY maestras_auspicios.mult_deport_disciplina
    ADD CONSTRAINT mult_deport_disciplina_pkey PRIMARY KEY (id);
 h   ALTER TABLE ONLY maestras_auspicios.mult_deport_disciplina DROP CONSTRAINT mult_deport_disciplina_pkey;
       maestras_auspicios            postgres    false    305            r           2606    68669 0   mult_deport_modalidad mult_deport_modalidad_pkey 
   CONSTRAINT     z   ALTER TABLE ONLY maestras_auspicios.mult_deport_modalidad
    ADD CONSTRAINT mult_deport_modalidad_pkey PRIMARY KEY (id);
 f   ALTER TABLE ONLY maestras_auspicios.mult_deport_modalidad DROP CONSTRAINT mult_deport_modalidad_pkey;
       maestras_auspicios            postgres    false    307            v           2606    86333 0   ptn_deport_categorias ptn_deport_categorias_pkey 
   CONSTRAINT     z   ALTER TABLE ONLY maestras_auspicios.ptn_deport_categorias
    ADD CONSTRAINT ptn_deport_categorias_pkey PRIMARY KEY (id);
 f   ALTER TABLE ONLY maestras_auspicios.ptn_deport_categorias DROP CONSTRAINT ptn_deport_categorias_pkey;
       maestras_auspicios            postgres    false    462            x           2606    86340 0   ptn_deport_disciplina ptn_deport_disciplina_pkey 
   CONSTRAINT     z   ALTER TABLE ONLY maestras_auspicios.ptn_deport_disciplina
    ADD CONSTRAINT ptn_deport_disciplina_pkey PRIMARY KEY (id);
 f   ALTER TABLE ONLY maestras_auspicios.ptn_deport_disciplina DROP CONSTRAINT ptn_deport_disciplina_pkey;
       maestras_auspicios            postgres    false    464            z           2606    86347 .   ptn_deport_modalidad ptn_deport_modalidad_pkey 
   CONSTRAINT     x   ALTER TABLE ONLY maestras_auspicios.ptn_deport_modalidad
    ADD CONSTRAINT ptn_deport_modalidad_pkey PRIMARY KEY (id);
 d   ALTER TABLE ONLY maestras_auspicios.ptn_deport_modalidad DROP CONSTRAINT ptn_deport_modalidad_pkey;
       maestras_auspicios            postgres    false    466            �           2606    68671 0   mult_personal_interno cuenta_interna_unique_pers 
   CONSTRAINT     w   ALTER TABLE ONLY multiplo.mult_personal_interno
    ADD CONSTRAINT cuenta_interna_unique_pers UNIQUE (cuenta_interna);
 \   ALTER TABLE ONLY multiplo.mult_personal_interno DROP CONSTRAINT cuenta_interna_unique_pers;
       multiplo            postgres    false    315            v           2606    68673 0   mult_cuentas_internas mult_cuentas_internas_pkey 
   CONSTRAINT     w   ALTER TABLE ONLY multiplo.mult_cuentas_internas
    ADD CONSTRAINT mult_cuentas_internas_pkey PRIMARY KEY (id_cuenta);
 \   ALTER TABLE ONLY multiplo.mult_cuentas_internas DROP CONSTRAINT mult_cuentas_internas_pkey;
       multiplo            postgres    false    309            |           2606    68675     mult_menu_int mult_menu_int_pkey 
   CONSTRAINT     e   ALTER TABLE ONLY multiplo.mult_menu_int
    ADD CONSTRAINT mult_menu_int_pkey PRIMARY KEY (id_menu);
 L   ALTER TABLE ONLY multiplo.mult_menu_int DROP CONSTRAINT mult_menu_int_pkey;
       multiplo            postgres    false    310            �           2606    68677 4   mult_menu_operacion_int mult_menu_operacion_int_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY multiplo.mult_menu_operacion_int
    ADD CONSTRAINT mult_menu_operacion_int_pkey PRIMARY KEY (id_menu, id_operacion, id_rol_int);
 `   ALTER TABLE ONLY multiplo.mult_menu_operacion_int DROP CONSTRAINT mult_menu_operacion_int_pkey;
       multiplo            postgres    false    312    312    312            �           2606    68679 .   mult_operaciones_int mult_operaciones_int_pkey 
   CONSTRAINT     x   ALTER TABLE ONLY multiplo.mult_operaciones_int
    ADD CONSTRAINT mult_operaciones_int_pkey PRIMARY KEY (id_operacion);
 Z   ALTER TABLE ONLY multiplo.mult_operaciones_int DROP CONSTRAINT mult_operaciones_int_pkey;
       multiplo            postgres    false    313            �           2606    68681 0   mult_personal_interno mult_personal_interno_pkey 
   CONSTRAINT     }   ALTER TABLE ONLY multiplo.mult_personal_interno
    ADD CONSTRAINT mult_personal_interno_pkey PRIMARY KEY (id_pers_interno);
 \   ALTER TABLE ONLY multiplo.mult_personal_interno DROP CONSTRAINT mult_personal_interno_pkey;
       multiplo            postgres    false    315            �           2606    68683 <   mult_roles_cuentas_internas mult_roles_cuentas_internas_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY multiplo.mult_roles_cuentas_internas
    ADD CONSTRAINT mult_roles_cuentas_internas_pkey PRIMARY KEY (cuenta_interna, rol);
 h   ALTER TABLE ONLY multiplo.mult_roles_cuentas_internas DROP CONSTRAINT mult_roles_cuentas_internas_pkey;
       multiplo            postgres    false    316    316            �           2606    68685 "   mult_roles_int mult_roles_int_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY multiplo.mult_roles_int
    ADD CONSTRAINT mult_roles_int_pkey PRIMARY KEY (id_rol);
 N   ALTER TABLE ONLY multiplo.mult_roles_int DROP CONSTRAINT mult_roles_int_pkey;
       multiplo            postgres    false    317            x           2606    68687 4   mult_cuentas_internas persona_internca_unique_cuenta 
   CONSTRAINT     w   ALTER TABLE ONLY multiplo.mult_cuentas_internas
    ADD CONSTRAINT persona_internca_unique_cuenta UNIQUE (id_persona);
 `   ALTER TABLE ONLY multiplo.mult_cuentas_internas DROP CONSTRAINT persona_internca_unique_cuenta;
       multiplo            postgres    false    309            z           2606    68689 2   mult_cuentas_internas uk_amilgkfpjdroqwk01iah9ob64 
   CONSTRAINT     p   ALTER TABLE ONLY multiplo.mult_cuentas_internas
    ADD CONSTRAINT uk_amilgkfpjdroqwk01iah9ob64 UNIQUE (email);
 ^   ALTER TABLE ONLY multiplo.mult_cuentas_internas DROP CONSTRAINT uk_amilgkfpjdroqwk01iah9ob64;
       multiplo            postgres    false    309            ~           2606    68691 *   mult_menu_int uk_jatuhdi2812w96v31sy9v7o6i 
   CONSTRAINT     i   ALTER TABLE ONLY multiplo.mult_menu_int
    ADD CONSTRAINT uk_jatuhdi2812w96v31sy9v7o6i UNIQUE (nombre);
 V   ALTER TABLE ONLY multiplo.mult_menu_int DROP CONSTRAINT uk_jatuhdi2812w96v31sy9v7o6i;
       multiplo            postgres    false    310            �           2606    68693 +   mult_roles_int uk_o0few3oc4qomqh6q63cuqfo9b 
   CONSTRAINT     j   ALTER TABLE ONLY multiplo.mult_roles_int
    ADD CONSTRAINT uk_o0few3oc4qomqh6q63cuqfo9b UNIQUE (nombre);
 W   ALTER TABLE ONLY multiplo.mult_roles_int DROP CONSTRAINT uk_o0few3oc4qomqh6q63cuqfo9b;
       multiplo            postgres    false    317            �           2606    68695 6   mult_documentos_facturas mult_documentos_facturas_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY multiplo_documentos.mult_documentos_facturas
    ADD CONSTRAINT mult_documentos_facturas_pkey PRIMARY KEY (id);
 m   ALTER TABLE ONLY multiplo_documentos.mult_documentos_facturas DROP CONSTRAINT mult_documentos_facturas_pkey;
       multiplo_documentos            postgres    false    320            �           2606    68697 $   mult_documentos mult_documentos_pkey 
   CONSTRAINT     y   ALTER TABLE ONLY multiplo_documentos.mult_documentos
    ADD CONSTRAINT mult_documentos_pkey PRIMARY KEY (id_documento);
 [   ALTER TABLE ONLY multiplo_documentos.mult_documentos DROP CONSTRAINT mult_documentos_pkey;
       multiplo_documentos            postgres    false    319            �           2606    68699 :   mult_plantillas_documentos mult_plantillas_documentos_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY multiplo_documentos.mult_plantillas_documentos
    ADD CONSTRAINT mult_plantillas_documentos_pkey PRIMARY KEY (id_plantilla);
 q   ALTER TABLE ONLY multiplo_documentos.mult_plantillas_documentos DROP CONSTRAINT mult_plantillas_documentos_pkey;
       multiplo_documentos            postgres    false    323            �           2606    68701 5   mult_documentos_facturas uk_5g0u7wki3bn6xs27omorjdp72 
   CONSTRAINT     �   ALTER TABLE ONLY multiplo_documentos.mult_documentos_facturas
    ADD CONSTRAINT uk_5g0u7wki3bn6xs27omorjdp72 UNIQUE (codigo_factura);
 l   ALTER TABLE ONLY multiplo_documentos.mult_documentos_facturas DROP CONSTRAINT uk_5g0u7wki3bn6xs27omorjdp72;
       multiplo_documentos            postgres    false    320            �           2606    68703 7   mult_plantillas_documentos uk_kdkvrqj4yfq57fidbjcvph3n9 
   CONSTRAINT     �   ALTER TABLE ONLY multiplo_documentos.mult_plantillas_documentos
    ADD CONSTRAINT uk_kdkvrqj4yfq57fidbjcvph3n9 UNIQUE (nombre);
 n   ALTER TABLE ONLY multiplo_documentos.mult_plantillas_documentos DROP CONSTRAINT uk_kdkvrqj4yfq57fidbjcvph3n9;
       multiplo_documentos            postgres    false    323            �           2606    68705 5   mult_documentos_facturas uk_ny4gmatgc7k9jget1dbvwja9u 
   CONSTRAINT     �   ALTER TABLE ONLY multiplo_documentos.mult_documentos_facturas
    ADD CONSTRAINT uk_ny4gmatgc7k9jget1dbvwja9u UNIQUE (numero_documento);
 l   ALTER TABLE ONLY multiplo_documentos.mult_documentos_facturas DROP CONSTRAINT uk_ny4gmatgc7k9jget1dbvwja9u;
       multiplo_documentos            postgres    false    320            �           2606    68707 J   mult_conciliacion_aprobada_detalle mult_conciliacion_aprobada_detalle_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY negocio.mult_conciliacion_aprobada_detalle
    ADD CONSTRAINT mult_conciliacion_aprobada_detalle_pkey PRIMARY KEY (id);
 u   ALTER TABLE ONLY negocio.mult_conciliacion_aprobada_detalle DROP CONSTRAINT mult_conciliacion_aprobada_detalle_pkey;
       negocio            postgres    false    325            �           2606    68709 :   mult_conciliacion_aprobada mult_conciliacion_aprobada_pkey 
   CONSTRAINT     y   ALTER TABLE ONLY negocio.mult_conciliacion_aprobada
    ADD CONSTRAINT mult_conciliacion_aprobada_pkey PRIMARY KEY (id);
 e   ALTER TABLE ONLY negocio.mult_conciliacion_aprobada DROP CONSTRAINT mult_conciliacion_aprobada_pkey;
       negocio            postgres    false    324            �           2606    68711 @   mult_conciliacion_detalle_xls mult_conciliacion_detalle_xls_pkey 
   CONSTRAINT        ALTER TABLE ONLY negocio.mult_conciliacion_detalle_xls
    ADD CONSTRAINT mult_conciliacion_detalle_xls_pkey PRIMARY KEY (id);
 k   ALTER TABLE ONLY negocio.mult_conciliacion_detalle_xls DROP CONSTRAINT mult_conciliacion_detalle_xls_pkey;
       negocio            postgres    false    328            �           2606    68713 0   mult_conciliacion_xls mult_conciliacion_xls_pkey 
   CONSTRAINT     o   ALTER TABLE ONLY negocio.mult_conciliacion_xls
    ADD CONSTRAINT mult_conciliacion_xls_pkey PRIMARY KEY (id);
 [   ALTER TABLE ONLY negocio.mult_conciliacion_xls DROP CONSTRAINT mult_conciliacion_xls_pkey;
       negocio            postgres    false    330            �           2606    68715 6   mult_fecha_gen_tbl_amort mult_fecha_gen_tbl_amort_pkey 
   CONSTRAINT     u   ALTER TABLE ONLY negocio.mult_fecha_gen_tbl_amort
    ADD CONSTRAINT mult_fecha_gen_tbl_amort_pkey PRIMARY KEY (id);
 a   ALTER TABLE ONLY negocio.mult_fecha_gen_tbl_amort DROP CONSTRAINT mult_fecha_gen_tbl_amort_pkey;
       negocio            postgres    false    332            �           2606    68717 B   mult_porc_interes_tbl_proyecto mult_porc_interes_tbl_proyecto_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY negocio.mult_porc_interes_tbl_proyecto
    ADD CONSTRAINT mult_porc_interes_tbl_proyecto_pkey PRIMARY KEY (id);
 m   ALTER TABLE ONLY negocio.mult_porc_interes_tbl_proyecto DROP CONSTRAINT mult_porc_interes_tbl_proyecto_pkey;
       negocio            postgres    false    334            ~           2606    86361 H   ptn_conciliacion_aprobada_detalle ptn_conciliacion_aprobada_detalle_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY negocio.ptn_conciliacion_aprobada_detalle
    ADD CONSTRAINT ptn_conciliacion_aprobada_detalle_pkey PRIMARY KEY (id);
 s   ALTER TABLE ONLY negocio.ptn_conciliacion_aprobada_detalle DROP CONSTRAINT ptn_conciliacion_aprobada_detalle_pkey;
       negocio            postgres    false    470            |           2606    86354 8   ptn_conciliacion_aprobada ptn_conciliacion_aprobada_pkey 
   CONSTRAINT     w   ALTER TABLE ONLY negocio.ptn_conciliacion_aprobada
    ADD CONSTRAINT ptn_conciliacion_aprobada_pkey PRIMARY KEY (id);
 c   ALTER TABLE ONLY negocio.ptn_conciliacion_aprobada DROP CONSTRAINT ptn_conciliacion_aprobada_pkey;
       negocio            postgres    false    468            �           2606    86368 >   ptn_conciliacion_detalle_xls ptn_conciliacion_detalle_xls_pkey 
   CONSTRAINT     }   ALTER TABLE ONLY negocio.ptn_conciliacion_detalle_xls
    ADD CONSTRAINT ptn_conciliacion_detalle_xls_pkey PRIMARY KEY (id);
 i   ALTER TABLE ONLY negocio.ptn_conciliacion_detalle_xls DROP CONSTRAINT ptn_conciliacion_detalle_xls_pkey;
       negocio            postgres    false    472            �           2606    86375 .   ptn_conciliacion_xls ptn_conciliacion_xls_pkey 
   CONSTRAINT     m   ALTER TABLE ONLY negocio.ptn_conciliacion_xls
    ADD CONSTRAINT ptn_conciliacion_xls_pkey PRIMARY KEY (id);
 Y   ALTER TABLE ONLY negocio.ptn_conciliacion_xls DROP CONSTRAINT ptn_conciliacion_xls_pkey;
       negocio            postgres    false    474            �           2606    86382 4   ptn_fecha_gen_tbl_amort ptn_fecha_gen_tbl_amort_pkey 
   CONSTRAINT     s   ALTER TABLE ONLY negocio.ptn_fecha_gen_tbl_amort
    ADD CONSTRAINT ptn_fecha_gen_tbl_amort_pkey PRIMARY KEY (id);
 _   ALTER TABLE ONLY negocio.ptn_fecha_gen_tbl_amort DROP CONSTRAINT ptn_fecha_gen_tbl_amort_pkey;
       negocio            postgres    false    476            �           2606    86389 @   ptn_porc_interes_tbl_proyecto ptn_porc_interes_tbl_proyecto_pkey 
   CONSTRAINT        ALTER TABLE ONLY negocio.ptn_porc_interes_tbl_proyecto
    ADD CONSTRAINT ptn_porc_interes_tbl_proyecto_pkey PRIMARY KEY (id);
 k   ALTER TABLE ONLY negocio.ptn_porc_interes_tbl_proyecto DROP CONSTRAINT ptn_porc_interes_tbl_proyecto_pkey;
       negocio            postgres    false    478            �           2606    86391 4   ptn_fecha_gen_tbl_amort uk_on0pv2jqr1wtcyadv3t22w1ph 
   CONSTRAINT     w   ALTER TABLE ONLY negocio.ptn_fecha_gen_tbl_amort
    ADD CONSTRAINT uk_on0pv2jqr1wtcyadv3t22w1ph UNIQUE (id_proyecto);
 _   ALTER TABLE ONLY negocio.ptn_fecha_gen_tbl_amort DROP CONSTRAINT uk_on0pv2jqr1wtcyadv3t22w1ph;
       negocio            postgres    false    476            �           2606    68719 5   mult_fecha_gen_tbl_amort uk_pwmvi9aier4kcs4gckytm0eii 
   CONSTRAINT     x   ALTER TABLE ONLY negocio.mult_fecha_gen_tbl_amort
    ADD CONSTRAINT uk_pwmvi9aier4kcs4gckytm0eii UNIQUE (id_proyecto);
 `   ALTER TABLE ONLY negocio.mult_fecha_gen_tbl_amort DROP CONSTRAINT uk_pwmvi9aier4kcs4gckytm0eii;
       negocio            postgres    false    332            �           2606    68721 $   mult_parametros mult_parametros_pkey 
   CONSTRAINT     u   ALTER TABLE ONLY parametrizacion.mult_parametros
    ADD CONSTRAINT mult_parametros_pkey PRIMARY KEY (id_parametro);
 W   ALTER TABLE ONLY parametrizacion.mult_parametros DROP CONSTRAINT mult_parametros_pkey;
       parametrizacion            postgres    false    336            �           2606    86398 "   ptn_parametros ptn_parametros_pkey 
   CONSTRAINT     s   ALTER TABLE ONLY parametrizacion.ptn_parametros
    ADD CONSTRAINT ptn_parametros_pkey PRIMARY KEY (id_parametro);
 U   ALTER TABLE ONLY parametrizacion.ptn_parametros DROP CONSTRAINT ptn_parametros_pkey;
       parametrizacion            postgres    false    480            �           2606    68723 D   mult_detalle_porc_sol_aprobadas mult_detalle_porc_sol_aprobadas_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_detalle_porc_sol_aprobadas
    ADD CONSTRAINT mult_detalle_porc_sol_aprobadas_pkey PRIMARY KEY (id);
 p   ALTER TABLE ONLY promotor.mult_detalle_porc_sol_aprobadas DROP CONSTRAINT mult_detalle_porc_sol_aprobadas_pkey;
       promotor            postgres    false    338            �           2606    68725 <   mult_documentos_financieros mult_documentos_financieros_pkey 
   CONSTRAINT     |   ALTER TABLE ONLY promotor.mult_documentos_financieros
    ADD CONSTRAINT mult_documentos_financieros_pkey PRIMARY KEY (id);
 h   ALTER TABLE ONLY promotor.mult_documentos_financieros DROP CONSTRAINT mult_documentos_financieros_pkey;
       promotor            postgres    false    340            �           2606    68727 8   mult_documentos_juridicos mult_documentos_juridicos_pkey 
   CONSTRAINT     x   ALTER TABLE ONLY promotor.mult_documentos_juridicos
    ADD CONSTRAINT mult_documentos_juridicos_pkey PRIMARY KEY (id);
 d   ALTER TABLE ONLY promotor.mult_documentos_juridicos DROP CONSTRAINT mult_documentos_juridicos_pkey;
       promotor            postgres    false    342            �           2606    68729 :   mult_empresa_datos_anuales mult_empresa_datos_anuales_pkey 
   CONSTRAINT     z   ALTER TABLE ONLY promotor.mult_empresa_datos_anuales
    ADD CONSTRAINT mult_empresa_datos_anuales_pkey PRIMARY KEY (id);
 f   ALTER TABLE ONLY promotor.mult_empresa_datos_anuales DROP CONSTRAINT mult_empresa_datos_anuales_pkey;
       promotor            postgres    false    344            �           2606    68731     mult_empresas mult_empresas_pkey 
   CONSTRAINT     h   ALTER TABLE ONLY promotor.mult_empresas
    ADD CONSTRAINT mult_empresas_pkey PRIMARY KEY (id_empresa);
 L   ALTER TABLE ONLY promotor.mult_empresas DROP CONSTRAINT mult_empresas_pkey;
       promotor            postgres    false    346            �           2606    68733 &   mult_indicadores mult_indicadores_pkey 
   CONSTRAINT     p   ALTER TABLE ONLY promotor.mult_indicadores
    ADD CONSTRAINT mult_indicadores_pkey PRIMARY KEY (id_indicador);
 R   ALTER TABLE ONLY promotor.mult_indicadores DROP CONSTRAINT mult_indicadores_pkey;
       promotor            postgres    false    348            �           2606    68735 4   mult_porc_sol_aprobadas mult_porc_sol_aprobadas_pkey 
   CONSTRAINT     t   ALTER TABLE ONLY promotor.mult_porc_sol_aprobadas
    ADD CONSTRAINT mult_porc_sol_aprobadas_pkey PRIMARY KEY (id);
 `   ALTER TABLE ONLY promotor.mult_porc_sol_aprobadas DROP CONSTRAINT mult_porc_sol_aprobadas_pkey;
       promotor            postgres    false    350            �           2606    68737 2   mult_proyectos_cuentas mult_proyectos_cuentas_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_proyectos_cuentas
    ADD CONSTRAINT mult_proyectos_cuentas_pkey PRIMARY KEY (id_proyecto_cuenta);
 ^   ALTER TABLE ONLY promotor.mult_proyectos_cuentas DROP CONSTRAINT mult_proyectos_cuentas_pkey;
       promotor            postgres    false    353            �           2606    68739 "   mult_proyectos mult_proyectos_pkey 
   CONSTRAINT     k   ALTER TABLE ONLY promotor.mult_proyectos
    ADD CONSTRAINT mult_proyectos_pkey PRIMARY KEY (id_proyecto);
 N   ALTER TABLE ONLY promotor.mult_proyectos DROP CONSTRAINT mult_proyectos_pkey;
       promotor            postgres    false    352            �           2606    68741 .   mult_proyectos_rutas mult_proyectos_rutas_pkey 
   CONSTRAINT     |   ALTER TABLE ONLY promotor.mult_proyectos_rutas
    ADD CONSTRAINT mult_proyectos_rutas_pkey PRIMARY KEY (id_proyecto_ruta);
 Z   ALTER TABLE ONLY promotor.mult_proyectos_rutas DROP CONSTRAINT mult_proyectos_rutas_pkey;
       promotor            postgres    false    355            �           2606    68743 &   mult_rango_pagos mult_rango_pagos_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY promotor.mult_rango_pagos
    ADD CONSTRAINT mult_rango_pagos_pkey PRIMARY KEY (id);
 R   ALTER TABLE ONLY promotor.mult_rango_pagos DROP CONSTRAINT mult_rango_pagos_pkey;
       promotor            postgres    false    357            �           2606    68745 0   mult_tipo_actividades mult_tipo_actividades_pkey 
   CONSTRAINT     z   ALTER TABLE ONLY promotor.mult_tipo_actividades
    ADD CONSTRAINT mult_tipo_actividades_pkey PRIMARY KEY (id_actividad);
 \   ALTER TABLE ONLY promotor.mult_tipo_actividades DROP CONSTRAINT mult_tipo_actividades_pkey;
       promotor            postgres    false    359            �           2606    68747 6   mult_tipo_calificaciones mult_tipo_calificaciones_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_tipo_calificaciones
    ADD CONSTRAINT mult_tipo_calificaciones_pkey PRIMARY KEY (id_tipo_calificacion);
 b   ALTER TABLE ONLY promotor.mult_tipo_calificaciones DROP CONSTRAINT mult_tipo_calificaciones_pkey;
       promotor            postgres    false    361            �           2606    86405 B   ptn_detalle_porc_sol_aprobadas ptn_detalle_porc_sol_aprobadas_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_detalle_porc_sol_aprobadas
    ADD CONSTRAINT ptn_detalle_porc_sol_aprobadas_pkey PRIMARY KEY (id);
 n   ALTER TABLE ONLY promotor.ptn_detalle_porc_sol_aprobadas DROP CONSTRAINT ptn_detalle_porc_sol_aprobadas_pkey;
       promotor            postgres    false    482            �           2606    86415 :   ptn_documentos_financieros ptn_documentos_financieros_pkey 
   CONSTRAINT     z   ALTER TABLE ONLY promotor.ptn_documentos_financieros
    ADD CONSTRAINT ptn_documentos_financieros_pkey PRIMARY KEY (id);
 f   ALTER TABLE ONLY promotor.ptn_documentos_financieros DROP CONSTRAINT ptn_documentos_financieros_pkey;
       promotor            postgres    false    484            �           2606    86425 6   ptn_documentos_juridicos ptn_documentos_juridicos_pkey 
   CONSTRAINT     v   ALTER TABLE ONLY promotor.ptn_documentos_juridicos
    ADD CONSTRAINT ptn_documentos_juridicos_pkey PRIMARY KEY (id);
 b   ALTER TABLE ONLY promotor.ptn_documentos_juridicos DROP CONSTRAINT ptn_documentos_juridicos_pkey;
       promotor            postgres    false    486            �           2606    86432 8   ptn_empresa_datos_anuales ptn_empresa_datos_anuales_pkey 
   CONSTRAINT     x   ALTER TABLE ONLY promotor.ptn_empresa_datos_anuales
    ADD CONSTRAINT ptn_empresa_datos_anuales_pkey PRIMARY KEY (id);
 d   ALTER TABLE ONLY promotor.ptn_empresa_datos_anuales DROP CONSTRAINT ptn_empresa_datos_anuales_pkey;
       promotor            postgres    false    488            �           2606    86442    ptn_empresas ptn_empresas_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY promotor.ptn_empresas
    ADD CONSTRAINT ptn_empresas_pkey PRIMARY KEY (id_empresa);
 J   ALTER TABLE ONLY promotor.ptn_empresas DROP CONSTRAINT ptn_empresas_pkey;
       promotor            postgres    false    490            �           2606    86452 $   ptn_indicadores ptn_indicadores_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY promotor.ptn_indicadores
    ADD CONSTRAINT ptn_indicadores_pkey PRIMARY KEY (id_indicador);
 P   ALTER TABLE ONLY promotor.ptn_indicadores DROP CONSTRAINT ptn_indicadores_pkey;
       promotor            postgres    false    492            �           2606    86459 2   ptn_porc_sol_aprobadas ptn_porc_sol_aprobadas_pkey 
   CONSTRAINT     r   ALTER TABLE ONLY promotor.ptn_porc_sol_aprobadas
    ADD CONSTRAINT ptn_porc_sol_aprobadas_pkey PRIMARY KEY (id);
 ^   ALTER TABLE ONLY promotor.ptn_porc_sol_aprobadas DROP CONSTRAINT ptn_porc_sol_aprobadas_pkey;
       promotor            postgres    false    494            �           2606    86471 0   ptn_proyectos_cuentas ptn_proyectos_cuentas_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_proyectos_cuentas
    ADD CONSTRAINT ptn_proyectos_cuentas_pkey PRIMARY KEY (id_proyecto_cuenta);
 \   ALTER TABLE ONLY promotor.ptn_proyectos_cuentas DROP CONSTRAINT ptn_proyectos_cuentas_pkey;
       promotor            postgres    false    497            �           2606    86464     ptn_proyectos ptn_proyectos_pkey 
   CONSTRAINT     i   ALTER TABLE ONLY promotor.ptn_proyectos
    ADD CONSTRAINT ptn_proyectos_pkey PRIMARY KEY (id_proyecto);
 L   ALTER TABLE ONLY promotor.ptn_proyectos DROP CONSTRAINT ptn_proyectos_pkey;
       promotor            postgres    false    495            �           2606    86478 ,   ptn_proyectos_rutas ptn_proyectos_rutas_pkey 
   CONSTRAINT     z   ALTER TABLE ONLY promotor.ptn_proyectos_rutas
    ADD CONSTRAINT ptn_proyectos_rutas_pkey PRIMARY KEY (id_proyecto_ruta);
 X   ALTER TABLE ONLY promotor.ptn_proyectos_rutas DROP CONSTRAINT ptn_proyectos_rutas_pkey;
       promotor            postgres    false    499            �           2606    86485 .   ptn_tipo_actividades ptn_tipo_actividades_pkey 
   CONSTRAINT     x   ALTER TABLE ONLY promotor.ptn_tipo_actividades
    ADD CONSTRAINT ptn_tipo_actividades_pkey PRIMARY KEY (id_actividad);
 Z   ALTER TABLE ONLY promotor.ptn_tipo_actividades DROP CONSTRAINT ptn_tipo_actividades_pkey;
       promotor            postgres    false    501            �           2606    86492 4   ptn_tipo_calificaciones ptn_tipo_calificaciones_pkey 
   CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_tipo_calificaciones
    ADD CONSTRAINT ptn_tipo_calificaciones_pkey PRIMARY KEY (id_tipo_calificacion);
 `   ALTER TABLE ONLY promotor.ptn_tipo_calificaciones DROP CONSTRAINT ptn_tipo_calificaciones_pkey;
       promotor            postgres    false    503            �           2606    68749 2   mult_tipo_actividades uk_72fo58mmwuqohqdi4jp7u4fdm 
   CONSTRAINT     q   ALTER TABLE ONLY promotor.mult_tipo_actividades
    ADD CONSTRAINT uk_72fo58mmwuqohqdi4jp7u4fdm UNIQUE (nombre);
 ^   ALTER TABLE ONLY promotor.mult_tipo_actividades DROP CONSTRAINT uk_72fo58mmwuqohqdi4jp7u4fdm;
       promotor            postgres    false    359            �           2606    86494 )   ptn_empresas uk_8sd2t7brtd04pnpylmt7vtct4 
   CONSTRAINT     h   ALTER TABLE ONLY promotor.ptn_empresas
    ADD CONSTRAINT uk_8sd2t7brtd04pnpylmt7vtct4 UNIQUE (nombre);
 U   ALTER TABLE ONLY promotor.ptn_empresas DROP CONSTRAINT uk_8sd2t7brtd04pnpylmt7vtct4;
       promotor            postgres    false    490            �           2606    68751 *   mult_empresas uk_a7gd7n11j5w8ja8nb53i55q6j 
   CONSTRAINT     i   ALTER TABLE ONLY promotor.mult_empresas
    ADD CONSTRAINT uk_a7gd7n11j5w8ja8nb53i55q6j UNIQUE (nombre);
 V   ALTER TABLE ONLY promotor.mult_empresas DROP CONSTRAINT uk_a7gd7n11j5w8ja8nb53i55q6j;
       promotor            postgres    false    346            �           2606    86496 1   ptn_tipo_actividades uk_likeoyj1ob7j1rqffhv0rndjr 
   CONSTRAINT     p   ALTER TABLE ONLY promotor.ptn_tipo_actividades
    ADD CONSTRAINT uk_likeoyj1ob7j1rqffhv0rndjr UNIQUE (nombre);
 ]   ALTER TABLE ONLY promotor.ptn_tipo_actividades DROP CONSTRAINT uk_likeoyj1ob7j1rqffhv0rndjr;
       promotor            postgres    false    501            �           2606    68753 #   mult_cuentas mult_cuentas_email_key 
   CONSTRAINT     _   ALTER TABLE ONLY public.mult_cuentas
    ADD CONSTRAINT mult_cuentas_email_key UNIQUE (email);
 M   ALTER TABLE ONLY public.mult_cuentas DROP CONSTRAINT mult_cuentas_email_key;
       public            postgres    false    364            �           2606    68755 ,   mult_cuentas mult_cuentas_identificacion_key 
   CONSTRAINT     q   ALTER TABLE ONLY public.mult_cuentas
    ADD CONSTRAINT mult_cuentas_identificacion_key UNIQUE (identificacion);
 V   ALTER TABLE ONLY public.mult_cuentas DROP CONSTRAINT mult_cuentas_identificacion_key;
       public            postgres    false    364            �           2606    68757    mult_cuentas mult_cuentas_pkey 
   CONSTRAINT     c   ALTER TABLE ONLY public.mult_cuentas
    ADD CONSTRAINT mult_cuentas_pkey PRIMARY KEY (id_cuenta);
 H   ALTER TABLE ONLY public.mult_cuentas DROP CONSTRAINT mult_cuentas_pkey;
       public            postgres    false    364            �           2606    68759 %   mult_cuentas mult_cuentas_usuario_key 
   CONSTRAINT     c   ALTER TABLE ONLY public.mult_cuentas
    ADD CONSTRAINT mult_cuentas_usuario_key UNIQUE (usuario);
 O   ALTER TABLE ONLY public.mult_cuentas DROP CONSTRAINT mult_cuentas_usuario_key;
       public            postgres    false    364            �           2606    68761 "   mult_empleados mult_empleados_pkey 
   CONSTRAINT     i   ALTER TABLE ONLY public.mult_empleados
    ADD CONSTRAINT mult_empleados_pkey PRIMARY KEY (id_empleado);
 L   ALTER TABLE ONLY public.mult_empleados DROP CONSTRAINT mult_empleados_pkey;
       public            postgres    false    366            �           2606    68763 )   mult_empleados mult_empleados_usuario_key 
   CONSTRAINT     g   ALTER TABLE ONLY public.mult_empleados
    ADD CONSTRAINT mult_empleados_usuario_key UNIQUE (usuario);
 S   ALTER TABLE ONLY public.mult_empleados DROP CONSTRAINT mult_empleados_usuario_key;
       public            postgres    false    366            �           2606    68765 %   mult_personas mult_personas_email_key 
   CONSTRAINT     a   ALTER TABLE ONLY public.mult_personas
    ADD CONSTRAINT mult_personas_email_key UNIQUE (email);
 O   ALTER TABLE ONLY public.mult_personas DROP CONSTRAINT mult_personas_email_key;
       public            postgres    false    368            �           2606    68767     mult_personas mult_personas_pkey 
   CONSTRAINT     v   ALTER TABLE ONLY public.mult_personas
    ADD CONSTRAINT mult_personas_pkey PRIMARY KEY (id_persona, identificacion);
 J   ALTER TABLE ONLY public.mult_personas DROP CONSTRAINT mult_personas_pkey;
       public            postgres    false    368    368            �           2606    68769    mult_roles mult_roles_pkey 
   CONSTRAINT     \   ALTER TABLE ONLY public.mult_roles
    ADD CONSTRAINT mult_roles_pkey PRIMARY KEY (id_rol);
 D   ALTER TABLE ONLY public.mult_roles DROP CONSTRAINT mult_roles_pkey;
       public            postgres    false    370            �           2606    68771    mult_token mult_token_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public.mult_token
    ADD CONSTRAINT mult_token_pkey PRIMARY KEY (id_token);
 D   ALTER TABLE ONLY public.mult_token DROP CONSTRAINT mult_token_pkey;
       public            postgres    false    372            �           2606    68773     mult_usuarios mult_usuarios_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY public.mult_usuarios
    ADD CONSTRAINT mult_usuarios_pkey PRIMARY KEY (id_usuario);
 J   ALTER TABLE ONLY public.mult_usuarios DROP CONSTRAINT mult_usuarios_pkey;
       public            postgres    false    374            �           1259    68774    idx01_bene_ident    INDEX     O   CREATE INDEX idx01_bene_ident ON auspicios.mult_beneficiario USING btree (id);
 '   DROP INDEX auspicios.idx01_bene_ident;
    	   auspicios            postgres    false    222            �           1259    68775    idx01_bene_person    INDEX     X   CREATE INDEX idx01_bene_person ON auspicios.mult_beneficiario USING btree (id_persona);
 (   DROP INDEX auspicios.idx01_bene_person;
    	   auspicios            postgres    false    222            �           1259    68776    idx01_beneficiarioxactivo    INDEX     g   CREATE INDEX idx01_beneficiarioxactivo ON auspicios.mult_auspicios USING btree (beneficiario, activo);
 0   DROP INDEX auspicios.idx01_beneficiarioxactivo;
    	   auspicios            postgres    false    213    213            �           1259    68777    idx01_val_aus_bene    INDEX     c   CREATE INDEX idx01_val_aus_bene ON auspicios.mult_auspicios_valoracion USING btree (beneficiario);
 )   DROP INDEX auspicios.idx01_val_aus_bene;
    	   auspicios            postgres    false    220            �           1259    68778    idx01_val_aus_benexanio    INDEX     p   CREATE INDEX idx01_val_aus_benexanio ON auspicios.mult_auspicios_valoracion USING btree (beneficiario, activo);
 .   DROP INDEX auspicios.idx01_val_aus_benexanio;
    	   auspicios            postgres    false    220    220            �           1259    68779    idx02_auspicioxbeneficiario    INDEX     a   CREATE INDEX idx02_auspicioxbeneficiario ON auspicios.mult_auspicios USING btree (beneficiario);
 2   DROP INDEX auspicios.idx02_auspicioxbeneficiario;
    	   auspicios            postgres    false    213            �           1259    68780    idx02_auspicioxestado    INDEX     U   CREATE INDEX idx02_auspicioxestado ON auspicios.mult_auspicios USING btree (estado);
 ,   DROP INDEX auspicios.idx02_auspicioxestado;
    	   auspicios            postgres    false    213            �           1259    68781    idx02_bene_ident_repre    INDEX     g   CREATE INDEX idx02_bene_ident_repre ON auspicios.mult_beneficiario USING btree (id, id_representante);
 -   DROP INDEX auspicios.idx02_bene_ident_repre;
    	   auspicios            postgres    false    222    222            �           1259    68782    idx01_cuenta_id    INDEX     M   CREATE INDEX idx01_cuenta_id ON cuenta.mult_cuentas USING btree (id_cuenta);
 #   DROP INDEX cuenta.idx01_cuenta_id;
       cuenta            postgres    false    225                       1259    68783    idx01_pers_ident    INDEX     T   CREATE INDEX idx01_pers_ident ON cuenta.mult_personas USING btree (identificacion);
 $   DROP INDEX cuenta.idx01_pers_ident;
       cuenta            postgres    false    245            	           1259    68784    idx01_persinfo_idpersona    INDEX     k   CREATE INDEX idx01_persinfo_idpersona ON cuenta.mult_pers_info_adicional USING btree (id_persona, estado);
 ,   DROP INDEX cuenta.idx01_persinfo_idpersona;
       cuenta            postgres    false    241    241                       1259    68785    idx01_rol_nombreestado    INDEX     W   CREATE INDEX idx01_rol_nombreestado ON cuenta.mult_roles USING btree (nombre, estado);
 *   DROP INDEX cuenta.idx01_rol_nombreestado;
       cuenta            postgres    false    246    246            �           1259    68786    idx02_cuenta_mail    INDEX     K   CREATE INDEX idx02_cuenta_mail ON cuenta.mult_cuentas USING btree (email);
 %   DROP INDEX cuenta.idx02_cuenta_mail;
       cuenta            postgres    false    225                       1259    68787    idx02_pers_ident_status    INDEX     c   CREATE INDEX idx02_pers_ident_status ON cuenta.mult_personas USING btree (identificacion, estado);
 +   DROP INDEX cuenta.idx02_pers_ident_status;
       cuenta            postgres    false    245    245            �           1259    68788    idx03_cuenta_mail_status    INDEX     Z   CREATE INDEX idx03_cuenta_mail_status ON cuenta.mult_cuentas USING btree (email, estado);
 ,   DROP INDEX cuenta.idx03_cuenta_mail_status;
       cuenta            postgres    false    225    225            -           1259    68789 	   idx01_sol    INDEX     W   CREATE INDEX idx01_sol ON historicas.mult_sol_x_identifiacion USING btree (solicitud);
 !   DROP INDEX historicas.idx01_sol;
    
   historicas            postgres    false    267            2           1259    68790    idx01_detaamort_idtblamort    INDEX     z   CREATE INDEX idx01_detaamort_idtblamort ON inversion.mult_detalle_amortizacion USING btree (id_tbl_amortizacion, estado);
 1   DROP INDEX inversion.idx01_detaamort_idtblamort;
    	   inversion            postgres    false    271    271            5           1259    68791    idx01_docu_tipodocu    INDEX     j   CREATE INDEX idx01_docu_tipodocu ON inversion.mult_doc_aceptados USING btree (id_tipo_documento, estado);
 *   DROP INDEX inversion.idx01_docu_tipodocu;
    	   inversion            postgres    false    273    273            A           1259    68792    idx01_sol_doc_numsolicitud    INDEX     j   CREATE INDEX idx01_sol_doc_numsolicitud ON inversion.mult_solicitudes_documentos USING btree (solicitud);
 1   DROP INDEX inversion.idx01_sol_doc_numsolicitud;
    	   inversion            postgres    false    276            8           1259    68793    idx01_sol_numsolicitud    INDEX     b   CREATE INDEX idx01_sol_numsolicitud ON inversion.mult_solicitudes USING btree (numero_solicitud);
 -   DROP INDEX inversion.idx01_sol_numsolicitud;
    	   inversion            postgres    false    275            K           1259    68794    idx01_trans_soli    INDEX     ^   CREATE INDEX idx01_trans_soli ON inversion.mult_transacciones USING btree (numero_solicitud);
 '   DROP INDEX inversion.idx01_trans_soli;
    	   inversion            postgres    false    283            L           1259    68795    idx02_trans_soliesta    INDEX     j   CREATE INDEX idx02_trans_soliesta ON inversion.mult_transacciones USING btree (numero_solicitud, estado);
 +   DROP INDEX inversion.idx02_trans_soliesta;
    	   inversion            postgres    false    283    283            B           1259    68796    idx03_sol_doc_numsolesta    INDEX     p   CREATE INDEX idx03_sol_doc_numsolesta ON inversion.mult_solicitudes_documentos USING btree (solicitud, estado);
 /   DROP INDEX inversion.idx03_sol_doc_numsolesta;
    	   inversion            postgres    false    276    276            9           1259    68797    idx03_sol_numsolestaactu    INDEX     s   CREATE INDEX idx03_sol_numsolestaactu ON inversion.mult_solicitudes USING btree (numero_solicitud, estado_actual);
 /   DROP INDEX inversion.idx03_sol_numsolestaactu;
    	   inversion            postgres    false    275    275            M           1259    68798    idx03_trans_conciliado    INDEX     p   CREATE INDEX idx03_trans_conciliado ON inversion.mult_transacciones USING btree (numero_solicitud, conciliado);
 -   DROP INDEX inversion.idx03_trans_conciliado;
    	   inversion            postgres    false    283    283            :           1259    68799    idx04_sol_numsolamort    INDEX     u   CREATE INDEX idx04_sol_numsolamort ON inversion.mult_solicitudes USING btree (numero_solicitud, tabla_amortizacion);
 ,   DROP INDEX inversion.idx04_sol_numsolamort;
    	   inversion            postgres    false    275    275            N           1259    68800    idx04_trans_comprobante    INDEX     y   CREATE INDEX idx04_trans_comprobante ON inversion.mult_transacciones USING btree (numero_solicitud, numero_comprobante);
 .   DROP INDEX inversion.idx04_trans_comprobante;
    	   inversion            postgres    false    283    283            ;           1259    68801    idx05_sol_numsolinvestor    INDEX     v   CREATE INDEX idx05_sol_numsolinvestor ON inversion.mult_solicitudes USING btree (numero_solicitud, id_inversionista);
 /   DROP INDEX inversion.idx05_sol_numsolinvestor;
    	   inversion            postgres    false    275    275            <           1259    68802    idx06_sol_numsolusercreacion    INDEX     z   CREATE INDEX idx06_sol_numsolusercreacion ON inversion.mult_solicitudes USING btree (numero_solicitud, usuario_creacion);
 3   DROP INDEX inversion.idx06_sol_numsolusercreacion;
    	   inversion            postgres    false    275    275            =           1259    68803    idx07_sol_numsolproyecto    INDEX     u   CREATE INDEX idx07_sol_numsolproyecto ON inversion.mult_solicitudes USING btree (numero_solicitud, codigo_proyecto);
 /   DROP INDEX inversion.idx07_sol_numsolproyecto;
    	   inversion            postgres    false    275    275            >           1259    68804    idx08_sol_numsolpagare    INDEX     j   CREATE INDEX idx08_sol_numsolpagare ON inversion.mult_solicitudes USING btree (numero_solicitud, pagare);
 -   DROP INDEX inversion.idx08_sol_numsolpagare;
    	   inversion            postgres    false    275    275            h           1259    86322    idx01_minxmax    INDEX     N   CREATE INDEX idx01_minxmax ON maestras.ptn_rango_pago USING btree (min, max);
 #   DROP INDEX maestras.idx01_minxmax;
       maestras            postgres    false    454    454            Y           1259    68805    idx01_naciona_id    INDEX     ]   CREATE INDEX idx01_naciona_id ON maestras.mult_nacionalidades USING btree (id_nacionalidad);
 &   DROP INDEX maestras.idx01_naciona_id;
       maestras            postgres    false    291            Z           1259    68806    idx02_naciona_iso    INDEX     Z   CREATE INDEX idx02_naciona_iso ON maestras.mult_nacionalidades USING btree (iso, estado);
 '   DROP INDEX maestras.idx02_naciona_iso;
       maestras            postgres    false    291    291            s           1259    68807    idx01_cuenta_int_id    INDEX     \   CREATE INDEX idx01_cuenta_int_id ON multiplo.mult_cuentas_internas USING btree (id_cuenta);
 )   DROP INDEX multiplo.idx01_cuenta_int_id;
       multiplo            postgres    false    309            �           1259    68808    idx01_pers_int_ident    INDEX     c   CREATE INDEX idx01_pers_int_ident ON multiplo.mult_personal_interno USING btree (id_pers_interno);
 *   DROP INDEX multiplo.idx01_pers_int_ident;
       multiplo            postgres    false    315            �           1259    68809    idx01_rol_nombreestado    INDEX     ]   CREATE INDEX idx01_rol_nombreestado ON multiplo.mult_roles_int USING btree (nombre, estado);
 ,   DROP INDEX multiplo.idx01_rol_nombreestado;
       multiplo            postgres    false    317    317            t           1259    68810    idx02_cuenta_int_mail    INDEX     Z   CREATE INDEX idx02_cuenta_int_mail ON multiplo.mult_cuentas_internas USING btree (email);
 +   DROP INDEX multiplo.idx02_cuenta_int_mail;
       multiplo            postgres    false    309            �           1259    68811    idx02_pers_cuenta    INDEX     _   CREATE INDEX idx02_pers_cuenta ON multiplo.mult_personal_interno USING btree (cuenta_interna);
 '   DROP INDEX multiplo.idx02_pers_cuenta;
       multiplo            postgres    false    315            �           1259    68812    idx01_cod_fact    INDEX     j   CREATE INDEX idx01_cod_fact ON multiplo_documentos.mult_documentos_facturas USING btree (codigo_factura);
 /   DROP INDEX multiplo_documentos.idx01_cod_fact;
       multiplo_documentos            postgres    false    320            �           1259    68813    idx01_docu_tipodoc_esta    INDEX     u   CREATE INDEX idx01_docu_tipodoc_esta ON multiplo_documentos.mult_documentos USING btree (id_tipo_documento, estado);
 8   DROP INDEX multiplo_documentos.idx01_docu_tipodoc_esta;
       multiplo_documentos            postgres    false    319    319            �           1259    68814    idx02_estaado    INDEX     a   CREATE INDEX idx02_estaado ON multiplo_documentos.mult_documentos_facturas USING btree (estado);
 .   DROP INDEX multiplo_documentos.idx02_estaado;
       multiplo_documentos            postgres    false    320            �           1259    68815    idx02_id_cliente    INDEX     h   CREATE INDEX idx02_id_cliente ON multiplo_documentos.mult_documentos_facturas USING btree (id_cliente);
 1   DROP INDEX multiplo_documentos.idx02_id_cliente;
       multiplo_documentos            postgres    false    320            �           1259    68816     idx01_estadoconciliacionaprobada    INDEX     j   CREATE INDEX idx01_estadoconciliacionaprobada ON negocio.mult_conciliacion_aprobada USING btree (estado);
 5   DROP INDEX negocio.idx01_estadoconciliacionaprobada;
       negocio            postgres    false    324            �           1259    68817    idx01_estadoxls    INDEX     T   CREATE INDEX idx01_estadoxls ON negocio.mult_conciliacion_xls USING btree (estado);
 $   DROP INDEX negocio.idx01_estadoxls;
       negocio            postgres    false    330            �           1259    68818    idx01_fechgen_idproy    INDEX     a   CREATE INDEX idx01_fechgen_idproy ON negocio.mult_fecha_gen_tbl_amort USING btree (id_proyecto);
 )   DROP INDEX negocio.idx01_fechgen_idproy;
       negocio            postgres    false    332            �           1259    68819     idx01_porc_inte_tbl_proy_codproy    INDEX     w   CREATE INDEX idx01_porc_inte_tbl_proy_codproy ON negocio.mult_porc_interes_tbl_proyecto USING btree (codigo_proyecto);
 5   DROP INDEX negocio.idx01_porc_inte_tbl_proy_codproy;
       negocio            postgres    false    334            �           1259    68820    idx02_fechgen_fechgenera    INDEX     j   CREATE INDEX idx02_fechgen_fechgenera ON negocio.mult_fecha_gen_tbl_amort USING btree (fecha_generacion);
 -   DROP INDEX negocio.idx02_fechgen_fechgenera;
       negocio            postgres    false    332            �           1259    68821    idx03_fechgen_fechcreaci    INDEX     h   CREATE INDEX idx03_fechgen_fechcreaci ON negocio.mult_fecha_gen_tbl_amort USING btree (fecha_creacion);
 -   DROP INDEX negocio.idx03_fechgen_fechcreaci;
       negocio            postgres    false    332            �           1259    68822    idx01_param_parametro    INDEX     g   CREATE INDEX idx01_param_parametro ON parametrizacion.mult_parametros USING btree (parametro, estado);
 2   DROP INDEX parametrizacion.idx01_param_parametro;
       parametrizacion            postgres    false    336    336            �           1259    68823    idx02_param_valor    INDEX     _   CREATE INDEX idx02_param_valor ON parametrizacion.mult_parametros USING btree (valor, estado);
 .   DROP INDEX parametrizacion.idx02_param_valor;
       parametrizacion            postgres    false    336    336            �           1259    68824    idx01_pro_docfin_empresa    INDEX     e   CREATE INDEX idx01_pro_docfin_empresa ON promotor.mult_documentos_financieros USING btree (empresa);
 .   DROP INDEX promotor.idx01_pro_docfin_empresa;
       promotor            postgres    false    340            �           1259    68825    idx01_pro_docjur_empresa    INDEX     c   CREATE INDEX idx01_pro_docjur_empresa ON promotor.mult_documentos_juridicos USING btree (empresa);
 .   DROP INDEX promotor.idx01_pro_docjur_empresa;
       promotor            postgres    false    342            �           1259    68826    idx01_proycuenta_idproyecto    INDEX     g   CREATE INDEX idx01_proycuenta_idproyecto ON promotor.mult_proyectos_cuentas USING btree (id_proyecto);
 1   DROP INDEX promotor.idx01_proycuenta_idproyecto;
       promotor            postgres    false    353            �           1259    68827    idx01_proyecto_idproyecto    INDEX     ]   CREATE INDEX idx01_proyecto_idproyecto ON promotor.mult_proyectos USING btree (id_proyecto);
 /   DROP INDEX promotor.idx01_proyecto_idproyecto;
       promotor            postgres    false    352            �           1259    68828    idx02_proyecto_idproyestact    INDEX     n   CREATE INDEX idx02_proyecto_idproyestact ON promotor.mult_proyectos USING btree (id_proyecto, estado_actual);
 1   DROP INDEX promotor.idx02_proyecto_idproyestact;
       promotor            postgres    false    352    352            �           1259    68829    idx03_pro_docfin_empresaestado    INDEX     s   CREATE INDEX idx03_pro_docfin_empresaestado ON promotor.mult_documentos_financieros USING btree (empresa, activo);
 4   DROP INDEX promotor.idx03_pro_docfin_empresaestado;
       promotor            postgres    false    340    340            �           1259    68830    idx03_pro_docjur_empresaestado    INDEX     q   CREATE INDEX idx03_pro_docjur_empresaestado ON promotor.mult_documentos_juridicos USING btree (empresa, activo);
 4   DROP INDEX promotor.idx03_pro_docjur_empresaestado;
       promotor            postgres    false    342    342            �           1259    68831    idx03_proyecto_estact    INDEX     [   CREATE INDEX idx03_proyecto_estact ON promotor.mult_proyectos USING btree (estado_actual);
 +   DROP INDEX promotor.idx03_proyecto_estact;
       promotor            postgres    false    352            �           1259    68832    indx01_idempresaxcuenta    INDEX     a   CREATE INDEX indx01_idempresaxcuenta ON promotor.mult_empresas USING btree (id_empresa, cuenta);
 -   DROP INDEX promotor.indx01_idempresaxcuenta;
       promotor            postgres    false    346    346            �           1259    68833    indx01_idempresaxventas    INDEX     f   CREATE INDEX indx01_idempresaxventas ON promotor.mult_empresa_datos_anuales USING btree (id_empresa);
 -   DROP INDEX promotor.indx01_idempresaxventas;
       promotor            postgres    false    344            �           2606    68834 *   mult_auspicios fk1wnlwuxuqekvfm5ash28qjo0p    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_auspicios
    ADD CONSTRAINT fk1wnlwuxuqekvfm5ash28qjo0p FOREIGN KEY (beneficiario) REFERENCES auspicios.mult_beneficiario(id);
 W   ALTER TABLE ONLY auspicios.mult_auspicios DROP CONSTRAINT fk1wnlwuxuqekvfm5ash28qjo0p;
    	   auspicios          postgres    false    213    3815    222            �           2606    68839 *   mult_auspicios fk3qwgi3m1ni7573aoorbvbn58l    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_auspicios
    ADD CONSTRAINT fk3qwgi3m1ni7573aoorbvbn58l FOREIGN KEY (id_valoracion) REFERENCES auspicios.mult_auspicios_valoracion(id);
 W   ALTER TABLE ONLY auspicios.mult_auspicios DROP CONSTRAINT fk3qwgi3m1ni7573aoorbvbn58l;
    	   auspicios          postgres    false    213    3810    220            �           2606    68844 -   mult_beneficiario fk4lxq6yp5n8bi2mfewmp03xpgn    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_beneficiario
    ADD CONSTRAINT fk4lxq6yp5n8bi2mfewmp03xpgn FOREIGN KEY (modalidad) REFERENCES maestras_auspicios.mult_deport_modalidad(id);
 Z   ALTER TABLE ONLY auspicios.mult_beneficiario DROP CONSTRAINT fk4lxq6yp5n8bi2mfewmp03xpgn;
    	   auspicios          postgres    false    222    307    3954            "           2606    86567 2   ptn_titulos_deportivos fk5626tovhwscdjk5q1ixxfgfw6    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_titulos_deportivos
    ADD CONSTRAINT fk5626tovhwscdjk5q1ixxfgfw6 FOREIGN KEY (disciplina) REFERENCES maestras_auspicios.ptn_deport_disciplina(id);
 _   ALTER TABLE ONLY auspicios.ptn_titulos_deportivos DROP CONSTRAINT fk5626tovhwscdjk5q1ixxfgfw6;
    	   auspicios          postgres    false    386    4216    464                       2606    86552 ,   ptn_beneficiario fk5hh64fha8rrrc8mem5kgrjxus    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_beneficiario
    ADD CONSTRAINT fk5hh64fha8rrrc8mem5kgrjxus FOREIGN KEY (id_persona) REFERENCES cuenta.ptn_personas(identificacion);
 Y   ALTER TABLE ONLY auspicios.ptn_beneficiario DROP CONSTRAINT fk5hh64fha8rrrc8mem5kgrjxus;
    	   auspicios          postgres    false    4141    407    384            �           2606    68849 3   mult_titulos_deportivos fk82qpql3otm72e8cdcxemjj1ij    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_titulos_deportivos
    ADD CONSTRAINT fk82qpql3otm72e8cdcxemjj1ij FOREIGN KEY (disciplina) REFERENCES maestras_auspicios.mult_deport_disciplina(id);
 `   ALTER TABLE ONLY auspicios.mult_titulos_deportivos DROP CONSTRAINT fk82qpql3otm72e8cdcxemjj1ij;
    	   auspicios          postgres    false    223    3952    305            �           2606    68854 3   mult_titulos_deportivos fk83mc7c3yhc3ys3pwtw3m1cw8y    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_titulos_deportivos
    ADD CONSTRAINT fk83mc7c3yhc3ys3pwtw3m1cw8y FOREIGN KEY (beneficiario) REFERENCES auspicios.mult_beneficiario(id);
 `   ALTER TABLE ONLY auspicios.mult_titulos_deportivos DROP CONSTRAINT fk83mc7c3yhc3ys3pwtw3m1cw8y;
    	   auspicios          postgres    false    222    223    3815                       2606    86527 4   ptn_auspicios_valoracion fkc3hy8vgdjw36hemxvhkjittjt    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_auspicios_valoracion
    ADD CONSTRAINT fkc3hy8vgdjw36hemxvhkjittjt FOREIGN KEY (beneficiario) REFERENCES auspicios.ptn_beneficiario(id);
 a   ALTER TABLE ONLY auspicios.ptn_auspicios_valoracion DROP CONSTRAINT fkc3hy8vgdjw36hemxvhkjittjt;
    	   auspicios          postgres    false    384    383    4099            �           2606    68859 2   mult_auspicios_torneos fkc7q89u4puhd3dd1soau3i94hg    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_auspicios_torneos
    ADD CONSTRAINT fkc7q89u4puhd3dd1soau3i94hg FOREIGN KEY (auspicio) REFERENCES auspicios.mult_auspicios(id);
 _   ALTER TABLE ONLY auspicios.mult_auspicios_torneos DROP CONSTRAINT fkc7q89u4puhd3dd1soau3i94hg;
    	   auspicios          postgres    false    3798    213    218                       2606    86502 )   ptn_auspicios fkckk1c7144am1fg6l9nu1nr123    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_auspicios
    ADD CONSTRAINT fkckk1c7144am1fg6l9nu1nr123 FOREIGN KEY (estado) REFERENCES auspicios.ptn_auspicios_estados(id_estado);
 V   ALTER TABLE ONLY auspicios.ptn_auspicios DROP CONSTRAINT fkckk1c7144am1fg6l9nu1nr123;
    	   auspicios          postgres    false    377    4089    376            �           2606    68864 -   mult_beneficiario fkctaof0lfjai6mfshqj2vqg4ph    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_beneficiario
    ADD CONSTRAINT fkctaof0lfjai6mfshqj2vqg4ph FOREIGN KEY (id_persona) REFERENCES cuenta.mult_personas(identificacion);
 Z   ALTER TABLE ONLY auspicios.mult_beneficiario DROP CONSTRAINT fkctaof0lfjai6mfshqj2vqg4ph;
    	   auspicios          postgres    false    222    3859    245                       2606    86537 +   ptn_beneficiario fkfbejw8c2mi9srp55rf2wtrru    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_beneficiario
    ADD CONSTRAINT fkfbejw8c2mi9srp55rf2wtrru FOREIGN KEY (cuenta_bancaria) REFERENCES cuenta.ptn_pers_cuentas(id_pers_cuenta);
 X   ALTER TABLE ONLY auspicios.ptn_beneficiario DROP CONSTRAINT fkfbejw8c2mi9srp55rf2wtrru;
    	   auspicios          postgres    false    394    4119    384                       2606    86517 1   ptn_auspicios_torneos fkgssm3skrl9cy2g9hljhntq99j    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_auspicios_torneos
    ADD CONSTRAINT fkgssm3skrl9cy2g9hljhntq99j FOREIGN KEY (auspicio) REFERENCES auspicios.ptn_auspicios(id);
 ^   ALTER TABLE ONLY auspicios.ptn_auspicios_torneos DROP CONSTRAINT fkgssm3skrl9cy2g9hljhntq99j;
    	   auspicios          postgres    false    4087    381    376            #           2606    86562 2   ptn_titulos_deportivos fkh0jrxeog8h8vp51xwn6xt7o4t    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_titulos_deportivos
    ADD CONSTRAINT fkh0jrxeog8h8vp51xwn6xt7o4t FOREIGN KEY (beneficiario) REFERENCES auspicios.ptn_beneficiario(id);
 _   ALTER TABLE ONLY auspicios.ptn_titulos_deportivos DROP CONSTRAINT fkh0jrxeog8h8vp51xwn6xt7o4t;
    	   auspicios          postgres    false    386    4099    384                       2606    86547 ,   ptn_beneficiario fkj1u0ha9i05g39urb15v2q774d    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_beneficiario
    ADD CONSTRAINT fkj1u0ha9i05g39urb15v2q774d FOREIGN KEY (modalidad) REFERENCES maestras_auspicios.ptn_deport_modalidad(id);
 Y   ALTER TABLE ONLY auspicios.ptn_beneficiario DROP CONSTRAINT fkj1u0ha9i05g39urb15v2q774d;
    	   auspicios          postgres    false    466    384    4218                       2606    86532 ,   ptn_beneficiario fkkamqfc2gfusp1v40m026onq3s    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_beneficiario
    ADD CONSTRAINT fkkamqfc2gfusp1v40m026onq3s FOREIGN KEY (categoria) REFERENCES maestras_auspicios.ptn_deport_categorias(id);
 Y   ALTER TABLE ONLY auspicios.ptn_beneficiario DROP CONSTRAINT fkkamqfc2gfusp1v40m026onq3s;
    	   auspicios          postgres    false    462    384    4214                       2606    86512 4   ptn_auspicios_recompesas fklrhqcfpwco0m6g8adyhm40pnd    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_auspicios_recompesas
    ADD CONSTRAINT fklrhqcfpwco0m6g8adyhm40pnd FOREIGN KEY (auspicio) REFERENCES auspicios.ptn_auspicios(id);
 a   ALTER TABLE ONLY auspicios.ptn_auspicios_recompesas DROP CONSTRAINT fklrhqcfpwco0m6g8adyhm40pnd;
    	   auspicios          postgres    false    376    4087    379            �           2606    68869 -   mult_beneficiario fkmrrasrw7ihw0en77pvj7wqbvt    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_beneficiario
    ADD CONSTRAINT fkmrrasrw7ihw0en77pvj7wqbvt FOREIGN KEY (categoria) REFERENCES maestras_auspicios.mult_deport_categorias(id);
 Z   ALTER TABLE ONLY auspicios.mult_beneficiario DROP CONSTRAINT fkmrrasrw7ihw0en77pvj7wqbvt;
    	   auspicios          postgres    false    303    3950    222            �           2606    68874 2   mult_auspicios_torneos fkn7k871wm5htsm8je40tsryvso    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_auspicios_torneos
    ADD CONSTRAINT fkn7k871wm5htsm8je40tsryvso FOREIGN KEY (pais) REFERENCES maestras.mult_nacionalidades(id_nacionalidad);
 _   ALTER TABLE ONLY auspicios.mult_auspicios_torneos DROP CONSTRAINT fkn7k871wm5htsm8je40tsryvso;
    	   auspicios          postgres    false    3932    291    218                        2606    86542 ,   ptn_beneficiario fknsy6sgn77cpoe37y4ftmfw4ig    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_beneficiario
    ADD CONSTRAINT fknsy6sgn77cpoe37y4ftmfw4ig FOREIGN KEY (disciplina) REFERENCES maestras_auspicios.ptn_deport_disciplina(id);
 Y   ALTER TABLE ONLY auspicios.ptn_beneficiario DROP CONSTRAINT fknsy6sgn77cpoe37y4ftmfw4ig;
    	   auspicios          postgres    false    384    4216    464                       2606    86497 )   ptn_auspicios fknwjw63y2c1kicjhykclqsbbx0    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_auspicios
    ADD CONSTRAINT fknwjw63y2c1kicjhykclqsbbx0 FOREIGN KEY (beneficiario) REFERENCES auspicios.ptn_beneficiario(id);
 V   ALTER TABLE ONLY auspicios.ptn_auspicios DROP CONSTRAINT fknwjw63y2c1kicjhykclqsbbx0;
    	   auspicios          postgres    false    384    4099    376            �           2606    68879 5   mult_auspicios_recompesas fkny3l4ys8d5a8jr6bbfs69rs4b    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_auspicios_recompesas
    ADD CONSTRAINT fkny3l4ys8d5a8jr6bbfs69rs4b FOREIGN KEY (auspicio) REFERENCES auspicios.mult_auspicios(id);
 b   ALTER TABLE ONLY auspicios.mult_auspicios_recompesas DROP CONSTRAINT fkny3l4ys8d5a8jr6bbfs69rs4b;
    	   auspicios          postgres    false    3798    213    216            �           2606    68884 5   mult_auspicios_valoracion fkohru21cnq9ohcudkgxcw1vsm7    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_auspicios_valoracion
    ADD CONSTRAINT fkohru21cnq9ohcudkgxcw1vsm7 FOREIGN KEY (beneficiario) REFERENCES auspicios.mult_beneficiario(id);
 b   ALTER TABLE ONLY auspicios.mult_auspicios_valoracion DROP CONSTRAINT fkohru21cnq9ohcudkgxcw1vsm7;
    	   auspicios          postgres    false    220    3815    222            �           2606    68889 )   mult_auspicios fkpfgj7qvx1dfs37sqt34skfwo    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_auspicios
    ADD CONSTRAINT fkpfgj7qvx1dfs37sqt34skfwo FOREIGN KEY (estado) REFERENCES auspicios.mult_auspicios_estados(id_estado);
 V   ALTER TABLE ONLY auspicios.mult_auspicios DROP CONSTRAINT fkpfgj7qvx1dfs37sqt34skfwo;
    	   auspicios          postgres    false    3800    214    213            !           2606    86557 ,   ptn_beneficiario fkq0g6dv80te1karih6pw1glyy2    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_beneficiario
    ADD CONSTRAINT fkq0g6dv80te1karih6pw1glyy2 FOREIGN KEY (id_representante) REFERENCES cuenta.ptn_personas(identificacion);
 Y   ALTER TABLE ONLY auspicios.ptn_beneficiario DROP CONSTRAINT fkq0g6dv80te1karih6pw1glyy2;
    	   auspicios          postgres    false    407    384    4141            �           2606    68894 -   mult_beneficiario fkrsweagnkm0ma7u0d1ell2u613    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_beneficiario
    ADD CONSTRAINT fkrsweagnkm0ma7u0d1ell2u613 FOREIGN KEY (id_representante) REFERENCES cuenta.mult_personas(identificacion);
 Z   ALTER TABLE ONLY auspicios.mult_beneficiario DROP CONSTRAINT fkrsweagnkm0ma7u0d1ell2u613;
    	   auspicios          postgres    false    245    222    3859                       2606    86507 )   ptn_auspicios fksn5pupsi80rpll8s84r4pnxad    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_auspicios
    ADD CONSTRAINT fksn5pupsi80rpll8s84r4pnxad FOREIGN KEY (id_valoracion) REFERENCES auspicios.ptn_auspicios_valoracion(id);
 V   ALTER TABLE ONLY auspicios.ptn_auspicios DROP CONSTRAINT fksn5pupsi80rpll8s84r4pnxad;
    	   auspicios          postgres    false    4097    376    383                       2606    86522 1   ptn_auspicios_torneos fksyn17b2wubbvlf66qroeba65p    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.ptn_auspicios_torneos
    ADD CONSTRAINT fksyn17b2wubbvlf66qroeba65p FOREIGN KEY (pais) REFERENCES maestras.ptn_nacionalidades(id_nacionalidad);
 ^   ALTER TABLE ONLY auspicios.ptn_auspicios_torneos DROP CONSTRAINT fksyn17b2wubbvlf66qroeba65p;
    	   auspicios          postgres    false    4199    381    452            �           2606    68899 ,   mult_beneficiario fkt4gqe8nuou1ruslcd7pa87v6    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_beneficiario
    ADD CONSTRAINT fkt4gqe8nuou1ruslcd7pa87v6 FOREIGN KEY (cuenta_bancaria) REFERENCES cuenta.mult_pers_cuentas(id_pers_cuenta);
 Y   ALTER TABLE ONLY auspicios.mult_beneficiario DROP CONSTRAINT fkt4gqe8nuou1ruslcd7pa87v6;
    	   auspicios          postgres    false    3838    222    231            �           2606    68904 -   mult_beneficiario fkt97csssa46qt9qchhyl7m7ti0    FK CONSTRAINT     �   ALTER TABLE ONLY auspicios.mult_beneficiario
    ADD CONSTRAINT fkt97csssa46qt9qchhyl7m7ti0 FOREIGN KEY (disciplina) REFERENCES maestras_auspicios.mult_deport_disciplina(id);
 Z   ALTER TABLE ONLY auspicios.mult_beneficiario DROP CONSTRAINT fkt97csssa46qt9qchhyl7m7ti0;
    	   auspicios          postgres    false    305    222    3952            9           2606    86682 -   ptn_roles_cuentas fk1vw9r045ds5wdyik62j127axl    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_roles_cuentas
    ADD CONSTRAINT fk1vw9r045ds5wdyik62j127axl FOREIGN KEY (rol) REFERENCES cuenta.ptn_roles(id_rol);
 W   ALTER TABLE ONLY cuenta.ptn_roles_cuentas DROP CONSTRAINT fk1vw9r045ds5wdyik62j127axl;
       cuenta          postgres    false    410    409    4143            /           2606    86662 3   ptn_pers_info_adicional fk2dv8sd7ilhk4dbfaguid9ix3u    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional
    ADD CONSTRAINT fk2dv8sd7ilhk4dbfaguid9ix3u FOREIGN KEY (usuario_creacion) REFERENCES cuenta.ptn_cuentas(id_cuenta);
 ]   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional DROP CONSTRAINT fk2dv8sd7ilhk4dbfaguid9ix3u;
       cuenta          postgres    false    387    404    4105            �           2606    68909 /   mult_menu_operacion fk3dbssdraqbv1g2dbo51mjy43v    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_menu_operacion
    ADD CONSTRAINT fk3dbssdraqbv1g2dbo51mjy43v FOREIGN KEY (id_operacion) REFERENCES cuenta.mult_operaciones(id_operacion);
 Y   ALTER TABLE ONLY cuenta.mult_menu_operacion DROP CONSTRAINT fk3dbssdraqbv1g2dbo51mjy43v;
       cuenta          postgres    false    229    228    3836            0           2606    86652 3   ptn_pers_info_adicional fk4a360vsika24bptsusndcnej9    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional
    ADD CONSTRAINT fk4a360vsika24bptsusndcnej9 FOREIGN KEY (id_persona) REFERENCES cuenta.ptn_personas(identificacion);
 ]   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional DROP CONSTRAINT fk4a360vsika24bptsusndcnej9;
       cuenta          postgres    false    404    407    4141            ;           2606    86687 %   ptn_token fk4ctxnejjx1b55wer3oxa66pw3    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_token
    ADD CONSTRAINT fk4ctxnejjx1b55wer3oxa66pw3 FOREIGN KEY (id_cuenta) REFERENCES cuenta.ptn_cuentas(id_cuenta);
 O   ALTER TABLE ONLY cuenta.ptn_token DROP CONSTRAINT fk4ctxnejjx1b55wer3oxa66pw3;
       cuenta          postgres    false    4105    412    387            -           2606    86622 .   ptn_pers_domicilios fk6ikk2yk76vis9d410q3k649l    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_domicilios
    ADD CONSTRAINT fk6ikk2yk76vis9d410q3k649l FOREIGN KEY (id_pais) REFERENCES maestras.ptn_nacionalidades(id_nacionalidad);
 X   ALTER TABLE ONLY cuenta.ptn_pers_domicilios DROP CONSTRAINT fk6ikk2yk76vis9d410q3k649l;
       cuenta          postgres    false    398    452    4199            �           2606    68914 4   mult_pers_info_adicional fk7tm439902vjic4fy2ha8pprmw    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_info_adicional
    ADD CONSTRAINT fk7tm439902vjic4fy2ha8pprmw FOREIGN KEY (id_repre_legal_jur) REFERENCES cuenta.mult_pers_repre_legal(id_repre_legal);
 ^   ALTER TABLE ONLY cuenta.mult_pers_info_adicional DROP CONSTRAINT fk7tm439902vjic4fy2ha8pprmw;
       cuenta          postgres    false    243    241    3853            )           2606    86602 ,   ptn_pers_cuentas fk8fs9cbgbs7jrkg8p1og1x7b81    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_cuentas
    ADD CONSTRAINT fk8fs9cbgbs7jrkg8p1og1x7b81 FOREIGN KEY (persona) REFERENCES cuenta.ptn_personas(identificacion);
 V   ALTER TABLE ONLY cuenta.ptn_pers_cuentas DROP CONSTRAINT fk8fs9cbgbs7jrkg8p1og1x7b81;
       cuenta          postgres    false    407    394    4141            �           2606    68919 -   mult_pers_cuentas fk9jro64uwpr1j8tcj6rel1o4s8    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_cuentas
    ADD CONSTRAINT fk9jro64uwpr1j8tcj6rel1o4s8 FOREIGN KEY (id_banco) REFERENCES maestras.mult_bancos(id_banco);
 W   ALTER TABLE ONLY cuenta.mult_pers_cuentas DROP CONSTRAINT fk9jro64uwpr1j8tcj6rel1o4s8;
       cuenta          postgres    false    285    231    3922            1           2606    86627 3   ptn_pers_info_adicional fk9pwbupbhoxwqp46cpjxiph0np    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional
    ADD CONSTRAINT fk9pwbupbhoxwqp46cpjxiph0np FOREIGN KEY (id_domicilio) REFERENCES cuenta.ptn_pers_domicilios(id_domicilio);
 ]   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional DROP CONSTRAINT fk9pwbupbhoxwqp46cpjxiph0np;
       cuenta          postgres    false    404    4125    398            2           2606    86657 3   ptn_pers_info_adicional fkajwylhsnen8qcycpaytp2r1s0    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional
    ADD CONSTRAINT fkajwylhsnen8qcycpaytp2r1s0 FOREIGN KEY (id_doc_identificacion) REFERENCES cuenta.ptn_pers_documentos(id_documento);
 ]   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional DROP CONSTRAINT fkajwylhsnen8qcycpaytp2r1s0;
       cuenta          postgres    false    404    396    4123            �           2606    68924 &   mult_token fkaktb0ixkw0ed3db7xkbotr1ks    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_token
    ADD CONSTRAINT fkaktb0ixkw0ed3db7xkbotr1ks FOREIGN KEY (id_cuenta) REFERENCES cuenta.mult_cuentas(id_cuenta);
 P   ALTER TABLE ONLY cuenta.mult_token DROP CONSTRAINT fkaktb0ixkw0ed3db7xkbotr1ks;
       cuenta          postgres    false    3824    250    225            �           2606    68929 .   mult_roles_cuentas fkaofxijpvp5ikkrf60hncc27ry    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_roles_cuentas
    ADD CONSTRAINT fkaofxijpvp5ikkrf60hncc27ry FOREIGN KEY (rol) REFERENCES cuenta.mult_roles(id_rol);
 X   ALTER TABLE ONLY cuenta.mult_roles_cuentas DROP CONSTRAINT fkaofxijpvp5ikkrf60hncc27ry;
       cuenta          postgres    false    3862    247    246            �           2606    68934 0   mult_pers_domicilios fkbbfp0kdrruqoienrjmnupq1qs    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_domicilios
    ADD CONSTRAINT fkbbfp0kdrruqoienrjmnupq1qs FOREIGN KEY (id_ciudad) REFERENCES maestras.mult_ciudades(id_ciudad);
 Z   ALTER TABLE ONLY cuenta.mult_pers_domicilios DROP CONSTRAINT fkbbfp0kdrruqoienrjmnupq1qs;
       cuenta          postgres    false    3924    287    235            .           2606    86617 /   ptn_pers_domicilios fkbsbosvrbrvay6gfc058gq23mo    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_domicilios
    ADD CONSTRAINT fkbsbosvrbrvay6gfc058gq23mo FOREIGN KEY (id_ciudad) REFERENCES maestras.ptn_ciudades(id_ciudad);
 Y   ALTER TABLE ONLY cuenta.ptn_pers_domicilios DROP CONSTRAINT fkbsbosvrbrvay6gfc058gq23mo;
       cuenta          postgres    false    398    448    4193            �           2606    68939 4   mult_pers_info_adicional fkbu1elemh54tn4y2yoqvaprvft    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_info_adicional
    ADD CONSTRAINT fkbu1elemh54tn4y2yoqvaprvft FOREIGN KEY (id_domicilio) REFERENCES cuenta.mult_pers_domicilios(id_domicilio);
 ^   ALTER TABLE ONLY cuenta.mult_pers_info_adicional DROP CONSTRAINT fkbu1elemh54tn4y2yoqvaprvft;
       cuenta          postgres    false    3842    241    235            &           2606    86592 .   ptn_menu_operacion fkbwbq12mp45xr53gjd5j6mqrnd    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_menu_operacion
    ADD CONSTRAINT fkbwbq12mp45xr53gjd5j6mqrnd FOREIGN KEY (id_rol) REFERENCES cuenta.ptn_roles(id_rol);
 X   ALTER TABLE ONLY cuenta.ptn_menu_operacion DROP CONSTRAINT fkbwbq12mp45xr53gjd5j6mqrnd;
       cuenta          postgres    false    390    4143    409            �           2606    68944 4   mult_pers_info_adicional fkcm7cti9tke6437nbf9rn8jeo8    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_info_adicional
    ADD CONSTRAINT fkcm7cti9tke6437nbf9rn8jeo8 FOREIGN KEY (id_tipo_cuenta) REFERENCES cuenta.mult_pers_cuentas(id_pers_cuenta);
 ^   ALTER TABLE ONLY cuenta.mult_pers_info_adicional DROP CONSTRAINT fkcm7cti9tke6437nbf9rn8jeo8;
       cuenta          postgres    false    3838    231    241            �           2606    68949 4   mult_pers_info_adicional fkcnq0gp620qgbt1t5awn62kgx0    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_info_adicional
    ADD CONSTRAINT fkcnq0gp620qgbt1t5awn62kgx0 FOREIGN KEY (id_persona) REFERENCES cuenta.mult_personas(identificacion);
 ^   ALTER TABLE ONLY cuenta.mult_pers_info_adicional DROP CONSTRAINT fkcnq0gp620qgbt1t5awn62kgx0;
       cuenta          postgres    false    3859    245    241            �           2606    68954 %   mult_menu fkd1gik4fpslhjd0376bro41shm    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_menu
    ADD CONSTRAINT fkd1gik4fpslhjd0376bro41shm FOREIGN KEY (id_padre) REFERENCES cuenta.mult_menu(id_menu);
 O   ALTER TABLE ONLY cuenta.mult_menu DROP CONSTRAINT fkd1gik4fpslhjd0376bro41shm;
       cuenta          postgres    false    226    3830    226            +           2606    86612 /   ptn_pers_documentos fkdcdaojjic6c28h8jyl1ibs249    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_documentos
    ADD CONSTRAINT fkdcdaojjic6c28h8jyl1ibs249 FOREIGN KEY (id_info_adicional) REFERENCES cuenta.ptn_pers_info_adicional(id_info_adicional);
 Y   ALTER TABLE ONLY cuenta.ptn_pers_documentos DROP CONSTRAINT fkdcdaojjic6c28h8jyl1ibs249;
       cuenta          postgres    false    404    396    4133            3           2606    86642 3   ptn_pers_info_adicional fkdckafq1q6qhrvuxllnmgbmf6w    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional
    ADD CONSTRAINT fkdckafq1q6qhrvuxllnmgbmf6w FOREIGN KEY (id_repre_legal_jur) REFERENCES cuenta.ptn_pers_repre_legal(id_repre_legal);
 ]   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional DROP CONSTRAINT fkdckafq1q6qhrvuxllnmgbmf6w;
       cuenta          postgres    false    404    4137    406            �           2606    68959 0   mult_pers_domicilios fkdrs0ywpq6we90fgpk02clhm0m    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_domicilios
    ADD CONSTRAINT fkdrs0ywpq6we90fgpk02clhm0m FOREIGN KEY (id_pais) REFERENCES maestras.mult_nacionalidades(id_nacionalidad);
 Z   ALTER TABLE ONLY cuenta.mult_pers_domicilios DROP CONSTRAINT fkdrs0ywpq6we90fgpk02clhm0m;
       cuenta          postgres    false    235    291    3932            �           2606    68964 4   mult_pers_info_adicional fkdshhnpvr4o2lc18sco9t8vhbh    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_info_adicional
    ADD CONSTRAINT fkdshhnpvr4o2lc18sco9t8vhbh FOREIGN KEY (usuario_creacion) REFERENCES cuenta.mult_cuentas(id_cuenta);
 ^   ALTER TABLE ONLY cuenta.mult_pers_info_adicional DROP CONSTRAINT fkdshhnpvr4o2lc18sco9t8vhbh;
       cuenta          postgres    false    225    241    3824            �           2606    68969 .   mult_roles_cuentas fke448420r7qrogt7gnum825gub    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_roles_cuentas
    ADD CONSTRAINT fke448420r7qrogt7gnum825gub FOREIGN KEY (cuenta_interna) REFERENCES multiplo.mult_cuentas_internas(id_cuenta);
 X   ALTER TABLE ONLY cuenta.mult_roles_cuentas DROP CONSTRAINT fke448420r7qrogt7gnum825gub;
       cuenta          postgres    false    309    3958    247            4           2606    86637 3   ptn_pers_info_adicional fke6wn0advbqaehbb8rxuqedf1p    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional
    ADD CONSTRAINT fke6wn0advbqaehbb8rxuqedf1p FOREIGN KEY (id_firma_jur) REFERENCES cuenta.ptn_pers_firmas(id_firma);
 ]   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional DROP CONSTRAINT fke6wn0advbqaehbb8rxuqedf1p;
       cuenta          postgres    false    402    404    4129            5           2606    86647 3   ptn_pers_info_adicional fkeklk8pfkfckvlf5qctyux9jc2    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional
    ADD CONSTRAINT fkeklk8pfkfckvlf5qctyux9jc2 FOREIGN KEY (id_tipo_cuenta) REFERENCES cuenta.ptn_pers_cuentas(id_pers_cuenta);
 ]   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional DROP CONSTRAINT fkeklk8pfkfckvlf5qctyux9jc2;
       cuenta          postgres    false    404    394    4119            �           2606    68974 -   mult_pers_cuentas fkepqi6vdan0s0mhx60fa5s8mmo    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_cuentas
    ADD CONSTRAINT fkepqi6vdan0s0mhx60fa5s8mmo FOREIGN KEY (persona) REFERENCES cuenta.mult_personas(identificacion);
 W   ALTER TABLE ONLY cuenta.mult_pers_cuentas DROP CONSTRAINT fkepqi6vdan0s0mhx60fa5s8mmo;
       cuenta          postgres    false    231    245    3859            �           2606    68979 0   mult_pers_documentos fkfaa0po7yghwpgwx1cf3tu8t4n    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_documentos
    ADD CONSTRAINT fkfaa0po7yghwpgwx1cf3tu8t4n FOREIGN KEY (id_info_adicional) REFERENCES cuenta.mult_pers_info_adicional(id_info_adicional);
 Z   ALTER TABLE ONLY cuenta.mult_pers_documentos DROP CONSTRAINT fkfaa0po7yghwpgwx1cf3tu8t4n;
       cuenta          postgres    false    3851    233    241            *           2606    86597 ,   ptn_pers_cuentas fkfao4fou0xewbxrbcofse11nci    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_cuentas
    ADD CONSTRAINT fkfao4fou0xewbxrbcofse11nci FOREIGN KEY (id_banco) REFERENCES maestras.ptn_bancos(id_banco);
 V   ALTER TABLE ONLY cuenta.ptn_pers_cuentas DROP CONSTRAINT fkfao4fou0xewbxrbcofse11nci;
       cuenta          postgres    false    394    446    4191            �           2606    68984 4   mult_pers_info_adicional fkfbwb9ys3io61mmtx00456exrl    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_info_adicional
    ADD CONSTRAINT fkfbwb9ys3io61mmtx00456exrl FOREIGN KEY (id_est_finan_jur) REFERENCES cuenta.mult_pers_est_finan(id_est_finan);
 ^   ALTER TABLE ONLY cuenta.mult_pers_info_adicional DROP CONSTRAINT fkfbwb9ys3io61mmtx00456exrl;
       cuenta          postgres    false    237    241    3844            �           2606    68989 .   mult_roles_cuentas fkfom3xd1398mmn6byagx8t56ao    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_roles_cuentas
    ADD CONSTRAINT fkfom3xd1398mmn6byagx8t56ao FOREIGN KEY (cuenta) REFERENCES cuenta.mult_cuentas(id_cuenta);
 X   ALTER TABLE ONLY cuenta.mult_roles_cuentas DROP CONSTRAINT fkfom3xd1398mmn6byagx8t56ao;
       cuenta          postgres    false    225    247    3824            :           2606    86677 -   ptn_roles_cuentas fkg0e12n4l1k1uskaphqur232ve    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_roles_cuentas
    ADD CONSTRAINT fkg0e12n4l1k1uskaphqur232ve FOREIGN KEY (cuenta) REFERENCES cuenta.ptn_cuentas(id_cuenta);
 W   ALTER TABLE ONLY cuenta.ptn_roles_cuentas DROP CONSTRAINT fkg0e12n4l1k1uskaphqur232ve;
       cuenta          postgres    false    410    387    4105            8           2606    86672 0   ptn_pers_repre_legal fkhqix9rcqta96gu1vqvvnjd7xo    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_repre_legal
    ADD CONSTRAINT fkhqix9rcqta96gu1vqvvnjd7xo FOREIGN KEY (id_pais) REFERENCES maestras.ptn_nacionalidades(id_nacionalidad);
 Z   ALTER TABLE ONLY cuenta.ptn_pers_repre_legal DROP CONSTRAINT fkhqix9rcqta96gu1vqvvnjd7xo;
       cuenta          postgres    false    452    406    4199            %           2606    86577 $   ptn_menu fkiad7oni02dqu6dqsoj0ikp41f    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_menu
    ADD CONSTRAINT fkiad7oni02dqu6dqsoj0ikp41f FOREIGN KEY (id_padre) REFERENCES cuenta.ptn_menu(id_menu);
 N   ALTER TABLE ONLY cuenta.ptn_menu DROP CONSTRAINT fkiad7oni02dqu6dqsoj0ikp41f;
       cuenta          postgres    false    389    389    4111            6           2606    86667 3   ptn_pers_info_adicional fkiahtgayhowyvhqbnrpkpvp1w1    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional
    ADD CONSTRAINT fkiahtgayhowyvhqbnrpkpvp1w1 FOREIGN KEY (usuario_modificacion) REFERENCES cuenta.ptn_cuentas(id_cuenta);
 ]   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional DROP CONSTRAINT fkiahtgayhowyvhqbnrpkpvp1w1;
       cuenta          postgres    false    4105    387    404            '           2606    86587 .   ptn_menu_operacion fkkce8ttrlyq9joca4ep4bqkxwx    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_menu_operacion
    ADD CONSTRAINT fkkce8ttrlyq9joca4ep4bqkxwx FOREIGN KEY (id_operacion) REFERENCES cuenta.ptn_operaciones(id_operacion);
 X   ALTER TABLE ONLY cuenta.ptn_menu_operacion DROP CONSTRAINT fkkce8ttrlyq9joca4ep4bqkxwx;
       cuenta          postgres    false    4117    390    392            �           2606    68994 .   mult_roles_cuentas fkl7ny5phpx14y69v58wtt98mqh    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_roles_cuentas
    ADD CONSTRAINT fkl7ny5phpx14y69v58wtt98mqh FOREIGN KEY (usuari_creacion) REFERENCES multiplo.mult_cuentas_internas(id_cuenta);
 X   ALTER TABLE ONLY cuenta.mult_roles_cuentas DROP CONSTRAINT fkl7ny5phpx14y69v58wtt98mqh;
       cuenta          postgres    false    3958    309    247            (           2606    86582 .   ptn_menu_operacion fklhrjcs9dhysbuvy7kf3uhe0ng    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_menu_operacion
    ADD CONSTRAINT fklhrjcs9dhysbuvy7kf3uhe0ng FOREIGN KEY (id_menu) REFERENCES cuenta.ptn_menu(id_menu);
 X   ALTER TABLE ONLY cuenta.ptn_menu_operacion DROP CONSTRAINT fklhrjcs9dhysbuvy7kf3uhe0ng;
       cuenta          postgres    false    4111    389    390            �           2606    68999 (   mult_cuentas fkmdissqlw0pvkmt7jhp7vsjyyy    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_cuentas
    ADD CONSTRAINT fkmdissqlw0pvkmt7jhp7vsjyyy FOREIGN KEY (id_persona) REFERENCES cuenta.mult_personas(identificacion);
 R   ALTER TABLE ONLY cuenta.mult_cuentas DROP CONSTRAINT fkmdissqlw0pvkmt7jhp7vsjyyy;
       cuenta          postgres    false    245    225    3859            �           2606    69004 4   mult_pers_info_adicional fkn3mubcrsdoojkp0qlcrv986lk    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_info_adicional
    ADD CONSTRAINT fkn3mubcrsdoojkp0qlcrv986lk FOREIGN KEY (usuario_modificacion) REFERENCES cuenta.mult_cuentas(id_cuenta);
 ^   ALTER TABLE ONLY cuenta.mult_pers_info_adicional DROP CONSTRAINT fkn3mubcrsdoojkp0qlcrv986lk;
       cuenta          postgres    false    3824    241    225            �           2606    69009 /   mult_menu_operacion fkp0jhj477bplr2boubuhnw5g82    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_menu_operacion
    ADD CONSTRAINT fkp0jhj477bplr2boubuhnw5g82 FOREIGN KEY (id_menu) REFERENCES cuenta.mult_menu(id_menu);
 Y   ALTER TABLE ONLY cuenta.mult_menu_operacion DROP CONSTRAINT fkp0jhj477bplr2boubuhnw5g82;
       cuenta          postgres    false    3830    226    228            �           2606    69014 4   mult_pers_info_adicional fkp8ih4xrl5onk4xutkms6o2spj    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_info_adicional
    ADD CONSTRAINT fkp8ih4xrl5onk4xutkms6o2spj FOREIGN KEY (id_doc_identificacion) REFERENCES cuenta.mult_pers_documentos(id_documento);
 ^   ALTER TABLE ONLY cuenta.mult_pers_info_adicional DROP CONSTRAINT fkp8ih4xrl5onk4xutkms6o2spj;
       cuenta          postgres    false    3840    241    233            7           2606    86632 3   ptn_pers_info_adicional fkpwf9pa0emtw5gl399opfi1vss    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional
    ADD CONSTRAINT fkpwf9pa0emtw5gl399opfi1vss FOREIGN KEY (id_est_finan_jur) REFERENCES cuenta.ptn_pers_est_finan(id_est_finan);
 ]   ALTER TABLE ONLY cuenta.ptn_pers_info_adicional DROP CONSTRAINT fkpwf9pa0emtw5gl399opfi1vss;
       cuenta          postgres    false    404    400    4127            �           2606    69019 4   mult_pers_info_adicional fkql83uof903op4t7tok7jokro1    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_info_adicional
    ADD CONSTRAINT fkql83uof903op4t7tok7jokro1 FOREIGN KEY (id_firma_jur) REFERENCES cuenta.mult_pers_firmas(id_firma);
 ^   ALTER TABLE ONLY cuenta.mult_pers_info_adicional DROP CONSTRAINT fkql83uof903op4t7tok7jokro1;
       cuenta          postgres    false    239    241    3846            �           2606    69024 1   mult_pers_repre_legal fkrdmca45rgyu216ea0kod6c2qv    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_repre_legal
    ADD CONSTRAINT fkrdmca45rgyu216ea0kod6c2qv FOREIGN KEY (id_pais) REFERENCES maestras.mult_nacionalidades(id_nacionalidad);
 [   ALTER TABLE ONLY cuenta.mult_pers_repre_legal DROP CONSTRAINT fkrdmca45rgyu216ea0kod6c2qv;
       cuenta          postgres    false    291    243    3932            �           2606    69029 0   mult_pers_documentos fkrp9toyd664m7mnqv8cm1duskh    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_pers_documentos
    ADD CONSTRAINT fkrp9toyd664m7mnqv8cm1duskh FOREIGN KEY (id_tipo_documento) REFERENCES maestras.mult_tipo_documentos(id_tipo_documento);
 Z   ALTER TABLE ONLY cuenta.mult_pers_documentos DROP CONSTRAINT fkrp9toyd664m7mnqv8cm1duskh;
       cuenta          postgres    false    233    295    3936            ,           2606    86607 /   ptn_pers_documentos fkrywh3x2cl2rb2ql2lu2sh5m6y    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_pers_documentos
    ADD CONSTRAINT fkrywh3x2cl2rb2ql2lu2sh5m6y FOREIGN KEY (id_tipo_documento) REFERENCES maestras.ptn_tipo_documentos(id_tipo_documento);
 Y   ALTER TABLE ONLY cuenta.ptn_pers_documentos DROP CONSTRAINT fkrywh3x2cl2rb2ql2lu2sh5m6y;
       cuenta          postgres    false    4204    456    396            �           2606    69034 .   mult_menu_operacion fksvm7ky4xqe00lhl7ch3ry57k    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.mult_menu_operacion
    ADD CONSTRAINT fksvm7ky4xqe00lhl7ch3ry57k FOREIGN KEY (id_rol) REFERENCES cuenta.mult_roles(id_rol);
 X   ALTER TABLE ONLY cuenta.mult_menu_operacion DROP CONSTRAINT fksvm7ky4xqe00lhl7ch3ry57k;
       cuenta          postgres    false    228    246    3862            $           2606    86572 '   ptn_cuentas fktcwxwc1myybs7fi5h1pb72al0    FK CONSTRAINT     �   ALTER TABLE ONLY cuenta.ptn_cuentas
    ADD CONSTRAINT fktcwxwc1myybs7fi5h1pb72al0 FOREIGN KEY (id_persona) REFERENCES cuenta.ptn_personas(identificacion);
 Q   ALTER TABLE ONLY cuenta.ptn_cuentas DROP CONSTRAINT fktcwxwc1myybs7fi5h1pb72al0;
       cuenta          postgres    false    4141    387    407            �           2606    69039 -   mult_form_contact fk3po2k2vwldw1jhjsq1rwm19fd    FK CONSTRAINT     �   ALTER TABLE ONLY footer.mult_form_contact
    ADD CONSTRAINT fk3po2k2vwldw1jhjsq1rwm19fd FOREIGN KEY (estado_anterior) REFERENCES inversion.mult_tipo_estados(id_estado);
 W   ALTER TABLE ONLY footer.mult_form_contact DROP CONSTRAINT fk3po2k2vwldw1jhjsq1rwm19fd;
       footer          postgres    false    255    3912    282            �           2606    69044 -   mult_form_contact fk4su0phupmdmbr2id6vt6llo0q    FK CONSTRAINT     �   ALTER TABLE ONLY footer.mult_form_contact
    ADD CONSTRAINT fk4su0phupmdmbr2id6vt6llo0q FOREIGN KEY (estado_actual) REFERENCES inversion.mult_tipo_estados(id_estado);
 W   ALTER TABLE ONLY footer.mult_form_contact DROP CONSTRAINT fk4su0phupmdmbr2id6vt6llo0q;
       footer          postgres    false    3912    282    255            <           2606    86692 ,   ptn_form_contact fka6w1mlnf59qo5ggswtlboh9w9    FK CONSTRAINT     �   ALTER TABLE ONLY footer.ptn_form_contact
    ADD CONSTRAINT fka6w1mlnf59qo5ggswtlboh9w9 FOREIGN KEY (estado_actual) REFERENCES inversion.ptn_tipo_estados(id_estado);
 V   ALTER TABLE ONLY footer.ptn_form_contact DROP CONSTRAINT fka6w1mlnf59qo5ggswtlboh9w9;
       footer          postgres    false    442    417    4185            =           2606    86697 ,   ptn_form_contact fko5gul7yiuve1a82mma3knpjgx    FK CONSTRAINT     �   ALTER TABLE ONLY footer.ptn_form_contact
    ADD CONSTRAINT fko5gul7yiuve1a82mma3knpjgx FOREIGN KEY (estado_anterior) REFERENCES inversion.ptn_tipo_estados(id_estado);
 V   ALTER TABLE ONLY footer.ptn_form_contact DROP CONSTRAINT fko5gul7yiuve1a82mma3knpjgx;
       footer          postgres    false    417    4185    442            A           2606    86717 6   ptn_historial_conciliacion fk1k41shidfynrfdwunwqrncpoj    FK CONSTRAINT     �   ALTER TABLE ONLY historicas.ptn_historial_conciliacion
    ADD CONSTRAINT fk1k41shidfynrfdwunwqrncpoj FOREIGN KEY (id_file) REFERENCES negocio.ptn_conciliacion_xls(id);
 d   ALTER TABLE ONLY historicas.ptn_historial_conciliacion DROP CONSTRAINT fk1k41shidfynrfdwunwqrncpoj;
    
   historicas          postgres    false    425    4226    474            �           2606    69049 7   mult_historial_conciliacion fk50vbfeh8gmw93xqi0vtnhni9o    FK CONSTRAINT     �   ALTER TABLE ONLY historicas.mult_historial_conciliacion
    ADD CONSTRAINT fk50vbfeh8gmw93xqi0vtnhni9o FOREIGN KEY (id_file) REFERENCES negocio.mult_conciliacion_xls(id);
 e   ALTER TABLE ONLY historicas.mult_historial_conciliacion DROP CONSTRAINT fk50vbfeh8gmw93xqi0vtnhni9o;
    
   historicas          postgres    false    330    263    4009            �           2606    69054 /   mult_hist_solicitud fk66v2e4hpvdngaxl7dfvw4wbky    FK CONSTRAINT     �   ALTER TABLE ONLY historicas.mult_hist_solicitud
    ADD CONSTRAINT fk66v2e4hpvdngaxl7dfvw4wbky FOREIGN KEY (usuario_modificacion_interno) REFERENCES multiplo.mult_cuentas_internas(id_cuenta);
 ]   ALTER TABLE ONLY historicas.mult_hist_solicitud DROP CONSTRAINT fk66v2e4hpvdngaxl7dfvw4wbky;
    
   historicas          postgres    false    3958    309    261            �           2606    69059 .   mult_hist_proyecto fk76qvmslk6m8hs0o9wpqtp6lgy    FK CONSTRAINT     �   ALTER TABLE ONLY historicas.mult_hist_proyecto
    ADD CONSTRAINT fk76qvmslk6m8hs0o9wpqtp6lgy FOREIGN KEY (id_proyecto) REFERENCES promotor.mult_proyectos(id_proyecto);
 \   ALTER TABLE ONLY historicas.mult_hist_proyecto DROP CONSTRAINT fk76qvmslk6m8hs0o9wpqtp6lgy;
    
   historicas          postgres    false    352    4050    259            �           2606    69064 ?   mult_historial_conciliacion_detalle fkc1329s95rfn75098mh30vxb4d    FK CONSTRAINT     �   ALTER TABLE ONLY historicas.mult_historial_conciliacion_detalle
    ADD CONSTRAINT fkc1329s95rfn75098mh30vxb4d FOREIGN KEY (id_historial) REFERENCES historicas.mult_historial_conciliacion(id);
 m   ALTER TABLE ONLY historicas.mult_historial_conciliacion_detalle DROP CONSTRAINT fkc1329s95rfn75098mh30vxb4d;
    
   historicas          postgres    false    264    263    3882            ?           2606    86707 .   ptn_hist_solicitud fkfru8ib5pykumadmk5jy2ie02f    FK CONSTRAINT     �   ALTER TABLE ONLY historicas.ptn_hist_solicitud
    ADD CONSTRAINT fkfru8ib5pykumadmk5jy2ie02f FOREIGN KEY (solicitud) REFERENCES inversion.ptn_solicitudes(numero_solicitud);
 \   ALTER TABLE ONLY historicas.ptn_hist_solicitud DROP CONSTRAINT fkfru8ib5pykumadmk5jy2ie02f;
    
   historicas          postgres    false    423    4179    437            �           2606    69069 /   mult_hist_solicitud fkglp59f216sl9b80w04bajo6ix    FK CONSTRAINT     �   ALTER TABLE ONLY historicas.mult_hist_solicitud
    ADD CONSTRAINT fkglp59f216sl9b80w04bajo6ix FOREIGN KEY (usuario_modificacion) REFERENCES cuenta.mult_cuentas(id_cuenta);
 ]   ALTER TABLE ONLY historicas.mult_hist_solicitud DROP CONSTRAINT fkglp59f216sl9b80w04bajo6ix;
    
   historicas          postgres    false    225    3824    261            �           2606    69074 /   mult_hist_solicitud fkkdq76bcxshbkdw7fikxjuy4od    FK CONSTRAINT     �   ALTER TABLE ONLY historicas.mult_hist_solicitud
    ADD CONSTRAINT fkkdq76bcxshbkdw7fikxjuy4od FOREIGN KEY (solicitud) REFERENCES inversion.mult_solicitudes(numero_solicitud);
 ]   ALTER TABLE ONLY historicas.mult_hist_solicitud DROP CONSTRAINT fkkdq76bcxshbkdw7fikxjuy4od;
    
   historicas          postgres    false    261    3904    275            @           2606    86712 .   ptn_hist_solicitud fkm1wr0mwn60jdnmi6r9lrtic50    FK CONSTRAINT     �   ALTER TABLE ONLY historicas.ptn_hist_solicitud
    ADD CONSTRAINT fkm1wr0mwn60jdnmi6r9lrtic50 FOREIGN KEY (usuario_modificacion) REFERENCES cuenta.ptn_cuentas(id_cuenta);
 \   ALTER TABLE ONLY historicas.ptn_hist_solicitud DROP CONSTRAINT fkm1wr0mwn60jdnmi6r9lrtic50;
    
   historicas          postgres    false    387    423    4105            B           2606    86722 >   ptn_historial_conciliacion_detalle fkp14xtvndjg67dbudc3wcrv5k2    FK CONSTRAINT     �   ALTER TABLE ONLY historicas.ptn_historial_conciliacion_detalle
    ADD CONSTRAINT fkp14xtvndjg67dbudc3wcrv5k2 FOREIGN KEY (id_historial) REFERENCES historicas.ptn_historial_conciliacion(id);
 l   ALTER TABLE ONLY historicas.ptn_historial_conciliacion_detalle DROP CONSTRAINT fkp14xtvndjg67dbudc3wcrv5k2;
    
   historicas          postgres    false    427    4167    425            >           2606    86702 ,   ptn_hist_proyecto fkybibtf333nt9kntdiltqo6kj    FK CONSTRAINT     �   ALTER TABLE ONLY historicas.ptn_hist_proyecto
    ADD CONSTRAINT fkybibtf333nt9kntdiltqo6kj FOREIGN KEY (id_proyecto) REFERENCES promotor.ptn_proyectos(id_proyecto);
 Z   ALTER TABLE ONLY historicas.ptn_hist_proyecto DROP CONSTRAINT fkybibtf333nt9kntdiltqo6kj;
    
   historicas          postgres    false    4252    421    495            �           2606    69079 .   mult_doc_aceptados fk19es28eem392tgewc36w82yfi    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_doc_aceptados
    ADD CONSTRAINT fk19es28eem392tgewc36w82yfi FOREIGN KEY (id_tipo_documento) REFERENCES maestras.mult_tipo_documentos(id_tipo_documento);
 [   ALTER TABLE ONLY inversion.mult_doc_aceptados DROP CONSTRAINT fk19es28eem392tgewc36w82yfi;
    	   inversion          postgres    false    273    3936    295            �           2606    69084 ,   mult_solicitudes fk1lkxgf93jljnf8njchoop7ng5    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_solicitudes
    ADD CONSTRAINT fk1lkxgf93jljnf8njchoop7ng5 FOREIGN KEY (id_tipo_solicitud) REFERENCES maestras.mult_tipo_solicitud(id);
 Y   ALTER TABLE ONLY inversion.mult_solicitudes DROP CONSTRAINT fk1lkxgf93jljnf8njchoop7ng5;
    	   inversion          postgres    false    297    275    3938            �           2606    69089 ,   mult_solicitudes fk1qfdnj30l9pqrxsq6gx2vq7bn    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_solicitudes
    ADD CONSTRAINT fk1qfdnj30l9pqrxsq6gx2vq7bn FOREIGN KEY (usuario_creacion) REFERENCES cuenta.mult_cuentas(id_cuenta);
 Y   ALTER TABLE ONLY inversion.mult_solicitudes DROP CONSTRAINT fk1qfdnj30l9pqrxsq6gx2vq7bn;
    	   inversion          postgres    false    275    3824    225            �           2606    69094 .   mult_doc_aceptados fk1txyr03uk8re50sbg3xp200y2    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_doc_aceptados
    ADD CONSTRAINT fk1txyr03uk8re50sbg3xp200y2 FOREIGN KEY (identificacion) REFERENCES cuenta.mult_personas(identificacion);
 [   ALTER TABLE ONLY inversion.mult_doc_aceptados DROP CONSTRAINT fk1txyr03uk8re50sbg3xp200y2;
    	   inversion          postgres    false    245    273    3859            S           2606    86827 -   ptn_transacciones fk2ra56avomras9id2mvdkbe2ew    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_transacciones
    ADD CONSTRAINT fk2ra56avomras9id2mvdkbe2ew FOREIGN KEY (usuario_creacion) REFERENCES cuenta.ptn_cuentas(id_cuenta);
 Z   ALTER TABLE ONLY inversion.ptn_transacciones DROP CONSTRAINT fk2ra56avomras9id2mvdkbe2ew;
    	   inversion          postgres    false    387    444    4105            Q           2606    86802 2   ptn_tabla_amortizacion fk34qow81eoix57xn5br2ep86rq    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_tabla_amortizacion
    ADD CONSTRAINT fk34qow81eoix57xn5br2ep86rq FOREIGN KEY (usuario_creacion) REFERENCES cuenta.ptn_cuentas(id_cuenta);
 _   ALTER TABLE ONLY inversion.ptn_tabla_amortizacion DROP CONSTRAINT fk34qow81eoix57xn5br2ep86rq;
    	   inversion          postgres    false    4105    387    441            �           2606    69099 5   mult_detalle_amortizacion fk466i3iuijo589psodyvv1ckgr    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_detalle_amortizacion
    ADD CONSTRAINT fk466i3iuijo589psodyvv1ckgr FOREIGN KEY (id_tbl_amortizacion) REFERENCES inversion.mult_tabla_amortizacion(id_tbl_amortizacion);
 b   ALTER TABLE ONLY inversion.mult_detalle_amortizacion DROP CONSTRAINT fk466i3iuijo589psodyvv1ckgr;
    	   inversion          postgres    false    3910    280    271            �           2606    69104 ,   mult_solicitudes fk62prhuvn5mf7dbcm75kq5r2gn    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_solicitudes
    ADD CONSTRAINT fk62prhuvn5mf7dbcm75kq5r2gn FOREIGN KEY (codigo_proyecto) REFERENCES promotor.mult_proyectos(id_proyecto);
 Y   ALTER TABLE ONLY inversion.mult_solicitudes DROP CONSTRAINT fk62prhuvn5mf7dbcm75kq5r2gn;
    	   inversion          postgres    false    352    275    4050            G           2606    86747 +   ptn_solicitudes fk6dmlehik7i0lw00swt0hc1yv7    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_solicitudes
    ADD CONSTRAINT fk6dmlehik7i0lw00swt0hc1yv7 FOREIGN KEY (tabla_amortizacion) REFERENCES inversion.ptn_tabla_amortizacion(id_tbl_amortizacion);
 X   ALTER TABLE ONLY inversion.ptn_solicitudes DROP CONSTRAINT fk6dmlehik7i0lw00swt0hc1yv7;
    	   inversion          postgres    false    437    441    4183            T           2606    86817 -   ptn_transacciones fk6hxx0secscsydsf2qq6h6pa88    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_transacciones
    ADD CONSTRAINT fk6hxx0secscsydsf2qq6h6pa88 FOREIGN KEY (proyecto) REFERENCES promotor.ptn_proyectos(id_proyecto);
 Z   ALTER TABLE ONLY inversion.ptn_transacciones DROP CONSTRAINT fk6hxx0secscsydsf2qq6h6pa88;
    	   inversion          postgres    false    495    444    4252            �           2606    69109 .   mult_transacciones fk6nrbasmcm493h312c4l2nsths    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_transacciones
    ADD CONSTRAINT fk6nrbasmcm493h312c4l2nsths FOREIGN KEY (usuario_creacion) REFERENCES cuenta.mult_cuentas(id_cuenta);
 [   ALTER TABLE ONLY inversion.mult_transacciones DROP CONSTRAINT fk6nrbasmcm493h312c4l2nsths;
    	   inversion          postgres    false    283    225    3824            �           2606    69114 3   mult_tabla_amortizacion fk73q82eygaj3ubpe6ictqot21w    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_tabla_amortizacion
    ADD CONSTRAINT fk73q82eygaj3ubpe6ictqot21w FOREIGN KEY (usuario_creacion) REFERENCES cuenta.mult_cuentas(id_cuenta);
 `   ALTER TABLE ONLY inversion.mult_tabla_amortizacion DROP CONSTRAINT fk73q82eygaj3ubpe6ictqot21w;
    	   inversion          postgres    false    3824    280    225            H           2606    86752 +   ptn_solicitudes fk81htsh35iw0wqtgpryjayk1yu    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_solicitudes
    ADD CONSTRAINT fk81htsh35iw0wqtgpryjayk1yu FOREIGN KEY (documentos) REFERENCES inversion.ptn_solicitudes_documentos(id_sol_documentos);
 X   ALTER TABLE ONLY inversion.ptn_solicitudes DROP CONSTRAINT fk81htsh35iw0wqtgpryjayk1yu;
    	   inversion          postgres    false    4181    437    439            R           2606    86797 2   ptn_tabla_amortizacion fk9mw5gqayg3cq9fg9twtodup21    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_tabla_amortizacion
    ADD CONSTRAINT fk9mw5gqayg3cq9fg9twtodup21 FOREIGN KEY (id_tipo_tabla) REFERENCES maestras.ptn_tipo_tablas(id_tipo_tabla);
 _   ALTER TABLE ONLY inversion.ptn_tabla_amortizacion DROP CONSTRAINT fk9mw5gqayg3cq9fg9twtodup21;
    	   inversion          postgres    false    4210    441    460            U           2606    86807 -   ptn_transacciones fkaor3qqbcvjm0riye4kuj3m69s    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_transacciones
    ADD CONSTRAINT fkaor3qqbcvjm0riye4kuj3m69s FOREIGN KEY (id_tipo_documento) REFERENCES maestras.ptn_tipo_documentos(id_tipo_documento);
 Z   ALTER TABLE ONLY inversion.ptn_transacciones DROP CONSTRAINT fkaor3qqbcvjm0riye4kuj3m69s;
    	   inversion          postgres    false    4204    456    444            E           2606    86742 -   ptn_doc_aceptados fkd7lo28awnspbf3vu7woq7vvwb    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_doc_aceptados
    ADD CONSTRAINT fkd7lo28awnspbf3vu7woq7vvwb FOREIGN KEY (identificacion) REFERENCES cuenta.ptn_personas(identificacion);
 Z   ALTER TABLE ONLY inversion.ptn_doc_aceptados DROP CONSTRAINT fkd7lo28awnspbf3vu7woq7vvwb;
    	   inversion          postgres    false    435    407    4141            �           2606    69119 ,   mult_solicitudes fke14o5nqlhi4xxigsxxr545e5q    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_solicitudes
    ADD CONSTRAINT fke14o5nqlhi4xxigsxxr545e5q FOREIGN KEY (pagare) REFERENCES inversion.mult_tabla_amortizacion(id_tbl_amortizacion);
 Y   ALTER TABLE ONLY inversion.mult_solicitudes DROP CONSTRAINT fke14o5nqlhi4xxigsxxr545e5q;
    	   inversion          postgres    false    275    280    3910            �           2606    69124 7   mult_solicitudes_documentos fkf8bmsncnpj855v4fw1wwye5qi    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_solicitudes_documentos
    ADD CONSTRAINT fkf8bmsncnpj855v4fw1wwye5qi FOREIGN KEY (solicitud) REFERENCES inversion.mult_solicitudes(numero_solicitud);
 d   ALTER TABLE ONLY inversion.mult_solicitudes_documentos DROP CONSTRAINT fkf8bmsncnpj855v4fw1wwye5qi;
    	   inversion          postgres    false    276    275    3904            �           2606    69129 ,   mult_solicitudes fkfcf9actoyhr0iqgpt5nbl159c    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_solicitudes
    ADD CONSTRAINT fkfcf9actoyhr0iqgpt5nbl159c FOREIGN KEY (tabla_amortizacion) REFERENCES inversion.mult_tabla_amortizacion(id_tbl_amortizacion);
 Y   ALTER TABLE ONLY inversion.mult_solicitudes DROP CONSTRAINT fkfcf9actoyhr0iqgpt5nbl159c;
    	   inversion          postgres    false    275    280    3910            �           2606    69134 ,   mult_solicitudes fkgi17ag0mmsdmjtlmmy853tsb0    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_solicitudes
    ADD CONSTRAINT fkgi17ag0mmsdmjtlmmy853tsb0 FOREIGN KEY (documentos) REFERENCES inversion.mult_solicitudes_documentos(id_sol_documentos);
 Y   ALTER TABLE ONLY inversion.mult_solicitudes DROP CONSTRAINT fkgi17ag0mmsdmjtlmmy853tsb0;
    	   inversion          postgres    false    275    276    3908            �           2606    69139 2   mult_tabla_amortizacion fkgx0ua04too2y1nrcrr6nmml3    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_tabla_amortizacion
    ADD CONSTRAINT fkgx0ua04too2y1nrcrr6nmml3 FOREIGN KEY (id_tipo_tabla) REFERENCES maestras.mult_tipo_tablas(id_tipo_tabla);
 _   ALTER TABLE ONLY inversion.mult_tabla_amortizacion DROP CONSTRAINT fkgx0ua04too2y1nrcrr6nmml3;
    	   inversion          postgres    false    280    299    3942            �           2606    69144 .   mult_transacciones fkhwrh1nbfv7uufsqxaq03poojh    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_transacciones
    ADD CONSTRAINT fkhwrh1nbfv7uufsqxaq03poojh FOREIGN KEY (id_forma_pago) REFERENCES maestras.mult_forma_pago(id_forma_pago);
 [   ALTER TABLE ONLY inversion.mult_transacciones DROP CONSTRAINT fkhwrh1nbfv7uufsqxaq03poojh;
    	   inversion          postgres    false    283    289    3926            P           2606    86792 6   ptn_solicitudes_documentos fkidgsedf6d9h11rvi1gklchb05    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_solicitudes_documentos
    ADD CONSTRAINT fkidgsedf6d9h11rvi1gklchb05 FOREIGN KEY (solicitud) REFERENCES inversion.ptn_solicitudes(numero_solicitud);
 c   ALTER TABLE ONLY inversion.ptn_solicitudes_documentos DROP CONSTRAINT fkidgsedf6d9h11rvi1gklchb05;
    	   inversion          postgres    false    437    439    4179            �           2606    69149 0   mult_datos_inversion fkinodr53cbo0t1rpahrslaxvgs    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_datos_inversion
    ADD CONSTRAINT fkinodr53cbo0t1rpahrslaxvgs FOREIGN KEY (solicitud) REFERENCES inversion.mult_solicitudes(numero_solicitud);
 ]   ALTER TABLE ONLY inversion.mult_datos_inversion DROP CONSTRAINT fkinodr53cbo0t1rpahrslaxvgs;
    	   inversion          postgres    false    269    275    3904            I           2606    86782 +   ptn_solicitudes fkjigewgw3vic64gbs24oja1y2c    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_solicitudes
    ADD CONSTRAINT fkjigewgw3vic64gbs24oja1y2c FOREIGN KEY (id_tipo_solicitud) REFERENCES maestras.ptn_tipo_solicitud(id);
 X   ALTER TABLE ONLY inversion.ptn_solicitudes DROP CONSTRAINT fkjigewgw3vic64gbs24oja1y2c;
    	   inversion          postgres    false    458    437    4206            J           2606    86787 +   ptn_solicitudes fkker76ylsig224fyinwmh0v65t    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_solicitudes
    ADD CONSTRAINT fkker76ylsig224fyinwmh0v65t FOREIGN KEY (usuario_creacion) REFERENCES cuenta.ptn_cuentas(id_cuenta);
 X   ALTER TABLE ONLY inversion.ptn_solicitudes DROP CONSTRAINT fkker76ylsig224fyinwmh0v65t;
    	   inversion          postgres    false    437    387    4105            �           2606    69154 ,   mult_solicitudes fkks2tal1jb9joycy3wk44bodrf    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_solicitudes
    ADD CONSTRAINT fkks2tal1jb9joycy3wk44bodrf FOREIGN KEY (estado_actual) REFERENCES inversion.mult_tipo_estados(id_estado);
 Y   ALTER TABLE ONLY inversion.mult_solicitudes DROP CONSTRAINT fkks2tal1jb9joycy3wk44bodrf;
    	   inversion          postgres    false    275    3912    282            V           2606    86822 -   ptn_transacciones fkl73sl1qr4fl4kryetux74jnt5    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_transacciones
    ADD CONSTRAINT fkl73sl1qr4fl4kryetux74jnt5 FOREIGN KEY (numero_solicitud) REFERENCES inversion.ptn_solicitudes(numero_solicitud);
 Z   ALTER TABLE ONLY inversion.ptn_transacciones DROP CONSTRAINT fkl73sl1qr4fl4kryetux74jnt5;
    	   inversion          postgres    false    4179    437    444            W           2606    86812 -   ptn_transacciones fklo8w63s2yv8gdldg1pp1kt8es    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_transacciones
    ADD CONSTRAINT fklo8w63s2yv8gdldg1pp1kt8es FOREIGN KEY (id_forma_pago) REFERENCES maestras.ptn_forma_pago(id_forma_pago);
 Z   ALTER TABLE ONLY inversion.ptn_transacciones DROP CONSTRAINT fklo8w63s2yv8gdldg1pp1kt8es;
    	   inversion          postgres    false    450    444    4195            �           2606    69159 ,   mult_solicitudes fkmms683poqusbf3144islp4sia    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_solicitudes
    ADD CONSTRAINT fkmms683poqusbf3144islp4sia FOREIGN KEY (id_inversionista) REFERENCES cuenta.mult_cuentas(id_cuenta);
 Y   ALTER TABLE ONLY inversion.mult_solicitudes DROP CONSTRAINT fkmms683poqusbf3144islp4sia;
    	   inversion          postgres    false    225    275    3824            D           2606    86732 4   ptn_detalle_amortizacion fkn54c3p5cg0daaan9fhpv8fp19    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_detalle_amortizacion
    ADD CONSTRAINT fkn54c3p5cg0daaan9fhpv8fp19 FOREIGN KEY (id_tbl_amortizacion) REFERENCES inversion.ptn_tabla_amortizacion(id_tbl_amortizacion);
 a   ALTER TABLE ONLY inversion.ptn_detalle_amortizacion DROP CONSTRAINT fkn54c3p5cg0daaan9fhpv8fp19;
    	   inversion          postgres    false    433    441    4183            K           2606    86772 +   ptn_solicitudes fknec2cvebowow9ex35n53g11x4    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_solicitudes
    ADD CONSTRAINT fknec2cvebowow9ex35n53g11x4 FOREIGN KEY (pagare) REFERENCES inversion.ptn_tabla_amortizacion(id_tbl_amortizacion);
 X   ALTER TABLE ONLY inversion.ptn_solicitudes DROP CONSTRAINT fknec2cvebowow9ex35n53g11x4;
    	   inversion          postgres    false    441    4183    437            �           2606    69164 .   mult_transacciones fkneeynnnfgcfc0la3t5vffudeq    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_transacciones
    ADD CONSTRAINT fkneeynnnfgcfc0la3t5vffudeq FOREIGN KEY (numero_solicitud) REFERENCES inversion.mult_solicitudes(numero_solicitud);
 [   ALTER TABLE ONLY inversion.mult_transacciones DROP CONSTRAINT fkneeynnnfgcfc0la3t5vffudeq;
    	   inversion          postgres    false    275    283    3904            F           2606    86737 -   ptn_doc_aceptados fko9x0hw0vulom6b14xcbp4vxil    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_doc_aceptados
    ADD CONSTRAINT fko9x0hw0vulom6b14xcbp4vxil FOREIGN KEY (id_tipo_documento) REFERENCES maestras.ptn_tipo_documentos(id_tipo_documento);
 Z   ALTER TABLE ONLY inversion.ptn_doc_aceptados DROP CONSTRAINT fko9x0hw0vulom6b14xcbp4vxil;
    	   inversion          postgres    false    456    435    4204            L           2606    86757 +   ptn_solicitudes fkp5klotdkkjhp1x3b0kpcgyd7l    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_solicitudes
    ADD CONSTRAINT fkp5klotdkkjhp1x3b0kpcgyd7l FOREIGN KEY (estado_actual) REFERENCES inversion.ptn_tipo_estados(id_estado);
 X   ALTER TABLE ONLY inversion.ptn_solicitudes DROP CONSTRAINT fkp5klotdkkjhp1x3b0kpcgyd7l;
    	   inversion          postgres    false    442    4185    437            M           2606    86777 +   ptn_solicitudes fkpur4tlqx1rwpg6yu78ads977j    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_solicitudes
    ADD CONSTRAINT fkpur4tlqx1rwpg6yu78ads977j FOREIGN KEY (codigo_proyecto) REFERENCES promotor.ptn_proyectos(id_proyecto);
 X   ALTER TABLE ONLY inversion.ptn_solicitudes DROP CONSTRAINT fkpur4tlqx1rwpg6yu78ads977j;
    	   inversion          postgres    false    4252    437    495            N           2606    86767 +   ptn_solicitudes fkqtyo8rs9lvugq5mpvg7ssvqby    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_solicitudes
    ADD CONSTRAINT fkqtyo8rs9lvugq5mpvg7ssvqby FOREIGN KEY (id_inversionista) REFERENCES cuenta.ptn_cuentas(id_cuenta);
 X   ALTER TABLE ONLY inversion.ptn_solicitudes DROP CONSTRAINT fkqtyo8rs9lvugq5mpvg7ssvqby;
    	   inversion          postgres    false    4105    387    437            O           2606    86762 +   ptn_solicitudes fkqyti3pgy21uoa6x8ykhrgajy2    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_solicitudes
    ADD CONSTRAINT fkqyti3pgy21uoa6x8ykhrgajy2 FOREIGN KEY (ultimo_historial) REFERENCES historicas.ptn_hist_solicitud(id);
 X   ALTER TABLE ONLY inversion.ptn_solicitudes DROP CONSTRAINT fkqyti3pgy21uoa6x8ykhrgajy2;
    	   inversion          postgres    false    437    423    4165            �           2606    69169 .   mult_transacciones fksxs1sa68xtsftn2udr6c4ictw    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_transacciones
    ADD CONSTRAINT fksxs1sa68xtsftn2udr6c4ictw FOREIGN KEY (id_tipo_documento) REFERENCES maestras.mult_tipo_documentos(id_tipo_documento);
 [   ALTER TABLE ONLY inversion.mult_transacciones DROP CONSTRAINT fksxs1sa68xtsftn2udr6c4ictw;
    	   inversion          postgres    false    3936    283    295            �           2606    69174 ,   mult_solicitudes fkt1wqahg7p2y6r5xbmb5tue0hl    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_solicitudes
    ADD CONSTRAINT fkt1wqahg7p2y6r5xbmb5tue0hl FOREIGN KEY (ultimo_historial) REFERENCES historicas.mult_hist_solicitud(id);
 Y   ALTER TABLE ONLY inversion.mult_solicitudes DROP CONSTRAINT fkt1wqahg7p2y6r5xbmb5tue0hl;
    	   inversion          postgres    false    275    3880    261            C           2606    86727 /   ptn_datos_inversion fkt2uhmp1dx93p0ynvcmeqyap8w    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.ptn_datos_inversion
    ADD CONSTRAINT fkt2uhmp1dx93p0ynvcmeqyap8w FOREIGN KEY (solicitud) REFERENCES inversion.ptn_solicitudes(numero_solicitud);
 \   ALTER TABLE ONLY inversion.ptn_datos_inversion DROP CONSTRAINT fkt2uhmp1dx93p0ynvcmeqyap8w;
    	   inversion          postgres    false    437    431    4179            �           2606    69179 .   mult_transacciones fktd30ulqjpr1bldowvipeb5jao    FK CONSTRAINT     �   ALTER TABLE ONLY inversion.mult_transacciones
    ADD CONSTRAINT fktd30ulqjpr1bldowvipeb5jao FOREIGN KEY (proyecto) REFERENCES promotor.mult_proyectos(id_proyecto);
 [   ALTER TABLE ONLY inversion.mult_transacciones DROP CONSTRAINT fktd30ulqjpr1bldowvipeb5jao;
    	   inversion          postgres    false    283    352    4050            �           2606    69184 )   mult_ciudades fkc8t08eh772uemwrou4v4dvq0a    FK CONSTRAINT     �   ALTER TABLE ONLY maestras.mult_ciudades
    ADD CONSTRAINT fkc8t08eh772uemwrou4v4dvq0a FOREIGN KEY (id_pais) REFERENCES maestras.mult_nacionalidades(id_nacionalidad);
 U   ALTER TABLE ONLY maestras.mult_ciudades DROP CONSTRAINT fkc8t08eh772uemwrou4v4dvq0a;
       maestras          postgres    false    287    291    3932            X           2606    86832 (   ptn_ciudades fkfjp65ncbphfe1kovk9k16rlpa    FK CONSTRAINT     �   ALTER TABLE ONLY maestras.ptn_ciudades
    ADD CONSTRAINT fkfjp65ncbphfe1kovk9k16rlpa FOREIGN KEY (id_pais) REFERENCES maestras.ptn_nacionalidades(id_nacionalidad);
 T   ALTER TABLE ONLY maestras.ptn_ciudades DROP CONSTRAINT fkfjp65ncbphfe1kovk9k16rlpa;
       maestras          postgres    false    452    4199    448            �           2606    69189 3   mult_menu_operacion_int fk4ju5t0g5vn81wnwt82oojrqlk    FK CONSTRAINT     �   ALTER TABLE ONLY multiplo.mult_menu_operacion_int
    ADD CONSTRAINT fk4ju5t0g5vn81wnwt82oojrqlk FOREIGN KEY (id_rol_int) REFERENCES multiplo.mult_roles_int(id_rol);
 _   ALTER TABLE ONLY multiplo.mult_menu_operacion_int DROP CONSTRAINT fk4ju5t0g5vn81wnwt82oojrqlk;
       multiplo          postgres    false    312    3981    317            �           2606    69194 7   mult_roles_cuentas_internas fk4t3j61vb4o6hny0qg5qjhsy9n    FK CONSTRAINT     �   ALTER TABLE ONLY multiplo.mult_roles_cuentas_internas
    ADD CONSTRAINT fk4t3j61vb4o6hny0qg5qjhsy9n FOREIGN KEY (cuenta_interna) REFERENCES multiplo.mult_cuentas_internas(id_cuenta);
 c   ALTER TABLE ONLY multiplo.mult_roles_cuentas_internas DROP CONSTRAINT fk4t3j61vb4o6hny0qg5qjhsy9n;
       multiplo          postgres    false    3958    309    316            �           2606    69199 7   mult_roles_cuentas_internas fkaw4q6eubqaess7qb5e27ckwx5    FK CONSTRAINT     �   ALTER TABLE ONLY multiplo.mult_roles_cuentas_internas
    ADD CONSTRAINT fkaw4q6eubqaess7qb5e27ckwx5 FOREIGN KEY (rol) REFERENCES multiplo.mult_roles_int(id_rol);
 c   ALTER TABLE ONLY multiplo.mult_roles_cuentas_internas DROP CONSTRAINT fkaw4q6eubqaess7qb5e27ckwx5;
       multiplo          postgres    false    316    317    3981            �           2606    69204 1   mult_personal_interno fkmlbdv0kyvlycrywk8faxyo0a2    FK CONSTRAINT     �   ALTER TABLE ONLY multiplo.mult_personal_interno
    ADD CONSTRAINT fkmlbdv0kyvlycrywk8faxyo0a2 FOREIGN KEY (cuenta_interna) REFERENCES multiplo.mult_cuentas_internas(id_cuenta);
 ]   ALTER TABLE ONLY multiplo.mult_personal_interno DROP CONSTRAINT fkmlbdv0kyvlycrywk8faxyo0a2;
       multiplo          postgres    false    315    3958    309            �           2606    69209 7   mult_roles_cuentas_internas fkn51qo463500a1xor8w6hn5o59    FK CONSTRAINT     �   ALTER TABLE ONLY multiplo.mult_roles_cuentas_internas
    ADD CONSTRAINT fkn51qo463500a1xor8w6hn5o59 FOREIGN KEY (usuari_creacion) REFERENCES multiplo.mult_cuentas_internas(id_cuenta);
 c   ALTER TABLE ONLY multiplo.mult_roles_cuentas_internas DROP CONSTRAINT fkn51qo463500a1xor8w6hn5o59;
       multiplo          postgres    false    309    316    3958            �           2606    69214 )   mult_menu_int fkohxivl96q4gj17m6v1i86jp5l    FK CONSTRAINT     �   ALTER TABLE ONLY multiplo.mult_menu_int
    ADD CONSTRAINT fkohxivl96q4gj17m6v1i86jp5l FOREIGN KEY (id_padre) REFERENCES multiplo.mult_menu_int(id_menu);
 U   ALTER TABLE ONLY multiplo.mult_menu_int DROP CONSTRAINT fkohxivl96q4gj17m6v1i86jp5l;
       multiplo          postgres    false    310    310    3964            �           2606    69219 3   mult_menu_operacion_int fkop381vj45dxmh6yq203agkuo7    FK CONSTRAINT     �   ALTER TABLE ONLY multiplo.mult_menu_operacion_int
    ADD CONSTRAINT fkop381vj45dxmh6yq203agkuo7 FOREIGN KEY (id_operacion) REFERENCES cuenta.mult_operaciones(id_operacion);
 _   ALTER TABLE ONLY multiplo.mult_menu_operacion_int DROP CONSTRAINT fkop381vj45dxmh6yq203agkuo7;
       multiplo          postgres    false    3836    312    229            �           2606    69224 1   mult_cuentas_internas fkpv99rf8prfo9akfjedo94wapj    FK CONSTRAINT     �   ALTER TABLE ONLY multiplo.mult_cuentas_internas
    ADD CONSTRAINT fkpv99rf8prfo9akfjedo94wapj FOREIGN KEY (id_persona) REFERENCES multiplo.mult_personal_interno(id_pers_interno);
 ]   ALTER TABLE ONLY multiplo.mult_cuentas_internas DROP CONSTRAINT fkpv99rf8prfo9akfjedo94wapj;
       multiplo          postgres    false    309    3976    315            �           2606    69229 3   mult_menu_operacion_int fkrop7c842q02tcjfmy3u6f4b4w    FK CONSTRAINT     �   ALTER TABLE ONLY multiplo.mult_menu_operacion_int
    ADD CONSTRAINT fkrop7c842q02tcjfmy3u6f4b4w FOREIGN KEY (id_menu) REFERENCES cuenta.mult_menu(id_menu);
 _   ALTER TABLE ONLY multiplo.mult_menu_operacion_int DROP CONSTRAINT fkrop7c842q02tcjfmy3u6f4b4w;
       multiplo          postgres    false    3830    312    226            �           2606    69234 +   mult_documentos fkmhn1xgn210u7ul66fpoiudgog    FK CONSTRAINT     �   ALTER TABLE ONLY multiplo_documentos.mult_documentos
    ADD CONSTRAINT fkmhn1xgn210u7ul66fpoiudgog FOREIGN KEY (id_tipo_documento) REFERENCES maestras.mult_tipo_documentos(id_tipo_documento);
 b   ALTER TABLE ONLY multiplo_documentos.mult_documentos DROP CONSTRAINT fkmhn1xgn210u7ul66fpoiudgog;
       multiplo_documentos          postgres    false    3936    295    319            �           2606    69239 :   mult_porc_interes_tbl_proyecto fk364v88tlc7rwh3pcqcluowpxf    FK CONSTRAINT     �   ALTER TABLE ONLY negocio.mult_porc_interes_tbl_proyecto
    ADD CONSTRAINT fk364v88tlc7rwh3pcqcluowpxf FOREIGN KEY (id_tipo_tabla) REFERENCES maestras.mult_tipo_tablas(id_tipo_tabla);
 e   ALTER TABLE ONLY negocio.mult_porc_interes_tbl_proyecto DROP CONSTRAINT fk364v88tlc7rwh3pcqcluowpxf;
       negocio          postgres    false    299    334    3942            �           2606    69244 =   mult_conciliacion_aprobada_detalle fk4k781511drtqfcegdn8imjul    FK CONSTRAINT     �   ALTER TABLE ONLY negocio.mult_conciliacion_aprobada_detalle
    ADD CONSTRAINT fk4k781511drtqfcegdn8imjul FOREIGN KEY (id_conciliacion) REFERENCES negocio.mult_conciliacion_aprobada(id);
 h   ALTER TABLE ONLY negocio.mult_conciliacion_aprobada_detalle DROP CONSTRAINT fk4k781511drtqfcegdn8imjul;
       negocio          postgres    false    325    4002    324            �           2606    69249 9   mult_conciliacion_detalle_xls fk6ws6o927h9m9yggah4pwoh21n    FK CONSTRAINT     �   ALTER TABLE ONLY negocio.mult_conciliacion_detalle_xls
    ADD CONSTRAINT fk6ws6o927h9m9yggah4pwoh21n FOREIGN KEY (id_conciliacion) REFERENCES negocio.mult_conciliacion_xls(id);
 d   ALTER TABLE ONLY negocio.mult_conciliacion_detalle_xls DROP CONSTRAINT fk6ws6o927h9m9yggah4pwoh21n;
       negocio          postgres    false    328    4009    330            \           2606    86852 9   ptn_porc_interes_tbl_proyecto fk8vfh9hp0u3aptnaj99ownubmv    FK CONSTRAINT     �   ALTER TABLE ONLY negocio.ptn_porc_interes_tbl_proyecto
    ADD CONSTRAINT fk8vfh9hp0u3aptnaj99ownubmv FOREIGN KEY (codigo_proyecto) REFERENCES promotor.ptn_proyectos(id_proyecto);
 d   ALTER TABLE ONLY negocio.ptn_porc_interes_tbl_proyecto DROP CONSTRAINT fk8vfh9hp0u3aptnaj99ownubmv;
       negocio          postgres    false    495    478    4252            �           2606    69254 :   mult_porc_interes_tbl_proyecto fka07njcnludtt75yk6m1f1amrm    FK CONSTRAINT     �   ALTER TABLE ONLY negocio.mult_porc_interes_tbl_proyecto
    ADD CONSTRAINT fka07njcnludtt75yk6m1f1amrm FOREIGN KEY (codigo_proyecto) REFERENCES promotor.mult_proyectos(id_proyecto);
 e   ALTER TABLE ONLY negocio.mult_porc_interes_tbl_proyecto DROP CONSTRAINT fka07njcnludtt75yk6m1f1amrm;
       negocio          postgres    false    352    334    4050            Y           2606    86837 =   ptn_conciliacion_aprobada_detalle fkhshboe2p4f24hqj78cptemxkx    FK CONSTRAINT     �   ALTER TABLE ONLY negocio.ptn_conciliacion_aprobada_detalle
    ADD CONSTRAINT fkhshboe2p4f24hqj78cptemxkx FOREIGN KEY (id_conciliacion) REFERENCES negocio.ptn_conciliacion_aprobada(id);
 h   ALTER TABLE ONLY negocio.ptn_conciliacion_aprobada_detalle DROP CONSTRAINT fkhshboe2p4f24hqj78cptemxkx;
       negocio          postgres    false    470    468    4220            [           2606    86847 3   ptn_fecha_gen_tbl_amort fknla6p5sksqamsub8hhc2xs27v    FK CONSTRAINT     �   ALTER TABLE ONLY negocio.ptn_fecha_gen_tbl_amort
    ADD CONSTRAINT fknla6p5sksqamsub8hhc2xs27v FOREIGN KEY (id_proyecto) REFERENCES promotor.ptn_proyectos(id_proyecto);
 ^   ALTER TABLE ONLY negocio.ptn_fecha_gen_tbl_amort DROP CONSTRAINT fknla6p5sksqamsub8hhc2xs27v;
       negocio          postgres    false    495    476    4252            Z           2606    86842 8   ptn_conciliacion_detalle_xls fko1xwem3q5wc2ujptiixulv0lt    FK CONSTRAINT     �   ALTER TABLE ONLY negocio.ptn_conciliacion_detalle_xls
    ADD CONSTRAINT fko1xwem3q5wc2ujptiixulv0lt FOREIGN KEY (id_conciliacion) REFERENCES negocio.ptn_conciliacion_xls(id);
 c   ALTER TABLE ONLY negocio.ptn_conciliacion_detalle_xls DROP CONSTRAINT fko1xwem3q5wc2ujptiixulv0lt;
       negocio          postgres    false    4226    472    474            �           2606    69259 4   mult_fecha_gen_tbl_amort fkr9dmsno0e8y324ttb60iyafjr    FK CONSTRAINT     �   ALTER TABLE ONLY negocio.mult_fecha_gen_tbl_amort
    ADD CONSTRAINT fkr9dmsno0e8y324ttb60iyafjr FOREIGN KEY (id_proyecto) REFERENCES promotor.mult_proyectos(id_proyecto);
 _   ALTER TABLE ONLY negocio.mult_fecha_gen_tbl_amort DROP CONSTRAINT fkr9dmsno0e8y324ttb60iyafjr;
       negocio          postgres    false    332    352    4050            ]           2606    86857 8   ptn_porc_interes_tbl_proyecto fkwfoe6ekq30h1e44cfc3mva0c    FK CONSTRAINT     �   ALTER TABLE ONLY negocio.ptn_porc_interes_tbl_proyecto
    ADD CONSTRAINT fkwfoe6ekq30h1e44cfc3mva0c FOREIGN KEY (id_tipo_tabla) REFERENCES maestras.ptn_tipo_tablas(id_tipo_tabla);
 c   ALTER TABLE ONLY negocio.ptn_porc_interes_tbl_proyecto DROP CONSTRAINT fkwfoe6ekq30h1e44cfc3mva0c;
       negocio          postgres    false    460    4210    478                       2606    69264 0   mult_proyectos_rutas fk2q49c55wuvsddd6u6iy9xonr4    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_proyectos_rutas
    ADD CONSTRAINT fk2q49c55wuvsddd6u6iy9xonr4 FOREIGN KEY (id_proyecto) REFERENCES promotor.mult_proyectos(id_proyecto);
 \   ALTER TABLE ONLY promotor.mult_proyectos_rutas DROP CONSTRAINT fk2q49c55wuvsddd6u6iy9xonr4;
       promotor          postgres    false    4050    355    352            f           2606    86902 2   ptn_porc_sol_aprobadas fk2wxbmnx7jlhcpmyd7jmr1jpji    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_porc_sol_aprobadas
    ADD CONSTRAINT fk2wxbmnx7jlhcpmyd7jmr1jpji FOREIGN KEY (codigo_proyecto) REFERENCES promotor.ptn_proyectos(id_proyecto);
 ^   ALTER TABLE ONLY promotor.ptn_porc_sol_aprobadas DROP CONSTRAINT fk2wxbmnx7jlhcpmyd7jmr1jpji;
       promotor          postgres    false    495    4252    494            g           2606    86937 (   ptn_proyectos fk39yww8ofssqsl7997ooa7xpd    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_proyectos
    ADD CONSTRAINT fk39yww8ofssqsl7997ooa7xpd FOREIGN KEY (id_indicador) REFERENCES promotor.ptn_indicadores(id_indicador);
 T   ALTER TABLE ONLY promotor.ptn_proyectos DROP CONSTRAINT fk39yww8ofssqsl7997ooa7xpd;
       promotor          postgres    false    495    492    4248                       2606    69269 *   mult_proyectos fk3snnnrwsul91qlkym3xdofy7w    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_proyectos
    ADD CONSTRAINT fk3snnnrwsul91qlkym3xdofy7w FOREIGN KEY (calificacion_interna) REFERENCES promotor.mult_tipo_calificaciones(id_tipo_calificacion);
 V   ALTER TABLE ONLY promotor.mult_proyectos DROP CONSTRAINT fk3snnnrwsul91qlkym3xdofy7w;
       promotor          postgres    false    361    4063    352                       2606    69274 *   mult_proyectos fk6k15pjt5lsi8al7kwipmimcf1    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_proyectos
    ADD CONSTRAINT fk6k15pjt5lsi8al7kwipmimcf1 FOREIGN KEY (ultimo_historial) REFERENCES historicas.mult_hist_proyecto(id);
 V   ALTER TABLE ONLY promotor.mult_proyectos DROP CONSTRAINT fk6k15pjt5lsi8al7kwipmimcf1;
       promotor          postgres    false    259    352    3878                       2606    69279 *   mult_proyectos fk7arem6i6l1dvuq2pl0dhfjaxr    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_proyectos
    ADD CONSTRAINT fk7arem6i6l1dvuq2pl0dhfjaxr FOREIGN KEY (tabla_amortizacion) REFERENCES inversion.mult_tabla_amortizacion(id_tbl_amortizacion);
 V   ALTER TABLE ONLY promotor.mult_proyectos DROP CONSTRAINT fk7arem6i6l1dvuq2pl0dhfjaxr;
       promotor          postgres    false    352    3910    280            b           2606    86897 (   ptn_empresas fk80vgchymd0m9libq5a8vetk03    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_empresas
    ADD CONSTRAINT fk80vgchymd0m9libq5a8vetk03 FOREIGN KEY (pais) REFERENCES maestras.ptn_nacionalidades(id_nacionalidad);
 T   ALTER TABLE ONLY promotor.ptn_empresas DROP CONSTRAINT fk80vgchymd0m9libq5a8vetk03;
       promotor          postgres    false    490    452    4199            �           2606    69284 ;   mult_detalle_porc_sol_aprobadas fk9oqrjsfph1p1ml1olc2xuetis    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_detalle_porc_sol_aprobadas
    ADD CONSTRAINT fk9oqrjsfph1p1ml1olc2xuetis FOREIGN KEY (id_porc_sol_aprobada) REFERENCES promotor.mult_porc_sol_aprobadas(id);
 g   ALTER TABLE ONLY promotor.mult_detalle_porc_sol_aprobadas DROP CONSTRAINT fk9oqrjsfph1p1ml1olc2xuetis;
       promotor          postgres    false    338    4045    350            p           2606    86952 /   ptn_proyectos_rutas fka9xrgs8ghcaxaesjjcl86qx3s    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_proyectos_rutas
    ADD CONSTRAINT fka9xrgs8ghcaxaesjjcl86qx3s FOREIGN KEY (id_proyecto) REFERENCES promotor.ptn_proyectos(id_proyecto);
 [   ALTER TABLE ONLY promotor.ptn_proyectos_rutas DROP CONSTRAINT fka9xrgs8ghcaxaesjjcl86qx3s;
       promotor          postgres    false    4252    495    499                       2606    69289 )   mult_empresas fkb17myle3vufb03iphr3w1h6jp    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_empresas
    ADD CONSTRAINT fkb17myle3vufb03iphr3w1h6jp FOREIGN KEY (cuenta) REFERENCES cuenta.mult_cuentas(id_cuenta);
 U   ALTER TABLE ONLY promotor.mult_empresas DROP CONSTRAINT fkb17myle3vufb03iphr3w1h6jp;
       promotor          postgres    false    225    3824    346            h           2606    86922 )   ptn_proyectos fkcancl2e54460rh0es8fpxft37    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_proyectos
    ADD CONSTRAINT fkcancl2e54460rh0es8fpxft37 FOREIGN KEY (estado_actual) REFERENCES inversion.ptn_tipo_estados(id_estado);
 U   ALTER TABLE ONLY promotor.ptn_proyectos DROP CONSTRAINT fkcancl2e54460rh0es8fpxft37;
       promotor          postgres    false    442    495    4185                       2606    69294 )   mult_empresas fkdalm06d4tp5udf95px3thubql    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_empresas
    ADD CONSTRAINT fkdalm06d4tp5udf95px3thubql FOREIGN KEY (actividad) REFERENCES promotor.mult_tipo_actividades(id_actividad);
 U   ALTER TABLE ONLY promotor.mult_empresas DROP CONSTRAINT fkdalm06d4tp5udf95px3thubql;
       promotor          postgres    false    359    4059    346            n           2606    86942 1   ptn_proyectos_cuentas fkdt8t9fx459lr33kt85x1e9o6p    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_proyectos_cuentas
    ADD CONSTRAINT fkdt8t9fx459lr33kt85x1e9o6p FOREIGN KEY (id_banco) REFERENCES maestras.ptn_bancos(id_banco);
 ]   ALTER TABLE ONLY promotor.ptn_proyectos_cuentas DROP CONSTRAINT fkdt8t9fx459lr33kt85x1e9o6p;
       promotor          postgres    false    446    4191    497                       2606    69299 2   mult_proyectos_cuentas fke209w25aaq3cxndpxlov8kh4g    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_proyectos_cuentas
    ADD CONSTRAINT fke209w25aaq3cxndpxlov8kh4g FOREIGN KEY (id_banco) REFERENCES maestras.mult_bancos(id_banco);
 ^   ALTER TABLE ONLY promotor.mult_proyectos_cuentas DROP CONSTRAINT fke209w25aaq3cxndpxlov8kh4g;
       promotor          postgres    false    353    285    3922            c           2606    86882 (   ptn_empresas fkek2e8vsw3agp5q137w80scimy    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_empresas
    ADD CONSTRAINT fkek2e8vsw3agp5q137w80scimy FOREIGN KEY (actividad) REFERENCES promotor.ptn_tipo_actividades(id_actividad);
 T   ALTER TABLE ONLY promotor.ptn_empresas DROP CONSTRAINT fkek2e8vsw3agp5q137w80scimy;
       promotor          postgres    false    490    4258    501                        2606    69304 6   mult_empresa_datos_anuales fkf8tw1vny4jbpmk5cnbmrt9wb8    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_empresa_datos_anuales
    ADD CONSTRAINT fkf8tw1vny4jbpmk5cnbmrt9wb8 FOREIGN KEY (id_empresa) REFERENCES promotor.mult_empresas(id_empresa);
 b   ALTER TABLE ONLY promotor.mult_empresa_datos_anuales DROP CONSTRAINT fkf8tw1vny4jbpmk5cnbmrt9wb8;
       promotor          postgres    false    4039    344    346            i           2606    86932 )   ptn_proyectos fkfgcxle8xrcbxjiybp5f4047c1    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_proyectos
    ADD CONSTRAINT fkfgcxle8xrcbxjiybp5f4047c1 FOREIGN KEY (ultimo_historial) REFERENCES historicas.ptn_hist_proyecto(id);
 U   ALTER TABLE ONLY promotor.ptn_proyectos DROP CONSTRAINT fkfgcxle8xrcbxjiybp5f4047c1;
       promotor          postgres    false    495    421    4163                       2606    69309 )   mult_empresas fkg4u09nmft6xhjmrw31pfi7h5n    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_empresas
    ADD CONSTRAINT fkg4u09nmft6xhjmrw31pfi7h5n FOREIGN KEY (pais) REFERENCES maestras.mult_nacionalidades(id_nacionalidad);
 U   ALTER TABLE ONLY promotor.mult_empresas DROP CONSTRAINT fkg4u09nmft6xhjmrw31pfi7h5n;
       promotor          postgres    false    346    291    3932                       2606    69314 3   mult_porc_sol_aprobadas fkh8v9tnj5f7cdwd85kyh9n4cgq    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_porc_sol_aprobadas
    ADD CONSTRAINT fkh8v9tnj5f7cdwd85kyh9n4cgq FOREIGN KEY (codigo_proyecto) REFERENCES promotor.mult_proyectos(id_proyecto);
 _   ALTER TABLE ONLY promotor.mult_porc_sol_aprobadas DROP CONSTRAINT fkh8v9tnj5f7cdwd85kyh9n4cgq;
       promotor          postgres    false    4050    350    352            	           2606    69319 *   mult_proyectos fkialcun684j3drxd92vv8c032h    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_proyectos
    ADD CONSTRAINT fkialcun684j3drxd92vv8c032h FOREIGN KEY (id_empresa) REFERENCES promotor.mult_empresas(id_empresa);
 V   ALTER TABLE ONLY promotor.mult_proyectos DROP CONSTRAINT fkialcun684j3drxd92vv8c032h;
       promotor          postgres    false    346    352    4039            d           2606    86892 (   ptn_empresas fkid951cy7qdtmdg0w8k001pgpy    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_empresas
    ADD CONSTRAINT fkid951cy7qdtmdg0w8k001pgpy FOREIGN KEY (dato_anual_actual) REFERENCES promotor.ptn_empresa_datos_anuales(id);
 T   ALTER TABLE ONLY promotor.ptn_empresas DROP CONSTRAINT fkid951cy7qdtmdg0w8k001pgpy;
       promotor          postgres    false    490    4242    488            e           2606    86887 (   ptn_empresas fkit16rc182u7iu0ypw35bydj34    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_empresas
    ADD CONSTRAINT fkit16rc182u7iu0ypw35bydj34 FOREIGN KEY (cuenta) REFERENCES cuenta.ptn_cuentas(id_cuenta);
 T   ALTER TABLE ONLY promotor.ptn_empresas DROP CONSTRAINT fkit16rc182u7iu0ypw35bydj34;
       promotor          postgres    false    4105    387    490            
           2606    69324 *   mult_proyectos fkjhv7jyv8h1p02t5gfmqjs95o8    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_proyectos
    ADD CONSTRAINT fkjhv7jyv8h1p02t5gfmqjs95o8 FOREIGN KEY (estado_actual) REFERENCES inversion.mult_tipo_estados(id_estado);
 V   ALTER TABLE ONLY promotor.mult_proyectos DROP CONSTRAINT fkjhv7jyv8h1p02t5gfmqjs95o8;
       promotor          postgres    false    3912    282    352            �           2606    69329 7   mult_documentos_financieros fkkdtkfle986r37dfs4sagoqb2x    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_documentos_financieros
    ADD CONSTRAINT fkkdtkfle986r37dfs4sagoqb2x FOREIGN KEY (empresa) REFERENCES promotor.mult_empresas(id_empresa);
 c   ALTER TABLE ONLY promotor.mult_documentos_financieros DROP CONSTRAINT fkkdtkfle986r37dfs4sagoqb2x;
       promotor          postgres    false    340    4039    346                       2606    69334 *   mult_proyectos fkkduxbcgo6oe3i8edf8o4bgt11    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_proyectos
    ADD CONSTRAINT fkkduxbcgo6oe3i8edf8o4bgt11 FOREIGN KEY (estado_anterior) REFERENCES inversion.mult_tipo_estados(id_estado);
 V   ALTER TABLE ONLY promotor.mult_proyectos DROP CONSTRAINT fkkduxbcgo6oe3i8edf8o4bgt11;
       promotor          postgres    false    282    3912    352            _           2606    86867 6   ptn_documentos_financieros fkky3oael5f86yokx0cdfwm2ifv    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_documentos_financieros
    ADD CONSTRAINT fkky3oael5f86yokx0cdfwm2ifv FOREIGN KEY (empresa) REFERENCES promotor.ptn_empresas(id_empresa);
 b   ALTER TABLE ONLY promotor.ptn_documentos_financieros DROP CONSTRAINT fkky3oael5f86yokx0cdfwm2ifv;
       promotor          postgres    false    484    490    4244                       2606    69339 *   mult_proyectos fkloi4mldo9ocu03e8s98n7c1xd    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_proyectos
    ADD CONSTRAINT fkloi4mldo9ocu03e8s98n7c1xd FOREIGN KEY (id_indicador) REFERENCES promotor.mult_indicadores(id_indicador);
 V   ALTER TABLE ONLY promotor.mult_proyectos DROP CONSTRAINT fkloi4mldo9ocu03e8s98n7c1xd;
       promotor          postgres    false    348    4043    352                       2606    69344 2   mult_proyectos_cuentas fkmghipaimqrd5dvu7idxrqq4ym    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_proyectos_cuentas
    ADD CONSTRAINT fkmghipaimqrd5dvu7idxrqq4ym FOREIGN KEY (id_proyecto) REFERENCES promotor.mult_proyectos(id_proyecto);
 ^   ALTER TABLE ONLY promotor.mult_proyectos_cuentas DROP CONSTRAINT fkmghipaimqrd5dvu7idxrqq4ym;
       promotor          postgres    false    352    353    4050            ^           2606    86862 :   ptn_detalle_porc_sol_aprobadas fkmq2l3e63ftq2rq56j7j493bxv    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_detalle_porc_sol_aprobadas
    ADD CONSTRAINT fkmq2l3e63ftq2rq56j7j493bxv FOREIGN KEY (id_porc_sol_aprobada) REFERENCES promotor.ptn_porc_sol_aprobadas(id);
 f   ALTER TABLE ONLY promotor.ptn_detalle_porc_sol_aprobadas DROP CONSTRAINT fkmq2l3e63ftq2rq56j7j493bxv;
       promotor          postgres    false    482    494    4250                       2606    69349 )   mult_empresas fkniomsfjsw62mmmakd5iar0vcn    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_empresas
    ADD CONSTRAINT fkniomsfjsw62mmmakd5iar0vcn FOREIGN KEY (dato_anual_actual) REFERENCES promotor.mult_empresa_datos_anuales(id);
 U   ALTER TABLE ONLY promotor.mult_empresas DROP CONSTRAINT fkniomsfjsw62mmmakd5iar0vcn;
       promotor          postgres    false    4036    346    344            o           2606    86947 1   ptn_proyectos_cuentas fknj0i3skxglp8gct8anlqj8oji    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_proyectos_cuentas
    ADD CONSTRAINT fknj0i3skxglp8gct8anlqj8oji FOREIGN KEY (id_proyecto) REFERENCES promotor.ptn_proyectos(id_proyecto);
 ]   ALTER TABLE ONLY promotor.ptn_proyectos_cuentas DROP CONSTRAINT fknj0i3skxglp8gct8anlqj8oji;
       promotor          postgres    false    495    4252    497            a           2606    86877 5   ptn_empresa_datos_anuales fknlo6s0kn9hl7uptqd3sv7tw8y    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_empresa_datos_anuales
    ADD CONSTRAINT fknlo6s0kn9hl7uptqd3sv7tw8y FOREIGN KEY (id_empresa) REFERENCES promotor.ptn_empresas(id_empresa);
 a   ALTER TABLE ONLY promotor.ptn_empresa_datos_anuales DROP CONSTRAINT fknlo6s0kn9hl7uptqd3sv7tw8y;
       promotor          postgres    false    4244    490    488            `           2606    86872 4   ptn_documentos_juridicos fkpn35mldxubsvss0k3i49k448g    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_documentos_juridicos
    ADD CONSTRAINT fkpn35mldxubsvss0k3i49k448g FOREIGN KEY (empresa) REFERENCES promotor.ptn_empresas(id_empresa);
 `   ALTER TABLE ONLY promotor.ptn_documentos_juridicos DROP CONSTRAINT fkpn35mldxubsvss0k3i49k448g;
       promotor          postgres    false    490    486    4244            j           2606    86927 )   ptn_proyectos fkr1o6tern02wdxq2o7x07dq95c    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_proyectos
    ADD CONSTRAINT fkr1o6tern02wdxq2o7x07dq95c FOREIGN KEY (estado_anterior) REFERENCES inversion.ptn_tipo_estados(id_estado);
 U   ALTER TABLE ONLY promotor.ptn_proyectos DROP CONSTRAINT fkr1o6tern02wdxq2o7x07dq95c;
       promotor          postgres    false    442    495    4185            �           2606    69354 5   mult_documentos_juridicos fkre5s43jgoad1x2hcmm8qq2hul    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.mult_documentos_juridicos
    ADD CONSTRAINT fkre5s43jgoad1x2hcmm8qq2hul FOREIGN KEY (empresa) REFERENCES promotor.mult_empresas(id_empresa);
 a   ALTER TABLE ONLY promotor.mult_documentos_juridicos DROP CONSTRAINT fkre5s43jgoad1x2hcmm8qq2hul;
       promotor          postgres    false    4039    342    346            k           2606    86907 )   ptn_proyectos fkrwdkht0qo6q6mqahyeaptso66    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_proyectos
    ADD CONSTRAINT fkrwdkht0qo6q6mqahyeaptso66 FOREIGN KEY (tabla_amortizacion) REFERENCES inversion.ptn_tabla_amortizacion(id_tbl_amortizacion);
 U   ALTER TABLE ONLY promotor.ptn_proyectos DROP CONSTRAINT fkrwdkht0qo6q6mqahyeaptso66;
       promotor          postgres    false    495    441    4183            l           2606    86917 )   ptn_proyectos fktjiupo7xc48ix9ee4qgg4kbll    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_proyectos
    ADD CONSTRAINT fktjiupo7xc48ix9ee4qgg4kbll FOREIGN KEY (id_empresa) REFERENCES promotor.ptn_empresas(id_empresa);
 U   ALTER TABLE ONLY promotor.ptn_proyectos DROP CONSTRAINT fktjiupo7xc48ix9ee4qgg4kbll;
       promotor          postgres    false    490    4244    495            m           2606    86912 (   ptn_proyectos fkw48p33k9r3of7tijsai0rnva    FK CONSTRAINT     �   ALTER TABLE ONLY promotor.ptn_proyectos
    ADD CONSTRAINT fkw48p33k9r3of7tijsai0rnva FOREIGN KEY (calificacion_interna) REFERENCES promotor.ptn_tipo_calificaciones(id_tipo_calificacion);
 T   ALTER TABLE ONLY promotor.ptn_proyectos DROP CONSTRAINT fkw48p33k9r3of7tijsai0rnva;
       promotor          postgres    false    4262    503    495                       2606    69359 %   mult_cuentas mult_cuentas_id_rol_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.mult_cuentas
    ADD CONSTRAINT mult_cuentas_id_rol_fkey FOREIGN KEY (id_rol) REFERENCES public.mult_roles(id_rol);
 O   ALTER TABLE ONLY public.mult_cuentas DROP CONSTRAINT mult_cuentas_id_rol_fkey;
       public          postgres    false    370    364    4081                       2606    69364 *   mult_empleados mult_empleados_usuario_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.mult_empleados
    ADD CONSTRAINT mult_empleados_usuario_fkey FOREIGN KEY (usuario) REFERENCES public.mult_usuarios(id_usuario);
 T   ALTER TABLE ONLY public.mult_empleados DROP CONSTRAINT mult_empleados_usuario_fkey;
       public          postgres    false    374    366    4085                       2606    69369 *   mult_personas mult_personas_id_cuenta_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.mult_personas
    ADD CONSTRAINT mult_personas_id_cuenta_fkey FOREIGN KEY (id_cuenta) REFERENCES public.mult_cuentas(id_cuenta);
 T   ALTER TABLE ONLY public.mult_personas DROP CONSTRAINT mult_personas_id_cuenta_fkey;
       public          postgres    false    368    4069    364                       2606    69374 $   mult_token mult_token_id_cuenta_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.mult_token
    ADD CONSTRAINT mult_token_id_cuenta_fkey FOREIGN KEY (id_cuenta) REFERENCES public.mult_cuentas(id_cuenta);
 N   ALTER TABLE ONLY public.mult_token DROP CONSTRAINT mult_token_id_cuenta_fkey;
       public          postgres    false    372    4069    364                       2606    69379 '   mult_usuarios mult_usuarios_id_rol_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.mult_usuarios
    ADD CONSTRAINT mult_usuarios_id_rol_fkey FOREIGN KEY (id_rol) REFERENCES public.mult_roles(id_rol);
 Q   ALTER TABLE ONLY public.mult_usuarios DROP CONSTRAINT mult_usuarios_id_rol_fkey;
       public          postgres    false    374    370    4081            �     x���=n9���)�"�HF�n`����pbp��{�-�G��n	�I�`>V�{��1Z~-DH�7��րs%Y>?-��2�BZ��saMd3�7�F�ԳH�h�����T��-�t����D�r��W���kyet��["�p�JǑ2S�qr��B�T�	yƹW'kb�u�^x��2U �G '���6)0+m'�m|���0�����2�$��s}/��.):����\���UA�Ży��	��)��-U�M�{m2)6�S"���%ī�eߋ�&]�0��c�Tg�:��Q�e׍6݋�W����	F�!6���'�s���+�9��h��Q?�]+�3�zFI��ٰn�'�kakTs|k��5��$4��Y�M-]m]�fLN���l�?����:�v��i��JN(9\Kk#��<��G�ك�-5.KF9�}�̙��'��2��p�� r�AK�Ȯq�1�pt���I��i�W���~�p�p���{]���$�S���=�bu���ĕG`s��~�%?,���#��d��w���#�G6�&=��g�/<,�=U���G�b}��l�$��xP��>-z����~����%�!+t�\���4�!UA�����3� ���ڏ���L ��f�ϓ��]�._���W�e<<Kԣ�K�w���g\A0����O���O�ē6~�~�Y�p�[�H�r�k*w�`��ȇpS��~�ח��ʕT虾��9+��h����o��������Ӈ�����%�z0&!��0���9������      �   �   x�u�1�0Eg|
�v��M#$\;������Je�����h�*IY�6K�3��,�,�BM��{0r_	�5L�Oi/j���'C>)ۏ!�"s ���p<8�-WWw]�ks�E��m�x�w��4����kGq���<B_�C�      �   �  x��Y]n�F~�N�/@NDR$-(��t�@R2` /�H�*dR%� 9G/��G����.�Z��r�ځ	۲������̮-�<�|�$�s�+3�W'v�c���t������d��L�^����D��{ߛ]Aur'i�4!���q��sw2]���p�Mع;f�<���#g2s�;aWn0]�������g�`�tiu���؄�;c7`.��W�L����̝���a��f��wϱx��.���$��g�{EJj�@�p>��B�������y[�F7�kP��8��s�w�c�
x�C���(�z 	�ޓ�|"L���}V�T�Y5��<N��E1��E�ℭC�VI����m�aqfۘ-����x�$Ή�6�Vi��Y�M�B�:��@נL��]髨+�m�|g+X]�XS�TZe(A��Gr����>œZǲ�r��G �z� ��B�'V*��\#v��h�X���T�U�|LN�U�&�Xڮ��h�w ��������rҜV��Pb���v[%�F1�.�"�٥��<��"�����g������M�^�z]l�7]�^�d���w����oι�G��g��� ;��8�kU�$id�9��!��+-n1�`Q!��ʏ�ݴ�P]�xR�{�e�x6�<v��Q-���K@�>
��.�d���s9
ܢ��W0�#T��c�~��}�CQ����E~Z_H{�y��ԫ1V�o�b��`6N� )9���V�r�̹�?I�w8������v�3�%�d�.�|��G�������ߝ�P	2�e���uM���oԵ&0�3�%M�`w��(*��(_���U�5w��-L���f�wEw��#Q���*��!��ڭ6)�d+��M��\*�/�BX���կZ� ��$�3U� OIن��?w�M*��f �,2%"ɾ��Z�QX��oq����h��$�$�PV���ǒC++Q����*YrT����`-Q������.��p�9 fO9�����!c����g�ޒd{+@���`��.v��EL�?���?5��ZR�
iU�C�V5�#仴݀��r.��R��������V)g���>}��+�`�O`<�U��k��;�eu�A��am����	q�uL�_�k��ұf>r���֑`*�Ao�,t:����B�u���A�nk�f�����+�ץ]6�#���Af�x�ݛN9�wY�}�4ۮ>�D<��x��T�8� �^o�/O#?����/��|���$�Ԟ"�fr6������l���°��F_���#3���	e��4#E����e�� ��H�#�H�V��qM���O51hRs����X�(��h)~�a��oy}`����QHk�Ӂ��}q�x�$�,ʗ`�/�T�E�\��؅���t*�wd]�����&߭ť��Uc�O�7a���rn�s|�h��|W��T�#��"j�u:rqe�/�d;�♍U|3���^y�X��Э	��S��n���������fz�|_�rY�إy��^���+��7ɑ��H�
�I�er� ������R�4G �=0�dʘF��':7�gn=������~���j�J'-�$L��[���4�1��;-Ĥ4
"���[n>2Ȯ���yL4g�ô��:(�J������g��`"���XZk=`Jh8z�񲩅ێ�j%��;~� �\���'?�^1� 2����Y�r@��D������p?�����v�'�d�����VO�E33�N?pF]�١u,C`����A���nD�J�Q=��b^����JZ��0|�4X�.|eP�����1J���Sb�G4�a��^n�#zM�AŜ�������۠�|*Ux/�񊕆�)o�
"E�ѱ5Q
�H'�N+{���C=�����Ň�<�,� ��E3.��W��}Z��F�$ݔ~�3Mxl�F)+/��E:��[��Kl�9�d�R1蝏o;�ο`       �   W  x��X�r�6>CO��R�s�i&�K�)7q&Z�]�ȤKJ��Uz˭=�z��u� ��t����v��L)C��h�� �	�R~���;f��o�P:%t��m�Ȓ����C<��y�>��#�I�8��P�F�8�j��*�p��<O"��Y%�T)�c-��{�V�]᷋l�g�>�rcėFܩC��#�,I������M<��)7���'I��u�� �����)�:4hJ5��ƿ�"M����
�1K-���� ����͓E��x~������*Y�q�D�M��k�p,Z5m]6�Z�)��?�}�cu�HqQ�>ęRE�K��m�U��ޕ�)��|��x~)���5�i�-ޔ�+������bW>5��߅Ԕ���Xk-��c�>��K�i5b*��v��@W,W���	*���d�ɫ,_�mʭ�q:�8:9	��~�ve{!c[�g�p���� p-�N�����P]BU��ʺie�	x�r��Zu�\�պ�	��)�jk�>�y���q�T����]�S�+������R�Vυ�|�C�����ڀ6�8y(���I���x~@ �6�I��_|WW_ʶ�vE[���*�G��E�Y0v�
&�����yn�#�g=�G)5N@�ev8�,��G������(vp�G�~sr����bG�؄�)��ݾ�Z����� �{�7^�=$��KB��^��S+Hb��A~8=��I���U���d�!��y���8���n[�ǘ-�}��R\]��TUXvRŵ�P�g��W8_B�X��G)3����&4���1(p�f�?:jԮV��l.f�|uԲz6��y��h�AS͗Y2�����m�;c�2��l&�:��7FɵJ��븁_�n���� ��0R��Q�����3MLR��$�V��,���0(�FƳ�$JK���S�S����nN��?F���ڏ�x�B5��'�ъ���eo���:|�l���2U;D�,�t�,�f`֨<�� =��@t�2�����7�I�����e��]���KƹC�f4��+ς�B3�y�

g��*�]���=ް��^��-�3��Tg<�e���;X�������*����KQ 檥�,f��޸��!�L����|p�w�R9mV��M7�Ձ���^�o�Ek:�Lɕ2�v��v`������I�wF:�[�R��N�z���ݏT�#pG�aO9Ӻzɇ�B�*�:�t���H;D���ۨ� .��Ӂ��9��(�TRW�B�ʰ=�(J�'9�6�WY�{}0��EF�2{�<Vap��+�$5(��}����O��]F��y4���+�]�iFC �#��U�PW�b'�n �! |
i��	ӌ��Fbimp�?�������x�Vx�a 
�qC?J��RdI�o��"C@hqBb。�	�5�^�&�R3-|�9�{@O�%��hۢ��˶@P/�y�4��e8/j|�t�*�����м<�V�80M��W����#Ώ!��(���G�a�#�*G]h��Ͱq0C�zD��#�OS��`��*�J�H���L㼉��*P��ޒ��@��U�k�7���[3bݼ�m�����ᆯv��Dڜ�h+���YK?��L&��?�
      �     x�ՙK�E���߃�8���\�8 �H���3m��ځ�O�{f3ɶ�f7ӻ[������kF@���O��woȻS6������H9���{d� Y�e>�v�>_�~��㧧����\<},��T|<6���P>��Cu~iO������\�_Ο_�u^|�<�����ͩi��x����x�J�������T�>�__���|ʚ[_t�Q?��l�21�鿀e����� ��>�#1������%� O��-,�I��ˉ3��#�<3܁72E� 	d��֡���0�\e؇H��!����'́�@(�[�T�$!�mڶ����G}��H���E�v�Y���7܁��,�}Xj#Wma'XB�]*�0ӗ�.i�)Af�����@:�nIJ�9�>iUx�a5�J��
:G��&~!�B��`�e�����%��@���#_zp3�eϽ.�aF�/Ӱq��`��D��Q��'�MhJ;�:F��e�%X��.+��2�(�Ī��+ �%D��JѴR�,�׈�p��â�oW�N/������䕀�IIS|�Ğ�����͌9���I�X'����u=c��Ώ!�N���|��D�:uR�C� �rd��1gK���ٗ����T��ޱ��>jD��8iT8,��,Q��2��)/^ihΥO|>T����9�v�*��z���C�����H��]���~�S��d�:�n]��+�Ն��NM� (eK���XM�2?�ƠN�������E�IU��3[��fj�s�ǐz&q���B!�
��@��4L)���x:��O���٤W2�@�}Z�o�X�SM�gb�.�7���!e�U�IM�*����.��M�CKTU��"�&!�פ,a�W.B�����$zH���[=6�c���$�ꏤ���Hc��;�*ߪh���t�G�&�jl4s�2dX�6���� ��Җ��-[��P/�6Bܨ���ߪ��Im��[B����8�ߎV-~� �(�+������0U�����4���/YI-7a_�mN!�ݒ� /��.7^�Ou}	���#6���/]��l	365n����bw[<�Χ������>�nqR�*�W���e��T�+�oGuC]��������Z��_�����Z����T�~�L��L���և�˧��M΁k��nu���%�𗣎E��qkһl7L���T˪�RǙTݒ�/��0cG�t��.��#��$p�=�($."D�DY��J��$��z>�פ�pA��s�ݠ��h��n�ks9^��ώ%����E�o"w��QT?$g�2��1\j�a      �   M  x��[�r��]C_�e/#q���0$f�1E�H)�q���$[dM�1�f��"��e�2�w^�?�ӍW�df&q��J����{p����>Z�i[��1Ϸ��_��qmcN�]��n"��|���r��)�%k�c6HX�t�ئ|}A�l�"�],Y��Ѥ)�^�*���'9��3u�4�2Gw��Ӗ=�Ög9���(
��W���#{g�e)eqzY��Y�fi|-X�5�D����)^"�����"_�x�!ˣ�q<@2F�ј��0%x�<zG	%1���1Z��1Ζ�9#�(��5%8%,&��Q��J��Y���O$ܰ�������'��%+zNf�9y&�r �#��i�ɜnx6�^�f���Rr��X�HLn�j��\3.ħ	N��CG���~XS�� ӰLǰN�A5���@�ul���Cӷ}}h�Л��ٖ󄥗��cο�JcX��~�����s� vu�A4�w�@A�����ݟ��Dt"[
A��-�tYBr�&O$P>�,��ʕ�mS��5���
<����0���<&�-�}��
����^��d��_�!���Js�������%�O��_f����>
n�׳�U��v�L�d�g�n���r�L���'P��1ej!4��p�3����2�B�3��{�eE��L�����}
e� �)����Ԧ�-�&�t��cA��I�&����^~5���&���Z��W�۠S0��M�	yA����>���ի+q�Nt��(�t�U�-��-.�`^��x5��Ɓf�g�g���-�O�T��c`^Y��-���ށ�`�CJ����k��R
�o�ˇ�b9�_#���F��K�]�?��L8�`��ȹ6��u��#L�)�xͲ���mh���ɍ�ʞ8lÀfz���p �������ms�xs={,nf�i8����Nf���W�hh&B$� ���UB��e�l�~��
�{[�G6�[�9�3�8�H/���G�7ڞ��G��9<�]�|L*�_��P9�9��t��� 8bs�9�P�y��		 �C�~C�*6�&�6�K�c�$�M�%Q,|�
N<�e6�?ϐ�>l�"�9'c��^#�|�)-D�%��wx� s!�bMAc&t��|$iN�k�7q�	�V�ŤX=��#1:�,!Z�s�RS9���
�S�5�#3P������N|,���ğ4k�`\^��F����,���}<�}��wj�L(O���'T�ȄR �kX�O
W:^CX�������9��%�#13}���-�CN۸w���tA��UH��Ei��*�0���?��¢uk�(ۈ���##������$�\�W	�/MaXZS$�2�RHi�d��%x�lK�7��S��M��f�r���h����1��������L��G0���o��2fk� �̶݂0�o����cPi���|S�D�&��FX�6M@h,�]���F�� �%�,e	�H�`T����c�y�7HNG4Y		0~��n�{fX�a馭����T���i8.^�w�� 84�*��gv4�/J3׾O�X��쇂�_)�yBI��
V�3�N�̻q��|b�&�i\�3�p�7Hg-������*%����T��p�qx3��^�! h���諀�&�?_�\��/^�\�	n��Y����]��Ģ��������[ ���
A�֒�^���k��uW�Y������9�V�DR�
袄�a�3�#O�Q"�@1�S��V9ϴ��h@���<��Wh�Q�4kXFI��=H�GWZ�1,�9EI�>��Յ �@Wz��-�2������c�<�[��W�I�.
p�]�0��q&C'��G����<���uI�B��~
^8R�R  ��yÖN�jq���b����4���*����|�ʪU�buA�RՅf�g��[��@�򪡲',��A�V�@X�)|��-J3�^��;��δ�ҵ4Im���cJ�%�V#B�#D��w#ڢX6�T���,��~x����E	F�7N�9JKeOx��ۺy�u����:�ʖ�������,;�*c�0�>4�����bZ*b�;'0�5�#bQ �bi��4?�:n���g[���ebʺ�/�+�9lz�)V�ǘܢh����{�	V��Y�yKX�-~۷MA��s��ڏ�D9�:E<�`8�T�U�t�>b���q䒸j���\Gw{�ݫ�"b�|�)씂�HP����f���Y��"=Y6Q�l�^>��""�WQ�|�*׮�kd�����W�����bI���d-סc��ef�˪��� v���&�v����v��\)da�իb�	dݦM�4JĂwL��vL�+L���.�|�K�@�<�A�"�f�Z�p��d����Y=�L�4��2^��p���p�VW>8�=i��E(��J���J�"+����j?2Zca��'���	F��d��C�ޣ��dŋ4?=A#�
E"*[[�����c�-L D� ���N�Hl.�yN���a�{OE�RH�')�ŵ���
)����j^�`+��䞄��UqB ��e9T%c<%x���{�$��(�J���W��(F]bH!C1O�
���"����F�8F�H���nY/����I�#�&_T���h��f�=o=��f��i��� �e�-����f�O�"����I����V��(��	gS���w���=�B�߶�¸���[��{g��r��7}ւ�-Ja�����k*������pL�Z��冴�+J�SW���~<W
}�)��q�1t�t�_�mp���{)l�]d��:غ��k�V�������9�R*����b���&��L��M�Xv�ɋ$,W�ԜE��U��&xdT����@�5�;�XL3uѫ\ewVن���@�.U�b�Ҡ$�9M�o�S�}iɆ�9�XK�����K��=��2OP3h(O���5�/J3ז)]��y�ј���e\��FThр��OSV-�B,܆�pN&w����̃��-����L�����c��b����~cCE�����l���@G
\�l����Xy�*����ph���mx,4�j�ɾЊʞƻ�g{z�;��
�"��&��Y�1���k�Y��o�g�4Hv?S����҈,�"�G�E9D%gh�9UBx�r�qE��a�u�Z�Vn�B#Qѱ�����}v)�J ���f�f���W��D�o����\8��u 4�H���g��"GG�8�{���j�P�3�"|̉l����G�ki���-O���]�b�����c��ru9Mm��ѰS��r�E�'���}\���?0�}��<������ڶj�ڼFلy�_f������<�#�7`��X�7��&�U4M~�,��,�E7�i�������?�9�-      �   �  x��ZKs�H>�EE�e"�xUz�(������ݱ�[;9$�ٞ��=�i���f=��v��"lc*�ˬ|UV�����A�_B�a[�AT�V���(�b����ᡇ�P
�4F���
S�`���eZ�y�������){��˃HqMSU��)1/L�˘�6+��D���M !���|�n�j�Gp�ڣ�(O˼�~���Ώ�H�j�q5F��&��+�R<��M���������|U`�g���6	q��ކI=M�ڂ�<}̱��I�����p�	�Q�L��-��}�aP����~�>f%�c[TM�u�a��QY D,�ԉ�h`^���Ĩ�g��ȌܳM�K����MJ��)L�U^@܀1�&8�b/�z��=�ĄI�NtÂ�Ѕ[�u�z��!N=�݀{'�l�I@4/eՠ��k��-�e�]e]c�L��k��h0�yhHA�0���߰����~W�GЩ`�7��7E���&|�`��`^����x�6��M� HU,�l�°���X��qa8�	���aR�����Lk�&[1�5�W{�T�yoLI jm	�P}�j�$�4ͬ��S�������rz�h�h��+6���%��&��7�x�|���0|����n�䋦+ʖ/��dEBqd3VR��b��e��P��
�,�x%�:���(%s/��w���_��~��C��:��`�"����b�&�
�2�� �q�ğ��9,������p��?<wa/J��%f���E��3�;����ţh�	�D�i�-�}pl��`:�A
B�i[�t2Q�'��C��4]`�]�c�q��,��X���-fnȖ�2d.�l�r
�j��f�0uږ�F{bu��}u��2}اߤ8�(�8�7�WS�|�_a<�n�߮r(��塍�9h��7LEU8]�Y�Kai�ڭ��A4"��}�L	b5�~ؒoٵn��0��y@#͋|����(@T�a4ڶ�Y~6󪑪I��!1�m��>�"���L�s䭌c+��+^������b�(��dCk�<��0��Ƿ�;ſBR�/H:�937��cH{\�n9WStn�:�����C퀅ݏ=?��b�ƭ!b�~�mBwI��Q{+[��-�9�Fwؐ����,�E�#��]��u�Cq���-ӱ�����.�(�P�b�wϝ_Q����}(�4�� ��H#�7<T\#ͣx�Gx�^4��H�H�����$j�mc���%v:G�0��^ >i���6)�/�:��#n"�������ӂh������O`t�pd��&���K���?��8�H�UKS���!u?Ww7�{��hCd��hkb�'�Gc{bh�>��膦H��fn8�C��n0>J�hzt�	�<��ҋ �>Nn�O��@XNw�+�謯�h���U��<��wy\�����:j�:�H� i�E��9�I>���s����®=Ă��ל�iL�5sW�D�/E�Րs_�>z/̀��`D�D�B�;���6*�'Ж
��/5iuKue���&��K��ڏ�)���P�6t̯OvO���,&`@H�a3���1�=0B �\N�8<�w4�z����`�)���4�0�>X��Eח@(�;�bN{I���Ԯ8�����a)��~
&��<����A��i}��a>3�Dn����2�m���1�h�z�uo���mŨ-'�HܾuQ�\��H�N^9�(�WBf��,*�BRD��BCZQѵ��ab�HLf����Sm�E�U���bԳ'��!T-�h@$�.�"���JCP�t��&�)�u1E���Q�#�o)tqQ�#���=�hnB�o(�!��U�0�i��&�+v0"*�~�1�Z&��ض�y">�lҠ����t�>.A*n�%W����lWmV��M�7���pG��+������82;
���3�o�m��S7U�����!-;x�u�FV�������<ج��f�o�9��UWĳ��w�?$Q�����6m�6.�qH�<��_�d�._e�-4/���g�J��5ߦ='���-�3;v��g)��J�s8�`�G:Q5V r���wM�5�K�C-v�aG�6��mY�I�Z�b{�"�8qlC%�[*���^��E� ���J{B�Y�<+]G�3,��}���a�� Ź|�T[ ���xd��5�oN�cV��F�u� ]t)��g���aa�T��eL�T�nw�2��i��xWM�PL[�\�����!�3�(�?�N�7�Pp�=@��ߥOi��o����!)׎]y��:�quu����%�R�0��VkE�;U��c՗�O�n\8�퀚ũ���L��>��9>=�ٙ�q�~�E�����@ͧ7;;#���A���3E�W&=u��)H{g��k���6������{M��N�k��I����)���Zw�Xӑ���*��+�&1D.�>$?��$�]L�0��Nn~f�^�񐾹�����������l	��đ^-��%I䍂��
R�$Ɩ�xq�$�/?<S��֗�xL����D�o6Y-�X�v�m�4��&�~c� 4,Ǘ,�!�Yzˆ����N�%�˴�z�"Wm��#��S�ܬ�[OZ ����YuE�׶��^�����������c'>M�a#�"_�����Z����b���(>��«;1�hf4��֟5�_�ޚAtD[M������]��j��Q�w�L��eۆNLX���/�z.L�@��}o��Y"��<>H�G�Zq��7m��h�Z?~Dhğ����8(S�G,�M��#D�I�з�أ�(-���{��n��n�4�ӴD��yc�Mi�"����S4ќ��f��p��m�o)n�ic���0_�Z�=��<,�l"�S4*:�x�F��i�quqq�?����      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x��ZY����}���p�n�l��QeP�1N���(��8����,������膬��ef�\� ��K���?���YO�ƾ�Le�׳���'��`f��XP��j�L$m��lYL{����>�J�n���ѯ8���&J#?��َ���EzA ����?����W3<@U;y�"�y�������J�)�S���h]�қf�i��2^&�����/R��������!XHX�R�gS�6غ�=g9&h��$�O��;Ahg�Ꞇ�%��'�3�0e���@�W�g8V��+$�I$����~@F���ñs��}��X�l��mO��i�=�? )f�u䚞^̆�^��s�た�I�n�7;����n=���L��f�	�@r����1B"� a�>�j{vׂڐ�����~�Φ B�<R�&�Y�j-�s2���XBL��&�WD��W�3 �U;~Ő9�"��P�����x?~��.�ZVlp���0�l��_X�C-��[���W�].��}���.��� ���e��,��OH��a��/���Ls���j9���A������@��"�+ ���7�䘞:�3�l�t�\�����nz�I���\�͹�z�oa=` �	@?z�+�G�f
Ѥ}����wp�a��,q65���y����f.
!eSY������]aW�[n�>s�������(���6�/�s������+-4�**vD��e�x�ױu�?���As*�R~���{�R�'F,Ay���W}>�<Њ�~"9N2�$$o���u-%��j��Xa�Ew�B5�G�ɲR��)�r�X�_���� ���E��H��N�v-+�z{q�ˏ2��P���9q�
�K��
���~��4�����NJ�eDX�Njg���`4J��$ǲ�f�~�w�#*�O` �� .Օ��uWr�:��(M�8�c�8@}�x����pr�5�%��EO�Y���}G��'eLZk�cX�?�9��M�C������rV:�:�f��Qe�q�[z���]]z���ɮ#|0����^EXިˇN�V\���4@��+��9H&�w$�[��}��-��͑���b�� B�SK�v�Cdgr���L�3�y��Y�vhI��R+�����Ib��N}��G�_��p�@EF��'����%r#���Tƈp"���ݡ޷�F�X�ȺtjMջ���k��⩎��:<�A>k�9,d�.���<�5:�w�Fa�~�o�J�<("ɋܳ������v��EK��T��U ��i�s���a!'�J�f�f�7��b/`��������r�ڝ`Ԑv�Π�zD���������Y�&^���{	��G��i/�6~Z�kb�uɔ-��oI����:�zWm=ei���*�ֹ��}6�:��SYo�=n��D�b4��k��l��k�EN�q��M� ��"�6uR.}��-�2��Y<���w��,��O	�����:E�c#Т[�wiiG&nf�p?��yf>ku;Y�š/�A#���^��TO���R�+R���3���˶e4m�>���z��p� b�B���8�`BE21�����
��aL\��9{5��Q�����`D�w��;�ÜX� ���$��nn�ħ�����K���˭+�VZ��ȅ�\����5�Du�����8^���O�u�%D�G	m���zO.,�;b�"���,��V��@�,ְ���� &<G�=b��^F�CLZAWo�����JصfR}>	�>Ɲ	#i���l�}��Map��@�z����7[�Oظx�;,D�r�#��q�/;uY&�r��mGY��]:���Z!C�eZ#�;iM��zt~ �~�t( r��P1c�ZCמ� ����sG�I�}���
�5b�>��f󠏝Ğ�%�{9����k�n��~P4��Z�"�X��Q��_K�7@|�틾r�7w;UO��F�����;�(O�l��'!ᮭ��,}5�u���i��FT��XJ���7Mn��9jct⧧�$e���K���p<�������IH�5[�R�qDx���	���(�=8��\[�v��$�d��6s�ڸ�ff����db��#X�;��U��L^ ;�JH��ӊ��?�LI��m(�zӦC�T��4�?E�9����$f�Ig&΅~׏�����ܘ4z*��lY��	�:���� /��F��nqE�R"y�.\��)�A��@P�X��o���Kb߭�ٞ!8i6����������$�]�ҳ�Q�<�g��Q��w���0=�~���F�� P��@H�����kIu[�j�Pk��? ݙ�Gljc���c�+U�Y(��:��ّ���@èyTdk�/V�}Z�XA�뙦��OȽB����ϭ���i��J�jC�r�N0�)�|�1�H��H>�RZg����-d�;ѕ'�8�l�t�S/i�Z���N��	��������3PH�m"��y;oC������?��'�ȁ	�qR���$l��PkcS��D�E�Y���,M�h��aF��+�ݳ������WAQ��h�/F풎y��D�x��7��E����MyC�����h��9������J �£�����N-y��A��<���n8�>��M�̌R=KA�%�%?���P�c)�ґ�U;[�U8^xn��o���d�"��6�p����;[���|���+٩��]M��o��]�Ήg����N�Q\�N�i�_�����	���&Y����D���9���l���|��[�e�OsG��� :g�Y�Fl��5�Z�6g�rܥ�G��}��?�E�"p�ճPlk��
(��_E��ǜT���<EL����q�v�ޙ����g����9
�T�%;��i�D�9	��e��b�B�.�����hsp�SP<���,�Dў1d���ݹ\k�e{[�6�A�����6�a6�Z����i�u��Z�HEy\�33��`�q�MK��U�B����b��4�e��F�ϴ%Y3�)_NR7D�"�G֬�ٶ�y3=�[fk|1E�*8�����.KU���h��V{�V�Ĕ["ߵCx�� [,'~2I��
oX�)�Jo��5��\N�F��Nx�Y���1��!o�:�Λc3�:ˆ:VeD߸ys��dM����Y,�'<�O]i�kJ�~�7��4<��T)�I�-�h>b�~0�E��}>���r���:���>׷bg2�՛zB�q�}�G�������@�3KgP��W�hOd%u�Wj�?��j��p�$
t�|j�鈫�P~�%u6�\w��0�xʎ�姕���^T�m�C�
�}c�wh_d��'�F�v:*9��tD�*�����Vn?��n+$���T�;�uo��!�p�]t�M��i��%��Iw�v]�l�Z���6��q�_ȱ2����$�v:�	z?�+�Y���7Ý#г�<\ty�iqu��rh��wFY��r�<�D�{TXq`v��a�NO&iw��c���%%�1mӀyS%e�uA�Y�#V%W+���T&�i{��<���fZ7-V���[�H����ګ�OO��c�7�ks9\�.�]�@�N�	ĪY|�,8��j��[�V�]>� TUre)���?���..Ý��e
Y�k�i�����n3�dұ�<��}�{����W�{�S����Vn+-=j�GdNm�0�TYh>rf�Ұ����Iw|�'���	�ȗ� ���E�i�Xd_�w�n�-"~ۮ<�Y��f�Vd~���r�І����5m[g��qZˑ���Ct�Oq}��Q7X���V��|�ɰ��F�ۢR�A��QV������=������z��~��d��G$�o�|c7�F�l�]����n}�hso_:�_,���o]�o�J���B�l��U�%
`tɇ���[H�T�L�>���X����'�13
㴓ԄX��4O:���~����?	m��8c�ť3m�ۻR�ۄ^��W����B
lbylc�ԄYR;6	
,u�i��Uq+�u��ł �  W��mc+$�o��xEY\���B������r|
���KD�9�����q�$��.�f����������6gR�l�`L�l���]4&�-C��G�9�B������H:�����k���һ��:�(H*�`{���َ8�nu�)3�o�1g~m�Z�lyk�Eܥ��/�)�s7	����]��~��>J��$\�mPyGU�f�j�c`xx{}��^��>_�׽0UE���ZPvG��6�\���d���t���a��m�eMf�!����`9��i1��t`rw����y,��@p=-��@(�A����J��_�tw��������������M����.���Nᦦh�T0�^��is����J�Ib`���̅����Q�
���q�w�'Gy��|]r���IJ�a���Xo*��Y�\F�������wZM�A��Ņ���;���B��7����W�Ո)^o��J�(_�|%��W���N�4���2��Zͱ�-�^.8�2�U#N�M6٦�2:�}�<3<�<&�'/������E5��,_z���K���a���e��|px	G4���mf�m�����58aN�Co�a'� �o6{�����G��/.��E��~O���?�w�l@�Ŋm&9
�}Zk���5�;������h-�U̉+.8AG]8��ß�{/�"%=u�|]���/b
�SJ��~|S��E̷����/b��"����2?~��n!�^          S  x��Tێ�@|n��@�����8��ЧI6�Һ�`�:��������9�̌�f|�TU�EI�˭�O��������ZgJ�)ug��h�f,]σQ�^~����B?�B��A��� �c�WR,Uͳ��w,���z ���h$ :B�T��
d���D]������Ei�mA~�G���cQ�m�v�<�RO��b<�����1���wc�,Խ�x�Z�f�hi�8zҒ�J������0ai��VE}�w����j�7r/T-����)��g��L֯��>_�okh���If<�=�d?�l���9��_Fj�������4��w;Q��
��2�!cJ��;��A=�s]��~�,���x���!�����[D�2٘�3�Kq��|������3ƞc{6Z�5��y��`�AK`W���@t��V��x�k���tB����D�E�ecǶ,��e���m�ڊ�/�+�7�p/'��U��gU�hH<L�m[k���T��Ȭ侧�7Y�b�Ju/��$�t��������ε�Dw}�u����\i�����7�?�^D�Y���a~U�Y�l�w��}         ]   x�E���@C�R�#��b�u�>v��	��f����X*'^���+pz�sqNo�-��8�ZE�ǗI�iKש�<N���쒤$)Iyvy\D{         d   x�3�H-��,IUH.JM,�t�4202�50�56T04�2��22�3��Ks!�������S�eז�������O\	�	\[jNfnf>]0\1z\\\ �7:           x��V�n�6]�_�/(�&��-�a*�Jv�A���0�t�EE���XϕǞI:U�(1x���s�|i��5!4񧏟>}����S����������w��o�J#�F�Vi��L���j�0���/�}h�}��������F����J!$�6J	��n����/<��"~ȩFޕm6���(��ZO�iE����
�����|y50��.��0�ab����
q	���/#OC����N��x?�u�Z(��Rj��L�s��sճC���eZ/�C�fN�^g�c�_�qX^S��H����ՎiK6��Q�K�~�Α�6�i��ǴCڛ��gJ� /.���K�篊�q�3O�ײ��ǈ��A��"x���I��s����� �����3J���1�2,Q�ב��j�b�8�L��ZwqpWS�ykW�����r�jlZ�tp�2�n5��Y�oX:g��8�LDi� m��93��R��i�P���xz_�T�:�[�&v3Q��y+u`F�ծ�|�m9"MEŌ0�ol^��#����c�|%�\\�Z~Gh�3�����+ޥ]�S>��]����
�]��4}���#H�{��ӑ���U�c�Ee���(���"����Wa�s8���D�Y^q�Y���7����b� i[X���~�P5J�����/�[Wo�T�Q��i��%��\�x���L�5�;�G�W �d��B>]yX����H��>�9=>b��������ta0���\/{���K46��|�]R��obߧM���v����(�!��(>� ԫH |{���"�%/��
�A!�e���	�r_Pe���>o�S�$����Ė��)RX�.�k�[ ۜ�)5k��ʁ���8P���ટ�L_a%�6-[d�z��xΠ�瑟{��X���ĺ(�  �-lu�~>���8@ﾔ^��5�ݥӟ������E�Aa�&R�g�TOyK�y}.���C�!�؋��!{�����9B��/�7i��%;p�>�f�֋��L`�ce[k��� 7�+aш8Wl���{�>ו]-wi�e���L��i�X���MK+�׸�� �:`HA���Y]��3ٰ�:��h���3/���8vq5g��)�Z�����t����W���.�sy��e�i��dތ�XLW�%�C�K��WΜ� �5n��rݢ���6f�&�9%*d��y�B�s�ύ_��y8��.h9s˔��B~v�r�#����¥��ŧ���c�������]k�r�F(�x�œ��c����?         T  x��WKo�@>ǿ�N(��ٗ��Q��@�V�r[S�;Ć
~=�f�8�c,��R�꧝�1;;��tD@4�`L�!�6J9�:A���!�|]eiͯ�xH"H �/��j��E�J�U+�[Q,�p2Y���r�E�Et��"��"7��F�f�X<$�]���4.*�x����I�+^�I�&yM���l������ӳ��������������ɧKq|q��G�Q#�!� $	�#� ��-M��Uۢ�Xmq�nG?z���[�`����KA���P�{DI IhTS�Ԡ�K�B����ߎ^�,ۭ�JANAy���Q	ߢ�`3�8Z��U�f��H%��U�`4��7`�n�U
t8��L����9��w���S��LJջ�P-�j�!tz���[� ��=�H)TF��f3���FK�ƦL$�����7�=�X�[o��:���l���h������)_��;W���DV,��h��l 3z��j���4���z�&����`xI�y��o20Z���L�����2(A;��a�у܀U;�nan/ص� T�Js��;� ��Nǣl֯��NrV�T��9��C���!�<d�+L�$�o�愔ݓ�#�%�n�|�jP��=��^�ʲ�"���q�<���_�"���\v��?9��N?�x���׿g�zz|}9[���|^]�sRy��='1�Z���d����`�'<f��o���6�>���_7.Sٯ�"����'��W�������0�f�̗/��
%h��=X),/�S/�<��a�W����ys��=�M�x�a�V3M)�B�@P?�A�J��u������0g���y|沖      	   u  x�}��N�0E��W\��Pj;i�e�IGHi�j��kb�HN,���̖�#	�HQ�s߽~�Ŝ��>��E�4,i��7����	|�cEm�B����e�Sv�ai�	�4��ȧ@��j�C��kjB������i�gT��}�Cc�dW�a�=����eb����ԃ���:fN>�b���%�$.�v��l�UQ^n�� �%aEw�vo�����+�_����w#���g��?���e���b�%��9��,�ݛg�H��[��U����{V���ް`��s����Yħ�L��8�� �1[=}�i�����x�����ď�,����򏧩���g���7��հ�Ϣ�N�AqhE��o'Q���            x������ � �            x������ � �         �  x���Mn�0���)r�$ER�w���q�:E�(���c(��e#Id���ǟ��\;���tn]�v����Җ���ڠ�Y=��C��XcRE,B���Ohw�q�8H1&�5'�_G�p����+YK1+�O>uo�p��A�4�u�i)�p���Sz2!p���3$:$ � q!�c�?B��c�4�������S���8�aء����R|�����V/IV����/� �"a�3��0����R����{7���s߿ݦ�Y�0�Vx�Hd&��$�nOKa$�[��CmJDI40���Ht&�U0P\
#	�!;���q�!�B�Yc�b�ѵ!>b�[N�f�`a���}Z
�@ȏt������ ��l!l 0?�������#'� ������/)������2	�ۅ�`db��R\�þ{�}?N�ak?&�Lu*��W�즩�±)v�M���ʆ�Hk��CXݿ<����Q6���֠]D�!�*Tvl5��-�Ɗ����QA^1����F��4O�-]���BUxw�]��m����ڼ��/(�Q�P�r�BI.f�#3p�Ԁ6lʬq��Q�a/��.���!ŪS��K�T����AAiPkD�UV(�)V��)q^�c{^�a|�UU�:��            x������ � �         1  x��Z�n�H}V��?`��=�-�mf)RC]flXh��g�x�I��>�����Ƕ�yI����B`#��:]}�ԩnK9�5!_��<Z�l�27y��?7a��3b����
1:&���j{F��?�'�H\�S����c�����R�m}�J�,Y��d��̭'n�H�2�+b��Ja�֥��GK ����H:�<ք�h�8#���
�̯\��i�u��V*�>%FP%�Y��:/"w�IR��4
s#E��/QR�I7�*��i��gRIq2I5������I��'c�\Ĕ�
�`���I�Et���(-(����r�8���N�t����>�7C`(�[]m��$+,���X���!&y���i�&���h���*�E1��B)���?#������g	28 �U'3�� L��|����O��w����}���������?����uDՔ�)W1��
 Y�*�O�?PT�s���Փ�"_��I��M�l�ADF����rӿF0�-!��K�97������?��������n��2K��P�,�j���7���2����A�E~ݓP8�"��S�yU�F�zFk�T!�(Nc��B*�BSl��a|&p��7�\u�ˉ��'��v�,c����L}�r#����>?�"�*Yk�a����e�d���� Sﱛ �W.��B2ʚ�����}���#�Œ���.}8~+f�h9THm���Lԥ���=.=)����SRA0��ۿW?l�M4�N��
���bU�4��0u�sE��"�	| �V@�[=������nI�ϋ�Q��E���¶Qw��Z�*E!j��PT7'2�Ș�VW&l�����ph0\>�2G������p�{w�������LQ�x�X�'��������?n���`���A�6�ԤCC�%R��Y/�������ݧ�>~x|�������om�����R����?�PL{����I��h�����X+�!Q>sfY�D����s�]�����!M�mc�D$�q�l��2�S�3�����ԋ�\3%�s��y�Y>���@�K䏄&�b�*���:S��:w��YEt�#��\m��N)0��-�A���O���d�@�x��h#\�@l�ę�`� �<q@I�<>�[���tw�nSF
�P�%�� gO�!��ݩ�Hi���n�~�51+&¨%���m��b`��Il8�Y�¯0\��"�I*���81	t�qIȨۈJ�)���ߝ��H�KkV�>''H�Bbet)�$ �;sG�)������P�k�5T�0�@񝀣$��`\c�+2����ŝ�yTS3슴5�!��77J�0����L�|����)1`�YGWj��ϯ���
�ږ*8&0M�*5�p���cj|6�)u�@��K7����T��[[��#�ZC�eћ|����/)U\q4-U�{�L_�3�f0�M����y0�I�,����F�-��U]�H��-�����XM����~+p=����$$��vB7o�Gמ��֫�7��}
ᨦ�Ԉv/�N<
��1��z}N��L!����/{8��LP�:�U��m2
���Uo�9��j��'����Q��A�y#K�b?E�&!��]�%�����m���
숆)LU8hg�H7��E�������-#�G��1����`1���xI�g��}��o���^Ӝ����c6 *��% �B��_���9�,ݜGs�̋u��#�ZzHf�܀rFy���/7�lt�F�d}]l��yZ��:���zſ{�A�a9�7B(�D���T�C��`�i�R��4.���C�j]\�d��n|�y�橿�&��ö��;�=j� �ΗPig�]�AN��S�c��!5�P;��`�}����7�Ĕ�D;,'���)$�������}|����~���_v_w��r{�������&�[.�z���-�rVWn�	�(��f�(!��j��:����E���2����=�t��*_/\�Ɛ���C�1f��!j��pDR~v�c��.��"��:��揣!�u���� �0��i9�5��|�?�����@��C�����9.=aPDu�S�%^]��)��~f��� ���I��Ҍ�SgA���B�"K�-M案�'�p6�,ֶ�]+o����������SqzL�Ñ��x���YJ�g�0q��%p_1��`&�2(/�M<1�h���(&Q<n��?��䰼�1	�a�i�Z&�$��^G�� ၭ��7��p�`a�`Љ&���C������.h,�h����C�j<;����55�R���`�X��|Z�~�������ڏ�=E� f�#�/�P�\p&a��gTi�$f�e��ɶ��6���7��Y[
��m�j��Ƌ7�x��Yu�dB�(7B�PqR�5Q޶���xG�<['C�^�P�F"�x�~Ъ���f2����#����3.7c ,C>ꆏ�a�Q��C��9�E5[3x�LFf�J�ޖv���Z�Ұ=ayҎ�^���BBܺ4/6�!hm͈��<���J\�B�v
���k�w�e8z�f�ʨZ��U[��3�JCsZ����C㝩�tb���"���W�B���g�Ip��R�đ��*P72c�"�p��L'��<�M�n������� �182��Mro��Q8Vy�{0�z
&/YH}�s����T�>��rN����Pvp;^��φ�ixgcn�_Cс���h;T^�O[,pD��rA�_�9 ����jx�z��6��         �   x�M��N!E�Կ(�o��`�8��gVnP�C����{���m�:�ގ	&6���N(6woK�pN@�D�L���?}�)���`���洤�3\�y�$'�{��?|q�+{�'a/��\�Z-�-،��Fw8�
7Dz��-{�%�mK�n���?�-� ��#xOƣzP�a��G�^��G���Q�6��r��B���"vv$�䲦XR>���|
 _֊U         W  x���K�9���)|��&U��d�A�0�q�P�TV��po��E�?������@< ��ߞ_����MA���o7��&�x�4��x�ƓƌWt�����K|e����p�-#*�����=��*-Hx~>��E<��b���a�T�_���7 �^
2"	�Y;Җ`��`&1
;S�H�G
 �T�?�Nr*]$ u��Ns*Q����p�S���xTQB��Ns.��pQ$��ܩ�E�"!�iU��Nt.f�kw2A��K���\�pv�#��Ns)�S����wb���a���E;ѥ�����8L�;ե�#��f�N��Lp*"H9ɣ�w��H�si�H���h��P�����.�M�颺�*��j�i�ʤ�\T�D+#����0���מ�/�����Z�R:@��%>���9S,jiz����5�8X���5�.�g^ ��hẶr�7��
��b��-�>HW���X]9����Ȱ����������}��y�;O 7�$�]|������P�@Z�y���g\�����1�I���?�eL�C��QS�4ϖ�y�$&�����2y��}��6!��^��B��H]N���R9�`�LX�Yn�Z��2�2�y� .��C>�
h�xUrhޔy���s�ἆˡi��`[��v�	���I������^T]	j	"�3������-<圐�7�45$�ލ7=��ƪ�1LwJ�i����<Or��vZKO�Zȵx1�˾K�ఖ�J�n��Wm!�|�3�@��h����;�O=�ހ���{�XA;%G� n�x��A��t��\��H
�������{@�           x�]Vɑ�<w[!�Q+P�-��~��J��&b�dU"��r	�$�$�����"{��*y���m�db;5���hΪ�{�d����?P�bz�<E��Qs�-�J����;��ee��M��ʬ$FqK�J��E屸���MG�;��J�m�1���>��B%$[�[��(^�/�3M�ʛ$I��L�H����1fYT/.�a�
$����_^����]�L��L��o/qO�y=D� ֐[�o$pn�{�k����Ɉ%Y��bYO$y�V��=���ˍ=~CI"���E�\�[��L:�Pe�{E�V6�ij��(����	C�K������&-��d{�1=	���uWn1i.�o�T~C�K�1+����ة3;�Z����"��u�v�BD=�_��M��\۲��G�}����l�!�8n��T�_���7U9_K׎�FZB���B�P�\#낤ƞ�@�䙈o�Z9;r���|��)�<�Yd�t�2U3��=���\�m�7��
;��x4��!�y�����Ә�+š����+Aķ���b(�mf˗��S���p��w��<��}u�~x'�M�����V6.���d#Kj'ѐ5Ջ������Y�Q)˱���B�}%RGx�t�Nʹ�%�p^�f'����}}��Wh[Pc�����58���]+��J>��ZD6��o48�����Wi�|�!���Кع�Ijsx���d���V^���Umx-i1w�D멵n)��e�}�OH`ˏ\Y�8[�!l9۶��2���W�Ȟ+�����vAD��G���6١�z��v�j@ M�����4vPo�CB���,�0�E;�$��6`�|������E��s����v�(��x=ȇ�{K�=y7�<EIM3�
t5b1�ʮ"Y����*��=��kwke`*��*#Pۂx!��ݡ�o�!m�/��L�h~��Ac�B;z�"~v�������Y�ēU?F�;��B\�����KB�5'� �R�I�o���j��yy�(��h��t���45�[:ѐP�(������������������k���P���j����<'�@|܈ֿs����^��zMRq:ع$�6O��۰a)��Q'��\���k��
(s^Gw�2���"�PV˴����`*����!:�P��q6�����Q�sa�ς��~C^�w^{pT���)�"!T�A*��W��9/X>�:'���Z�ųP&!�Pg�y�8WE���P��Z������ʆ��
�c\m�;e��(h��X�3�Vxǎ�~"Bk����PLg0\6��pp�R/��QU_}YV�� 'Z}7���:�{pL�ž�����u�d*SW��ȩ���)~�|k������P�q��iZ����L�ǂ;ʻ�	;V�4aJ��T)w��@��^"8o��g��&q0@�La����[�@SL?P9��N�����\JE��|ƹ5t�tƹ���ġI�����H�_H����.|"ć��s�A��^�;2.T���G���s��� ���=      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �         �  x��W[��6��"�K��ןs�� U.��dzHBK=6����x�b�SS3 Eh4���L<ͼ�TdY}����؝���]���P]��]�8��/h볇��,֬F
)����l�ȷ�R�=s�E�Y�5WjZf������v�����QN��/jU�X�w+t.;�ݎ��=���"�=�y{:��N��&l�8�����F�OO�^Y�3n��n������xPZ�(l�/�쫶,)c�鳪'G������� ӾL�:o�U�>���6g[�F�lv��-z���Ly��֑7�M�b�������9QҢPl�h����b� /�xB���1w�Nz02'�V�j�a�	D�rfX؞V��x*ju��/��\�(/��33ol����;�D�d�:�0; |�̹Z�s'vIy=��ӡ��{�{�k�r�t��c����ჺ��y[eo ݗ�j�����͛*�"����M�T+�OE�y�E�񙮉c�ZQW`�:�)�	���gH �+��L�����B]O�����s�+�]��^��=��A}Y���?9[�3�r
L	�8���k���4���0����:����U���fJ@��	�K�	-se%�G���̔����h�-�i� �N���e��ТA��t���R9K.Z��|�����E�e��Ol���O�ZE���?K���_��0v?���3Z�Y�O;���l܉��[�Ɣ�m9����V4���-�8/�\���$t^��z��6�PU��+�Fv��	h�ZO3HLpZ#�Љ�4x���7N	s�4M��K0��Zgʑ��A�<��[���Q��N|�P�hV�[$N��Կ�W�9J(�xB-I�����˪p�S��3�F�l�S+�5��>[a�K�I]��ch��DPС�a����z���'
�@��9 ��s*���+�4���M�?4��s6��y[�~ J�)�E>�b6Q��`<�Z��dj�������ZA�M}M��02p �@P@�[O5P&3P�I�m#T�?�Ђ9n���^�!��n�C3j3}�"ე.�2��OAU�	���c�cJ��1���3�2h|��h�`�t���� �@�y7O��	��;4���@F���H�""?�M�Ii�,G{`C��7�x�*ɬPύ������W����Q��1�^��!�]��I�)H"�yVF�b����٬���1fyRg���ܣ��ϳ�K���y��z�`���E�P'&R���S�}��a���:�rl�U�R�{L����hGuۄϱhĞ��i�w �S>��F� ��iC�96��Q&,�t֫����_�b��|����av:,��=qRKd�i�1A�$�����<�v��GOg�����q٥��L���>��O͓H�82.A)�2�u�9��t�!.p�zblĚ��t�-�'�4?shU�P~���B�j�?v߾}���D�            x���sI����_�c���X	�d�m$k��Jf�" ��ۭP$U	�]UI�����8��|�C6|��F4�ؾ�Y� !Y�%�d���?^f�|�����~�{�WkZͥ�S��ɐ��_�ǃ��NT؃�a�;�C����@$��C�"y^8��>-�l��E�cgۓ�,NN|��w�o��+�I�<z��dXa<M�ܙ�/�����Z����\���2</���"OD�e���d5�L��&���A���[u���
{����U`�E��Y���ߜD<�"D"L��.Y�@�]�@D��:х�M�0��O�ߏ����#���"C*<�*<�W'��`҇�e0`q�>/�dWJ� �9�!�L�ʋ�}����D��P$%C#�%���\��$.�{C;C��h0��'��O*��+���J[a�i�����Y�6��g[�=��ʬ�=
[���-i�M��ହ�G	$<i���r��ӧ��i���a��?m�/�5�Z_��j,ߋ��:T�03H&V\ۜ\�ᶑ_��s��e��Ӊ�y }h�}�`@��N[�X*fiT�o�/�y��)���;ۣ�<z>�v]��vog�{����q2��dx,�DB�R�A��;Wx��1f	��~�	D���n�z�~�����2ؗ�Q���d_�Ρ
Lz��,��hM~|�V`�P'xבp��{rG�{;�]��M�z�U_���:t���lB�8�������T�lJ�Ѯj�umO����d �LZO�<F���Q���DE�3�Õ>�+�C��y��}��]��߬q7и�9K��
��V��q-2 /�)���X1��:�`��&�Y����N�q5;��7�f���?	�8)���,�~`�5���.���k���E�L���^�i�f%��z`��.�����pBZT�-�߈�{⍟\0Ge ��g������M�#�?w.�*(Eb ~YD��'i���N5�%)sS��� ��>�.�I�\��kO?~�^������Q
DC�Q��}�f���>�l5����ҍ?�	��"
���S�#L���O�
B2U��M�dS)N��&Yag�x@�����
y���W�n��^M$L��]��kg��ZbI_�j_)�}�Qx@��yp�f�Yt�^.��b�e�͝��hs怘:��]�K�"��f7�Z�a����ǆ��mV���=ȑU�fw��j�j:�]�\f�ʣcO�<��I�o�ƃL��e[-���s�Չ�0<�,��SI��J1�v����U��(���<��M����g07>{�x����k��թ5� .�3�hY�.���z�B#\���I�����ۢa�^:��<O�(K�t�׹<���y��'�l��܇z�M
yf��&��8ZT�>wEO�7���GՍG/����������gk?l��Q���du���^~��ʞ�4I{B���0~��;>��X�+��V��:���U����w���I�],��*���l��.�_u�o�>Sw�px2_����%+���l-����ia68�̵���I&Q���TG���i�����_��3=��O���$w���_aǂ�^�ADԗ��v��J�{��� �|rQ����MEb�
�)���][/�� �+���W�*
�5(�c"B((�����I	XF��M"�
45��V�^��̍�U(��"q���L�NU}":��	D�вQ�� 2Dc��x�ч�[*u������{�k+fXȍ�C*�G�A�'��]/�ݔ�X����HBªȪ�ᯩ��T�}R`���J����'�i����H���
t\X^fo\5�Y�0��R�j2�)L���џ�0�5�6mg]X0�P���h�"�؁W�ҹ�!,�tK ~ᯅ���"��PA����j[^ C,8���>�2��b�b�dd���	׶���%���C*ː��F�~qm�8�wk���0��Ȗ�1�IݛY�z����
��C�@W̍�cDVy���E]���U��!��;��,�bb�/�W�/2c��Ԍ��O���M��+ {�w��urrǆuw8id�����S�%��a3��'	w��)`�%�5�rf$'S#��g��bƱ�όc�0J�B��,����Zd#�A�w�֥L{;��!�����"��3vc�W1�E�����h��}>y��ygƮy]��S�7��`��1�s�!��b�k�����5�m����m�B�a���52V;��7"����5��AՕ8����c�BZDf�
�d�T[E&CX`ꁃV�ZO��a�N�]�c79Af4]�ɸ��0ٸ����d�u#a�8L����aF&�+0;@qP�;�������d-� >�t�x/W�GVӪ�:]kI�r�Zq�P>���[P=�B���<pa]��{�.�`3�.����`������qMrن�ˮ_�@��x��K�NE�j`'~�>��{?���+�����?e�fA
Acg��H���z)�Ap��M�~A��Vq���i��w�x��McgU��|�
�_m�q�4˛:Cek`2tn�Z�w�z�4ɻ�A�l�P�ps�lFs����	�*Ɠg�.+���=r.���~U۵���^�jUkN��2#�.�+��p��Mk5_��
���b�&��&����q�j�{�{_e8	�f���ݹ7=��ks��F�"�Vuv[m�'��u��p�M�g���4�y`���6{P�n���Q���Dm��3t��jCd`���k�����W���e�RY��$*�2��$#�!22�Ȥ"��L*2�Ȥ"��L*2�Ȥ"��|{*�/ᬎ�aOѐ}�hZ!��F<� E�!�W|�eu�����ݬ֬�RN���ɺ������S!8&ҚIk&��[Ԛ!V�b�C��i�m#a���mhF�a�4��J ���e�S�0�=μw�B���|��崫�C�j>bs
5P"�D@��%J�(P"�D@��%J�&��@���f�W�K5��Y�J��H'��o�t�	>�#p���/�5��k�:�S�i��.�� �t�#xš/g$�9����A�6H���	� �5o���(�F*�����w��l�1��ǧ�j�Cl�FI�Ҁ�C2�&v,I�$��K,I�$��K,I�$��K,I�$��[,��k``V ��lU����Q� aǒe�ɩ�K�.y1� Y��u,}M6nB�\/_�,�������e�藐���m2��"ß�~��8�$�)��x�7[h(��h2�_�I�]���L �L�CL�r����a�TD���͙4���Wcvt����L�C\	61`~^�
nv�j��ٌ1���tA���s�^u������N�i�������~��m;�����_jo|���#O�+���H����8إ�z�,��سB_c_�����NL7C̝�;1wb��܉�s'�N̝�;1wb��܉�s7��N!��Y����,��}gvwPg���<�|O���Ɵ���C��HD���d��v�� ��m۪�~�vf���,�y^3;�������O�3s���|��.�h������e[p�X����r�J�7�ܱ2�	��N���~�J��I��]����n4��}\7���P���p�����6�h����_,tه������8e2�H�"1��,�H�"1��,�H�"1��,�H�"1�ĬĬQ�Oim
�=x�����ΏG-��Y��'�>,����~R�H���T��)7_����	����S�M%��]䱌��<u�M%W|�Ε7�\-��?��M8X��*�`w�9�=��4��d}�� �*��5+V �����w���xQ�ڬQ��mu��[W�lr5��f������'>O|��<�y������'>O|���5�y<��,y_��D���|�+���!LO��k�����d�=�7�Tk/j����9�>��4���v��/�>;䎕i����B{^.����hn4��=��M!(W�i~�y���"�Лі����oV���x��� '  �2R�����g�Ìp΍�����jH�!����jH�!����jH�!����jH��'Q7����&���lvj]g�m���Nc�mCP�D�˒HH"!��_�Hx-g�d^w�����S`�Hdv��,�Wg��f0\�0-���$���~���u�Y�dG��
Z�����橡H�Q�c:p@�f!"L��*c� ������9h�;�Mԭv�a�6&C��H=��?�ND���'RO��.!="�D���'RO��Γz��gr0�Ƌx��=BX��n�{�j��)��V���%_Ĭ4I�Gv8�D+:S��>}"�sO����U_҇��8ǛD�v����~������x|ݠ�@7�n��	t�&�M��@7�n��	t�&�M�����	��&3��A�U���9���|��>��)6��E&i�01nb�ĸ�q�0�i����K��I��e��1Ԉ;�>b��fڋ��E��83q�;�d�3g&�L��83qf��ę�3g&�L������d2C�s�l5��>2�H3�f"�D��4i&�L��H3�f"�D��4i&�L��H3�f"�D��4i�,�ܶ�L9<��ٵ	7n&�L��p3�f�̈́�	7n&�L��p3�f�̈́�	7n&�L��p3��+�f<uV���<�_:l�Ĭ��A�	����y�U㍓S G��.�gb�w�=/�(7������	O*�zj��9��M�{;M��mw�y� ��P/�z;��v����^�b���U�gW���_��������]��K �:dy���]k7OT�H��6�n�e~c��!��Kv�c��ί�`�\������|�&�}�w�D��t�&�M��H7�n"�D��t�&�M��HwN�GH��F��j��:^s��Zn�k�m�Y��A�j��ƒF���b�C�����87��k�׳(�i6��/��W�������>	�~��L�Yg���O`&S�Km�"���Kd0�@��9"���{�J��c�����'��#��H�i*������3������>�zN�j�9�{��dXmL��cY�u(�%��JޙW��e1m�9�6{����'��2[V�,`my�0oҹ�˗��U�مf�ɮv����;`-;@��o�Hv�Y��@�ԽE����0��XǄ7�n�J~�p�]����Z�c�?،����}1UU/�o�D�v�m;��F-����y�U�~vqU�aŽ0Q�ǜ�> ��6\ׂ����+M�Ix&ᙄg�Ix&ᙄg�Ix&ᙄg�Ix&ᙄ�3;�"@SN�V����.��_�������Qo��      �      x������ � �      �      x������ � �         �  x�͔�n�0�����D�c��w�!�B��7C`�b{�=�^l$M[T����#K������9����؉�,�����F�^=T���LU4_��_�Ǆl�\�P�)�>�Eg����K݁+u)�H�G��C���7т��L~�+������g�ĸw[�:��\�)a�*�`��FB���<�Ɖ�I���㻌E"����6������Z��i8:�.u��ə��:s����ݷ�4E�a_��yS�Y̵��Yw�?S�J�a;�bY���f��}Q�/d���z8��j�v"���d�l��Z/��y��s��|�=������������a��6��L~S�)cq�0��>0Ʉ�Ι�e���m|B��T��ON�	�?��S�"���"D_6��[r�P�T\��+�Jצ��m��ě�b��vK~      �      x������ � �            x������ � �      !   �  x���]o�0���_�?��؎�\1�M11���@��Ĕ@��M��9]�����1��MU�y��<�S�ڬ����rĀG
0&��BI�����풮j���̦�����WYj���i��+i��ڎ,m��6�li󌾩�ޭ�wu�ݜ�^y��!8�䂰��~t]��ggE�wY�W6/l�J[d��U^�XR5��eeZݵ�t�vqP7UP��ʳ$��Եg��@���6�x�p���ڭG�E<V��0"��k��os��[T]�<���{�K�Ox �$J)^���n�'�y*���('�F�(o�I�i�*� O��̀�Њ�l�8
���.f}��q�p߄����y;B�8=��K�(�A-�<�PA�
I�)���S��
��H�{w����Ɩ�w׸"���b�����&p�B���<����0�qm��G2,)
������pN1�4CTbvnv�����4J/�(���N}�A��y2�04GR�6Q�6�<�R ��]t$��a���*��^j߈��
��5J'��/ �����������~s��������%
�����1��m��]�εY��cg�.��QeX]�'�h_��		R��E���Z�d5����PH��!�1c�7�X,��(G�      #   :	  x��[�nG}��b~�����<m6X�{�d �-� `�%��V�pHi�Fpϴ �l���T��t6�lP#����t�Ǩ��ͯ�����x��v���������]�;t�O�O����?�ۻ���n�ʿ����.��� G7����w���	Z��gt��]��(�}������w��vߏ�����y��=��o�?Ӆ[:��[	=]��BC�Co��h$h:����p�����7Ú	h{�~���~?��}���9pc��Q LoQ� a�;~���SX>�N�S��x�;ޏt�~y�9�n��N�����aw�~��8ǘ�xu�(Q�*��5*�f�n�}~�����=��Ӷ���Z��m�����G�̦`�l�`����Y�&��1�<��!0KI�bR� �Y �	y&��z�[٣����iBNq�i���R7�z�4��QN�#�WDCZ�x��X "��9 9���v�T���V���V��F �t&���7V%Yʟ�f�f�A�)GN'��l�0ҝ2i�gi�J�Ll�wZ%Xk��j���LN���9���4�8}��r$��_|c��yTF��p�30�e��#�IkO�|H�f������RJh�׻,�t�6���6���V����i�������F�x�����ݨc;%�ހ�6݇,�� `rP܂�y1��1�� ��u���X��N�+��o陵O�-��,@�\R�_z��!���co���cg�L�H�sAa'�j���R��*�ڥ3lf��GK�W g��3�n����pF%b�X�Xf�z'�:�;J���VB�,��D�K����;J�pAgDOT�sNaSm~��Q��N��N!�)���Q�`r��� .�ʔ�-�Iᤷ�ip��T�=�!gW�N��&A����I.�]��X�4X9f��KY����ki���GxW�.U��Y}ȚO�c��l>J�5f�!g�$b`���	g���3W9�"Q��EN�#��8�`��mFR�:} v=s�B�CaX�kfR���\ �F#J�H��b"��zOb}쟪��ȧ-	��d����x�!��(C��:�tռ3t�K��^j��%K����Ae��A�K�F9�Vb��&&gh���
����i��L�KE�_��m�ϔ�:�9�蚔�R��irM��bX^fMT4�u��U
�a���C�,���'!^�T.�ެ��[�S��p6���dJq*!�Ԥ�$�h��˙�+94�p�8���M/�Q�B��G�&n%E*���y�/U�$*�5�j��mˇ�e��"a�i��-��"��G��M��D��˔��o�6OuR��ӌ�>�a�r�ļJn�Z4�og*�Ĝf$���v&�N�gFR�7��R������&�*n5
�Q|I��ÛIC$
�Ӆ�2F��3���(�"y�2Z������@oB.>�^҉�S|v��[�{����N��Ո�~|.3:}���+Mh�ES����[�A6�禤8�x8�:��v��-2LDF����T�4_�|������*��f�!�����~�����w��)iy�)M��yR��(��Z��'5)�e5��j��o���\�e5ߎ���fX�}��_��*粜+'V-���T����m~�Wvuј��dR���˝��v���ͻ˩����cGdg�����n��mH�hA�V��� ���F��Q��&�� �&��?��?��(��#S!Q��% %��Y��re���D\�e[�"�H�(��z�A��\tCk�b~����w�y�R�����N�D�CXF��J�ܝ�P�p#؟��]=!9dQ�'�Ӥ����&��{���f��{1Z�OeЦ�ȱL
R�y�"��Kl�]��&�`jsD@�4vj���ѡ>�Aj�  1Ԙ�JK�Dq3w��3��9�ȑ����	H(��X� ����ŋ[��������Ǝj���2�y~qu_d~�ā~\�ny�(����?4y���ګ:�~���x��L�
�%��GZ�CT xB@%U�����']�`w�� �}0�
���%�F��a�7�n���(M5����E͔��񟛹��W���VCA�TS�Ĝj~2ZL:��C6 +OuWN/ˤ��WV��|�t"~�MR)���2izB2�Z�uo��ӗ���j�,v�U����0���k@�^������X53����Fl3�&��$��M��r���<k�F���97O��i�Њ6��,��W�2q-�����Ns�����|�q�u������n*�X[<'�맂��S�|�~*5WVy������
=�}A���@��M^Wg�VP�3m�͊W�A����\J��      %   B  x�m�;nA�xy���%���o��@����	��oឱ��e�8(كyI�B;#��Is����u{~����z��>���W܇�AS#"N������k��e~y�a���\\IFܥ\�:�QSy��rh��"^����0����ܠ^�0U�V�]�&� �/�7��wo�gC�$�u��r�� _�yW3(8�k��9s\�R�~<�^��?�^�~h�'7AYy��jv��>�s�{�c�9�Y�V~�}g��?��qF�o-SK��Wa
x��ẕr�ש��&�-�ͳ�m�,��m%���_��7D��"�m�      &   �  x����n�0���S�E�c'��!*!�{�瘁�M�L%�����19��b@��ܠ4�3Ɉ�F�D����~��W]:����;�w��+k�2B��̰�,�$��H3!5J�1SFQ�1��Av��Y��|����x������;�����_�Ep���a_J�I���#	�ƒ�粸��0�։'�*84�����5���]�k��iUF!7����2�!O2���e����D Y�H@�����B��B@�r��Bꯎ�ޕ�Q�4ܔJ�*K��)�� Y��2���Ms��~^��Mc0F���J ��mbTD2��ٓ�{��H+�u�����,��qԶf2]��٬Xˉm��#@��k��V�8��c����J�b#��� ~ mȉ&      )      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      +   p   x�E��	�0ϙb��r���SA���̌���q��y�@�"��^�Ń���ǓU�Ů�&ZI4"lA�&"mI�@~���)�˱;r@]h��)���m�Ͷ������d�;�?��;�      -      x��]͎丑>g>E]|ZT�I�f�ɗ�{l`ў.�k�S��*�ϴϱ/��HJ���Au�)U��D�#�����__ޟ�����=��������@/�������OF{$�����P�o�_������Ͽ���?��?������~}z����������_^�~��������/�_��v{yz_����o��?�����o�o�����?�������������?1�h���?_�?������|db�r&(���ק�.�qڸ;����Th�nJ������-����S�ݫ����M=���)걌zT��SN=~�߫g��^���xF=���i���WO�՛)�~�S��)�����%38���q���3�SHHA10>�P03@�j=s
NN��^�)Q�?�H�W����)ȉ��T*���KN����u~7����,ܗ�7������y��G��+�s>�pyZ�����vÁ�Gʶ�✧���@�}V�TR���z����l���:}\�?ʉ�5��K��N��?r�S)�q�������==|~z�����1���$�2<�;Ŝ_4����̫Բ0�D/�8-ͽ�f�}�O����k��Zf5�E��M�o$3^Hi�ث��r��@�8�����ύ�‴�b~��,:��({��������~K�4y'�}�bJ��I�ۢ���br�v��P�/�r*�2x�|�����6��6���R�;�(%Bp�60�c^���g؆���A�!(���mX��{'_�\^�4
A��$�(�˗>)���2Е.�f|���=���݆/���|�����7�Yc=g����Fu2��S|A7(�9O�-�j�6tN��wZ�)j�{g-�t8�&��r�jC�Y�;m�\���U�[UH��^#��i�j�Q�L�d�A�?�	�d�b-����
�W9���Ƚ������*��!��tgu��I��T�z�5������p�Z��y����wB,�tl����A���cs-�&Z��:��r h36�@���d�K2:�@��ƴ�^L,�c��%��K��AZ��@��nFc6��.��R]�Y�
�3Nt3f�*�Ә���C 
k�*�t�PU�٨����QsU���#L�*�I�,��Z-N�T����-��)�gS�]}8UO(�閈�����4��M�w�OQ�������&-��YFu?���Е��v�r��p� Ņ.����x��1�R���3�/5q��py�K����7�q�i@�MX�1YE�@7Q�� +3�dL��\Z8H��4��haP˺�/����}Ě�wkϟh�6i��M�n�J�O� �.SLkȈ�i�U������2��@�c�0�>s"/��͉,HJ��s��g���Z�Nۍj�,Q���U{Ka���es�K�����qٖ��E9ѝ7�X=4�ڜ��~��+Ote�^ʪ�l�
��C�y8"8��;r'�r��4����~rr��xs�P2Qƥd�\��� �O�OzOP ���$ZP)�?�OM�@�,?94���|\�	ȓ��M�@~,hB�b��*�+,����Dޚj���u~u������Tȴ	�.��������E/Wѯ��
��5$�N�����Zq{W3����F\��������c.d���Un4I;����áq��:c����$������v�\�� �z��q�(R�u^�i�G���b��&x�Қ4�3���9��	��C[|H�>r��o���os��o2��3+�#���{"�#���$�rD��&x�_��o�)�����r�E��Xһl�.�4��Qw���Q	K�@|�[�ŏy�y���{�������B�Xk�x� �9���L�P�5G�I@��3_4��Aj�	�o��l�h� ��̇MMˠ�-�N�:��eR,/�>2�9��9�A=��	X�;d�sV���l9i�����s�D��&a�n��8�U�a�̇͊�X�m�9�j5MjM3)���㏹�u�1���/��n����DLF��l���$�%�C��^Vs~�㇟ .N���&'��_�D���_6Rb�����o�j��O�.İ�R�9�.j���N�k����M6[2���N�T�SMH��.�(眂����U0 �E�Bp�z��/$	���1�1�T+�1�$+�1��*�1�/�1�/�1�/d�AL����AL���!X�z�_��,�顠y��U'�����̧hR���k�B�yi�6��u}Ծ&nEVЋBփ[B�r���H�:J,������?�SIJ�Cj�f)��E��7G��^' ��|~�f����5����c!њC;���ؚ̺�ݓ��PU�hh�*����&-
�D�hh�.�of'�FCCU�������PU�hh�*����&-��X��߫�nݛ)L��u��Uw���z����{��K��
R;X��/t2�Y�M��֮xS�f��5���𧂄i�V%�TH�1�K��ְ.��ú��q���[�����,��[�{"�J�b��u	����%�h06^^�h�؀y]��%�3Q[b�+���e�X��Q�r�iG;NY�ܕ�����e�q%��c`��34�qح��5�5T��^����?��\�һ�4�`d5�U��9��k
[)��&7����ĚV�^FM+n/����Cӊ�K�i��eʹ���eZq{�!m���A����Vd��e ��q_��Iᑔ��f��|d����v�h����'�g��e?���sz��w��r��B���鄽c��O�C{ǜ��덦Iڟ��.YZ�|�R��U��J�F~��	o4��k"|�l�o����>}�ͤ�e��otIH�����8;�f��i�Jot�k�{����j�7��;�{�>���,�#)ƕ^��-B�v[��� h��-@Њ�[~���BN�kgpo�._���G�ʝ0�)ǅ�Ix\��ǝ�� 6��8�I*c��^�����L����,G�kX���{[� ��*e)�NI"e�������1��0��19���L���Gh�9\��=s�Z���i�I�9޵�����(e�zSi�ѝq�t��3�ѿ(�g��v����������oQ�=l|j<E�d)0����Le뢪osa����$��]7*��*n�vX�DE"�y�43`	4`)ݩQ�(��4*�4*�b;@�i�c�,1�r{E9'�.ɑ�t��2y镭'�����ʮ 'UoS�P�w�� ��SW�!r�C����I�*�FE���[���{M6����DW�b�wR��7��\	��7�Va�:*Bw��E脮��EW�<&���b[q��+)�Uǚ�kFϙ��m�̹�)��m����n���5/wA�O�(2:Oa��,�E�1��l=(i\��H�˽����ʽ�W�l+�*�uu2�.NG;�B��j�X����.�-.\����������q�XY��F��(�;��������z��X�����ZD8ˉA��lD8˃A�S�G�)zl$z).��}81\�ے1܊������ ��&�u��tu�ޮsu��>suD���@�E�)#��7�O���]�y�uZ���n���D��&��LL�s7�ed"?-��=�TV��E���,�[�L\K W�+�:8��;�|�W��4�3����?E�q�	�"�b���6`ߢ�m?ϩ␆��ܡ�s����Wm��׻���'(x���İ����0o�r�
5��L%��^"�ۜ޿���ɤ��]*��/ח�w��N�3�c�RY�t-�M���Y����%8�ǅm���C���������]Y՝�9?�:�i�5��+\�NJ<�kBc/�����eLA���{�KL������[�E�%�}��hdh��F6�)��^�����*>�g�������j�:>�(�����|����m��$Q�bՀ}k��o�G�>/'|�2��N��'D������w���z~��P��-L*����[FT��jQ�﷾@��Ϧ���F͚�?a��� �	  M�w Y��+��o�h��h݊��Q�{��ђ��1�`E�\E�XE�TE����=N�h��Q͡�V���G�S�裕)`�Ѫ0��#���|��Tm�1V��
?6�U�G'����٭*���j\���o��^��W�Gg����)�*`t��
8t��xy��\�h�0:�U��uU��]E 3@Ά�Re�ۻ�3��F+��E �=�3G�bzD_���*ihaKID�$�J�?D�\�U�x��n��Q��E�m�(S��)	[(V��V��B�*O��I��pFf<�<1���`���V�^�s��w�)�$/e����E�}��:D,Y���%���d�2"�,ZF��q�<�9�����2�1��;"��i����ɺאuL��2�_�g8�+E�������.	��޾MU\�Cu\�Iud�Oud�e��*',MS)2��]tbd_,�륉�\d���[E���WtT��Y��@U�u��wۼ����|�9��TӹЫb�����J�ʬ��'�Tӂ�:�Ժi�hx#�	�tn�"�ҝ|�vݼ(xG6޴y�/��
$ig�dB>z}��-����s��qw@<����9��8�; �p��n�h�s��w�<��������w��98��M�(F{�2����~7�όQR�)�s����st���f���m��0��� $n�3���AJ_�=!)� ��eAH�2� �@�DT"�"�*�!�!��h鮕Ȳ��SzT�졐W�Bĕ"�qEH!D\�QqBzOE��
���ʈ�,�AdH社���SS���2�\{��xOf3sӞ_��Y#!�gXͷ��is�(*���o��e��!E/���y� �t+<$?Э��@��C��
�u+<�>ԭ����^q+ܵ�YE�W:ú�\@����A���xP�m=�[���A���xP�u=�]��ףaؼK���|VJ֑����'�D��������`����)O	�L	�>i��uR��Y�;�V��S��ˏ�=<>,*>(����8�L�b����k���Ϫ�}����Փ
�+&K5���y�q^ D\k��Bĵs�q� Q�h"��!b�&��a����?ᯄ+�w�:9D��A�� ��,��e��\���܄�'�/�L�� ��7�wK[�w[�w�Z��Z�{ZS/+��M���WWzt)�|��q�G�q�G�q�G�q�G�q�G�q�G�q��q������"߫�R��/��D2��.�jݡ�NI�p���*.�=U�Er���H&Uɧ��"YUU\$���K��*.�gUEF����|�ˈ�I;��O.�"")��a��\��p=��˖J�q�$GK�q�$GJ��!�(I"��!�I"��ab�H��N,����Y�J�(�T/ K�0ƃ�a<�j�)�z�
D��x�kW�GT�[j��?�ZܲU�Q-nٮ݊� J����[��iR�����@���!S�C��L�s���	L�V��O�H^M�wU��k��m�ڬ���-&$|�*��e���HZ~^�7��o��#j��6q}�Y8p���3c��#����Gt0�M!�h_�A��Թ3� ���w���K��/�����\0x?tg�`�~.��f��r�`�RsERQ�R�3�V��Q�Q[)2U���i-�R9dD��NAȈΑ��5�zsd� dD���<AȽ<��6AȽt��0AȽ���*AȽ���$A����A��T��A�f(�����$��	l������NS�=�_F,�R;�^�ij� �^�ij� �^jj� �^&jj� �^:jj� �^Njj� �^bjj� �^vjj� �~�jj� 쩟��� ����q�8)$��J��������+{���%�
DZ�˻��4���[�U�{i���[��4�[�U�{)���[��P�[�U�{酻E\ߎ�u\��z�[�U������\UB?-q���J�_�Xԩ�|ϔH��HZ֚��&���B��R�CW[����)A~/(�8u�^rPjq���T�����{�C�ũ��҈R�S��%����K1J-N��p�Z���~�Qjq�494��s����r�;M�0F"�����L�Np�/E���h���8�\l� pI=�b��#��Gt����L.��.ǌ��,�G�oFm?�^e��w"j�;�b��W��Gd�Ő圻}t�� !�1 �1��<�V���x
�� Bc@�c� ���	 �Q���g����?s~��Y��W+υ�y> 2s{ �1�Bsx �1oBsu �1?Bsr �Q�uo ���`����i�����情�V&��������o�~{zz{~���k��)#�ͅ'W+�~�ޕ�~�]��� Is      /   �  x�͙�j9�ϣ��X��#��[�}0$�Ǉ@`p�1k�&NfB`�~%O�Ӫ�{X���_U��ڰx�y󴻽��@�t��ԅ�̘Y�(.->^-�on^��|�z�~������z�a}��:�<�\]]\ۧ�����vOۼ\���q�����q��n�}�������������a���/���v��������v�_�ߥ�	1 �?�U���k/��n.ޝ��������a�{�v�k��r�\��6z��CQ�"0:׋�W4|̑�Nb~�4X9�t�s6 �i6|L���@��	Ǽ�u��i(���.�N!{,���15�rd$�v6E�;�H�����lh��Q�$�`1���=�qP��UW4��;�uD&>���`�����&�	hV�<w�� c4��[�h
����*~�񤒏�g
�ӜБ x|�$~W���g&rHI�L�ύ�1���s�e~N3�}YY�$���1���@p�q�$�xnďa�Y<��R��6'�?K/?�M�V��g��R���s#~�{�X9'�'Nbp�=/((0)�[�܈�^|�T�(y��D$O�
�|�E��Ǩ�s�}����X`ma����r&���uV�m"
I�4��G~����;����2�������˝���l'��&L(� ���P18�x�L��B��N��΍v�qp��d�ǆ ��l	�Mz/E��AMq�E�ä��V<7�5��S]A�
{�:DC��/dA�ύxM�*>�@�,�K"<�� 	���!M�_fJ!#ڈ"8��p/=m&Cz�����\�$�IBC['A�~�g�O��'?��"U������	�y�wsj#�k�Q�һGg���M8|38�̷Ԯ O�Q\P��|WuAp�ޛt��~i�t$d0����Ƙ��      1     x����n�8�������#Ow��(tm��ݫ޵	P�m���;�kE��J!�0�?�s���>���h�<�
���%�;v���8�����}��s��w�.�޹�/�����& ��&{m���>\���0����m0�2$f��I#dǍ�F~!�D�����6��2 ���L('}�"�$��a�X�����s��;����M������~2q�C��(�H}@�-)X�L���A�j��.ĥ�9�<H�(��o�%�ò	r�j��oM�@�UC2���r�5�IlE+�pl�#��,�5�z�T�E��5��F]Mi�����H5�D6�ъרiE�AM���ɇ��h��S����o&�bR{��R�!m%�Bm����&lT������4 �͋Q�U��f��l����J}�Jk��˽�!�����i��8�O��at6��\�6�X�)l��i���d�#��ya��m��O�̬���o�"Ĝ-�b L$#`1G%����
W�T�4g$@K�ms�C��k���z0�����4#��~��-D1�#�<:�Bꣶ�.�o���6��`�B���T�����������������??����\��@�hA��8Uj��)G���I�	�A���P6d��X�h@S�N�T�9��Q`���@���8�T�<�+�u��٢��c��T�_+[%�8&Ʃ���!����8�};���e��F�`y�bѰ��Blp�����x3��X�j"+�~ۋ5�V�֐�����~�      2   r  x�͚]o�6���_�?`���K�U�aڮ[���bk�1�4$e��Gч-�l�j�� Jt�H������Pe����m�9lU���b��6žV��V=�+U��~�~T��?�J���ժR��jS?�����pI�w��(��RK$�K�.�.� ">���[}��u���KY���k]�����(���o�z�bqw����s�3�����X&	����g��;�.����!�Z�Vy���g[U��͞w�ҕ_�C��������m�ة�����t��:�y����o�/>��~����-��I�"��q�$����U�z���UC1�JfHc	�W=J�Wa@�zhF�����U�9�WQ/p������W�B�M[��U �WũW<#z�b�L�uO�U���UGԫ��U��ٽ�u�����ɔ���U�_SyW��U�W���$�W�2O�����UC8����ZvhF�����U�9�W,�~&SSF��I��7�7�Ǽ
G`�ꄢ畘�H�1�?�̓W=�1�Nî�s�µ���i�^%��G��V0�
�7���m^�!py�R�{�����	x󪡴y����A3y�u������|�BH�[�#pz�P��L_�����ց'�!��ׁ�Z���i?�D�Db���
B����E�b����!�V��XS��
�:�X�&D�4b!����%VKq.�L_I%��I�j	�+L�bu�3�%# Ꮖ5�^�2_�>�p4��!֑bp4�ł��L��4E�K,����8�G��Zg��0�XL�)D���7l
"p��P̒�}V,y�N�)���ςoj�˭�w&�h�/�Z��SQզr@i d	CJ�����H��^�������~ܾ�� C���r�$#�Q=� �$�p!�0��ϵe���~(�y4�y�����o�~_����
�IO/,/8Ex�,�եT\����[�x��46ui��C��G
R�<�-�5���il��x��t��)cH#��]%��42�^6}koTÙƦ� �,'����F�aMc�p�Q�1��FC�JcSƐF��<E�z��,i������ߞ���p��!�4�1M�`���尧���4�e�i��P:��Zf�F
��8����#�!      6   �  x���ّ�0��!�I�]�e�	l�+0`�9��ʦ���,�����Y�^P^d��<!�-��.
� [��B�H��+����}SO�U�rb -%,����1���M�2��<n���=�\Ɣ�}���G�%�L�g\iH����M%��H�%�[V�􅺐��j�x|A>6\���6�-��b���x�u���Ȯ�cc�'l�5�\�/&���nelzHnD�-is�o@�p{�<�!��Y?qv5��!OI�M%�#Q#�\����N4����P������-$
�$U���1[��qNf�3�}A�?:��&"������ʸeq���8�!8fI9�o��~�Nj����7�<%Ty�L9�~�i����iQ)O��Ad*�[��""<"s�L�팴��X����j����z��0h������l]�yl�J�����B�|_K#_��(�J�X%Q���t������T��I�r_@��M ��ݤ�&F�nIb.+��')�j��c��W���X�4⻏���	�!<���ao���;���(�H�C�jg��m猱G��¦���0���dY�.��U��0>��=��FQ�B,,���p��m���g\�Z��	�h��S��ģ|�"3K�����1�W;Z�pX�W�}s!�\C~�KE(_85�ќ�Mo+sAbx�I%������m87�)���5ӻ��4�ײo:q�(V��1	�P�������x��K66�8�^���"����F�*�:�Fq�T ¸Kt���)O��疑e�	MyB���\�D�DDnt�ӯ�,"-P�A�$Xt�j��U��	�F�ľ�D4l�iW�3�?�i�L��u��V����e~;�r��&HT��G�[ˢI�(���h�s��]b<����0��^�X/�|�Qn�$��S�%�O"pb}�G|�F�#����=��2�*�:�`m��� ?��c=��gV( �0�%�3���>�ؾ�$��z�Z���R��r�Iv4�W���iX�a<�΄���l�CV�:���m�YT�.�Q�+��x����3M'X�IϦ�;#x>�|�喽ޗ��`�4��q�i4�xȂb��c��ո�DT1V���kA�ul����J}m�,׾�&({��{��PɭPqn���c��g�tS�W�[z�!���q�� 5�y����HsY��u]����      8   �  x��TKn�0]ӧ�@�	�#0�I�T��ni��I���z�3�]v�#�b���X��4=���7��Y��̣��Ye{�h�m��-pc�����F�Z��f�`T�{�*�+<�ۨF���&uF�u���U��F���C#�8N������=�x{����y7jk`��iA�j�F��=[��$���͊8�`MUDaG>N��x�w��`���2�4Zq�����/�R>~����)!���Ox�ӁQ��o�jԀ�=�+�Gy���A�3E0�ơ��<�TDIƨ�R�n'i���Q�����R7�t��w��+�E	+��A��i=(�S|P������Z�:et�R��"�Ҥ�%O�,Ϋ�����g�(F�u���GG��v�����vu=��\�<�X���	Y����Q{���Q� %i��"��!�);�"�E��d��y5c�����R"�-�v #��SX7���6LR��:C;DuډӜ곜�/ir�2]K��K!
V$Y,i=	ir0�����J��pc[��&,.����vNd���0g�����鼙��G��Mҝ}b�-1Dg��Ch��b�i�"��fƋ6��4hpQ��؉:�lLq��i���
�"��@ZC��1��X�[�/U�G�[��e��$J���vR�
-��LD�\͟L�K20L���v{z�qJ�V ��Y,��      9     x����N�H���������>��Y;�	rF;i���e$"Y��o���&$�\؁������j���jRd7�*�PD#�#2Bn}���#`ׄxV�P���J�d�G��lV
1�w����6�����ߧ���i�|z^ޭ^�Ϗ���Z�xZ��w�ו���r����/�]�ǣ���~t���,��˳b�0}�l���>������o��h1Ǩѳ��	���Q_Lp1K6rC��Q0R�;
.��S.��bzSVٸ�N�jr��s�ܠv��	�e;�L'���˃b��m��Zm�Ix=E�	�ݸ\Uv[��c�LJK�@�u-8e�3��g��q��y�C�#�$<��,p��,x���|6��g�t<�-�#B��憵�́��zCȝ$�5�)���N�$2��ۓ���\!7V��yG��2�#󝤃 Q�
���䴐��?BC��V�r˹�-1R��Du?iK�W�βq�e���ƝQ�k�������"�.��5���ц �w�g�럥�F�MuZ&A��G �xF�)�J:r�-j�>��m~$��}۠�)�����3:�ap���Wଌ9��	�J��$`�����֩����4�\�Z$h��2>�A����9��ҟy�FrZ-х��V�����b6+����Ł�ǥ�͘R҄�9�B�Z�}���l0x�$�v���X���Б�(2f�'�Н�N�P����$kF��D应lK�!�ÇM(]�������ع4��;gF���6Fٻt /��)2=��l ����o�i��6:������S�5�ί�o-Z*� ���֓�^�ͺ�{AqV���{�`%�%3e1yZ��d�t�9��������ͤ�R^��+0�E��R��"I�5b r���h%0I���)���U'-'���YvU�'� X�˘�����^v�0ư)߭�u��(�Q^������%��֯ˇ�n�}�~�sy��	��8#�3�K��I����u����x�~�����+�.����6�m%Wk5�߭��%wl[[������)�      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      ;   G  x�]R[r� ��N�	2�y��\I�bpyx�����'8�B�+�����G0Qa�O��=�>z��!���\�i�J�W�y�L������R}ұA1��Eϫz}0V�⌽5̩s�7�*K�[z_s>~���
�
5�I�'�C.\��c�׽�ٝkb�r4,����gi�"�wO�([�؈�~����r�"2R��/���������\���ړn��3�&��+��8���na0xP���0�A�\\���q��EF�8��u-ˑ�E��LjI�)��,��}�fl��Ե��|.+"��&���T���e�q�m���l�� -s��      =   )  x�uWˮ�:\����̦ūx,�S�%�i�i4?��^��U��w���gRs��jQN���Ȉ$S�j�^+�n��\=V�to��.Ԥ�Ŝ�R�z\tbg�w�n�׃u>��؛Ča�����|Z��Ӈ�q�qj��a�3��F���c��aK�'&�z�G�T���7���?�/�)W������B��/~���:���I|�Soʸd�|jE��ʯՇ+�}�h� R���U��4xe��ܥ��]�#�h��G3GK����Q��R��,���Tz󸗷P�O��Ju�sv�}�k
e�׽��:_�����ZV���EvD��]�j�ӑ��K�>��hQ��^�T����b�J�;8�;j@l�L�
�{d��
�\���2J�NIi\G�2�Qn��.z|�ʂ���Tg��[G+�)oj2��x[��~���p�=-�7�f�z��,[ �i{~K���j�7�kh�X�ۑ����[�Bi��D��~F7o��v�ϼ��r�&�0@8o�F�h߶�]$�e��Mt��ɇſ��'T�괛e?�zj}��"O{��7J�����mU삸���`�vw9�QY�����V'8���O�**��5�L��ߩs�¹�=uA8|�O4��� �D������	5�n>�V�����@u�^�~E��F�� �U�h�L9M��zuї��G��/:D ��}b;�Ŧ8:<�u���J�0#��on57��ƽ�T`O���.�f-�M�����B;M�~�gcӵ�r~ w-p��k��bqv	1�����E��[{�ʖ��̼ؑ-7��G@o��B�Ńi�
md.�_�i�ңq���qv����W#�ж���
��4Ud�{7�h}��6?�Z�R�,��{����h P�D���a���%�Y
|�A�mJk��~��r�ei��?�&�F��"(Q�$T�A)�*o����z/��A*Q(d�ץ����' 	��rӓ�~��,��uB	Gܺ�)�z�qӣęC+	ba��"�d�~C��0���b�$� �?('�dk�}�%ܜ��	����%�N�:[j4*42eϗ� #�8��Y<�q/>:x�)[�Db:�_�<eWq@~��l)��-3Eq�̫�B�[��Q�����M$���Z��3�))���i�̎L,:���z��>1Y�� f�i�I> ��)����@�5�e%�*�Gq
����s�æZ-`{T����L�Y� @LP ��X�ʹ�A`��AG�6������:����w�Y��s�/y�}:^����>�5%����X��R�q� �xm�Se琍�J�@kOrmL&�7A_��;�Ku�袡�ʭ~x*ɰ���f���q̐cJ���J��Z�aK�mg	�����Sf�� ��l���A�#Ձ$��?��_�C͐T��&LIEM(
����Q�0���v�2�_����-i�1��e�1�=�2=�#g��05�j0��Z섌���i9D���;-h(D��.�I�zL�$�5�0����!�=&��c`5�9^*�P1:U�ߠѧn���9J�lhӿͮ�d�,�A�c�]l�+%q��}gcyh�E��9�"�~�������-n	w�L��rƩ��A������ٶ�py�������Aiw4�ݟ<D�;����wZw�n�&���|��]Zm�/-r	q1�y����"ޜdJ�l�
�_ _KC�t-A�Ht��̨ys(��O��L(�p�u��H�e]bJ�Mob��m9�!��W�C2���l�M��ޜ�c�f���	���%��t|�7Ȍ�}n������r��      ?   *   x�3�	r�vsr�s�t�t�2�tq���rb���� ���      A   	  x�UXKr�:\��	&,�ci	�0�$h��s���/f�1���&�
�{"ff%�_���V���#������J_�����5;������@����m�Q*kb"t�w���k�O���B���}S�X�֚7�mn��m����9��ع޳%���9�8jc���h�C�+;,�l�ϵ6�K0��A�S�����j��E��J1[���bm�]�{���niIm/v��ճ;��Q���m�ft�n���MdTԺqvU]��Ts~�{��/��ѐ���V��W��ƚ��F�(ļnEQ�/�^�<�1#hG�]�a�T�?�~�u��M���B��^�����0_\|�.�84��IH�̆�k��U<�v�:��X�y;b��3
F�ͫx���>G���3!�"f�x��U�{!�����^T�c�}��y�Wr�ŕD���ѕ����b+=on+wf�y��̄�e!f�Wű���=�҃h�m�tS��o�;��0���gRhv���XmAj��I�0�����Y���
��EcS�!ݕ8�g�o6y~�j<Vf�i]�)�`��A��P�aef/�!D�ɠ�}O�H��T	��O�:}��O��G�͞n6����x�l4l�b�1b~K#b)���R0��DA.6a`�� s�Mm���j�>�J����g��j�B)��d�B�\#�9���	��S`
気�����tm�U�t	��q��6a"XWp�7Ͽ�Q�y�Gύ�@�\�	��@s�/n�Í��|-�3��Ӣq��~���"M��vq�B������<~Z��s���9n�T�n���Q<B_`�χ���a ��;]V���"��-��/�5D����͑n��j􅨄��G�����	y�g4ǓenwC��H':b��[�y���6�c���u�'`��Aw)��k�����Ya[��(ޱU?���%܏]�`	Z�35���8wi吏���m���o�	ۛ7�f-2l�Ě|�,��+F��\�O��%	~~��������e,�y�)�~�i_2~c9N��T��]�?v�DK >����bN4�+��Ws:h�ǣ��q�>%���]a��}��4��D�����/؜�o$'�.�T�9���*�9ph?e��[��2�>&N�3{�b˪>B���=�ڇ��㡨$mgv��u��Th����G�p���U���Ѻ���3=��/���U�9������+6g��wsH���YI@��"JB��ʢ߾��=>����
��<S�/�*9�2��H;�0��]�{	����'�A���� 
�`�L�OJ�����~��I�'�V�Ɂ�t���j�[�ٹ��޾�T����}��E���z�/o=N��@ds�����?��X@[�I���qC�v�a2�%�5L%!O��4|.����3�C�l�C���EL�ŕH�,~��4?����/�'fx��B߇?d�$��e���Hf:���Z�ļa���B����%�%<�5f�\����C8�eڌ+� y�$b���+��h|5N
��-���_��/���ޤ@���� ?�A �VA��-�Ys��~�%�En݈�G5fw�b����\2$1����ƾ�I�=#7��'�d���b/���kaZ�^TLU6w�,�T��-g��C E��f*��V��	���j�؍�eS�����Zy��M�������2n[�h�d�z"�[$u��G沔�T(�Qr�)��#..Sf�%���M���~x��u�V�d�~��d��:zU�&�tP����:�K	��(r��AO��^��C��5+DX�UR��f��⸑��?��r5zws+Ic�b��a��yX9� )-����筑�4�P6�͚��J�uQg[ք�o�o��K����A���ox�B��v$^�������Şy\(DɄ��H�\9%���?'���a�zϘ��C>���V�w��A�U<� ;�/�ҵ�#ٕ�ؙ��W�CA�	����y��-�,$�y����M˒�m%㝳�� A�"s��S�qa��D�~]��P�� ���+�@Q�����E�"��7��~�z%��hĠC�:!��%>�?�RPh���&��k��2^��Y�,p�ŷ�K�JH��«��ɂHɕ��)�T���w����o����(�%7�jB�W��`?(~�N	ڂc@��|_0�����hm=���s����B�V�_\��{יS���C���w����۸0h4���O�ӊ���&n�G!Ph�c��<����P׻';����=m3Y��$�rq��X�V�&�ȇj[k�_+5���1�^�      C   A   x�3�,�445 =N8������r���j��R0ՆPEf�`E&@E&0EFh�, &��qqq Ϻ�      E   p   x�3�t>�2�4'�ӑˈ�%�����̒| Ϙ3��ʢ�̼�b�J����������b��	�crijQJ�BJ�Bh1��IT�I,IL�/��f���PR��tj������ ,�%�      G      x������ � �      I   �   x�3�IL�ITHIUH��/*ɬJL�<�9(����W�ZT����Y\���������������yx��>
����E�
�����t�����%�%�9 )��� ����Լ̔D��E���%�E8�r��wr������ ��\�      K   ,   x�3�v�p�<<ُӑˈ���9����/�ȍ���� ��	      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      M   �  x�]�]v� ���U�ޓ�?K�c	��\��$���Fz��l�wF�v������0Ж��.Ju>�~=��>�z�{�v��nw1)�)����ȹ�`���-���2p�d�,�3�7��/��f��d�3Hf��(��b�#նn�����m%��b�<�TG3o�,E	"D5Q�~��D&�1��(GjIǁ'�����O�122op0s����Q��7u`Un��uզFV0=���4e�(FC��:w�i�f>m3S�3}����:�Rb�}���ɺ��7���[�%2f�D���Hl�G*WT�8r�L��H�������Q<�G�K4�\%j���w��a��ȳ��W������"�Wު��_M�P�@�cq�����/�zmNҭ��k�a�	�&]ȍ�0���
i�h/!���!N�G+!�Q�w~=}��E���rE�9�;=��pc
ɭb�Z��������i��#v��;��3���N�1��tG�O��r�g�eY�%�ެ����Mxt�p�⺄k��Z��[�]�^�^�.����0�v��o^�P���QJ��v��%�!�̤и�3��i��
w_(�ځKS!HZϚϮ�,J}5D]dѲ�Yt,�U����� 5�*�N듬�8N��^�������-�?��6a�d����70Ax������G�4D÷O������]��i�or�4��4�O��A���}��}�s���u���v�?_����P�T�      O   �   x�=�[n!E��U�
���?�f��VU��tm�X���{��9��R���2NwT8C�<}�{���w[Q>(�%�����@ .���S�<��*zQ�9#�8��W[���e��Bm���[�?�Ȍ a���lR�B��MK �w.&�����;���q�r}�4w�XoS,Ԕ�3��3g��`������.�+�l���:0���b�:G�UD7^�s�{�V+      Q   ~   x�E�=�0��99E66$�Y�6V1r���J�z�������}�����
��p��W�[�����A)k��벹p����<�muݮ�B,���b��:&}cjz7U�,-n�8E�9VH�u��n�&�      �      x������ � �      �      x������ � �      �      x������ � �      S   �  x���[k�@F��_�?�ݙ��I&���8-�S(��^�jt1����J�BBM��|��ىf����t6���(h���q3'�o=�??M�x��㸙ތw��������D���fk�����r�-�̮[��,�uf�����yH�@�0!�!SDɤ�p�Q�n�$?��!�ŏ]���}q�+m
�њ�N2�Ilt�i��|���/"��P!g�Dkɸ:�Sh����PH��i
-{t��_g�VȘy�y�·�M������'��kt��^&��(E��Be����t�\IaD_�'[�\��.{[�3��R����ʴT��)h'pYӾlܣ}�%����q ���i���W�+����#���(0M�mX���T�>�y;���4���+�ʕ�X\P�U�P%��)� u��]��y7����?Bc}�      T   �   x���=�0 �_�m��q�V! ����<mԀ&%������p��I��] 8@�ˎ	G��t&e6�� %�B/�	�n�EC�µs�{m�o�2�.�����+2��p��p��o(o#�t��E~
�.jS���ߖ��V�R�|e�ܼ�=A)�6	w��M�<]���c���}�{��N#      V   N   x�E���0��fϴ
q��C��Ο���^m�هM������2������i�Rɿ$�R>�ϴ�D9Qx �x�      W   d   x�3�H-��,IUH.JM,�t�4202�50�56T04�2��22�3��Ks!�������S�eז�������O\	�	\[jNfnf>]0\1z\\\ �7:      Y   v  x���OK�@�ϓO�'�����\ڠQ����P(�]J��Ҫ��]��R��������N��	G�n�w�t�8,<8 $��dD)+� �uNd�,`� ���z����e��/��몁n���LА�ȸ��'�ըj?�,<�2��XR9WJ�7�㢚L�w��=����=�6����k�?�f��ﺀ~oE�ғ��C#+?�_Y�������{`��)�J	���t�������
���%H1�&����MἜ��WUS~= G�<��h+Z������dV��XN(�w�2�Q"i�M&D=�[8kO]Ӹ�A�G��"��ڍ*7I/�fT~3�(��X.,/�Bs��AI+T������&�e�n���qgm�Y�$�+�ҳc      Z   �   x���͊�0��s�}���$Ӝˮ[��7�D"h����[�=��!��/af���_�[���d�ad�h<R��x`�&�N���݆�W��j֮?W�G�ձHc��K�TaS�@u�r��m�'� �?�<oY ���?q:K/�N3��/\>c �.w�����%������V���3�P{�]��v�x�T��t�l�`EQ���wj      [   �   x�M���0D׽������HC�%��r�DB�В���m��v�̙�!âR�Fc�4��}l4Z�ʤl~�K`Kq��̍�%�&�2�v�\lY��E�'�Ċ̨��Ύϸ��H�5�y�%�X%��=�=� 5��?'^�F����ٻ���뿭��_=@      ]   �   x����
�0 ������[�)�M���?�@��3s����n:x���Ǐ��=��>�h�#AL����A#A^g�8fJW�ȅT�>�TI&R�$/������lc����y��f��b�-j��b�lg,��í��Y���x�<a���C�͚�5/�Ҍ�T2��~��A��d�      ^   �   x�M�A�@���_�P�g�7%(:����E��O�P�a��1�u8u�jh����PC���H`Kh�.S�B�\T=��-~�׵4�����8�gu����)�qz��.J�n7يw�˿�_Yv��Ȍ1o�$&�      a      x������ � �      b   �   x�uҹJA��)������"M��D3a�@�E<�ߞ�fzӂ���t��gl3�DXC+h�Ċ��������񹼽=?-�7FC[C+�
��lL	��
=��� �Ƶ���6�rH��H� �dCQw���L��!3RG��;�2��\W���������"-���	ۚK�?�? �����Zp	Ap�TǄvDl���6$��s�M���      c   �  x���Mn� ��p�\��� ,)q$�n��R�ުG��J������̧��װf�v���vܤ��^5�0b��m�:A s ��*�q�G� 4(���?�8�1u),�YƱ�۰n�m+�c�7/m^I���2�4��b�r�u;��~��u���d�8E,0o�a���t�G�]�i�[�>��$zܵ�Ahg��V���L��?EARA�� a%��!uc�2������$OxFt7�A��N��vZֿoWzԡ?O�7����s�'�K���#���p
 � aB�K��/�BTFC�e~�����$��q\��H�a����^3uuX8��i��X����8��g��H��ֺJ��xo���E��X���\U ���p�"�\�u�Q~ͥ��'�r�      f   �  x���[r�0E��U�gԭ��b��U`g�*S��Y�ll�@2<ܲB��>�W}�6 ��c�X6��{i�M��O'P!V�Tʊ�]zM?w�J *���m��v�S �S�(��Q�]ݮڴ�u?M�M��6b;�n��Й�~(���
@��:��ĺ�D?��:�껴h��N }�s�A1]�7�����ny��J��Z��v�}&X�/�,O�~��v�$])�yV�l�P;k�)B���g����M��+M��A����Ao����+���4��D���D�3݅8��g���e�ad̵�?Y���{��K����h m͇e�z�&��.+A��:��Jч�./� �|�=k����E?	�R�m��0I�W��u;9	|(>a�h�$82Z6���ғ��-^{	|��䵗�g�OxM��l�񚮽89�^�|�|�k���Г/�yMe�]j.�	R��r��D�M��5U�sx��T����� ���z����~Q�|�8����;��ۇ��5�����㕷���Dc6���L�h�q�u�m���-5�'p ԊA��������C?��J�';�������E$��/5rJ����E���7��M�'r.5A��xB���0M�Ī��\:�4c����|K��U�$�4c	�I�x�ܳ�<@�����K�'}{��ar���� EP����wR���0      h   Z  x��ӹn�0�Y~� {�:8�:�H�d+���*����o�������y�eǲ!rL.
E$�"" ���k������c��o;�P��T��"n:,�Y��,n
��@lpbv��6,:p��|o�Y������[,NL9���b��!!���bs&�(븴8ֱA)�c�%���p�ioK�Z���Yz�i=�[��vM`1���4�&;�Z�7m�����^N�������"�J]�V@��)2�l<��&_�6X.c_bJ�G�������O�׈�|Y�QSմ�r�73�M�[�^_��k�,QE0�!�����l.���Bi�Z��ޗ,u�	R�&]s�]�� �      j   �   x�m��
�0�u�/W�#I��V܈E�	%�A���Ӵ��f70sR�̀X�h�����||��{�N�H�4(i��jY��$H�� ͼvc�R]�� ��%���m��Ǽ��i�^������c�"!C50N��Z��+B�      l   �   x���;�@�z}
.`4�@G>E�P����("(�HĖ.�4c[A*���,�����i]��`��p���e|(`:("|^�������Va�Y�C��POJlIb9BI� 9}">ס�Y�4�%�F��qF̑W�-G\�#u҂�[C��bv�����[�+.S-"O���      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      �      x������ � �      n   �  x��X�v�H]G|��J�`а�0 '.	�*�O���H'.$� ɕ�G��E����?�/@�l+Sյ����}�{��\�1/�|�$�-W�ED&�%��J��Wz}b�U���{ӱ��KD���� �t�	������x�W���H r,ǤXE�m!��3^�R¥��{��~ Աl7t���!��#�	)./�1�#?@h�ϗ,^��"/��'���<NY���Ц����mفx�C�v�4��Oh��,�ӄ%�P ��1�Z���K�NRo|�2�IZyi5�؜ei�d�2_���i>��B��6�Йx�eK�}�z7t�cߙ�J7d��j���J�q��|_BJUE�f���9�<�a9P��[�yτ{(���4
�)�C���{1K�yYy���eLb� (���O*!���@)�V��\�(h`���V4� �4/bP�=qA]��I:K+���1�E��C�v��-E��ީ*�h��A��+������6�KE�ho=4Ze�t�����������"9���5uM(��TtX0h;���n��r�tt������Eo#s%"� /�w�z��$E%�#lZ���DF����h �+?��ǂC^��,��l�q���D�aO�<�5��8&
���(Q�o��X8K���|t�	A����v��wo���/�<9��7i�&&*�h��-�-�/���et7Ҙh�~����Ԃw�tk���S������MDnA;���&���X�K�e��4�&G(��Fx��z ��1�n�w�g��K��7M��$&��T8B��x�:oǫ�{YL��S=�7G֊����v�#���yj����Io7Y���f��<�V�2,4ǎw~����\so9�9�H�$+-�(.���hX�)�a<z�3��t�fR�gK���e����9g���HbvI��{ l�..����!2և�smA��2.��cjމ��'��"�J��]�K�G.8�;��h`�5V��V;ht9be��.��	b�����l (�������aSދ �"0?��%�|�3ʺ=���G��ӹ��d|�f$Qo�{>�[w���F�6ړR�X�Q��U��_T\IV0���)+$X���A�L&c�W�Q��8��~W�Cc�����Qg��ڙ�e�\�����f;�`��ɪ��jOz�5�ZTlo�c�U��ƛU�%�柳Փ�k �Lm�	š��Y�`����5/�ƚ�m)u��>��t;zU
����%/�bcY�Us��؃�
~uB��tL������T2��ڡ�d��>��pd-�y�G\Y�W�s���L�,��"��b�$�;-ґ�O ��v�ӕ�nՓ'����9���!�%�F�Ed�Ҷ�tY�'tm�t���oÒV���ǧ�Kq��-�.��";a����4�ViV%㎴�"�����n�C���{wϸ�a�(��k��z������ ��W���t{r�}�T����Q[����~��~t\E� ��җ�6d��뽏���n��i{u����循�՞��I]�v���"
b��c���Ȫ����άHw�Y���M����UX'��q�~��ǌW�!Hʫ�_)�,>��v�8K��]qTc���9�O�_�>��b�>-cӈ�����q�f�W�?�t7[��4%��:��6�\)�N�q��'�ۈh~��_-ei���O��[RUݓV��qm�A��X��簥8�{��� �a��fL|�k�+�u���M��(�s�1�3��$��,=,T��h��W�6d4!���-��am�\Ov���=S�ӡW=�3�Nt�0�9�I%&e������j���0�>O��bl��l��5Tk�����!��Ӳ����������b�y�'��|��� O�:S��ʿ�z�V�6������b���Rŗ;��r�O��Bz�~R<��1��~>      �      x������ � �      p   �   x����m$1�3�$0����ț�p�q��>�� �E� �E�W��/��e���	��X9���f$D�ÞI���(F9�9'6e����]+�0�%�r��~&��<�6��'���@�	��P|��#������e�;��B��u�|""���_�Y�իz���)��B#���]�i��M�?H�4gFj#z��9����_�      r     x�͔�j�0���}�d��zjY��\��)hFbd���^ܵ ��P!0Ʉ��Ǐ������:M�epzH�씑�V�kjYGV��6=]ff�[/'��>���H�l
fe�9 �F����gj��(�N������P��=�k#M�����-rP�P���)�ig2�j���c͡���Wa1�8-�>�[�1�9�c�R�cv��(p�n8�J��[�'�8�R7ƿJ� )U��E�R
E.���S)�O({	��lS
E��y��Z7�Q�8���[�      t   V  x�͕Mn�0��p�^ c��V���2�i-���Q�_SE$ ���teɌ�}���A`�ok����vh��͚�q�X+��U���F��T���H	;G��uTk��V[mb��);�1y��� Zb(qZ��䨫/�J��iÖډm��`u�!�\;�/�_��6���@Ap���/��]y��q�����xJ|�)�V���%q R�Cg`~ے�%�U�;�y���A���{�����%��-���?���#m����i=!���.�4-�%h���f�:��N�x�^Z_�ςvb�-��ݧd��}`�c��ԓ�����~6�v�UI���0�|�]      v   6  x���Kn� �5���og�'��U`2�b��Y$R��;T~
#�����qHm�B� �`ge�BŮU�|�:��gAnB�=FC��g�L���,цv+(��&�,Vh����B����7�0�A��)%�c&'ц�De�8f�)(G�8������p���{b0�K���:�g�8��I�fF���`��!��c�}'xU��9Q�)�!��0�s�Cg,�����@��e��{���+��uvt)sP��o#܃t#*2?cVZey3�|M	����*Ą��㵙��j�J��      x   d  x��YKo��>˿�! @�4z�I��w��H�n�Zd[�	�ְI�js�O����-W�����zڛ���a���ꪯ�����+��+�e�^U��:���/�\�܍��9=��D�ʄ�L��U&y�Y�R�p�"��7W�,��HC�Y�B��Rd�>�l�2&b��36�Y��U&s>���"e9~	V���q%2i]��U#�HD�Hѓ�3]�b��4�����@`$�{��0�CCDN���E���^�К�<���D�eL&
G��V�ZI�6�aR�\�	g�|���*�3��2|f"�	��Z+��T"Zĉ����`�c���1�]�GPZ,�m��4�`@?p�s2i��p	J8�v���u�m�=*���9�E,�̈́��|��ٌ0ri���5t�M��ޏa\h�h�OP@��pM����b�B.��9�XTWkoӪ9Ehp�ϟ����.�pBg�l��V+�.k���6HTU?c�ꫂ�9x��G��yF}[�R�^�4��W�m#^��Q_�kP0����
Z����O*Si�Rs�1�^�1_
6��G^���εP����7�0���۝�&�\9�R%X���<|�1��/�+�
��m�{M�]����NЬ�U�vv�w9�ѳ��KV�h�v���)v̰$$�X�I"Ƕ�1��QdZ�ː�ieXt��5qnKN��ʎ�*;�M6B��j79�^h���[>��"�i���J��V��+v"�N��&�K��>t�r�ϔRME(��x�h�-3��h{Hn�Tm#�D��Gr�TkU��īv�7i$	-LD���UEf�1U�x�H�R���w*���v�� ><LDvO�c�/�n̾G*Ju�ԙ�N'��F���w�?���*�1�f�^*��@P�(�v���f}���}S��p٭X�~�z!:I�(��K+
3M��x����nr٠T6_���}�.{���^\���#�a��Ŗ4{��R�2�H�J_��}����k�����u:�����+G@�~΂c��zw��t����xNvjxbjˉ�I;!��:�	�K���
3l�0]�D��)$Br�&�6�R�L �eHw�x������d�+�}��мԖ��Xd��6���m�x4�Eb���T����ދB�O�pi���g���c�N6_~�@��@W)*ւnI.؝�86mE�W�5f��n�a��1�Lgb��̦��j!�����B�wH��K����$�*S���?�\�����J X�عo����UȬK��e@���찟�^E1b�\
i�vr�;�Lǵ���%���L��x:Զ�aH�r)@�+F��تؚ͠���'��îPb�	�y�w	3O��<[r�g|mඕ���-��u��5o�"��ڏ_v�"7��Lz
�V���!O?*3D���CcMTվ�|ch+��v�� ��4SE�`8ս�5�!�~��:]/p�M��[���.�����޽#-�E�t��ZM�}��S�j����]��������e�k۷Oke�e'rI��k�IO�����A	R��l��z%�F7⭨
fre#�ѥ`���"�:����⽛���F=����D����J�wR��O�迯Eo�ϕ��	��.~?���1A^���ǣ��t��/:���:�:��A�붜f��jt���m�"/B���B��^'h=#�1A����ft9���fw�޷f/��/���}��L���Ƌl@6��l�;��=���	A�%\p��[
s�n���m��h7�tߎ�{�Qo0�� �ޮ��o8����U'ӛ�`:���K�'�EJb[�X��l�T�%�G������
���� ^�{���W��ieX���*��֏ �2"��y
xl���-/�߰3�u��wϚ� js����]?u����n����v�����`��p>.��#��FO",�,��<<`����qEG�*�!��Y�8�h{g�f^��y]�w��m�����\ �t0��F���������bpd�p��\�&��9�a��Q���DWŚ����Y��� �Gp=��u�M�d:��׿��棱A�ﴽ����0tt��#$���J`Ϗ�x���YC���^�on+pC�*W1�2�'v���9;;��6�      z   �  x��S�j�0>�O1�B[���Sai/�@CK{�el�nd�3�����sa_�#'���J!��A�f���:ן��۹��%b ��ǟ0I�ﵧ�E!I`�ʲ�D�q
��B)z�O�1��'8�Ws��I���R�^��_�9͋R2�BV=�QYW�=N\1<�������%;Ve�Cic#�,�g�A��P���X��W�p2.�`Q��vݺn{↶s�оq�o�g+����,�v�ǅ3�,�w���q���:�N���Z�'�E�&Ժ/j�����
�`^�\(es�D��b��j��9b��U��;	�6C5�a��Xx�M�_C�6��c�.�P����/����?\g�m{�Ė�����^z�a�C1E��\�G/`u� Jk6&^V��=��K����װ/��0ͫ�{Q릉.0+�Y�fx~�O�!�>���C��6M�%ԅ�      |   $  x����J�@���)�3���WVA,���PR;H0&%��ﴉiQ[w?�o���b&�$I|KĈ5)�d��@�1��}��V]���k7])�� �R��M�Im�Ń@9���3p}U�iWGCр"G6��0rSxLe��iYMr����`��2_y��4l#�hPy�#�f��p7U��5�k�ٮ��:o�n��i�EQ�r�	��?�q������3�Рa�G#C�_��gv�������m�op���չX��(s�~�ڽ�B��N\̟����1��b�
G�EE����      ~   �  x����n�@������;3{��JHR����ԋH��ꊓ�����#��:c�D(U��k�����^��}	����"���J�O ^��RHR�1:���m�Q|3� b�m"+FW���PX�;y�e��j�	�y& !�5)B �o7��w7�C�<�w_�K�@�?���G�|�6E�j+�Ky�3
��W+Ο)�ˋ#x����D�Q13 �[�h��<��s<�2�y25�8�J����a٦RYI�h"R�JR�ՠ�j������D��Rr)��8��
e�a���lÜ�T��Ηa9��"ϖ�*�$׿�܊��|S�u��&��������p�l��8�޻M��?$8��h�Ee&���C�9��{~��̊h�e�h�]�r�إ�uj���4�vIס��t(`��E{0�TK�㼚��yu����i�:��d�ݫn�U��߱X��vZ�6{�,'D�մ�a$�j:%���$:��E�"T)_o|l4i�j��H�i	<�@A��ޜ:��1�鯳诳��r���!�̛�x%w#ԡ���M�;;��3�B'uv�$� �0����C��>��b3��o-a%��%��0�vI�v�a00Gh=
�:����#��ɝB�{�1�t���?�:v΃��m����ݴgV�s�X"�/�p�g����a�����J�& Ku��Dx�{��_&���         �   x���M��0�s�\�Fz��]�	L�B��?GmBB�a6�}<=q���#��
��� ➋�H�B������zw���m�+v�'?yYC���<�jR����@J�1WAЂ�򯄱�y�D�j3YCJ�4�+��r�4��F/<��+,��rNv�a�d��OL"qkά�k�n��?=nߒ��3��_aT      �   ^  x���Qo�0�g�.PZqo�l�d�uY�9�(���>�X6L�������5�i��H���L�B��$K#A@&>�I�T9��IfQ!@
�b�@u�@a��Os���°,�ض��O�:�'7���LR��/��Yp%���b�<g�����]1έK�!����lj��^����O��Ǹ����'�p���TPl�L�/t��A�F�zn��N�m���4�pR�v��뺄���ƽ���]n�-�2GW^J;ex�C9����B�q�*�������n9��ȭg֖�mL[�ޅ�;��r
+�x�89�ʔ������$��o;Xw��n��>��      �      x������ � �      �   �   x�m���@F��� ��"�DC�ud��E>���!��$����eUWۜH#g8`���!\	����I��M9b	��JG2�ftO]�Pr5��lW׮�w��Ӌ'�
���s�6��^-)�<	�,Ŵ�������"M�u��!|�xJ�      �      x�3�t�t-.I�I,����� &�             x������ � �            x������ � �            x������ � �            x������ � �            x������ � �      
      x������ � �            x������ � �            x������ � �            x������ � �            x������ � �            x������ � �            x������ � �      �   }  x���[w�H��ɯ�~풪���4���q�"�rS�K��SؗtkfVf�����:��s@b�
4!�cX	�q�c�U]�I����������������ė�v�]���s��ONg�l���,<3?����,渐�:�#۲e���L(ۚ��Ƨ_6�2�E���A��D���ȃ�a3��SZK�eU���Bc��L�8I�̪3���h�ꮬU�݂��(ȉ��֣���
ff�6�������|����>�=�B���z��a;:�e9%Q�}�6���gZ�9_��l�^��Eu�*�����o�jO���P�����٨��E���p�@�~�)�l�@�,��
�ϳ!LT��:*��9���m���ߖ�¹�"�F��,߮$�P�hS	x����SU_QX�d�E �G3@�"��[ �3�s��:^�w�"'��3�J��D�)ijp��M{|�4��ݤSvI�'i��ېӸ퍎�o�ɸ,�:��Bc�kp.&%�Δ?��gz���s��'mH��M~�������pI�$3I�������#|�-���r�%��|o���'�y�^{��z;\&�y:�ʖ��.�m�����>�$H}��	�,�$@�󒥍e{�9��˞�u5A�"2Q��j��qr���y.�p�*W.c��'����
8.��^� &6Rꩫ{��.��<���R�t܀����ݔ����:�6�A׹�~��g�͊�J)�6Z?c�2#�˚�&u��0����y�G3q�,W�cO�A����2|8my���� �H���C%x� =�i���8�� F�
�#G[v&8���8���3x5�۞��L&[�9����L�6�.Q�ʙm��Y��\�x�}.$1{@)	xja|t͙:���)��	S$u�M	��턽,�kB~!�q$F`����fs�N�����4?�sa�;�bv��7����z�<�ru�y����ݭr"a!d�M� �*I}���X�����޴��%��(c�p_N�6m�����6�\W�N"�nǥ�Ԧ��8��:/C��-�9�L�_���w���*QG�xC�Uy,��Y�I`�؊�f{l�QG����D�W��{�b�&�n�I�,P2c9�^�9 W&F�^��b�p�y�IE�OE������v��E�����4i�!�6q<��d(	����!7ц�6)�&�6Q���04�Z��(�����:�i�U0�y_�. ���5/o�?^�-�?����(�'�},�!��?�c�̰h� �P���݅� @�IOۂf��qMל�t�Ky�����a^_�Ӻ0��!�����C����v�������޼�{*���?��
X�}��n��g�=�Q���t�-�Œ�Ecw��͏l�ճ��5���+<�`@f��J5�����8���@Gn&
�x��*���wE� ��j�u���c-��e����9�(P^�����#��{I��Y]_g� �r��\51V���fX@s>;���vz{�0v�d4��'݃1]��咮�􆝑�M�naV���3�:���VŐO=�	C~��E�<���*R�+�,=֕�+/�\8�}N�p{s�D��#B]�@p��F�u�t<р%+�a����>	�+PD�b[�Ϸڛ�=/��!�;�:��t"�m��4m��[��N�_N8__e��?�(�Qڀ� ��?l��T�v�{�1����"�s�E��.G�3� \4�Ww�%��	����iv�YX���O�
,��*r�7a+�����g�2J].�V�,����@��""Q���
���<y��C�8�w�k�D���l`��z���3%��0���Mn/u���1�7!��h_I~~e�E���� ��;�T=��~��>��fz��o������߹      �   �  x����N�0���O�h��@�\u �b���FPqc���*iP*�ۯ��^Q$�>>#ͧ3A��9�w�$j�+�_C���)(���ݺ����~���j*�ݸ��M!D�(���(aat���.��0�<�����H�7�R>�Q����P�f�Fv���}$.�v��m\7�m�s���Զ���k�fh��'�Q"�A)Ŕݒ�<�Vl���av��gۗu��I2�����%��B9�����xr��b'���{�\��M���rmߴ��)CxD�3�7'�^��U������}���g�cn0(P(���0*#!��(��+&9S�xf��`g��}�?��,�8Ý�s*g9w���i_���?�>M`vܿRp/�4{7�҆��
��\�
�g��g�n����Sl2�<v|����V�u      �   `  x����R�H���S����ni��n���f$ٓ��F��R��R��r�+/6���周2�p���=��pD���q�(J��͋�eIQ�����1:wii��<5�d��Ȥ���I�����a:��6�C��T���j�	��3<'�a<��s���oS:�lDT@T,t�I(y��d���Cs�<+�)I�g���1�M�sn�K4si����3�L�J9�zD#���ղ
���Кk��!Lc*��-h��3�@�<1��.l�W��6M�s��A1�x�h���JT�����|xh�˩���������Ra$u��p8s�Z""�9�;1�`j]>Q�'�$s��e7&�7 +Ȉ��k�euح_��fu���*��x��X��c҅p�[�X�s8N�|������&�����ϧ�A�ն�﫰Z�Ի&�iv����HD�㊘.;�Vz\!%VB 20&+]7o����?�ML���p�������%����j�iv�昗*����ʘ���.��c)��xS{��.���m'F!7�/�C?�
̅�M�Xm�vͲ�W���8
4VT
2�K�"�)L�/��b_XH�H?�GS^��e�q�/��T��Sux�������c<����逈X���I��Dq� I�U�T �Gx�Ɨ��`/�{V���bb.���ɝsAi?$�-����ɌR�j��jj]��`b��A{Ӷ@צlC��
0ُ#,�����z%D'���+5`+֎)�1ա��) ��r-0!1ޏ���./���� oyJ�+a�ޅ`�zUu�v��w<�OB��L�섓a�	6S� ���_����՛+Sx:=��k�|ح��u�]V���xAi�5Ӄ��'`�����g$$��`�v���O�p������>�l�cB#�H!����=��~nv�zW���J0*I����*Ec"�'S�F��Q
V�[ }���Y2v���4�CG�Y���$��e��������)�j�� ��S�%B��.�/*XC���l ���&�E�&�Л���9�iH~I'<�T��7U��cV�)DU����*p�qH���@�V�
��� ��A��ԏ���i��^�,�N8�:�-(��t�H��ekw>�#(�F�=;���_�,3sT\;ئ�ߙ�}�B��C5���:�iM4�mx-�~j�E�V����
:��x��jZ�y�b����,�����:�T�����7���ٹ�F mW���P�����Im�!>����f��^�|�#0��+}�L���<�"<f8�'1�eF���@�;�����!؍7.(�9��-��A��Z�o�I���#"�wM�E���4'������b%"      �   H   x��M
�@е�a�~� �HH��ʬ��9��{+qj��=��ڔH��;���뫃�%�v��+N<�d��      �   �  x�]��m�:D��*Ҁ�M����g�%,�&�Àţ�E,:h��tT+#c���_؆��ӥ`C��*��#�4����Ee���x����Lg��5�0d3����Ʒa���h���ݔ+��m[���al~�#|��cw8��K��,�S�qT�	�9 Dg_[~����
�Rv'�qZ=�["hVף"�'i�o�<��v�g��<O�J���[���>y+�o�>�7ȃX�q pk];2�����U�>">zh_��h�H<ȷ���[[3�4���Ќ�q�K�%���P� ��|K\h�Տq��ꘞ�UcY��8��Ӛ���'B��7��z9��� ;_�^�Ulw0�3��^@{��@(DF�g��M)�\\��S9�s��/�_/ ^��6.��2�M���d'"5�1bTb8����п��Mr�L~Qc����esv�Js��g#���C����i�^�iȱ6T��^�e54�U3�\�����r[P!�(���@d/��l�}<U��óE�5���B�yUH{Z����_�E��ƴ3�噆������`�]�o�?�h^ӸH�٢\g˽�k�Ԋ�C;~"���=�.ʮ!��+͞�f�̻�VU_>���� �ݙv��Z.��Wm����3�B�v��ۂsI��CE��?�r^��X^�OFR�Zd��	d�#xiJD������Lo�(L��u] ��'�      �   �  x�=�_�b@ ���_��k�g�CZF#��A(
R�~�=�}��}�<L)r�b�VK�Bݜj�K�7X[�`f=�s���Q��:e��x
�S29��,��r��sj���߸_��M|U��U����F7�zƸ�3��_��?�bKB3��쟑�_�ʆ �Bo꫄�DNDd����\�WT^X��ɕͱ�%���B�Pu処�.����bk�\[���k�[��`�T/+�'���g����F#�1�p���{I/l�O6��"I7�;�=T^��4ЩȠޮ�D�\�VwZ�E_�C�����W$ iQ}F'�����0��MQ�[�&uy6��9C��`���!�6C��È����&
���3�D�	rme9��W�����r��gE��F~���]?��C��rF���� � ̴�1�Jl��t^f4Y��l��-�Y��C3s�	l������C�N     