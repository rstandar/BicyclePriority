import json
import time


def sim(event, context):
    
    now = time.time() % 30
    
    resDict = {
        "status" : "green",
        "time_left" : int( 15 - ( now % 15 ) )
    }
    
    if now < 15:
        resDict["status"] = "red"

    return {
        'statusCode': 200,
        'body': json.dumps(resDict)
    }
