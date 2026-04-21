# 🅿️ Proyecto SmartParking — Guía Completa

> **Asignatura:** Programación II · Curso 2025/26 · 2º Semestre  
> **Tipo:** Proyecto en parejas · Java 1.16+  

---

## 📋 Índice

1. [¿Qué es SmartParking?](#-qué-es-smartparking)
2. [Conceptos clave del dominio](#-conceptos-clave-del-dominio)
3. [Arquitectura y paquetes](#-arquitectura-y-paquetes)
4. [Diagrama de dependencias entre paquetes](#-diagrama-de-dependencias-entre-paquetes)
5. [Diagrama de dependencias entre clases](#-diagrama-de-dependencias-entre-clases)
6. [Descripción de cada clase](#-descripción-de-cada-clase)
7. [Orden de implementación recomendado](#-orden-de-implementación-recomendado)
8. [Requisitos: Obligatorios vs Opcionales](#-requisitos-obligatorios-vs-opcionales)
9. [Errores a evitar](#-errores-a-evitar)
10. [Notas de entrega](#-notas-de-entrega)

---

## 🚗 ¿Qué es SmartParking?

SmartParking es una aplicación de gestión de aparcamiento para una localidad dividida en zonas geográficas cuadradas (**matriz N×M**). Los usuarios pueden **reservar plazas** desde el móvil antes de llegar, y la infraestructura de sensores y cámaras valida la ocupación.

### Dos tipos de reserva

| Tipo | Cuándo | Si no hay plaza libre... |
|------|--------|--------------------------|
| **Anticipada** | ≥ 24h antes del inicio | Se añade a una **lista de espera** con prioridad (ALTA > MEDIA > BAJA) |
| **Inmediata** | < 24h antes del inicio | Se busca en zonas **limítrofes** dentro de un radio máximo indicado por el usuario |

---

## 🗺️ Conceptos clave del dominio

### Distancia de Manhattan

La distancia entre dos zonas `(i, j)` e `(i', j')` es:

```
dist = |i - i'| + |j - j'|
```

> 💡 *Piénsalo como el número de manzanas que hay que recorrer en una ciudad cuadriculada: no puedes ir en diagonal, solo recto.*

### Criterios de búsqueda para reserva inmediata (en orden de prioridad)

```
1️⃣  Precio más bajo en la zona
2️⃣  Menor distancia Manhattan a la zona solicitada
3️⃣  Sentido horario de recorrido (como desempate final)
```

### Ejemplo visual — Radio desde (0,0)

```
     Col→  0    1    2    3    4
Fila↓
  0        X    1    3    6    ...
  1        2    4    7    ...
  2        5    8    ...
  3        9    ...
  4        ...
```
*Las zonas se exploran en espiral horaria, priorizando siempre el precio.*

### Lista de espera (reservas anticipadas)

```
Orden de prioridad → ALTA > MEDIA > BAJA
A igual prioridad  → FIFO (primero en llegar, primero en ser atendido)
```

---

## 📦 Arquitectura y paquetes

```
SmartParking/
│
├── 🟡 controladores/
│   ├── ControladorReservas.java          ← ampliar
│   └── excepciones/
│       ├── PlazaOcupada.java             ← proporcionada
│       ├── ReservaInvalida.java          ← proporcionada
│       └── SolicitudReservaInvalida.java ← proporcionada
│
├── 🟡 modelo/
│   ├── gestoresplazas/
│   │   ├── GestorLocalidad.java          ← ampliar
│   │   ├── GestorZona.java               ← ampliar
│   │   └── huecos/
│   │       ├── GestorHuecos.java         🟠 proporcionada completa
│   │       ├── Hueco.java                🟠 proporcionada completa
│   │       └── Plaza.java                🟠 proporcionada completa
│   │
│   ├── reservas/
│   │   ├── EstadoValidez.java            🟠 proporcionada completa
│   │   ├── Reserva.java                  🟠 proporcionada completa
│   │   └── Reservas.java                 🟠 proporcionada completa
│   │
│   └── reservas/solicitudesreservas/
│       ├── SolicitudReserva.java         🟡 ampliar
│       ├── SolicitudReservaAnticipada.java 🟡 implementar desde cero
│       ├── SolicitudReservaInmediata.java  🟡 implementar (opcional)
│       └── TEnumPrioridad.java           🟠 proporcionada completa
│
└── modelo/vehiculos/
    └── Vehiculo.java                     🟠 proporcionada completa
```

**Leyenda:**
- 🟡 **Amarillo** — El alumno debe implementar/ampliar
- 🟠 **Naranja** — Proporcionada completa, no modificar

---

## 🔗 Diagrama de dependencias entre paquetes

```
ControladorReservas
       │
       ├──────────────────────────────────┐
       │                                  │
  GestorLocalidad ◄──────────────  Reservas / Reserva
       │
  GestorZona[][]
       │
       ├── GestorHuecos  ←→  Hueco[]
       │       │
       │    Plaza[]
       │
       └── IList<SolicitudReservaAnticipada>  (lista de espera)
                │
         SolicitudReserva (superclase)
                ├── SolicitudReservaAnticipada
                └── SolicitudReservaInmediata (opcional)
```

---

## 🔗 Diagrama de dependencias entre clases


<img width="761" height="644" alt="Diagrama-de-clases" src="https://github.com/user-attachments/assets/6c986bdb-9d9e-4b4b-b2d5-ead13d991776" />


**Leyenda:**
- 🟡 **Amarillo** — El alumno debe implementar desde 0
- 🟠 **Naranja** — Proporcionada completa, no modificar
- 🔵 **Azul** — Parcialmente completa, ampliar

---

## 🧩 Descripción de cada clase

### `GestorHuecos` 🟠

> *Es como el libro de reservas de un hotel: sabe qué habitaciones (plazas) están libres en qué franja horaria.*

Gestiona los huecos temporales en el intervalo `[HORAINICIO, HORAFIN)` para las plazas de una zona.

| Método | Descripción |
|--------|-------------|
| `reservarHueco(tI, tF)` | Devuelve el hueco más pequeño que cubre `[tI, tF]`, o `null` si no existe |
| `liberarHueco(hueco)` | Registra el hueco como disponible de nuevo |
| `existeHueco(tI, tF)` | Indica si hay algún hueco libre en ese intervalo |

---

### `GestorZona` 🟡 *(ampliar)*

> *El gerente de cada zona del parking: coordina las plazas, la lista de espera y los huecos reservados.*

#### Atributos

```java
private int iZona, jZona;               // Coordenadas de la zona
private Plaza[] plazas;                 // Plazas físicas de la zona
private double precio;                  // Precio por hora
private IList<SolicitudReservaAnticipada> listaEspera;
private GestorHuecos gestorHuecos;
private IList<Hueco> huecosReservados;
```

#### Métodos obligatorios a implementar

| Método | Qué debe hacer |
|--------|----------------|
| `GestorZona(i, j, noPlazas, precio)` | Crear plazas, listas vacías y el `GestorHuecos` |
| `reservarHueco(tI, tF)` | Delegar en `gestorHuecos`, añadir a `huecosReservados` y devolver el hueco |
| `existeHueco(tI, tF)` | Delegar en `gestorHuecos.existeHueco()` |
| `existeHuecoReservado(hueco)` | Comprobar si el hueco está en `huecosReservados` |
| `meterEnListaEspera(solicitud)` | Insertar manteniendo el orden: prioridad DESC, luego FIFO |

#### Métodos opcionales

| Método | Qué debe hacer |
|--------|----------------|
| `liberarHueco(hueco)` | Quitar de `huecosReservados` y liberar en `gestorHuecos` |
| `getSolicitudesAtendidasListaEspera()` | Recorrer `listaEspera`, reservar huecos posibles, devolverlos y eliminarlos de la lista |

---

### `GestorLocalidad` 🟡 *(ampliar)*

> *La dirección central del parking: conoce todas las zonas y delega en ellas.*

#### Atributo principal

```java
private GestorZona[][] gestoresZonas;
```

#### Métodos obligatorios

| Método | Qué debe hacer |
|--------|----------------|
| `GestorLocalidad(plazas[][], precios[][])` | Crear un `GestorZona` para cada posición `(i,j)` |
| `existeZona(i, j)` | Comprobar que `0 ≤ i < N` y `0 ≤ j < M` |
| `getRadioMaxI()` | Devolver `N - 1` |
| `getRadioMaxJ()` | Devolver `M - 1` |
| `getGestorZona(i, j)` | Devolver `gestoresZonas[i][j]` |
| `existeHuecoReservado(hueco, i, j)` | Delegar en `gestoresZonas[i][j].existeHuecoReservado(hueco)` |

#### Método opcional

| Método | Qué debe hacer |
|--------|----------------|
| `getSolicitudesAtendidasListaEspera(i, j)` | Delegar en `getGestorZona(i,j).getSolicitudesAtendidasListaEspera()` |

---

### `SolicitudReserva` 🟡 *(ampliar — superclase)*

> *El formulario de reserva: contiene todos los datos comunes a cualquier tipo de solicitud.*

#### Atributos (privados)

```java
private int iZona, jZona;
private LocalDateTime tInicial, tFinal;
private Vehiculo vehiculo;
private GestorZona gestorZona;   // null hasta que se gestiona
private Hueco hueco;             // null hasta que se completa la reserva
```

#### Métodos obligatorios

| Método | Lógica |
|--------|--------|
| `esValida(gestorLocalidad)` | `existeZona(i,j)` **&&** `tI < tF` **&&** `!vehiculo.getSancionado()` |
| `gestionarSolicitudReserva(gestor)` | Asignar `gestorZona = gestor.getGestorZona(i,j)` e intentar `gestorZona.reservarHueco(tI, tF)` |

---

### `SolicitudReservaAnticipada` 🟡 *(implementar desde cero)*

> *Hereda de `SolicitudReserva` y añade la prioridad. Si no hay hueco, se queda en lista de espera.*

#### Atributo adicional

```java
private TEnumPrioridad prioridad;  // ALTA, MEDIA o BAJA
```

#### Métodos

| Método | Lógica |
|--------|--------|
| `SolicitudReservaAnticipada(i, j, tI, tF, vehiculo, prioridad)` | Llamar a `super(...)` e inicializar `prioridad` |
| `gestionarSolicitudReserva(gestor)` | Llamar a `super.gestionarSolicitudReserva(gestor)`. Si `getHueco() == null` → llamar a `gestorZona.meterEnListaEspera(this)` |

---

### `SolicitudReservaInmediata` 🟡 *(opcional — implementar desde cero)*

> *Tiene un radio de búsqueda: si la zona pedida no tiene hueco, explora las vecinas por precio, distancia y sentido horario.*

#### Atributo adicional

```java
private int radio;
```

#### Métodos

| Método | Lógica |
|--------|--------|
| `SolicitudReservaInmediata(i, j, tI, tF, vehiculo, radio)` | Llamar a `super(...)` e inicializar `radio` |
| `esValida(gestorLocalidad)` | `super.esValida()` **&&** `radio > 0` **&&** existe al menos una zona en el borde exacto del radio |
| `gestionarSolicitudReserva(gestor)` | `super.gestionarSolicitudReserva(gestor)`. Si `getHueco() == null` → explorar zonas dentro del radio ordenadas por **precio → distancia → horario** hasta encontrar hueco |

#### ⚠️ Validación del radio

El radio es válido si **existe al menos una zona** en la localidad que se encuentre **exactamente** a esa distancia o menos. Si todo el radio cae fuera del mapa, la solicitud es inválida.

```
Ejemplo: localidad 5×5, zona (0,0), radio=7 → existe zona (4,3) a dist 7 ✅
                                    radio=8 → no existe ninguna zona a dist 8 ❌
```

---

### `ControladorReservas` 🟡 *(ampliar)*

> *El jefe de operaciones: recibe peticiones del exterior y coordina todo el sistema.*

#### Atributos

```java
private Reservas registroReservas;
private GestorLocalidad gestorLocalidad;
```

#### Métodos obligatorios

| Método | Lógica |
|--------|--------|
| `ControladorReservas(plazas[][], precios[][])` | Crear `new Reservas()` y `new GestorLocalidad(plazas, precios)` |
| `hacerReserva(solicitud)` | Si `!solicitud.esValida(...)` → lanzar `SolicitudReservaInvalida`. Gestionar solicitud. Si `hueco != null` → registrar y devolver `numReserva`. Si no → devolver `-1` |
| `getReserva(numReserva)` | Delegar en `registroReservas.obtenerReserva(numReserva)` |
| `ocuparPlaza(i,j,numPlaza,numReserva,vehiculo)` | Validar reserva → lanzar `ReservaInvalida` si falla. Comprobar plaza libre → lanzar `PlazaOcupada` si ocupada. Asignar vehículo a la plaza |

#### Métodos opcionales

| Método | Lógica |
|--------|--------|
| `desocuparPlaza(numReserva)` | Asignar `null` al vehículo de la plaza y liberar el hueco |
| `anularReserva(numReserva)` | Liberar hueco y borrar del registro |
| `getReservasRegistradasDesdeListaEspera(i, j)` | Obtener solicitudes atendidas de la lista de espera y registrar una reserva por cada una |

---

### Clases proporcionadas completas 🟠

| Clase | Rol |
|-------|-----|
| `Plaza` | Representa una plaza física: tiene número y referencia al vehículo que la ocupa |
| `Hueco` | Intervalo temporal `[tI, tF]` asociado a una `Plaza` |
| `GestorHuecos` | Gestiona los huecos libres de un conjunto de plazas |
| `Reserva` | Objeto reserva registrada: contiene matrícula, hueco, zona y estado de validez |
| `Reservas` | Repositorio de reservas con `registrar`, `obtener` y `borrar` |
| `EstadoValidez` | Enum: `PENDING`, `OK`, `FAILED` |
| `Vehiculo` | Matrícula + flag de sanción |
| `TEnumPrioridad` | Enum: `BAJA`, `MEDIA`, `ALTA` |

---

## 🗓️ Orden de implementación recomendado

```
Fase 1 ── GestorZona (sin meterEnListaEspera)
            └─ Test: TestGestorZonaOblig (todos menos testMeterListaEspera)

Fase 2 ── GestorLocalidad (todos los métodos obligatorios)
            └─ Test: TestGestorLocalidadOblig

Fase 3 ── SolicitudReserva + SolicitudReservaAnticipada + meterEnListaEspera()
            └─ Test: TestSolicitudReservaAnticipadaOblig
                     TestGestorZonaOblig (completo)
                     TestGestorLocalidadOblig

Fase 4 ── ControladorReservas (obligatorio)
            4a. Constructor
            4b. hacerReserva()
            4c. getReserva()
            4d. ocuparPlaza()
            └─ Test: TestControladorReservasOblig

──── PARTE OPCIONAL ────────────────────────────────

Fase 5 ── desocuparPlaza()
Fase 6 ── anularReserva()
Fase 7 ── getReservasRegistradasDesdeListaEspera()
Fase 8 ── SolicitudReservaInmediata (esValida + gestionarSolicitudReserva)
            └─ Test: TestSolicitudReservaInmediataOpcional
                     TestGestorZonaOpcional
                     TestGestorLocalidadOpcional
                     TestControladorReservasOpcional
```

---

## ✅ Requisitos: Obligatorios vs Opcionales

| Requisito | Tipo | Nota máxima sin él |
|-----------|------|--------------------|
| Constructores (todas las clases menos `SolicitudReservaInmediata`) | Obligatorio | — |
| Solicitar reserva anticipada | Obligatorio | — |
| Ocupación de una plaza | Obligatorio | — |
| Desocupación de una plaza | Opcional | 6/10 |
| Anulación de reserva | Opcional | 6/10 |
| Revisión de lista de espera | Opcional | 6/10 |
| Solicitar reserva inmediata | Opcional | 6/10 |

> ⚠️ **Si solo se implementan los requisitos obligatorios, la nota máxima es 6 sobre 10.**

---

## 🚫 Errores a evitar

### Errores graves (calificación muy baja)

```
❌ Atributos públicos
❌ Operaciones de entrada/salida (System.out, Scanner...) en las clases implementadas
❌ Usar implementaciones de TADs distintas a las de la asignatura
```

### Errores penalizados por el profesor

```
❌ Atributos friendly o protected
❌ Métodos auxiliares públicos
❌ Código duplicado
❌ Código innecesario o inalcanzable
❌ Documentación deficiente (faltan comentarios significativos)
❌ Atributos innecesarios de clase o de instancia
❌ Identificadores no significativos
❌ No seguir el convenio de nombres de Oracle (camelCase, PascalCase, etc.)
❌ Código mal indentado
❌ Usar if para asignar o devolver booleanos (cuando basta con return expr)
❌ Bucles con ruptura: return, break o continue
❌ Recorrer estructuras completas cuando basta con recorrer una parte
```

---

## 📬 Notas de entrega

| Detalle | Valor |
|---------|-------|
| **Plataforma** | https://entrega2.fi.upm.es/ |
| **Plazo** | 5 de mayo a las 10:00 AM |
| **Intentos máximos** | 10 entregas |
| **Grupos** | Parejas (excepcionalmente tríos con autorización) |
| **Versión Java** | 1.16 |
| **Detección de copias** | Automática con software especializado |

> ⚠️ **Una entrega admitida no implica aprobado.** El código será revisado manualmente por un profesor.

---

*Documento generado como guía de referencia para el Proyecto SmartParking — Programación II, UPM.*
