package lightpaths

import vector.Vector
import world.World

import scala.annotation.tailrec

case class lightpaths(world: World, origin: Vector, direction: Vector, c: Int, n: Int, color: Vector, reflection: Double){
  @tailrec
  private def traceHelper(world: World, origin: Vector, direction: Vector, distance: Double, ind: Int, n: Int, c: Int): (Double, Int) = {
    if (c >= n) {
      (distance, ind)
    } else {
      val d = world.spheres(c).intersect(origin, direction)
      if (d > 0 && d < distance) {
        traceHelper(world, origin, direction, d, c, n, c + 1)
      } else {
        traceHelper(world, origin, direction, distance, ind, n, c + 1)
      }
    }
  }

  def findCol(world: World, origin: Vector, direction: Vector, color: Vector, reflection: Double): (Vector,Vector,Vector,Double) = {

    val distInd = traceHelper(world, origin, direction, 999999999, -1, world.spheres.length, 0)
    val index = distInd._2
    val distance = distInd._1

    if (index < 0) {
      (color, origin, direction,reflection)
    } else {

      val pz = Vector(distance, distance, distance)*direction
      val pp = pz + origin
      val n = (pp - world.spheres(index).center).unit()
      val p = (n * Vector(.00001, .00001, .00001) + pp)
      val lts = (world.lights(0).center - p).unit()

      val nInt = traceHelper(world, p, lts, 999999999, -1, world.spheres.length, 0)


      val liDist = (world.lights(0).center - pp).length()

      if (nInt._1 < liDist) {
        (color, origin, direction, reflection)
      } else {
        val illu = Vector(0, 0, 0)
        val illuA = illu + (world.spheres(index).ambient * world.lights(0).ambient)
        val illuD = illuA + (world.spheres(index).diffuse * world.lights(0).diffuse * Vector(lts % n, lts % n, lts % n))

        val itc = (origin - pp).unit()
        val h = (itc + lts).unit()

        val itmp = world.spheres(index).specular * world.lights(0).specular * Vector(n % h, n % h, n % h)
        val illuSx = math.pow(itmp.x, world.spheres(index).lum / 4)
        val illuSy = math.pow(itmp.y, world.spheres(index).lum / 4)
        val illuSz = math.pow(itmp.z, world.spheres(index).lum / 4)
        val illuS = illuD + Vector(illuSx, illuSy, illuSz)

        val col: Vector = color + (illuS * Vector(reflection, reflection, reflection))
        val ref = world.spheres(index).roughness * reflection
        val dir = direction.reflect(n)

        (col, p, dir, ref)

      }

    }
  }

  @tailrec
  private def trace(world: World, origin: Vector, direction: Vector, c: Int, n: Int, color: Vector, reflection: Double): Vector = {
    if(c >= n){
      color
    }else{
      val th = findCol(world, origin, direction, color, reflection)
      trace(world, th._2, th._3, c+1, n, th._1, th._4)
    }

  }

  def callTrace(): Vector ={
    trace(world, origin, direction, c, n, color, reflection)
  }
}
