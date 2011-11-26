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
  val shellWriter = new OutputStreamWriter(process.getOutputStream)
  val shellInputStream = process.getInputStream;
  val shellErrorStream = process.getErrorStream
  private val DEBUG_KEY = "ShellService"

  def sudo(cmd: String) = {
    shellWriter.write(cmd)
    shellWriter.flush()
    Log.d(DEBUG_KEY, "Run cmd : " + cmd)
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
    process.destroy()
    super.onDestroy()
  }

  def onBind(p1: Intent) = new ShellServiceBinder

  class ShellServiceBinder extends Binder {
    def getService = service
  }

}

trait ActivityMixinShellService extends Activity {
  context =>
  lazy val shellService = shellServiceHolder
  private var shellServiceHolder: ShellService = null
  private val con = new ServiceConnection {
    def onServiceConnected(className: ComponentName, binder: IBinder) {
      shellServiceHolder = binder.asInstanceOf[ShellService#ShellServiceBinder].getService
    }

    def onServiceDisconnected(className: ComponentName) {}
  }

  abstract override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    bindService(new Intent(context, classOf[ShellService]), con, Context.BIND_AUTO_CREATE)
  }

  abstract override def onDestroy() {
    unbindService(con)
    super.onDestroy()
  }
}

