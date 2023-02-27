package knightec.bicyclepriority.presentation.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class TrafficLightHTTP {

    fun requestTraficLightStatus (ctx : Context, result: MutableLiveData<JSONObject>, url: String){
        val queue = Volley.newRequestQueue(ctx)
        val req = JsonObjectRequest(
            Request.Method.GET, url, JSONObject(),
            {
                    response -> result.value = response
            },
            {
                    error -> print("ERROR: $error")
            }
        )
        queue.add(req)
    }

}