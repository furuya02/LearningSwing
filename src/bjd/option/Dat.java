package bjd.option;

import java.util.ArrayList;

import bjd.ctrl.CtrlType;
import bjd.util.ListBase;

public class Dat extends ListBase<OneDat> {
	
	//private ArrayList<CtrlType> ctrlTypeList;
	private boolean[] isSecretList; //�V�[�N���b�g�����K�v�ȃJ����
	private int colMax;
	
	public Dat(ArrayList<CtrlType> ctrlTypeList) {
		//this.ctrlTypeList = ctrlTypeList;
		//�J�������̏�����
		colMax = ctrlTypeList.size();
		//isSecretList�̐���
		isSecretList = new boolean[colMax];
		for (int i = 0; i < colMax; i++) {
			isSecretList[i] = false;
			if (ctrlTypeList.get(i) == CtrlType.HIDDEN) {
				isSecretList[i] = true;
			}
		}
	}
	
	public boolean add(boolean enable, String str) {
		if (str == null) {
			throw new IllegalArgumentException("������null���n����܂���");
		}
		String[] list = str.split("\t");
		if (list.length != colMax) {
			throw new IllegalArgumentException("�J����������v���܂���");
		}

		if (!ar.add(new OneDat(enable, list, isSecretList))) {
			return false;
		}
		return true;
	}
	
    public String toReg(boolean isSecret) {
    	StringBuilder sb = new StringBuilder();
        for (OneDat o : ar) {
            if (sb.length() != 0) {
                sb.append("\b");
            }
            sb.append(o.toReg(isSecret));
        }
        return sb.toString();
    }

	public boolean fromReg(String str) {
		ar.clear();
		if (str == null) {
			return false;
		}
		if (str.equals("")) {
			return false;
		}
		// �e�s����
		String [] lines = str.split("\b");
		if (lines.length <= 0) {
			return false;
		}
		for (String l : lines) { 
			//�_�~�[��OneDat���쐬
			OneDat oneDat = new OneDat(true, new String[colMax], isSecretList);
			if (oneDat.fromReg(l)) {
				if (ar.add(oneDat)) {
					continue; // ��������
				}
			}
			//�������s
			ar.clear();
			return false;
		}
		return true;
	}
}
