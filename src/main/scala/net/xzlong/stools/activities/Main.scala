package net.xzlong.stools.activities

import net.xzlong.stools.R
import collection.JavaConversions._
import java.util.{List => JList, Map => JMap}
import net.xzlong.stools.services.ActivityMixinUtilService
import android.app.{AlertDialog, ListActivity}
import android.content.{DialogInterface, Intent}
import android.view.{LayoutInflater, View}
import android.widget.{EditText, Toast, ListView, SimpleAdapter}
import android.os.{Environment, Bundle}

class Main extends ListActivity with ActivityMixinUtilService {
  context =>

  import Cons._

  lazy val layoutInflater = LayoutInflater.from(context)

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setListAdapter(new SimpleAdapter(context, buildData,
      android.R.layout.simple_list_item_1,
      Array(TITLE),
      Array(android.R.id.text1)))

  }


  override def onListItemClick(l: ListView, v: View, position: Int, id: Long) {
    position match {
      case 0 =>
        checkSdThenDo(() => utilService.backupNv())
      case 1 =>
        showDialog(DIALOG_READ_ESN)
      case 2 =>
        showDialog(DIALOG_CHANGE_ESN)
      case 3 =>
        checkSdThenDo(() => utilService.backupZImage())
      case 4 =>
        checkSdThenDo(() => showDialog(DIALOG_FLASH_Z_IMAGE))
      case 5 =>
        showDialog(DIALOG_REBOOT)
      case 6 =>
        showDialog(DIALOG_REBOOT_RECOVERY)
      case 7 =>
        showDialog(DIALOG_REBOOT_DOWNLOAD)
      case 8 =>
        Toast.makeText(context, "Thanks for use it.", Toast.LENGTH_LONG).show()
    }
  }

  private def buildData: JList[JMap[String, Object]] = {
    List[JMap[String, Object]](
      Map(TITLE -> getString(R.string.title_backup_nv_data)),
      Map(TITLE -> getString(R.string.title_read_esn)),
      Map(TITLE -> getString(R.string.title_write_esn)),
      Map(TITLE -> getString(R.string.title_backup_zImage)),
      Map(TITLE -> getString(R.string.title_flash_zImage)),
      Map(TITLE -> getString(R.string.title_reboot)),
      Map(TITLE -> getString(R.string.title_reboot_recovery)),
      Map(TITLE -> getString(R.string.title_reboot_download)),
      Map(TITLE -> "By 夜精灵")
    )
  }

  private lazy val currentEsn = utilService.readEsn()

  override def onCreateDialog(id: Int, args: Bundle) = {
    id match {
      case DIALOG_CHANGE_ESN =>
        val changeEnsView = layoutInflater.inflate(R.layout.change_esn, null)
        val esnEditText = changeEnsView.findViewById(R.id.change_esn_entry).asInstanceOf[EditText]
        new AlertDialog.Builder(this)
          .setView(changeEnsView)
          .setPositiveButton(getString(R.string.change_esn_ok), new DialogInterface.OnClickListener {
          def onClick(p1: DialogInterface, p2: Int) {
            utilService.changeEsn(esnEditText.getText.toString.trim)
          }
        })
          .create
      case DIALOG_READ_ESN =>
        new AlertDialog.Builder(this)
          .setTitle("ESN: " + currentEsn)
          .create()

      case DIALOG_FLASH_Z_IMAGE =>
        new AlertDialog.Builder(this)
          .setTitle(getString(R.string.title_flash_zImage))
          .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener {
          def onClick(p1: DialogInterface, p2: Int) {
            checkSdThenDo(() => utilService.flashZImage())
          }
        })
          .create
      case DIALOG_REBOOT =>
        new AlertDialog.Builder(this)
          .setTitle(getString(R.string.title_reboot))
          .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener {
          def onClick(p1: DialogInterface, p2: Int) {
            utilService.rebootDevice()
          }
        })
          .create
      case DIALOG_REBOOT_RECOVERY =>
        new AlertDialog.Builder(this)
          .setTitle(getString(R.string.title_reboot_recovery))
          .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener {
          def onClick(p1: DialogInterface, p2: Int) {
            utilService.rebootDevice2Recovery()
          }
        })
          .create
      case DIALOG_REBOOT_DOWNLOAD =>
        new AlertDialog.Builder(this)
          .setTitle(getString(R.string.title_reboot_download))
          .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener {
          def onClick(p1: DialogInterface, p2: Int) {
            utilService.rebootDevice2Download()
          }
        })
          .create
    }
  }

  private def checkSdThenDo(action: () => Unit ) {
    if(Environment.MEDIA_MOUNTED == Environment.getExternalStorageState) {
      action()
    } else {
      Toast.makeText(context, getString(R.string.sdcard_not_mount), Toast.LENGTH_LONG).show()
    }
  }


  private object Cons {
    val TITLE = "title"
    val USAGE = "usage"
    val DIALOG_CHANGE_ESN = 0
    val DIALOG_READ_ESN = 1
    val DIALOG_FLASH_Z_IMAGE = 2
    val DIALOG_REBOOT = 3
    val DIALOG_REBOOT_RECOVERY = 4
    val DIALOG_REBOOT_DOWNLOAD = 5
  }

}