# language: es
Característica: Transacciones de punta a punta de una reserva
  Como equipo de calidad de Bookify
  queremos recorrer el ciclo completo de una reserva entre clientes y negocios
  para saber si el producto cumple lo que esperan ambos.

  @T1 @HU-03 @HU-17 @aceptacion
  Escenario: Ana reserva una cita y la Barbería la registra como atendida
    Dado que la Barbería tiene un horario libre para su servicio
    Cuando Ana consulta la oferta de la Barbería
    Entonces Ana encuentra ese horario en la oferta
    Cuando Ana reserva ese horario
    Entonces la reserva de Ana queda confirmada
    Y Ana ya no ve ese horario en la oferta de la Barbería
    Cuando termina la hora de la cita
    Y la Barbería marca la cita de Ana como "atendida"
    Entonces la cita de Ana figura como "atendida" en el historial de la Barbería
    Y queda registrado que la Barbería cambió la cita de Ana de "confirmada" a "atendida"

  @T2 @HU-03 @HU-17 @aceptacion
  Escenario: Luis no puede tomar el horario de Ana y Ana no asiste a su cita
    Dado que la Barbería tiene un horario libre para su servicio
    Cuando Ana reserva ese horario
    Entonces la reserva de Ana queda confirmada
    Cuando Luis intenta reservar ese horario
    Entonces Bookify rechaza la reserva de Luis porque el horario ya no está disponible
    Y el horario conserva una única reserva, a nombre de Ana
    Cuando termina la hora de la cita
    Y la Barbería marca la cita de Ana como "no asistió"
    Entonces la cita de Ana figura como "no asistió" en el historial de la Barbería
    Y queda registrado que la Barbería cambió la cita de Ana de "confirmada" a "no asistió"

  @T3 @HU-03 @defecto @bug-291
  Escenario: Luis no puede dejar una cita a nombre de Ana
    Dado que la Barbería tiene un horario libre para su servicio
    Cuando Luis intenta reservar ese horario a nombre de Ana
    Entonces Bookify rechaza la solicitud o registra la cita a nombre de Luis
    Y Ana no tiene ninguna cita en ese horario

  @T4 @HU-01 @defecto @bug-189
  Escenario: El Spa no puede publicar horarios en la agenda de la Barbería
    Dado que la Barbería tiene un día sin horarios publicados
    Cuando el Spa intenta publicar un horario ese día en la agenda de la Barbería
    Entonces el Spa recibe un rechazo porque la agenda es de otro negocio
    Y la Barbería sigue sin horarios publicados ese día

  @T5 @HU-01 @HU-03 @defecto @bug-187
  Escenario: Ana reserva un horario que la Barbería acaba de publicar
    Dado que la Barbería tiene un día sin horarios publicados
    Y la Barbería publicó ese día un horario de "10:00" a "11:00"
    Cuando Ana reserva ese horario
    Entonces la reserva de Ana queda confirmada
    Y Ana ya no ve ese horario en la oferta de la Barbería
