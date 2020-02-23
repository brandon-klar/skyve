package modules.test.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.skyve.CORE;
import org.skyve.domain.messages.DomainException;
import org.skyve.impl.domain.AbstractPersistentBean;

/**
 * Inverse One To One Persistent
 * <br/>
 * One to one inverse.
 * 
 * @navhas n aggAssociation 0..1 InverseOneToOnePersistent
 * @stereotype "persistent"
 */
@XmlType
@XmlRootElement
public class InverseOneToOnePersistent extends AbstractPersistentBean {
	/**
	 * For Serialization
	 * @hidden
	 */
	private static final long serialVersionUID = 1L;

	/** @hidden */
	public static final String MODULE_NAME = "test";
	/** @hidden */
	public static final String DOCUMENT_NAME = "InverseOneToOnePersistent";

	/** @hidden */
	public static final String aggAssociationPropertyName = "aggAssociation";
	/** @hidden */
	public static final String invAggAssociationPropertyName = "invAggAssociation";

	/**
	 * Aggregated Association
	 **/
	private InverseOneToOnePersistent aggAssociation = null;
	/**
	 * Inverse
	 **/
	private InverseOneToOnePersistent invAggAssociation;

	@Override
	@XmlTransient
	public String getBizModule() {
		return InverseOneToOnePersistent.MODULE_NAME;
	}

	@Override
	@XmlTransient
	public String getBizDocument() {
		return InverseOneToOnePersistent.DOCUMENT_NAME;
	}

	public static InverseOneToOnePersistent newInstance() {
		try {
			return CORE.getUser().getCustomer().getModule(MODULE_NAME).getDocument(CORE.getUser().getCustomer(), DOCUMENT_NAME).newInstance(CORE.getUser());
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new DomainException(e);
		}
	}

	@Override
	@XmlTransient
	public String getBizKey() {
		try {
			return org.skyve.util.Binder.formatMessage(org.skyve.CORE.getUser().getCustomer(),
														"Inverse One To One Persistent",
														this);
		}
		catch (@SuppressWarnings("unused") Exception e) {
			return "Unknown";
		}
	}

	@Override
	public boolean equals(Object o) {
		return ((o instanceof InverseOneToOnePersistent) && 
					this.getBizId().equals(((InverseOneToOnePersistent) o).getBizId()));
	}

	/**
	 * {@link #aggAssociation} accessor.
	 * @return	The value.
	 **/
	public InverseOneToOnePersistent getAggAssociation() {
		return aggAssociation;
	}

	/**
	 * {@link #aggAssociation} mutator.
	 * @param aggAssociation	The new value.
	 **/
	@XmlElement
	public void setAggAssociation(InverseOneToOnePersistent aggAssociation) {
		if (this.aggAssociation != aggAssociation) {
			preset(aggAssociationPropertyName, aggAssociation);
			this.aggAssociation = aggAssociation;
		}
	}

	/**
	 * {@link #invAggAssociation} accessor.
	 * @return	The value.
	 **/
	@XmlElement
	public InverseOneToOnePersistent getInvAggAssociation() {
		return invAggAssociation;
	}

	/**
	 * {@link #invAggAssociation} mutator.
	 * @param invAggAssociation	The new value.
	 **/
	public void setInvAggAssociation(InverseOneToOnePersistent invAggAssociation) {
		if (this.invAggAssociation != invAggAssociation) {
			this.invAggAssociation = invAggAssociation;
		}
	}
}
