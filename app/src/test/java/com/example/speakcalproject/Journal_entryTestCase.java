package com.example.speakcalproject;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import android.util.Pair;
import android.widget.EditText;
import android.widget.ListView;

import com.example.speakcalproject.Journal_entry;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

import java.util.ArrayList;
import java.util.Collections;

@RunWith(JUnit4.class)
public class JournalEntryTest {
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockFoodRef;
    @Mock
    private Task<QuerySnapshot> mockTask;
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private QueryDocumentSnapshot mockDocument;

    private Journal_entry journalEntry;

    public JournalEntryTest(FirebaseFirestore mockFirestore) {
        this.mockFirestore = mockFirestore;
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mockFirestore.collection(anyString())).thenReturn(mockFoodRef);
        OngoingStubbing<Query> queryOngoingStubbing = when(mockFoodRef.whereEqualTo(anyString(), anyString())).thenReturn(mockTask);

        doReturn(true).when(mockTask).isSuccessful();
        doReturn(mockQuerySnapshot).when(mockTask).getResult();

        when(mockQuerySnapshot.iterator()).thenReturn(Collections.singletonList(mockDocument).iterator());

        journalEntry = new Journal_entry();
        journalEntry.db = mockFirestore;
    }



    @Test
    public void testHandleButtonClick() {
        // Arrange
        EditText editText = new EditText(null);
        editText.setText("Test Food");
        ArrayList<Pair<String, String>> foodList = new ArrayList<>();
        ListView listView = new ListView(null);
        when(mockDocument.getString(anyString())).thenReturn("100");

        // Act
        journalEntry.handleButtonClick(editText, foodList, listView, "Breakfast", "2023-10-24");

        // Assert
        assertEquals(1, foodList.size());
        assertEquals("Food: Test Food", foodList.get(0).first);
        assertEquals("Calories: 100", foodList.get(0).second);
    }
}

