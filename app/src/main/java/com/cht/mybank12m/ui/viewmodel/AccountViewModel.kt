package com.cht.mybank12m.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cht.mybank12m.data.model.Account
import com.cht.mybank12m.data.model.AccountState
import com.cht.mybank12m.data.network.AccountsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountsApi: AccountsApi
): ViewModel() {

    private val _accountsList = MutableLiveData<List<Account>>()
    val accountsList: LiveData<List<Account>> = _accountsList

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun loadAccounts() {
        accountsApi.fetchAccounts().handleResponse(
            onSuccess = { _accountsList.value = it },
            onError = { _errorMessage.value = it }
        )
    }

    fun addAccount(account: Account) {
        accountsApi.createAccount(account).handleResponse(onSuccess = {loadAccounts()})
    }

    fun updateFullyAccount(account: Account) {
        accountsApi.updateFullyAccount(account.id!!, account).handleResponse(onSuccess = {loadAccounts()})
    }

    fun updateStateAccount(accountId: String, accountState: AccountState) {
        accountsApi.updateAccountState(accountId, accountState).handleResponse(
            onSuccess = {
                _successMessage.value = "Состояние счета изменено!"
                loadAccounts()
            }
        )
    }
    fun deleteAccount(accountId: String) {
        accountsApi.deleteAccount(accountId).handleResponse(onSuccess = {loadAccounts()})
    }

    private fun <T> Call<T>?.handleResponse(
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        this?.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                val resultBody = response.body()
                if (response.isSuccessful && resultBody != null) {
                    onSuccess(resultBody)
                } else {
                    onError(response.code().toString())
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                onError(t.message.toString())
            }
        })

    }

}