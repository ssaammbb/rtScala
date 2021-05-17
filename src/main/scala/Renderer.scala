package renderer

import java.awt.{Color, Graphics}
import java.awt.image.BufferedImage

import camera.Camera
import lightpaths.lightpaths
import vector.Vector
import world.World

import scala.annotation.tailrec


case class Renderer(world: World, width: Int, height: Int, origin: Vector, c: Int) {
  @tailrec
  private def renderXpassG(world: World, origin: Vector, c: Double, n: Double, y: Double, height: Double, g: Graphics, step: Double, xval: Int, yval: Int): (Graphics) = {
    if (c >= n) {
      g
    } else {
      val pxx = Vector(c,y,0)
      val dirr = pxx-origin
      val unit = dirr.unit()

      val calcLightPaths = new lightpaths(world, origin, unit, 0, 3, Vector(0,0,0), 1)
      val cd: Vector = calcLightPaths.callTrace()


      val cc = Vector(cd.x*255,cd.y*255,cd.z*255)
      val pix = Vector((0.0.max(cc.x)).min(255),(0.0.max(cc.y)).min(255),(0.0.max(cc.z)).min(255))
      val col = new Color(pix.x.toInt,pix.y.toInt,pix.z.toInt)

      g.setColor(col)
      g.drawLine(xval,yval,xval,yval)

      renderXpassG(world, origin, c + step, n, y, height, g, step, xval+1, yval)
    }
  }

  @tailrec
  private def renderYpassG(world: World, origin: Vector, c: Double, n: Double, width: Int, g: Graphics, step: Double, yval: Int): Graphics = {
    if (c < n) {
      g
    } else {
      val newStep = 2.0/width.toDouble
      val gg = renderXpassG(world, origin, -1, 1, c, n, g, newStep, 0, yval)
      renderYpassG(world, origin, c - step, n, width, gg, step, yval+1)
    }
  }

  def renderG(world: World, width: Int, height: Int, origin: Vector, c: Int): Unit = {
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
    val name = "E:/rtScala/src/main/scala/Drawing" + c.toString + ".png"

    javax.imageio.ImageIO.write(canvas, "png", new java.io.File(name))
  }

  def render(): Unit ={
    renderG(world, width, height, origin, c)
  }
}
