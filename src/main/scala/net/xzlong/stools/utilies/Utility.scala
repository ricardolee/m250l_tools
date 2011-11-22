package net.xzlong.stools.utilies

import java.io.{RandomAccessFile, File, OutputStreamWriter, BufferedWriter}


/*
  The length of hex string must be eight.
 */
object Utility {

  val esnPattern = """^[0-9A-Fa-f]{8}"""

  val runtime = Runtime.getRuntime

  def sudo(cmd: String) {
    val process = runtime.exec("su")
    val writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream))
    writer.write(cmd)
    writer.flush()
    writer.write("exit\n")
    writer.flush()
    val res = process.waitFor
    if (res != 0) {
      throw new RuntimeException("Execution of cmd '%s' failed with exit code %d".format(cmd, res))
    }
  }

  def hex2long(hex: String) = {
    val high = Integer.valueOf(hex.substring(0, 4), 16).longValue
    val low = Integer.valueOf(hex.substring(4, 8), 16).longValue
    high << 16 ^ low
  }

  def long2hex(value: Long) = {
    val resultHex = value.toHexString
    if (resultHex.length < 8) "0" + resultHex else resultHex
  }

  def long2ByteArray(value: Long) = {
    val byteArray = new Array[Byte](4)
    var temp = value << 8
    for (i <- 0 to 3) {
      temp >>= 8
      byteArray(3 - i) = temp.asInstanceOf[Byte]
    }
    byteArray
  }

  def esn2long(esn: String) = {
    assert(esn.matches(esnPattern))
    val array = esn.toCharArray
    val resultArray = new Array[Char](8)
    for (i <- 0 until 4) {
      val index = i * 2
      resultArray(index) = array(7 - index - 1)
      resultArray(index + 1) = array(7 - index)
    }
    val tempHex = String.valueOf(resultArray)
    val temp = ~hex2long(tempHex)
    val mark = ~hex2long("81fce35b")
    temp ^ mark
  }

  def esn2ByteArray(esn: String) = long2ByteArray(esn2long(esn))

  def writeEsn2File(esn: String, file: File) {
    assert(esn.matches(esnPattern))
    val raf = new RandomAccessFile(file, "rw")
    raf.write(esn2ByteArray(esn))
    raf.write(0x01.asInstanceOf[Byte])
    raf.close()
  }

}