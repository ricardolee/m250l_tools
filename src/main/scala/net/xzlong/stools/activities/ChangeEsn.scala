package net.xzlong.stools.activities

import android.app.Activity
import net.xzlong.stools.R
import net.xzlong.stools.utilies.Utility
import Utility._
import android.view.View.OnClickListener
import android.view.View
import java.io.File
import android.widget.{Toast, EditText, Button}
import android.os.Bundle
import net.xzlong.stools.services.ActivityMixinShellService
import android.util.Log

class ChangeEsn extends ActivityMixinShellService {
  context =>

  val DEBUG_KEY = "ChangeEsn"
  lazy val writeButton = findViewById(R.id.change_esn_ok).asInstanceOf[Button]
  lazy val esnEditText = findViewById(R.id.change_esn_entry).asInstanceOf[EditText]
  lazy val writeClick = new OnClickListener {
    def onClick(v: View) {
      val esn = esnEditText.getText.toString.trim
      if (esn.matches(esnPattern)) {
        val (is, es) = shellService.sudo("chmod 777 /efs/nv_data.bin\n")
        Log.d(DEBUG_KEY, "\n-------------------\nshell print: " + is +
          "\n ------------------\nerror print: " + es)
        new Thread() {
          override def run() {
            synchronized {
              wait(100) // Make sure the change permission of the file "nv_data.bin" has been done.
              writeEsn2File(esn, new File("/efs/nv_data.bin"))
            }
            shellService.sudo("chmod 755 /efs/nv_data.bin\n")
            Toast.makeText(context, getString(R.string.change_esn_write_ok), Toast.LENGTH_LONG).show()
          }
        }.run()
      } else {
        Toast.makeText(context, getString(R.string.change_esn_bad), Toast.LENGTH_LONG).show()
      }
    }
  }

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.change_esn)
    writeButton.setOnClickListener(writeClick)
  }

}