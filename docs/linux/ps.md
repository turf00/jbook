# Simple uses of ps

## List all processes no matter the user

a=lift the self only limit
u=user format
x=BSD format all processes

`ps aux`

## List process with command by id

u=user format
p=process id

`ps up <pid>`

## Show tree of processes

`ps fxp <pid>`

## Show parent id of process with command

p=process id
o=output format

`ps p <pid> -o ppid,cmd`

## Show processes with parent id

Uses pgrep with different delimiter to find the process children.

`ps -f -p"$(pgrep -d, -P <ppid>)"  `

## Work out what these do

`ps -ef -p <pid>`
