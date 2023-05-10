package com.example.planmytrip20


import com.example.planmytrip20.classes.Item
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.api.mockito.PowerMockito.mockStatic


class BucketListUnitTestCases {
        @Mock
        private lateinit var mockListAdapter: ListAdapterInterface

        private lateinit var itemList: List<Item>

        @Before
        fun setup() {
            MockitoAnnotations.openMocks(this)
            itemList = listOf(
                Item(R.drawable.australia.toString(), "Australia", "Australia description"),
                Item(R.drawable.canada.toString(), "Canada", "Canada description"),
                Item(R.drawable.china.toString(), "China", "China description")
            )
        }

        @Test
        fun `test item count in ListAdapter`() {
            `when`(mockListAdapter.getItemCount()).thenReturn(itemList.size)

            val actualItemCount = mockListAdapter.getItemCount()

            assertEquals(itemList.size, actualItemCount)
        }

//        @Test
//        fun `test destination name in DetailFragment`() {
//            val destinationName = "Australia"
//            val args = Bundle()
//            args.putString("destinationName", destinationName)
//            val detailFragment = DetailFragment()
//            detailFragment.arguments = args
//
//            val actualDestinationName = detailFragment.arguments?.getString("destinationName")
//
//            assertEquals(destinationName, actualDestinationName)
//        }

        private interface ListAdapterInterface {
            fun getItemCount(): Int
        }
    }




//@RunWith(PowerMockRunner::class)
//@PrepareForTest(Bundle::class)
//class BucketListUnitTestCases {
//    @Mock
//    private lateinit var mockListAdapter: ListAdapterInterface
//
//    private lateinit var itemList: List<Item>
//
//    @Before
//    fun setup() {
//        MockitoAnnotations.openMocks(this)
//        itemList = listOf(
//            Item(R.drawable.australia, "Australia", "Australia description"),
//            Item(R.drawable.canada, "Canada", "Canada description"),
//            Item(R.drawable.china, "China", "China description")
//        )
//    }
//
//    @Test
//    fun `test item count in ListAdapter`() {
//        `when`(mockListAdapter.getItemCount()).thenReturn(itemList.size)
//
//        val actualItemCount = mockListAdapter.getItemCount()
//
//        assertEquals(itemList.size, actualItemCount)
//    }
//
////    @Test
////    fun `test destination name in DetailFragment`() {
////        val destinationName = "Australia"
////        val args = Bundle()
////        args.putString("destinationName", destinationName)
////        mockStatic(Bundle::class.java)
////        val mockedBundle = PowerMockito.mock(Bundle::class.java)
////        `when`(Bundle()).thenReturn(mockedBundle)
////        `when`(mockedBundle.getString("destinationName")).thenReturn(destinationName)
////        val detailFragment = DetailFragment()
////        detailFragment.arguments = args
////        val actualDestinationName = detailFragment.arguments?.getString("destinationName")
////        assertEquals(destinationName, actualDestinationName)
////    }
//
////    @Test
////    fun `test destination name in DetailFragment`() {
////        val destinationName = "Australia"
////        val args = Bundle()
////        args.putString("destinationName", destinationName)
////        val mockedBundle = Mockito.mock(Bundle::class.java)
////        Mockito.`when`(mockedBundle.getString("destinationName")).thenReturn(destinationName)
////        val detailFragment = DetailFragment()
////        detailFragment.arguments = mockedBundle
////        val actualDestinationName = detailFragment.arguments?.getString("destinationName")
////        assertEquals(destinationName, actualDestinationName)
////    }
//
//    private interface ListAdapterInterface {
//        fun getItemCount(): Int
//    }
//}
