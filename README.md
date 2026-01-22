# Automating Creation Of Service Pipeline Using A Seed Job In Jenkins

A comprehensive solution for automating the creation and management of Jenkins pipeline jobs for microservices using Jenkins Job DSL and seed jobs.

## Overview

This project provides a seed job-based automation system that automatically creates and maintains Jenkins pipeline jobs for multiple microservices in your organization. Instead of manually creating and configuring individual pipeline jobs for each microservice, you can simply add service definitions to a configuration file, and the seed job will automatically generate all necessary pipelines.

## Features

- 🚀 **Automated Pipeline Creation**: Automatically create pipeline jobs for all services defined in configuration
- 📋 **Centralized Configuration**: Single YAML file to manage all service pipelines
- 🔄 **Consistent Pipeline Structure**: Standardized Jenkinsfile template for all microservices
- ⚙️ **Flexible Parameters**: Support for string, boolean, and choice parameters
- 📅 **Scheduled Builds**: Configure cron schedules for automated builds
- 📁 **Organized Structure**: Automatic folder creation and view generation
- 🔧 **Easy Maintenance**: Update configuration file to add/modify/remove services

## Project Structure

```
.
├── seed-jobs/
│   └── createServicePipelines.groovy    # Main seed job DSL script
├── pipeline-templates/
│   └── Jenkinsfile.template             # Reusable Jenkinsfile template for microservices
├── config/
│   └── services.yaml                    # Service definitions and configuration
├── examples/
│   ├── jenkins-setup.md                 # Jenkins setup guide
│   └── adding-new-service.md            # Guide for adding new services
└── README.md
```

## Quick Start

### Prerequisites

- Jenkins server with the following plugins:
  - Job DSL Plugin
  - Pipeline Plugin
  - Git Plugin
  - Credentials Plugin

### Setup Instructions

1. **Clone this repository**:
   ```bash
   git clone https://github.com/KrishnaPathak824/Automating-Creation-Of-Service-Pipeline-Using-A-Seed-Job-In-Jenkins.git
   ```

2. **Configure your services**:
   - Edit `config/services.yaml` to define your microservices
   - Update organization name, Git URLs, and credentials

3. **Create the seed job in Jenkins**:
   - Create a new Freestyle project named `service-pipeline-seed-job`
   - Configure Git SCM to point to this repository
   - Add a "Process Job DSLs" build step
   - Set DSL script path to: `seed-jobs/createServicePipelines.groovy`

4. **Run the seed job**:
   - Click "Build Now" on the seed job
   - The seed job will read the configuration and create all service pipelines

5. **Verify**:
   - Check that a folder named `[YourOrganization]-Services` is created
   - All service pipelines should be inside this folder
   - A view named `[YourOrganization]-Services-View` shows all pipelines

For detailed setup instructions, see [examples/jenkins-setup.md](examples/jenkins-setup.md)

## Usage

### Adding a New Service

1. Edit `config/services.yaml`:
   ```yaml
   services:
     - name: "new-service"
       repo: "new-service"
       branch: "main"
       description: "Description of the new service"
       schedule: "H 2 * * *"
       parameters:
         - type: "string"
           name: "ENVIRONMENT"
           defaultValue: "development"
           description: "Target environment"
   ```

2. Commit and push the changes

3. Run the seed job (or wait for automatic trigger)

4. The new service pipeline will be automatically created

For more details, see [examples/adding-new-service.md](examples/adding-new-service.md)

### Modifying Existing Services

Simply update the service configuration in `config/services.yaml` and re-run the seed job. The Job DSL plugin will automatically update the existing pipeline jobs.

### Removing a Service

Remove the service entry from `config/services.yaml` and re-run the seed job. Note: You may need to manually delete the old pipeline job, or configure the seed job with the `removedJobAction` setting.

## Configuration

### services.yaml Structure

```yaml
# Global configuration
organization: "MyOrganization"
gitBaseUrl: "https://github.com"
credentials: "github-credentials"
jenkinsfilePath: "Jenkinsfile"

# Service definitions
services:
  - name: "service-name"              # Display name (required)
    repo: "repository-name"            # Git repository name
    branch: "main"                     # Git branch to build
    description: "Service description" # Pipeline description
    schedule: "H 2 * * *"             # Cron schedule (optional)
    enableConcurrentBuilds: false      # Allow concurrent builds
    parameters:                        # Build parameters (optional)
      - type: "string"
        name: "PARAM_NAME"
        defaultValue: "value"
        description: "Parameter description"
```

### Jenkinsfile Template

The `pipeline-templates/Jenkinsfile.template` provides a standardized pipeline with stages for:
- Checkout
- Environment Setup
- Build
- Test
- Code Quality Analysis
- Security Scan
- Docker Image Build
- Docker Image Push
- Deployment
- Smoke Tests

Copy this template to each microservice repository and customize as needed.

## Advanced Features

### Custom Parameters

Support for multiple parameter types:
- **String**: Text input
- **Boolean**: Checkbox
- **Choice**: Dropdown with predefined options

### Scheduled Builds

Configure cron expressions for automatic builds:
- `H 2 * * *` - Daily at 2 AM
- `H */4 * * *` - Every 4 hours
- `H 0 * * 1-5` - Weekdays at midnight

### Build Management

- Automatic log rotation (keeps last 10 builds)
- Artifact retention (keeps last 5 builds with artifacts)
- Build timeouts and timestamps
- Concurrent build control

## Benefits

✅ **Reduced Manual Work**: No need to manually create and configure pipeline jobs

✅ **Consistency**: All services follow the same pipeline structure and standards

✅ **Scalability**: Easily manage dozens or hundreds of microservices

✅ **Version Control**: Pipeline configuration is stored in Git

✅ **Quick Onboarding**: New services can be added in minutes

✅ **Centralized Management**: Single source of truth for all service configurations

## Best Practices

1. **Keep Jenkinsfile consistent** across all microservices for easier maintenance
2. **Use meaningful service names** that match your repository names
3. **Document service-specific configurations** in individual service repositories
4. **Test changes locally** using Jenkins Job DSL Playground when possible
5. **Use credentials binding** for sensitive information
6. **Review script approvals** in Jenkins before approving

## Troubleshooting

### Common Issues

1. **"Scripts not permitted to use method"**
   - Go to Manage Jenkins → In-process Script Approval
   - Approve the required methods

2. **"Credentials not found"**
   - Verify credentials ID matches between Jenkins and config/services.yaml

3. **YAML parsing errors**
   - Validate YAML syntax using an online YAML validator
   - Check indentation (YAML is whitespace-sensitive)

For more troubleshooting tips, see [examples/jenkins-setup.md](examples/jenkins-setup.md)

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## License

This project is open source and available under the MIT License.

## Author

Krishna Pathak

## Acknowledgments

- Jenkins Job DSL Plugin documentation
- Jenkins Pipeline documentation
- Community best practices for CI/CD automation