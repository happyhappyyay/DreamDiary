package com.happyhappyyay.android.dreamdiary.journal

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.happyhappyyay.android.dreamdiary.R
import com.happyhappyyay.android.dreamdiary.database.Page
import com.happyhappyyay.android.dreamdiary.database.PageDao
import com.happyhappyyay.android.dreamdiary.database.startOfDay
import com.happyhappyyay.android.dreamdiary.speech.Instruction
import com.happyhappyyay.android.dreamdiary.speech.JournalAudioListener
import kotlinx.coroutines.*
import java.sql.Date
import java.util.*

class JournalViewModel(application: Application, private val database:PageDao) : AndroidViewModel(application) {
    private val textNumbers:HashMap<String, Int> = hashMapOf("one" to 1, "two" to 2, "to" to 2, "three" to 3, "four" to 4, "for" to 4, "five" to 5,
        "six" to 6, "seven" to 7, "eight" to 8, "ate" to 8, "nine" to 9, "ten" to 10)
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var intent: Intent
    private val _isRecording = MutableLiveData<Boolean>()
    val isRecording
    get() = _isRecording
    private val _text = MutableLiveData<String>()
    val text
    get() = _text
    private val _pageNumber = MutableLiveData<Int>()
    val pageNumber: MutableLiveData<Int>
    get() = _pageNumber
    private val _snackBarText = MutableLiveData<String>()
    val snackBarText
    get()= _snackBarText
    val pages = database.getAllPages()
    var dispatcher = Dispatchers.IO
    val app = application
    private val sharedPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    init{
        initialize()
    }

    private fun initialize(){
        if(hasAutoRecord()){
            setIsRecording(true)
        }
    }

    private fun hasAutoRecord():Boolean{
        return sharedPrefs.getBoolean(app.getString(R.string.pref_key_auto_record), false)
    }

    private fun initializeRecord(){
        if(SpeechRecognizer.isRecognitionAvailable(getApplication())) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
            speechRecognizer?.setRecognitionListener(
                JournalAudioListener { error, text ->
                    if(isRecording.value!!) {
                        if (error == true && text != null) {
                            Log.d("JournalViewModel", "listener for error")
                            //                      speech recognizer calls on error continuously upon error
                            _isRecording.value = !error
                            setSnackBarText(text)
                        } else {
                            //                       speech recognizer calls on result twice for s8
                            text?.let {
                                _text.value = it
                                clearRecording()
                                Log.d("JournalViewModel", "listener for text")
                            }
                        }
                    }
                })
        }
        else{
            Log.d("JournalViewModel","recognizer not available")
        }
    }

    fun startRecording(){
        if(speechRecognizer==null){
            initializeRecord()
        }
        Log.d("JournalViewModel", "start recording")
        if(!::intent.isInitialized) {
            intent = Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            if (Build.VERSION.SDK_INT >= 23) {
                if(sharedPrefs.getBoolean(app.getString(R.string.pref_key_prefer_offline), false))
                    intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            }
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true)
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 50000)
            intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2500)

        }
        speechRecognizer?.startListening(intent)
    }

    fun containsInstructions(
        text: String,
        entryPos: Int,
        pages: List<Page>? = this.pages.value
    ){
        var instructions = text.toLowerCase(Locale.getDefault())
        val dateRegexString = "(\\w{4,9}\\s\\d{1,2})\\w{0,2},?\\s?(\\d{4})?"
        if(instructions.length >= 35){
            instructions = instructions.substring(0,35)
        }
        System.out.println(text)
//        Log.d("JournalViewModel","instructions")
        if(instructions.contains(Instruction.ADD_ENTRY.instruction)){
            Log.d("JournalViewModel", "add entry")
            val index = instructions.indexOf(Instruction.ADD_ENTRY.instruction) + Instruction.ADD_ENTRY.instruction.length
            addEntryVoice(pages, text.substring(index),entryPos)
        }
        else if(instructions.contains(Instruction.DELETE_ENTRY.instruction)){
            if(instructions.contains(Instruction.DELETE_ENTRY_ALL.instruction)){
                    val position = pageNumber.value
                    if(position != null){
                        if(pages!=null) {
                            deleteAllEntryInformation(pages[position])
                            setSnackBarText("${app.getString(R.string.deleted_all_entries)} ${pages[position].date.toDayFormat()}")
                            return
                        }
                    }
                }
            Log.d("JournalViewModel","delete position: $entryPos with $text")
            val index = instructions.indexOf(Instruction.DELETE_ENTRY.instruction) + Instruction.DELETE_ENTRY.instruction.length
            val number = instructions.substring(index)
            var entryNumber: Int? = null
            for((k,v) in textNumbers){
                if(number.contains(k)){
                    entryNumber = v.minus(1)
                    break
                }
            }
            if(entryNumber == null) {
                val match = "Delete Entry (\\d+)".toRegex(RegexOption.IGNORE_CASE).find(text)
                entryNumber = match?.destructured.toString().toIntOrNull()?.minus(1)
            }
            deleteEntryVoice(pages,entryPos,entryNumber)
        }
        else if(instructions.contains(Instruction.GO_TO.instruction)){
            Log.d("JournalViewModel","Go To- page:$entryPos")
            if(instructions.contains(Instruction.FIRST_PAGE.instruction)){
                _pageNumber.value = 0
            }
            else if(instructions.contains(Instruction.LAST_PAGE.instruction)){
                _pageNumber.value = pages?.size?.minus(1)
            }
            else{
                val match = StringBuilder(Instruction.GO_TO.instruction).append(dateRegexString)
                    .toString().toRegex(RegexOption.IGNORE_CASE).find(text)
                val list = match?.destructured?.toList()
                Log.d("JournalViewModel","Go To- match:$list")
                if(!list.isNullOrEmpty()){
                    val date = createCompleteDate(list)
                    if(!pages.isNullOrEmpty() && date !=null) {
                        Log.d("JournalViewModel","Go To- date:${date.toDayFormat()}")
                        for (i in pages.indices){
                            if(pages[i].date == date){
                                _pageNumber.value = i
                                break
                            }
                        }
                    }
                }
            }
        }
        else if(instructions.contains(Instruction.ADD_PAGE.instruction)){
            val match = StringBuilder(Instruction.ADD_PAGE.instruction).append(dateRegexString)
                .toString().toRegex(RegexOption.IGNORE_CASE).find(text)
            val list = match?.destructured?.toList()
            if(!list.isNullOrEmpty()){
                val date = createCompleteDate(list)
                if(!pages.isNullOrEmpty() && date !=null) {
                    if(date.time <= startOfDay().time){
                        for (i in pages.indices){
                            if(pages[i].date == date){
                                return
                            }
                        }
                        createPage(Page(date = date))
                        setSnackBarText("${app.getString(R.string.added_page)}$date")
                    }
                }
            }
        }
        else if(instructions.contains(Instruction.DELETE_PAGE.instruction)){
            val match = StringBuilder(Instruction.DELETE_PAGE.instruction).append(dateRegexString)
                .toString().toRegex(RegexOption.IGNORE_CASE).find(text)
            val list = match?.destructured?.toList()
            Log.d("JournalViewModel","Delete Page- match:$list")
            if(!list.isNullOrEmpty()){
                val date = createCompleteDate(list)
                if(!pages.isNullOrEmpty() && date !=null) {
                    Log.d("JournalViewModel","Delete Page- date:${date.toDayFormat()}")
                    for (i in pages.indices){
                        if(pages[i].date == date){
                            deletePage(pages[i])
                            setSnackBarText("${app.getString(R.string.deleted_page)}$date")
                            break
                        }
                    }
                }
            }
        }
        clearVoiceText()
    }

    private fun checkExistsInEntry(page: Page, pos: Int):Boolean{
        System.out.println(" exists ${page.entries.size} $pos")
        return pos < page.entries.size
    }

    private fun checkDateAgainstMonth(stringDate: String):Boolean{
        val months = listOf("January", "February","March","April","May","June","July","August","September",
        "October","November","December")
        val firstSpace = stringDate.indexOf(" ")
        val month = stringDate.substring(0,firstSpace)
        val comma = stringDate.indexOf(',')
        var numericString = stringDate.substring(firstSpace+1,comma)
        if(numericString[0] == '0'){
            if(numericString.length>1){
                numericString = numericString.substring(1)
            }
        }
        val numeric = numericString.toInt()
        System.out.println("$stringDate $numeric")
        return when(month){
            months[0],months[2],months[4],months[6],months[7],months[9],months[11] -> numeric in 1..31
            months[3],months[5],months[8],months[10] -> numeric in 1..30
            months[1] -> numeric in 1..29
            else -> false
        }
    }

    private fun createCompleteDate(list: List<String>):Date?{
        val stringDate: String
        stringDate = if(list[1].isNotBlank()){
            list.toString().substring(1,list.toString().length-1)
        } else{
            val sbDate = StringBuilder(list[0]).append(Date(System.currentTimeMillis()).toYearFormat())
            Log.d("JournalViewModel","createDate- match: $sbDate")
            sbDate.toString()
        }
        return if(checkDateAgainstMonth(stringDate)) stringDate.toDateFormat() else null
    }

    private fun addEntryVoice(pages: List<Page>?, text:String, entryPos: Int){
            val entry = if(pages!=null) retrieveEntryFromListOrNull(pages,entryPos) else null
            updateEntryInformation(entry,entry?.entries?.size?: 0,text)
            val date = entry?.date?.toDayFormat() ?: startOfDay().toDayFormat()
            setSnackBarText("${app.getString(R.string.added_entry)}$date")
    }

    fun updateEntryInformation(page:Page?, position:Int, text:String){
        Log.d("JournalViewModel","inside ${pages.value?.size}")
        if(page != null){
            Log.d("JournalViewModel","position: $position with $text")
            if(position>= page.entries.size){
                if(text.isNotBlank()){
                    page.entries.add(text)
                    if(position == 0){
                        createPage(page)
                    }
                    else{
                        Log.d("JournalViewModel","update")
                        updatePage(page)
                    }
                }
            }
            else {
                if (page.entries[position] != text) {
                    page.entries[position] = text
                    updatePage(page)
                }
            }
        }
        else{
            val newPage = Page()
            newPage.entries.add(text)
            createPage(newPage)
        }
    }

    private fun deleteEntryVoice(pages: List<Page>?, entryPos: Int, pos:Int?){
        Log.d("JournalViewModel","voice- page:$entryPos entry:$pos ")
        System.out.println("voice- page:$entryPos entry:$pos")
         if(pos != null){
             val entry = if(pages!=null) retrieveEntryFromListOrNull(pages, entryPos) else null
             if(entry!=null) {
                     deleteEntryInformation(entry, pos)
                     setSnackBarText("${app.getString(R.string.deleted_entry)}${pos+1}")
                 }
         }
    }

    fun deleteEntryInformation(page: Page, pos: Int) {
        Log.d("JournalViewModel","voice- page:${page.date} entry:$pos ")
        System.out.println("voice- page:${page.date} entry:$pos ")
        if(checkExistsInEntry(page,pos)) {
            System.out.println("in")
                page.entries.removeAt(pos)
                updatePage(page)
            }
    }

    fun deleteAllEntryInformation(page: Page){
        page.entries.clear()
        updatePage(page)
    }

    fun goToDateOrCreate(pages: List<Page>? = this.pages.value,date:Date){
        System.out.println("${pages}")
        if(pages!=null && pageNumber.value!=null){
            System.out.println("in")
            Log.d("JournalViewModel","createEntryGoTo ${date.time}")
            if(pages.isEmpty()){
                System.out.println("empty")
                createPage(Page(date=date))
                _pageNumber.value = 0
                return
            }
            var behindToday = pages[pages.size-1].date != startOfDay()
            for (i in pages.indices){
                Log.d("JournalViewModel","${pages[i].date.time}")
                if(pages[i].date == date){
                    _pageNumber.value = i
                    behindToday = false
                    break
                }
                else if(pages[i].date.time > date.time){
                    createPage(Page(date=date))
                    if(_pageNumber.value!!.minus(1) >=0) {
                        _pageNumber.value = i-1
                        behindToday = false
                    }
                    else{
                        _pageNumber.value = 0
                        behindToday = false
                    }
                    break
                }
            }
            if(behindToday && date.time<= startOfDay().time){
                createPage(Page(date=date))
                _pageNumber.value = pages.size
            }
        }
    }

    private fun retrieveEntryFromListOrNull(
        pages: List<Page>,
        pos: Int
    ):Page?{
        if(pages.isNotEmpty() && pos < pages.size && pos > -1){
            return pages[pos]
        }
        return null
    }

    fun setupDiaryEntry(page:Page,position:Int):String{
        val entryNumber = "[${position+1}]"
        return if(page.entries.size == position) "$entryNumber " else "$entryNumber ${page.entries[position]}"
    }

    fun hasPassword(): Boolean {
        if(!sharedPrefs.getBoolean(app.getString(R.string.pref_key_require_pass),false) ||
            "" == sharedPrefs.getString(app.getString(R.string.pref_key_password),"")){
            return false
        }
        return true
    }

    fun deletePage(page: Page){
        uiScope.launch {
            delete(page)
        }
    }

    private suspend fun delete(page: Page){
        withContext(dispatcher){
            database.deletePage(page.pageId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        speechRecognizer?.destroy()
    }

    private fun createPage(page: Page){
        uiScope.launch {
            insert(page)
        }
    }

    private suspend fun insert(page:Page){
        withContext(dispatcher){
            database.insert(page)
        }
    }

    private fun updatePage(page:Page){
        Log.d("JournalViewModel","update page")
        uiScope.launch {
            update(page)
        }
    }

    private suspend fun update(page:Page){
        withContext(dispatcher){
            database.update(page)
        }
        Log.d("JournalViewModel","${pages.value?.size}")
    }

    private fun clearRecording(){
        setIsRecording(false)
        speechRecognizer!!.stopListening()
    }

    private fun setSnackBarText(text: String){
        _snackBarText.value = text
    }

    fun setIsRecording(recording: Boolean){
        _isRecording.value = recording
    }

    fun setPageNumber(pageNumber: Int){
        _pageNumber.value = pageNumber
    }

    fun clearSnackBar(){
        _snackBarText.value = null
        Log.d("JournalViewModel","snackbar null")
    }

    private fun clearVoiceText(){
        _text.value = null
    }
}



