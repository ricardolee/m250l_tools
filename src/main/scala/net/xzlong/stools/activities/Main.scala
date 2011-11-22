package net.xzlong.stools.activities

import android.os.Bundle
import net.xzlong.stools.R
import android.view.View
import android.view.View.OnClickListener
import net.xzlong.stools.utilies.Utility
import Utility._
import android.app.ListActivity
import collection.JavaConversions._
import java.util.{List => JList, Map => JMap}
import android.content.Intent
import android.widget.{Toast, ListView, SimpleAdapter, TextView}

class Main extends ListActivity {
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
        sudo("cp /efs/nv_data.bin /sdcard/nv_data.bin.bak\n")
        Toast.makeText(context, getString(R.string.backup_nv_data_ok) ,Toast.LENGTH_LONG).show()
      case 1 =>
        startActivity(new Intent().setClass(context, classOf[ChangeEsn]))
      case 2 =>
        sudo("reboot\n")
      case 3 =>
        Toast.makeText(context, "Thanks for use it.",Toast.LENGTH_LONG).show()
    }
  }

  def buildData:JList[JMap[String, Object]]  = {

    List[JMap[String,Object]](
      Map("title" -> getString(R.string.title_backup_nv_data)),
      Map("title" -> getString(R.string.title_write_esn)),
      Map("title" -> getString(R.string.title_reboot)),
      Map("title" -> "By 夜精灵"))
  }
}