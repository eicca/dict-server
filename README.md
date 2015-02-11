# Smart translate server

Server for smart translate chrome (more to come) extension:
https://github.com/eicca/dict-chrome

## Prerequisites

You will need [Leiningen][1] 2 or above installed.

[1]: https://github.com/technomancy/leiningen

## Setting up

```
cp profiles.clj.template profiles.clj
vim profiles.clj
```

## Running

To start a web server for the application, run:

    lein ring server-headless

## Deploying

Application should work with heroku/dokku.

## Running tests

```
lein with-profile dev test
```

## License

MIT
