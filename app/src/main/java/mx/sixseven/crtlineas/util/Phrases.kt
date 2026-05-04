package mx.sixseven.crtlineas.util

// ══════════════════════════════════════════════════════════
// Phrases.kt — CRT Líneas Android
// Las 210 frases + 60 de "llevas rato aquí"
// Autor: Six-Seven | MIT
// ══════════════════════════════════════════════════════════

object Phrases {

    // ── 60 frases de bienvenida ────────────────────────────
    val welcome = listOf(
        // Personitas
        "Bienvenido. Las personitas que trabajan dentro de la app ya encendieron sus computadoras del tamaño de un grano de arroz y están listas para trabajar.",
        "Revisando que todas las personitas de la oficina interna hayan fichado a tiempo... ✓ Confirmado. Pueden continuar.",
        "El jefe de las personitas diminutas de la app acaba de llegar en su bicicleta del tamaño de una hormiga. Ya podemos empezar.",
        "Las personitas del departamento de consultas están tomando su café matutino en tazas del tamaño de una semilla de ajonjolí. En un momento atienden.",
        "Advertencia: las personitas que trabajan aquí dentro tienen sindicato. Si haces demasiadas consultas seguidas pueden ir a huelga. Consulta con moderación.",
        // SAT / IMSS / Burocracia
        "Sabías que el SAT sabe exactamente cuánto ganaste el año pasado pero aun así te pide que se lo digas tú. Esta app no hace eso. Aquí la información fluye en la dirección correcta.",
        "Esta app tarda 15 minutos en lo que el IMSS tarda 3 semanas, 2 citas canceladas y una llamada que nunca contestaron.",
        "En el tiempo que tardas en sacar turno en el SAT, esta app ya consultó todos los portales del CRT y te sirvió los resultados con limón y sal.",
        "La señora de la ventanilla que te dice 'ese trámite es en otra ventanilla' no existe aquí. Esta app hace todo en una sola pantalla. Revolucionario.",
        "Ahorita empezamos. Y cuando decimos ahorita, decimos en segundos. No en el sentido mexicano de ahorita.",
        "En México para demostrar que no te robaron la identidad tienes que ir tú a demostrarlo. Esta app hace ese trabajo. El sistema sigue sin tener sentido, pero al menos ya tenemos la app.",
        "Dato histórico: antes de esta app, revisar todos los portales del CRT tomaba entre 3 y 5 horas. Aproximadamente lo mismo que esperar en el IMSS para una cita que al final cancelaron.",
        "La señora de la ventanilla que te manda a sacar copia de la copia de la copia no trabaja aquí. Bienvenido a un trámite que sí funciona.",
        "Esta app no te va a pedir que regreses mañana porque el sistema está caído. Los sistemas que consultamos nosotros sí pueden estar caídos, pero al menos te avisamos cuáles y por qué.",
        "Curiosidad: el CRT tiene más de 100 portales activos. Revisarlos todos manualmente es el equivalente moderno de hacer una fila en el Registro Civil.",
        // Castigo divino / Fantasmas
        "Según estudios no verificados, revisar 100 portales del CRT manualmente es uno de los 7 castigos modernos del purgatorio. Esta app te saca del purgatorio. Gratis.",
        "Los fantasmas de personas que intentaron revisar los portales del CRT manualmente deambulan en los pasillos del internet diciendo 'error 403... error 403...' Esta app exorciza esos fantasmas.",
        "Se dice que si dices 'VinculaTuLinea' tres veces frente al espejo, aparece un error 403. No lo intentes. Usa la app.",
        "Esta app fue construida sobre las ruinas espirituales de todos los que intentaron revisar los portales del CRT manualmente y no sobrevivieron para contarlo.",
        "Los antiguos mayas no tenían portales del CRT. Tampoco tenían esta app. Pero si hubieran tenido internet, definitivamente hubieran necesitado ambas cosas.",
        "Cuenta la leyenda que hay un portal del CRT que dice 'Próximamente' desde 2019. Su fantasma sigue apareciendo en la lista. Esta app lo documenta con respeto y algo de lástima.",
        "En algún lugar del internet hay un fantasma burocrático que lleva años esperando a que cargue un portal del CRT. Esta app no puede salvarlo. Pero a ti sí.",
        // Cifrado y privacidad
        "Tu CURP está cifrado con AES-256-GCM usando el chip de seguridad de tu teléfono. Ni nosotros podemos leerlo. Ni el SAT. Ni tu ex. Especialmente tu ex.",
        "Esta app guarda tu información de forma cifrada y la borra sola a las 24 horas. El SAT guarda tu información para siempre. Somos diferentes.",
        "Tus datos están cifrados con la misma tecnología que usan los bancos. Solo que aquí no te cobran comisión por consultarlos.",
        "Esta app no vende tus datos. No los comparte. No los guarda más de 24 horas. Si buscabas una app que sí haga todo eso, la dirección de Facebook es otra.",
        "El cifrado de esta app es tan robusto que si alguien roba tu teléfono y quiere ver tu CURP, necesitaría el chip de seguridad de tu propio dispositivo. O sea, también tendría que robarte a ti.",
        "Dato tranquilizador: tu CURP nunca sale de tu teléfono hacia nuestros servidores. Porque no tenemos servidores.",
        // 2am
        "Si estás abriendo esta app a las 2am porque alguien te dijo que pueden robar tu identidad y no pudiste dormir, bienvenido. Eres exactamente el usuario para el que fue hecha.",
        "Son las 2am y estás revisando si alguien registró líneas a tu nombre. Eso dice mucho de ti. Cosas buenas. Sigue así.",
        "Si llegaste aquí después de ver un video de TikTok sobre robo de identidad telefónica, bienvenido. El video tenía razón. Esta app también.",
        "Abriste esta app. Eso significa que ya eres más precavido que el 90% de la población que no sabe que estos portales existen. Punto para ti.",
        // La app gratuita
        "Esta app es gratuita, de código abierto, sin publicidad, y fue hecha por alguien con demasiadas opiniones sobre la burocracia mexicana. En fin.",
        "Advertencia: esta app fue hecha por un desarrollador que tiene más café en la sangre que glóbulos rojos. Si algo falla, probablemente fue a las 3am.",
        "Esta app no tiene publicidad. No tiene suscripciones. No tiene versión premium. El código fuente está en GitHub para que lo verifiques tú mismo.",
        "El desarrollador de esta app también hizo la extensión de Chrome y Firefox. Lo que quiere decir que tiene un problema serio con los portales del CRT y ha decidido canalizarlo productivamente.",
        "Esta app cuesta \$0. Consultar los portales manualmente cuesta entre 3 y 5 horas de tu vida. La matemática es sencilla.",
        "Dato sobre el desarrollador: analizó el tráfico de red de 15 portales diferentes para encontrar sus APIs y ahorrarles ese tiempo a extraños de internet. Raro, sí. Útil, también.",
        // Portales caídos
        "Hay 15 portales del CRT con error conocido. Esta app los documenta, los monitorea, y te dice exactamente cuál es el problema de cada uno.",
        "Error 403 significa que el portal bloquea el acceso. Es el equivalente digital de la señora de la ventanilla que pone el letrero de 'regrese en 20 minutos' a las 2:50pm.",
        "Algunos portales del CRT llevan meses caídos sin que nadie los arregle. Esta app los lista con fecha de defunción y un mensaje de condolencias técnicas.",
        // Datos absurdos
        "En el tiempo que esta app tarda en consultar todos los portales, una tortillería produce 7,200 tortillas. Dato completamente relevante.",
        "Sin esta app necesitarías resolver aproximadamente 480 CAPTCHAs manualmente. Con esta app resuelves los que no se pueden evitar.",
        "4 horas de consulta manual equivalen a 160 semáforos en rojo de 90 segundos en CDMX hora pico. Esta app te ahorra exactamente eso.",
        "Podrías leer el 5.6% del Quijote en el tiempo que tardarías en revisar los portales del CRT manualmente. Con esta app tienes tiempo para ese 5.6%.",
        // Varios
        "Esta app habla 19 idiomas. Los portales del CRT hablan solo español y a veces ni eso porque están caídos.",
        "Bienvenido a la única app mexicana de seguridad de identidad con frases sarcásticas incluidas sin costo adicional.",
        "Esta app no juzga si abriste esto por preocupación genuina o por curiosidad. Ambas razones son válidas.",
        "El CRT publicó los portales para proteger a los ciudadanos. Esta app publicó la forma de consultarlos todos de una vez para proteger el sanity de los ciudadanos.",
        "Consultar si alguien robó tu identidad no debería ser difícil. Esta app se aseguró de que no lo sea.",
        "Esta app fue hecha en México, para mexicanos, y para cualquiera en el mundo que quiera saber si tiene líneas telefónicas mexicanas a su nombre. El mundo es un lugar extraño.",
        "Los portales del CRT existen desde 2021. Esta app existe desde que alguien se hartó de revisarlos uno por uno.",
        "Sin costo. Sin publicidad. Sin trampa. Sin servidor propio. Con sarcasmo. Eso es todo lo que ofrecemos.",
        "Esta app es como tener un asistente que hace todos los trámites aburridos por ti, sin el costo de un asistente.",
        "Bienvenido. Hoy es un buen día para saber si alguien te robó la identidad. Todos los días son buenos días para eso.",
        "Esta app no requiere registro, no pide tu correo, no te manda notificaciones molestas, no guarda historial en la nube. Refrescante, ¿verdad?",
        "Listo para empezar. Las personitas de la app, el cifrado AES-256, los 19 idiomas disponibles, y el sarcasmo incorporado están todos listos.",
    )

    // ── 90 frases de donación ──────────────────────────────
    val donation = listOf(
        // Personitas
        "Consulta terminada. Las personitas de la oficina interna están archivando tus resultados en sus diminutos archiveros de metal. Si quieres que sigan teniendo trabajo, una donación siempre ayuda.",
        "Listo. Las personitas del departamento de resultados ya imprimieron tu reporte en papel del tamaño de una pestaña. El sindicato de personitas agradece cualquier contribución.",
        "Los pequeños empleados de la app trabajaron muy duro hoy. El más dedicado incluso hizo horas extra en su diminuta silla ergonómica. Una donación cubriría su café de mañana.",
        "Las personitas de TI viven en condominios del tamaño de un pixel. Los precios del pixel-real están por las nubes. Dona y ayúdalos a no mudarse a una resolución más baja.",
        "El jefe de las personitas acaba de revisar los resultados con su lupa del tamaño de una célula. Él no pide aumento, pero acepta donaciones.",
        "Las personitas del departamento legal revisaron que esta app no viola ninguna ley en sus diminutos libros jurídicos. El colegio de personitas abogadas cobra honorarios en criptomonedas fraccionadas.",
        "Consulta completada. Las personitas del área de control de calidad verificaron cada resultado dos veces. Son muy meticulosas para su tamaño.",
        "Las personitas que trabajan en el área de portales caídos llevan semanas intentando contactar a los administradores. Nadie les contesta. Una donación levanta el ánimo.",
        // Impresoras
        "Dato verificado: cada vez que alguien dona a esta app, una impresora en algún lugar del mundo deja de atascarse por exactamente 24 horas. La ciencia aún no puede explicarlo.",
        "Si donas ahora mismo, una impresora imprimirá perfectamente a la primera, sin pedir que cambies el cartucho que en realidad no está vacío. Es magia.",
        "Estudios no publicados sugieren que los donantes de esta app experimentan un 73% menos de problemas con impresoras en los siguientes 30 días.",
        "Con cada donación, una impresora corporativa deja de hacer ese ruido raro antes de atascarse. No sabemos cómo. No preguntamos. Solo funciona.",
        "Dona y en algún lugar del mundo una impresora imprimirá el documento correcto, en el tamaño correcto, sin girar la página 90 grados sin razón aparente.",
        "Se dice que los donantes de apps gratuitas tienen mejor karma con la tecnología. Las impresoras, el WiFi, las computadoras lentas... todo mejora.",
        // El amor de tu vida
        "Dato estadístico inventado pero inspirador: las personas generosas que donan a causas tecnológicas son un 40% más atractivas. Hoy puedes ser más atractivo Y haberte ahorrado 4 horas.",
        "Según una encuesta que acabamos de crear, el 8 de cada 10 personas prefieren a alguien que apoya apps gratuitas de código abierto. El amor de tu vida podría estar buscando exactamente eso.",
        "Donar a esta app no garantiza que encuentres el amor de tu vida. Pero no donar tampoco lo garantiza. Dado que el resultado es el mismo, mejor dona y al menos las personitas comen.",
        "Tu perfil de citas mejoraría notablemente si pudiera decir 'apoya apps de código abierto que protegen la identidad'. Una donación y eso es literalmente verdad.",
        "Las personas que donan a esta app duermen mejor, tienen mejor karma digital, y según fuentes que nos inventamos, son más felices en sus relaciones.",
        "Hoy te ahorraste 4 horas de consultas manuales. Con esas 4 horas podrías salir a conocer gente. Si conoces a alguien especial, una pequeña donación sería una forma muy original de agradecer.",
        // SAT / IMSS
        "Esta app no te va a mandar una carta intimidante como el SAT. Solo te pide una donación voluntaria. La diferencia es que nosotros preguntamos amablemente.",
        "El SAT cobra multas. El IMSS cobra cuotas. Nosotros no cobramos nada. Si te parece justo, una donación voluntaria es la forma más elegante de reconocerlo.",
        "Acabas de hacer en 15 minutos lo que sin esta app tomaría dos citas canceladas en el IMSS y una llamada al SAT que nadie contesta.",
        "La señora de la ventanilla que te manda a otra ventanilla no trabaja aquí. Todo funciona en una sola pantalla. Si eso vale algo para ti, ya sabes dónde donar.",
        "Para tramitar algo en el IMSS necesitas: CURP, comprobante de domicilio, dos fotografías, y paciencia infinita. Para donar aquí solo necesitas: voluntad.",
        "Imagina cobrar por cada portal del CRT que consultamos. Sería como el SAT pero con más captchas. En cambio, esto es gratis. Eso merece consideración.",
        // Castigo divino / Fantasmas
        "Acabas de escapar de uno de los 7 castigos modernos del purgatorio digital. Las ánimas que no tuvieron esta app te envidian desde el más allá.",
        "Los fantasmas de quienes intentaron revisar los 100+ portales manualmente aplauden desde donde estén. Una donación les daría algo de paz.",
        "En el mundo espiritual, cada consulta que esta app hace por ti libera el alma de alguien que pasó horas revisando portales antes de que existiera la app.",
        "Se dice que si donas a esta app, el fantasma del portal de VinculaTuLinea que da error 403 descansa en paz por 24 horas.",
        "Las ánimas de los portales caídos agradecen que alguien documente su existencia. Una donación ayuda a mantener ese registro espiritual-técnico.",
        "El universo tiene una forma de equilibrar las cosas. Tú usaste una app gratuita. El universo sugiere una donación para restaurar el balance cósmico.",
        // Cifrado
        "Tu CURP estuvo cifrado con AES-256-GCM durante toda la consulta. No lo vimos. No lo guardamos. No lo vendemos. Si eso te parece valioso, una donación lo demuestra.",
        "Esta app protegió tus datos con el mismo nivel de cifrado que usan los bancos, sin cobrarte comisión bancaria.",
        "Tu información se borrará automáticamente en 24 horas. El SAT guarda la tuya para siempre. Somos diferentes. Si aprecias esa diferencia, ya sabes.",
        "Tu CURP nunca salió de tu teléfono hacia nuestros servidores. Porque no tenemos servidores. Mantener esa filosofía cuesta cero infraestructura. Aunque una donación alegra el día.",
        // Datos absurdos
        "Acabas de ahorrar aproximadamente 4 horas de tu vida. Al salario mínimo eso equivale a \$139 pesos. Esta app te los ahorró gratis.",
        "En el tiempo que duró esta consulta, una tortillería produjo miles de tortillas. Tú produjiste certeza sobre tu identidad. Ambas cosas son valiosas.",
        "Esta app consultó portales que cubren aproximadamente 80+ operadoras telefónicas. Si hubieras ido uno por uno, estarías llegando al portal número 3 ahorita. En el sentido mexicano del ahorita.",
        // Desarrollador
        "Esta app es gratuita. El desarrollador come de todos modos, pero las donaciones hacen que el café de las madrugadas cuando arregla bugs valga más la pena.",
        "Código abierto. Sin publicidad. Sin suscripción. Solo una petición honesta: si te fue útil, una donación es la forma más directa de decirlo.",
        "El desarrollador de esta app analizó el tráfico de red de decenas de portales para encontrar sus APIs. Ese nivel de dedicación merece al menos un café.",
        "Esta app no te va a mandar notificaciones molestas pidiendo donaciones. Solo esta pantalla, una vez, al final de la consulta. Después, silencio total.",
        "Mantener código abierto, gratuito y funcional cuesta tiempo. El tiempo cuesta café. El café cuesta dinero. La cadena es clara.",
        "Si esta app te salvó de 4 horas de trámites digitales, considera que el desarrollador invirtió muchas más horas construyéndola.",
        // Miscelánea
        "Misión cumplida. Las personitas de la app ya guardaron los resultados en sus diminutos USB.",
        "Consulta completada. Si quieres que esta app siga existiendo y mejorando, una donación es la señal más clara que puedes dar.",
        "Esta app habla 19 idiomas. Tus resultados están guardados por 24 horas. La opción de donar sigue disponible. Todo en orden.",
        "Listo. Ahora sabes algo que la mayoría de la gente no sabe sobre su propia identidad telefónica. Ese conocimiento es gratuito. La donación también es opcional.",
        "Si esta app fuera un trámite del gobierno, habría 47 pasos, 3 citas previas, y un error al final. Agradece que no lo es.",
        "Esta app no tiene estrellitas que calificques para presionarte. Solo este mensaje honesto: si te fue útil, en ko-fi.com/sixseven8 siempre hay lugar para donar.",
        "Resultado listo. Las personitas del área de empaquetado ya pusieron el resultado en una cajita del tamaño de un átomo.",
        "Esta app no gana dinero por publicidad. No gana por datos. No gana por suscripciones. Las donaciones son literalmente la única forma de que el proyecto genere algo.",
        "Hoy consultamos decenas de portales por ti. Mañana podríamos agregar más. Depende de que el proyecto siga vivo.",
        "Si donas ahora, no pasa nada dramático. Solo la satisfacción de haber apoyado algo útil. Y las personitas bailando, pero eso no lo puedes ver.",
        "Esta app fue hecha en México, para proteger a la gente de fraude de identidad telefónica, de forma gratuita.",
        "Las personitas del departamento de finanzas informa que el balance de donaciones del mes podría mejorar. Solo informando.",
        "Consulta terminada. El universo registró que hiciste algo inteligente hoy. También sugiere, sin imponer, que una donación completaría el ciclo kármico.",
        "Esta app no tiene términos y condiciones de 47 páginas. Tiene esto: es gratis, protege tus datos, y si te fue útil, puedes donar. Tres oraciones. Fin.",
        "Listo. Ahora ve a vivir tu vida sabiendo que revisaste tus líneas. Y si sientes gratitud espontánea hacia una app que te ahorró horas, ya sabes dónde canalizarla.",
        "Esta fue tu consulta. Esperamos que los resultados sean tranquilizadores. Y considera donar de cualquier forma.",
        "Consulta completada. Esta app seguirá funcionando mientras los portales del CRT existan y el desarrollador recuerde renovar el dominio. Las donaciones ayudan con lo tercero.",
        "Si esta app te pareció útil, compártela. Si te pareció muy útil, compártela y dona. Si te pareció extraordinariamente útil, haz todo lo anterior.",
        "Las personitas del área de recursos humanos informan que la moral está alta pero el presupuesto de café está bajo.",
        "Esta app consultó portales de empresas que van desde Telcel OMV hasta empresas que probablemente nunca habías escuchado. Todo en unos minutos.",
        "Listo. Si tienes preguntas, el código fuente está en GitHub. Si tienes sugerencias, los Issues están abiertos. Si tienes gratitud, ko-fi.com/sixseven8 la recibe.",
        "Consulta terminada. Las personitas están apagando sus computadoras del tamaño de un grano de arroz. Son muy dedicadas para su tamaño.",
        "Cada vez que alguien dona, el desarrollador considera mejorar la app o simplemente tomar una siesta sabiendo que el proyecto importa. Las tres opciones son válidas.",
        "Esta app existe porque alguien se hartó de revisar portales uno por uno y decidió resolver el problema para todos.",
        "Esta app es de código abierto. Transparencia total. Si te da confianza, una donación es la mejor forma de demostrarlo.",
        "Consulta lista. Si los resultados te generaron más preguntas, el canal de Issues en GitHub es el lugar correcto.",
        "Las personitas del área legal aclaran que no hay obligación legal ni moral de donar. Las de finanzas no añaden nada pero ponen cara.",
        "Esta app seguirá siendo gratuita con o sin donaciones. Pero con donaciones el desarrollador puede comprar café de mejor calidad para arreglar bugs. La calidad del café impacta directamente la calidad del código.",
        "Las personitas del departamento de marketing querían poner un banner publicitario. Las de ética vetaron la propuesta. Las de finanzas sugirieron este mensaje como compromiso.",
        "Si en este momento estás pensando 'debería donar' pero luego vas a cerrar esto y olvidarlo, este es tu recordatorio de que el momento es ahora. Las personitas te miran.",
        "Consulta terminada. El nivel de sarcasmo de la app se mantiene estable. El nivel de donaciones podría mejorar. Solo uno de esos dos depende de ti.",
        "Esta pantalla es el único momento en que pedimos donación. Después, silencio absoluto. Aprovecha la oportunidad o no. Sin juicios.",
        "Las personitas de la app aceptarían un abrazo como forma de pago, pero la tecnología actual no lo permite. Las donaciones en ko-fi.com/sixseven8 son la segunda mejor opción.",
        "Dato final del día: revisaste tus líneas, protegiste tu identidad, ahorraste horas. Toda esa información: gratis. La donación: opcional.",
        "Esta pantalla aparece una vez por consulta. No más. Sin resentimientos. Las personitas son profesionales.",
        "El balance cósmico de haber usado una app gratuita puede restaurarse donando. O no. El cosmos es flexible. Las personitas, menos.",
        "Si esta app te ahorró tiempo, dinero en taxis para hacer el trámite en persona, o una crisis existencial a las 2am, cualquier donación es bienvenida.",
        "Las personitas del departamento de contabilidad llevan el registro de clicks en el botón de donar. Llevan mucho tiempo esperando que ese número suba.",
        "Fin de la consulta. Las personitas apagan las luces de la oficina interna y se van a sus hogares del tamaño de un pixel. Una donación garantiza que mañana regresen con energía.",
    )

    // ── 60 frases "llevas rato aquí" (3+ clicks en frases) ─
    val keepReading = listOf(
        // Meta-referencias
        "Llevas viendo frases un rato. La app fue diseñada para consultar líneas telefónicas, no para entretenimiento. Aunque entendemos que ambas cosas son válidas.",
        "Click número 3. Las personitas de la app se están mirando entre ellas con cara de '¿este usuario está bien?'",
        "Oye. Oye. Hay un botón grande que dice 'Consultar'. Ese era el plan original.",
        "Las personitas del departamento de análisis de comportamiento acaban de generar un reporte que dice 'usuario lleva mucho tiempo en la pantalla de frases'. El reporte tiene tres páginas. Todas dicen lo mismo.",
        "¿Sabías que esta app también consulta portales del CRT? Es su función principal. Por si acaso no habías notado.",
        "Llevas suficientes clicks como para que las personitas empiecen a preguntarse si deberían llamar a alguien. ¿Estás bien? En serio.",
        "La app fue creada para ahorrarte 4 horas de consultas manuales. Si llevas más de 4 horas viendo frases, técnicamente ya perdiste el beneficio. Reflexiona.",
        "Las personitas de recursos humanos están considerando contratar a alguien específicamente para generarte más frases. El presupuesto no alcanza. Dona y lo hacemos realidad.",
        "En el tiempo que llevas viendo frases, una tortillería produjo aproximadamente 500 tortillas. Dato relevante para dimensionar tu situación.",
        "El desarrollador imaginó que los usuarios abrirían la app, consultarían sus líneas, y seguirían con su día. No imaginó esto. Pero tampoco lo juzga.",
        // Papás, tíos, abuelos
        "Dile a tu papá que te preste su tarjeta de crédito y te ayude a donar. Estarán orgullosos de ver cómo te interesas por pobres ingenieros sin casa propia ni sueldo fijo.",
        "¿Tienes un tío que siempre dice que apoya a los emprendedores mexicanos? Este es el momento de ponerlo a prueba. El botón de Ko-fi está justo abajo.",
        "Tu abuela probablemente no sabe qué es Ko-fi pero sí sabe que ayudar a los demás es importante. Explícale el concepto. Fortalece vínculos y financia software.",
        "¿Tienes pareja? Muéstrale esta app. Dile que es tuya. Acepta el crédito. De paso dona para que la mentira tenga algo de verdad.",
        "Llama a tu mamá. No para pedirle que done, sino porque llevas mucho tiempo en el teléfono viendo frases y probablemente ella se pregunta qué estás haciendo.",
        "¿Tienes un primo que trabaja en tecnología y siempre habla de 'apoyar el ecosistema'? Este es su ecosistema. Mándale el link.",
        "Tu abuelo sobrevivió cosas mucho más difíciles que donar a una app. Si le explicas que esto protege identidades de fraude, probablemente entienda la importancia.",
        "Si en tu familia hay alguien que siempre dice 'yo apoyo a los jóvenes con talento', aquí hay un joven con talento que necesita apoyo. El talento está en GitHub.",
        "¿Tu jefe habla mucho de 'innovación mexicana'? Una app gratuita, de código abierto, que protege identidades. Pídele que done en nombre de la empresa.",
        "Veo que estás disfrutando la app. Dile a tu papá que te preste su tarjeta de crédito y te ayude a donar. Estará orgulloso de ver cómo te interesas por pobres ingenieros sin casa propia.",
        // Adicción a las frases
        "Llevamos la cuenta: llevas más de 5 clicks en este botón. Las personitas del departamento de estadísticas están actualizando sus gráficas con expresión preocupada.",
        "Esta pantalla se va a volver tu favorita, ¿verdad? Lo vemos venir. Las personitas ya están preparando contenido especial para usuarios frecuentes. Spoiler: es más sarcasmo.",
        "Si esta app fuera una persona, ya te habría pedido que se vieran menos seguido. No porque no le gustes, sino por salud emocional.",
        "Llevas tanto tiempo en esta pantalla que las personitas ya te conocen por nombre. No sabemos tu nombre, pero le pusieron uno. Te llaman 'El de las frases'.",
        "En el mundo de las apps, esto se llama 'engagement'. En el mundo real se llama 'procrastinación'. Ambas interpretaciones son válidas.",
        "Las personitas del área de entretenimiento están encantadas con tu visita extendida. Las del área de productividad están menos entusiastas. Hay tensión en la oficina interna.",
        "Si donas ahora, las personitas del área de contenido tienen presupuesto para escribir más frases. Si no donas, eventualmente verás las mismas frases repetidas.",
        "¿Sabes qué es más gracioso que estas frases? El hecho de que llevas rato leyéndolas en lugar de hacer la consulta para la que descargaste la app.",
        "Esta app tiene un límite no oficial de frases antes de que las personitas se cansen de generarlas. No sabemos cuál es ese límite. Tú tampoco. Es un experimento mutuo.",
        "Si en lugar de ver frases donaras, las personitas podrían contratar a un escritor de comedias para mejorar el sarcasmo. Todos saldríamos ganando.",
        // Preocupación simulada
        "En serio, ¿estás bien? Esta es una pregunta genuina. Si necesitas hablar de algo, esta app no tiene esa función. Pero el desarrollador tiene GitHub.",
        "Las personitas del departamento de bienestar emocional quieren saber si hay algo que puedan hacer por ti hoy, además de mostrarte frases. Probablemente la respuesta sea 'no', pero preguntamos.",
        "Si llevas mucho tiempo aquí porque no quieres enfrentar los resultados de tu consulta, entendemos. Pero eventualmente hay que consultar.",
        "Las personitas del área de salud mental sugieren que si usas el humor para evitar la ansiedad, está bien. Pero también consulta tus líneas.",
        "¿Estás procrastinando algo? Esta app no es terapia. Pero las frases son gratis y el botón de Ko-fi está disponible cuando estés listo.",
        "Oye. Todo va a estar bien. Lo de las líneas, lo de la consulta, lo de la vida en general. Anda, consulta.",
        "Las personitas de la app te envían un abrazo virtual del tamaño de un átomo. Fue dado con todo el corazón diminuto que tienen.",
        "Si estás aquí porque genuinamente disfrutas el sarcasmo y no tienes prisa, está perfectamente bien. Las personitas trabajan turnos. Siempre hay alguien.",
        // Personitas que ya se fueron a cenar
        "Las personitas del turno de día ya se fueron a cenar. Las del turno de noche acaban de llegar y llevan 5 minutos en el trabajo. Ya están confundidas por tu comportamiento.",
        "Son las personitas del turno de noche. No sabemos qué pasó aquí antes de que llegáramos, pero encontramos el contador de clicks en un número alarmante.",
        "Las personitas de la oficina interna ya terminaron su jornada. Dejaron las frases en modo automático. No hay nadie para atenderte. Solo el modo automático y tú.",
        "El jefe de las personitas pasó a revisar antes de irse. Vio el contador de clicks. Suspiró. Apagó la luz de su diminuta oficina. Se fue sin decir nada. Fue poético.",
        "Las personitas del turno nocturno encontraron la cafetera encendida, los escritorios con trabajo pendiente, y un usuario que lleva mucho tiempo en la pantalla de frases. Están procesando.",
        "Son las 2am en algún lugar del mundo. Las personitas del turno nocturno toman su café en tazas del tamaño de una semilla. Tú estás viendo frases. Todos haciendo lo suyo.",
        // Absurdas
        "Dato: si donas cada vez que lees una frase graciosa, para cuando llegues a esta habrías donado varias veces. La lógica es circular pero la intención es buena.",
        "Esta frase fue generada por las personitas del departamento de frases de emergencia, que se activa cuando el contador de clicks supera cierto umbral. Bienvenido al departamento de emergencias.",
        "Cada frase que lees consume aproximadamente 0.0000001 kilovatios de tu teléfono. Llevas suficientes frases para haber cargado una hormiga robot si existieran. Dato inútil pero preciso.",
        "Las frases de esta app fueron escritas por un ser humano real, no por IA. Puedes verificarlo en que algunas son más graciosas que otras. La IA hubiera sido más consistente. Más aburrida, pero más consistente.",
        "Esta es la frase número cincuenta de este bloque especial. Las personitas del departamento de contabilidad llevan la cuenta en una hoja de cálculo del tamaño de una célula.",
        // Donación con giro absurdo
        "Esta app se creó para ahorrarte horas de tiempo. Si vas a pasar tanto tiempo aquí probando frases, dona. Así también haces algo bueno por un desarrollador desempleado.",
        "Científicos no afiliados a ninguna institución real sugieren que donar a apps gratuitas aumenta la probabilidad de que el WiFi funcione bien por el resto del día.",
        "Hay una teoría no comprobada de que cada donación hace que en algún lugar del mundo un trámite del gobierno se complete en el primer intento. No podemos probarlo. Pero tampoco descartarlo.",
        "Si donas ahora, las personitas del departamento de celebraciones harán una pequeña fiesta. Tendrán pastelitos del tamaño de un grano de azúcar. Merece la pena.",
        "El 100% de las personas que han donado a esta app son, por definición, personas que han donado a esta app. Puedes unirte a ese selecto grupo ahora mismo.",
        "Las personitas del departamento de relaciones públicas quieren que sepas que donar no resuelve el cambio climático ni arregla los portales caídos. Pero hace que el desarrollador se sienta bien.",
        "Si llevas tanto tiempo que ya memorizaste alguna frase, ese es el nivel de compromiso que merece una donación.",
        "Las personitas del departamento jurídico aclaran que ninguna de estas frases constituye una obligación de donar. Las de finanzas no añaden nada pero ponen cara.",
        "Esta es probablemente una de las últimas frases del bloque especial. Las personitas están agotando su creatividad. Una donación financiaría un retiro creativo. En un spa del tamaño de una célula.",
        "Llegaste al final del bloque especial de frases. Las personitas te aplauden con sus diminutas manos. El sonido es inaudible para el oído humano. Ahora sí, ¿consultamos?",
    )

    // ── Función de selección aleatoria sin repetir seguidas ─
    private var lastWelcomeIndex = -1
    private var lastDonationIndex = -1
    private var lastKeepReadingIndex = -1

    fun getWelcome(): String {
        var idx: Int
        do { idx = welcome.indices.random() } while (idx == lastWelcomeIndex)
        lastWelcomeIndex = idx
        return welcome[idx]
    }

    fun getDonation(): String {
        var idx: Int
        do { idx = donation.indices.random() } while (idx == lastDonationIndex)
        lastDonationIndex = idx
        return donation[idx]
    }

    fun getKeepReading(): String {
        var idx: Int
        do { idx = keepReading.indices.random() } while (idx == lastKeepReadingIndex)
        lastKeepReadingIndex = idx
        return keepReading[idx]
    }
}
