# Algoritmo Factorial con Recursión de Cola

## Definición del Algoritmo

```Scala
def factorial(n: Int): BigInt = {
  @annotation.tailrec
  def loop(x: Int, acumulador: BigInt): BigInt = {
    if (x <= 1) acumulador
    else loop(x - 1, acumulador * x)
  }
  loop(n, 1)
}
```

* La función `factorial` calcula el factorial de un número `n` utilizando **recursión de cola**.
* La función interna `loop` es la que hace la recursión:

  * Recibe dos parámetros:

    * `x`: el valor actual decreciente hasta llegar a 1.
    * `acumulador`: donde se guarda el resultado parcial en cada paso.
* El decorador `@annotation.tailrec` obliga a que la función sea optimizada como recursión de cola, es decir, **no se acumulan llamados en la pila**.

## Explicación paso a paso

### Caso base

```Scala
if (x <= 1) acumulador
```

Cuando `x` llega a `1`, la función retorna directamente el valor acumulado, evitando más llamadas.

### Caso recursivo

```Scala
loop(x - 1, acumulador * x)
```

En cada llamada:

* Se reduce el valor de `x` en 1.
* Se multiplica el acumulador por `x` y se pasa a la siguiente iteración.
* Como es recursión de cola, la llamada recursiva es la **última instrucción** en ejecutarse, lo que permite a Scala optimizar la pila.

---

## Llamados de pila en recursión de cola

Ejemplo:

```Scala
factorial(5)
```

### Paso 1: Llamada inicial

```Scala
loop(5, 1)
```

### Paso 2: Primera iteración

```Scala
loop(4, 5)   // acumulador = 1 * 5
```

### Paso 3: Segunda iteración

```Scala
loop(3, 20)  // acumulador = 5 * 4
```

### Paso 4: Tercera iteración

```Scala
loop(2, 60)  // acumulador = 20 * 3
```

### Paso 5: Cuarta iteración

```Scala
loop(1, 120) // acumulador = 60 * 2
```

### Paso 6: Caso base

```Scala
return 120
```

---

## Diferencia con recursión normal

* En **recursión normal** cada llamada queda en la pila esperando a que termine la siguiente, lo que puede causar desbordamiento si `n` es muy grande.
* En **recursión de cola**, el compilador transforma el proceso en un **bucle optimizado**, por lo que no se guarda cada llamada en la pila y el algoritmo puede ejecutarse para valores muy grandes sin problema.

---

## Ejemplo de uso

```Scala
val resultado = factorial(5)
println(resultado)  // 120
```

El resultado de `factorial(5)` es `120`.


## Diagrama de llamados de pila con recursión de cola

```mermaid
sequenceDiagram
    participant Main as factorial(5)
    participant L1 as loop(5, 1)
    participant L2 as loop(4, 5)
    participant L3 as loop(3, 20)
    participant L4 as loop(2, 60)
    participant L5 as loop(1, 120)

    Main->>L1: llamada inicial
    L1->>L2: tail call con (4, 5)
    L2->>L3: tail call con (3, 20)
    L3->>L4: tail call con (2, 60)
    L4->>L5: tail call con (1, 120)
    L5-->>Main: return 120
```


## Punto 1: consultas con expresiones `for`
 
### Idea general
 
Una expresión `for ... yield` recorre una colección y arma otra nueva. El
compilador la traduce a llamadas de `withFilter`, `map` y `flatMap`:
 
| En el `for` | Se traduce a |
|---|---|
| `x <- lista` (único generador) con `yield e` | `lista.map(x => e)` |
| `if condicion` | `withFilter(x => condicion)` |
| dos generadores `x <- a; y <- b` con `yield e` | `a.flatMap(x => b.map(y => e))` |
 
### `fuertes(r, umbral)`
 
```scala
for {
  s <- r
  if s.magnitud > umbral
} yield s.region
```
 
Se traduce a:
 
```scala
r.withFilter(s => s.magnitud > umbral).map(s => s.region)
```
 
Traza con `fuertes(registro, 5.0)`:
 
1. Se recorren los siete sismos en orden.
2. Pasan el filtro (magnitud > 5.0): #2 (5.1), #4 (6.0) y #6 (5.3).
3. `map` toma la región de cada uno: `List("Santander", "Choco", "Valle")`.
No se quitan repetidos y se conserva el orden, como pide el enunciado.
 
### `enRegion(r, region)`
 
```scala
for {
  s <- r
  if s.region == region
} yield s.magnitud
```
 
Se traduce a `r.withFilter(s => s.region == region).map(s => s.magnitud)`.
 
Traza con `enRegion(registro, "Valle")`: pasan los sismos #1, #3 y #6, y sus
magnitudes son `List(4.2, 3.8, 5.3)`. Si la región no existe, ningún sismo pasa
el filtro y el resultado es `List()`.
 
### `paresCercanos(r, dif)`
 
Esta función usa dos `for` y una lista auxiliar:
 
```scala
val regiones = r.map(_.region).distinct.zipWithIndex
```
 
`distinct` deja cada región una sola vez, en el orden en que aparece, y
`zipWithIndex` le pone a cada una su posición. Con el registro de ejemplo:
`List(("Valle", 0), ("Santander", 1), ("Choco", 2))`.
 
**`for` interno (función `hayCercanos`)**
 
```scala
(for {
  x <- enRegion(r, a)
  y <- enRegion(r, b)
  if Math.abs(x - y) <= dif
} yield 1).nonEmpty
```
 
Se traduce a:
 
```scala
enRegion(r, a).flatMap(x =>
  enRegion(r, b).withFilter(y => Math.abs(x - y) <= dif).map(y => 1)
).nonEmpty
```
 
Prueba todas las combinaciones de una magnitud de `a` con una de `b`. Si alguna
difiere a lo sumo `dif`, la lista tiene un elemento y `nonEmpty` es `true`.
 
**`for` externo**
 
```scala
for {
  (a, i) <- regiones
  (b, j) <- regiones
  if i < j
  if hayCercanos(a, b)
} yield (a, b)
```
 
Se traduce a:
 
```scala
regiones.flatMap { case (a, i) =>
  regiones.withFilter { case (b, j) => i < j && hayCercanos(a, b) }
          .map { case (b, j) => (a, b) }
}
```
 
La condición `i < j` es la que evita repeticiones: de cada pareja de regiones
solo se considera la que tiene primero la que apareció antes. Así, si salió
`("Valle", "Choco")` no puede salir `("Choco", "Valle")`, y una región nunca se
empareja consigo misma (porque `i < i` es falso).
 
Traza con `paresCercanos(registro, 0.6)`:
 
| Pareja (i < j) | Menor diferencia de magnitudes | ¿≤ 0.6? |
|---|---|---|
| Valle – Santander | 0.2 (5.3 y 5.1) | sí |
| Valle – Choco | 0.7 (5.3 y 6.0) | no |
| Santander – Choco | 0.9 (5.1 y 6.0) | no |
 
Resultado: `List(("Valle", "Santander"))`.
 
## Punto 2: agrupar con mapas
 
### `porRegion(r)`
 
```scala
r.groupBy(s => s.region)
```
 
`groupBy` crea un mapa cuya llave es la región y cuyo valor es la lista de
sismos de esa región, en el mismo orden en que estaban en el registro. Con el
ejemplo:
 
```text
"Valle"     -> List(sismo #1, #3, #6)
"Santander" -> List(sismo #2, #5)
"Choco"     -> List(sismo #4, #7)
```
 
Una región que no está en el registro no aparece como llave.
 
### `conteoPorRegion(r)`
 
```scala
porRegion(r).map { case (region, sismos) => (region, sismos.length) }
```
 
Recorre el mapa y reemplaza cada lista por su tamaño:
`Map("Valle" -> 3, "Santander" -> 2, "Choco" -> 2)`. Con el registro vacío,
`porRegion` da `Map()` y el resultado también es `Map()`.
 
### `magnitudMaxima(r)`
 
```scala
porRegion(r).map { case (region, sismos) => (region, sismos.map(s => s.magnitud).max) }
```
 
Para cada región se sacan las magnitudes de sus sismos y se toma la mayor. El
`max` nunca falla porque cada llave de `porRegion` tiene al menos un sismo.
Resultado con el ejemplo: `Map("Valle" -> 5.3, "Santander" -> 5.1, "Choco" -> 6.0)`.
 
```mermaid
flowchart LR
    R["Registro (List[Sismo])"] --> G["porRegion (groupBy)"]
    G --> C["conteoPorRegion (length)"]
    G --> M["magnitudMaxima (max)"]
```
 

