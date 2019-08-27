package webserv

// TODO: Middleware: Check if session cookie set
//   NO : 403 + Optional HTML Meta Redirect to login (No on API, Yes on Static)
//   YES: Set user info in context

// TODO: Middleware: Check user allowed to make query
//   Might make sense to have one for each query type...