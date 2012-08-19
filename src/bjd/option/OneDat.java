package bjd.option;

import java.util.ArrayList;
import bjd.util.IDispose;

public class OneDat implements IDispose {

	private boolean enable;
	private ArrayList<String> strList = new ArrayList<>();
	private boolean[] isSecretList;

	@Override
	public void dispose() {

	}

	public boolean isEnable() {
		return enable;
	}

	// �K�v�ɂȂ�����L���ɂ���
	// public void setEnable(boolean enable) {
	// this.enable = enable;
	// }

	public ArrayList<String> getStrList() {
		return strList;
	}

	private OneDat() {
		// �f�t�H���g�R���X�g���N�^�̉B��
	}

	public OneDat(boolean enable, String[] list, boolean[] isSecretList) {

		if (list == null || isSecretList == null || list.length != isSecretList.length) {
			throw new IllegalArgumentException("�����ɖ���������܂�");
		}

		this.enable = enable;
		this.isSecretList = new boolean[list.length];
		for (int i = 0; i < list.length; i++) {
			strList.add(list[i]);
			this.isSecretList[i] = isSecretList[i];
		}
	}

	// �@�R���X�g���N�^�Œ�`�����^�Ɉ�v���Ă��Ȃ��Ƃ�false��Ԃ�
	public boolean fromReg(String str) {
		
		if (str == null) {
			return false;
		}
		String[] tmp = str.split("\t");

		//�J�������m�F
		if (tmp.length != strList.size() + 1) {
			return false;
		}
		
		//enable�J����
		switch (tmp[0]) {
			case "":
				enable = true;
				break;
			case "#":
				enable = false;
				break;
			default:
				return false;
		}
		//�ȍ~�̕�����J����
		strList = new ArrayList<String>();
		for (int i = 1; i < tmp.length; i++) {
			strList.add(tmp[i]);
		}
		return true;
	}

	public String toReg(boolean isSecret) {
		StringBuilder sb = new StringBuilder();
		if (!enable) {
			sb.append("#");
		}
		for (int i = 0; i < strList.size(); i++) {
			sb.append('\t');
			if (isSecret && isSecretList[i]) { // �V�[�N���b�g�J����
				sb.append("***");
			} else {
				sb.append(strList.get(i));
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException(); // ����Ďg�p����Ȃ��悤�ɗ�O�i�������j�Ƃ���
	}
}
