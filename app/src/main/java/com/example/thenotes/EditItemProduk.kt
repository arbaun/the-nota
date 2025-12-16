package com.example.thenotes

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        .collectAsState(ItemNota(0, 0, "", 0.0, 0, 0.0)).value
    var selectedText by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("Nama Produk") }
    var textHarga by remember { mutableStateOf("0") }
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
                                itemNota.qty = textQty.toInt()
                                itemNota.subtotal = textSubtotal.toDouble()
                                itemNotaViewModel.updateItem(itemNota)
                            } else {
                                if (text.contentEquals(p_name)) {
                                    itemNota.qty = textQty.toInt()
                                    itemNota.harga_produk = textHarga.toDouble()
                                    itemNota.subtotal = textSubtotal.toDouble()
                                    itemNotaViewModel.updateItem(itemNota)
                                } else {
                                    val produk = Produk(
                                        id = 0, text,
                                        textHarga.toDouble()
                                    )
                                    viewModelProduk.addProduk(produk)
                                    itemNota.nama_produk = text
                                    itemNota.harga_produk = textHarga.toDouble()
                                    itemNota.qty = textQty.toInt()
                                    itemNota.subtotal = textSubtotal.toDouble()
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
                        if (text.length >= 3) {
                            filteredSugestion = sugestions.value.filter {
                                it.nama_produk.contains(
                                    text,
                                    ignoreCase = true
                                )
                            }
                            expanded = filteredSugestion.isNotEmpty()
                        }
                    },
                    Modifier
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                if (!initFocusState) {
                                    text = ""
                                    initFocusState = true;
                                }
                                text=""
                            }
                        }
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable)
                        .focusRequester(focus),

                    label = {
                        if (initFocusState) {
                            Text("Nama Produk")
                        } else {
                            Text("Nama Produk")
                        }
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded,
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable)
                        )
                    }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false

                    }, Modifier.fillMaxWidth(),
                    properties = androidx.compose.ui.window.PopupProperties(focusable = false)
                ) {
                    filteredSugestion.forEach { sugestions ->
                        DropdownMenuItem(
                            text = { Text(sugestions.nama_produk) },
                            onClick = {
                                text = sugestions.nama_produk
                                selectedText = sugestions.nama_produk

                                textQty = 1.toString()
                                textHarga = sugestions.harga_produk.toString()
                                textSubtotal = formatter.format(sugestions.harga_produk)

                                expanded = false
                                focus.freeFocus()
                            })
                    }
                }
            }

            TextField(
                value = textHarga,
                onValueChange = {
                    textHarga = it
                    if (textHarga.isNotEmpty() && textQty.isNotEmpty()) {

                        var subtotal = textHarga.toDouble() * textQty.toInt()
                        textSubtotal = formatter.format(subtotal)

                    } else {
                        textSubtotal = "0"
                    }
                },
                Modifier
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            if (!initFocusHargaState) {
                                textHarga = ""
                                initFocusHargaState = true
                            }
                        } else {
                            if (textHarga.isEmpty()) {
                                textHarga = "0"
                            }
                        }

                    }
                    .padding(10.dp),
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
            Row(modifier.padding(10.dp)) {
                IconButton(onClick = {
                    var qtye = textQty.toInt()
                    if (qtye >= 1) {
                        qtye -= 1
                        textQty = qtye.toString()
                        if (textHarga.isNotEmpty() && textQty.isNotEmpty()) {

                            var subtotal = textHarga.toDouble() * textQty.toInt()
                            textSubtotal = formatter.format(subtotal)

                        } else {
                            textSubtotal = "0"
                        }
                    }
                }) {
                    Icon(Icons.Default.Minimize, contentDescription = "min button")
                }
                TextField(
                    value = textQty,
                    onValueChange = {
                        textQty = it
                        if (textHarga.isNotEmpty() && textQty.isNotEmpty()) {

                            var subtotal = textHarga.toDouble() * textQty.toInt()
                            textSubtotal = formatter.format(subtotal)

                        } else {
                            textSubtotal = "0"
                        }
                    },
                    Modifier.onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            textQty = ""
                        } else {
                            textQty = "0"
                        }
                    },
                    label = { Text("Qty") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                IconButton(onClick = {
                    var qtye = textQty.toInt()
                    qtye += 1
                    textQty = qtye.toString()
                    if (textHarga.isNotEmpty() && textQty.isNotEmpty()) {
                        var subtotal = textHarga.toDouble() * textQty.toInt()
                        textSubtotal = formatter.format(subtotal)

                    } else {
                        textSubtotal = "0"
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "add button")
                }

            }

            Text(textSubtotal, Modifier.fillMaxWidth())

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProduk(name: String, ctx: Activity, container: AppContainer, id: Int) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("Nama Produk") }
    var textHarga by remember { mutableStateOf("0") }
    var initFocusState by remember { mutableStateOf(false) }
    var initFocusHargaState by remember { mutableStateOf(false) }
    val viewModelProduk = ProdukViewModel(container.produkRepository)
    val produk = viewModelProduk.getProdukById(id).collectAsState(Produk(0, "", 0.0)).value
    text = produk.nama_produk
    textHarga = produk.harga_produk.toString()
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
        var modifier = Modifier.padding(innerPadding)
        Column(modifier.padding(10.dp)) {
            TextField(
                value = text,
                onValueChange = { text = it },
                Modifier
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            if (!initFocusState) {
                                text = ""
                                initFocusState = true;
                            }
                        }
                    }
                    .padding(10.dp),

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
                                textHarga = "0"
                                initFocusHargaState = true
                            }
                        } else {
                            if (textHarga.isEmpty()) {
                                textHarga = "0"
                            }
                        }

                    }
                    .padding(10.dp),
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


        }
    }
}
