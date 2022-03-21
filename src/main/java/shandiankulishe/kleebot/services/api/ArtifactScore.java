package shandiankulishe.kleebot.services.api;

import java.io.File;

public class ArtifactScore {
    public static String ScoreFactory(String fileName){
        ArtifactScore score=new ArtifactScore();
        return score.getArtifactScore(fileName);
    }
    private ArtifactScore(){}
    private native String getArtifactScore(String fileName);
}
