# Popular Movies App

![Screen](https://raw.githubusercontent.com/djkovrik/PopularMovies/master/images/poster.jpg)

Popular Movies App created as a part of Udacity Android Developer Nanodegree Program. The application fetches movie data using [themoviedb.org](https://www.themoviedb.org/) API.

## Features:

* Explore the most popular and top rated movies.
* Mark as favorite any movie for easy access in the future.
* Watch movie trailers.
* Read movie reviews.
* UI optimized for different screen sizes.
* Main movie information as well as cached images stored in a local database so you can browse it even offline.
* Scheduled database synchronisation with actual data from [themoviedb.org](https://www.themoviedb.org/) (once per day).

## Used libraries:

* [Facebook Stetho](http://facebook.github.io/stetho/)
* [Picasso](http://square.github.io/picasso/)
* [Retrofit](http://square.github.io/retrofit/)

## Build from the source:

In order to build the app you must provide your own API key fom themoviedb.org.
Open gradle.properties file and paste your key instead of ***YOUR_API_KEY*** text in this line:
```
MY_API_KEY="YOUR_API_KEY"
```

## License:
```
Copyright 2017, Sergey Vasilchenko

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```