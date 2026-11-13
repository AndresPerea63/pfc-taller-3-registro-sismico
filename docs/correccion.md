# Ejemplo de informe de corrección

Fundamentos de Programación Funcional y Concurrente.
Documento realizado por el docente Juan Francisco Díaz.

## 1. Argumentar la corrección de programas recursivos

Sea $f : A \to B$ una función, y $A$ un conjunto definido recursivamente
(recordar la definición de Matemáticas Discretas I), como por ejemplo los
naturales o las listas.

Sea $P_f$ un programa recursivo (lineal o en árbol) desarrollado en Scala (o en
cualquier lenguaje de programación) hecho para calcular $f$:

```scala
def Pf(a: A): B = { // Pf recibe a de tipo A, y devuelve f(a) de tipo B
  ...
}
```

¿Cómo argumentar que $P_f(a)$ siempre devuelve $f(a)$ como respuesta? Es decir,
¿cómo argumentar que $P_f$ es correcto con respecto a su especificación?

La respuesta es sencilla: demostrando el siguiente teorema.

```math
\forall a \in A : P_f(a) == f(a)
```

Cuando uno tiene que demostrar que algo se cumple para todos los elementos de
un conjunto definido recursivamente, es natural usar inducción estructural. En
términos prácticos, esto significa demostrar que:

- Para cada valor básico $a$ de $A$, se tiene que $P_f(a) == f(a)$.
- Para cada valor $a \in A$ construido recursivamente a partir de otro(s)
  valor(es) $a' \in A$, se tiene que
  $P_f(a') == f(a') \rightarrow P_f(a) == f(a)$. (Esta es la hipótesis de
  inducción).

### Ejemplo: factorial recursivo

Sea $f : \mathbb{N} \to \mathbb{N}$ la función que calcula el factorial de un
número natural, es decir, $f(n) = n!$. Y sea $P_f$ el siguiente programa en
Scala:

```scala
def Pf(n: Int): Int = { // Pf recibe n de tipo Int, y devuelve n! de tipo Int
  if (n == 0) 1 else n * Pf(n - 1)
}
```

Vamos a demostrar que $\forall n \in \mathbb{N} : P_f(n) == n!$

**Caso base:** $n = 0$

```math
P_f(0) \rightarrow \text{if } (0 == 0)\ 1 \text{ else } 0 \ast P_f(-1) \rightarrow 1
```

Por otro lado, $f(0) = 0! = 1$. Entonces $P_f(0) == f(0)$.

**Caso de inducción:** $n = k + 1$, $k \geq 0$. Hay que demostrar:
$P_f(k) == f(k) \rightarrow P_f(k + 1) == f(k + 1)$

```math
P_f(k+1) \rightarrow \text{if } (k+1 == 0)\ 1 \text{ else } (k+1) \ast P_f(k) \rightarrow (k+1) \ast P_f(k)
```

Usando la hipótesis de inducción (HI):

```math
\rightarrow (k+1) \ast k! = (k+1)!
```

Por lo tanto, $P_f(k + 1) == f(k + 1)$.

Concluimos por inducción que $\forall n \in \mathbb{N} : P_f(n) == n!$

### Ejemplo: el máximo de una lista

Sea $f : \text{List}[\mathbb{N}] \to \mathbb{N}$ la función que calcula el
máximo de una lista de enteros positivos, no vacía. Y sea $P_f$ el siguiente
programa en Scala:

```scala
def maxLin(l: List[Int]): Int = {
  if (l.tail.isEmpty) l.head
  else math.max(maxLin(l.tail), l.head)
}
```

Demostraremos que:

```math
\forall n \in \mathbb{N} \setminus \{0\} : P_f(\text{List}(a_1, a_2, \ldots, a_n)) == f(\text{List}(a_1, a_2, \ldots, a_n))
```

**Caso base:** $n = 1$

```math
P_f(\text{List}(a_1)) \rightarrow \text{if } \text{List}(a_1).\text{tail.isEmpty then } \text{List}(a_1).\text{head else } \ldots \rightarrow \text{List}(a_1).\text{head} \rightarrow a_1
```

Por otro lado, $f(\text{List}(a_1)) = a_1$. Entonces
$P_f(\text{List}(a_1)) == f(\text{List}(a_1))$.

**Caso de inducción:** $n = k + 1$, $k \geq 1$. Se debe demostrar:

```math
P_f(\text{List}(b_1, b_2, \ldots, b_k)) == f(\text{List}(b_1, b_2, \ldots, b_k)) \rightarrow P_f(\text{List}(a_1, a_2, \ldots, a_{k+1})) == f(\text{List}(a_1, a_2, \ldots, a_{k+1}))
```

Empecemos por calcular qué devuelve $P_f$ usando el modelo de sustitución:

```math
P_f(L) \rightarrow \text{if } L.\text{tail.isEmpty then } L.\text{head else math.max}(P_f(L.\text{tail}), L.\text{head})
```

```math
\rightarrow \text{math.max}(P_f(\text{List}(a_2, \ldots, a_{k+1})), a_1)
```

Sea $b = P_f(\text{List}(a_2, \ldots, a_{k+1}))$; por la hipótesis de
inducción, $b = f(\text{List}(a_2, \ldots, a_{k+1}))$. Hay dos posibilidades:

- Si $\text{math.max}(b, a_1) = b$, entonces $b \geq a_1$ y
 $b == f(\text{List}(a_1, a_2, \ldots, a_{k+1}))$.
- Si $\text{math.max}(b, a_1) = a_1$, entonces $a_1 \geq b$ y
 $a_1 == f(\text{List}(a_1, a_2, \ldots, a_{k+1}))$.

Por lo tanto, $P_f(L) == f(L)$.

Concluimos por inducción que:

```math
\forall n \in \mathbb{N} \setminus \{0\} : P_f(\text{List}(a_1, a_2, \ldots, a_n)) == f(\text{List}(a_1, a_2, \ldots, a_n))
```

## 2. Argumentar la corrección de programas iterativos

Para argumentar la corrección de programas iterativos, se debe formalizar cómo
es la iteración. Esto implica definir:

- Cómo se representa un estado de la iteración, $s$.
- Cuál es el estado inicial, $s_0$.
- Cuál es el estado final (o cómo se reconoce que un estado es final): $s_f$.
- Qué condición (o predicado) cumple todo estado: $\text{Inv}(s)$ (invariante
  de la iteración).
- El mecanismo para pasar de un estado al siguiente: $\text{transformar}(s)$.
  Si $s_i$ es el estado $i$, entonces $\text{transformar}(s_i) = s_{i+1}$.

Un programa iterativo tiene la siguiente forma:

```scala
def Pf(a: A): B = { // Pf recibe a de tipo A, y devuelve f(a) de tipo B
  def Pf_iter(s: Estado): B =
    if (esFinal(s)) respuesta(s) else Pf_iter(transformar(s))
  Pf_iter(s0)
}
```

Demostración de corrección:

- $\text{Inv}(s_0)$: el estado inicial cumple la condición invariante.
- Si $(s_i \neq s_f \land \text{Inv}(s_i)) \rightarrow \text{Inv}(\text{transformar}(s_i))$:
  el nuevo estado cumple la condición invariante si el estado anterior la
  cumplía.
- De lo anterior se concluye $\text{Inv}(s_f)$, es decir, el estado final
  cumple la condición invariante. Luego,
  $\text{Inv}(s_f) \rightarrow \text{respuesta}(s_f) == f(a)$.
- Finalmente, demostrar que siempre se llega al estado final $s_f$. Esto
  implica que
  $P_f(a) == \text{iter}(s_0) == \text{respuesta}(s_f) == f(a)$.

### Ejemplo: factorial iterativo

Considere el siguiente programa iterativo en Scala para calcular la función
factorial:

```scala
def Pf(n: Int): Int = { // Pf recibe n de tipo Int, y devuelve n! de tipo Int
  def Pf_iter(i: Int, n: Int, ac: Int): Int =
    if (i > n) ac else Pf_iter(i + 1, n, i * ac)
  Pf_iter(1, n, 1)
}
```

Este programa implementa el siguiente proceso iterativo:

- Un estado $s = (i, n, ac)$.
- El estado inicial es $s_0 = (1, n, 1)$.
- $(i, n, ac)$ es final si $i > n$, o lo que es lo mismo, si $i = n + 1$.
- La invariante de ciclo es
  $\text{Inv}(i, n, ac) \equiv i \leq n + 1 \land ac = (i-1)!$.
  La invariante de ciclo es una relación que SIEMPRE se cumple en el ciclo.
- $\text{transformar}((i, n, ac)) = (i+1, n, i \ast ac)$.

Ahora, demostramos los puntos mencionados:

**1.** $\text{Inv}(s_0)$: el estado inicial cumple la condición invariante.

```math
s_0 = (1, n, 1) \implies 1 \leq n + 1 \land 1 = 0!
```

**2.** La invariante se mantiene con la transformación de estados,
$(s_i \neq s_f \land \text{Inv}(s_i)) \rightarrow \text{Inv}(\text{transformar}(s_i))$:

1. Primer cambio, $i = i + 1$, lo que implica $ac = ((i+1) - 1)! = i!$.
2. Segundo cambio, $ac = i \ast ac$, entonces $ac = (i - 1)! \ast i = i!$.
3. Como se puede ver en ambos cambios indicados en la transformación, la
   invariante se mantiene.

**3.** $\text{Inv}(s_f) \rightarrow \text{respuesta}(s_f) == f(a)$

```math
(n + 1 \leq n + 1) \land ac = ((n+1)-1)! \rightarrow ac == n!
```

**4.** En cada paso, la componente $i$ del estado incrementa, acercándose a $n+1$.
Después de $n$ iteraciones, se alcanza $n+1$.

Esto implica que $P_f(n) == \text{iter}(1, n, 1) == n!$

### Ejemplo: el máximo de una lista

Se desea calcular el máximo de una lista de enteros positivos, no vacía. Sea
$f : \text{List}[\mathbb{N}] \to \mathbb{N}$ la función que calcula ese valor.
Y sea $P_f$ el siguiente programa en Scala:

```scala
def maxIt(l: List[Int]): Int = {
  def maxAux(max: Int, l: List[Int]): Int = {
    if (l.isEmpty) max
    else maxAux(math.max(max, l.head), l.tail)
  }
  maxAux(l.head, l.tail)
}
```

Este programa implementa el siguiente proceso iterativo:

- Un estado $s = (max, l)$ donde $l = \text{List}(a_i, a_{i+1}, \ldots, a_k)$
  es una cola de $L$.
- El estado inicial es
  $s_0 = (L.\text{head}, L.\text{tail}) = (a_1, \text{List}(a_2, \ldots, a_k))$.
- $s = (max, l)$ es final si $l$ es vacía.
- $\text{Inv}(max, l) \equiv l = \text{List}(a_i, a_{i+1}, \ldots, a_k) \land max = f(\text{List}(a_1, a_2, \ldots, a_{i-1}))$.
- $\text{transformar}((max, l)) = (nmax, l.\text{tail})$ donde $nmax = max$ si
  $max \geq l.\text{head}$, y $nmax = l.\text{head}$ si no.

Demostración de los puntos:

**1.** $\text{Inv}(s_0)$: el estado inicial cumple la condición invariante.

```math
s_0 = (a_1, \text{List}(a_2, \ldots, a_k)) \implies a_1 = f(\text{List}(a_1))
```

**2.** $(s_i \neq s_f \land \text{Inv}(s_i)) \rightarrow \text{Inv}(\text{transformar}(s_i))$

```math
\neg\, l.\text{isEmpty} \land l = \text{List}(a_i, a_{i+1}, \ldots, a_k) \land max = f(\text{List}(a_1, a_2, \ldots, a_{i-1}))
```

```math
\rightarrow l.\text{tail} = \text{List}(a_{i+1}, \ldots, a_k) \land nmax = f(\text{List}(a_1, \ldots, a_i))
```

**3.** $\text{Inv}(s_f) \rightarrow \text{respuesta}(s_f) == f(a)$

```math
\text{Inv}((max, \text{List}())) \rightarrow max = f(\text{List}(a_1, \ldots, a_k))
```

**4.** En cada paso, la lista $l$ se reduce, acercándose a ser vacía. Después de
$k$ iteraciones, $l = \text{List}()$.

Esto implica que $P_f(L) == \text{maxAux}(L.\text{head}, L.\text{tail}) == f(L)$

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

Si la magnitud inicial no supera el mínimo, el resultado es la lista vacía. Para magnitudes positivas y un mínimo no negativo, la secuencia termina incluyendo únicamente los valores que superan dicho mínimo.

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