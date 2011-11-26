package net.xzlong.stools.activities

import net.xzlong.stools.R
import android.view.View
import android.app.ListActivity
import collection.JavaConversions._
import java.util.{List => JList, Map => JMap}
import android.widget.{Toast, ListView, SimpleAdapter}
import android.content.Intent
import net.xzlong.stools.services.ActivityMixinShellService
import android.os.Bundle

class Main extends ListActivity with ActivityMixinShellService {
  context =>
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setListAdapter(new SimpleAdapter(context, buildData,
      android.R.layout.simple_list_item_1,
      Array("title"),
      Array(android.R.id.text1)))

  }


  override def onListItemClick(l: ListView, v: View, position: Int, id: Long) {
    position match {
      case 0 =>
        shellService.sudo("cp /efs/nv_data.bin /sdcard/nv_data.bin.bak\n")
        Toast.makeText(context, getString(R.string.backup_nv_data_ok), Toast.LENGTH_LONG).show()
      case 1 =>
        startActivity(new Intent().setClass(context, classOf[ChangeEsn]))
      case 2 =>
        shellService.sudo("reboot\n")
      case 3 =>
        Toast.makeText(context, "Thanks for use it.", Toast.LENGTH_LONG).show()
      case 4 =>
        shellService.sudo("ls /sdcard/\n")
    }
  }

  def buildData: JList[JMap[String, Object]] = {
    List[JMap[String, Object]](
      Map("title" -> getString(R.string.title_backup_nv_data)),
      Map("title" -> getString(R.string.title_write_esn)),
      Map("title" -> getString(R.string.title_reboot)),
      Map("title" -> "By 夜精灵"),
      Map("title" -> "test sudo ")
    )
  }
}