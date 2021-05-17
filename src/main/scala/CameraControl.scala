package cameracontrol

import camera.Camera
import vector.Vector
import world.World
import renderer.Renderer

import scala.annotation.tailrec

case class CameraControl(w: World, c: Double, n: Double, pos: Vector, cam: Camera, ind: Int) {
  @tailrec
  private def m_360xy(w: World, c: Double, n: Double, pos: Vector, cam: Camera, ind: Int): Vector = {
    if(c >= n){
      pos
    }else{
      val np = cam.nextPosistion(ind)
      val r = Renderer(w, 1920, 1080, np, ind)
      r.renderG(w, 1920,1080,np, ind)
      println(ind + " frames rendered\n")
      m_360xy(w, c + (2*Math.PI)/100, n, np,  cam, ind + 1)
    }
  }

  def rotateCamAroundPoint(): Unit ={
    m_360xy(w, c, n, pos, cam, ind)
  }
}
