package com.example.thenotes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.thenotes.data.AppContainer
import com.example.thenotes.data.Customer
import com.example.thenotes.data.Nota
import com.example.thenotes.ui.theme.TheNotesTheme

class TambahCustomerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val ctx: Activity= this as Activity
        val my_app = application as TheNotes
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheNotesTheme {
                AddCustomerView(ctx, my_app.container)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerView(ctx: Activity, container: AppContainer){
    val context = LocalContext.current
    var text by remember { mutableStateOf("Nama") }
    var initFocusState by remember { mutableStateOf(false) }
    val viewModel = TheNoteCustomerViewModel(container.customerRepository)
    val notaViewModel = NotaViewModel(container)
    val time = notaViewModel.getTime()
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {CenterAlignedTopAppBar(title = {Text("Kepada")},
            navigationIcon = { IconButton(onClick = { ctx.finish()}){
                Icon(Icons.Default.ArrowBackIosNew,contentDescription="back")
            } },
            actions = {
                IconButton(onClick = {
                    if(!text.isEmpty() && !text.contentEquals("Name")){
                        val customer = Customer(nama = text)
                        val id = viewModel.addCustomer(customer)
                        val nota = Nota(0,text, time,0.0)
                        notaViewModel.addNota(nota)
                        Log.d("abcd", "$id")
                        val intent = Intent(context, DetailNota::class.java)
                        intent.putExtra("datetime", nota.date_time)
                        context.startActivity(intent)
                        ctx.finish()
                    }
                }) {
                    Icon(Icons.Default.Save, contentDescription = "tombol save")
                }
            }
        )}
    ) { innerPadding->
        Column(Modifier.padding(innerPadding
        ).padding(10.dp)) {

            TextField(
                value = text,
                onValueChange = { text = it },
                Modifier.onFocusChanged{focusState ->
                    if (focusState.isFocused){
                        if(!initFocusState){
                            text = ""
                            initFocusState=true;
                        }
                    }
                },

                label = {if (initFocusState){
                    Text("Nama")
                }else{
                    Text("")
                }
                }
            )
            //Text(text)
        }
    }
}

