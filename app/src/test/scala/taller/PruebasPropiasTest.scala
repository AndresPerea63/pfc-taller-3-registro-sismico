package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PruebasPropiasTest extends AnyFunSuite {

  val s = new RegistroSismico()
  import s._

  // Registro propio, distinto del que entrega el material.
  val mio: List[Sismo] = List(
    Sismo(Fecha(2026, 1), "Cauca", 4.0, 10),
    Sismo(Fecha(2026, 1), "Nariño", 6.5, 80),
    Sismo(Fecha(2026, 2), "Cauca", 4.5, 12),
    Sismo(Fecha(2026, 2), "Huila", 4.2, 20),
    Sismo(Fecha(2026, 3), "Nariño", 5.5, 90),
    Sismo(Fecha(2026, 3), "Cauca", 7.0, 15)
  )

  val vacio: List[Sismo] = List()

  // Punto 1 -------------------------------------------------------------------

  test("fuertes (propio): umbral 4.4 en el registro propio") {
    assert(fuertes(mio, 4.4) == List("Nariño", "Cauca", "Nariño", "Cauca"))
  }

  test("fuertes (propio): sobre el registro vacío no hay nada") {
    assert(fuertes(vacio, 1.0) == List())
  }

  test("fuertes (propio): un umbral negativo deja pasar todos") {
    assert(fuertes(mio, -1.0) == mio.map(_.region))
  }

  test("enRegion (propio): las magnitudes de Cauca en orden") {
    assert(enRegion(mio, "Cauca") == List(4.0, 4.5, 7.0))
  }

  test("enRegion (propio): una región con un solo sismo") {
    assert(enRegion(mio, "Huila") == List(4.2))
  }

  test("paresCercanos (propio): con dif 0.3 solo Cauca y Huila") {
    assert(paresCercanos(mio, 0.31) == List(("Cauca", "Huila")))
  }

  test("paresCercanos (propio): con dif 1.0 salen Cauca-Nariño y Cauca-Huila") {
    assert(paresCercanos(mio, 1.0) == List(("Cauca", "Nariño"), ("Cauca", "Huila")))
  }

  test("paresCercanos (propio): el registro vacío no tiene parejas") {
    assert(paresCercanos(vacio, 1.0) == List())
  }

  test("paresCercanos (propio): con una sola región no hay parejas") {
    val unaRegion = List(
      Sismo(Fecha(2026, 1), "Cauca", 4.0, 10),
      Sismo(Fecha(2026, 2), "Cauca", 4.1, 10)
    )
    assert(paresCercanos(unaRegion, 10.0) == List())
  }

  test("paresCercanos (propio): dif enorme da todas las parejas una vez") {
    assert(paresCercanos(mio, 100.0) ==
      List(("Cauca", "Nariño"), ("Cauca", "Huila"), ("Nariño", "Huila")))
  }

  // Punto 2 -------------------------------------------------------------------

  test("porRegion (propio): Cauca conserva sus tres sismos en orden") {
    assert(porRegion(mio)("Cauca").map(_.magnitud) == List(4.0, 4.5, 7.0))
  }

  test("porRegion (propio): las llaves son las tres regiones") {
    assert(porRegion(mio).keySet == Set("Cauca", "Nariño", "Huila"))
  }

  test("porRegion (propio): el registro vacío da el mapa vacío") {
    assert(porRegion(vacio) == Map())
  }

  test("conteoPorRegion (propio): tres, dos y uno") {
    assert(conteoPorRegion(mio) == Map("Cauca" -> 3, "Nariño" -> 2, "Huila" -> 1))
  }

  test("conteoPorRegion (propio): la suma de los conteos es el tamaño del registro") {
    assert(conteoPorRegion(mio).values.sum == mio.length)
  }

  test("magnitudMaxima (propio): la mayor de cada región") {
    assert(magnitudMaxima(mio) ==
      Map("Cauca" -> 7.0, "Nariño" -> 6.5, "Huila" -> 4.2))
  }

  test("magnitudMaxima (propio): una región con un solo sismo") {
    val uno = List(Sismo(Fecha(2026, 1), "Meta", 3.3, 5))
    assert(magnitudMaxima(uno) == Map("Meta" -> 3.3))
  }

  test("magnitudMaxima (propio): el registro vacío da el mapa vacío") {
    assert(magnitudMaxima(vacio) == Map())
  }

  test("magnitudMaxima (propio): no depende del orden del registro") {
    assert(magnitudMaxima(mio.reverse) == magnitudMaxima(mio))
  }

  test("conteoPorRegion (propio): tampoco depende del orden del registro") {
    assert(conteoPorRegion(mio.reverse) == conteoPorRegion(mio))
  }

  // Punto 4 -------------------------------------------------------------------

  test("replicas calcula correctamente la secuencia") {
    val resultado = replicas(8.0).take(5).toList
    assert(resultado == List(8.0, 6.4, 5.12, 4.1, 3.28))
  }

  test("replicas conserva la magnitud inicial") {
    assert(replicas(7.35).head == 7.35)
  }

  test("replicas permite obtener una cantidad limitada de elementos") {
    val resultado = replicas(2.75).take(150).toList
    assert(resultado.length == 150)
  }

  test("replicasSensibles se detiene cuando la magnitud deja de superar el minimo") {
    val resultado = replicasSensibles(9.0, 6.0)
    assert(resultado == List(9.0, 7.2))
  }

  test("replicasSensibles excluye los valores iguales al minimo") {
    val resultado = replicasSensibles(6.25, 5.0)
    assert(resultado == List(6.25))
  }

  test("replicas redondea cada nueva magnitud a dos decimales") {
    val resultado = replicas(3.0).take(4).toList
    assert(resultado == List(3.0, 2.4, 1.92, 1.54))
  }

  test("replicasSensibles devuelve una lista vacia cuando la magnitud inicial no supera el minimo") {
    val resultado = replicasSensibles(4.0, 4.0)
    assert(resultado.isEmpty)
  }

}