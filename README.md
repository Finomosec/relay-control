# relay-control for SR-201
Project to configure and control the LAN ethernet relay (SR-201) manually
and/or by a program (eg. java, many more are possible).

Since it was such a pain to find and figure out all the information about this relay,
I wanted to share my findings with you. Enjoy!

## Hardware
LAN Ethernet Relay SR-201

You can get it from several online shops, for example: eBay, Amazon, AliExpress, etc.

![SR-201 Hardware](external%20resources/manuals/SR-201%20Hardware.jpg)

There are several models of this relay (with up to 8 channels/switches),
but they all seem to use the same firmware and protocol.

## External Resources
In the folder [external resources](external%20resources) you can find some manuals,
tools and source code in different languages to control and configure the relay
(no guarantees, I just found them on the internet... after several long searches... finally... luckily).

## Communicating with the relay

A simple telnet connection is enough to communicate with the relay.
You can use the telnet command on Linux or Windows, or any other telnet client like putty,
as well as any other tool or language that can open a TCP/telnet connection to control the relay.

The relay uses 2 ports:
* CONTROL port: `6722` (for switching)
* CONFIG port: `5111` (for configuration)

## Relay CONTROL commands
**Relay CONTROL port:** `6722`

### Query status:
> 00 = status request  

reply: 
> 11000000

Each digit is one switch: 1 = on, 0 = off.  
Depending on the model there are up to 8 switches.

### Switch on/off:

* The first digit is the command
  * 1 = turn on
  * 2 = turn off
* The second digit is the switch number (depending on your model from 1 up to 8)

> 11  = switch 1 on  
> 21  = switch 1 off  
> 12  = switch 2 on  
> 22  = switch 2 off

### Timed switching:
The number after the colon is the "number of seconds" to keep the given state, then switch to the other state.
e.g. switch on for 5 seconds, then switch off again.

> 11:5 = switch 1 on for 5 seconds then off again
> 22:999 = switch 2 off for 999 seconds then on again

## Relay CONFIG commands

**Relay CONFIG port:** `5111`

Replace `${SESSION-ID}` with a random SESSION-ID (number) and store it for later use.

All following commands need the same Session-ID or they will fail with `>ERR;`.

It looks like the Session-ID is used to prevent multiple clients to configure
the relay concurrently and so create an inconsistent state.

p.s. it also works when fully omitting the `${SESSION-ID}` on all commands,
which is very convenient when doing this manually (via terminal).

### Query Config
> #1`${SESSION-ID}`;

This also sets the SESSION-ID and overrides any previous SESSION-ID.

#### Reply:
> &gt;192.168.1.100,255.255.255.0,192.168.1.1,,0,2831,0123456789ABCD000000,192.168.1.1,connect.tutuuu.com,0;

#### Decoded:
1. Relay-IP
2. Subnet-Mask (IP)
3. Gateway (IP)
4. ?
5. Restore relay state before power off (0/1)
6. Firmware-Version (2831 = 1.0.2831)
7. Digits 1-14: Device-ID (Hex); Digits 15-20: Password for Remote Control
8. DNS-Server (IP)
9. Remote-Control-Server (Domain)
10. Remote-Control-Enabled (0/1)

### For all following commands:

#### Reply on Success:
> &gt;OK;

#### Reply on Error:
> &gt;ERR;

### Set IP:
> #2`${SESSION-ID}`,192.168.1.137;

### Set Subnet Mask:
> #3`${SESSION-ID}`,255.255.255.0;

### Set Gateway:
> #4`${SESSION-ID}`,192.168.1.1;

### Set "Enable Remote Control"
> #A`${SESSION-ID}`,1;  
> #A`${SESSION-ID}`,0;

### Set DNS:
> #8`${SESSION-ID}`,192.168.1.1;

### Set Server:
> #9`${SESSION-ID}`,connect.tutuuu.com;

### Set Password:
> #B`${SESSION-ID}`,000000;

### Set "Restore relay state before power off"
> #6`${SESSION-ID}`,1;  
> #6`${SESSION-ID}`,0;

### Restart:
> #7`${SESSION-ID}`;

### Unknown command:  
It returns `>OK;`, no matter what parameters I use.
> #5`${SESSION-ID}`;  
> #5`${SESSION-ID}`,`${PARAMS}`;
