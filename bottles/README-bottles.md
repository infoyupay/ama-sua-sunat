# SUNAT en Bottles
**InfoYupay Way™**

## 1. Identificación del Problema

En un país donde la autoridad tributaria obliga a miles de contribuyentes a usar sistemas **antiguos**, **cerrados** y **no compatibles** con plataformas modernas, aún persisten tres aplicaciones SUNAT instaladas on-premises que dependen por completo del ecosistema Windows:

- **PDT Integrador**
- **PLAME**
- **PLE**

El problema es técnico, histórico y estructural:

| Sistema        | Tecnología             | Estado actual                                | Problema en Linux                 |
|----------------|------------------------|----------------------------------------------|-----------------------------------|
| PDT Integrador | Win32 / Delphi         | Obsoleto, pero aún vigente en varios módulos | Requiere APIs antiguas de Windows |
| PLAME          | Adobe AIR (Harman AIR) | Vigente                                      | AIR no existe para Linux          |
| PLE            | Adobe AIR (Harman AIR) | Vigente                                      | AIR no existe para Linux          |

**Adobe AIR ya no pertenece a Adobe**: fue vendido a **Harman (Samsung)**. El runtime sigue siendo propietario y no 
tiene versión oficial para Linux.

Por tanto, ejecutar estas herramientas en Linux exige soluciones creativas y prácticas, que respeten la libertad del
usuario sin depender de licencias privativas.

Las alternativas existentes han sido:

- Máquina virtual con Windows
- Wine con Winetricks
- WineHQ dockerizado
- Bottles

A continuación analizamos por qué Bottles es la opción más estable y libre en 2025.

### VM con Windows
Fácil de implementar, pero introduce dependencia total de Microsoft Windows, consumo elevado de recursos 
y aislamiento innecesario.

### Wine + Winetricks
Funciona, pero requiere experiencia y da problemas bajo Wayland: aun con XWayland hay fallas de renderizado y ventanas
que no aparecen.

### WineHQ dockerizado
Dificultades similares a Wine en Wayland. Además, pierde aceleración de ventanas.

### Bottles
La solución más moderna, limpia y compatible. Maneja prefixes aislados, configuraciones reproducibles, 
dependencias comunes y opera correctamente bajo **Wayland** o **X11**, dejando la elección al usuario.

---

## 2. Objetivos

- Ejecutar **PLE** en Linux sin Windows.
- Ejecutar **PLAME** en Linux sin Windows.
- Ejecutar **PDT Integrador** en Linux sin Windows.
- Hacerlo de manera estable, libre de máquinas virtuales y sin depender de software privativo adicional.

---

## 3. Hipótesis (Plan de Acción)

La forma más práctica, estable y replicable de ejecutar estas herramientas es **Bottles**.

Aunque Docker sería ideal para empaquetar un entorno inmutable, hoy su compatibilidad gráfica con Wayland y AIR 
no es óptima. Bottles, en cambio:

- funciona nativamente bajo Wayland o X11;
- permite crear prefixes aislados;
- facilita instalar dependencias de Wine;
- maneja configuración fina sin esfuerzo;
- respeta la libertad del usuario para elegir su stack.

Plan:

1. Crear una botella para PLE y PLAME (ambos dependen de AIR).
2. Crear otra botella independiente para PDT Integrador (requiere bibliotecas Win32 más antiguas).

---

## 4. Experimentación

Instalaremos Bottles desde Flatpak y crearemos la botella llamada **ama-sua-AIR** para PLE y PLAME.

### Dependencias necesarias en la botella

Estas dependencias aseguran que Adobe AIR, PLAME y PLE funcionen correctamente:

- `mono`
- `gecko`
- `dotnet35sp1`
- `dotnet40`
- `vcredist2008`
- `vcredist2010`
- `vbrun6`
- `allfonts`
- `aairruntime` *(opcional: reemplaza la descarga manual del runtime de Harman AIR)*

#### ¿Por qué cada una?
- **.NET 3.5 SP1** → Instaladores antiguos de AIR usan componentes MSI que requieren APIs de .NET.
- **.NET 4.0** → Varias DLL internas de PLAME dependen de esta versión.
- **MSVC 2008/2010** → PLAME carga DLLs compiladas en esos toolchains.
- **VB6 runtime** → Algunos formularios internos del instalador dependen de VB6.
- **allfonts** → AIR **no** puede usar fuentes del sistema Linux; sin esto el texto sale cortado o ilegible.
- **mono / gecko** → Requerido para renderizar vistas HTML internas usadas por los instaladores.
- **Harman AIR Runtime** → Sin esto, PLAME y PLE no inician.

### Configuración de las unidades (drives)

#### ⚠️ Advertencia importante: **NO usar la unidad Z: para exportar archivos**

Aunque Wine monta `/` como **Z:**, este punto de montaje **no emula permisos NTFS correctamente**, lo que causa cuelgues
en PLAME al generar archivos.

**Solución comprobada:**  
Crear una unidad nueva:

- D:\
- E:\
- F:\
- la que prefieras (menos Z)

apuntando a una carpeta en Linux, por ejemplo:

- `~/SUNAT`
- `~/Downloads`
- `~/Documentos/SUNAT`

Esto garantiza que los exports (txt, xple, pdf) funcionen sin problemas.

### Instalación de PLE y PLAME

1. Descargar los instaladores desde la web de SUNAT.
2. Dentro del prefix de la botella, crear una carpeta: `C:\installers`
3. Copiar los instaladores ahí.
4. Abrir la consola de Bottles.
5. Ejecutarlos desde:
```terminaloutput
cd C:\installers
installer.exe
```

Esto evita que el instalador apunte a rutas fuera del prefix, lo cual causa fallas.

Finalizada la instalación, Bottles creará los accesos directos automáticamente.

---

## 5. Notas Adicionales

### Impresión

Para imprimir desde el PDT Integrador:

1. Instalar **cups-pdf** en Linux.
2. Configurar la impresora *PDF* como predeterminada.
3. PDT la detectará como si fuera una impresora Windows.

Generará los PDF en la ruta configurada por CUPS.

---

### Acceso a datos (xple, txt, etc.)

Para facilitar la vida, puedes crear un symlink hacia el interior del prefix:

```bash
ln -s ~/.var/app/com.usebottles.bottles/data/bottles/bottles/ama-sua-AIR/drive_c/PLE ~/PLE
```

Esto permite abrir y copiar los archivos generados como si fueran nativos.

## 6. Troubleshooting (Guía Rápida)
### PLE/PLAME no abre

→ Falta instalar Harman AIR Runtime o no se instaló correctamente.

### Se cuelga al exportar

→ Está usando la unidad Z:. Crear unidad D: y volver a intentar.

### El runtime AIR se “pierde”

→ Reiniciar la bottle y reinstalar aairruntime.

### Ventanas transparentes o invisibles bajo Wayland

→ Crear la botella con modo X11, no Wayland.

### Error al instalar ejecutables

→ Ejecutar siempre desde dentro del prefix: C:\installers.

### PDT no imprime

→ Revisar si cups-pdf está instalado y configurado como predeterminado.

---

## 7. Conclusión

Con esta configuración, las aplicaciones SUNAT históricamente limitadas a Windows pueden ejecutarse de forma estable y
eficiente en Linux —sin máquinas virtuales, sin dependencias privativas y sin sacrificar compatibilidad.

Este documento forma parte del proyecto AMA-SUA-SUNAT:

> Tecnología libre para un Estado honesto. Interoperabilidad tributaria sin cadenas.