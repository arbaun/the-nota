@file:OptIn(ExperimentalMaterial3Api::class)
package com.arbadev.thenota

import android.R.attr.navigationIcon
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arbadev.thenota.data.AppContainer
import com.arbadev.thenota.data.Produk
import com.arbadev.thenota.data.Setting
import com.arbadev.thenota.utils.QrGeneratorJson
import com.arbadev.thenota.ui.theme.TheNotesTheme
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

class ProdukShareQrCodeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx: Activity = this as Activity
        var penanda:String="penanda"
        penanda = intent.getStringExtra("name_info").toString()
        val myApp = application as TheNotes
        enableEdgeToEdge()
        setContent {
            TheNotesTheme {
                if(penanda.contentEquals("produk")) {
                    BitmapQr(ctx, myApp.container)
                }else if(penanda.contentEquals("setting")){
                    SettingQr(ctx,myApp.container)
                }
            }
        }
    }
}

@Composable
fun BitmapQr(activity: Activity, container: AppContainer){
    val produkViewModel = ProdukViewModel(container.produkRepository)
    val produklist = produkViewModel.getAllProdukStream.collectAsState(listOf<Produk>())
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = { CenterAlignedTopAppBar({Text("Export produk database", fontSize = 18.sp)},
            navigationIcon = { IconButton(onClick = {activity.finish()}){
                Icon(Icons.Default.ArrowBackIosNew,"back")
            } }
        ) }
    ){innerPadding ->
        val modifier = Modifier.padding(innerPadding).fillMaxWidth().fillMaxHeight()
        if(produklist.value.isNotEmpty()){
            val qrGeneratorJson = QrGeneratorJson(zipandBase64(produkViewModel.createJsonString(produklist.value)))
            //val qrGeneratorJson = QrGeneratorJson("test doang")
            qrBitmap = qrGeneratorJson.generate(500)
            Column(modifier.background(Color.White)) {
                qrBitmap?.let {
                    Image(it.asImageBitmap(),"qrcode",
                        Modifier.padding(bottom = 20.dp,top= 40.dp).fillMaxWidth(), contentScale = ContentScale.Fit)
                }?:run {
                    Text("cannot load qrcode", Modifier.padding(bottom = 20.dp).fillMaxWidth())
                }
             //   Text(produkViewModel.createJsonString(produklist.value),Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun SettingQr(activity: Activity, container: AppContainer){
    val settingViewModel = SettingViewModel(container.settingRepository)
    val settingrepo =  settingViewModel.settingList.collectAsState(emptyList())
    var setting: Setting?=null
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = { CenterAlignedTopAppBar({Text("Export setting database", fontSize = 18.sp)},
            navigationIcon = { IconButton(onClick = {activity.finish()}){
                Icon(Icons.Default.ArrowBackIosNew,"back")
            } }
        ) }
    ){innerPadding ->
        val modifier = Modifier.padding(innerPadding).fillMaxWidth().fillMaxHeight()
        if(settingrepo.value.isNotEmpty()){
            settingrepo.value.forEach {
                setting = it
            }
            setting?.let {
                val qrGeneratorJson = QrGeneratorJson(zipandBase64(settingViewModel.toJson(it)))
                //val qrGeneratorJson = QrGeneratorJson("test doang")
                qrBitmap = qrGeneratorJson.generate(500)
            }
            Column(modifier.background(Color.White)) {
                qrBitmap?.let {
                    Image(it.asImageBitmap(),"qrcode",
                        Modifier.padding(bottom = 20.dp,top= 40.dp).fillMaxWidth(), contentScale = ContentScale.Fit)
                }?:run {
                    Text("cannot load qrcode", Modifier.padding(bottom = 20.dp).fillMaxWidth())
                }
                //   Text(produkViewModel.createJsonString(produklist.value),Modifier.fillMaxWidth())
            }
        }
    }
}

fun zipandBase64(json:String): ByteArray{
    val bos = ByteArrayOutputStream(json.length)
    GZIPOutputStream(bos).use {
        it.write(json.toByteArray(Charsets.UTF_8))
    }
    return bos.toByteArray()
}