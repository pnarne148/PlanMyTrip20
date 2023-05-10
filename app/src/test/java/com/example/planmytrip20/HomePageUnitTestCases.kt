package com.example.planmytrip20

import com.example.planmytrip20.classes.Item
import com.example.planmytrip20.ui.home.HomepageAdapter
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class HomepageAdapterTestCases {

    @Mock
    private lateinit var onItemClickListener: HomepageAdapter.OnItemClickListener

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

//    @Test
//    fun onCreateViewHolder_returnsNonNullViewHolder() {
//        // Arrange
//        val adapter = HomepageAdapter(emptyList(), onItemClickListener)
//
//        // Act
//        val viewHolder = adapter.onCreateViewHolder(Mockito.mock(ViewGroup::class.java), 0)
//
//        // Assert
//        assertNotNull(viewHolder)
//    }

    @Test
    fun getItemCount_returnsCorrectItemCount() {
        // Arrange
        val itemList = listOf(
            Item(R.drawable.australia.toString(), "Australia", "Description 1"),
            Item(R.drawable.canada.toString(), "Canada", "Description 2"),
            Item(R.drawable.china.toString(), "China", "Description 3")
        )
        val adapter = HomepageAdapter(itemList, onItemClickListener)

        // Act
        val itemCount = adapter.itemCount

        // Assert
        assertEquals(itemList.size, itemCount)
    }

//    @Test
//    fun onBindViewHolder_bindsDataToViewHolder() {
//        // Arrange
//        val itemList = listOf(
//            Item(R.drawable.australia, "Australia", "Description 1"),
//            Item(R.drawable.canada, "Canada", "Description 2"),
//            Item(R.drawable.china, "China", "Description 3")
//        )
//        val adapter = HomepageAdapter(itemList, onItemClickListener)
//        val viewHolder = adapter.ViewHolder(Mockito.mock(View::class.java))
//
//        // Act
//        adapter.onBindViewHolder(viewHolder, 1)
//
//        // Assert
//        assertEquals(itemList[1].Name, viewHolder.textView.text)
//        // Add more assertions to verify data binding
//    }
}
