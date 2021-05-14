package rt

import scala.annotation.tailrec

import java.awt.image.BufferedImage
import java.awt.Graphics
import java.awt.Color

import vector._
import sphere._
import world._

object Main extends App{

  @tailrec
  def traceHelper(world: World, origin: Vector, direction: Vector, distance: Double, ind: Int, n: Int, c: Int): (Double, Int) = {
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

  def trace(world: World, origin: Vector, direction: Vector, c: Int, n: Int, color: Vector, reflection: Double): Vector = {
    if(c >= n){
      color
    }else{
      val th = findCol(world, origin, direction, color, reflection)
      trace(world, th._2, th._3, c+1, n, th._1, th._4)
    }

  }

  @tailrec
  def renderXpassG(world: World, origin: Vector, c: Double, n: Double, y: Double, height: Double, g: Graphics, step: Double, xval: Int, yval: Int): (Graphics) = {
    if (c >= n) {
      g
    } else {
      val pxx = Vector(c,y,0)
      val dirr = pxx-origin
      val unit = dirr.unit()

      val cd: Vector = trace(world, origin, unit, 0, 5, Vector(0,0,0), 1)


      val cc = Vector(cd.x*255,cd.y*255,cd.z*255)
      val pix = Vector((0.0.max(cc.x)).min(255),(0.0.max(cc.y)).min(255),(0.0.max(cc.z)).min(255))
      val col = new Color(pix.x.toInt,pix.y.toInt,pix.z.toInt)

      g.setColor(col)
      g.drawLine(xval,yval,xval,yval)

      renderXpassG(world, origin, c + step, n, y, height, g, step, xval+1, yval)
    }
  }

  @tailrec
  def renderYpassG(world: World, origin: Vector, c: Double, n: Double, width: Int, g: Graphics, step: Double, yval: Int): Graphics = {
    if (c < n) {
      g
    } else {
      val newStep = 2.0/width.toDouble
      val gg = renderXpassG(world, origin, -1, 1, c, n, g, newStep, 0, yval)
      renderYpassG(world, origin, c - step, n, width, gg, step, yval+1)
    }
  }

  def renderG(world: World, width: Int, height: Int, origin: Vector): Unit = {
    val size = (1920, 1080)
    val canvas = new BufferedImage(size._1, size._2, BufferedImage.TYPE_INT_RGB)
    val g = canvas.createGraphics()
    g.setColor(Color.white)
    g.fillRect(0, 0, 1920, 1080)
    g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)

    val ra = width.toDouble/height.toDouble
    val start = 1/ra
    val end = -1/ra
    val step = (start-end)/height

    val gg = renderYpassG(world, origin, start, end, width, g, step, 0)
    gg.dispose()

    javax.imageio.ImageIO.write(canvas, "png", new java.io.File("drawing.png"))
  }

  def main(): Unit = {
    val s = Sphere(Vector(-.2, 0, -1), .7, Vector(.2,.2,.2), Vector(.1,.3,.5), Vector(1,1,1), 100, 0.1)
    val s1 = Sphere(Vector(.6, 0, 0), .3, Vector(0,0,0), Vector(.3,.1,.15), Vector(1,1,1), 100, 0)
    val s2 = Sphere(Vector(-.45, -.2, .3), .15, Vector(.1,.1,.1), Vector(.9,.2,.1), Vector(1,1,1), 100, .3)
    val s3 = Sphere(Vector(0, -1000, 0), 999.3, Vector(.2,.2,.2), Vector(.9,.9,.9), Vector(1,1,1), 100, .5)

    val l = Sphere(Vector(-1, .3, .3), 1, Vector(1,1,1), Vector(.3,1,1), Vector(1,1,1), 100, 1)

    val objs = List(s1, s2, s3,s)
    val lights = List(l)

    val w = World(objs, lights)
    renderG(w, 1920,1080,Vector(0,0,1))
  }
  main()
}



