import sys,glob,json,xml.etree.ElementTree as ET
d,out=sys.argv[1],sys.argv[2]
files=glob.glob(d) if d.endswith('.xml') else glob.glob(d+'/*.xml')
rows=[]
for f in files:
    if f.endswith('Completo.xml') or f.endswith('Aceptacion.xml') or f.endswith('Defectos.xml'): continue
    for tc in ET.parse(f).getroot().iter('testcase'):
        fl=tc.find('failure'); er=tc.find('error'); sk=tc.find('skipped')
        x=fl if fl is not None else er
        st='falla' if x is not None else ('omitido' if sk is not None else 'pasa')
        msg=''
        if x is not None: msg=' '.join(((x.get('message') or x.text or '')).replace('java.lang.AssertionError:','').split())[:220]
        rows.append(dict(escenario=tc.get('classname','').split('.')[-1]+' :: '+tc.get('name'),estado=st,duracion_s=float(tc.get('time') or 0),primera_linea=msg))
rows.sort(key=lambda r:r['escenario'])
json.dump(rows,open(out,'w',encoding='utf-8'),ensure_ascii=False,indent=1)
