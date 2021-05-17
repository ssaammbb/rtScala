package rt

import vector._
import sphere._
import world._
import camera._
import renderer._
import cameracontrol._

object Main extends App{

  def main(args: String): Unit = {
    val s = Sphere(Vector(-.2, 0, -1), .7, Vector(.2,.2,.2), Vector(.1,.3,.5), Vector(1,1,1), 100, 0.1)
    val s1 = Sphere(Vector(.6, 0, 0), .3, Vector(0,0,0), Vector(.3,.1,.15), Vector(1,1,1), 100, 0)
    val s2 = Sphere(Vector(-.45, -.2, .3), .15, Vector(.1,.1,.1), Vector(.9,.2,.1), Vector(1,1,1), 100, .3)
    val s3 = Sphere(Vector(0, -1000, 0), 999.3, Vector(.2,.2,.2), Vector(.9,.9,.9), Vector(1,1,1), 100, .5)

    val l = Sphere(Vector(-1, .3, .3), 1, Vector(1,1,1), Vector(.3,1,1), Vector(1,1,1), 100, 1)

    val objs = Array(s1, s2, s3,s)
    val lights = Array(l)

    val w = World(objs, lights)
    val cam: Camera = Camera(simpleSphere(Vector(3,0.3,3),1))
    args match{
      case("1") => {
        val r = new Renderer(w, 1920,1080,cam.gizmo.center, -1)
        r.render()
      }
      case("2") => {
        val controller = new CameraControl(w, 0, 2*Math.PI, Vector(0,.1,1), cam, 0)
        controller.rotateCamAroundPoint()
      }
      case _ => return
    }





  }
  val arg: String = scala.io.StdIn.readLine("Enter 1 to render single frame, 2 to render camera rotating around point>")
  //val a = Array(arg)
  //print(arg)

  main(arg)
}



