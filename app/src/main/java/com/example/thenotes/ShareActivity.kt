@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.thenotes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.thenotes.data.AppContainer
import com.example.thenotes.data.ItemNota
import com.example.thenotes.data.Nota
import com.example.thenotes.data.Setting
import com.example.thenotes.ui.theme.TheNotesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ShareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var id=0
        id = intent.getIntExtra("id",0)
        val ctx: Activity= this as Activity
        val myApp = application as TheNotes
        enableEdgeToEdge()
        setContent {
            TheNotesTheme {
                NotaView(id, myApp.container, ctx)
            }
        }
    }
}



@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NotaView(id: Int, container: AppContainer, ctx: Activity){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var imagebytes by remember { mutableStateOf<ByteArray?>(null) }
    val settingViewModel = SettingViewModel(container.settingRepository)
    val notaViewModel = NotaViewModel(container)
    val itemNotaViewModel = ItemNotaViewModel(container.itemNotaRepository)
    val settinglist = settingViewModel.settingList.collectAsState(listOf<Setting>()).value
    val nota = notaViewModel.getNotaByIdStream(id).collectAsState(Nota(0, "","",0.0)).value
    val listItemNota = itemNotaViewModel.getItemByNotaIdStream(id).collectAsState(listOf<ItemNota>()).value
    var notaBitmap by remember { mutableStateOf<Bitmap?>(null) }
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = { CenterAlignedTopAppBar({Text("Preview", fontSize = 18.sp)},
            navigationIcon = { IconButton(onClick = {ctx.finish()}){
                Icon(Icons.Default.ArrowBackIosNew,"back")
            } },
            actions = {IconButton(onClick = {
                if(listItemNota.isNotEmpty()) {
                    SimpanDanKirimGambar(context, notaBitmap!!)
                }
            }) {
                Icon(Icons.Default.Share,"share")
            }}
            ) }
    ) {innerPadding ->
        val modifier  = Modifier.padding(innerPadding).fillMaxWidth()
        if(listItemNota.isNotEmpty()) {
            Column(modifier) {
                if(settinglist.isNotEmpty()) {
                    lateinit var setting: Setting
                    settinglist.forEach { seting ->
                        setting = seting
                    }
                    if(!setting.uri_logo.contentEquals("kosong")){
                        coroutineScope.launch {
                            withContext(Dispatchers.IO){
                                val itemGambar = context.contentResolver.openInputStream(setting.uri_logo.toUri())
                                imagebytes = itemGambar?.readBytes()!!
                                itemGambar?.close()
                                notaBitmap = createNotaBitmap(listSetting = settinglist,imagebytes!!, listItemNota, nota)
                            }
                        }
                    }


                }else{
                    notaBitmap = createNotaBitmap(listSetting = settinglist,imagebytes!!, listItemNota, nota)
                }
                notaBitmap?.let{
                    Image(
                        it.asImageBitmap(), "nota image",
                        modifier, Alignment.Center
                    )
                }

            }
        }else{
            Column(modifier) {
                Text("Kamu jangan masuk sini sebelum add item")
            }
        }
    }
}

private fun SimpanDanKirimGambar(context: Context, notaBitmap: Bitmap) {
    val cacheNotaPath = File(context.cacheDir, "images")
    cacheNotaPath.mkdirs()
    val fileNota = File(cacheNotaPath, "nota-${System.currentTimeMillis()}.png")
    try {
        val stream = FileOutputStream(fileNota)
        notaBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
    } catch (e: Exception) {
        e.printStackTrace()
        return
    }
    val fileUriNota =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", fileNota)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, fileUriNota)
        //putExtra(Intent.EXTRA_TEXT, "nota pembelianmu")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //setPackage("com.whatsapp")
    }
    context.startActivity(Intent.createChooser(shareIntent, "Bagikan Nota Melalui:"))
}

fun createNotaBitmap(listSetting: List<Setting>, imagebytes: ByteArray, listItem:List<ItemNota>, nota: Nota): Bitmap{
    val lebarBitmap=700
    var tinggiBitmap=0
    val tinggiHeader = 150
    val tinggiFooter = 100
    val tinggiItem = 30
    tinggiBitmap = tinggiHeader + tinggiFooter +(listItem.size* tinggiItem)+500
    val bitmap = Bitmap.createBitmap(lebarBitmap, tinggiBitmap, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(Color.WHITE)
    val paintTeksBesar = Paint().apply {
        color = Color.BLACK
        textSize = 24f
        textAlign = Paint.Align.CENTER
        typeface= Typeface.DEFAULT_BOLD
    }
    val paintTeksKecil = Paint().apply {
        color = Color.BLACK
        textSize = 18f
        textAlign = Paint.Align.LEFT
        typeface = Typeface.MONOSPACE
    }
    val paintTeksTotal = Paint().apply {
        color = Color.BLACK
        textSize = 20f
        textAlign = Paint.Align.RIGHT
        typeface = Typeface.DEFAULT_BOLD
    }
    val paintGaris = Paint().apply {
        color = Color.BLACK
        strokeWidth= 2f
    }
    val paintBitmap = Paint().apply {
        isFilterBitmap=true
        isAntiAlias= true
    }
    //lateinit var imagebytes : ByteArray
    var yPos =30f
    if(listSetting.isNotEmpty()){
        lateinit var setting: Setting
        listSetting.forEach { seting ->
            setting = seting
        }
        if(!setting.uri_logo.contentEquals("kosong")){
            imagebytes?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                val matrix = android.graphics.Matrix()
                matrix.reset()
                val targetwidth =100f
                val targetheight = 50f
                val targetx= 10f
                val targety= 30f
                val scalex = targetwidth/ bitmap.width
                val scaley = targetheight / bitmap.height
                matrix.postScale(scalex,scaley)
                matrix.postTranslate(targetx,targety)
                canvas.drawBitmap(bitmap, matrix,paintBitmap)
            }
        }
        canvas.drawText(setting.nama_toko, lebarBitmap/2f,yPos,paintTeksBesar)
        yPos +=40
        setting.alamat_toko?.let {
            canvas.drawText(it, lebarBitmap/2f,yPos, paintTeksKecil)
            yPos+=30
        }
        val datetime = nota.date_time.replace("GMT+07:00","")
        canvas.drawText("Tanggal:${datetime}", 10f,yPos, paintTeksKecil)
        canvas.drawText("Kepada: ${nota.customer_name}", lebarBitmap-10f, yPos, paintTeksKecil.apply {
            textAlign = Paint.Align.RIGHT
        })
        yPos += 15f
        canvas.drawLine(10f, yPos, lebarBitmap-10f,yPos, paintGaris )
        yPos +=20
        paintTeksKecil.textAlign = Paint.Align.LEFT
        val formater = java.text.DecimalFormat("#,###")
        listItem.forEach { itemNota ->
            canvas.drawText(itemNota.nama_produk, 10f, yPos, paintTeksKecil)
            canvas.drawText(formater.format(itemNota.subtotal), lebarBitmap-10f, yPos, paintTeksTotal.apply {
                textAlign= Paint.Align.RIGHT;textSize=18f
            })
            yPos += 30f
            canvas.drawText(itemNota.qty.toString(), 10f, yPos, paintTeksKecil)
            canvas.drawText("@${formater.format(itemNota.harga_produk)}", 30f, yPos, paintTeksKecil)
            yPos+=30f
        }
        canvas.drawLine(10f, yPos, lebarBitmap-10f, yPos, paintGaris)
        yPos +=20
        canvas.drawText("Total: ", 10f, yPos, paintTeksTotal.apply {
            textAlign= Paint.Align.LEFT;textSize=22f
        })
        canvas.drawText(formater.format(nota.total), lebarBitmap-10f, yPos, paintTeksTotal.apply {
            textAlign= Paint.Align.RIGHT;textSize=24f
        })
        yPos +=40f
        canvas.drawText(setting.catatan_kaki, lebarBitmap/2f, yPos, paintTeksKecil.apply {
            textAlign= Paint.Align.CENTER;textSize=16f
        })
        yPos += 30
    }else{
        val datetime = nota.date_time.replace("GMT+07:00","")
        canvas.drawText("Tanggal:${datetime}", 10f,yPos, paintTeksKecil)
        canvas.drawText("Kepada: ${nota.customer_name}", lebarBitmap-10f, yPos, paintTeksKecil.apply {
            textAlign = Paint.Align.RIGHT
        })
        yPos += 15f
        canvas.drawLine(10f, yPos, lebarBitmap-10f,yPos, paintGaris )
        yPos +=20
        val formater = java.text.DecimalFormat("#,###")
        listItem.forEach { itemNota ->
            canvas.drawText(itemNota.nama_produk, 10f, yPos, paintTeksKecil)
            canvas.drawText(formater.format(itemNota.subtotal), lebarBitmap-10f, yPos, paintTeksTotal.apply {
                textAlign= Paint.Align.RIGHT;textSize=18f
            })
            yPos += 30f
            canvas.drawText(itemNota.qty.toString(), 10f, yPos, paintTeksKecil)
            canvas.drawText("@${formater.format(itemNota.harga_produk)}", 30f, yPos, paintTeksKecil)
            yPos += 30f
        }
        canvas.drawLine(10f, yPos, lebarBitmap-10f, yPos, paintGaris)
        yPos +=20
        canvas.drawText("Total: ", 10f, yPos, paintTeksTotal.apply {
            textAlign= Paint.Align.LEFT;textSize=22f
        })
        canvas.drawText(formater.format(nota.total), lebarBitmap-10f, yPos, paintTeksTotal.apply {
            textAlign= Paint.Align.RIGHT;textSize=24f
        })
        yPos +=40f
    }
    val finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, lebarBitmap, yPos.toInt()+10)
    return finalBitmap
}