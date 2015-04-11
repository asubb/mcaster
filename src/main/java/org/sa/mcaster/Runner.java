package org.sa.mcaster;

import javax.sound.midi.*;

public class Runner {
    public static void main(final String[] args) throws InvalidMidiDataException, MidiUnavailableException, InterruptedException {
        System.out.println("List of the MIDI devices");
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        MidiDevice in = null;
        MidiDevice out = null;
        for (MidiDevice.Info info : infos) {
            System.out.println("Name:" + info.getName());
            System.out.println("\tDescription: " + info.getDescription());
            System.out.println("\tVendor: " + info.getVendor());

            MidiDevice midiDevice = MidiSystem.getMidiDevice(info);
            try {
                if (!midiDevice.isOpen()) {
                    System.out.println("Opening device");
                    try {
                        midiDevice.open();
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                    }
                }
                if (
//                        info.getName().contains("Microsoft")
//                        info.getName().contains("Fast Track")
                        info.getName().contains("LoopBe")
                        && midiDevice.getMaxReceivers() < 0 && out == null) {
                    out = midiDevice;
                    System.out.println("Got out device: " + out);
                }
                if (info.getName().contains("Fast Track") && midiDevice.getMaxTransmitters() < 0) {
                    in = midiDevice;
                    System.out.println("Got in device: " + in);
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            System.out.println("--DONE-\n");
        }

        Sequencer sequencer = MidiSystem.getSequencer();

        MyReceiver filter = new MyReceiver(out);
        if (in != null) {
            in.getTransmitter().setReceiver(filter);
        }

        sequencer.getTransmitter().setReceiver(filter);

        Sequence sequence = new Sequence(Sequence.PPQ, 80);
        Track track = sequence.createTrack();
        for (int i = 0; i < 200; i++) {
            int note = 1;
            switch (i % 4) {
                case 2:
                    note = 33;//38; // snare
                    break;
                case 1:
                    note = 31;//36; // kick
                    break;
                case 3:
                    note = 31;//36; // kick
                    break;
                case 0:
                    note = 42; // open hi hat
                    break;
            }
            ShortMessage myMsg = new ShortMessage(ShortMessage.NOTE_ON, 9, note, 93);
            ShortMessage myMsg2 = new ShortMessage(ShortMessage.NOTE_ON, 9, note, 0);
            track.add(new MidiEvent(myMsg, 0 + i * 40));
            track.add(new MidiEvent(myMsg2, (i + 1) *40));
        }
        sequencer.setSequence(sequence);
        System.out.println("Opening sequencer " + sequencer);
        sequencer.open();
        System.out.println("Opened sequencer " + sequencer);
        System.out.println("Starting sequencer " + sequencer);
        sequencer.start();
        System.out.println("Started sequencer " + sequencer);
        Thread.sleep(5000);
        System.out.println("Stopping sequencer " + sequencer);
        sequencer.stop();
        System.out.println("Stopping sequencer " + sequencer);
        System.out.println("Closing sequencer " + sequencer);
        sequencer.close();
        System.out.println("Closed sequencer " + sequencer);
//        Thread.sleep(60000);
        System.out.println("Closing in " + in);
        if (in != null) in.close();
        System.out.println("Closed in " + in);
        System.out.println("Closing out " + out);
        if (out != null) out.close();
        System.out.println("Closed out " + out);
        System.out.println("\n\n\t\tBYE!");
    }

    private static class MyReceiver implements Receiver {
        private final MidiDevice out;

        public MyReceiver(MidiDevice out) {
            this.out = out;
        }

        @Override
        public void send(MidiMessage message, long timeStamp) {
            if (message.getStatus() == ShortMessage.TIMING_CLOCK || message.getStatus() == ShortMessage.ACTIVE_SENSING) {
                return;
            }
            System.out.println("Got: message=" + message.getMessage() + ", status= " + message.getStatus() + ", " + timeStamp);
            byte[] m = message.getMessage();
            int status = m[0] & 0x00FF;
            int channel = (m[0] & 0xF0) >> 4;
            int note = m[1];
            int velocity = m[2];
            System.out.print("status=" + status + ", channel=" + channel + ", note=" + note + ", velocity=" + velocity + " [");
            for (int i = 0; i < m.length; i++) {
                System.out.print(i + ":" + (int) m[i] + ", ");
            }
            System.out.println("]");

            // send to out
            try {
                if (note ==112) note = 42;
                out.getReceiver().send(new ShortMessage(status, channel, note, velocity), 0);
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            } catch (InvalidMidiDataException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close() {

        }
    }
}