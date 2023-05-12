package eu.markustegelane.bssp;

import android.graphics.Color;
import android.graphics.Typeface;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class BlueScreen implements Serializable {
    private int background;
    private int foreground;
    private int highlight_bg;
    private int highlight_fg;

    private String[] ecodes = new String[]{"RRRRRRRRRRRRRRRR", "RRRRRRRRRRRRRRRR", "RRRRRRRRRRRRRRRR", "RRRRRRRRRRRRRRRR"};

    private String os;

    private Typeface font;

    Map<String, String> titles;
    Map<String, String> texts;
    Map<String, String[]> codefiles;

    Map<String, Boolean> bools;
    Map<String, Integer> ints;
    Map<String, String> strings;
    Map<Integer, Integer> progression;

    private final Random r;

    public BlueScreen(String base_os, boolean autosetup) {
        this.r = new Random();
        this.background = Color.argb(255, 0, 0, 0);
        this.foreground = Color.argb(255, 255, 255, 255);
        this.highlight_bg = Color.argb(255, 255, 255, 255);
        this.highlight_fg = Color.argb(255, 0, 0, 0);
        this.os = base_os;
        this.titles = new Hashtable<String, String>();
        this.texts = new Hashtable<String, String>();

        this.codefiles = new Hashtable<String, String[]>();

        this.ints = new Hashtable<String, Integer>();

        this.bools = new Hashtable<String, Boolean>();

        this.strings = new Hashtable<String, String>();

        this.progression = new Hashtable<Integer, Integer>();

        this.font = Typeface.create("Lucida Console", Typeface.NORMAL);
        if (autosetup) { SetOSSpecificDefaults(); }
    }

    public Map<String, Boolean> AllBools() { return this.bools; }
    public Map<String, Integer> AllInts() { return this.ints; }
    public Map<String, String> AllStrings() { return this.strings; }
    public Map<Integer, Integer> AllProgress() { return this.progression; }

    // blue screen properties
    public boolean GetBool(String name) {
        if (this.bools.containsKey(name)) {
            return Boolean.TRUE.equals(this.bools.get(name));
        } else {
            return false;
        }
    }

    public void SetBool(String name, boolean value) {
        if (this.bools.containsKey(name)) {
            this.bools.replace(name, value);
        } else {
            this.bools.put(name, value);
        }
    }

    public String GetString (String name) {
        switch (name) {
            case "os": return this.os;
            case "ecode1": return this.ecodes[0];
            case "ecode2": return this.ecodes[1];
            case "ecode3": return this.ecodes[2];
            case "ecode4": return this.ecodes[3];
            default:
                if (this.strings.containsKey(name)) {
                    return strings.get(name);
                } else if (this.titles.containsKey(name)) {
                    return titles.get(name);
                } else if (this.texts.containsKey(name)) {
                    return texts.get(name);
                } else {
                    return "";
                }
        }
    }

    public void ClearAllTitleTexts() {
        this.titles.clear();
        this.texts.clear();
    }

    public void ClearProgress() {
        this.progression.clear();
    }

    public void SetString(String name, String value) {
        if ("os".equals(name)) {
            this.os = value;
        } else {
            if (this.strings.containsKey(name)) {
                this.strings.replace(name, value);
            } else {
                this.strings.put(name, value);
            }
        }
    }

    public void SetTitle(String name, String value) {
        this.titles.replace(name ,value);
    }

    public void PushTitle(String name, String value) {
        this.titles.put(name, value);
    }

    public void SetText(String name, String value) {
        this.texts.replace(name ,value);
    }

    public void PushText(String name, String value) {
        this.texts.put(name, value);
    }

    // theming
    public int GetTheme(boolean bg, boolean highlight) {
        if (highlight) {
            if (bg) {
                return this.highlight_bg;
            } else {
                return this.highlight_fg;
            }
        }
        if (bg) {
            return this.background;
        } else {
            return this.foreground;
        }
    }

    public void SetTheme(int bg, int fg, boolean highlight) {
        if (highlight) {
            this.highlight_fg = fg;
            this.highlight_bg = bg;
            return;
        }
        this.background = bg;
        this.foreground = fg;
    }

    // error codes
    public String[] GetCodes() {
        return this.ecodes;
    }

    public void SetCodes(String code1, String code2, String code3, String code4) {
        this.ecodes = new String[]{ code1, code2, code3, code4 };
    }

    private int RGB(int r, int g, int b) {
        return Color.argb(255, r, g, b);
    }

    // integers
    public int GetInt(String name) {
        return this.ints.getOrDefault(name, 1);
    }

    public void SetInt(String name, int value) {
        if (this.ints.containsKey(name)) {
            this.ints.replace(name, value);
        } else {
            this.ints.put(name, value);
        }
    }

    public void SetFont(String font_family, int style) {
        this.font = Typeface.create(font_family, style);
    }

    public Typeface GetFont() {
        return this.font;
    }

    public Map<String, String> GetTitles() {
        return this.titles;
    }

    public Map<String, String> GetTexts() {
        return this.texts;
    }


    // progress keyframes
    public int GetProgression(int name)
    {
        return this.progression.getOrDefault(name, 0);
    }
    public void SetProgression(int name, int value)
    {
        if (this.progression.containsKey(name))
        {
            this.progression.replace(name, value);
        }
        else
        {
            this.progression.put(name, value);
        }
    }

    public void SetAllProgression(int[] keys, int[] values)
    {
        this.progression.clear();
        for (int i = 0; i < keys.length; i++)
        {
            this.progression.replace(keys[i], values[i]);
        }
    }

    //GenAddress uses the last function to generate multiple error address codes
    public String GenAddress(int count, int places, boolean lower)
    {
        StringBuilder ot = new StringBuilder();
        String inspir = GetString("ecode1");
        for (int i = 0; i < count; i++)
        {
            if (i == 1) { inspir = GetString("ecode2"); }
            if (i == 2) { inspir = GetString("ecode3"); }
            if (i == 3) { inspir = GetString("ecode4"); }
            if (!ot.toString().equals("")) { ot.append(", "); }
            ot.append("0x").append(GenHex(places, inspir));
        }
        if (lower) { return ot.toString().toLowerCase(); }
        return ot.toString();
    }
    //generates hexadecimal codes
    //lettercount sets the length of the actual hex code
    //inspir is a string where each character represents if the value is fixed or random
    public String GenHex(int lettercount, String inspir)
    {
        //sleep command is used to make sure that randomization works properly
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        StringBuilder output = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < lettercount; i++)
        {
            int temp = r.nextInt(15);
            char lette = ' ';
            if ((inspir + inspir).substring(i, 1).equals("R"))
            {
                if (temp < 10) { lette = (Integer.toString(temp)).charAt(0); }
                if (temp == 10) { lette = 'A'; }
                if (temp == 11) { lette = 'B'; }
                if (temp == 12) { lette = 'C'; }
                if (temp == 13) { lette = 'D'; }
                if (temp == 14) { lette = 'E'; }
                if (temp == 15) { lette = 'F'; }
            }
            else
            {
                lette = ((inspir + inspir).substring(i, 1)).charAt(0);
            }
            output.append(lette);
        }
        return output.toString();
    }

    public void PushFile(String name, String[] codes)
    {
        if (!codefiles.containsKey(name))
        {
            codefiles.put(name, codes);
        }
    }

    public Map<String, String[]> GetFiles()
    {
        return codefiles;
    }

    public void ClearFiles()
    {
        codefiles.clear();
    }

    public void RenameFile(String key, String renamed)
    {
        String[] codes;
        for (Map.Entry<String, String[]> kvp : this.GetFiles().entrySet())
        {
            if (Objects.equals(kvp.getKey(), key))
            {
                codes = kvp.getValue();
                this.codefiles.remove(key);
                this.PushFile(renamed, codes);
                break;
            }
        }
    }

    public void SetFile(String key, int subcode, String code)
    {

        for (Map.Entry<String, String[]> kvp : this.GetFiles().entrySet())
        {
            if (Objects.equals(key, kvp.getKey()))
            {
                String[] codearray = kvp.getValue();
                codearray[subcode] = code;
                this.codefiles.replace(key, codearray);
                break;
            }
        }
    }

    //GenFile generates a new file for use in Windows NT blue screen
    public String GenFile(Boolean lower)
    {
        // add code here later
        return "null.sys";
    }

    public void SetDefaultProgression()
    {
        int totalmillis = 100;
        int percent = 0;
        while (percent < 100)
        {
            int val = r.nextInt(9);
            if (val + percent > 100)
            {
                val = 100 - percent;
            }
            if (val != 0)
            {
                SetProgression(totalmillis, val);
                percent += val;
            }
            totalmillis += 100;
        }
        SetInt("progressmillis", totalmillis);
    }
    // default hacks for specific OS
    public void SetOSSpecificDefaults()
    {
        SetBool("watermark", true);
        switch (this.os)
        {
            case "BOOTMGR":
                SetTheme(RGB(0, 0, 0), RGB(192, 192, 192), false);
                SetTheme(RGB(0, 0, 0), RGB(255, 255, 255), true);
                SetFont("Consolas", Typeface.NORMAL);
                PushTitle("Main", "Windows Boot Manager");
                PushText("Troubleshooting introduction", "Windows failed to start. A recent hardware or software change might be the\ncause.To fix the problem:");
                PushText("Troubleshooting", "1. Insert your Windows installation disc and restart your computer.\n2. Choose your language settings, and then click \"Next.\"\n3. Click \"Repair your computer.\"");
                PushText("Troubleshooting without disc", "If you do not have this disc, contact your system administrator or computer\nmanufacturer for assistance.");
                PushText("Error description", "The boot selection failed because a required device is\ninaccessible.");
                PushText("Info", "Info:");
                PushText("Status", "Status:");
                PushText("Continue", "ENTER=Continue");
                PushText("Exit", "ESC=Exit");
                SetString("code", "0x0000000e");
                break;
            case "Windows 1.x/2.x":
                SetTheme(RGB(0, 0, 170), RGB(255, 255, 255), false);
                SetTheme(RGB(170, 170, 170), RGB(0, 0, 170), true);
                SetInt("blink_speed", 100);
                SetString("friendlyname", "Windows 1.x/2.x (Text mode, Standard)");
                SetBool("playsound", true);
                SetString("qr_file", "local:1");
                SetBool("font_support", false);
                SetBool("blinkblink", false);
                SetString("qr_file", "local:1");
                break;
            case "Windows 3.1x":
                SetTheme(RGB(0, 0, 170), RGB(255, 255, 255), false);
                SetTheme(RGB(170, 170, 170), RGB(0, 0, 170), true);
                SetInt("blink_speed", 100);
                PushTitle("Main", "Windows");
                PushText("No unresponsive programs", "Altough you can use CTRL+ALT+DEL to quit an application that has\nstopped responding to the system, there is no application in this\nstate.\nTo quit an application, use the application's quit or exit command,\nor choose the Close command from the Control menu.\n* Press any key to return to Windows\n* Press CTRL + ALT + DEL again to restart your computer.You will\nlose any unsaved information in all applications.");
                PushText("Prompt", "Press any key to continue");
                SetString("friendlyname", "Windows 3.1 (Text mode, Standard)");
                SetBool("font_support", false);
                SetBool("blinkblink", true);
                SetString("screen_mode", "No unresponsive programs");
                break;
            case "Windows 9x/Me":
                SetTheme(RGB(0, 0, 170), RGB(255, 255, 255), false);
                SetTheme(RGB(170, 170, 170), RGB(0, 0, 170), true);
                SetInt("blink_speed", 100);
                SetCodes("0RRRRRRRRRRRRRRR", "RRRRRRRRRRRRRRRR", "RRRRRRRRRRRRRRRR", "RRRRRRRRRRRRRRRR");
                PushTitle("Main", "Windows");
                PushTitle("System is busy", "System is busy. ");
                PushTitle("Warning", "WARNING!");
                PushText("System error", "An error has occurred. To continue:\n\nPress Enter to return to Windows, or\n\nPress CTRL + ALT + DEL to restart your computer. If you do this,\nyou will lose any unsaved information in all open applications.\n\nError: {0}");
                PushText("Application error", "A fatal exception {2} has occurred at {0}:{1}. The current\napplication will be terminated.\n\n* Press any key to terminate current application\n* Press CTRL + ALT + DEL again to restart your computer. You will\n  lose any unsaved information in all applications.");
                PushText("Driver error", "A fatal exception {2} has occurred at {0}:{1} in VXD VMM(01) +\n{2}. The current application will be terminated.\n\n* Press any key to terminate current application\n* Press CTRL + ALT + DEL again to restart your computer. You will\n  lose any unsaved information in all applications.");
                PushText("System is busy", "The system is busy waiting for the Close Program dialog box to be\ndisplayed. You can wait and see if it appears, or you can restart\nyour computer.\n\n* Press any key to return to Windows and wait.\n* Press CTRL + ALT + DEL again to restart your computer. You will\n  lose any unsaved information in programs that are running.");
                PushText("System is unresponsive", "The system is either busy or has become unstable. You can wait and\nsee if it becomes available again, or you can restart your computer.\n\n* Press any key to return to Windows and wait.\n* Press CTRL + ALT + DEL again to restart your computer. You will\n  lose any unsaved information in programs that are running.");
                PushText("Prompt", "Press any key to continue");
                SetString("friendlyname", "Windows 9x/Millennium Edition (Text mode, Standard)");
                SetBool("font_support", false);
                SetBool("blinkblink", true);
                SetString("screen_mode", "System error");
                break;
            case "Windows CE":
                SetTheme(RGB(0, 0, 128), RGB(255, 255, 255), false);
                PushText("A problem has occurred...", "A problem has occurred and Windows CE has been shut down to prevent damage to your\ncomputer.");
                PushText("CTRL+ALT+DEL message", "If you will try to restart your computer, press Ctrl+Alt+Delete.");
                PushText("Technical information", "Technical information:");
                PushText("Technical information formatting", "*** STOP: {0} ({1})");
                PushText("Restart message", "The computer will restart automatically\nafter {0} seconds.");
                SetInt("timer", 30);
                SetFont("Lucida Console", Typeface.NORMAL);
                SetString("friendlyname", "Windows CE 5.0 and later (750x400, Standard)");

                SetString("code", "IRQL_NOT_LESS_OR_EQUAL (0x0000000A)");
                break;
            case "Windows NT 3.x/4.0":
                SetTheme(RGB(0, 0, 160), RGB(170, 170, 170), false);
                PushText("Error code formatting", "*** STOP: {0} ({1})");
                PushText("CPUID formatting", "CPUID: {0} 6.3.3 irql:lf SYSVER 0xf0000565");
                PushText("Stack trace heading", "Dll Base DateStmp - Name");
                PushText("Stack trace table formatting", "{0} {1} - {2}");
                PushText("Memory address dump heading", "Address  dword dump   Build [1381]                            - Name");

                PushText("Memory address dump table", "{0} {1} {2} {3} {4} {5}           - {6}");
                PushText("Troubleshooting text", "Restart and set the recovery options in the system control panel\nor the /CRASHDEBUG system start option.");
                SetInt("blink_speed", 100);
                SetString("friendlyname", "Windows NT 4.0/3.x (Text mode, Standard)");
                for (int n = 0; n < 40; n++)
                {
                    String[] inspirn = { "RRRRRRRR", "RRRRRRRR" };
                    PushFile(GenFile(true), inspirn);
                }
                for (int n = 0; n < 4; n++)
                {
                    String[] inspirn = { "RRRRRRRR", "RRRRRRRR", "RRRRRRRR", "RRRRRRRR", "RRRRRRRR", "RRRRRRRR" };
                    PushFile(GenFile(true), inspirn);
                }
                SetBool("font_support", false);
                SetBool("blinkblink", true);

                SetString("code", "IRQL_NOT_LESS_OR_EQUAL (0x0000000A)");
                SetBool("stack_trace", true);
                break;
            case "Windows 2000":
                PushText("Error code formatting", "*** STOP: {0} ({1})");
                PushText("Troubleshooting introduction", "If this is the first time you've seen this Stop error screen,\nrestart your computer. If this screen appears again, follow\nthese steps: ");
                PushText("Troubleshooting text", "Check for viruses on your computer. Remove any newly installed\nhard drives or hard drive controllers. Check your hard drive\nto make sure it is properly configured and terminated.\nRun CHKDSK /F to check for hard drive corruption, and then\nrestart your computer.");
                PushText("Additional troubleshooting information", "Refer to your Getting Started manual for more information on\ntroubleshooting Stop errors.");
                PushText("File information", "*** Address {0} base at {1}, DateStamp {2} - {3}");
                SetFont("Lucida Console", Typeface.BOLD);
                SetString("friendlyname", "Windows 2000 Professional/Server Family (640x480, Standard)");
                SetTheme(RGB(0, 0, 128), RGB(255, 255, 255), false);
                String[] inspirw2k = { "RRRRRRRR", "RRRRRRRR", "RRRRRRRR" };
                SetString("culprit", GenFile(true));
                PushFile(GetString("culprit"), inspirw2k);
                SetString("code", "IRQL_NOT_LESS_OR_EQUAL (0x0000000A)");
                SetBool("show_description", true);
                SetBool("font_support", false);
                break;
            case "Windows XP":
                PushText("A problem has been detected...", "A problem has been detected and Windows has been shut down to prevent damage\nto your computer.");
                PushText("Troubleshooting introduction", "If this is the first time you've seen this Stop error screen,\nrestart your computer. If this screen appears again, follow\nthese steps:");
                PushText("Troubleshooting", "Check to make sure any new hardware or software is properly installed.\nIf this is a new installation, ask your hardware or software manufacturer\nfor any Windows updates you might need.\n\nIf problems continue, disable or remove any newly installed hardware\nor software. Disable BIOS memory options such as caching or shadowing.\nIf you need to use Safe mode to remove or disable components, restart\nyour computer, press F8 to select Advanced Startup Options, and then\nselect Safe Mode.");
                PushText("Technical information", "Technical information:");
                PushText("Technical information formatting", "*** STOP: {0} ({1})");
                PushText("Culprit file", "The problem seems to be caused by the following file: ");
                PushText("Physical memory dump", "Beginning dump of physical memory\nPhysical memory dump complete.");
                PushText("Technical support", "Contact your system administrator or technical support group for further\nassistance.");
                SetBool("auto", true);
                SetFont("Lucida Console", Typeface.NORMAL);
                SetString("friendlyname", "Windows XP (640x480, Standard)");
                String[] inspirb = { "RRRRRRRR", "RRRRRRRR", "RRRRRRRR" };
                SetString("culprit", GenFile(true));
                PushFile(GetString("culprit"), inspirb);
                SetTheme(RGB(0, 0, 128), RGB(255, 255, 255), false);

                SetBool("autoclose", true);
                SetString("code", "IRQL_NOT_LESS_OR_EQUAL (0x0000000A)");
                SetBool("show_description", true);
                SetBool("font_support", true);
                break;
            case "Windows Vista":
                PushText("A problem has been detected...", "A problem has been detected and Windows has been shut down to prevent damage\nto your computer.");
                PushText("Troubleshooting introduction", "If this is the first time you've seen this Stop error screen,\nrestart your computer. If this screen appears again, follow\nthese steps:");
                PushText("Troubleshooting", "Check to make sure any new hardware or software is properly installed.\nIf this is a new installation, ask your hardware or software manufacturer\nfor any Windows updates you might need.\n\nIf problems continue, disable or remove any newly installed hardware\nor software. Disable BIOS memory options such as caching or shadowing.\nIf you need to use Safe mode to remove or disable components, restart\nyour computer, press F8 to select Advanced Startup Options, and then\nselect Safe Mode.");
                PushText("Technical information", "Technical information:");
                PushText("Technical information formatting", "*** STOP: {0} ({1})");
                PushText("Collecting data for crash dump", "Collecting data for crash dump ...");
                PushText("Initializing crash dump", "Initializing disk for crash dump ...");
                PushText("Begin dump", "Beginning dump of physical memory.");
                PushText("End dump", "Physical memory dump complete.");
                PushText("Physical memory dump", "Dumping physical memory to disk:{0}");
                PushText("Culprit file", "The problem seems to be caused by the following file: ");
                PushText("Culprit file memory address", "***  {0} - Address {1} base at {2}, DateStamp {3}");
                PushText("Technical support", "Contact your system admin or technical support group for further assistance.");
                SetFont("Lucida Console", Typeface.NORMAL);
                SetString("friendlyname", "Windows Vista (640x480, Standard)");
                SetTheme(RGB(0, 0, 128), RGB(255, 255, 255), false);

                SetBool("autoclose", true);
                SetString("code", "IRQL_NOT_LESS_OR_EQUAL (0x0000000A)");
                String[] inspir = { "RRRRRRRR", "RRRRRRRR", "RRRRRRRR" };
                SetString("culprit", GenFile(true));
                PushFile(GetString("culprit"), inspir);
                SetBool("show_description", true);
                SetBool("font_support", true);
                break;
            case "Windows 7":
                PushText("A problem has been detected...", "A problem has been detected and Windows has been shut down to prevent damage\nto your computer.");
                PushText("Troubleshooting introduction", "If this is the first time you've seen this Stop error screen,\nrestart your computer. If this screen appears again, follow\nthese steps:");
                PushText("Troubleshooting", "Check to make sure any new hardware or software is properly installed.\nIf this is a new installation, ask your hardware or software manufacturer\nfor any Windows updates you might need.\n\nIf problems continue, disable or remove any newly installed hardware\nor software. Disable BIOS memory options such as caching or shadowing.\nIf you need to use Safe mode to remove or disable components, restart\nyour computer, press F8 to select Advanced Startup Options, and then\nselect Safe Mode.");
                PushText("Technical information", "Technical information:");
                PushText("Technical information formatting", "*** STOP: {0} ({1})");
                PushText("Collecting data for crash dump", "Collecting data for crash dump ...");
                PushText("Initializing crash dump", "Initializing disk for crash dump ...");
                PushText("Begin dump", "Beginning dump of physical memory.");
                PushText("End dump", "Physical memory dump complete.");
                PushText("Physical memory dump", "Dumping physical memory to disk:{0}");
                PushText("Culprit file", "The problem seems to be caused by the following file: ");
                PushText("Culprit file memory address", "***  {0} - Address {1} base at {2}, DateStamp {3}");
                PushText("Technical support", "Contact your system admin or technical support group for further assistance.");
                SetFont("Consolas", Typeface.NORMAL);
                SetString("friendlyname", "Windows 7 (640x480, ClearType)");
                SetTheme(RGB(0, 0, 128), RGB(255, 255, 255), false);

                SetBool("autoclose", true);
                SetString("code", "IRQL_NOT_LESS_OR_EQUAL (0x0000000A)");
                String[] inspirc = { "RRRRRRRR", "RRRRRRRR", "RRRRRRRR" };
                SetString("culprit", GenFile(true));
                PushFile(GetString("culprit"), inspirc);
                SetBool("show_description", true);
                SetBool("font_support", true);
                break;
            case "Windows 8/8.1":
                SetString("emoticon", ":(");
                PushText("Information text with dump", "Your PC ran into a problem and needs to restart. We're just\ncollecting some error info, and then you can restart. ({0}%\ncomplete)");
                PushText("Information text without dump", "Your PC ran into a problem that it couldn't\nhandle and now it needs to restart.");
                PushText("Error code", "You can search for the error online: {0}");
                SetFont("Segoe UI Semilight", Typeface.NORMAL);
                SetTheme(RGB(16, 113, 170), RGB(255, 255, 255), false);
                SetString("friendlyname", "Windows 8/8.1 (Native, ClearType)");
                SetInt("margin-x", 9);
                SetInt("margin-y", 12);

                SetBool("autoclose", true);
                SetString("code", "IRQL_NOT_LESS_OR_EQUAL (0x0000000A)");
                SetDefaultProgression();
                SetBool("show_description", true);
                SetBool("font_support", true);
                break;
            case "Windows 10":
                SetString("emoticon", ":(");
                PushText("Information text with dump", "Your PC ran into a problem and needs to restart. We're just\ncollecting some error info, and then we'll restart for you.");
                PushText("Information text without dump", "Your PC ran into a problem and needs to restart. We're just\ncollecting some error info, and then you can restart.");
                PushText("Additional information", "For more information about this issue and possible fixes, visit http://windows.com/stopcode");
                PushText("Culprit file", "What failed: {0}");
                PushText("Error code", "If you call a support person, give them this info:\n\nStop code: {0}");
                PushText("Progress", "{0}% complete");
                SetInt("qr_size", 110);
                SetString("qr_file", "local:0");
                SetFont("Segoe UI Semilight", Typeface.NORMAL);
                SetTheme(RGB(16, 113, 170), RGB(255, 255, 255), false);
                SetString("friendlyname", "Windows 10 (Native, ClearType)");
                SetInt("margin-x", 9);
                SetInt("margin-y", 12);

                SetBool("winxplus", true);
                SetBool("autoclose", true);
                SetBool("qr", true);
                SetBool("device", true);
                SetString("qr_file", "local:0");
                SetString("code", "IRQL_NOT_LESS_OR_EQUAL (0x0000000A)");
                SetBool("show_description", true);
                SetBool("font_support", true);
                SetDefaultProgression();
                break;
            case "Windows 11":
                SetString("emoticon", ":(");
                PushText("Information text with dump", "Your device ran into a problem and needs to restart. We're just\ncollecting some error info, and then we'll restart for you.");
                PushText("Information text without dump", "Your device ran into a problem and needs to restart. We're just\ncollecting some error info, and then you can restart.");
                PushText("Additional information", "For more information about this issue and possible fixes, visit http://windows.com/stopcode");
                PushText("Culprit file", "What failed: %s");
                PushText("Error code", "If you call a support person, give them this info:\n\nStop code: %s");
                PushText("Progress", "%s%% complete");
                SetInt("qr_size", 110);
                SetString("qr_file", "local:0");
                SetFont("Segoe UI Semilight", Typeface.NORMAL);
                SetTheme(RGB(0, 0, 128), RGB(255, 255, 255), false);
                SetString("friendlyname", "Windows 11 (Native, ClearType)");
                SetInt("margin-x", 9);
                SetInt("margin-y", 12);

                SetBool("winxplus", true);
                SetBool("autoclose", true);
                SetBool("blackscreen", false);
                SetBool("qr", true);
                SetString("qr_file", "local:0");
                SetString("code", "IRQL_NOT_LESS_OR_EQUAL (0x0000000A)");
                SetDefaultProgression();
                SetBool("show_description", true);
                SetBool("font_support", true);
                break;
        }

    }
}
