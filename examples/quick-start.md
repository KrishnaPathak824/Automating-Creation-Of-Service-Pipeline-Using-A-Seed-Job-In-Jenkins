# Quick Start Guide

Get up and running with the Jenkins seed job automation in 5 minutes!

## Prerequisites Checklist

- [ ] Jenkins server is running
- [ ] You have admin access to Jenkins
- [ ] Git plugin is installed
- [ ] Job DSL plugin is installed
- [ ] Pipeline plugin is installed

## Step-by-Step Setup

### Step 1: Install Required Plugins (2 minutes)

1. Open Jenkins web interface
2. Navigate to: **Manage Jenkins** → **Manage Plugins**
3. Click the **Available** tab
4. Search and select:
   - ✅ Job DSL
   - ✅ Pipeline
   - ✅ Git
5. Click **Install without restart**
6. Wait for installation to complete

### Step 2: Set Up Credentials (1 minute)

1. Navigate to: **Manage Jenkins** → **Manage Credentials**
2. Click **(global)** domain
3. Click **Add Credentials**
4. Fill in:
   - Kind: **Username with password**
   - Username: Your GitHub username
   - Password: Your GitHub personal access token
   - ID: `github-credentials` (must match config file!)
   - Description: GitHub Access
5. Click **OK**

### Step 3: Create the Seed Job (1 minute)

1. From Jenkins home, click **New Item**
2. Enter name: `service-pipeline-seed-job`
3. Select **Freestyle project**
4. Click **OK**

### Step 4: Configure the Seed Job (1 minute)

**Source Code Management:**
1. Select **Git**
2. Repository URL: `https://github.com/KrishnaPathak824/Automating-Creation-Of-Service-Pipeline-Using-A-Seed-Job-In-Jenkins.git`
3. Credentials: Select `github-credentials`
4. Branch Specifier: `*/main`

**Build Steps:**
1. Click **Add build step**
2. Select **Process Job DSLs**
3. Select **Look on Filesystem**
4. DSL Scripts: `seed-jobs/createServicePipelines.groovy`

**Save** the configuration

### Step 5: Customize Configuration (Optional)

If you want to use your own services:

1. Fork/clone this repository
2. Edit `config/services.yaml`
3. Update the seed job to point to your fork
4. Update organization, repositories, and service names

### Step 6: Run the Seed Job (30 seconds)

1. Go to the seed job page
2. Click **Build Now**
3. Wait for the build to complete (should take ~10 seconds)
4. Click the build number and view **Console Output**

### Step 7: Verify Success

You should see in the console output:
```
Successfully generated 5 service pipeline(s)
```

Check Jenkins home page for:
- ✅ New folder: `MyOrganization-Services`
- ✅ Inside folder: 5 pipeline jobs (user-service, product-service, etc.)
- ✅ New view: `MyOrganization-Services-View`

## What Just Happened?

The seed job:
1. ✅ Read `config/services.yaml`
2. ✅ Created a folder for organizing services
3. ✅ Generated 5 pipeline jobs (one for each service)
4. ✅ Configured each job with:
   - Git repository connection
   - Build parameters
   - Scheduled triggers
   - Build retention policies
5. ✅ Created a dashboard view

## Next Steps

### Test a Pipeline

1. Go to `MyOrganization-Services` folder
2. Click on any service (e.g., `user-service`)
3. Click **Build with Parameters**
4. Review parameters and click **Build**

**Note:** The build will fail because the repositories don't exist yet. This is expected!

### Add Your Own Services

1. **Fork this repository** to your GitHub account

2. **Update `config/services.yaml`**:
   ```yaml
   organization: "YourOrganization"
   gitBaseUrl: "https://github.com"
   
   services:
     - name: "your-service"
       repo: "your-service"
       branch: "main"
       description: "Your actual service"
   ```

3. **Update the seed job** to point to your fork

4. **Run the seed job** again

5. **Create a Jenkinsfile** in your service repository:
   - Copy `pipeline-templates/Jenkinsfile.template`
   - Save it as `Jenkinsfile` in your repository root
   - Customize build/test/deploy stages

6. **Trigger your pipeline** and watch it work!

## Configuration Quick Reference

### Minimal Service Configuration
```yaml
services:
  - name: "my-service"
    repo: "my-service"
    branch: "main"
```

### Service with Parameters
```yaml
services:
  - name: "my-service"
    repo: "my-service"
    branch: "main"
    parameters:
      - type: "string"
        name: "ENV"
        defaultValue: "dev"
      - type: "boolean"
        name: "RUN_TESTS"
        defaultValue: true
```

### Service with Schedule
```yaml
services:
  - name: "my-service"
    repo: "my-service"
    branch: "main"
    schedule: "H 2 * * *"  # Daily at 2 AM
```

## Common Issues

### "Credentials not found"
- **Solution:** Ensure credentials ID in Jenkins matches `config/services.yaml`

### "Scripts not permitted"
- **Solution:** Approve the scripts in **Manage Jenkins** → **In-process Script Approval**

### "Jenkinsfile not found"
- **Solution:** Add a Jenkinsfile to your service repository root

### "YAML parsing error"
- **Solution:** Validate YAML syntax, check indentation (use spaces, not tabs)

## Tips

💡 **Tip 1:** Start with the example configuration and modify it gradually

💡 **Tip 2:** Use `services-minimal.yaml` from examples as a template

💡 **Tip 3:** Test with one service before adding many

💡 **Tip 4:** Set up automatic triggers: Configure seed job with **Poll SCM** (`H/15 * * * *`)

💡 **Tip 5:** Use the troubleshooting guide if you encounter issues

## Help and Documentation

- 📖 [Full Setup Guide](jenkins-setup.md)
- 📖 [Adding New Services](adding-new-service.md)
- 📖 [Troubleshooting Guide](troubleshooting.md)
- 📖 [Main README](../README.md)

## Success!

If you've made it this far, you now have:
- ✅ A working seed job
- ✅ Auto-generated pipeline jobs
- ✅ A scalable system for managing microservice pipelines
- ✅ Knowledge of how to add new services

**Congratulations!** 🎉

You can now manage dozens of microservice pipelines by simply updating a configuration file!

## What's Next?

1. Customize the Jenkinsfile template for your tech stack
2. Add your actual microservices
3. Set up Docker registry credentials if using containers
4. Configure deployment targets (Kubernetes, AWS, etc.)
5. Add notifications (email, Slack, etc.)
6. Explore advanced features in `services-advanced.yaml`

---

**Questions?** Check the [troubleshooting guide](troubleshooting.md) or review the examples in this directory.
