package net.xzlong.stools.activities

import android.app.Activity
import android.os.Bundle
import net.xzlong.stools.R
import net.xzlong.stools.utilies.Utility
import Utility._
import android.view.View.OnClickListener
import android.view.View
import java.io.File
import android.widget.{Toast, EditText, Button}

class ChangeEsn extends Activity {
  context =>
  lazy val writeButton = findViewById(R.id.change_esn_ok).asInstanceOf[Button]
  lazy val esnEditText = findViewById(R.id.change_esn_entry).asInstanceOf[EditText]

  lazy val writeClick = new OnClickListener {
    def onClick(v: View) {
      val esn = esnEditText.getText.toString
      if(esn.matches(esnPattern)){
        sudo("chmod 777 /efs/nv_data.bin\n")
        writeEsn2File(esn, new File("/efs/nv_data.bin"))
        sudo("chmod 755 /efs/nv_data.bin\n")
        Toast.makeText(context, getString(R.string.change_esn_write_ok) ,Toast.LENGTH_LONG).show()
      } else {
        Toast.makeText(context, getString(R.string.change_esn_bad) ,Toast.LENGTH_LONG).show()
      }
    }
  }

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.change_esn)
    writeButton.setOnClickListener(writeClick)
  }
}