# Tipos de mensajes RCS

Fuente principal: [phones.agentMessages](https://developers.google.com/business-communications/rcs-business-messaging/reference/rest/v1/phones.agentMessages?hl=es-419)

Según la especificación oficial, el campo `content` de un mensaje del agente solo puede tomar uno de los siguientes valores:

- **text**: Texto codificado en UTF-8 de hasta 3,072 caracteres.
- **uploadedRbmFile**: Identificadores de un archivo y su miniatura subidos a la plataforma.
- **richCard**: Tarjeta enriquecida independiente.
- **contentInfo**: Información de un archivo, incluidas la URL y la miniatura.

Estos tipos se utilizan para simular la recepción de mensajes en el gateway.
