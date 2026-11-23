-- Datos base de QA (ficticios). Los escenarios no los modifican.
INSERT INTO tbl_pais(id_pais,nombre) VALUES (1,'Colombia');
INSERT INTO tbl_departamento(id_departamento,id_pais,nombre) VALUES (1,1,'Antioquia');
INSERT INTO tbl_ciudad (id_ciudad,id_departamento,nombre) VALUES (1,1,'Medellin');
DO $$
DECLARE n record; u uuid; p uuid;
BEGIN
 FOR n IN SELECT * FROM (VALUES ('a','Barberia QA','barberia@bookify-qa.test','Corte QA'),('b','Spa QA','spa@bookify-qa.test','Masaje QA')) v(k,nombre,correo,servicio) LOOP
  INSERT INTO tbl_usuario(id_estado_usuario,nombres,correo,password_hash)
   SELECT id_estado_usuario,n.nombre,n.correo,crypt('Calidad-QA-Local-2026!',gen_salt('bf',10)) FROM tbl_estado_usuario WHERE codigo='ACTIVO' RETURNING id_usuario INTO u;
  INSERT INTO tbl_usuario_rol(id_usuario,id_rol) SELECT u,id_rol FROM tbl_rol WHERE codigo='PROVEEDOR';
  p := (repeat(n.k,8)||'-0000-4000-8000-000000000001')::uuid;
  INSERT INTO tbl_proveedor(id_proveedor,nombre_comercial,direccion,id_categoria_comercial,id_tipo_proveedor) SELECT p,n.nombre,'Calle QA 1',(SELECT min(id_categoria_comercial) FROM tbl_categoria_comercial),(SELECT min(id_tipo_proveedor) FROM tbl_tipo_proveedor);
  INSERT INTO tbl_proveedor_usuario(id_proveedor,id_usuario,id_rol_proveedor) SELECT p,u,id_rol_proveedor FROM tbl_rol_proveedor WHERE codigo='PROPIETARIO';
  INSERT INTO tbl_sede(id_sede,id_proveedor,id_ciudad,nombre,direccion) VALUES (p,p,1,'Sede '||n.nombre,'Calle QA 1');
  INSERT INTO tbl_servicio(id_servicio,id_proveedor,nombre,duracion_minutos) VALUES (p,p,n.servicio,60);
  INSERT INTO tbl_servicio_sede(id_servicio_sede,id_servicio,id_sede) VALUES (p,p,p);
  INSERT INTO tbl_recurso(id_recurso,id_proveedor,id_tipo_recurso,nombre) SELECT p,p,id_tipo_recurso,'Recurso '||n.nombre FROM tbl_tipo_recurso WHERE codigo='HUMANO';
  INSERT INTO tbl_recurso_sede(id_recurso,id_sede) VALUES (p,p);
  INSERT INTO tbl_servicio_recurso(id_servicio_sede,id_recurso) VALUES (p,p);
  INSERT INTO tbl_agenda(id_agenda,id_proveedor,id_sede,nombre) VALUES (p,p,p,'Agenda '||n.nombre);
  INSERT INTO tbl_agenda_servicio(id_agenda_servicio,id_agenda,id_servicio_sede) VALUES (p,p,p);
 END LOOP;
END $$;
