@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.thenotes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.thenotes.data.AppContainer
import com.example.thenotes.data.Customer
import com.example.thenotes.data.ItemNota
import com.example.thenotes.data.Nota
import com.example.thenotes.data.Produk
import com.example.thenotes.ui.theme.TheNotesTheme
import kotlin.math.roundToInt

class DetailNota : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var datetime: String? = "test"
        datetime = intent.getStringExtra("datetime")
        val ctx: Activity = this as Activity
        val myApp = application as TheNotes
        enableEdgeToEdge()
        setContent {
            TheNotesTheme {
                ViewDetailNota(datetime.toString(), myApp.container, ctx)
            }
        }
    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun ViewDetailNota(datetime: String, container: AppContainer, ctx: Activity) {
    var id = 0
    val context = LocalContext.current
    //val viewModel = TheNoteCustomerViewModel(container.customerRepository)
    val notaViewModel = NotaViewModel(container)
    val detailNota: ItemNotaViewModel = ItemNotaViewModel(container.itemNotaRepository)
    //var dataDb = viewModel.getCustomerById(nama).collectAsState(Customer(0,nama))
    val dataNota =
        notaViewModel.getNotaByDatetimeStream(datetime).collectAsState(Nota(0, "", "", 0.0))
    id = dataNota.value.id
    val name = dataNota.value.customer_name
    val listItemNota = detailNota.getItemByNotaIdStream(id).collectAsState(emptyList())


    Scaffold(
        Modifier.fillMaxSize(), topBar = {
            CenterAlignedTopAppBar(
                { Text(name) },
                navigationIcon = {
                    IconButton(onClick = { ctx.finish() }) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "back")
                    }
                },
                actions = {
                    IconButton({
                        val intent = Intent(context, ShareActivity::class.java).apply {
                            putExtra("id", id)
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Default.Preview, "preview")
                    }
                }
            )
        },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = {
                val intent = Intent(context, AddProdukActivity::class.java)
                intent.putExtra("name", name)
                intent.putExtra("datetime", dataNota.value.date_time)
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.Add, contentDescription = "tambah")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .fillMaxWidth()
        Column(modifier) {
            if (listItemNota.value.isNotEmpty()) {
                dataNota.value.total = notaViewModel.calculateTotal(listItemNota.value)
                notaViewModel.updateNota(dataNota.value)
                val onDelete: (itemNota: ItemNota) -> Unit = { itemNota ->
                    detailNota.deleteItem(itemNota)
                }
                LazyColumn {
                    items(listItemNota.value) { itemNota ->
                        var is_show_icon by remember { mutableStateOf(false) }
                        var offset_x by remember { mutableStateOf(0f) }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, top = 5.dp, bottom = 15.dp, end = 20.dp)
                                .clickable {
                                    val intent = Intent(context, EditItemProduk::class.java)
                                    intent.putExtra("name", name)
                                    intent.putExtra("id", itemNota.id)
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
                                                onDelete(itemNota)
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
                                        .padding(10.dp)
                                )
                            }

                            val formatter = java.text.DecimalFormat("#,###")
                            Column(Modifier.weight(0.8f).padding(end=10.dp)) {
                                Text(itemNota.nama_produk, fontSize = 20.sp)
                                Row {
                                    Text(itemNota.qty.toString(), fontSize = 20.sp)
                                    Spacer(Modifier.width(20.dp))
                                    Text(
                                        formatter.format(itemNota.harga_produk),
                                        fontSize = 20.sp
                                    )
                                }
                            }
                            Text(formatter.format(itemNota.subtotal),Modifier.weight(0.2f), fontSize = 20.sp)
                        }
                    }

                }
            } else {
                Text("masih kosong")
            }
        }
    }
}
