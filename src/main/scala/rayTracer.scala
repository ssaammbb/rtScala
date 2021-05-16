package rt

import scala.annotation.tailrec

import java.awt.image.BufferedImage
import java.awt.Graphics
import java.awt.Color


import vector._
import sphere._
import world._
import camera._
import lightpaths._

/*import javax.media.Processor
import javax.media.protocol.DataSource
import javax.media.protocol.PullBufferDataSource
import javax.media.protocol.PullBufferDataSource._
import javax.media.protocol.PullBufferStream
import javax.media.DataSink
import javax.media.datasink.DataSinkListener
import javax.media.Buffer
import javax.media.format.VideoFormat*/

object Main extends App{



  @tailrec
  def renderXpassG(world: World, origin: Vector, c: Double, n: Double, y: Double, height: Double, g: Graphics, step: Double, xval: Int, yval: Int): (Graphics) = {
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
  def renderYpassG(world: World, origin: Vector, c: Double, n: Double, width: Int, g: Graphics, step: Double, yval: Int): Graphics = {
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
    val name = "Drawing" + c.toString + ".png"

    javax.imageio.ImageIO.write(canvas, "png", new java.io.File(name))
  }

  @tailrec
  def m_360xy(w: World, c: Double, n: Double, pos: Vector, cam: Camera, ind: Int): Vector = {
    if(c >= n){
      pos
    }else{
      val np = cam.nextPosistion(ind)
      renderG(w, 1920,1080,np, ind)
      println(ind + " frames rendered\n")
      m_360xy(w, c + (2*Math.PI)/100, n, np,  cam, ind + 1)
    }
  }

  def main(): Unit = {
    val s = Sphere(Vector(-.2, 0, -1), .7, Vector(.2,.2,.2), Vector(.1,.3,.5), Vector(1,1,1), 100, 0.1)
    val s1 = Sphere(Vector(.6, 0, 0), .3, Vector(0,0,0), Vector(.3,.1,.15), Vector(1,1,1), 100, 0)
    val s2 = Sphere(Vector(-.45, -.2, .3), .15, Vector(.1,.1,.1), Vector(.9,.2,.1), Vector(1,1,1), 100, .3)
    val s3 = Sphere(Vector(0, -1000, 0), 999.3, Vector(.2,.2,.2), Vector(.9,.9,.9), Vector(1,1,1), 100, .5)

    val l = Sphere(Vector(-1, .3, .3), 1, Vector(1,1,1), Vector(.3,1,1), Vector(1,1,1), 100, 1)

    val objs = Array(s1, s2, s3,s)
    val lights = Array(l)

    val w = World(objs, lights)
    val c: Camera = Camera(simpleSphere(Vector(0,0.1,1),1))
    renderG(w, 1920,1080,c.gizmo.center, 0)
    //m_360xy(w, 0, 2*Math.PI, Vector(0,.1,1), c, 0)
    //javax.media.protocol.PullBufferDataSource.
  }
  main()
}



