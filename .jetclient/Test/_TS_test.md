```toml
name = 'Chemist-BE-Test Script'
sortWeight = 500000
id = '6eb9113f-2d38-4f82-9121-173841bb920f'
```

#### Script

```js
jc.runRequest("/Auth/Login")

jc.testCase("Academi-year", function () {
    jc.runRequest("/Academi-year/Create academic year")

    jc.runRequest("/Academi-year/Single academic year")

    jc.runRequest("/Academi-year/Change Single academic year")

    jc.runRequest("/Academi-year/AFTER PUT: single academic year")

    jc.runRequest("/Academi-year/Delete single academic year")

    jc.runRequest("/Academi-year/AFTER DELETE: single academic year")
})

jc.testCase("School", function () {
    jc.runRequest("/School/Create school")

    jc.runRequest("/School/Single school")

    jc.runRequest("/School/Change Single school")

    jc.runRequest("/School/AFTER PUT: single school")

    jc.runRequest("/School/Delete single school")

    jc.runRequest("/School/AFTER DELETE: single school")
})

jc.testCase("Grade", function () {
    jc.runRequest("/Grade/Create grade")

    jc.runRequest("/Grade/Single grade")

    jc.runRequest("/Grade/Change Single grade")

    jc.runRequest("/Grade/AFTER PUT: single grade")

    jc.runRequest("/Grade/Delete single grade")

    jc.runRequest("/Grade/AFTER DELETE: single grade")
})

jc.testCase("SchoolClass", function () {
    jc.runRequest("/SchoolClass/Create school-class")

    jc.runRequest("/SchoolClass/Single school-class")

    jc.runRequest("/SchoolClass/Change Single school-class")

    jc.runRequest("/SchoolClass/AFTER PUT: single school-class")

    jc.runRequest("/SchoolClass/Delete single school-class")

    jc.runRequest("/SchoolClass/AFTER DELETE: single school-class")
})

jc.testCase("Room", function () {
    jc.runRequest("/Room/Create room")

    jc.runRequest("/Room/Single room")

    jc.runRequest("/Room/Change Single room")

    jc.runRequest("/Room/AFTER PUT: single room")

    jc.runRequest("/Room/Delete single room")

    jc.runRequest("/Room/AFTER DELETE: single room")
})
```
