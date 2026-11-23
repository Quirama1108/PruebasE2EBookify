# language: es
@HU-03
Característica: Crear una reserva
  Como cliente autenticado
  quiero reservar un servicio en un horario disponible
  para asegurar mi cita.

  # La reserva correcta y la doble reserva se comprueban en las transacciones T1 y T2.

  @defecto @bug-183
  Escenario: No se puede reservar un horario que ya comenzó
    Dado que la Barbería tiene un horario sin reservar que ya comenzó
    Cuando Ana intenta reservar ese horario
    Entonces Bookify rechaza la reserva de Ana porque el horario ya comenzó
    Y Ana no tiene ninguna cita en ese horario
