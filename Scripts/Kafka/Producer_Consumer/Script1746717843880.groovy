import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

// 1. Produce data to Kafka
def produceResp = WS.sendRequest(findTestObject('Kafka/Post_to_Kafka'))

WS.verifyResponseStatusCode(produceResp, 200)

// 2. Create Kafka consumer group
def createConsumerResp = WS.sendRequest(findTestObject('Kafka/Consumer_Group'))

if (createConsumerResp.getStatusCode() == 409) {
    println("Consumer group already exists - this is acceptable")
} else {
    WS.verifyResponseStatusCode(createConsumerResp, 200)
}

// 3. Subscribe the consumer to topic
def subscribeResp = WS.sendRequest(findTestObject('Kafka/Subscribe_to_Topic'))

WS.verifyResponseStatusCode(subscribeResp, 204 // Kafka REST returns 204 No Content
    )

// 4. Consume messages from Kafka
def consumeResp = WS.sendRequest(findTestObject('Kafka/Consume_Records'))

// Debug output - CORRECTED VERSION
println("Consume Response Status: ${consumeResp.getStatusCode()}")
println("Response Headers: ${consumeResp.getHeaderFields()}")
println("Response Body: ${consumeResp.getResponseText()}")

if (consumeResp.getStatusCode() == 405) {
    throw new Exception("""
        Consumption failed with 405 Method Not Allowed. 
        Please check:
        1. Correct HTTP method (likely POST)
        2. Proper endpoint URL
        3. Required headers (Accept, Content-Type)
        4. Consumer is properly subscribed
    """)
}

WS.verifyResponseStatusCode(consumeResp, 200)