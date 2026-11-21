package com.techstore.app.data.repository

import kotlinx.coroutines.delay

enum class DemoPaymentResult {
    APPROVED
}

object PaymentRepository {

    suspend fun simulateDemoPayment(): DemoPaymentResult {
        delay(1500)
        return DemoPaymentResult.APPROVED
    }
}
