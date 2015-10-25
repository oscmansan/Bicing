Para ejecutar el programa, des del directorio en el que se encuentra esta fichero, usamos el comando:

> java -jar Bicing.jar

Ademas, para poder modificar los parametros de la ejecucion se puden añadir los siguientes argumentos:

  "-nest n"  -> el problema tiene n estaciones (n = 25 por defecto)
  "-nbic n"  -> el problema tiene n bicicletas (n = 1250 por defecto)
  "-nf n"    -> el problema tiene n furgonetas (n = 20 por defecto)
  "-seed n"  -> se usa n como semilla (n = 1234 por defecto)

  "-dem E"   -> se usa una demanda equilibrada (valor por defecto)
  "-dem R"   -> se usa una demanda de hora punta

  "-nexp n"  -> indica el numero de experimentos a realizar (n = 10 por defecto)
  "-op3"     -> se usa el operador 3 (el operador 4 se usa por defecto)
  "-trivial" -> se usa la solucion inicial trivial (se usa la compleja por defecto)
  "-gratis"  -> no se tiene en cuenta el coste del transporte ni en el heuristico ni el las ganancias finales (por defecto se tiene en cuenta)

  "-alg hc"  -> se usa el algoritmo de Hill Climbing (valor por defecto)
  "-alg sa"  -> se usa el algoritmo de Simulated Annealing con sus parametros por defecto (iteraciones = 100000, stiter = 1000, k = 100000, lambda = 0.01)
  "-alg sa iteraciones stiter k lambda" -> se usa el algoritmo de Simulated Annealing con los parametros que se indican

  Ejemplos:

  > java -jar Bicing.jar -nest 27 -nexp 10 -trivial -seed 0 -alg sa 1000 100 5 0.001 -op3
  > java -jar Bicing.jar -alg hc -dem R -nf 40 -nest 45 -nbic 700
  > java -jar Bicing.jar -alg sa