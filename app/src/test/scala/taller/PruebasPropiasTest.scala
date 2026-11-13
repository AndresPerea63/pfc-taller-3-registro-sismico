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

  // Punto 3 -------------------------------------------------------------------

  test("conteoSeguro (propio): una región conocida responde su conteo") {
    assert(conteoSeguro(mio)("Cauca") == 3)
    assert(conteoSeguro(mio)("Huila") == 1)
  }

  test("conteoSeguro (propio): una región desconocida responde cero y no lanza excepción") {
    assert(conteoSeguro(mio)("Putumayo") == 0)
  }

  test("conteoSeguro (propio): el valor por defecto no agrega llaves al mapa") {
    val m = conteoSeguro(mio)
    assert(m("Putumayo") == 0)
    assert(m.keySet == Set("Cauca", "Nariño", "Huila"))
    assert(m.size == 3)
  }

  test("conteoSeguro (propio): coincide con conteoPorRegion en las regiones del registro") {
    assert(conteoSeguro(mio).toMap == conteoPorRegion(mio))
  }

  test("conteoSeguro (propio): una región consultada con otra grafía no existe") {
    assert(conteoSeguro(mio)("cauca") == 0)
  }

  test("maximaDe (propio): la máxima de Cauca es 7.0") {
    assert(maximaDe(mio, "Cauca") == Some(7.0))
  }

  test("maximaDe (propio): la máxima de Nariño es 6.5") {
    assert(maximaDe(mio, "Nariño") == Some(6.5))
  }

  test("maximaDe (propio): una región con un solo sismo devuelve ese sismo") {
    assert(maximaDe(mio, "Huila") == Some(4.2))
  }

  test("maximaDe (propio): una región ausente da None") {
    assert(maximaDe(mio, "Meta") == None)
  }

  test("maximaDe (propio): sobre el registro vacío da None") {
    assert(maximaDe(vacio, "Cauca") == None)
  }

  test("regionMasActiva (propio): Cauca, con tres sismos") {
    assert(regionMasActiva(mio) == Some("Cauca"))
  }

  test("regionMasActiva (propio): la cantidad pesa más que el orden alfabético") {
    val nariñoGana = List(
      Sismo(Fecha(2026, 1), "Nariño", 4.0, 10),
      Sismo(Fecha(2026, 2), "Nariño", 4.1, 10),
      Sismo(Fecha(2026, 3), "Nariño", 4.2, 10),
      Sismo(Fecha(2026, 4), "Cauca", 5.0, 10),
      Sismo(Fecha(2026, 5), "Cauca", 5.1, 10)
    )
    assert(regionMasActiva(nariñoGana) == Some("Nariño"))
  }

  test("regionMasActiva (propio): empate entre dos, gana la primera alfabéticamente") {
    val empate = List(
      Sismo(Fecha(2026, 1), "Zulia", 4.0, 10),
      Sismo(Fecha(2026, 2), "Zulia", 4.1, 10),
      Sismo(Fecha(2026, 3), "Arauca", 4.2, 10),
      Sismo(Fecha(2026, 4), "Arauca", 4.3, 10),
      Sismo(Fecha(2026, 5), "Meta", 4.4, 10)
    )
    assert(regionMasActiva(empate) == Some("Arauca"))
  }

  test("regionMasActiva (propio): empate de tres con un sismo cada una") {
    val tres = List(
      Sismo(Fecha(2026, 1), "Nariño", 4.0, 10),
      Sismo(Fecha(2026, 2), "Huila", 4.1, 10),
      Sismo(Fecha(2026, 3), "Cauca", 4.2, 10)
    )
    assert(regionMasActiva(tres) == Some("Cauca"))
  }

  test("regionMasActiva (propio): una sola región") {
    val una = List(Sismo(Fecha(2026, 1), "Meta", 3.0, 10))
    assert(regionMasActiva(una) == Some("Meta"))
  }

  test("regionMasActiva (propio): no depende del orden del registro") {
    assert(regionMasActiva(mio.reverse) == regionMasActiva(mio))
  }

  // Punto 5 -------------------------------------------------------------------

  test("informe (propio): el resumen de las tres regiones del registro propio") {
    assert(informe(mio, 3.0) ==
      Map("Cauca" -> Resumen(3, 7.0, 4),
          "Nariño" -> Resumen(2, 6.5, 4),
          "Huila" -> Resumen(1, 4.2, 2)))
  }

  test("informe (propio): un mínimo mayor que la máxima da cero réplicas") {
    assert(informe(mio, 8.0) ==
      Map("Cauca" -> Resumen(3, 7.0, 0),
          "Nariño" -> Resumen(2, 6.5, 0),
          "Huila" -> Resumen(1, 4.2, 0)))
  }

  test("informe (propio): una región con un solo sismo") {
    val uno = List(Sismo(Fecha(2026, 1), "Meta", 5.0, 10))
    assert(informe(uno, 3.0) == Map("Meta" -> Resumen(1, 5.0, 3)))
  }

  test("informe (propio): las llaves son las mismas de porRegion") {
    assert(informe(mio, 3.0).keySet == porRegion(mio).keySet)
  }

  test("informe (propio): las réplicas salen del sismo más fuerte, no del primero") {
    // En Cauca el primer sismo es de 4.0 (2 réplicas sobre 3.0) pero el más fuerte es 7.0.
    assert(informe(mio, 3.0)("Cauca").replicas == replicasSensibles(7.0, 3.0).length)
    assert(informe(mio, 3.0)("Cauca").replicas != replicasSensibles(4.0, 3.0).length)
  }

  test("informe (propio): no depende del orden del registro") {
    assert(informe(mio.reverse, 3.0) == informe(mio, 3.0))
  }

}