## Task planner

### Discription

Task planner is a REST service for creating, managing various types of tasks. It has three implementations:

1. **InMemoryTaskManager**, which storage its state in device memory
2. **FileBackedTaskManager**, which writes data to a csv-file
3. **HttpTaskManager**, which stores tasks in json format on the server

### Language, libraries

- Java _(JDK 11)_
- JUnit _5_
- Gson _2.9.0_

### Application features

- Various types of tasks, including the ability of a task to have subtasks
- History storage is realized on custom LinkedList (deletion with **O(1)**)
- Automatic sorting of tasks by time
- Storing the taskManager's data on the server using user's keys and api-token
- Test coverage - 92%

<div>
<a href="https://github.com/isthatkirill">
	<img src="https://img.shields.io/badge/created%20by-isthatkirill-blue.svg?style=flat" alt="">
</a>
</div>
