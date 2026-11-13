package taller

/**
 * Taller 3 — consultas sobre un registro sísmico con expresiones for, mapas y
 * secuencias perezosas.
 *
 * Complete cada función. Las firmas y los tipos no se modifican.
 */
class RegistroSismico {

  type Registro = List[Sismo]

  // Punto 1: consultas con expresiones for --------------------------------------

/** Las regiones de los sismos que superan el umbral, en orden y con repetidos. */
  def fuertes(r: Registro, umbral: Double): List[String] =
    for {
      s <- r                    // recorre cada sismo
      if s.magnitud > umbral    // se queda solo con los que superan el umbral
    } yield s.region            // y de cada uno devuelve su región

  /** Las magnitudes registradas en una región. */
  def enRegion(r: Registro, region: String): List[Double] =
    for {
      s <- r                    // recorre cada sismo
      if s.region == region     // se queda solo con los de esa región
    } yield s.magnitud          // y de cada uno devuelve su magnitud

  /** Parejas de regiones distintas con algún sismo de magnitudes cercanas. */
  def paresCercanos(r: Registro, dif: Double): List[(String, String)] = {
    // Regiones sin repetir, en el orden en que aparecen en el registro,
    // cada una con su posición (0, 1, 2, ...).
    val regiones = r.map(_.region).distinct.zipWithIndex

    // Dice si hay un sismo de la región a y uno de la región b con magnitudes
    // que difieren a lo sumo dif.
    def hayCercanos(a: String, b: String): Boolean =
      (for {
        x <- enRegion(r, a)
        y <- enRegion(r, b)
        if Math.abs(x - y) <= dif
      } yield 1).nonEmpty

    for {
      (a, i) <- regiones
      (b, j) <- regiones
      if i < j                  // a aparece antes que b: la pareja sale una sola vez
      if hayCercanos(a, b)
    } yield (a, b)
  }

  // Punto 2: agrupar con mapas --------------------------------------------------

/** Agrupa los sismos por región: región -> sus sismos. */
  def porRegion(r: Registro): Map[String, List[Sismo]] =
    r.groupBy(s => s.region)

  /** Cuántos sismos hay en cada región. */
  def conteoPorRegion(r: Registro): Map[String, Int] =
    porRegion(r).map { case (region, sismos) => (region, sismos.length) }

  /** La mayor magnitud de cada región. */
  def magnitudMaxima(r: Registro): Map[String, Double] =
    porRegion(r).map { case (region, sismos) => (region, sismos.map(s => s.magnitud).max) }

  // Punto 3: consultas seguras --------------------------------------------------

  /** Un mapa que responde cero por las regiones que no están en el registro. */
  def conteoSeguro(r: Registro): Map[String, Int] = ???

  /** La magnitud máxima de una región, o vacío si no hay registros suyos. */
  def maximaDe(r: Registro, region: String): Option[Double] = ???

  /** La región con más sismos; en empate, la primera en orden alfabético. */
  def regionMasActiva(r: Registro): Option[String] = ???

  // Punto 4: réplicas perezosas -------------------------------------------------

  def redondear(x: Double): Double = Math.round(x * 100.0) / 100.0

  /** La secuencia infinita que empieza en la magnitud y multiplica por 0.8. */
  def replicas(magnitud: Double): LazyList[Double] =
    magnitud #:: replicas(redondear(magnitud * 0.8))

  /** Las réplicas mientras superen el mínimo, incluida la primera. */
  def replicasSensibles(magnitud: Double, minimo: Double): List[Double] =
    replicas(magnitud).takeWhile(_ > minimo).toList

  // Punto 5: informe combinado --------------------------------------------------

  /** Conteo, magnitud máxima y réplicas esperadas del sismo más fuerte, por región. */
  def informe(r: Registro, minimo: Double): Map[String, Resumen] = ???
}
