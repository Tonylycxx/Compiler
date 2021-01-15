package generator;

public class JumpInst {

    public String jumpTy;
    public int ifTrueJump;
    public int ifFalseJump;

    public JumpInst(String jumpTy) {
        this.jumpTy = jumpTy;
    }

    public JumpInst(String jumpTy, int ifTrueJump) {
        this.jumpTy = jumpTy;
        this.ifTrueJump = ifTrueJump;
    }

    public JumpInst(String jumpTy, int ifTrueJump, int ifFalseJump) {
        this.jumpTy = jumpTy;
        this.ifTrueJump = ifTrueJump;
        this.ifFalseJump = ifFalseJump;
    }
}
