package generator;

import java.util.ArrayList;
import java.util.BitSet;

public class BBArranger {

    public ArrayList<BB> BBs;
    public BitSet path;
    public BitSet vis;
    public ArrayList<Integer> inDegree;
    public ArrayList<Integer> arr;

    public BBArranger(ArrayList<BB> BBs) {
        this.BBs = BBs;
        this.path = new BitSet(BBs.size());
        this.vis = new BitSet(BBs.size());
        this.inDegree = new ArrayList<>();
        this.arr = new ArrayList<>();
        for (int i = 0; i < BBs.size(); i++) {
            inDegree.add(0);
        }
    }

    public void buildArrangement(int start) throws Exception {
        buildVis(start);
        buildArr(start);
    }

    private void buildVis(int id) {
        if (path.get(id))
            return;
        int oldVal = inDegree.get(id);
        inDegree.set(id, oldVal + 1);
        if (vis.get(id))
            return;
        vis.set(id);
        path.set(id);
        if (BBs.get(id).jump.jumpTy.equals("Jump")) {
            buildVis(BBs.get(id).jump.ifTrueJump);
        } else if (BBs.get(id).jump.jumpTy.equals("JumpIf")) {
            buildVis(BBs.get(id).jump.ifTrueJump);
            buildVis(BBs.get(id).jump.ifFalseJump);
        } else {
        }
        path.clear(id);
    }

    private void buildArr(int id) throws Exception {
        if (path.get(id))
            return;
        int oldVal = inDegree.get(id);
        inDegree.set(id, oldVal - 1);
        if (inDegree.get(id) != 0)
            return;
        arr.add(id);
        path.set(id);
        if (BBs.get(id).jump == null) {
            new Exception("Not all routes return!");
        } else if (BBs.get(id).jump.jumpTy.equals("Jump")) {
            buildArr(BBs.get(id).jump.ifTrueJump);
        } else if (BBs.get(id).jump.jumpTy.equals("JumpIf")) {
            buildArr(BBs.get(id).jump.ifTrueJump);
            buildArr(BBs.get(id).jump.ifFalseJump);
        } else if (BBs.get(id).jump.jumpTy.equals("Return")) {
        } else if (BBs.get(id).jump.jumpTy.equals("Unreachable")) {
            new Exception("Unreachable BB has been visited!");
        } else {
            new Exception("Cannot reach here!");
        }
        path.clear(id);
    }

    public ArrayList<Integer> arrange() {
        return this.arr;
    }

}
