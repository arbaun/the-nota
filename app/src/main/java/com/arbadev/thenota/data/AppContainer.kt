package com.arbadev.thenota.data

import android.content.Context

interface AppContainer{
    val customerRepository: CustomerRepository
    val settingRepository: SettingRepository
    val produkRepository : ProdukRepository
    val notaRepository: NotaRepository
    val itemNotaRepository : ItemNotaRepository
}

class AppDataContainer(private val context: Context): AppContainer{
    override val customerRepository: CustomerRepository= CustomerRepository(CustomerDatabase.getDatabase(context = context).customerDao())
    override val settingRepository: SettingRepository= SettingRepository(CustomerDatabase.getDatabase(context = context).settingDao())
    override val produkRepository: ProdukRepository = ProdukRepository(CustomerDatabase.getDatabase(context).produkDao())
    override val notaRepository : NotaRepository = NotaRepository(CustomerDatabase.getDatabase(context).notaDao())
    override val itemNotaRepository: ItemNotaRepository = ItemNotaRepository(CustomerDatabase.getDatabase(context).itemNotaDao())

}