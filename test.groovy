
import groovy.json.*

  def JOB_NAME = '123'

CURRENT_JOB = JOB_NAME
JOBSTATUS = "SUCCESS"

stage("test") {
 sh('uname -a')
}
stage("test2") {
step([$class: 'LinkStats',
      job_stats: 'hello']);
}
