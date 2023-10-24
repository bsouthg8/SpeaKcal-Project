package com.example.speakcalproject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class removeFoodEntryTest {

    @Mock
    private Context mockContext;
    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseUser mockUser;
    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockCollection;
    @Mock
    private DocumentReference mockDocument;
    @Mock
    private Task<DocumentSnapshot> mockTask;
    @Mock
    private DocumentSnapshot mockSnapshot;

    @Before
    public void setUp() throws Exception {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);

        // Mock FirebaseAuth and Firestore instances
        mockFirebaseAuth();
        mockFirebaseFirestore();

        // Mocking the Food data from the snapshot
        Map<String, Object> foodData = new HashMap<>();
        foodData.put("someDateTime", "someFoodEntry");
        when(mockSnapshot.exists()).thenReturn(true);
        when(mockSnapshot.get("Food")).thenReturn(foodData);
    }

    private void mockFirebaseAuth() {
        when(FirebaseAuth.getInstance()).thenReturn(mockAuth);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUserID");
    }

    private void mockFirebaseFirestore() {
        when(FirebaseFirestore.getInstance()).thenReturn(mockFirestore);
        when(mockFirestore.collection(anyString())).thenReturn(mockCollection);
        when(mockCollection.document(anyString())).thenReturn(mockDocument);
        when(mockDocument.get()).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockSnapshot);
    }

    @Test
    public void testRemoveFoodEntry() {
        FoodEntry clickedFood = new FoodEntry();
        String mealType = "mealSample";
        String dateTime = "someDateTime";

        UserDatabaseManagement.removeFoodEntry(mockContext, clickedFood, mealType, dateTime);

        // Verify if the Firestore method to update the document was called
        verify(mockDocument, times(1)).update(eq("Food"), anyMap());
    }

    @Test
    public void testRemoveFoodEntryWhenDocumentDoesNotExist() {
        when(mockSnapshot.exists()).thenReturn(false);

        FoodEntry clickedFood = new FoodEntry();
        String mealType = "mealSample";
        String dateTime = "someDateTime";

        UserDatabaseManagement.removeFoodEntry(mockContext, clickedFood, mealType, dateTime);

        // Ensure that the Firestore method to update the document was NOT called
        verify(mockDocument, never()).update(eq("Food"), anyMap());
    }
}


