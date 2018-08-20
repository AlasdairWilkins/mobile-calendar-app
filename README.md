# mobile-calendar-app

This Android calendar app uses a REST API to let the user create an event on a given day, edit or delete it later, and view all events in either calendar or list view. The app is written natively in Java with a Node server that communicates with a SQL database. The backend is deployed on Google Cloud Server. Currently, the app only supports a single user. However, the events table includes a column for users that for now is hard-coded with my name, clearing the way for a later update to support multiple users.

The app opens to the current month. Today's date and the present time are used as the default values for the creation of a new event, though users can change this by selecting a different day before pressing the "Create Event" button. They can also use date selection to see all events for that day. Users can create events spanning multiple days, set either with specific start and end times or as all-day events. For now, the calendar display has six rows to accommodate months that start on a Friday or Saturday and end on a Sunday or Monday.

Planned innovations include the addition of multiple users and a login feature.
