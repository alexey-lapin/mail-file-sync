## mail-file-sync
sync files in emails with encryption

:zap: **WIP**

### Features
- microsoft ews (exchange) sender and receiver
- smtp sender
- imap receiver (in progress)
- split files by fixed or random length
- encryption of subject and body 

### Usage
on sender side  
`java -jar mfs.jar send --transport ews --ews-url https://owa.example.com/EWS/Exchange.asmx --ews-user john --ews-pass pass file.txt`

on receiver side  
`java -jar mfs.jar receive --transport ews --ews-url https://owa.example.com/EWS/Exchange.asmx --ews-user john --ews-pass pass Wf9nj`