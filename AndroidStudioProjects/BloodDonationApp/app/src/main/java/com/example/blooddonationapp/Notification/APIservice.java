package com.example.blooddonationapp.Notification;
import com.example.blooddonationapp.Notification.MyResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIservice {

    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key-AAAAqdlNz5k:APA91bG_K8R8opffjQsrruN4qUqokktqHg3LeOitjNtpK-t7UQrBrSabqOlQeDQlKHcpiUUq3ZeXgPj5P_KBUpvZl8Ne5biqUQtP8-Qmllm99_tC8bzhSIWZWaysV67X6KqCOkAc0pzq"

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body sender body);

}
