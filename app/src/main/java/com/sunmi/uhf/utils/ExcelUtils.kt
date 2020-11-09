package com.sunmi.uhf.utils

import com.sunmi.uhf.App
import com.sunmi.uhf.R
import com.sunmi.uhf.bean.LabelInfoBean
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.format.Alignment
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.Colour
import jxl.write.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

object ExcelUtils {
    private lateinit var arial14font: WritableFont
    private lateinit var arial14format: WritableCellFormat
    private lateinit var arial10font: WritableFont
    private lateinit var arial10format: WritableCellFormat
    private lateinit var arial12font: WritableFont
    private lateinit var arial12format: WritableCellFormat
    private const val UTF8_ENCODING = "UTF-8"
    private const val GBK_ENCODING = "GBK"
    private val mContent = arrayOf(
        "ID",
        "EPC",
        "PC",
        App.mContext.getString(R.string.find_number_text),
        App.mContext.getString(R.string.rssi_text),
        App.mContext.getString(R.string.carrier_frequency_text)
    )
    private const val FILE_SUFFIX = ".xls"
    private fun format() {
        try {
            arial14font = WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD)
            arial14font.colour = Colour.LIGHT_BLUE
            arial14format = WritableCellFormat(arial14font)
            arial14format.alignment = Alignment.CENTRE
            arial14format.setBorder(Border.ALL, BorderLineStyle.THIN)
            arial14format.setBackground(Colour.VERY_LIGHT_YELLOW)
            arial10font = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
            arial10format = WritableCellFormat(arial10font)
            arial10format.alignment = Alignment.CENTRE
            arial10format.setBorder(Border.ALL, BorderLineStyle.THIN)
            arial10format.setBackground(Colour.LIGHT_BLUE)
            arial12font = WritableFont(WritableFont.ARIAL, 12)
            arial12format = WritableCellFormat(arial12font)
            arial12format.setBorder(Border.ALL, BorderLineStyle.THIN)
        } catch (e: WriteException) {
            e.printStackTrace()
        }
    }

    private fun initExcel(fileName: String?, colName: Array<String>) {
        format()
        var workbook: WritableWorkbook? = null
        try {
            val file = File(fileName)
            if (file.exists()) {
                file.deleteOnExit()
                file.createNewFile()
            } else {
                file.createNewFile()
            }
            workbook = Workbook.createWorkbook(file)
            val sheet = workbook.createSheet("tags", 0)
            sheet.addCell(Label(0, 0, fileName, arial14format) as WritableCell)
            for (col in colName.indices) {
                sheet.addCell(Label(col, 0, colName[col], arial10format))
            }
            workbook.write()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (workbook != null) {
                try {
                    workbook.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun writeTagToExcel(fileName: String, list: List<LabelInfoBean>?): String {
        val file = fileName.trim() + FILE_SUFFIX
        initExcel(file, mContent)
        writeObjListToExcel(list, file)
        return file
    }

    private fun writeObjListToExcel(objList: List<LabelInfoBean>?, fileName: String?) {
        if (objList != null && objList.isNotEmpty()) {
            var writebook: WritableWorkbook? = null
            var inputStream: InputStream? = null
            try {
                val setEncode = WorkbookSettings()
                setEncode.encoding = UTF8_ENCODING
                inputStream = FileInputStream(File(fileName))
                val workbook = Workbook.getWorkbook(inputStream)
                writebook = Workbook.createWorkbook(
                    File(fileName),
                    workbook
                )
                val sheet = writebook.getSheet(0)
                for (j in objList.indices) {
                    val list = ArrayList<String?>()
                    val (epc, pc, findNum, rssi, frequency) = objList[j]
                    list.add((j + 1).toString())
                    list.add(epc)
                    list.add(pc)
                    list.add(findNum.toString())
                    list.add(rssi)
                    list.add(frequency)
                    for (i in list.indices) {
                        sheet.addCell(Label(i, j + 1, list[i], arial12format))
                    }
                }
                writebook.write()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}