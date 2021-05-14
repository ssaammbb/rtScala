package sphere

import vector._

case class Sphere(center: Vector, radius: Double, ambient: Vector, diffuse: Vector, specular: Vector, lum: Int, roughness: Double) {
  def intersect(origin: Vector, direction: Vector): Double = {
    val liMs = origin - this.center
    val b = (liMs % direction) * 2
    val c = (liMs % liMs) - (this.radius * this.radius)
    val d = b * b - 4 * c

    if (d < 0) {
      return Double.NaN
    }
    val sqd = math.sqrt(d)

    val distance = (-b - sqd) / (2.0)
    if (distance > 0) {
      return distance
    }
    val distance2 = (-b + sqd) / (2.0)
    if (distance2 > 0) {
      return distance2
    }
    Double.NaN
  }
}
