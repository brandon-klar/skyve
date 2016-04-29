package org.skyve.wildcat.metadata.view.widget.bound;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.skyve.metadata.view.Invisible;
import org.skyve.wildcat.bind.BindUtil;
import org.skyve.wildcat.metadata.view.AbsoluteSize;
import org.skyve.wildcat.metadata.view.ConstrainableSize;
import org.skyve.wildcat.util.UtilImpl;
import org.skyve.wildcat.util.XMLUtil;

// TODO Implement the rendering of this
@XmlRootElement(namespace = XMLUtil.VIEW_NAMESPACE)
@XmlType(namespace = XMLUtil.VIEW_NAMESPACE,
			propOrder = {"pixelWidth", 
							"minPixelWidth",
							"maxPixelWidth",
							"pixelHeight",
							"minPixelHeight",
							"maxPixelHeight",
							"invisibleConditionName", 
							"visibleConditionName"})
public class ProgressBar extends AbstractBound implements Invisible, AbsoluteSize, ConstrainableSize {
	private static final long serialVersionUID = 4360024000275982927L;

	private Integer pixelWidth;
	private Integer minPixelWidth;
	private Integer maxPixelWidth;
	private Integer pixelHeight;
	private Integer minPixelHeight;
	private Integer maxPixelHeight;
	private String invisibleConditionName;

	@Override
	public Integer getPixelWidth() {
		return pixelWidth;
	}

	@Override
	@XmlAttribute(required = false)
	public void setPixelWidth(Integer pixelWidth) {
		this.pixelWidth = pixelWidth;
	}

	@Override
	public Integer getMinPixelWidth() {
		return minPixelWidth;
	}

	@Override
	@XmlAttribute(required = false)
	public void setMinPixelWidth(Integer minPixelWidth) {
		this.minPixelWidth = minPixelWidth;
	}

	@Override
	public Integer getMaxPixelWidth() {
		return maxPixelWidth;
	}

	@Override
	@XmlAttribute(required = false)
	public void setMaxPixelWidth(Integer maxPixelWidth) {
		this.maxPixelWidth = maxPixelWidth;
	}

	@Override
	public Integer getPixelHeight() {
		return pixelHeight;
	}

	@Override
	@XmlAttribute(required = false)
	public void setPixelHeight(Integer pixelHeight) {
		this.pixelHeight = pixelHeight;
	}

	@Override
	public Integer getMinPixelHeight() {
		return minPixelHeight;
	}

	@Override
	@XmlAttribute(required = false)
	public void setMinPixelHeight(Integer minPixelHeight) {
		this.minPixelHeight = minPixelHeight;
	}

	@Override
	public Integer getMaxPixelHeight() {
		return maxPixelHeight;
	}

	@Override
	@XmlAttribute(required = false)
	public void setMaxPixelHeight(Integer maxPixelHeight) {
		this.maxPixelHeight = maxPixelHeight;
	}

	@Override
	public String getInvisibleConditionName() {
		return invisibleConditionName;
	}

	@Override
	@XmlAttribute(name = "invisible", required = false)
	public void setInvisibleConditionName(String invisibleConditionName) {
		this.invisibleConditionName = UtilImpl.processStringValue(invisibleConditionName);
	}

	// to enable JAXB XML marshaling
	@SuppressWarnings("static-method")
	String getVisibleConditionName() {
		return null;
	}

	@Override
	@XmlAttribute(name = "visible", required = false)
	public void setVisibleConditionName(String visibleConditionName) {
		this.invisibleConditionName = BindUtil.negateCondition(UtilImpl.processStringValue(visibleConditionName));
	}
}
