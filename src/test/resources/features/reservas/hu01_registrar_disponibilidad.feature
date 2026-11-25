# language: es
@HU-01
Característica: Registrar disponibilidad del proveedor
  Como proveedor autenticado
  quiero registrar un horario disponible para un servicio
  para que los clientes puedan reservar únicamente en horarios válidos.

  # Que el horario publicado se pueda reservar lo comprueba la transacción T5.

  @aceptacion
  Escenario: Un horario válido queda disponible y los clientes lo ven
    Dado que la Barbería tiene un día sin horarios publicados
    Cuando la Barbería publica ese día un horario de "10:00" a "11:00"
    Entonces el horario de "10:00" a "11:00" queda guardado como disponible en la agenda de la Barbería
    Y Ana ve el horario de "10:00" a "11:00" en la oferta de la Barbería

  @aceptacion
  Esquema del escenario: Se rechaza un horario que <cruce>
    Dado que la Barbería tiene un día sin horarios publicados
    Y la Barbería publicó ese día un horario de "10:00" a "11:00"
    Cuando la Barbería intenta publicar ese día un horario de "<inicio>" a "<fin>"
    Entonces la Barbería recibe un rechazo por conflicto de horarios
    Y ese día la Barbería conserva únicamente el horario de "10:00" a "11:00"

    Ejemplos:
      | cruce                              | inicio | fin   |
      | termina dentro del existente       | 09:30  | 10:30 |
      | empieza dentro del existente       | 10:30  | 11:30 |
      | está contenido en el existente     | 10:15  | 10:45 |
      | contiene por completo al existente | 09:00  | 12:00 |
      | es igual al existente              | 10:00  | 11:00 |

  @aceptacion
  Escenario: Se acepta un horario que empieza justo cuando termina el existente
    Dado que la Barbería tiene un día sin horarios publicados
    Y la Barbería publicó ese día un horario de "10:00" a "11:00"
    Cuando la Barbería publica ese día un horario de "11:00" a "12:00"
    Entonces el horario de "11:00" a "12:00" queda guardado como disponible en la agenda de la Barbería

  @aceptacion
  Esquema del escenario: Se rechaza un horario cuya hora final <motivo>
    Dado que la Barbería tiene un día sin horarios publicados
    Cuando la Barbería intenta publicar ese día un horario de "<inicio>" a "<fin>"
    Entonces la Barbería recibe un rechazo porque las horas no son válidas
    Y la Barbería sigue sin horarios publicados ese día

    Ejemplos:
      | motivo                   | inicio | fin   |
      | es anterior a la inicial | 11:00  | 10:00 |
      | es igual a la inicial    | 10:00  | 10:00 |
