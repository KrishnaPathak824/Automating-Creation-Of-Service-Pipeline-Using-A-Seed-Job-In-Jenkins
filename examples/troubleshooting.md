# Troubleshooting Guide

Common issues and solutions when working with the Jenkins seed job automation system.

## Configuration Issues

### Issue: YAML Parsing Error

**Error Message:**
```
Error parsing YAML configuration file
```

**Causes:**
- Invalid YAML syntax
- Incorrect indentation
- Missing quotes around special characters

**Solutions:**
1. Validate your YAML file using an online YAML validator
2. Check indentation - YAML uses spaces, not tabs
3. Use quotes around values containing special characters like `:` or `#`

**Example of correct YAML:**
```yaml
services:
  - name: "my-service"
    description: "Service with special chars: like this"
```

### Issue: Service Not Created

**Error Message:**
```
No pipeline job created for service X
```

**Causes:**
- Service definition missing required fields
- YAML structure incorrect
- Seed job didn't complete successfully

**Solutions:**
1. Ensure service has at minimum a `name` field
2. Check seed job console output for errors
3. Verify YAML is properly indented under `services:`

## Jenkins Permission Issues

### Issue: Script Not Permitted

**Error Message:**
```
Scripts not permitted to use method groovy.lang.GroovyObject invokeMethod
```

**Solution:**
1. Go to Jenkins → Manage Jenkins → In-process Script Approval
2. Find the pending approval
3. Click "Approve"

**Common Methods to Approve:**
- `groovy.lang.GroovyObject invokeMethod`
- `new groovy.yaml.YamlSlurper`
- `method java.lang.String trim`

### Issue: Credentials Not Found

**Error Message:**
```
Could not find credentials entry with ID 'github-credentials'
```

**Solution:**
1. Go to Jenkins → Manage Jenkins → Manage Credentials
2. Add credentials with the exact ID used in `services.yaml`
3. Default IDs expected:
   - `github-credentials` for Git access
   - `docker-hub-credentials` for Docker registry

## Seed Job Issues

### Issue: Cannot Read Configuration File

**Error Message:**
```
java.io.FileNotFoundException: config/services.yaml
```

**Causes:**
- Configuration file not in repository
- Seed job workspace not updated
- Incorrect file path in seed job

**Solutions:**
1. Verify `config/services.yaml` exists in repository
2. Ensure seed job has SCM configured correctly
3. Run a clean build of the seed job
4. Check the seed job workspace for the file

### Issue: Old Pipelines Not Removed

**Observation:**
Deleted services from configuration but pipeline jobs still exist in Jenkins

**Explanation:**
By default, Job DSL doesn't remove jobs that are no longer in the configuration

**Solution:**
Add configuration to seed job to remove old jobs:

In Jenkins job configuration, under "Process Job DSLs":
- Advanced → Action for removed jobs → "Delete"

Or programmatically in the DSL script:
```groovy
queue().with {
    removedJobAction('DELETE')
}
```

## Pipeline Execution Issues

### Issue: Pipeline Cannot Find Jenkinsfile

**Error Message:**
```
ERROR: Jenkinsfile not found in repository
```

**Causes:**
- Jenkinsfile doesn't exist in service repository
- Jenkinsfile path incorrect in configuration
- Wrong branch configured

**Solutions:**
1. Ensure Jenkinsfile exists in service repository root
2. If Jenkinsfile is in subdirectory, update `jenkinsfilePath` in config:
   ```yaml
   jenkinsfilePath: "ci/Jenkinsfile"
   ```
3. Verify the branch name is correct

### Issue: Git Clone Failed

**Error Message:**
```
ERROR: Error cloning remote repo 'origin'
```

**Causes:**
- Invalid repository URL
- Incorrect credentials
- Repository doesn't exist
- Network issues

**Solutions:**
1. Verify repository URL format:
   ```yaml
   gitBaseUrl: "https://github.com"
   organization: "YourOrg"
   repo: "your-repo"  # Results in: https://github.com/YourOrg/your-repo
   ```
2. Check credentials have access to the repository
3. Test Git access manually from Jenkins agent

### Issue: Parameters Not Showing

**Observation:**
Build parameters defined in configuration don't appear in Jenkins job

**Causes:**
- Parameter definition incorrect in YAML
- Job needs to be rebuilt after seed job update

**Solutions:**
1. Verify parameter syntax in `services.yaml`:
   ```yaml
   parameters:
     - type: "string"
       name: "PARAM_NAME"
       defaultValue: "value"
       description: "Description"
   ```
2. Re-run the seed job
3. Open the pipeline job configuration to verify parameters were added

## Common Mistakes

### 1. Using Tabs Instead of Spaces in YAML

❌ **Wrong:**
```yaml
services:
	- name: "service"  # Tab character
```

✅ **Correct:**
```yaml
services:
  - name: "service"  # Two spaces
```

### 2. Incorrect Branch Name

❌ **Wrong:**
```yaml
branch: "origin/main"
```

✅ **Correct:**
```yaml
branch: "main"
```

### 3. Missing Quotes in Values with Special Characters

❌ **Wrong:**
```yaml
schedule: H 2 * * *
```

✅ **Correct:**
```yaml
schedule: "H 2 * * *"
```

### 4. Incorrect Parameter Type

❌ **Wrong:**
```yaml
parameters:
  - type: "checkbox"  # Not a valid type
```

✅ **Correct:**
```yaml
parameters:
  - type: "boolean"  # Valid types: string, boolean, choice
```

## Debugging Tips

### View Seed Job Console Output

1. Go to the seed job in Jenkins
2. Click on the latest build number
3. Click "Console Output"
4. Look for error messages or stack traces

### Validate YAML Before Committing

```bash
# Using Python
python3 -c "import yaml; yaml.safe_load(open('config/services.yaml'))"

# Using online validators
# Visit: https://www.yamllint.com/
```

### Test Individual Pipeline Creation

Create a minimal configuration with just one service to isolate issues:

```yaml
organization: "Test"
gitBaseUrl: "https://github.com"
credentials: "github-credentials"

services:
  - name: "test-service"
    repo: "test-service"
    branch: "main"
```

### Enable Debug Logging

In the seed job DSL script, add debug statements:

```groovy
println "Processing service: ${serviceName}"
println "Repository URL: ${gitBaseUrl}/${organization}/${repoName}.git"
```

## Getting Help

If you're still experiencing issues:

1. Check the seed job console output for detailed error messages
2. Review Jenkins system logs: Manage Jenkins → System Log
3. Verify all prerequisites are installed and configured
4. Check the examples directory for working configurations
5. Consult Jenkins Job DSL documentation: https://plugins.jenkins.io/job-dsl/

## Useful Commands

### View Current Jobs

```groovy
Jenkins.instance.getAllItems(Job).each { job ->
    println "Job: ${job.fullName}"
}
```

### List All Credentials

```bash
# From Jenkins Script Console (Manage Jenkins → Script Console)
com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
    com.cloudbees.plugins.credentials.common.StandardCredentials.class
).each {
    println(it.id + " - " + it.description)
}
```

### Verify YAML Structure

```python
import yaml
import json

with open('config/services.yaml') as f:
    data = yaml.safe_load(f)
    print(json.dumps(data, indent=2))
```
