package de.terrestris.shogun2.model.layer.source;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import de.terrestris.shogun2.model.layer.util.TileGrid;

/**
 * Data source of layers for tile data from WMS servers.
 *
 * @author Andre Henn
 * @author terrestris GmbH & Co. KG
 *
 */
@Table
@Entity
public class TileWmsLayerDataSource extends ImageWmsLayerDataSource {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@Cascade(CascadeType.SAVE_UPDATE)
	private TileGrid tileGrid;

	/**
	 * default constructor
	 */
	public TileWmsLayerDataSource(){
		super();
	}

	/**
	 * @param name
	 * @param type
	 * @param url
	 * @param width
	 * @param height
	 * @param version
	 * @param layers
	 * @param styles
	 * @param tileGrid
	 */
	public TileWmsLayerDataSource(String name, String type, String url, int width,
			int height, String version, String layerNames,  String layerStyles,
			TileGrid tileGrid) {
		super(name, type, url, width, height, version, layerNames, layerStyles);
		this.tileGrid = tileGrid;
	}

	/**
	 * @return the tileGrid
	 */
	public TileGrid getTileGrid() {
		return tileGrid;
	}

	/**
	 * @param tileGrid the tileGrid to set
	 */
	public void setTileGrid(TileGrid tileGrid) {
		this.tileGrid = tileGrid;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
	 */
	@Override
	public int hashCode() {
		// two randomly chosen prime numbers
		return new HashCodeBuilder(47, 13).
				appendSuper(super.hashCode()).
				append(getTileGrid()).
				toHashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 *
	 *      According to
	 *      http://stackoverflow.com/questions/27581/overriding-equals
	 *      -and-hashcode-in-java it is recommended only to use getter-methods
	 *      when using ORM like Hibernate
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TileWmsLayerDataSource))
			return false;
		TileWmsLayerDataSource other = (TileWmsLayerDataSource) obj;

		return new EqualsBuilder().
				appendSuper(super.equals(other)).
				append(getTileGrid(), other.getTileGrid()).
				isEquals();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}

}
