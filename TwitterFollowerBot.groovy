import twitter4j.Twitter                  
import twitter4j.TwitterFactory                  
import twitter4j.Query

log("loading config")
def config = new ConfigSlurper().parse(new File("Config.groovy").toURL())

log("connecting with username '${config.twitter.username}'")
def twitter = new TwitterFactory().getInstance(config.twitter.username, config.twitter.password)

//user list to check and follow
def userList = []

config.queries?.terms?.each { queryTerm ->
	
	def query = new Query(queryTerm)
	query.setRpp(config.queries.rpp)
	
	def queryResult = twitter.search(query)
	def tweets = queryResult?.tweets

	log("querying for '${queryTerm}' returned ${tweets.size()} results")
	tweets.each { tweet ->
		if (!userList.contains(tweet.fromUser)) {
			userList << tweet.fromUser
		}
	}
}

log("will try to follow ${userList.size()} new users")
userList.each { user ->
	
	try {
		twitter.createFriendship(user, true)
		log("Created friendship between '${config.twitter.username}' and '${user}'")
	} catch (Exception exp) { 
		log("Error trying to follow user ${user}: ${exp.message}")
	}
}

void log(message) {
	println "[${new Date()}] [Twitter robot] [${message}]"
}