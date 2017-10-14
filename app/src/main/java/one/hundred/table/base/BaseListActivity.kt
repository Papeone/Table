package one.hundred.table.base

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.LinearLayout
import com.pape.adapter.AdapterSequence
import com.pape.adapter.ItemViewModel
import com.pape.adapter.MultiTypeAdapter
import one.hundred.table.R
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.appBarLayout
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.recyclerview.v7.recyclerView

/**
 * Created by zzy on 2017/10/13.
 */
abstract class BaseListActivity<P : BasePresenter> : AppCompatActivity(), BaseView<P> {

    var adapter = MultiTypeAdapter(AdapterSequence.ASC)

    lateinit var bottomView: LinearLayout

    lateinit var toolbar: Toolbar

    private lateinit var presenterCache: P

    override fun onCreate(savedInstanceState: Bundle?) {
        coordinatorLayout {
            appBarLayout {
                fitsSystemWindows = true
                toolbar = toolbar {
                    title = toolBarTitle()
                }.lparams(matchParent, dip(50)) {
                    scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                }
            }.lparams(matchParent, wrapContent)
            relativeLayout {
                recyclerView {
                    layoutManager = getRVLayoutManager()
                    adapter = this@BaseListActivity.adapter
                }.lparams(matchParent, wrapContent) {
                    alignParentTop()
                    above(R.id.bottomId)
                }
                bottomView = linearLayout {
                    id = R.id.bottomId
                    orientation = LinearLayout.VERTICAL
                }.lparams(matchParent, wrapContent) {
                    padding = dip(8)
                    alignParentBottom()
                }
            }.lparams {
                behavior = AppBarLayout.ScrollingViewBehavior()
            }

        }
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        initToolBarMenu(toolbar)
        initBottomView(bottomView)
        setPresenter(initPresenter())
        initViewData()
        super.onStart()
    }
    /**
     * 初始化toolbar 标题
     */
    abstract fun toolBarTitle(): String
    /**
     * 初始化toolbar 菜单
     */
    abstract fun initToolBarMenu(toolbar: Toolbar)
    /**
     * 初始化底部view
     */
    abstract fun initBottomView(bottomView: LinearLayout)
    /**
     * 初始化 presenter
     */
    abstract fun initPresenter(): P

    override fun setPresenter(presenter: P) {
        this.presenterCache = presenter
    }

    override fun getPresenter(): P {
        return presenterCache
    }

    /**
     * 初始化RecycleView Item数据
     */
    private fun initViewData() {
        addItemList(presenterCache.initViewData())
    }
    /**
     * 添加一项 Item
     */
    fun addItem(itemViewModel: ItemViewModel) {
        adapter.addItem(itemViewModel)
    }
    /**
     * 添加 Item集合
     */
    fun addItemList(list: List<ItemViewModel>) {
        adapter.addListItem(list)
    }
    /**
     * 添加BottomView
     */
    fun addBottomView(view: View) {
        bottomView.addView(view)
    }
    /**
     * 根据Item UUID 查找Item 并自动转换类型
     */
    inline fun <reified T : ItemViewModel> findItem(uuid: String): T? = adapter.findItem(uuid)
    /**
     * 设置RecycleView的layoutManager
     */
    open fun getRVLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(this)
}