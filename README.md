# Glosbe translate service

This service transforms glosbe.com html results into json.

## API

`POST /translations` with:
```clojure
(def TranslationReq
  {:source Locale
   :target Locale
   :query s/Str})
```

Response:
```clojure
(def Meaning
  {(s/optional-key :lexical) [s/Str]
   :translated-text s/Str
   :sounds [s/Str]
   :origin-name s/Str
   :web-url s/Str})

(def Translation
  {:target Locale
   :web-url s/Str
   :meanings [Meaning]})
```

Where `(def Locale s/Str)`

## Prerequisites

You need docker installed.

## Setting up

```
docker build -t glosbe-translate .
```

Don't forget to rebuild the container in case of dependencies change.

## Running

To start a web server for the application, run:

```
docker run -it --rm -v "$PWD/src":/usr/src/app/src  -p 3000:3000 glosbe-translate
```

## Launching REPL

```
docker run -it --rm -v "$PWD/src":/usr/src/app/src -p 7888:7888 glosbe-translate repl :start :host 0.0.0.0 :port 7888
```
nREPL server will be available at `localhost:7888`

## Deploying

TODO

## Running tests

```
docker run -it --rm -v "$PWD/src":/usr/src/app/src -v "$PWD/test":/usr/src/app/test glosbe-translate with-profile dev test
```

For REPL driven development it's easier to eval `(run-tests)` in a test file.

## License

MIT
