package io.bcaas.vo;

public class Input {
    private Output previousOutput;
    private long sequence;
    private String scriptSignature;

    public Output getPreviousOutput() {
        return previousOutput;
    }

    public void setPreviousOutput(Output previousOutput) {
        this.previousOutput = previousOutput;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getScriptSignature() {
        return scriptSignature;
    }

    public void setScriptSignature(String scriptSignature) {
        this.scriptSignature = scriptSignature;
    }
}
