const express = require('express')
const app = express()
const http = require('http').Server(app)
const bodyParser = require('body-parser')

app.use(bodyParser.urlencoded({extended: true}))
app.use(bodyParser.json())

const sqlite3 = require('sqlite3').verbose()
const database = './data.db'

let db = new sqlite3.Database(database, (err) => {
    if (err) {
        return console.error(err.message);
    }
});

let sql =
    `CREATE TABLE IF NOT EXISTS events (
     id INTEGER NOT NULL PRIMARY KEY,
     user TEXT NOT NULL,
     title TEXT NOT NULL,
     description TEXT,
     start_time BIGINT NOT NULL,
     end_time BIGINT NOT NULL,
     all_day TEXT NOT NULL
    );`


db.run(sql, (err) => {
    if (err) {
        return console.error(err.message)
    }
})

db.close()

http.listen(8000, "0.0.0.0", function() {
    console.log('Calendar backend listening on port 8000!');
});

app.get('/events', (req, res) => {

    console.log(req.query)

    let db = new sqlite3.Database(database, (err) => {
        if (err) {
            return console.error(err.message);
        }
    });

    let sql =
        `SELECT *
        FROM EVENTS
        ORDER BY
        start_time`

    db.all(sql, [], function(err, rows) {
        if (err) {
            throw err;
        }
        if (Object.keys(req.query).length !== 0) {
            res.send(parseData(rows, Number(req.query.start), Number(req.query.end)));
        } else {
            res.send(rows);
        }

    });


    db.close();

})

app.post('/events', (req, res) => {

    let db = new sqlite3.Database(database, (err) => {
        if (err) {
            return console.error(err.message);
        }
    });

    let sql =
        `INSERT INTO events (user, title, description, start_time, end_time, all_day)
        VALUES (?, ?, ?, ?, ?, ?)`

    let params = [req.body.user, req.body.title, req.body.description, req.body.start_time, req.body.end_time, req.body.all_day]


    db.run(sql, params, function(err) {
        if (err) {
            return console.error(err.message)
        } else {
            let responseObject = Object.assign(req.body)
            responseObject.ID = this.lastID
            res.send(JSON.stringify(responseObject))
            return console.log("Information inserted at " + this.lastID + ".")

        }
    })

    db.close()

})

app.delete('/events/:id', (req, res) => {

    let db = new sqlite3.Database(database, (err) => {
        if (err) {
            return console.error(err.message);
        }
    });

    let sql =
        `DELETE FROM events WHERE rowid=?`

    db.run(sql, req.params.id, (err) => {
        if (err) {
            return console.error(err.message)
        } else {
            return console.log("Success!")
        }
    })

    db.close()

    res.send("You deleted an event!")
})

app.put('/events/:id', (req, res) => {

    let db = new sqlite3.Database(database, (err) => {
        if (err) {
            return console.error(err.message);
        }
        console.log('Connected to the SQlite database.');
    });

    let sql =
        `UPDATE events
        SET user = ?,
            title = ?,
            description = ?,
            start_time = ?,
            end_time = ?,
            all_day = ?
        WHERE
            ID = ?`

    let params = [req.body.user, req.body.title, req.body.description, req.body.start_time,
        req.body.end_time, req.body.all_day, req.params.id]

    db.run(sql, (err) => {
        if (err) {
            return console.error(err.message, sql)
        } else {
            return console.log("Success!")
        }
    })

    db.close()

    res.send("You updated an event!")
})

function parseData(events, start, end) {

    let calendar = new Date()
    calendar.setTime(start)
    let endDate = calendar
    let dateLong = 86400000

    let dateObject = {}

    for (let day = 1; calendar.getTime() < end ; day++) {
        calendar.setDate(day)
        let date = calendar.getTime()
        dateObject[date] = []
        for (let i = 0; i < events.length; i++) {

            if (events[i].start_time < date + dateLong &&
                date <= events[i].end_time) {
                dateObject[date].push(events[i])
            }
        }
        day = calendar.getDate()
    }
    return dateObject
}