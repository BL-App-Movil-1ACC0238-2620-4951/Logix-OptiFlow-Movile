# Logix OptiFlow — App móvil (Android)

Cliente Android del sprint **Search & Booking** (US05–US07): bienvenida/login de paciente, búsqueda de ópticas, disponibilidad y reserva de citas. Consume el backend desplegado en Render o un servidor local.

## Backend y Swagger

| Entorno | Base URL |
|--------|-----------|
| **Producción (Render)** | `https://logix-optiflow-back-end.onrender.com/` |
| **Local (emulador)** | `http://10.0.2.2:8080/` (backend en tu PC, puerto 8080) |

- **Swagger UI:** [https://logix-optiflow-back-end.onrender.com/swagger-ui/index.html](https://logix-optiflow-back-end.onrender.com/swagger-ui/index.html)
- **OpenAPI JSON:** `GET /v3/api-docs`

> Render en plan free puede “dormir” el servicio: la primera petición puede tardar ~1 minuto. Vuelve a intentar o abre Swagger en el navegador para despertarlo.

## Ejecutar la app

1. Abre el proyecto en **Android Studio** y sincroniza Gradle.
2. En **Build Variants**, elige:
   - **`prodDebug`** → API en Render (recomendado para demo y registro).
   - **`localDebug`** → solo si tienes el backend corriendo en `localhost:8080`.
3. **Run** en emulador o dispositivo.

Opcional en `local.properties` (no se sube a git): `api.base.url.prod` / `api.base.url.local`. Ver `local.properties.example`.

---

## Datos de demo (dos segmentos del dominio)

El backend expone dos grandes áreas en Swagger:

1. **Search & Booking — Pacientes** (lo que usa esta app móvil).
2. **Clinical & Commercial** (expedientes, cotizaciones, órdenes de trabajo, etc.; **sin login de “personal clínico” en la app móvil aún** — se demuestra por Swagger).

### Segmento 1 — Paciente (login en la app)

Cuenta de demostración **ya usable** en Render (registro/login vía app o Swagger `POST /login`):

| Campo | Valor |
|--------|--------|
| **Correo** | `demo.paciente@optiflow.pe` |
| **Contraseña** | `Optiflow123` |
| **Nombre** | Demo Paciente OptiFlow |
| **Teléfono** | `999888777` |
| **ID paciente (UUID)** | `dc0315b5-57a9-4a43-8931-165bd3b6a890` |

**Flujo en la app (simulación US05–US07):**

1. Pantalla **OptiFlow** → pestaña **Iniciar sesión** → rol **Paciente**.
2. Correo y contraseña de la tabla → **Entrar a mi cuenta**.
3. **Buscar ópticas** (nombre, dirección, rating 4.0+ / 4.5+).
4. Elegir sucursal → **Disponibilidad** → reservar un `timeSlot` (requiere sesión de paciente).

**Flujo equivalente en Swagger:**

| Paso | Método | Ruta |
|------|--------|------|
| Login | `POST` | `/login` → body `{ "email", "password" }` |
| Listar ópticas | `GET` | `/optical-stores` |
| Búsqueda avanzada | `GET` | `/optical-stores/search?minRating=4.5` |
| Horarios | `GET` | `/optical-stores/{id}/availability` |
| Reservar cita | `POST` | `/appointments` → `patientId`, `opticalStoreId`, `timeSlotId` |
| Citas del paciente | `GET` | `/patients/{id}/appointments` |

### Segmento 2 — Clínico / comercial (Swagger)

No hay pantalla de **Personal clínico** en la app móvil (mensaje “próxima versión”). Para **demostrar el segundo segmento**, usa Swagger con el **mismo paciente** tras crear una cita, más el **catálogo sembrado** en el backend:

**Ópticas (seed):**

| Sucursal | ID |
|----------|-----|
| OptiFlow Miraflores | `11111111-1111-1111-1111-111111111111` |
| OptiFlow San Isidro | `22222222-2222-2222-2222-222222222222` |

**Técnicos (seed) — órdenes de trabajo:**

| Nombre | ID |
|--------|-----|
| Jorge Salas | `33333333-3333-3333-3333-333333333333` |
| María Quispe | `44444444-4444-4444-4444-444444444444` |

**Ejemplo de cadena en Swagger (después de `POST /appointments`):**

1. `POST /clinical-records` — `patientId` + `appointmentId` de la cita recién creada.
2. `POST /clinical-records/{id}/medical-history` — historia clínica.
3. `POST /clinical-records/{id}/prescription` — receta óptica.
4. `POST /quotations` / `POST /work-orders` — cotización y orden de laboratorio (tags *Clinical & Commercial* en Swagger).

Los IDs de `timeSlot` cambian con el tiempo; obtén uno vigente con  
`GET /optical-stores/11111111-1111-1111-1111-111111111111/availability`.

**Ejemplo body `POST /appointments`:**

```json
{
  "patientId": "dc0315b5-57a9-4a43-8931-165bd3b6a890",
  "opticalStoreId": "11111111-1111-1111-1111-111111111111",
  "timeSlotId": "<copiar de availability>"
}
```

---

## Registro de un paciente nuevo

Si prefieres otra cuenta de prueba:

- App: **Registrarme** → Paciente → `POST /patients` vía UI.
- Swagger: **Search & Booking - Patients** → `POST /patients` (contraseña mínimo 8 caracteres).

Luego `POST /login` con el mismo correo y contraseña.

---

## Stack técnico (resumen)

- Kotlin, Jetpack Compose, Material 3, Navigation Compose  
- Retrofit + OkHttp + Moshi  
- Product flavors `local` / `prod` → `BuildConfig.API_BASE_URL`  
- Sesión de paciente en DataStore (token + datos tras login)

## Repos relacionados

- Backend: `https://logix-optiflow-back-end.onrender.com/`
- Reporte / Figma: repositorio **Logix-OptiFlow-Report** del equipo
