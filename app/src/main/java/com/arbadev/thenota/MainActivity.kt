@file:OptIn(ExperimentalMaterial3Api::class)

package com.arbadev.thenota


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Approval
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.twotone.CollectionsBookmark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotApplyResult
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arbadev.thenota.data.AppContainer
import com.arbadev.thenota.ui.theme.TheNotesTheme
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.arbadev.thenota.data.ItemNota
import com.arbadev.thenota.data.Nota
import com.arbadev.thenota.data.Produk
import com.arbadev.thenota.data.Setting
import com.arbadev.thenota.utils.AnalisaQrCode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Locale
import java.util.zip.GZIPInputStream
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myapp = application as TheNotes
        val activity = this as Activity
        enableEdgeToEdge()
        setContent {
            TheNotesTheme {
                TheNotesApp(myapp.container, activity)
            }
        }
    }
}


@Composable
fun TheNotesApp(appContainer: AppContainer, activity: Activity) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.NOTA) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = ""
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {

        if (currentDestination.label.contentEquals("Nota")) {
            NotesView(
                modifier = Modifier.fillMaxSize(), container = appContainer
            )
        } else if (currentDestination.label.contentEquals("Produk")) {
            ListProduct(modifier = Modifier.fillMaxSize(), appContainer)
        } else if (currentDestination.label.contentEquals("Settings")) {
            Setting(
                appContainer
            )
        }else if (currentDestination.label.contentEquals("Scan")){
            QRScanner(appContainer, activity)
        }

    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    NOTA("Nota", Icons.AutoMirrored.Filled.Note),
    PRODUK("Produk", Icons.TwoTone.CollectionsBookmark),
    SETTINGS("Settings", Icons.Default.Settings),
    SCAN(label = "Scan", Icons.Default.QrCodeScanner),
}


@Composable
fun Setting(container: AppContainer) {
    val context = LocalContext.current
    val settingViewModel = SettingViewModel(container.settingRepository)
    val settingList = settingViewModel.settingList.collectAsState(listOf<Setting>())
    var nama_toko by remember { mutableStateOf("") }
    var is_firstrun by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    var catatan_kaki by remember { mutableStateOf("**Terima Kasih.**") }
    var alamat_toko by remember {mutableStateOf("")}
    var uri_logo by remember { mutableStateOf("kosong") }
    var imagebytes by remember { mutableStateOf<ByteArray?>(null) }
    val launcherActivityResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {uri->
        if(uri!=null) {
            coroutineScope.launch {
                withContext(Dispatchers.IO){
                    context.contentResolver.takePersistableUriPermission(
                        uri!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    uri_logo = uri.toString()
                    val itemGambar = context.contentResolver.openInputStream(uri!!)
                    imagebytes = itemGambar?.readBytes()
                    itemGambar?.close()
                }
            }
        }
    }
    Scaffold(Modifier.fillMaxSize(), {
        CenterAlignedTopAppBar(
            { Text("Settings", fontSize = 18.sp) },
            actions = {IconButton(onClick = {
                if(settingList.value.isNotEmpty()) {
                    val intent = Intent(context, ProdukShareQrCodeActivity::class.java)
                    intent.putExtra("name_info","setting")
                    context.startActivity(intent)
                }
            }) {
                Icon(Icons.Default.QrCode2,"create Qrcode")
            }
                IconButton(onClick = {
                    if (settingList.value.isNotEmpty()) {
                        var setting = Setting(0, "", "","","")
                        settingList.value.forEach { seting ->
                            setting = seting
                        }
                        setting.nama_toko = nama_toko
                        setting.catatan_kaki = catatan_kaki
                        setting.alamat_toko = alamat_toko
                        setting.uri_logo = uri_logo
                        settingViewModel.editSetting(setting)
                    } else {
                        val setting = Setting(0, nama_toko, alamat_toko, uri_logo, catatan_kaki)
                        settingViewModel.addSetting(setting)
                    }
                }) {
                    Icon(Icons.Default.Save, contentDescription = "simpan")
                }
            })
    }
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth()
        Column(modifier.padding(10.dp)) {
            if (settingList.value.isNotEmpty()) {
                if (is_firstrun) {
                    settingList.value.forEach {
                        nama_toko = it.nama_toko
                        catatan_kaki = it.catatan_kaki
                        alamat_toko = it.alamat_toko
                        uri_logo = it.uri_logo
                        if(!uri_logo.contentEquals("kosong")){
                            coroutineScope.launch {
                                withContext(Dispatchers.IO){
                                    try {
                                        val itemGambar =
                                            context.contentResolver.openInputStream(uri_logo.toUri())
                                        imagebytes = itemGambar?.readBytes()
                                        itemGambar?.close()
                                    }catch(e: Exception){
                                        //Toast.makeText(context, "error akses gambar bermasalah ganti gambar ke lokasi baru",
                                          //  Toast.LENGTH_SHORT).show()
                                        e.printStackTrace()

                                    }
                                }
                            }
                        }
                    }
                    is_firstrun = false
                }
                TextField(
                    value = nama_toko,
                    onValueChange = { nama_toko = it },
                    Modifier
                        .fillMaxWidth().padding(bottom = 20.dp)
                        ,
                    label = { Text("Nama Toko") }
                )
                TextField(
                    value = alamat_toko,
                    onValueChange = { alamat_toko = it },
                    Modifier
                        .fillMaxWidth().padding(bottom = 20.dp)
                        ,
                    label = { Text("Alamat") }
                )
                TextField(
                    value = catatan_kaki,
                    onValueChange = { catatan_kaki = it },
                    Modifier
                        .fillMaxWidth().padding(bottom = 20.dp)
                        ,

                    label = { Text("Catatan Kaki") }
                )
                IconButton(onClick = {
                    launcherActivityResult.launch(arrayOf("image/*"))
                }) {
                    Icon(Icons.Default.AddAPhoto,"add a picture")
                }
                imagebytes?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    Image(bitmap.asImageBitmap(), "your logo")
                }
            } else {
                TextField(
                    value = nama_toko,
                    onValueChange = { nama_toko = it },
                    Modifier.padding(bottom = 20.dp)
                        .fillMaxWidth()
                        ,

                    label = { Text("Nama Toko") }
                )
                TextField(
                    value = alamat_toko,
                    onValueChange = { alamat_toko = it },
                    Modifier.padding(bottom = 20.dp)
                        .fillMaxWidth()
                        ,
                    label = { Text("Alamat") }
                )
                TextField(
                    value = catatan_kaki,
                    onValueChange = { catatan_kaki = it },
                    Modifier.padding(bottom = 20.dp)
                        .fillMaxWidth()
                        ,

                    label = { Text("Catatan Kaki") }
                )
                IconButton(onClick = {
                   launcherActivityResult.launch(arrayOf("image/*"))
                }) {
                    Icon(Icons.Default.AddAPhoto,"add a picture")
                }
                imagebytes?.let {
                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    Image(bitmap.asImageBitmap(), "your logo")
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "AutoboxingStateCreation")
@Composable
fun ListProduct(modifier: Modifier = Modifier, container: AppContainer) {
    val context = LocalContext.current
    val produkViewModel = ProdukViewModel(container.produkRepository)
    val produklist = produkViewModel.getAllProdukStream.collectAsState(listOf<Produk>())
    Scaffold(
        modifier,
        topBar = { CenterAlignedTopAppBar({ Text("Produk", fontSize = 18.sp) },
            actions = {
                IconButton(onClick = {
                    if(produklist.value.isNotEmpty()) {
                        val intent = Intent(context, ProdukShareQrCodeActivity::class.java)
                        intent.putExtra("name_info","produk")
                        context.startActivity(intent)
                    }
                }) {
                    Icon(Icons.Default.QrCode2,"create Qrcode")
                }
            }) },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = {
                val intent = Intent(context, AddProdukActivity::class.java)
                intent.putExtra("name", "produk")
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Add, "floating button")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            if (produklist.value.isNotEmpty()) {
                val onDelete: (produk: Produk) -> Unit = { produk ->
                    produkViewModel.deleteProduk(produk)
                }
                LazyColumn {
                    items(produklist.value) { produk ->
                        var is_show_icon by remember { mutableStateOf(false) }
                        var offset_x by remember { mutableStateOf(0f) }
                        Row(Modifier.padding(10.dp).fillMaxWidth()
                            .clickable {
                                val intent = Intent(context, EditItemProduk::class.java)
                                intent.putExtra("name", "produk")
                                intent.putExtra("id", produk.id)
                                context.startActivity(intent)
                            }
                            .offset { IntOffset(offset_x.roundToInt(), 0) }
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragStart = {
                                        is_show_icon = false
                                    },
                                    onDragEnd = {
                                        if (is_show_icon) {
                                            is_show_icon = false
                                            onDelete(produk)
                                            offset_x = 0f
                                        }
                                    },
                                    onDragCancel = {
                                        is_show_icon = false
                                        offset_x = 0f
                                    }) { change, dragAmount ->
                                    change.consume()
                                    offset_x += dragAmount
                                    if (offset_x.roundToInt() >= 10) {
                                        is_show_icon = true
                                    } else {
                                        is_show_icon = false
                                        offset_x = 0f
                                    }
                                }
                            }) {
                            if (is_show_icon) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .background(Color.Red)
                                        .padding(end=10.dp)
                                )
                            }

                            Text(
                                produk.nama_produk,
                                Modifier.weight(0.6f),
                                fontSize = 20.sp,
                                textAlign = TextAlign.Start
                            )
                            produk.unit_produk?.let {
                                Text(
                                    it,
                                    Modifier.weight(0.2f),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Start
                                )
                            }?:run {
                                Text(
                                    "",
                                    Modifier.weight(0.2f),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Start
                                )
                            }
                            val textformat = java.text.DecimalFormat("#,###")
                            Text(text = textformat.format(produk.harga_produk),
                                Modifier.weight(0.2f),
                                fontSize = 20.sp,
                                textAlign = TextAlign.End
                            )

                        }
                        HorizontalDivider(Modifier.fillMaxWidth())
                    }
                }
            } else {
                Text("produk belum ditambahkan")
            }
        }
    }
}

@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter",
    "LocalContextConfigurationRead", "AutoboxingStateCreation"
)
@Composable
fun NotesView(modifier: Modifier = Modifier, container: AppContainer) {
    val context = LocalContext.current
    val notaViewModel = NotaViewModel(container)
    val itemNotaViewModel = ItemNotaViewModel(container.itemNotaRepository)
    val notalist = notaViewModel.allNotaStream.collectAsState(emptyList())
    Scaffold(
        modifier = modifier.fillMaxWidth(),
        {
            CenterAlignedTopAppBar(
                title = { Text(text = "Nota", fontSize = 18.sp) },

                )
        },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = {
                context.startActivity(
                    Intent(
                        context,
                        TambahCustomerActivity::class.java
                    )
                )
            }) {
                Icon(Icons.Default.Add, "")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            if (notalist.value.isNotEmpty()) {
                val onDelete: (nota: Nota) -> Unit = { nota ->
                    notaViewModel.deleteNota(nota)
                }
                LazyColumn {
                    items(notalist.value) { nota ->
                        var is_show_text by remember { mutableStateOf(false) }
                        var offset_x by remember { mutableStateOf(0f) }
                        Row(Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, top = 5.dp, bottom = 15.dp, end = 20.dp)
                            .clickable {
                                val intent = Intent(context, DetailNota::class.java)
                                intent.putExtra("datetime", nota.date_time)
                                context.startActivity(intent)
                            }
                            .offset { IntOffset(offset_x.roundToInt(), 0) }
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragStart = {
                                        is_show_text = false
                                    },
                                    onDragEnd = {
                                        if (is_show_text) {
                                            is_show_text = false
                                            onDelete(nota)
                                            offset_x = 0f
                                        }
                                    },
                                    onDragCancel = {
                                        is_show_text = false
                                        offset_x = 0f
                                    }) { change, dragAmount ->
                                    change.consume()
                                    offset_x += dragAmount
                                    if (offset_x.roundToInt() >= 10) {
                                        is_show_text = true
                                    } else {
                                        is_show_text = false
                                        offset_x = 0f
                                    }
                                }
                            }){
                            if (is_show_text) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .background(Color.Red)
                                        .padding(10.dp)
                                )
                            }
                            Column(Modifier.weight(0.8f).padding(end = 10.dp)) {
                                Text(nota.customer_name, fontSize = 20.sp)
                                val idtf = java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                                val dtf = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                                val time = dtf.format(idtf.parse(nota.date_time))
                                Text(time, Modifier.clickable{
                                    val intent = Intent(context, GantiTanggalActivity::class.java)
                                    intent.putExtra("datetime", nota.date_time)
                                    intent.putExtra("id",nota.id)
                                    context.startActivity(intent)
                                }, fontSize = 20.sp)
                            }
                            val formatter = java.text.DecimalFormat("#,###")
                            if (nota.total.isNaN()) {
                                val listItem = itemNotaViewModel.getItemByNotaIdStream(nota.id)
                                    .collectAsState(listOf<ItemNota>()).value
                                nota.total = notaViewModel.calculateTotal(listItem)
                            }
                            Text(formatter.format(nota.total),Modifier.weight(0.2f), fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)


                        }
                        HorizontalDivider(Modifier.fillMaxWidth())

                    }
                }
            } else {
                Text("Belum ada Nota")
            }
        }
    }
}

@Composable
fun QRScanner(appContainer: AppContainer,activity: Activity){
    val context = LocalContext.current
    //val haptic = LocalHapticFeedback.current
    var showDialog by remember{mutableStateOf(false)}
    var tableData by remember { mutableStateOf("data") }
    val produkVM = ProdukViewModel(appContainer.produkRepository)
    val settingVM = SettingViewModel(appContainer.settingRepository)
    val lifeCycleOw = LocalLifecycleOwner.current
    val cameraProviderFuture = remember{
        ProcessCameraProvider.getInstance(context)
    }
    var isFlashOn by remember { mutableStateOf(false) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var scanStatus by remember { mutableStateOf("SCANNING") }
    var radarColor = when(scanStatus){
        "SUCCESS" -> Color.Green
        "ERROR" -> Color.Red
        else -> Color.Blue
    }
    var hasPermission by remember{mutableStateOf(
        ContextCompat.checkSelfPermission(context,
            Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
    )}
    val  camperL = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {granted ->
            hasPermission =granted
        }
    )
    val launcherG = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
        {uri ->
            uri?.let {
                val image = InputImage.fromFilePath(context, it)
                val scanner = BarcodeScanning.getClient()
                scanner.process(image).addOnSuccessListener {barcodes ->
                    if(barcodes.isNotEmpty()){
                        val rawBytes = barcodes[0].rawBytes
                        try {
                            val json = decodeFromQr(rawBytes!!)
                            val jsob = JSONObject(json)
                            val table = jsob.getString("t")
                            tableData = table
                            if (table.contentEquals("produk")) {
                                Toast.makeText(context, "memasukkan data ke ${table}", Toast.LENGTH_SHORT).show()
                                val listProduk = produkVM.deserJson(json)
                                listProduk.forEach { produk ->
                                    produkVM.addProduk(produk)
                                }
                            } else if (table.contentEquals("setting")) {
                                Toast.makeText(context, "memasukkan data ke ${table}", Toast.LENGTH_SHORT).show()
                                val setting = settingVM.toDataSetting(json)
                                settingVM.addSetting(setting)
                            }
                            showDialog = true
                        }catch (e: Exception){
                            Toast.makeText(context, "qrcode lain",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context, "qrcode tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, "gagal mengambil gambar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
    Scaffold(Modifier.fillMaxSize(),
        topBar = {CenterAlignedTopAppBar({
            Text("Scan untuk import data")},
            actions = {
                IconButton(onClick = {
                    launcherG.launch("image/*")
                }) {
                    Icon(Icons.Default.Photo,"photo")
                }
                IconButton(onClick = {
                    isFlashOn = !isFlashOn
                    cameraControl?.enableTorch(isFlashOn)
                }) {
                    Icon(imageVector = if(isFlashOn)Icons.Filled.FlashOn else Icons.Filled.FlashOff,"photo", tint = if(isFlashOn) Color.Yellow else Color.Black)
                }
            })
        }) {innerPadding ->
        val modifier = Modifier.padding(innerPadding).fillMaxWidth().fillMaxHeight()
        LaunchedEffect(Unit) {
            if(!hasPermission){
                camperL.launch(Manifest.permission.CAMERA)
            }
        }
        if(showDialog){
            AlertDialog(
                onDismissRequest = {
                    showDialog=false
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    activity.finish()
                },
                confirmButton = {
                    androidx.compose.material3.Button(onClick = {
                        showDialog = false
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        activity.finish()
                    }) {
                        Text("Ok")
                    }
                },
                title = {Text("Impor Data.")},
                text = {Text("berhasil impor ${tableData}.")},
            )
        }
        if(hasPermission){
            Column(modifier){
                Box(Modifier.fillMaxWidth().padding(10.dp), contentAlignment = Alignment.Center) {
                    AndroidView(factory = {ctx->
                        val preview = PreviewView(ctx)
                        val executor = ContextCompat.getMainExecutor(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider= cameraProviderFuture.get()
                            val previ = Preview.Builder().build().also {
                                it.setSurfaceProvider(preview.surfaceProvider)
                            }
                            val imageAnali = ImageAnalysis.Builder().setBackpressureStrategy(
                                ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                            ).build().also {
                                it.setAnalyzer(executor, AnalisaQrCode{qrContent ->
                                    try {
                                        val json = decodeFromQr(qrContent)
                                        val jsob = JSONObject(json)
                                        val table = jsob.getString("t")
                                        tableData = table
                                        //haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        scanStatus = "SUCCESS"
                                        Log.d("MainActivity","masuk")
                                        Log.d("MainActivity","pak")
                                        Log.d("MainActivity","eko")
                                        Log.d("MainActivity","masuk")
                                        Log.d("MainActivity","pak")
                                        Log.d("MainActivity","eko")
                                        Log.d("MainActivity","masuk")
                                        Log.d("MainActivity","pak")
                                        Log.d("MainActivity","eko")
                                        Log.d("MainActivity","masuk")
                                        Log.d("MainActivity","pak")
                                        Log.d("MainActivity","eko")
                                        Log.d("MainActivity","masuk")
                                        Log.d("MainActivity","pak")
                                        Log.d("MainActivity","eko")
                                        if (table.contentEquals("produk")) {
                                            Toast.makeText(ctx, "memasukkan data ke ${table}", Toast.LENGTH_SHORT).show()
                                            val listProduk = produkVM.deserJson(json)
                                            listProduk.forEach { produk ->
                                                produkVM.addProduk(produk)
                                            }
                                        } else if (table.contentEquals("setting")) {
                                            Toast.makeText(ctx, "memasukkan data ke ${table}", Toast.LENGTH_SHORT).show()
                                            val setting = settingVM.toDataSetting(json)
                                             settingVM.addSetting(setting)
                                        }
                                        showDialog = true
                                        Log.d("MainActivity", "${showDialog}")
                                    }catch (e: Exception){
                                        //haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        scanStatus = "ERROR"
                                        Toast.makeText(ctx, "qrcode lain",Toast.LENGTH_SHORT).show()
                                        Log.d("MainActivity","masuk")
                                        Log.d("MainActivity","pak")
                                        Log.d("MainActivity","eko")
                                        Log.d("MainActivity","masuk")
                                        Log.d("MainActivity","pak")
                                        Log.d("MainActivity","eko")
                                        Log.d("MainActivity","masuk")
                                        Log.d("MainActivity","pak")
                                        Log.d("MainActivity","eko")
                                        Log.d("MainActivity","masuk")
                                        Log.d("MainActivity","pak")
                                        Log.d("MainActivity","eko")
                                        Log.d("MainActivity","masuk")
                                        Log.d("MainActivity","pak")
                                        Log.d("MainActivity","eko")
                                    }finally{

                                        scanStatus="SCANNING"

                                    }
                                })
                            }
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            try{
                                cameraProvider.unbindAll()
                               val camera= cameraProvider.bindToLifecycle(
                                    lifeCycleOw,
                                    cameraSelector,
                                    previ,
                                    imageAnali
                                )
                                cameraControl= camera.cameraControl
                                Log.d("MainActivity","${cameraControl.hashCode()}")
                            }catch (e: Exception){
                                e.printStackTrace()
                            }
                        },executor)
                        preview
                    }, Modifier.fillMaxWidth().height(40.dp))

                    Box(modifier = Modifier.size(250.dp)
                        .align(Alignment.Center)
                        .border(2.dp,
                            color = Color.White.copy(0.4f))){
                        ScanningAnimation(Modifier.fillMaxSize(),radarColor,scanStatus)
                    }
                }
            }
        }else{
            Box(modifier, contentAlignment = Alignment.Center){
                    IconButton(onClick = {
                        camperL.launch(Manifest.permission.CAMERA)
                    }) {
                        Box(Modifier.align(Alignment.Center)){
                            Icon(Icons.Default.Approval,"approval")
                            Icon(Icons.Default.Camera,"camera")
                        }
                    }

            }
        }
    }
}
@Composable
fun ScanningAnimation(modifier: Modifier, radarColor: Color, scanStatus: String){
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val fraction by infiniteTransition.animateFloat(0f,1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = if(scanStatus.contentEquals("SCANNING"))2000 else 5000, easing = LinearEasing),
            RepeatMode.Reverse),
        label = "Line Position"
    )
    Canvas(modifier) {
        val width = size.width
        val height = size.height
        val currentY = height * fraction
        drawLine(radarColor,
            Offset(0f, currentY),
            Offset(width, currentY),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawRect(brush = Brush.verticalGradient(colors = listOf(Color.Gray.copy(0.4f),Color.Transparent),
            startY = currentY,
            endY = currentY + 40.dp.toPx() ),
            topLeft = Offset(0f, currentY),
            size = Size(width, 40.dp.toPx())
        )
    }
}
fun decodeFromQr(scannedbytes: ByteArray): String{
    return GZIPInputStream(scannedbytes.inputStream()).bufferedReader().use {
        it.readText()
    }
}
