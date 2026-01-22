# Example Configuration: Adding a New Service

This example shows how to add a new microservice to the automated pipeline system.

## Step 1: Add Service to Configuration

Edit `config/services.yaml` and add your service:

```yaml
  - name: "auth-service"
    repo: "auth-service"
    branch: "main"
    description: "Authentication and authorization microservice"
    schedule: "H 2 * * *"  # Daily at 2 AM
    enableConcurrentBuilds: false
    parameters:
      - type: "string"
        name: "ENVIRONMENT"
        defaultValue: "development"
        description: "Target environment for deployment"
      - type: "boolean"
        name: "RUN_TESTS"
        defaultValue: true
        description: "Run automated tests"
      - type: "choice"
        name: "LOG_LEVEL"
        choices:
          - "INFO"
          - "DEBUG"
          - "WARN"
          - "ERROR"
        description: "Logging level for the application"
```

## Step 2: Create Jenkinsfile in Your Service Repository

Copy the template from `pipeline-templates/Jenkinsfile.template` to your service repository as `Jenkinsfile`.

Customize the build, test, and deployment stages according to your service's technology stack.

## Step 3: Run the Seed Job

Run the seed job in Jenkins to automatically create the pipeline for your new service.

The seed job will:
1. Read the configuration file
2. Create a new pipeline job for your service
3. Configure all parameters and triggers
4. Set up the SCM connection to your repository

## Configuration Options

### Service Properties

- **name**: The display name for the service (required)
- **repo**: The repository name (defaults to service name if not specified)
- **branch**: The git branch to build (defaults to 'main')
- **description**: Description of the service
- **schedule**: Cron expression for scheduled builds (optional)
- **enableConcurrentBuilds**: Allow multiple builds to run simultaneously (true/false)

### Parameter Types

- **string**: Text input parameter
- **boolean**: Checkbox parameter (true/false)
- **choice**: Dropdown with predefined options

### Schedule Examples

- `H 2 * * *` - Daily at 2 AM
- `H */4 * * *` - Every 4 hours
- `H 0 * * 1-5` - Weekdays at midnight
- `H H(0-7) * * *` - Once a day between midnight and 7 AM

## Advanced Configuration

### Custom Jenkinsfile Path

If your Jenkinsfile is not in the repository root:

```yaml
jenkinsfilePath: "ci/Jenkinsfile"
```

### Different Git Hosting

For GitHub Enterprise or other Git hosting:

```yaml
gitBaseUrl: "https://git.company.com"
```

### Custom Credentials

Use different credentials for specific services:

```yaml
credentials: "github-service-account"
```

## Best Practices

1. **Keep parameters consistent** across services for easier management
2. **Use the same Jenkinsfile structure** for all microservices
3. **Test locally** before adding to production
4. **Use descriptive names** for services and parameters
5. **Document service-specific configurations** in your service repositories
