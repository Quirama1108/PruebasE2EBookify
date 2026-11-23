-- Solo lectura. Devuelve UNA fila con el esquema public y los catalogos. Sin usuarios, reservas ni claves.
SELECT jsonb_build_object(
 'columnas', (SELECT jsonb_agg(jsonb_build_object('t',c.relname,'n',a.attnum,'c',a.attname,
      'tipo',format_type(a.atttypid,a.atttypmod),'nn',a.attnotnull,'ident',a.attidentity,'gen',a.attgenerated,
      'def',pg_get_expr(d.adbin,d.adrelid)) ORDER BY c.relname,a.attnum)
    FROM pg_class c JOIN pg_attribute a ON a.attrelid=c.oid AND a.attnum>0 AND NOT a.attisdropped
    LEFT JOIN pg_attrdef d ON d.adrelid=c.oid AND d.adnum=a.attnum
    WHERE c.relnamespace='public'::regnamespace AND c.relkind IN ('r','p')),
 'restricciones', (SELECT jsonb_agg(jsonb_build_object('t',conrelid::regclass::text,'n',conname,'tipo',contype,'def',pg_get_constraintdef(oid)))
    FROM pg_constraint WHERE connamespace='public'::regnamespace AND conrelid<>0),
 'indices', (SELECT jsonb_agg(indexdef) FROM pg_indexes WHERE schemaname='public'),
 'triggers', (SELECT jsonb_agg(pg_get_triggerdef(t.oid)) FROM pg_trigger t JOIN pg_class c ON c.oid=t.tgrelid
    WHERE c.relnamespace='public'::regnamespace AND NOT t.tgisinternal),
 'funciones', (SELECT jsonb_agg(pg_get_functiondef(p.oid)) FROM pg_proc p
    WHERE p.pronamespace='public'::regnamespace AND p.prokind IN ('f','p')
      AND NOT EXISTS (SELECT 1 FROM pg_depend x WHERE x.objid=p.oid AND x.classid='pg_proc'::regclass AND x.deptype='e')),
 'vistas', (SELECT jsonb_agg(jsonb_build_object('n',viewname,'def',definition)) FROM pg_views WHERE schemaname='public'),
 'enums', (SELECT jsonb_agg(jsonb_build_object('n',t.typname,'v',e.enumlabel) ORDER BY t.typname,e.enumsortorder)
    FROM pg_type t JOIN pg_enum e ON e.enumtypid=t.oid WHERE t.typnamespace='public'::regnamespace),
 'extensiones', (SELECT jsonb_agg(extname) FROM pg_extension),
 'catalogos', (SELECT jsonb_object_agg(c.relname, query_to_xml(format('SELECT * FROM public.%I',c.relname),true,false,'')::text)
    FROM pg_class c WHERE c.relnamespace='public'::regnamespace AND c.relkind IN ('r','p')
      AND (c.relname IN ('tbl_rol','tbl_rol_proveedor','tbl_categoria_comercial','tbl_dia_semana')
        OR c.relname LIKE 'tbl\_tipo\_%' OR c.relname LIKE 'tbl\_estado\_%'))
)::text AS esquema;
