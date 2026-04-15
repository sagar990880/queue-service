# Queue Service Assignment

## About Project
This project is a simple queue service.
I have implemented two types of queues:
- InMemoryPriorityQueue → works in memory and supports priority
- UpstashQueueService → uses Redis (Upstash) with HTTP API

## What it does
- Add message to queue (push)
- Get message from queue (pull)
- Delete message
- Priority handling (in memory queue)
- Redis based queue using Upstash

## Upstash Setup
1. Create a free Redis database from https://upstash.com
2. Copy your REST URL and REST TOKEN
3. Update these values in code:

Redis_Rest_URL = "YOUR_URL"  
Redis_Rest_TOKEN = "YOUR_TOKEN"

## How to run
Run test classes:
- InMemoryPriorityQueueTest
- UpstashQueueServiceTest

## Note
Token is not added in code for security reasons. Please add your own token before running.