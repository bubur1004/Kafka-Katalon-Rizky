import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

// Step 1: Create a consumer instance
def createConsumerResp = WS.sendRequest(findTestObject('Kafka/Consumer_Group'))

if (createConsumerResp.getStatusCode() == 409) {
    println("Consumer group already exists.")
} else {
    WS.verifyResponseStatusCode(createConsumerResp, 200)
}

// Step 2: Subscribe the consumer to a topic
def subscribeResp = WS.sendRequest(findTestObject('Kafka/Subscribe_to_Topic'))
WS.verifyResponseStatusCode(subscribeResp, 204) // No Content = success

// Step 3: Consume messages
def consumeResp = WS.sendRequest(findTestObject('Kafka/Consume_Records'))
	
println("Status: ${consumeResp.getStatusCode()}")
println("Body: ${consumeResp.getResponseText()}")

if (consumeResp.getStatusCode() == 406) {
    throw new Exception("406 Not Acceptable - likely missing correct Accept header.")
} else if (consumeResp.getStatusCode() == 405) {
    throw new Exception("405 Method Not Allowed - check that you're using GET method.")
}

WS.verifyResponseStatusCode(consumeResp, 200)
