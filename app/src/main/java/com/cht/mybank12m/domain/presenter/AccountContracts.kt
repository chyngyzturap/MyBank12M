package com.cht.mybank12m.domain.presenter

import com.cht.mybank12m.data.model.Account
import com.cht.mybank12m.data.model.AccountState

interface AccountContracts {
    interface View {
        fun showAccounts(list: List<Account>)
    }
    interface Presenter {
        fun loadAccounts()
        fun addAccount(account: Account)
        fun updateFullyAccount(account: Account)
        fun updateStateAccount(accountId: String, accountState: AccountState)
        fun deleteAccount(accountId: String)

    }
}