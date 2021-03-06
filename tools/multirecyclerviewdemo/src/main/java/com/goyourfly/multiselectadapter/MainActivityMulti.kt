package com.goyourfly.multiselectadapter

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.goyourfly.multiple.adapter.MultipleAdapter
import com.goyourfly.multiple.adapter.MultipleSelect
import com.goyourfly.multiple.adapter.menu.SimpleDeleteSelectAllMenuBar
import com.goyourfly.multiple.adapter.viewholder.color.ColorFactory
import com.goyourfly.multiple.adapter.viewholder.view.CheckBoxFactory
import com.goyourfly.multiple.adapter.viewholder.view.RadioBtnFactory

class MainActivityMulti : AppCompatActivity() {

    val recycler: RecyclerView by lazy { findViewById(R.id.recycler) as RecyclerView }
    val demoAdapter = DemoSectionAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_multi)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))

        recycler.adapter = MultipleSelect
                .with(this)
                .adapter(demoAdapter)
                .decorateFactory(ColorFactory())
                .linkList(demoAdapter.list)
                .ignoreViewType(arrayOf(demoAdapter.TYPE_SECTION))
                .customMenu(SimpleDeleteSelectAllMenuBar(this, resources.getColor(R.color.colorPrimary)))
                .build()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_multi, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_radio_btn_left -> {
                recycler.adapter = MultipleSelect
                        .with(this)
                        .adapter(demoAdapter)
                        .decorateFactory(RadioBtnFactory())
                        .ignoreViewType(arrayOf(demoAdapter.TYPE_SECTION))
                        .linkList(demoAdapter.list)
                        .customMenu(SimpleDeleteSelectAllMenuBar(this, resources.getColor(R.color.colorPrimary)))
                        .build()
            }
            R.id.action_radio_btn_right -> {
                recycler.adapter = MultipleSelect
                        .with(this)
                        .adapter(demoAdapter)
                        .decorateFactory(RadioBtnFactory(Gravity.RIGHT))
                        .ignoreViewType(arrayOf(demoAdapter.TYPE_SECTION))
                        .linkList(demoAdapter.list)
                        .customMenu(SimpleDeleteSelectAllMenuBar(this, resources.getColor(R.color.colorPrimary)))
                        .build()
            }
            R.id.action_check_box -> {
                recycler.adapter = MultipleSelect
                        .with(this)
                        .adapter(demoAdapter)
                        .decorateFactory(CheckBoxFactory(color = resources.getColor(R.color.colorPrimary)))
                        .ignoreViewType(arrayOf(demoAdapter.TYPE_SECTION))
                        .linkList(demoAdapter.list)
                        .customMenu(SimpleDeleteSelectAllMenuBar(this, resources.getColor(R.color.colorPrimary)))
                        .build()
            }
            R.id.action_background_color -> {
                recycler.adapter = MultipleSelect
                        .with(this)
                        .adapter(demoAdapter)
                        .decorateFactory(ColorFactory())
                        .ignoreViewType(arrayOf(demoAdapter.TYPE_SECTION))
                        .linkList(demoAdapter.list)
                        .customMenu(SimpleDeleteSelectAllMenuBar(this, resources.getColor(R.color.colorPrimary)))
                        .build()
            }
            R.id.action_more -> {
                startActivity(Intent(this,MoreActivityMulti::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(!(recycler.adapter as MultipleAdapter).cancel()){
            super.onBackPressed()
        }
    }

}
