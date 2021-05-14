package vector

case class Vector(x: Double, y: Double, z: Double) {
  def +(v: Vector) = Vector(x + v.x, y + v.y, z + v.z)

  def -(v: Vector) = Vector(x - v.x, y - v.y, z - v.z)

  def *(v: Vector) = Vector(x * v.x, y * v.y, z * v.z)

  def /(v: Vector) = Vector(x / v.x, y / v.y, z / v.z)

  def %(v: Vector): Double = x * v.x + y * v.y + z * v.z

  def length(): Double = math.sqrt(this % this)

  def unit():Vector = this * Vector(1 / this.length, 1 / this.length, 1 / this.length)
  def reflect(v: Vector): Vector = {
    val v1 = this%v
    val v2 = (Vector(v1,v1,v1)*Vector(2,2,2))
    val v3 = v*v2
    this-v3
  }

}