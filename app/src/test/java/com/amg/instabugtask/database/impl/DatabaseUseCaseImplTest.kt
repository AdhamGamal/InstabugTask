package com.amg.instabugtask.database.impl

import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.amg.instabugtask.database.WordsContract.WordEntry.BASE_URI
import com.amg.instabugtask.database.WordsContract.WordEntry.COLUMN_NAME_CHARS
import com.amg.instabugtask.database.WordsContract.WordEntry.COLUMN_NAME_COUNT
import com.amg.instabugtask.models.Word
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS


@TestInstance(PER_CLASS)
class DatabaseUseCaseImplTest {

    private val mockContentResolver = mockk<ContentResolver>(relaxed = true)
    private val mockCursor = mockk<Cursor>(relaxed = true)

    private lateinit var instance: DatabaseUseCaseImpl

    @BeforeEach
    fun setUp() {
        // need for BASE_URI
        every { Uri.parse(any()) } returns mockk(relaxed = true)

        instance = DatabaseUseCaseImpl(mockContentResolver)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @BeforeAll
    fun beforeAll() {
        mockkStatic(Uri::class)
    }

    @AfterAll
    fun afterAll() {
        unmockkAll()
    }

    @Nested
    @DisplayName("Given getWords()")
    inner class GivenGetWordsAndCursorHaveData {

        @Nested
        @DisplayName("When cursor is not null")
        inner class WhenCursorIsNotNull {
            @BeforeEach
            fun setUp() {
                every {
                    mockContentResolver.query(
                        any(),
                        any(),
                        any(),
                        any(),
                        any()
                    )
                } returns mockCursor
            }

            @Nested
            @DisplayName("When query is not null && sortOrder is not null")
            inner class WhenQueryAndSortOrderAreNotNull {
                @Test
                @DisplayName("Then return data from cursor")
                fun test() {
                    val fakeQuery = "abc"
                    val fakeSortOrder = "def"
                    val size = 1

                    val slotColumns = slot<Array<String>>()
                    val slotSelectionClause = slot<String>()
                    val slotSelectionArgs = slot<Array<String>>()

                    every { mockCursor.count } returns size
                    every { mockCursor.moveToFirst() } returns true
                    every { mockCursor.moveToNext() } returns false

                    val words = instance.getWords(fakeQuery, fakeSortOrder)

                    verify {
                        mockContentResolver.query(
                            BASE_URI,
                            capture(slotColumns),
                            capture(slotSelectionClause),
                            capture(slotSelectionArgs),
                            fakeSortOrder
                        )
                    }

                    val columns = slotColumns.captured
                    val selectionClause = slotSelectionClause.captured
                    val selectionArgs = slotSelectionArgs.captured

                    assertEquals(columns[0], COLUMN_NAME_CHARS)
                    assertEquals(columns[1], COLUMN_NAME_COUNT)
                    assertEquals(selectionClause, "$COLUMN_NAME_CHARS LIKE ?")
                    assertEquals(selectionArgs[0], "%$fakeQuery%")

                    verify {
                        mockCursor.count
                        mockCursor.moveToFirst()
                        mockCursor.moveToNext()
                        mockCursor.close()
                    }

                    assertEquals(words.size, size)
                }
            }

            @Nested
            @DisplayName("When only sortOrder is not null")
            inner class WhenSortOrderIsNotNull {
                @Test
                @DisplayName("Then return data from cursor")
                fun test() {
                    val fakeQuery = null
                    val fakeSortOrder = "def"

                    val slotColumns = slot<Array<String>>()
                    val slotSelectionArgs = slot<Array<String>>()


                    instance.getWords(fakeQuery, fakeSortOrder)

                    verify {
                        mockContentResolver.query(
                            BASE_URI,
                            capture(slotColumns),
                            null,
                            capture(slotSelectionArgs),
                            fakeSortOrder
                        )
                    }

                    val columns = slotColumns.captured
                    val selectionArgs = slotSelectionArgs.captured

                    assertEquals(columns.size, 2)
                    assertEquals(selectionArgs.size, 0)
                }
            }

            @Nested
            @DisplayName("When only query is not null")
            inner class WhenQueryIsNotNull {
                @Nested
                @DisplayName("Given query not found")
                inner class GivenQueryNoTFound {
                    @Test
                    @DisplayName("Then return data from cursor")
                    fun test() {
                        val fakeQuery = "abc"
                        val fakeSortOrder = null
                        val size = 0

                        val slotColumns = slot<Array<String>>()
                        val slotSelectionClause = slot<String>()
                        val slotSelectionArgs = slot<Array<String>>()

                        every { mockCursor.count } returns size

                        val words = instance.getWords(fakeQuery, fakeSortOrder)

                        verify {
                            mockContentResolver.query(
                                BASE_URI,
                                capture(slotColumns),
                                capture(slotSelectionClause),
                                capture(slotSelectionArgs),
                                fakeSortOrder // null
                            )
                        }

                        val columns = slotColumns.captured
                        val selectionClause = slotSelectionClause.captured
                        val selectionArgs = slotSelectionArgs.captured

                        assertEquals(columns.size, 2)
                        assertEquals(selectionClause, "$COLUMN_NAME_CHARS LIKE ?")
                        assertEquals(selectionArgs.size, 1)

                        verify {
                            mockCursor.count
                            mockCursor.close()
                        }

                        verify(exactly = 0) {
                            mockCursor.moveToFirst()
                            mockCursor.moveToNext()
                        }

                        assertEquals(words.size, size)
                    }
                }
            }
        }

        @Nested
        @DisplayName("Given cursor is null")
        inner class GivenCursorIsNull {

            @BeforeEach
            fun setUp() {
                every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns null
            }

            @Nested
            @DisplayName("When query is not null && sortOrder is not null")
            inner class WhenQueryAndSortOrderAreNotNull {
                @Test
                @DisplayName("Then return data from cursor")
                fun test() {
                    val fakeQuery = "abc"
                    val fakeSortOrder = "def"

                    val words = instance.getWords(fakeQuery, fakeSortOrder)

                    verify { mockContentResolver.query(BASE_URI, any(), any(), any(), any()) }

                    verify(exactly = 0) {
                        mockCursor.count
                        mockCursor.moveToFirst()
                        mockCursor.moveToNext()
                        mockCursor.close()
                    }

                    assertEquals(words.size, 0)
                }
            }
        }
    }

    @Nested
    @DisplayName("Given insertWords()")
    inner class GivenInsertWords {
        @Nested
        @DisplayName("When list is not empty")
        inner class WhenListIsNotEmpty {
            @Test
            @DisplayName("Then bulkInsert and return size")
            fun test() {
                val fakeList = listOf(Word("a"), Word("b"), Word("c"), Word("d"))
                val slotContentValues = slot<Array<ContentValues>>()

                every { mockContentResolver.bulkInsert(any(), any()) } returns fakeList.size

                val result = instance.insertWords(fakeList)

                verify { mockContentResolver.bulkInsert(BASE_URI, capture(slotContentValues)) }

                val contentValues = slotContentValues.captured

                assertEquals(contentValues.size, fakeList.size)
                assertEquals(result, fakeList.size)
            }
        }

        @Nested
        @DisplayName("When list is empty")
        inner class WhenListIsEmpty {
            @Test
            @DisplayName("Then bulkInsert and return size")
            fun test() {
                val fakeList = emptyList<Word>()

                every { mockContentResolver.bulkInsert(any(), any()) } returns fakeList.size

                val result = instance.insertWords(fakeList)

                verify(exactly = 0) { mockContentResolver.bulkInsert(BASE_URI, any()) }

                assertEquals(result, fakeList.size)
            }
        }
    }

    @Nested
    @DisplayName("Given removeWords()")
    inner class GivenRemoveWords {
        @Test
        @DisplayName("Then return count")
        fun test() {
            val size = 5

            every { mockContentResolver.delete(any(), any(), any()) } returns size

            val result = instance.removeWords()

            verify { mockContentResolver.delete(BASE_URI, null, null) }

            assertEquals(result, size)
        }
    }
}