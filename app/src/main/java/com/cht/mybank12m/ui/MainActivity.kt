package com.cht.mybank12m.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cht.mybank12m.data.model.Account
import com.cht.mybank12m.data.model.AccountState
import com.cht.mybank12m.databinding.ActivityMainBinding
import com.cht.mybank12m.databinding.DialogAddAccountBinding
import com.cht.mybank12m.ui.adapter.AccountAdapter
import com.cht.mybank12m.ui.viewmodel.AccountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AccountAdapter
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAdapter()
        initClicks()
        subscribeToLiveData()
    }

    private fun subscribeToLiveData(){
        viewModel.accountsList.observe(this) {
            adapter.submitList(it)
        }
        viewModel.successMessage.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        viewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
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
                .setTitle(getAlertDialogTitle(false))
                .setView(binding.root)
                .setPositiveButton("Добавить") { _, _ ->
                    val account = Account(
                        name = etName.text.toString(),
                        currency = etCurrency.text.toString(),
                        balance = etBalance.text.toString().toInt()
                    )
                    viewModel.addAccount(account)
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
                .setTitle(getAlertDialogTitle(true))
                .setView(binding.root)
                .setPositiveButton("Изменить") { _, _ ->

                    val updatedAccount = account.copy(
                        name = etName.text.toString(),
                        currency = etCurrency.text.toString(),
                        balance = etBalance.text.toString().toInt()
                    )

                    viewModel.updateFullyAccount(updatedAccount)
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
                    viewModel.deleteAccount(it)
                },
                onSwitchToggle = { id, isChecked ->
                    viewModel.updateStateAccount(id, AccountState(isChecked))
                }
            )
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = adapter
        }
    }

    private fun getAlertDialogTitle(isEditDialog: Boolean): String {
        val title = if (isEditDialog) {
            "Изменить счет"
        } else {
            "Добавить счет"
        }
        return title
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAccounts()
    }
}