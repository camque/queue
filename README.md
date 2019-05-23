Queue by ZeroMetal
================================================


Installation
------------

Library is available via maven central

    <dependency>
        <groupId>com.github.camque</groupId>
        <artifactId>queue</artifactId>
        <version>1.0.0</version>
    </dependency>

Tutorial
---------------------

Inject the next bean into the class you are going to audit:

    @Inject
    private IPerformanceRecorder recorder;

Finally, in the desired method include the following lines of code at the beginning and end of the method, or where you want to audit the execution:

    private void methodJMX() {
        final long startTrx = System.currentTimeMillis();
    
        //Any business code
    
        this.recorder.registerEvent(this.getClass(), "methodJMX", System.currentTimeMillis() - startTrx, true);
    }




License
-------
GNU General Public License v3
