# language: es
@HU-17
Característica: Registrar asistencia o inasistencia
  Como proveedor
  quiero registrar si una cita finalizada fue atendida o si el cliente no asistió
  para mantener un historial confiable sobre el cumplimiento de mis reservas.

  # Registrar una cita atendida y una inasistencia se comprueba en las transacciones T1 y T2.

  @aceptacion
  Esquema del escenario: No se registra "<resultado>" en una cita que <momento>
    Dado que Ana tiene una cita confirmada con la Barbería que <momento>
    Cuando la Barbería intenta marcar la cita de Ana como "<resultado>"
    Entonces la Barbería recibe un rechazo porque la cita no ha terminado
    Y la cita de Ana figura como "confirmada" en el historial de la Barbería
    Y no queda registro de asistencia de la cita de Ana

    Ejemplos:
      | momento         | resultado  |
      | no ha comenzado | atendida   |
      | no ha comenzado | no asistió |
      | está en curso   | atendida   |
      | está en curso   | no asistió |

  @aceptacion
  Esquema del escenario: No se registra "<resultado>" en una cita cancelada
    Dado que Ana tenía una cita con la Barbería que fue cancelada y cuya hora ya pasó
    Cuando la Barbería intenta marcar la cita de Ana como "<resultado>"
    Entonces la Barbería recibe un rechazo porque la cita está cancelada
    Y la cita de Ana figura como "cancelada" en el historial de la Barbería
    Y no queda registro de asistencia de la cita de Ana

    Ejemplos:
      | resultado  |
      | atendida   |
      | no asistió |

  @aceptacion
  Escenario: Un negocio no puede registrar la asistencia de una cita de otro negocio
    Dado que Ana tiene una cita confirmada con la Barbería que ya terminó
    Cuando el Spa intenta marcar la cita de Ana como "atendida"
    Entonces el Spa recibe un rechazo porque la cita es de otro negocio
    Y la cita de Ana figura como "confirmada" en el historial de la Barbería
    Y no queda registro de asistencia de la cita de Ana
