package com.example.m_commerce.domain.usecases

import com.example.m_commerce.data.repository_imp.settings_repo.CurrencyExchangeRepositoryImp
import javax.inject.Inject

class GetCurrencyPrefUseCase@Inject constructor(private val repository: CurrencyExchangeRepositoryImp) {

    suspend operator fun invoke(): Pair<Boolean, Float> {
        return Pair(repository.getCurrencyPreference(), repository.getUsdToEgp())
    }
}