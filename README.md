Concierge
=========

Concierge is the frontend of the Content API. It recieves HTTP requests from users, and queries Elasticsearch to respond to those requests.


Architecture
------------

There are two stacks - content-api (live) and content-api-preview (preview). Both stacks should run the same build in
production. Riff Raff will upload the Concierge artifact to a bucket for each stack. Riff raff by default will deploy to
both stacks unless specified through the Preview mode. For production this should only be used in situations such as an
emergency fix to a stack.

Concierge is responsible for fulfilling user requests for content. In the future, this may include rate-limiting and key management. Concierge is read-only: any future writable aspect of the API will likely be handled by a separate system.

It provides a set of HTTP resources that represent individual items as well as our collection structures: tags and sections. All single items (a piece of content, a section, a tag) live under the root path `/`. This provides URI-compatability with the main site. Content collections live under `/search` (for content), `/sections`, and `/tags`.

Concierge runs on EC2 machines through Autoscaling in Cloudformation. They run Ubuntu 14.04 LTS (the Trusty Tahr). You can SSH onto these machines with the `ubuntu` user, given you have our keyfile. Most of our things live in `/home/content-api/`.

The Concierge process is a Jar file, which is run through Upstart. It can be restarted with `sudo restart concierge`.


Running locally
---------------

You must have a `concierge.properties` file in `~/.gu/` which specifies override values for all the empty properties listed in `global.properties`. This will include keys for accessing AWS.

It requires an Elasticsearch cluster -- there is a script to run it locally in the `elasticsearch` directory.

To start the project itself:

```
$ sbt run
```

If it's working, you should get a response from http://localhost:8701/. There should be content under `/search`, `/sections`, and `/tags`.


Building and deploying
----------------------

The build server runs `build.sh`, which runs the `assembly` SBT target to produce a Jar. It also downloads the Logstash agent, and packages them both together into a deployable artifact.

We deploy using Riff Raff. This will upload the artifact produced by the build server to S3. The Autoscaling group will then be doubled in size, bringing up new machines which download and run the latest artifact. Once they are healthy, the old machines are killed. This all happens automatically.


Usage
-----

We have CNames for the load balancers. In production we go via Mashery who do key management and rate limiting. However, since Mashery charge us for requests made, so for internal Guardian use, we have another domain (internal-dot) which goes directly to the Concierge load balancer. Since this bypasses all rate limiting you should not check that domain into any public repositories or use it in any client-side code.

**CODE**
* http://content.code.dev-guardianapis.com/search
* http://content.code.dev-guardianapis.com/sections
* http://content.code.dev-guardianapis.com/tags

**PROD** (directly)
* http://internal.content.guardianapis.com/search
* http://internal.content.guardianapis.com/sections
* http://internal.content.guardianapis.com/tags

**PROD** (via Mashery)
* http://content.guardianapis.com/search
* http://content.guardianapis.com/sections
* http://content.guardianapis.com/tags
