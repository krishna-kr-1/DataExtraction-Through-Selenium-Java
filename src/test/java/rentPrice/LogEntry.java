package rentPrice;

class LogEntry{
    int serialNo;
    String timestamp;
    String process;
    String expected;
    String actual;
    
    public LogEntry(int serialNo, String timestamp, String process, String expected, String actual) {
        this.serialNo = serialNo;
        this.timestamp = timestamp;
        this.process = process;
        this.expected = expected;
        this.actual = actual;
    }
   
}




