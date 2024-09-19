package com.example.shoppinglist

import android.graphics.Paint.Align
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoppinglist.ui.theme.ShoppingListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingListTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShoppingListApp(innerPadding)
                }
            }
        }
    }
}

@Composable         //composable shows in UI
fun ShoppingListApp (
    innerPadding : PaddingValues
) {

    var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }       //state updating
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }


    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center
    ) {

        Button(onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)) {

            Text(text = "Add Item")

        }

        LazyColumn (                //more efficient for scrollable apps
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            items(sItems) {

                item->
                if(item.isEditing) {
                    ShoppingItemEditor(item = item,
                        onEditComplete = {editedName, editedQuantity ->
                            sItems = sItems.map{it.copy(isEditing = false)}
                            val editedItem = sItems.find{it.id == item.id}
                            editedItem ?.let {
                                it.name = editedName
                                it.quantity = editedQuantity
                            }
                    } )
                } else {
                    ShoppingListItem(item = item,
                        onEditClick = { sItems = sItems.map {it.copy(isEditing = it.id == item.id)} },
                        onDeleteClick = { sItems = sItems - item })
                }



            }

        }

    }

    if(showDialog) {

        AlertDialog(onDismissRequest = { showDialog = false },
            confirmButton = {

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(onClick = {
                        if(itemName.isNotBlank()) {
                            val newItem = ShoppingItem(id = sItems.size+1,
                                name = itemName,
                                quantity = itemQuantity.toInt())

                            sItems = sItems + newItem
                            showDialog = false
                            itemName = " "

                        }
                    }) {

                        Text(text = "Add")

                    }

                    Button(onClick = { showDialog = false }) {

                        Text(text = "Cancel")

                    }
                }

            },
            title = { Text(text = "Add Shopping Item")},
            text = {
                Column {

                    OutlinedTextField(
                        value = itemName,           //which value input
                        onValueChange = {itemName = it},        //input stores in "it" and assign it to i=itemValue
                        singleLine = true,          //only input one line of text
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp))

                    OutlinedTextField(value = itemQuantity,
                        onValueChange = {itemQuantity = it},
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp))

                }
            })

    }

}

@Composable
fun ShoppingItemEditor(
    item : ShoppingItem,
    onEditComplete : (String, Int) -> Unit

) {

    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {

            BasicTextField(value = editedName ,
                onValueChange = {editedName = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp))

            BasicTextField(value = editedQuantity ,
                onValueChange = {editedQuantity = it},
                singleLine = true,
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp))

        }

        Button(onClick = {
            isEditing = false
            onEditComplete(editedName,
                editedQuantity.toIntOrNull() ?: 1)

        }) {
            Text(text = "Save")
        }

    }

}

@Composable
fun ShoppingListItem (
    item : ShoppingItem,
    onEditClick : () -> Unit,       //callback lambda function
    onDeleteClick : () -> Unit

) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                border = BorderStroke(
                    2.dp,
                    Color(0XFF018786)
                ),
                shape = RoundedCornerShape(20)
            ), horizontalArrangement =  Arrangement.SpaceBetween
    ) {
        Text(text = item.name, modifier = Modifier.padding(8.dp))

        Text(text = "Qty : ${item.quantity}",
            modifier = Modifier.padding(8.dp) )

        Row (
            modifier = Modifier.padding(8.dp)
        ) {
            IconButton(onClick = onEditClick) {

                Icon(imageVector = Icons.Default.Edit,
                    contentDescription = "Edit" )

            }

            IconButton(onClick = { onDeleteClick }) {

                Icon(imageVector = Icons.Default.Delete,
                    contentDescription = "Delete" )

            }

        }

    }

}

data class ShoppingItem (
    val id : Int,           // user cannot change
    var name : String,          // user cannot change
    var quantity : Int,
    var isEditing : Boolean = false

)