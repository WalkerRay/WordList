package model;

public class Words {
    private String word;
    private String translation;
    private String example;
    private String exampleTran;

    public Words(String word, String translation, String example, String exampleTran){
        this.word = word;
        this.translation = translation;
        this.example = example;
        this.exampleTran = exampleTran;
    }
    public Words(){

    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getExampleTran() {
        return exampleTran;
    }

    public void setExampleTran(String exampleTran) {
        this.exampleTran = exampleTran;
    }
}
