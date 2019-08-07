var gulp = require('gulp')
var sass = require('gulp-sass')
var sourcemaps = require('gulp-sourcemaps')
var npmDist = require('gulp-npm-dist')
var rename = require('gulp-rename')
var del = require('del')

function clean () {
  // You can use multiple globbing patterns as you would with `gulp.src`,
  // for example if you are using del 2.0 or above, return its promise
  return del([ 'libs' ])
}

function compileSass () {
  return gulp.src('./scss/AIM.scss') // path to your file
    .pipe(sourcemaps.init())
    .pipe(sass({ outputStyle: 'compressed' }).on('error', sass.logError))
    // .pipe(sourcemaps.write())
    .pipe(gulp.dest('./css'))
}

function copyLibs () {
  return gulp.src(npmDist({
    copyUnminified: true
  }), { base: './node_modules/' })
    .pipe(rename(function (path) {
      path.dirname = path.dirname.replace(/\/dist/, '').replace(/\\dist/, '')
    }))
    .pipe(gulp.dest('./libs'))
}

const build = gulp.series(clean, gulp.parallel(compileSass, copyLibs))
exports.default = build
exports.sass = compileSass
