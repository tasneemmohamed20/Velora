package com.example.m_commerce.domain.usecases

import com.example.m_commerce.domain.repository.IDiscountCodesRepository
import javax.inject.Inject

class DiscountCodesUsecse @Inject constructor(private val repository: IDiscountCodesRepository) {

    suspend operator fun invoke() = repository.getDiscountCodes()
}