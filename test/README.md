# Testing Guidelines

## Test classification

We employ data-centric classification of tests on the basis of:

* How fast is it?
* When should they execute (pre or post deployment)
* Should they block deployment on failure?

instead of the myriad of vague and non-standard categories such as unit, integration, acceptance, etc.
where no two teams use the same definitions.

We have just two categories: `Small` and `Large`
  
### Small

fast *AND* pre-deployment *AND* blocking

* Test is *fast* if when included in the `Small` suite, the 5 min ceiling is not broken.
* Test is *pre-deployment* if it executes before deployment. 
* Test is *blocking* if it blocks deployment on failure.
 
### Large

slow *OR* post-deployment *OR* non-blocking

* Test is *slow* if when included in the `Small` suite, the 5 min ceiling is broken.
* Test is *post-deployment* if it executes after deployment. 
* Test is *non-blocking* if it does not block the deployment process on failure. 
 
## Test promotion

A test can be promoted from Large to Small if the team agrees it satisfies the requirements.

*Note that this means a colossal (system end-to-end integration-with-3rd parties browser acceptance) 
test might as well become a `Small` test.*

## Test Pyramid

* Prefer writing `Small` tests.
* `Small` test suite should complete within five minutes.
* Write browser driving tests only for major critical paths.
* All browser driving tests (Selenium) begin their life in `Large` category. However, they
can be promoted.

## Test execution

| Suite     | SBT command                    |
| --------- | ------------------------------ |
| Small     | `sbt test` or `sbt small-test` |
| Large     | `sbt large-test`               |
| All tests | `sbt test-all`                 |

Tests are partitioned in the following tree structure in the project

```
identity-admin [root]
    |--- test
        |--- small
        |--- large
```

`Large` tests are tagged with [`Large`](./large/util/Tags.scala). Test is by default 
`Small` unless explicitly tagged.

`Large` tests are executed post-deployment via [RiffRaff hook](https://riffraff.gutools.co.uk/deployment/hooks).

## Test configuration

Tests have their own [test.conf](../conf/test.conf) which includes main app's 
[configuration file](../conf/application.conf). Environmental variables have priority:

```
identity-admin.stage = "DEV"
identity-admin.stage = ${?IDENTITY_ADMIN_STAGE}
```