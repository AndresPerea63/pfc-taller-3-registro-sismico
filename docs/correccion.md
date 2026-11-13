# Informe de corrección

Taller 3: Programación funcional y concurrente. Consultas sobre un registro sísmico.

## Método

Cada función se compara con una especificación matemática. Para las funciones
que recorren una lista, que son las expresiones `for` del punto 1 y las
agrupaciones del punto 2, el argumento es **inducción estructural** sobre el
registro $R$: el caso base es $R = []$, y el paso de inducción supone que la
función es correcta para $R'$ y demuestra que lo es para $R = s :: R'$, donde
$s$ es el primer sismo.

Notación: $\text{reg}(s)$ y $\text{mag}(s)$ son la región y la magnitud del
sismo $s$; $\text{Reg}(R)$ es la lista de regiones de $R$ sin repetir, en orden
de aparición.

La secuencia perezosa del punto 4 es infinita, y no hay lista sobre la que hacer
inducción: cada elemento se calcula cuando se pide, así que la propiedad se
enuncia sobre los primeros $n$ elementos y se demuestra por inducción sobre $n$.

## Punto 1
 
### `fuertes`
 
**Especificación:**
$\text{fuertes}(R, u) = [\text{reg}(s) \mid s \in R,\ \text{mag}(s) > u]$, en el
mismo orden de $R$.
 
**Inducción estructural sobre $R$** (el `for` traducido es `map` sobre `filter`):
 
- **Caso base**, $R = []$: no hay sismos, el `for` devuelve $[]$, que es el
  valor de la especificación.
- **Paso**, $R = s :: R'$. Por hipótesis de inducción,
  $\text{fuertes}(R', u)$ es correcto. Si $\text{mag}(s) > u$, el sismo pasa el
  filtro y su región queda al frente: $\text{reg}(s) :: \text{fuertes}(R', u)$.
  Si no, se descarta y queda $\text{fuertes}(R', u)$. En ambos casos coincide con
  la especificación.
### `enRegion`
 
Es el mismo argumento con la condición $\text{reg}(s) = g$ y la proyección
$\text{mag}(s)$:
 
$$\text{enRegion}(R, g) = [\text{mag}(s) \mid s \in R,\ \text{reg}(s) = g]$$
 
### `paresCercanos`
 
Sea $\text{Reg}(R)$ la lista de regiones sin repetir, en orden de aparición, y
sea $M_g = \text{enRegion}(R, g)$. Definimos
 
$$\text{cerca}(a, b) \iff \exists\, x \in M_a,\ \exists\, y \in M_b : |x - y| \le d$$
 
**Especificación:**
$\text{paresCercanos}(R, d) = [(a, b) \mid a, b \in \text{Reg}(R),\ \text{pos}(a) < \text{pos}(b),\ \text{cerca}(a, b)]$.
 
**Argumento:**
 
1. *`hayCercanos(a, b)` equivale a `cerca(a, b)`.* El `for` interno genera un
   elemento por cada par $(x, y)$ con $|x-y| \le d$. La lista es no vacía si y
   solo si existe al menos un par así.
2. *Cada pareja sale una sola vez.* El `for` externo exige
   $\text{pos}(a) < \text{pos}(b)$. Para dos regiones distintas $a \ne b$ solo
   uno de los órdenes $(a,b)$ o $(b,a)$ cumple esa desigualdad, y para $a = b$
   no se cumple nunca. Por eso no hay repetidas ni parejas de una región
   consigo misma.
3. *Orden.* Los dos generadores recorren `regiones` en orden de aparición, así
   que las parejas salen ordenadas por la posición de la primera región y luego
   de la segunda.
## Punto 2
 
### `porRegion`
 
**Especificación:** para cada región $g \in \text{Reg}(R)$,
 
$$\text{porRegion}(R)(g) = [s \in R \mid \text{reg}(s) = g]$$
 
y las regiones que no están en $R$ no son llaves. `groupBy` construye
exactamente ese mapa: una llave por cada valor distinto de $\text{reg}(s)$ y,
dentro de cada llave, los sismos en su orden original.
 
### `conteoPorRegion` y `magnitudMaxima`
 
Ambas transforman el valor de cada llave de `porRegion`:
 
$$\text{conteoPorRegion}(R)(g) = \left|\{s \in R \mid \text{reg}(s) = g\}\right|$$
 
$$\text{magnitudMaxima}(R)(g) = \max\{\text{mag}(s) \mid s \in R,\ \text{reg}(s) = g\}$$
 
El máximo está bien definido porque todo $g$ que es llave tiene al menos un
sismo (si no hubiera sismos de $g$, $g$ no sería llave). Con el registro vacío
ambos mapas quedan vacíos.
 
### El resultado no depende del orden de recorrido
 
Sea $\pi(R)$ cualquier reordenamiento de $R$. Se cumple:
 
$$\text{conteoPorRegion}(\pi(R)) = \text{conteoPorRegion}(R)
\qquad
\text{magnitudMaxima}(\pi(R)) = \text{magnitudMaxima}(R)$$
 
**Razón:** los dos valores se definen con conjuntos (multiconjuntos) de sismos
de cada región, y estos no cambian al reordenar. El tamaño de un multiconjunto
no depende del orden, y el máximo es una operación conmutativa y asociativa:
$\max(a, b) = \max(b, a)$ y $\max(\max(a,b),c) = \max(a,\max(b,c))$. Por eso
cualquier orden de recorrido da el mismo mapa.
 
(La lista de sismos dentro de `porRegion` sí conserva el orden del registro, y
por eso `porRegion` no cumple esta propiedad como igualdad de listas; solo la
cumplen los resultados de los dos resúmenes.)
 



## Punto 3

Sea $\text{Reg}(R)$ el conjunto de regiones que aparecen en $R$, y sean

$$C_R(g) = \left|\{s \in R \mid \text{reg}(s) = g\}\right|, \qquad M_R(g) = \max\{\text{mag}(s) \mid s \in R,\ \text{reg}(s) = g\}$$

el conteo y la magnitud máxima de la región $g$. Del punto 2 se sabe que
$\text{conteoPorRegion}(R)(g) = C_R(g)$ y $\text{magnitudMaxima}(R)(g) = M_R(g)$
para todo $g \in \text{Reg}(R)$, y que esos mapas no tienen otras llaves.
Además $g \in \text{Reg}(R) \iff C_R(g) \geq 1$, de modo que $M_R(g)$ está bien
definido exactamente en $\text{Reg}(R)$.

### `conteoSeguro`

**Especificación:** para todo $g$ (conocido o no),
$\text{conteoSeguro}(R)(g) = C_R(g)$, donde $C_R(g) = 0$ si $g \notin \text{Reg}(R)$.

**Argumento:** hay dos casos.

- $g \in \text{Reg}(R)$: la llave está en `conteoPorRegion(R)`, y
  `withDefaultValue` no cambia los valores de las llaves presentes. El resultado
  es $C_R(g)$.
- $g \notin \text{Reg}(R)$: ningún sismo de $R$ tiene región $g$, así que
  $C_R(g) = |\emptyset| = 0$. La llave no está en el mapa y `apply` devuelve el
  valor por defecto, $0$. Coincide, y no se lanza excepción.

### `maximaDe`

**Especificación:**
$\text{maximaDe}(R, g) = \text{Some}(M_R(g))$ si $g \in \text{Reg}(R)$, y
$\text{None}$ si no.

**Argumento:** `get` devuelve `Some(v)` si y solo si la llave está en el mapa, y
`None` si no. Las llaves de `magnitudMaxima(R)` son exactamente $\text{Reg}(R)$,
y el valor de la llave $g$ es $M_R(g)$. Por lo tanto el resultado es
$\text{Some}(M_R(g))$ cuando $g \in \text{Reg}(R)$ y $\text{None}$ cuando no hay
sismos de $g$.

### `regionMasActiva`

**Especificación:** $\text{regionMasActiva}(R) = \text{None}$ si $R = []$. Si no,
$\text{Some}(g^\ast)$ con $g^\ast \in \text{Reg}(R)$ tal que

$$C_R(g^\ast) = \max_{g \in \text{Reg}(R)} C_R(g) \quad \land \quad g^\ast = \min\{g \in \text{Reg}(R) \mid C_R(g) = C_R(g^\ast)\}$$

donde $\min$ es el orden alfabético de los nombres.

**Argumento:** sea $k(g) = (-C_R(g),\ g)$ la clave que usa `minByOption`, y sea
$\prec$ el orden lexicográfico sobre pares, es decir

$$(a, g) \prec (b, h) \iff a < b \ \lor\ (a = b \land g < h)$$

1. $\prec$ es un orden total, porque lo son $<$ sobre los enteros y $<$ sobre los
   nombres. Las claves de regiones distintas son distintas (difieren en el
   segundo componente), así que sobre el conjunto finito
   $\{k(g) \mid g \in \text{Reg}(R)\}$ el mínimo existe y es único si el conjunto
   no es vacío.
2. Sea $g^\ast$ la región cuya clave es la mínima. Para toda otra región $g$,
   $k(g^\ast) \prec k(g)$, que significa
   $-C_R(g^\ast) < -C_R(g)$, o bien $-C_R(g^\ast) = -C_R(g)$ y $g^\ast < g$.
   Lo primero es $C_R(g^\ast) > C_R(g)$; lo segundo, un empate en el conteo que se
   rompe a favor del menor nombre. Así $g^\ast$ tiene el conteo máximo y, entre
   las de conteo máximo, el menor nombre.
3. Si $R = []$, $\text{Reg}(R) = \emptyset$, el mapa es vacío y `minByOption`
   devuelve `None`. Si $R \neq []$, hay al menos una región y devuelve
   `Some(g*)`; el `.map` solo extrae el nombre.

### El resultado no depende del orden de recorrido

Los conteos $C_R(g)$ no cambian si se reordena $R$ (el tamaño de un
multiconjunto no depende del orden). Y el mínimo de un conjunto finito bajo un
orden total no depende del orden en que se enumeren sus elementos: $\min$ es
conmutativo y asociativo. Por lo tanto, para todo reordenamiento $\pi(R)$,

$$\text{regionMasActiva}(\pi(R)) = \text{regionMasActiva}(R)$$

y lo mismo vale para $\text{conteoSeguro}$ y $\text{maximaDe}$, que dependen solo
de $C_R$ y de $M_R$. El desempate alfabético es lo que hace que no importe cuál
región se encuentre primero en el registro.

Observación: "orden alfabético" es el orden natural de `String` en Scala, que
compara por código de carácter. Con nombres que empiezan con mayúscula, como los
del registro, coincide con el alfabético; las letras con tilde van después de la
`z`.

## Punto 4

### `replicas`

**Especificación:**

Sea $r(x)$ la función de redondeo a dos decimales:

$$
r(x)=\frac{\operatorname{round}(100x)}{100}
$$

La secuencia de réplicas se define mediante:

$$
x_0=m
$$

$$
x_{n+1}=r(0.8x_n)
$$

Por tanto:

$$
\text{replicas}(m)=[x_0,x_1,x_2,x_3,\ldots]
$$

**Argumento de corrección:**

La implementación utiliza:

```scala
magnitud #:: replicas(redondear(magnitud * 0.8))
```

1. **Caso inicial.** El operador `#::` coloca la magnitud recibida como primer elemento de la secuencia. Por tanto, el primer elemento siempre es $x_0=m$.

2. **Paso recursivo.** La cola de la secuencia se obtiene llamando nuevamente a `replicas`, pero con la magnitud actual multiplicada por $0.8$ y redondeada. Así, cada nuevo elemento cumple la relación $x_{n+1}=r(0.8x_n)$.

3. **Evaluación perezosa.** Como se utiliza `LazyList`, los elementos siguientes no se calculan hasta que se necesitan. La secuencia puede ser infinita sin construir todos sus elementos en memoria. Por ejemplo, `take(4)` obtiene únicamente los primeros cuatro elementos.

Por inducción sobre la posición de los elementos, cada elemento generado corresponde a la especificación de la secuencia.

### `replicasSensibles`

**Especificación:**

La función devuelve la parte inicial de `replicas(m)` cuyos elementos superan estrictamente el mínimo $u$:

$$
\text{replicasSensibles}(m,u)
=
[x_n \mid x_n \in \text{replicas}(m),\ x_n>u]
$$

**Argumento de corrección:**

La implementación utiliza:

```scala
replicas(magnitud).takeWhile(_ > minimo).toList
```

1. **Filtrado.** `takeWhile` conserva los elementos consecutivos que cumplen la condición $x_n>u$.

2. **Exclusión del límite.** Cuando aparece un elemento menor o igual que $u$, la secuencia se detiene. Por tanto, ningún elemento igual al mínimo queda incluido.

3. **Conversión a lista.** `toList` convierte los elementos seleccionados en una lista ordinaria, conservando su orden.

Si la magnitud inicial no supera el mínimo, el resultado es la lista vacía. Si la magnitud inicial supera el mínimo, el resultado contiene únicamente los valores consecutivos que lo superan. La terminación de esta función depende del mínimo: se garantiza para un mínimo mayor o igual a 0.02 (ver la sección de terminación del punto 5), porque el redondeo a dos decimales hace que la secuencia se estanque en 0.02 y no baje más.

### Evaluación perezosa y terminación

La función `replicas` produce una secuencia infinita, pero esto no implica que todos sus elementos deban calcularse.

La evaluación perezosa permite solicitar solamente la cantidad necesaria de elementos. Por ejemplo:

$$
\text{replicas}(5.0).take(4)
=
[5.0,4.0,3.2,2.56]
$$

En `replicasSensibles`, `takeWhile` permite detener el recorrido cuando se encuentra el primer valor que no supera el mínimo, evitando recorrer innecesariamente el resto de la secuencia.

### Pruebas propias

Se realizaron siete pruebas unitarias para verificar:

1. Que `replicas` genere correctamente los valores sucesivos.
2. Que conserve la magnitud inicial sin modificarla.
3. Que permita obtener una cantidad limitada de elementos de la secuencia infinita.
4. Que `replicasSensibles` se detenga cuando los valores dejan de superar el mínimo.
5. Que excluya los valores iguales al mínimo.
6. Que cada nueva magnitud se redondee a dos decimales.
7. Que devuelva una lista vacía cuando la magnitud inicial no supera el mínimo.


## Punto 5

### `informe`

**Especificación:** para todo $g \in \text{Reg}(R)$,

$$\text{informe}(R, u)(g) = \text{Resumen}\big(C_R(g),\ M_R(g),\ \left|\text{replicasSensibles}(M_R(g), u)\right|\big)$$

y $\text{informe}(R, u)$ no tiene más llaves que $\text{Reg}(R)$.

**Argumento:** la función transforma el mapa `porRegion(R)`, que por el punto 2
tiene exactamente las llaves $\text{Reg}(R)$ y, en la llave $g$, la lista
$S_g = [s \in R \mid \text{reg}(s) = g]$ (no vacía). `Map.map` con una función que
conserva la llave produce un mapa con las mismas llaves. En la llave $g$:

- `sismos.length` es $|S_g| = C_R(g)$.
- `sismos.map(s => s.magnitud).max` es $\max\{\text{mag}(s) \mid s \in S_g\} = M_R(g)$.
  El `max` está definido porque $S_g \neq []$.
- `replicasSensibles(maxima, minimo).length` es
  $|\text{replicasSensibles}(M_R(g), u)|$, por el argumento del punto 4 con
  $m = M_R(g)$.

Por lo tanto el valor en $g$ es el `Resumen` de la especificación. Con
$R = []$ no hay llaves y el resultado es `Map()`.

**Consistencia con los puntos anteriores.** Como la primera componente es
$C_R(g)$ y la segunda $M_R(g)$, se cumple que
$\text{informe}(R, u)$ restringido a esas componentes es `conteoPorRegion(R)` y
`magnitudMaxima(R)`, respectivamente. Las pruebas lo verifican.

**Se usa el sismo más fuerte.** La tercera componente depende de $M_R(g)$, el
máximo de la región, y no del primer sismo que se encuentre. Si cambia el sismo
más fuerte cambia la cantidad de réplicas, pero no si cambia el orden.

### El resultado no depende del orden de recorrido

Cada componente de $\text{informe}(R, u)(g)$ es función de $C_R(g)$, $M_R(g)$ y
$u$. Los dos primeros no dependen del orden (punto 2), y la tercera componente
es una función de $M_R(g)$ y $u$ únicamente. Para cualquier reordenamiento
$\pi(R)$, entonces,

$$\text{informe}(\pi(R), u) = \text{informe}(R, u)$$

como igualdad de mapas (mismas llaves y mismos valores). Lo que sí cambia con el
orden es el orden de iteración del mapa, que no hace parte de su igualdad.

### Terminación de `replicasSensibles`

El punto 5 usa `replicasSensibles`, así que su resultado es finito solo si
`takeWhile(_ > minimo)` encuentra un elemento que lo detenga. Sea
$x_0 = m$, $x_{n+1} = r(0.8\,x_n)$ con $r(x) = \text{round}(100x)/100$.

- Mientras $x_n > 0.025$ se cumple $r(0.8\,x_n) \leq 0.8\,x_n + 0.005 < x_n$:
  la secuencia decrece estrictamente. Desde $x_1$ los valores son múltiplos de
  $0.01$, así que el decrecimiento es de al menos $0.01$ por paso y se llega a
  $x_n \leq 0.025$, es decir $x_n \leq 0.02$, en una cantidad finita de pasos.
- Para $u \geq 0.02$ existe entonces un $n$ con $x_n \leq u$, y `takeWhile` se
  detiene. El resultado es finito.
- Para $u < 0.02$ (por ejemplo $u = 0$) no se garantiza la terminación:
  $r(0.8 \cdot 0.02) = 0.02$ y $r(0.8 \cdot 0.01) = 0.01$ son puntos fijos del
  redondeo, así que $x_n$ se estanca en $0.02$ o en $0.01$. Si se estanca en un
  valor mayor que $u$, `takeWhile(_ > u)` nunca encuentra un elemento que lo
  detenga. Por ejemplo, desde $m = 5.0$ la secuencia se estanca en $0.02$, y con
  $u = 0.019$ el programa no termina. El enunciado fija la fórmula de redondeo,
  por lo que `informe` se debe usar con un mínimo $u \geq 0.02$, que es el caso
  de las magnitudes sísmicas con sentido físico.
