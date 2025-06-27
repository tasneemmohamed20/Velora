package com.example.m_commerce.presentation.account.settings.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.m_commerce.presentation.account.settings.view_model.AddressMapViewModel
import com.example.m_commerce.presentation.utils.theme.Primary
import com.example.m_commerce.presentation.utils.theme.PurpleGrey40
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapSearch(
    onBack: () -> Unit,
    onResultClick: (LatLng) -> Unit,
    viewModel: AddressMapViewModel
) {
    val query = viewModel.searchQuery.collectAsState().value
    val results = viewModel.searchResults.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            modifier = Modifier
//                .padding(horizontal = 8.dp, vertical = 0.dp)
                .fillMaxWidth()
                .padding(start = 0.dp, end = 8.dp, top = 0.dp, bottom = 0.dp),
//                .shadow(elevation = 2.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onBack()
                    viewModel.clearQuery()
                          },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Primary.copy(alpha = 0.7f)
                )
            }

            TextField(
                value = query,
                onValueChange = { newValue -> viewModel.onQueryChange(newValue) },
                placeholder = {
                    Text(
                        text = "Search location",
//                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .border(
                        width = 1.dp,
                        color = Primary.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearQuery() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Primary.copy(alpha = 0.7f)
                            )
                        }
                    }
                    else{
                        Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Primary.copy(alpha = 0.7f)
                    )
                    }
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                )
            )
        }

//        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = Color.Transparent, thickness = 1.dp, modifier = Modifier.shadow(elevation = 2.dp))

        LazyColumn {
            itemsIndexed(
                items = results,
                key = { _, item -> item.placeId }
            ) { index, prediction ->
                SearchResultRow(
                    title = prediction.getPrimaryText(null).toString(),
                    description = prediction.getSecondaryText(null).toString(),
                    onClick = {

                        viewModel.getPlaceDetails(prediction.placeId) { latLng ->
                            viewModel.updateCurrentLocation(latLng)
                            onResultClick(latLng)
                        }
                    }
                )
                if (index < results.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 56.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultRow(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            tint = Primary.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
//                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PurpleGrey40                )
            }
        }
    }
}
