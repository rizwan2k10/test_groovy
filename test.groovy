import groovy.json.*

  def JOB_NAME = '123'

CURRENT_JOB = JOB_NAME
JOBSTATUS = "SUCCESS"
node {
        stage("test") {
         sh('uname -a')
        }
           stage('Test Robot Controller API')
           {
                print "Testing API server: ${ROBOT_CONTROLLER}";

                def r = restGet("http://${ROBOT_CONTROLLER}/jenkins/testget");
                print r;
                if (r != "hello jenkins")
                {
                    error "Response from get was invalid";
                }

                r = restPost("http://${ROBOT_CONTROLLER}/jenkins/testpost", '{"message": "Post Test Succeeded"}');
                if (!r.contains("You sent us:"))
                {
                    error "Response from post was invalid";
                }

           } 

    stage("test2") {
    step([$class: 'LinkStats',
          job_stats: "http://${ROBOT_CONTROLLER}/job_stats?buildname=${env.JOB_NAME}&buildnumber=${env.BUILD_NUMBER}"]);
    }

}
def restGet(endpoint)
{
    // Returns raw response from a get call
    // param - endpoint : full url of api call, e.g. 'http://172.27.36.205:5003/jenkins/test?username=joe'
    print "GET: ${endpoint}";

    def string = endpoint.toURL().getText();

    // Remove quotes around string, and whitespace at end and return
    string = string.replaceAll("^\"|\"\$", "");
    return string.trim()
} // close restGet

def restPost(endpoint, json_str)
{
    // Returns raw response from post call, takes an endpoint and json as input
    // param - endpoint : full url of api call, e.g. 'https://172.27.36.205:5003/jenkins/testpost'
    // param - json_str : stringified JSON object to send
    print "POST: ${endpoint} with JSON: ${json_str}";

    def post = new URL(endpoint).openConnection();

    post.setRequestMethod("POST");
    post.setDoOutput(true);
    post.setRequestProperty("Content-Type", "application/json");
    post.getOutputStream().write(json_str.getBytes("UTF-8"));

    def postRC = post.getResponseCode();
    println(postRC);
    if(postRC.equals(200))
    {
        return post.getInputStream().getText();
    } else {
        error "POST failed with endpoint: ${endpoint} and JSON: ${json_str}";
    }
} // close restPost

