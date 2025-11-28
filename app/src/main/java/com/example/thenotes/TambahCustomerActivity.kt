package com.example.thenotes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.thenotes.ui.theme.TheNotesTheme

class TambahCustomerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheNotesTheme {
                AddCustomerView()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerView(){
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {TopAppBar(title = {Text("TO")})}
    ) { innerPadding->
        Column(Modifier.padding(innerPadding
        ).padding(10.dp)) {
            var text by remember { mutableStateOf("Name") }
            var initFocusState by remember { mutableStateOf(false) }
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
                    Text("Name")
                }else{
                    Text("")
                }
                }
            )
            //Text(text)
        }
    }
}

