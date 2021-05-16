package camera
import vector._
import sphere._

import scala.annotation.tailrec

case class Camera(gizmo: simpleSphere) {


  def nextPosistion(c: Int): Vector = {
    val theta = ((2*Math.PI)/100)*(c.toDouble)
    val xval = (gizmo.center.x + ((gizmo.radius)*Math.cos(theta)))
    val yval = (gizmo.center.z + ((gizmo.radius)*Math.sin(theta)))
    Vector(xval,gizmo.center.y,yval)

  }


}
