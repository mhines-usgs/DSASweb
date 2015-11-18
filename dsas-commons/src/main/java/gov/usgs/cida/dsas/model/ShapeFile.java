package gov.usgs.cida.dsas.model;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

/**
 * @author isuftin
 */
public class ShapeFile implements IShapeFile, AutoCloseable {

	private DataStore ds = null;
	private File shapefileLocation = null;

	public ShapeFile(File shapefileLocation) throws IOException {
		this.shapefileLocation = shapefileLocation;
		if (this.shapefileLocation.isFile()) {
			this.shapefileLocation = this.shapefileLocation.getParentFile();
		}

		validate();

		Map<String, Object> map = new HashMap<>(1);
		map.put("url", this.shapefileLocation.toURI().toURL());
		this.ds = DataStoreFinder.getDataStore(map);
	}


	@Override
	public List<File> getRequiredFiles() {
		Collection<File> requiredFiles = FileUtils.listFiles(this.shapefileLocation, REQUIRED_FILES, false);
		return new ArrayList<>(requiredFiles);
	}

	@Override
	public List<File> getOptionalFiles() {
		Collection<File> optionalFiles = FileUtils.listFiles(this.shapefileLocation, OPTIONAL_FILES, false);
		return new ArrayList<>(optionalFiles);
	}

	@Override
	public boolean validate() throws IOException {
		if (!this.shapefileLocation.exists()) {
			throw new IOException(MessageFormat.format("File location at {0} does not exist.", shapefileLocation));
		}
		
		if (this.shapefileLocation.isFile() && this.shapefileLocation.getName().endsWith(".zip")) {
			throw new IOException("Shapefile may not be a zip file. Shapefile must point to a directory.");
		}

		Collection<File> requiredFiles = FileUtils.listFiles(this.shapefileLocation, REQUIRED_FILES, false);
		if (requiredFiles.size() < 3) {
			throw new IOException("Shapefile does not meet content requirements for a shapefile (.shp, .shx, .dbf)");
		}
		
		return true;
	}

	@Override
	public String getEPSGCode() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void close() throws Exception {
		if (this.ds != null) {
			this.ds.dispose();
		}
	}

}