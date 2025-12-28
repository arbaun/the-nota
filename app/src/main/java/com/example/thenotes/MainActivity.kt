@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.thenotes


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.twotone.CollectionsBookmark
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thenotes.data.AppContainer
import com.example.thenotes.ui.theme.TheNotesTheme
import androidx.compose.ui.unit.IntOffset
import androidx.core.net.toUri
import com.example.thenotes.data.ItemNota
import com.example.thenotes.data.Nota
import com.example.thenotes.data.Produk
import com.example.thenotes.data.Setting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myapp = application as TheNotes
        enableEdgeToEdge()
        setContent {
            TheNotesTheme {
                TheNotesApp(myapp.container)
            }
        }
    }
}


@Composable
fun TheNotesApp(appContainer: AppContainer) {
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
            actions = {
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
                                    val itemGambar = context.contentResolver.openInputStream(uri_logo.toUri())
                                    imagebytes = itemGambar?.readBytes()
                                    itemGambar?.close()
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
        topBar = { CenterAlignedTopAppBar({ Text("Produk", fontSize = 18.sp) }) },
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
