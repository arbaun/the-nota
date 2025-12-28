package com.example.thenotes

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thenotes.data.AppContainer
import com.example.thenotes.data.ItemNota
import com.example.thenotes.data.Nota
import com.example.thenotes.data.Produk
import com.example.thenotes.ui.theme.TheNotesTheme

class EditItemProduk : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var nama: String? = "test"
        var id: Int = 0
        val ctx: Activity = this as Activity
        nama = intent.getStringExtra("name")
        id = intent.getIntExtra("id", 0)
        val myApp = application as TheNotes
        enableEdgeToEdge()
        setContent {
            TheNotesTheme {
                if (nama.toString().contentEquals("produk")) {
                    EditProduk("produk", ctx, myApp.container, id)
                } else {
                    EditItem(nama.toString(), ctx, myApp.container, id)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItem(name: String, ctx: Activity, container: AppContainer, id: Int) {
    val context = LocalContext.current
    val formatter = java.text.DecimalFormat("#,###")
    var is_first_run by remember { mutableStateOf(true) }
    val itemNotaViewModel = ItemNotaViewModel(container.itemNotaRepository)
    val itemNota = itemNotaViewModel.getItemNotaByIdStream(id)
        .collectAsState(ItemNota(0, 0, "", 0.0, "",0, 0.0)).value
    var selectedText by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("Nama Produk") }
    var textHarga by remember { mutableStateOf("0") }
    var textUnit by remember { mutableStateOf("") }
    var textQty by remember { mutableStateOf("0") }
    var p_name = itemNota.nama_produk
    var initFocusState by remember { mutableStateOf(false) }
    var initFocusHargaState by remember { mutableStateOf(false) }
    var textSubtotal by remember { mutableStateOf("0") }
    val viewModelProduk = ProdukViewModel(container.produkRepository)
    var sugestions = viewModelProduk.getAllProdukBySearch(text).collectAsState(listOf<Produk>())
    var filteredSugestion by remember { mutableStateOf(sugestions.value) }
    var expanded by remember { mutableStateOf(false) }
    val focus = remember { FocusRequester() }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tambah Item") },
                navigationIcon = {
                    IconButton(onClick = { ctx.finish() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "tombol back")
                    }
                },
                actions = {
                    IconButton(onClick = {

                        if (textQty.isNotEmpty() && textSubtotal.isNotEmpty()
                            && text.isNotEmpty() && textHarga.isNotEmpty() && textQty.isNotEmpty()
                        ) {

                            if (text.contentEquals(selectedText)) {
                                itemNota.nama_produk = text
                                itemNota.harga_produk = textHarga.toDouble()
                                itemNota.unit_produk = textUnit.takeIf { it.isNotBlank() }
                                itemNota.qty = textQty.toInt()
                                itemNota.subtotal = textSubtotal.filter { it.isDigit() }.toDouble()
                                itemNotaViewModel.updateItem(itemNota)
                            } else {
                                if (text.contentEquals(p_name)) {
                                    itemNota.qty = textQty.toInt()
                                    itemNota.harga_produk = textHarga.toDouble()
                                    itemNota.unit_produk = textUnit.takeIf { it.isNotBlank() }
                                    itemNota.subtotal = textSubtotal.filter { it.isDigit() }.toDouble()
                                    itemNotaViewModel.updateItem(itemNota)
                                } else {
                                    val produk = Produk(
                                        id = 0, text,
                                        textHarga.toDouble(),
                                        textUnit.takeIf { it.isNotBlank() }
                                    )
                                    viewModelProduk.addProduk(produk)
                                    itemNota.nama_produk = text
                                    itemNota.harga_produk = textHarga.toDouble()
                                    itemNota.qty = textQty.toInt()
                                    itemNota.subtotal = textSubtotal.filter { it.isDigit() }.toDouble()
                                    itemNotaViewModel.updateItem(itemNota)
                                }
                            }

                        }
                        ctx.finish()
                    }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "tombol simpan")
                    }
                }
            )
        }) { innerPadding ->
        var modifier = Modifier.padding(innerPadding)
        if(itemNota.nama_produk.isNotEmpty()){
            if(is_first_run){
                text = itemNota.nama_produk
                textHarga = itemNota.harga_produk.toString()
                textQty = itemNota.qty.toString()
                textSubtotal = formatter.format(itemNota.subtotal)
                textUnit = itemNota.unit_produk?.let {
                    it
                }?:run{""}
                is_first_run = false
            }
        }
        Column(modifier.padding(10.dp)) {

            ExposedDropdownMenuBox(
                expanded,
                onExpandedChange = { expanded = it }) {
                TextField(
                    value = text,
                    onValueChange = { newValue ->
                        text = newValue
                        if(text.length>=3) {
                            filteredSugestion = sugestions.value.filter {
                                it.nama_produk.contains(
                                    text,
                                    ignoreCase = true
                                )
                            }
                            expanded = filteredSugestion.isNotEmpty()
                        }
                    },
                    Modifier.onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            if (!initFocusState) {
                                initFocusState = true;
                            }
                        }
                    }.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).
                    focusRequester(focus),

                    label = {
                        if (initFocusState) {
                            Text("Nama Produk")
                        } else {
                            Text("Nama Produk")
                        }
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded,
                            modifier= Modifier.menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable) )
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false

                    }, Modifier.fillMaxWidth(),
                    properties = androidx.compose.ui.window.PopupProperties(focusable = false)
                ) {
                    filteredSugestion.forEach { sugestions ->
                        DropdownMenuItem(
                            text = { Text(sugestions.nama_produk) },
                            onClick = {
                                text = sugestions.nama_produk
                                selectedText = sugestions.nama_produk
                                if(textQty.contentEquals("0")){
                                    textQty=1.toString()
                                }
                                textHarga = sugestions.harga_produk.toString()
                                textSubtotal = formatter.format(itemNotaViewModel.calculateSubtotal(textHarga.toDouble(), textQty.toInt()))
                                textUnit = sugestions.unit_produk?.let { it }?:run{""}
                                expanded = false
                                focus.freeFocus()
                            })
                    }
                }
            }

            TextField(
                value = textHarga,
                onValueChange = {
                    textHarga=it
                    if(textHarga.isNotEmpty() && textQty.isNotEmpty()){

                        var subtotal = itemNotaViewModel.calculateSubtotal(textHarga.toDouble(), textQty.toInt())
                        textSubtotal = formatter.format(subtotal)
                    }else{
                        textSubtotal = "0"
                    }
                },
                Modifier.onFocusChanged{focusState ->
                    if (focusState.isFocused){
                        if(!initFocusHargaState){
                            initFocusHargaState=true
                        }
                    }

                }.fillMaxWidth().padding(top=20.dp),
                label = {
                    if (initFocusHargaState){
                        Text("Harga Produk")
                    }else{
                        Text("Harga Produk")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            TextField(
                value = textUnit,
                onValueChange = { textUnit = it },
                Modifier.onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        if (!initFocusState) {
                            initFocusState = true;
                        }
                    }
                }.padding(top=20.dp).fillMaxWidth(),

                label = {
                    if (initFocusState) {
                        Text("Unit")
                    } else {
                        Text("Unit")
                    }
                }
            )
            Row(Modifier.fillMaxWidth().padding(top=20.dp)) {
                IconButton(onClick = {
                    var qtye = textQty.toInt()
                    if(qtye>=1) {
                        qtye -= 1
                        textQty =qtye.toString()
                        if(textHarga.isNotEmpty() && textQty.isNotEmpty()){

                            var subtotal = itemNotaViewModel.calculateSubtotal(textHarga.toDouble(), textQty.toInt())
                            textSubtotal = formatter.format(subtotal)

                        }else{
                            textSubtotal = "0"
                        }
                    }
                }, Modifier.weight(0.1f)) {
                    Icon(Icons.Default.Minimize, contentDescription = "min button")
                }
                TextField(
                    value = textQty,
                    onValueChange = { textQty=it
                        if(textHarga.isNotEmpty() && textQty.isNotEmpty()){

                            var subtotal = itemNotaViewModel.calculateSubtotal(textHarga.toDouble(), textQty.toInt())
                            textSubtotal = formatter.format(subtotal)
                        }else{
                            textSubtotal = "0"
                        }
                    },
                    Modifier.weight(0.3f),
                    label = {Text("Qty")},
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                IconButton(onClick = {
                    var qtye = textQty.toInt()
                    qtye += 1
                    textQty =qtye.toString()
                    if(textHarga.isNotEmpty() && textQty.isNotEmpty()){

                        var subtotal = itemNotaViewModel.calculateSubtotal(textHarga.toDouble(), textQty.toInt())
                        textSubtotal = formatter.format(subtotal)
                    }else{
                        textSubtotal = "0"
                    }
                },Modifier.weight(0.1f)) {
                    Icon(Icons.Default.Add, contentDescription = "plus button")
                }
                Spacer(Modifier.weight(0.5f))
            }

            ElevatedCard(modifier = Modifier.fillMaxWidth().height(50.dp).padding(top=20.dp)) {

                Text(textSubtotal, Modifier.fillMaxWidth(), fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }



        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProduk(name: String, ctx: Activity, container: AppContainer, id: Int) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("Nama Produk") }
    var textHarga by remember { mutableStateOf("0") }
    var textUnit by remember { mutableStateOf("") }
    var initFocusState by remember { mutableStateOf(false) }
    var initFocusHargaState by remember { mutableStateOf(false) }
    val viewModelProduk = ProdukViewModel(container.produkRepository)
    val produk = viewModelProduk.getProdukById(id).collectAsState(Produk(0, "", 0.0, "")).value
    text = produk.nama_produk
    textHarga = produk.harga_produk.toString()
    textUnit = produk.unit_produk?.let{
        it.toString()
    }?:run {
        ""
    }
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tambah Item") },
                navigationIcon = {
                    IconButton(onClick = { ctx.finish() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "tombol back")
                    }
                },
                actions = {
                    IconButton(onClick = {

                        if (text.isNotEmpty() && textHarga.isNotEmpty()) {

                            produk.nama_produk = text
                            produk.harga_produk = textHarga.toDouble()
                            produk.unit_produk = textUnit.takeIf { it.isNotBlank() }
                            viewModelProduk.updateProduk(produk)


                        }

                        ctx.finish()
                    }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "tombol simpan")
                    }
                }
            )
        }) { innerPadding ->
        var modifier = Modifier.padding(innerPadding).fillMaxWidth()
        Column(modifier) {
            TextField(
                value = text,
                onValueChange = { text = it },
                Modifier
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            if (!initFocusState) {
                                initFocusState = true;
                            }
                        }
                    }
                    .padding(10.dp).fillMaxWidth(),

                label = {
                    if (initFocusState) {
                        Text("Nama Produk")
                    } else {
                        Text("Nama Produk")
                    }
                }
            )

            TextField(
                value = textHarga,
                onValueChange = {
                    textHarga = it
                },
                Modifier
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            if (!initFocusHargaState) {
                                initFocusHargaState = true
                            }
                        }

                    }
                    .padding(10.dp).fillMaxWidth(),
                label = {
                    if (initFocusHargaState) {
                        Text("Harga Produk")
                    } else {
                        Text("Harga Produk")
                    }

                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
            TextField(
                value = textUnit,
                onValueChange = { textUnit = it },
                Modifier.onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        if (!initFocusState) {
                            initFocusState = true;
                        }
                    }
                }.padding(top=10.dp).fillMaxWidth(),

                label = {
                    if (initFocusState) {
                        Text("Unit")
                    } else {
                        Text("Unit")
                    }
                }
            )


        }
    }
}
