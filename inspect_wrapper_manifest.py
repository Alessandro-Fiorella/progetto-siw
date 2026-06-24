import zipfile
from pathlib import Path
p = Path('.mvn/wrapper/maven-wrapper.jar')
print('exists', p.exists(), 'size', p.stat().st_size if p.exists() else None)
with zipfile.ZipFile(p, 'r') as z:
    for name in z.namelist():
        if name.upper().endswith('MANIFEST.MF'):
            print('MANIFEST', name)
            print(z.read(name).decode('utf-8', 'replace'))
            break
