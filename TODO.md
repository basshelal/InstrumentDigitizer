# TODO

* Perform FFT on Audio File
* Find from transform on file: Frequency, Amplitude, Phase of each overtone
* Modulation(which can be seen as having a frequency like LFO)

# Notes

* 22-Jan-19 B.Helal
 so we have a few issues, mainly easy/simple custom sound synthesis, we could do a few things.
 
    * We could use ASIO, this would be very low level and perhaps is what we want, we have more control but it's 
    definitely not easy at all.
    
    * We could use Java Sound with MIDI which would mean that we'd need to create samples and write them to some 
    soundbank or something so that we can replay them when the keyboard is struck, in this case we'd need to create 
    the samples and write them, this has issues in that we need to deal with the soundbank and instrument BS that is 
    completely hidden and difficult to understand, as well as latency issues. I don't think I like this
    
    * Use Jsyn, still don't know about this method yet but will be trying it now, I might prefer it

# Done

* ~~Start synthesizing sounds~~
* ~~Fix latency issues~~
* ~~Commit to GitHub~~
* ~~Respond to MIDI actions simply~~
* ~~Synthesize basic sounds from MIDI actions~~