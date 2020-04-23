import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.happyhappyyay.android.dreamdiary.database.Page
import com.happyhappyyay.android.dreamdiary.database.PageDao
import com.happyhappyyay.android.dreamdiary.database.PageDatabase
import com.happyhappyyay.android.dreamdiary.journal.JournalViewModel
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import java.io.IOException
import java.sql.Date
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class JournalViewModelInstrumentedTest {
    private val testDispatcher = TestCoroutineDispatcher()
    private lateinit var pageDao: PageDao
    private lateinit var db: PageDatabase
    private lateinit var viewModel: JournalViewModel

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(context, PageDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        pageDao = db.pageDatabaseDao
        viewModel = JournalViewModel(context, pageDao)
        viewModel.pageNumber.value = 0
        viewModel.dispatcher = testDispatcher
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    @Throws(Exception::class)
    fun allReadAndWriteOperations() {
//      insert
        val page = Page()
        pageDao.insert(page)
        var today = pageDao.getToday()
        assertEquals(today?.date, page.date)
//      update
        today?.entries?.add("sounds")
        if (today != null) {
            pageDao.update(today)
            today = pageDao.getPage(today.pageId)
        }
        assertEquals("sounds", today?.entries?.get(0))
        assertEquals(1, today?.entries?.size)
        assertEquals(1, pageDao.getAllPages().blockingObserve()?.size)
//      deleteOne
        today?.pageId?.let { pageDao.deletePage(it) }
        today = pageDao.getToday()
        assertEquals(today?.date, null)
//      getAll
        val date = 1583301600000
        val page2 = Page(date = Date(date.plus(86400000)))
        val page3 =
            Page(date = Date(date.plus(86400000).plus(86400000)))
        pageDao.insert(page)
        pageDao.insert(page2)
        pageDao.insert(page3)
        var pages = pageDao.getAllPages().blockingObserve()
        assertEquals(pages?.size, 3)
//      deleteAll
        pageDao.deleteAll()
        pages = pageDao.getAllPages().blockingObserve()
        assertEquals(pages?.size, 0)
    }

    @Test
    @Throws(Exception::class)
    fun updateEntryInformationIsCorrect() = runBlocking {
        val entry = Page()
        viewModel.updateEntryInformation(entry, 0, "whoa oh oh oh")

        var today = pageDao.getToday()
        assertNotNull(today)
        assertEquals("whoa oh oh oh", today?.entries?.get(0))
        viewModel.updateEntryInformation(today!!, 5, "the best")

        today = pageDao.getPage(today.pageId)
        assertEquals("the best", today?.entries?.get(1))
        viewModel.updateEntryInformation(today!!, 1, "the best")

        today = pageDao.getToday()
        assertEquals("the best", today?.entries?.get(1))
        viewModel.updateEntryInformation(today!!, 1, "the best ")

        today = pageDao.getToday()
        assertEquals("the best ", today?.entries?.get(1))
    }

    @Test
    @Throws(Exception::class)
    fun containsInstructionsIsCorrect() = testDispatcher.runBlockingTest{
        val date = 1583301600000
        val entry = Page(date = Date(date))
        val entry2 = Page(date = Date(date.plus(86400000)))
        val entry3 =
            Page(date = Date(date.plus(86400000).plus(86400000)))
        pageDao.insert(entry)
        pageDao.insert(entry2)
        pageDao.insert(entry3)
        var pages = pageDao.getAllPages().blockingObserve()
        assertEquals(3, pages?.size)
        viewModel.containsInstructions("Add Entry oye oye oye", 0, pages)
        assertEquals("oye oye oye", pages?.get(0)?.entries?.get(0))
        viewModel.containsInstructions("Delete Entry one",0,pages)
        pages = pageDao.getAllPages().blockingObserve()
        assertEquals(0, pages?.get(0)?.entries?.size)
        viewModel.containsInstructions("Add Page March 1st, 2019",0,pages)
        pages = pageDao.getAllPages().blockingObserve()
        assertEquals(4,pages?.size)
        viewModel.containsInstructions("Go To March 6th, 2020",0,pages)
        var pageNum = viewModel.pageNumber.blockingObserve()
        assertEquals(3,pageNum)
        viewModel.containsInstructions("Go To First Page",0,pages)
        pageNum = viewModel.pageNumber.blockingObserve()
        assertEquals(0, pageNum)
        viewModel.containsInstructions("Go To Last Page",0,pages)
        pageNum = viewModel.pageNumber.blockingObserve()
        assertEquals(pages?.size?.minus(1),pageNum)
        viewModel.containsInstructions("Delete Page March 1st, 2019",0,pages)
        pages = pageDao.getAllPages().blockingObserve()
        assertEquals(3,pages?.size)
    }

    @Test
    @Throws(Exception::class)
    fun containsInstructionsIsIncorrect() = testDispatcher.runBlockingTest{
        val date = 1583301600000
        val entry = Page(date = Date(date))
        val entry2 = Page(date = Date(date.plus(86400000)))
        val entry3 =
            Page(date = Date(date.plus(86400000).plus(86400000)))
        pageDao.insert(entry)
        pageDao.insert(entry2)
        pageDao.insert(entry3)
        var entries = pageDao.getAllPages().blockingObserve()
        assertEquals(3, entries?.size)
        viewModel.containsInstructions("Add Entry oye oye oye", 0, entries)
        assertEquals("oye oye oye", entries?.get(0)?.entries?.get(0))
        viewModel.containsInstructions("Delete Entry five",0,entries)
        entries = pageDao.getAllPages().blockingObserve()
        assertEquals(1, entries?.get(0)?.entries?.size)
        viewModel.containsInstructions("Add Page March 99 2019",0,entries)
        entries = pageDao.getAllPages().blockingObserve()
        assertEquals(3,entries?.size)
        viewModel.containsInstructions("Add Page March 1st, 2052",0,entries)
        entries = pageDao.getAllPages().blockingObserve()
        assertEquals(3,entries?.size)
        viewModel.containsInstructions("Go To March 26th, 2020",0,entries)
        val entryNum = viewModel.pageNumber.blockingObserve()
        assertEquals(0,entryNum)
        viewModel.containsInstructions("Delete Page March 41st, 2019",0,entries)
        entries = pageDao.getAllPages().blockingObserve()
        assertEquals(3,entries?.size)
    }

    @Test
    @Throws(Exception::class)
    fun goToPageOrCreateIsCorrect() = testDispatcher.runBlockingTest{
        val date = 1583301600000
        val modelPages = ArrayList<Page>()
        viewModel.goToDateOrCreate(modelPages,Date(date))
        modelPages.add(Page(date = Date(date)))
        var pages = pageDao.getAllPages().blockingObserve()
        assertEquals(1,pages?.size)
        var page = Page(date = Date(date.plus(86400000)))
        pageDao.insert(page)
        modelPages.add(page)
        pages = pageDao.getAllPages().blockingObserve()
        var pageNum = viewModel.pageNumber.blockingObserve()
        assertEquals(2,pages?.size)
        assertEquals(0,pageNum)
        viewModel.goToDateOrCreate(modelPages,page.date)
        pageNum = viewModel.pageNumber.blockingObserve()
        assertEquals(1,pageNum)
        page = Page(date = Date(date.plus(86400000).plus(86400000)))
        pageDao.insert(page)
        modelPages.add(page)
        viewModel.goToDateOrCreate(modelPages,page.date)
        pageNum = viewModel.pageNumber.blockingObserve()
        assertEquals(2,pageNum)
        page = Page(date = Date(date.plus(86400000).plus(86400000).plus(86400000)))
        viewModel.goToDateOrCreate(modelPages,page.date)
        pageNum = viewModel.pageNumber.blockingObserve()
        assertEquals(3,pageNum)
    }
}


private fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)

    val observer = Observer<T> { t ->
        value = t
        latch.countDown()
    }

    observeForever(observer)

    latch.await(2, TimeUnit.SECONDS)
    return value
}
