package net.xzlong.stools.services

import java.io.OutputStreamWriter
import android.util.Log
import android.app.{Activity, Service}
import android.os.{Bundle, IBinder, Binder}
import android.content.{Context, ComponentName, ServiceConnection, Intent}


class ShellService extends Service {

  service =>

  private val runtime = Runtime.getRuntime
  private val process = runtime.exec("su")
  private val shellWriter = new OutputStreamWriter(process.getOutputStream)
  private val shellInputStream = process.getInputStream;
  private val shellErrorStream = process.getErrorStream
  private val DEBUG_KEY = "ShellService"

  def sudo(cmd: String, milliesForWait: Long = 0) = {
    shellWriter.write(cmd)
    shellWriter.flush()
    Log.d(DEBUG_KEY, "Run cmd : " + cmd)
    if (milliesForWait != 0)
      synchronized {
        wait(milliesForWait)
      }

    val la = shellInputStream.available
    val ea = shellErrorStream.available
    val ls = if (la != 0) {
      val tempArrayByte = new Array[Byte](la)
      shellInputStream.read(tempArrayByte)
      new String(tempArrayByte)
    } else ""

    val es = if (ea != 0) {
      val tempArrayByte = new Array[Byte](ea)
      shellErrorStream.read(tempArrayByte)
      new String(tempArrayByte)
    } else ""

    (ls, es)
  }

  override def onDestroy() {
    sudo("exit\n")
    process.waitFor()
    process.destroy()
    super.onDestroy()
  }

  def onBind(p1: Intent) = new ShellServiceBinder

  class ShellServiceBinder extends Binder {
    def getService = service
  }

}

protected trait MixinShellServiceBase {

  lazy val shellService = {
    assert(shellServiceHolder != null); shellServiceHolder
  }
  private var shellServiceHolder: ShellService = null
  protected val shellServiceConnection = new ServiceConnection {
    def onServiceConnected(className: ComponentName, binder: IBinder) {
      shellServiceHolder = binder.asInstanceOf[ShellService#ShellServiceBinder].getService
    }

    def onServiceDisconnected(className: ComponentName) {}
  }

}

trait ActivityMixinShellService extends Activity with MixinShellServiceBase {
  context =>

  abstract override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    bindService(new Intent(context, classOf[ShellService]), shellServiceConnection, Context.BIND_AUTO_CREATE)
  }

  abstract override def onDestroy() {
    unbindService(shellServiceConnection)
    super.onDestroy()
  }
}

trait ServiceMixinShellService extends Service with MixinShellServiceBase {
  context =>

  abstract override def onCreate() {
    super.onCreate()
    bindService(new Intent(context, classOf[ShellService]), shellServiceConnection, Context.BIND_AUTO_CREATE)
  }

  abstract override def onDestroy() {
    unbindService(shellServiceConnection)
    super.onDestroy()
  }
}

