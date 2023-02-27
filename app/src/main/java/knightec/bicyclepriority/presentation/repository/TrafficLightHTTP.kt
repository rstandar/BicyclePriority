package knightec.bicyclepriority.presentation.repository

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class TrafficLightHTTP {

    fun requestTraficLightStatus (ctx : Context, result: JSONObject, url: String){
        val queue = Volley.newRequestQueue(ctx)
        val req = JsonObjectRequest(
            Request.Method.GET, url, JSONObject(),
            {
                    response -> result = response
            },
            {
                    error -> print("ERROR: $error")
            }
        )
        queue.add(req)
    }

}