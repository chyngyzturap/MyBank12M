package com.cht.mybank12m.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cht.mybank12m.data.model.Account
import com.cht.mybank12m.data.model.AccountState
import com.cht.mybank12m.databinding.ActivityMainBinding
import com.cht.mybank12m.databinding.DialogAddAccountBinding
import com.cht.mybank12m.domain.presenter.AccountContracts
import com.cht.mybank12m.domain.presenter.AccountPresenter
import com.cht.mybank12m.ui.adapter.AccountAdapter

class MainActivity : AppCompatActivity(), AccountContracts.View {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var presenter: AccountContracts.Presenter
    private lateinit var adapter: AccountAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = AccountPresenter(this)

        initAdapter()
        initClicks()
    }

    private fun initClicks() {
        with(binding) {
            btnAdd.setOnClickListener {
                showAddDialog()
            }
        }
    }

    private fun showAddDialog(){
        val binding = DialogAddAccountBinding.inflate(LayoutInflater.from(this))
        with(binding){
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Добавить счет")
                .setView(binding.root)
                .setPositiveButton("Добавить") { _, _ ->
                    val account = Account(
                        name = etName.text.toString(),
                        currency = etCurrency.text.toString(),
                        balance = etBalance.text.toString().toInt()
                    )
                    presenter.addAccount(account)
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    private fun showEditDialog(account: Account){
        val binding = DialogAddAccountBinding.inflate(LayoutInflater.from(this))
        with(binding){

            etName.setText(account.name)
            etBalance.setText(account.balance.toString())
            etCurrency.setText(account.currency)

            AlertDialog.Builder(this@MainActivity)
                .setTitle("Изменить счет")
                .setView(binding.root)
                .setPositiveButton("Изменить") { _, _ ->

                    val updatedAccount = account.copy(
                        name = etName.text.toString(),
                        currency = etCurrency.text.toString(),
                        balance = etBalance.text.toString().toInt()
                    )

                    presenter.updateFullyAccount(updatedAccount)
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    private fun initAdapter() {
        with(binding) {
            adapter = AccountAdapter(
                onEdit = {
                    showEditDialog(it)
                },
                onDelete = {
                    presenter.deleteAccount(it)
                },
                onSwitchToggle = { id, isChecked ->
                    presenter.updateStateAccount(id, AccountState(isChecked))
                }
            )
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.loadAccounts()
    }

    override fun showAccounts(list: List<Account>) {
        adapter.submitList(list)
    }
}