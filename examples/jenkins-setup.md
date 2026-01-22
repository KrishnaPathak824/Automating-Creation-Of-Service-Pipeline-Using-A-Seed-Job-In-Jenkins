# Jenkins Setup Guide

This guide explains how to set up the seed job in Jenkins to automate pipeline creation.

## Prerequisites

1. Jenkins installed and running
2. Required Jenkins plugins:
   - Job DSL Plugin
   - Pipeline Plugin
   - Git Plugin
   - Credentials Plugin
   - Pipeline: Multibranch Plugin (optional)

## Installation Steps

### Step 1: Install Required Plugins

In Jenkins, go to:
1. `Manage Jenkins` → `Manage Plugins`
2. Go to the `Available` tab
3. Search and install:
   - Job DSL
   - Pipeline
   - Git
   - Credentials Binding (if not already installed)

### Step 2: Configure Credentials

1. Go to `Manage Jenkins` → `Manage Credentials`
2. Add credentials for:
   - GitHub/Git repository access (ID: `github-credentials`)
   - Docker registry (ID: `docker-hub-credentials`) - if using Docker
3. Use the same credential IDs as specified in `config/services.yaml`

### Step 3: Create the Seed Job

1. In Jenkins, click `New Item`
2. Enter name: `service-pipeline-seed-job`
3. Select `Freestyle project`
4. Click `OK`

### Step 4: Configure the Seed Job

**Source Code Management:**
1. Select `Git`
2. Repository URL: URL of this repository
3. Credentials: Select your GitHub credentials
4. Branch: `*/main` (or your branch name)

**Build Steps:**
1. Click `Add build step`
2. Select `Process Job DSLs`
3. Select `Look on Filesystem`
4. DSL Scripts: `seed-jobs/createServicePipelines.groovy`

**Build Triggers (Optional):**
- Check `Poll SCM` and set schedule: `H/15 * * * *` (every 15 minutes)
- Or use `GitHub hook trigger for GITScm polling` for instant updates

### Step 5: Run the Seed Job

1. Save the configuration
2. Click `Build Now`
3. Check the console output to verify success
4. You should see: "Successfully generated X service pipeline(s)"

### Step 6: Verify Pipeline Creation

After the seed job completes:
1. Go to Jenkins home page
2. You should see a folder named `[Organization]-Services`
3. Inside the folder, all service pipelines should be created
4. You can also view the `[Organization]-Services-View` for an overview

## Seed Job Permissions

The seed job requires the following permissions:

```groovy
// In Jenkins script approval, approve:
- method groovy.lang.GroovyObject invokeMethod java.lang.String java.lang.Object
- new groovy.yaml.YamlSlurper
```

To approve:
1. Go to `Manage Jenkins` → `In-process Script Approval`
2. Approve any pending script signatures

## Updating Services

To add, modify, or remove services:

1. Update `config/services.yaml`
2. Commit and push changes
3. Run the seed job (manually or wait for automatic trigger)
4. The seed job will update all pipelines accordingly

## Troubleshooting

### Issue: "Scripts not permitted to use method"

**Solution:** Go to `Manage Jenkins` → `In-process Script Approval` and approve the methods.

### Issue: "Credentials not found"

**Solution:** Verify credentials ID in Jenkins matches the one in `config/services.yaml`.

### Issue: "Cannot read configuration file"

**Solution:** Ensure the seed job has checked out the repository and the path to `config/services.yaml` is correct.

### Issue: Pipeline jobs not created

**Solution:** Check the seed job console output for errors. Ensure YAML syntax is valid.

## Advanced Configuration

### Using Different Configuration Files

You can have multiple configuration files for different environments:

```groovy
// Edit seed-jobs/createServicePipelines.groovy
def servicesConfigFile = readFileFromWorkspace('config/services-production.yaml')
```

### Organizing Jobs by Environment

Modify the folder structure in the seed job:

```groovy
folder("${organization}-${environment}-Services") {
    // ...
}
```

### Adding Notification Integration

Add to the seed job configuration:

```groovy
publishers {
    mailer('team@example.com', false, true)
}
```

## Security Best Practices

1. **Use credentials binding** for sensitive data
2. **Restrict seed job permissions** to authorized users only
3. **Review script approvals** before approving
4. **Use separate credentials** for different environments
5. **Enable audit logging** for seed job executions

## Next Steps

- Customize `pipeline-templates/Jenkinsfile.template` for your tech stack
- Add your services to `config/services.yaml`
- Set up proper credentials in Jenkins
- Configure notifications and integrations
- Review and approve any script security warnings
