package net.xzlong.stools.utilies

import org.scalatest.Suite

import Utility._
import io.Source
import java.io.{RandomAccessFile, File, FileOutputStream, ByteArrayOutputStream}

class UtilityTestSuite extends Suite {

  val resultArrayBuffer = Array(0x01.asInstanceOf[Byte],
      0x47.asInstanceOf[Byte],
      0x40.asInstanceOf[Byte],
      0xdb.asInstanceOf[Byte])

  val testESN = "80a3bb80"
  val resultHex = "014740db"

  def testEsn2hexString() {
    assert(long2hex(esn2long(testESN)) == resultHex)
  }

  def testEsn2ByteArray() {
    val r = esn2ByteArray(testESN)
    for (i <- 0 to 3) {
      assert(r(i) == resultArrayBuffer(i))
    }
  }

  def testWriteEsn2File() {
    val f = new File(getClass.getResource("/nv_data.bin").toURI)
    writeEsn2File(testESN,f)
  }
}