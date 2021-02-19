import { danger, fail, markdown, message, peril, schedule, warn } from 'danger'

danger.message("Hello World")
const diff = danger.git.JSONDiffForFile("README.md")
console.log(JSON.stringify(diff))

