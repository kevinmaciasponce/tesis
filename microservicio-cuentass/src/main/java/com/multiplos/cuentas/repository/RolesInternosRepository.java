package com.multiplos.cuentas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.multiplos.cuentas.models.Rol;
import com.multiplos.cuentas.models.RolInt;
import com.multiplos.cuentas.models.RolesInternos;
import com.multiplos.cuentas.pojo.empleado.RolInternoResponse;

public interface RolesInternosRepository extends JpaRepository<RolesInternos, Long> {


}
