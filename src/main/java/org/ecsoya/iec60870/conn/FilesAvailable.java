package org.ecsoya.iec60870.conn;

import java.util.ArrayList;
import java.util.List;

import org.ecsoya.iec60870.CP56Time2a;
import org.ecsoya.iec60870.asdu.ASDU;
import org.ecsoya.iec60870.asdu.CauseOfTransmission;
import org.ecsoya.iec60870.asdu.InformationObject;
import org.ecsoya.iec60870.asdu.ie.FileDirectory;
import org.ecsoya.iec60870.asdu.ie.value.NameOfFile;

public class FilesAvailable {
	public static class CS101n104File {

		public IFileProvider provider = null;

		public Object selectedBy = null;

		public CS101n104File(IFileProvider file) {
			this.provider = file;
		}

	}

	private List<CS101n104File> availableFiles = new ArrayList<CS101n104File>();

	public void addFile(IFileProvider file) {
		synchronized (availableFiles) {

			availableFiles.add(new CS101n104File(file));
		}

	}

	public CS101n104File getFile(int ca, int ioa, NameOfFile nof) {
		synchronized (availableFiles) {

			for (CS101n104File file : availableFiles) {
				if ((file.provider.getCommonAddress() == ca) && (file.provider.getInformationObjectAddress() == ioa)) {

					if (nof == NameOfFile.DEFAULT)
						return file;
					else {

						if (nof == file.provider.getNameOfFile())
							return file;
					}
				}
			}
		}

		return null;
	}

	public void removeFile(IFileProvider file) {
		synchronized (availableFiles) {

			for (CS101n104File availableFile : availableFiles) {

				if (availableFile.provider == file) {
					availableFiles.remove(availableFile);
					return;
				}

			}
		}
	}

	void sendDirectoy(IMasterConnection masterConnection, boolean spontaneous) {
		CauseOfTransmission cot;

		if (spontaneous)
			cot = CauseOfTransmission.SPONTANEOUS;
		else
			cot = CauseOfTransmission.REQUEST;

		synchronized (availableFiles) {

			int size = availableFiles.size();
			int i = 0;

			int currentCa = -1;
			int currentIOA = -1;

			ASDU directoryAsdu = null;

			for (CS101n104File file : availableFiles) {

				boolean newAsdu = false;

				if (file.provider.getCommonAddress() != currentCa) {
					currentCa = file.provider.getCommonAddress();
					newAsdu = true;
				}

				if (currentIOA != (file.provider.getInformationObjectAddress() - 1)) {
					newAsdu = true;
				}

				if (newAsdu) {
					if (directoryAsdu != null) {
						masterConnection.sendASDU(directoryAsdu);
						directoryAsdu = null;
					}
				}

				currentIOA = file.provider.getInformationObjectAddress();

				i++;

				if (directoryAsdu == null) {
					System.out.println("Send directory ASDU");
					directoryAsdu = new ASDU(masterConnection.getApplicationLayerParameters(), cot, false, false,
							(byte) 0, currentCa, true);
				}

				boolean lastFile = (i == size);

				byte sof = 0;

				if (lastFile)
					sof = 0x20;

				InformationObject io = new FileDirectory(currentIOA, file.provider.getNameOfFile(),
						file.provider.getFileSize(), sof, new CP56Time2a(file.provider.getFileDate()));

				directoryAsdu.addInformationObject(io);
			}

			if (directoryAsdu != null) {

				System.out.println("Send directory ASDU");
				masterConnection.sendASDU(directoryAsdu);
			}

		}
	}

}