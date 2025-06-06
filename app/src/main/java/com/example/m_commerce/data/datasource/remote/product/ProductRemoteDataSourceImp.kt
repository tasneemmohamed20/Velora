package com.example.m_commerce.data.datasource.remote.product

import android.util.Log
import com.apollographql.apollo.exception.ApolloException
import com.example.m_commerce.GetBrandsQuery
import com.example.m_commerce.data.datasource.remote.ApolloHelper.shopifyService
import com.example.m_commerce.domain.entities.Brand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class ProductRemoteDataSourceImp() : IProductRemoteDataSource {
    override suspend fun getBrands(): Flow<List<Brand>> = flow {
            val response = withContext(Dispatchers.IO) {
                shopifyService.query(GetBrandsQuery()).execute()
            }
            val brands = response.data?.collections?.edges
                ?.mapNotNull { it?.node }
                ?.map { node ->
                    Brand(
                        id = node.id,
                        title = node.title,
                        imageUrl = node.image?.url.toString()
                    )
                } ?: emptyList()
            emit(brands)
    }

}