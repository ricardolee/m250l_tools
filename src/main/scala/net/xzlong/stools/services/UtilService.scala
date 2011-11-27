package net.xzlong.stools.services

import net.xzlong.stools.util.Utils
import Utils._
import android.util.Log
import android.widget.Toast
import net.xzlong.stools.R
import android.app.{Activity, Service}
import android.os.{Bundle, IBinder, Binder}
import android.content.{Context, ComponentName, ServiceConnection, Intent}
import java.io.{FileInputStream, File}

class UtilService extends ServiceMixinShellService {
  utilService =>
  val DEBUG_KEY = "UtilService"

  def changeEsn(esn: String) {
    if (esn.matches(esnPattern)) {
      shellService.sudo("chmod 777 /efs/nv_data.bin\n", 100) // Wait 100 millies, make sure has been changed.
      writeEsn2File(esn, new File("/efs/nv_data.bin"))
      shellService.sudo("chmod 755 /efs/nv_data.bin\n")
      Toast.makeText(utilService, getString(R.string.change_esn_write_ok), Toast.LENGTH_LONG).show()
    } else {
      Toast.makeText(utilService, getString(R.string.change_esn_bad), Toast.LENGTH_LONG).show()
    }
  }

  def readEsn() = {
    val op = shellService.sudo("getprop ril.cdma.phone.id\n", 100)._1
    esnPattern.r.findFirstIn(op).getOrElse("")
  }

  def backupNv() {
    shellService.sudo("cp /efs/nv_data.bin /sdcard/nv_data.bin.bak\n")
    Toast.makeText(utilService, getString(R.string.backup_nv_data_ok), Toast.LENGTH_LONG).show()
  }

  def rebootDevice() {
    shellService.sudo("sync\nreboot\n")
  }

  def rebootDevice2Recovery() {
    shellService.sudo("sync\nreboot recovery\n")
  }

  def rebootDevice2Download() {
    shellService.sudo("sync\nreboot download\n")
  }


  def flashZImage() {
    val zImageFile = new File("/sdcard/zImage")
    if (zImageFile.exists) {
      new FileInputStream(zImageFile).close()
      shellService.sudo("dd if=/sdcard/zImage of=/dev/block/mmcblk0p5 bs=4096\n")
      Toast.makeText(utilService, getString(R.string.flash_zImage_ok), Toast.LENGTH_LONG).show()
    } else {
      Toast.makeText(utilService, getString(R.string.zImage_not_exist), Toast.LENGTH_LONG).show()
    }
  }

  def backupZImage() {
    shellService.sudo("rm /sdcard/zImage.bak\ndd if=/dev/block/mmcblk0p5 of=/sdcard/zImage.bak bs=4096\n")
    Toast.makeText(utilService, getString(R.string.zImage_backup_ok), Toast.LENGTH_LONG).show()
  }

  def onBind(p1: Intent) = new UtilServiceBinder

  class UtilServiceBinder extends Binder {
    def getService = utilService
  }

}

protected trait MixinUtilServiceBase {

  lazy val utilService = {
    assert(utilServiceHolder != null); utilServiceHolder
  }
  private var utilServiceHolder: UtilService = null
  protected val utilServiceConnection = new ServiceConnection {
    def onServiceConnected(className: ComponentName, binder: IBinder) {
      utilServiceHolder = binder.asInstanceOf[UtilService#UtilServiceBinder].getService
    }

    def onServiceDisconnected(className: ComponentName) {}
  }

}

trait ActivityMixinUtilService extends Activity with MixinUtilServiceBase {
  context =>

  abstract override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    bindService(new Intent(context, classOf[UtilService]), utilServiceConnection, Context.BIND_AUTO_CREATE)
  }

  abstract override def onDestroy() {
    unbindService(utilServiceConnection)
    super.onDestroy()
  }
}

trait ServiceMixinUtilService extends Service with MixinUtilServiceBase {
  context =>

  abstract override def onCreate() {
    super.onCreate()
    bindService(new Intent(context, classOf[UtilService]), utilServiceConnection, Context.BIND_AUTO_CREATE)
  }

  abstract override def onDestroy() {
    unbindService(utilServiceConnection)
    super.onDestroy()
  }
}