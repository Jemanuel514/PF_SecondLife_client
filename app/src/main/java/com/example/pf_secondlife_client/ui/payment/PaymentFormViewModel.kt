package com.example.pf_secondlife_client.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pf_secondlife_client.data.payments.PaymentsRepository
import com.example.pf_secondlife_client.data.posts.PostsRepository
import com.example.pf_secondlife_client.domain.models.PatchPostRequest
import com.example.pf_secondlife_client.domain.models.PostPaymentRequest
import com.example.pf_secondlife_client.domain.valuesSets.CardType
import com.example.pf_secondlife_client.domain.valuesSets.PostState
import com.example.pf_secondlife_client.network.api.errorHandling.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentFormViewModel(
    private val postId: String,
    private val paymentsRepository: PaymentsRepository,
    private val postsRepository: PostsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentFormModel>(PaymentFormModel.Locking)
    val uiState: StateFlow<PaymentFormModel> = _uiState.asStateFlow()

    private val _paymentState = MutableStateFlow(PaymentModel())
    val paymentState: StateFlow<PaymentModel> = _paymentState.asStateFlow()

    private var lockAcquired = false

    private fun acquireLock() {
        viewModelScope.launch {
            _uiState.value = PaymentFormModel.Locking
            postsRepository.patchPost(postId, PatchPostRequest(state = PostState.BUSY))
                .onSuccess {
                    lockAcquired = true
                    _uiState.value = PaymentFormModel.Ready
                }
                .onFailure { error ->
                    val message = when (error) {
                        is ApiException.Conflict -> "This post is being bought by another user."
                        is ApiException.Forbidden -> "Not allowed to buy this post."
                        is ApiException.Network -> "Not connected. Check your internet connection."
                        else -> error.message ?: "Couldn't start payment process."
                    }
                    _uiState.value = PaymentFormModel.Error(message)
                }
        }
    }

    fun onCardTypeChange(value: CardType) {
        _paymentState.value = _paymentState.value.copy(cardType = value, cardTypeError = null)
    }

    fun onCardholderNameChange(value: String) {
        _paymentState.value = _paymentState.value.copy(
            cardholderName = value,
            cardholderNameError = if (value.isBlank()) "Enter cardholder name" else null,
        )
    }

    fun onCardNumberChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        val error = when {
            digitsOnly.isEmpty() -> "Enter card number"
            digitsOnly.length !in 13..19 -> "The card number is not valid"
            else -> null
        }
        _paymentState.value = _paymentState.value.copy(cardNumber = digitsOnly, cardNumberError = error)
    }

    private fun formatExpirationInput(raw: String): String {
        val digits = raw.filter { it.isDigit() }.take(4)
        return when {
            digits.length <= 2 -> digits
            else -> "${digits.take(2)}/${digits.drop(2)}"
        }
    }

    fun onExpirationChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(4)
        val error = when {
            digitsOnly.isEmpty() -> "Enter expiration date"
            digitsOnly.length == 4 -> {
                val month = digitsOnly.substring(0, 2).toIntOrNull()
                if (month == null || month !in 1..12) "Invalid month" else null
            }
            else -> null
        }
        _paymentState.value = _paymentState.value.copy(expiration = digitsOnly, expirationError = error)
    }

    fun onCvvChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        val error = when {
            digitsOnly.isEmpty() -> "Enter CVV"
            digitsOnly.length !in 3..4 -> "Invalid CVV"
            else -> null
        }
        _paymentState.value = _paymentState.value.copy(cvv = digitsOnly, cvvError = error)
    }

    fun submitPayment() {
        val payment = _paymentState.value
        if (!payment.isValid()) return

        viewModelScope.launch {
            _uiState.value = PaymentFormModel.Submitting
            paymentsRepository.createPayment(postId, PostPaymentRequest(
                cardType = payment.cardType!!,
                owner = payment.cardholderName,
                cardNumber = payment.cardNumber,
                expirationMonth = payment.expiration.substring(0, 2).toInt(),
                expirationYear = payment.expiration.substring(2, 4).toInt(),
                cvv = payment.cvv,
            ))
                .onSuccess {
                    lockAcquired = false
                    _uiState.value = PaymentFormModel.Success
                }
                .onFailure { error ->
                    val message = when (error) {
                        is ApiException.Conflict -> "This post is not currently available."
                        is ApiException.Network -> "Not connected. Check your internet connection."
                        else -> error.message ?: "Couldn't process payment request."
                    }
                    _uiState.value = PaymentFormModel.Error(message)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (lockAcquired) {
            CoroutineScope(SupervisorJob()).launch {
                withContext(NonCancellable) {
                    postsRepository.patchPost(postId, PatchPostRequest(state = PostState.AVAILABLE))
                }
            }
        }
    }

    init {
        acquireLock()
    }
}