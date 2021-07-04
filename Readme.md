# Simple User CRD application
## Architecture
It is console multithread application which can read user input from cli and execute commands in FIFO order
There is readWrite lock system which take care of synchronization of critical section. 
System Allow multiple Readers but when Writer is writing other need to waits. Classic readers writers problem
```

                                                                                            
                                          Each consumer on separate thread                  
                                                                                            
                                                                                            
                                                +------------+                              
                                                |            |                              
                          +-------+             | Consumer 1 |                              
                          |       |        ---  |            |                              
                          |       |    ---/     +------------+                              
                          |       | --/               .                                     
+--------------+          |       |                   .                                     
|              |          | Queue |                   .                                     
|   Producer   | -------- |       |                   .                                     
|              |          |       |                   .                                     
+--------------+          |       |--\                                                      
                          |       |   ---\      +------------+            -                 
                          |       |       ---   |            |                              
                          |       |             | Consumer n |                              
                          +-------+             |            |                              
                                                +------------+                              
                                                                                            
                                                                                            
                                                                                            
```
### Commands
**ADD** Command will add new user 
**Constrants:** Id and Name muset but unique in table
**CLI Constrants** Every parameter is mandatory
```
add -name=UserName -id=1 -guid=someGuide 
```
**DELETE ALL** Command will delete user table
```
deleteAll
```
**PRINT ALL** Will print table
```
printAll
```
