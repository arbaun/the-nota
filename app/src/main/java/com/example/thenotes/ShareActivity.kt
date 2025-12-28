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
import android.graphics.text.LineBreaker
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
import java.util.Locale
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

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
    var scale by remember { mutableStateOf(1f) }
    var offset_x by remember { mutableStateOf(0f) }
    var offset_y by remember { mutableStateOf(0f) }
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
        val modifier  = Modifier.padding(innerPadding).fillMaxSize()
        if(listItemNota.isNotEmpty()) {
            Column{
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
                                notaBitmap = createNotaBitmap(listSetting = settinglist,imagebytes!!, listItemNota, nota,notaViewModel)
                            }
                        }
                    }else{
                        notaBitmap = createBitmapNoLogo(listSetting = settinglist, listItemNota, nota,notaViewModel)
                    }


                }else{
                    notaBitmap = createBitmapNoLogo(listSetting = settinglist, listItemNota, nota, notaViewModel)
                }
                notaBitmap?.let{
                    Image(
                        it.asImageBitmap(), "nota image",
                        modifier
                            .offset{ IntOffset(offset_x.roundToInt(),offset_y.roundToInt()) }
                            .graphicsLayer{
                                scaleX = scale
                                scaleY = scale
                            }.pointerInput(Unit){
                                detectTransformGestures { _,_,zoom,_->
                                    scale = (scale*zoom).coerceIn(0.5f,3f)
                                }

                            }
                            .pointerInput(Unit){
                                detectDragGestures { _,dragAmount ->
                                    val original = Offset(offset_x, offset_y)
                                    val summed = original + dragAmount
                                    val nilai_offset_baru = Offset(x = summed.x.coerceIn(0f,250.dp.toPx()),
                                        y = summed.y.coerceIn(0f, 250.dp.toPx()))
                                    offset_x = nilai_offset_baru.x
                                    offset_y = nilai_offset_baru.y
                                }
                            }, Alignment.TopStart,
                        contentScale = ContentScale.Fit
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

fun createNotaBitmap(listSetting: List<Setting>, imagebytes: ByteArray, listItem:List<ItemNota>, nota: Nota,notaViewModel: NotaViewModel): Bitmap{
    val lebarBitmap=500
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
        textSize = 20f
        textAlign = Paint.Align.LEFT
        typeface= Typeface.DEFAULT_BOLD
    }
    val paintTeksKecil = Paint().apply {
        color = Color.BLACK
        textSize = 18f
        textAlign = Paint.Align.LEFT
        typeface = Typeface.DEFAULT_BOLD
    }
    val paintTeksTotal = Paint().apply {
        color = Color.BLACK
        textSize = 18f
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
                val targetwidth = 100f
                val targetheight = 100f
                val targetx= 0f
                val targety= 50f
                val scalex = targetwidth/ bitmap.width
                val scaley = targetheight / bitmap.height
                matrix.postScale(scalex,scaley)
                matrix.postTranslate(targetx,targety)
                canvas.drawBitmap(bitmap, matrix,paintBitmap)
            }
        }
        canvas.drawText(setting.nama_toko, 120f,yPos,paintTeksBesar)
        yPos +=30
        setting.alamat_toko?.let {
            val textPaint = TextPaint().apply {
                color =Color.BLACK
                textSize= 18f
                isAntiAlias =true
            }
            val alignmentAlamat = Layout.Alignment.ALIGN_NORMAL
            //val spacingMultiplier = 1.0f
            //val spacingAddition = 0f
            val includePadding = true
            val staticLayout = StaticLayout.Builder.obtain(it, 0, it.length, textPaint, 360)
                .setAlignment(alignmentAlamat)
                .setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE)
                .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL)
                .setIncludePad(includePadding).build()
            canvas.save()
            canvas.translate(120f, yPos)
            staticLayout.draw(canvas)
            canvas.restore()
            yPos+=staticLayout.height+40f
        }
        val dtf = java.text.SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
        val itdf = java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyyy",Locale.ENGLISH)
        //val datetime = nota.date_time.replace("GMT+07:00","")
        canvas.drawText(dtf.format(itdf.parse(nota.date_time)), 10f,yPos, paintTeksKecil)
        yPos += 10f
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(),yPos, paintGaris )
        yPos +=20
        canvas.drawText(nota.customer_name, 10f, yPos, paintTeksKecil.apply {
            textAlign = Paint.Align.LEFT
        })
        yPos += 10f
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(),yPos, paintGaris )
        yPos +=20
        paintTeksKecil.textAlign = Paint.Align.LEFT
        val formater = java.text.DecimalFormat("#,###")
        listItem.forEach { itemNota ->
            canvas.drawText(itemNota.nama_produk, 10f, yPos, paintTeksKecil.apply {
                typeface= Typeface.DEFAULT
            })
            yPos += 30f
            canvas.drawText(formater.format(itemNota.subtotal), lebarBitmap-10f, yPos, paintTeksTotal.apply {
                textAlign= Paint.Align.RIGHT;textSize=18f;typeface= Typeface.DEFAULT_BOLD
            })
            itemNota.unit_produk?.let {
                canvas.drawText(itemNota.qty.toString()+" ${it.toString()} ", 120f, yPos, paintTeksTotal.apply {
                    typeface = Typeface.DEFAULT
                })
            }?:run {
                canvas.drawText(itemNota.qty.toString()+" ", 120f, yPos, paintTeksTotal.apply {
                    typeface = Typeface.DEFAULT
                })
            }
            canvas.drawText("@ ${formater.format(itemNota.harga_produk)}", 240f, yPos, paintTeksTotal)
            yPos+=20f
        }
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(), yPos, paintGaris)
        yPos +=20
        canvas.drawText("TOTAL: ", lebarBitmap/2f, yPos, paintTeksTotal.apply {
            textAlign= Paint.Align.LEFT;textSize=18f;typeface= Typeface.DEFAULT_BOLD
        })
        canvas.drawText(formater.format(nota.total), lebarBitmap-10f, yPos, paintTeksTotal.apply {
            textAlign= Paint.Align.RIGHT;textSize=18f
        })
        yPos+=30f
        canvas.drawText(notaViewModel.calculateTotalItemQty(listItem).toString(), 10f,yPos,paintTeksKecil)
        yPos +=40f
        canvas.drawText(setting.catatan_kaki, 145f, yPos, paintTeksKecil.apply {
            textAlign= Paint.Align.LEFT;textSize=16f
        })
        yPos += 30
    }else{
        val datetime = nota.date_time.replace("GMT+07:00","")
        canvas.drawText(datetime, lebarBitmap/2f,yPos, paintTeksKecil)
        yPos += 15f
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(),yPos, paintGaris )
        yPos +=20
        canvas.drawText(nota.customer_name, lebarBitmap/2f, yPos, paintTeksKecil.apply {
            textAlign = Paint.Align.RIGHT
        })
        yPos += 15f
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(),yPos, paintGaris )
        yPos +=20
        val formater = java.text.DecimalFormat("#,###")
        listItem.forEach { itemNota ->
            canvas.drawText(itemNota.nama_produk, lebarBitmap/2f, yPos, paintTeksKecil.apply {
                typeface= android.graphics.Typeface.DEFAULT
            })
            yPos += 30f
            canvas.drawText(formater.format(itemNota.subtotal), lebarBitmap-10f, yPos, paintTeksTotal.apply {
                textAlign= Paint.Align.RIGHT;textSize=18f
            })
            canvas.drawText(itemNota.qty.toString(), lebarBitmap/2f, yPos, paintTeksKecil)
            canvas.drawText("@${formater.format(itemNota.harga_produk)}", (lebarBitmap/2f)+20f, yPos, paintTeksKecil)
            yPos += 30f
        }
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(), yPos, paintGaris)
        yPos +=20
        canvas.drawText("Total: ", lebarBitmap/2f, yPos, paintTeksTotal.apply {
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

fun createBitmapNoLogo(listSetting: List<Setting>, listItem:List<ItemNota>, nota: Nota, notaViewModel: NotaViewModel): Bitmap{
    val lebarBitmap=500
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
        textSize = 20f
        textAlign = Paint.Align.LEFT
        typeface= Typeface.DEFAULT_BOLD
    }
    val paintTeksKecil = Paint().apply {
        color = Color.BLACK
        textSize = 18f
        textAlign = Paint.Align.LEFT
        typeface = Typeface.DEFAULT_BOLD
    }
    val paintTeksTotal = Paint().apply {
        color = Color.BLACK
        textSize = 18f
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
        canvas.drawText(setting.nama_toko, 120f,yPos,paintTeksBesar)
        yPos +=30
        setting.alamat_toko?.let {
            val textPaint = TextPaint().apply {
                color =Color.BLACK
                textSize= 18f
                isAntiAlias =true
            }
            val alignmentAlamat = Layout.Alignment.ALIGN_NORMAL
            //val spacingMultiplier = 1.0f
            //val spacingAddition = 0f
            val includePadding = true
            val staticLayout = StaticLayout.Builder.obtain(it, 0, it.length, textPaint, 360)
                .setAlignment(alignmentAlamat)
                .setBreakStrategy(LineBreaker.BREAK_STRATEGY_SIMPLE)
                .setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_FULL)
                .setIncludePad(includePadding).build()
            canvas.save()
            canvas.translate(120f, yPos)
            staticLayout.draw(canvas)
            canvas.restore()
            yPos+=staticLayout.height+40f
        }
        val dtf = java.text.SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
        val itdf = java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyyy",Locale.ENGLISH)
        //val datetime = nota.date_time.replace("GMT+07:00","")
        canvas.drawText(dtf.format(itdf.parse(nota.date_time)), 10f,yPos, paintTeksKecil)
        yPos += 10f
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(),yPos, paintGaris )
        yPos +=20
        canvas.drawText(nota.customer_name, 10f, yPos, paintTeksKecil.apply {
            textAlign = Paint.Align.LEFT
        })
        yPos += 10f
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(),yPos, paintGaris )
        yPos +=20
        paintTeksKecil.textAlign = Paint.Align.LEFT
        val formater = java.text.DecimalFormat("#,###")
        listItem.forEach { itemNota ->
            canvas.drawText(itemNota.nama_produk, 10f, yPos, paintTeksKecil.apply {
                typeface= Typeface.DEFAULT
            })
            yPos += 30f
            canvas.drawText(formater.format(itemNota.subtotal), lebarBitmap-10f, yPos, paintTeksTotal.apply {
                textAlign= Paint.Align.RIGHT;textSize=18f;typeface= Typeface.DEFAULT_BOLD
            })
            itemNota.unit_produk?.let {
                canvas.drawText(itemNota.qty.toString()+" ${it.toString()} ", 120f, yPos, paintTeksTotal.apply {
                    typeface = Typeface.DEFAULT
                })
            }?:run {
                canvas.drawText(itemNota.qty.toString()+" ", 120f, yPos, paintTeksTotal.apply {
                    typeface = Typeface.DEFAULT
                })
            }
            canvas.drawText("@ ${formater.format(itemNota.harga_produk)}", 240f, yPos, paintTeksTotal)
            yPos+=20f
        }
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(), yPos, paintGaris)
        yPos +=20
        canvas.drawText("TOTAL: ", lebarBitmap/2f, yPos, paintTeksTotal.apply {
            textAlign= Paint.Align.LEFT;textSize=18f;typeface= Typeface.DEFAULT_BOLD
        })
        canvas.drawText(formater.format(nota.total), lebarBitmap-10f, yPos, paintTeksTotal.apply {
            textAlign= Paint.Align.RIGHT;textSize=18f
        })
        yPos+=30f
        canvas.drawText(notaViewModel.calculateTotalItemQty(listItem).toString(), 10f,yPos,paintTeksKecil)
        yPos +=40f
        canvas.drawText(setting.catatan_kaki, 145f, yPos, paintTeksKecil.apply {
            textAlign= Paint.Align.LEFT;textSize=16f
        })
        yPos += 30
    }else{
        val dtf = java.text.SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
        val itdf = java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyyy",Locale.ENGLISH)
        //val datetime = nota.date_time.replace("GMT+07:00","")
        canvas.drawText(dtf.format(itdf.parse(nota.date_time)), 10f,yPos, paintTeksKecil)
        yPos += 10f
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(),yPos, paintGaris )
        yPos +=20
        canvas.drawText(nota.customer_name, 10f, yPos, paintTeksKecil.apply {
            textAlign = Paint.Align.LEFT
        })
        yPos += 10f
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(),yPos, paintGaris )
        yPos +=20
        paintTeksKecil.textAlign = Paint.Align.LEFT
        val formater = java.text.DecimalFormat("#,###")
        listItem.forEach { itemNota ->
            canvas.drawText(itemNota.nama_produk, 10f, yPos, paintTeksKecil.apply {
                typeface= Typeface.DEFAULT
            })
            yPos += 30f
            canvas.drawText(formater.format(itemNota.subtotal), lebarBitmap-10f, yPos, paintTeksTotal.apply {
                textAlign= Paint.Align.RIGHT;textSize=18f;typeface= Typeface.DEFAULT_BOLD
            })
            itemNota.unit_produk?.let {
                canvas.drawText(itemNota.qty.toString()+" ${it.toString()} ", 120f, yPos, paintTeksTotal.apply {
                    typeface = Typeface.DEFAULT
                })
            }?:run {
                canvas.drawText(itemNota.qty.toString()+" ", 120f, yPos, paintTeksTotal.apply {
                    typeface = Typeface.DEFAULT
                })
            }
            canvas.drawText("@ ${formater.format(itemNota.harga_produk)}", 240f, yPos, paintTeksTotal)
            yPos+=20f
        }
        canvas.drawLine(0f, yPos, lebarBitmap.toFloat(), yPos, paintGaris)
        yPos +=20
        canvas.drawText("TOTAL: ", lebarBitmap/2f, yPos, paintTeksTotal.apply {
            textAlign= Paint.Align.LEFT;textSize=18f;typeface= Typeface.DEFAULT_BOLD
        })
        canvas.drawText(formater.format(nota.total), lebarBitmap-10f, yPos, paintTeksTotal.apply {
            textAlign= Paint.Align.RIGHT;textSize=18f
        })
        yPos+=30f
        canvas.drawText(notaViewModel.calculateTotalItemQty(listItem).toString(), 10f,yPos,paintTeksKecil)
        yPos +=40f
    }
    val finalBitmap = Bitmap.createBitmap(bitmap, 0, 0, lebarBitmap, yPos.toInt()+10)
    return finalBitmap

}