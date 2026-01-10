@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.arbadev.thenota

import android.app.Activity
import android.icu.util.Calendar
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.arbadev.thenota.data.AppContainer
import com.arbadev.thenota.data.Nota
import com.arbadev.thenota.ui.theme.TheNotesTheme
import java.util.Locale

class GantiTanggalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var datetime: String? = "test"
        datetime = intent.getStringExtra("datetime")
        val id = intent.getIntExtra("id", 0)
        val ctx: Activity = this as Activity
        val myApp = application as TheNotes
        enableEdgeToEdge()
        setContent {
            TheNotesTheme {
                GantiDate(datetime.toString(), myApp.container, ctx,id)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GantiDate(dateStr:String, container: AppContainer, ctx: Activity, id:Int){
    val notaViewModel = NotaViewModel(container)
    var dateText by remember { mutableStateOf(dateStr) }
    var dataNota =
        notaViewModel.getNotaByIdStream(id).collectAsState(Nota(0, "", "", 0.0))
    val idtf = java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
    val cal = remember{
        Calendar.getInstance()
    }
    val dateInMilis = remember(dateStr) {
        try{
            idtf.parse(dateStr)?.time?: System.currentTimeMillis()
        }catch (e: Exception){
            System.currentTimeMillis()
        }
    }
    Scaffold(Modifier.fillMaxSize(), topBar = {
        CenterAlignedTopAppBar({Text("Ganti Tanggal")},
            navigationIcon = {
                IconButton(onClick = { ctx.finish() }) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "back")
                }
            },
            actions = {
                IconButton(onClick = {
                    if(dataNota.value.id>=0) {
                        val dates = dateText
                        if(dates.isNotEmpty()&& dates!="null") {
                            dataNota.value.date_time = dates
                            notaViewModel.updateNota(dataNota.value)
                            ctx.finish()
                        }
                    }
                }) {
                    Icon(Icons.Default.Save,"simpan")
                }
            })
    }) {innerPadding ->
        val modifier = Modifier.fillMaxWidth().padding(innerPadding)
        Column(modifier) {
            Text(dateText.split(" ")[2],Modifier.fillMaxWidth().padding(bottom = 30.dp))
            AndroidView(factory = {context->
                val themedContext = ContextThemeWrapper(context,R.style.WhiteCalendarBlackText)
                android.widget.CalendarView(themedContext).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                    date = dateInMilis
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        cal.set(year, month, dayOfMonth)
                        dateText= cal.time.toString()
                    }
                }
            }, modifier = Modifier.fillMaxWidth().background(Color.White))
        }
    }
}