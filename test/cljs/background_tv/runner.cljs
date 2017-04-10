(ns background-tv.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [background-tv.core-test]))

(doo-tests 'background-tv.core-test)
