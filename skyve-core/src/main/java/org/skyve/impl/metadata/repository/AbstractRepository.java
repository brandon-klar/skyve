package org.skyve.impl.metadata.repository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.skyve.domain.Bean;
import org.skyve.domain.messages.SkyveException;
import org.skyve.domain.types.Enumeration;
import org.skyve.impl.metadata.customer.CustomerImpl;
import org.skyve.impl.metadata.repository.router.Router;
import org.skyve.impl.metadata.user.UserImpl;
import org.skyve.impl.metadata.view.WidgetReference;
import org.skyve.impl.persistence.AbstractPersistence;
import org.skyve.impl.util.UtilImpl;
import org.skyve.metadata.MetaData;
import org.skyve.metadata.MetaDataException;
import org.skyve.metadata.customer.Customer;
import org.skyve.metadata.model.Extends;
import org.skyve.metadata.model.Persistent;
import org.skyve.metadata.model.Persistent.ExtensionStrategy;
import org.skyve.metadata.model.document.Bizlet;
import org.skyve.metadata.model.document.Document;
import org.skyve.metadata.module.Module;
import org.skyve.metadata.repository.Repository;
import org.skyve.metadata.user.User;
import org.skyve.metadata.view.View;

public abstract class AbstractRepository implements Repository {
	private static AbstractRepository repository;

	protected String absolutePath;
	protected boolean loadClasses = true;
	
	/**
	 * Absolute path constructor
	 * Prevent external instantiation.
	 */
	protected AbstractRepository(String absolutePath) {
		this.absolutePath = absolutePath.replace('\\', '/');
		if (this.absolutePath.charAt(this.absolutePath.length() - 1) != '/') {
			this.absolutePath += '/';
		}
	}

	/**
	 * Absolute path and load classes constructor
	 * Prevent external instantiation.
	 */
	protected AbstractRepository(String absolutePath, boolean loadClasses) {
		this(absolutePath);
		this.loadClasses = loadClasses;
	}

	/**
	 * Default constructor
	 * Prevent external instantiation.
	 */
	protected AbstractRepository() {
		this.absolutePath = UtilImpl.getAbsoluteBasePath();
	}

	public static AbstractRepository get() {
		AbstractRepository result = THREAD_MAP.get(Long.valueOf(Thread.currentThread().getId()));
		if (result == null) {
			result = repository;
		}
		return result;
	}

	public static void set(AbstractRepository repository) {
		AbstractRepository.repository = repository;
	}

	private static final ConcurrentHashMap<Long, AbstractRepository> THREAD_MAP = new ConcurrentHashMap<>();
	
	public void setForThread() {
		THREAD_MAP.put(Long.valueOf(Thread.currentThread().getId()), this);
	}
	
	public static void removeForThread() {
		THREAD_MAP.remove(Long.valueOf(Thread.currentThread().getId()));
	}
	
	public final String ROUTER_NAME = "router";
	public final String ROUTER_NAMESPACE = ROUTER_NAME + '/';
	public final String CUSTOMERS_NAME = "customers";
	public final String CUSTOMERS_NAMESPACE = CUSTOMERS_NAME + '/';
	public final String RESOURCES_NAMESPACE = "resources/";
	public final String MODULES_NAME = "modules";
	public final String MODULES_NAMESPACE = MODULES_NAME + '/';
	protected final String CONVERTERS_NAMESPACE = "converters/";
	public final String VIEWS_NAME = "views";
	public final String VIEWS_NAMESPACE = VIEWS_NAME + '/';
	protected final String MODELS_NAME = "models";
	protected final String MODELS_NAMESPACE = MODELS_NAME + '/';
	protected final String ACTIONS_NAME = "actions";
	protected final String ACTIONS_NAMESPACE = ACTIONS_NAME + '/';
	protected final String IMAGES_NAME = "images";
	protected final String IMAGES_NAMESPACE = IMAGES_NAME + '/';
	protected final String REPORTS_NAME = "reports";
	protected final String REPORTS_NAMESPACE = REPORTS_NAME + '/';
	protected final String QUERIES_NAME = "queries";
	protected final String QUERIES_NAMESPACE = QUERIES_NAME + '/';
	public final String DOMAIN_NAME = "domain";
	protected final String DOMAIN_NAMESPACE = DOMAIN_NAME + '/';
	public final String CLIENT_NAME = "client";
	public final String CLIENT_NAMESPACE = CLIENT_NAME + '/';

	protected abstract <T extends MetaData> T get(String name);

	protected abstract void put(String name, MetaData metaData);

	/**
	 * 
	 * @param customer if <code>null</code>, the entire repository goes.
	 */
	@Override
	public void evictCachedMetaData(Customer customer) {
		AbstractPersistence persistence = AbstractPersistence.get();
		User user = persistence.getUser();
		if (customer == null) {
			persistence.disposeAllPersistenceInstances();
		}
		else {
			// TODO drop the customer's class loader
		}

		persistence = AbstractPersistence.get();
		persistence.setUser(user);
		
		classes.clear();
	}

	// class maps
	private Map<String, Class<?>> classes = new TreeMap<>();
	
	public Class<?> getJavaClass(Customer customer, String fullyQualifiedJavaCodeName) {
		Class<?> result = null;
		
		String javaCodeLocation = null;
		if (customer == null) {
			javaCodeLocation = fullyQualifiedJavaCodeName;
		}
		else {
			javaCodeLocation = ((CustomerImpl) customer).getVTable().get(fullyQualifiedJavaCodeName);
		}
		if (javaCodeLocation != null) {
			result = classes.get(javaCodeLocation);
			if (result == null) {
				synchronized (this) {
					// check again in case this thread was stalled by another in the same spot
					result = classes.get(javaCodeLocation);
					if (result == null) {
						if (loadClasses) {
							String className = javaCodeLocation.replace('/', '.');
							try {
								result = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
							}
							catch (Exception e) {
								throw new MetaDataException("A problem was encountered loading class " + className, e);
							}
						}
						else {
							// Not loading classes
							// check for a java file and return a MetaData implementation
							// NB WidgetReference is a pretty simple MetaData implementation
							if (new File(this.absolutePath + javaCodeLocation + ".java").exists()) {
								result = WidgetReference.class;
							}
						}
					}
				}
			}
		}
		
		return result;
	}
	
	/**
	 * If customer is null, we must be looking for a repository code that does not 
	 * rely on the customer's vtable - not overloaded by a customer.
	 * 
	 * @param <T> The type of the metadata.
	 * @param customer The customer to load the code for, or null
	 * @param fullyQualifiedJavaCodeName
	 * @param assertExistence
	 * @param runtime	Are we really running or just generating etc.
	 * @return a new instance of the specified java class name or null if it does not exist in the customers vtable
	 */
	@SuppressWarnings("unchecked")
	public final <T extends MetaData> T getJavaMetaData(Customer customer, 
															String fullyQualifiedJavaCodeName,
															boolean assertExistence,
															boolean runtime) {
		T result = null;
		
		Class<?> type = getJavaClass(customer, fullyQualifiedJavaCodeName);
		if (type == null) {
			if (assertExistence) {
				throw new MetaDataException(fullyQualifiedJavaCodeName + " does not exist in the customer's vtable");
			}
		}
		else {
			try {
				result = (T) type.newInstance();
				if (runtime) {
					BeanProvider.injectFields(result);
				}
			}
			catch (SkyveException e) {
				throw e;
			}
			catch (Exception e) {
				throw new MetaDataException("A problem was encountered loading class " + type, e);
			}
		}
		return result;
	}

	@Override
	public String getReportFileName(Customer customer, Document document, String reportName) {
		StringBuilder path = new StringBuilder(64);
		path.append(document.getOwningModuleName()).append('.').append(document.getName());
		path.append(".reports.").append(reportName);
		String key = path.toString();
		String result = ((CustomerImpl) customer).getVTable().get(key);
		if (result == null) {
			throw new IllegalArgumentException("Report " + reportName + " for document " + 
												document.getOwningModuleName() + '.' + document.getName() + " is not defined.");
		}

		path.setLength(0);
		path.append(absolutePath).append(result).append(".jasper");
		return path.toString();
	}

	/**
	 * Check in &lt;customer-name&gt;/&lt;module-name&gt;/resources folder, 
	 * check in &lt;module-name&gt;/resources folder, 
	 * check in &lt;customer-name&gt;/resources folder, 
	 * check in resources folder.
	 * 
	 * @param imagePath The relative path to the image
	 * @param customerName The name of the customer.
	 * @param moduleName The name of the module.
	 * @return The resource file.
	 */
	@Override
	public final File findResourceFile(String resourcePath, String customerName, String moduleName) {
		File file = null;
		StringBuilder path = new StringBuilder(64);

		if (moduleName != null) {
			// Check customer module folder, if we have a customer to play with
			if (customerName != null) {
				path.append(absolutePath);
				path.append(CUSTOMERS_NAMESPACE);
				path.append(customerName);
				path.append('/');
				path.append(moduleName);
				path.append('/');
				path.append(RESOURCES_NAMESPACE);
				path.append(resourcePath);
				file = new File(path.toString());
				if (file.exists()) {
					return file;
				}
			}
			
			// Check module folder
			path.setLength(0);
			path.append(absolutePath);
			path.append(MODULES_NAMESPACE);
			path.append(moduleName);
			path.append('/');
			path.append(RESOURCES_NAMESPACE);
			path.append(resourcePath);
			file = new File(path.toString());
			if (file.exists()) {
				return file;
			}
		}

		// Check customer folder, if we have a customer to play with
		if (customerName != null) {
			path.setLength(0);
			path.append(absolutePath);
			path.append(CUSTOMERS_NAMESPACE);
			path.append(customerName);
			path.append('/');
			path.append(RESOURCES_NAMESPACE);
			path.append(resourcePath);
			file = new File(path.toString());
			if (file.exists()) {
				return file;
			}
		}
		
		path.setLength(0);
		path.append(absolutePath);
		path.append(RESOURCES_NAMESPACE);
		path.append(resourcePath);
		return new File(path.toString());
	}

	public String getEncapsulatingClassNameForEnumeration(org.skyve.impl.metadata.model.document.field.Enumeration enumeration) {
		StringBuilder result = new StringBuilder(64);
		
		result.append(MODULES_NAME).append('.');
		String moduleName = enumeration.getModuleRef();
		if (moduleName == null) {
			moduleName = enumeration.getOwningDocument().getOwningModuleName();
		}
		result.append(moduleName).append('.');
		result.append(DOMAIN_NAME).append('.');
		String documentName = enumeration.getDocumentRef();
		if (documentName == null) {
			documentName = enumeration.getOwningDocument().getName();
		}
		result.append(documentName);

		return result.toString();
	}
	
	public Document findNearestPersistentUnmappedSuperDocument(Customer customer, Module module, Document document) {
		Document result = null;
		
		Extends inherits = document.getExtends();
		if (inherits != null) {
			result = module.getDocument(customer, inherits.getDocumentName());
			if (result == null) {
				throw new MetaDataException("Document " + document.getName() + 
												" extends document " + inherits.getDocumentName() +
												" which does not exist in module " + module.getName());
			}
			Persistent inheritsPersistent = result.getPersistent();
			if (inheritsPersistent != null) {
				if (ExtensionStrategy.mapped.equals(inheritsPersistent.getStrategy())) {
					Extends baseInherits = result.getExtends();
					if (baseInherits != null) { // only recurse if we have a base document to recurse to
						Module baseModule = getModule(customer, result.getOwningModuleName());
						result = findNearestPersistentUnmappedSuperDocument(customer, baseModule, result);
					}
				}
			}
		}
		
		if (result != null) {
			Persistent persistent = result.getPersistent();
			if ((persistent == null) || 
					(persistent.getName() == null) ||
					ExtensionStrategy.mapped.equals(persistent.getStrategy())) {
				result = null;
			}
		}
		return result;
	}

	public abstract List<String> getAllCustomerNames();

	/**
	 * Used to return all module names defined in the modules area (not customer overridden definitions).
	 * 
	 * @return
	 */
	public abstract List<String> getAllVanillaModuleNames();

	/**
	 * 
	 * @param customer Can be null, which means get the un-overridden module.
	 * @param moduleName
	 * @return
	 */
	public abstract Module getModule(Customer customer, String moduleName);

	/**
	 * 
	 * @param customer Can be null, which means get the un-overridden document.
	 * @param module
	 * @param documentName
	 * @return
	 */
	public abstract Document getDocument(Customer customer, Module module, String documentName);

	public abstract <T extends Bean> Bizlet<T> getBizlet(Customer customer, Document document, boolean runtime);

	public abstract Class<Enumeration> getEnum(org.skyve.impl.metadata.model.document.field.Enumeration enumeration);

	public abstract void validateCustomerForGenerateDomain(Customer customer);

	public abstract void validateModuleForGenerateDomain(Customer customer, Module module);

	public abstract void validateDocumentForGenerateDomain(Customer customer, Document document);

	public abstract void validateViewForGenerateDomain(Customer customer, Document document, View view, String uxui);

	/**
	 * @return The global router that is not module specific.
	 */
	public abstract Router getGlobalRouter();

	/**
	 * @return A list of module-specific routers.
	 */
	public abstract List<Router> getModuleRouters();

	/**
	 * Override the interface method on AbstractRepository to return UserImpl.
	 */
	@Override
	public abstract UserImpl retrieveUser(String userName);

	/**
	 * Return a UserImpl with the customerName and name properties set from the user principal given.
	 */
	public static UserImpl setCustomerAndUserFromPrincipal(String userPrincipal) {
		UserImpl result = null;
		if (userPrincipal != null) {
			result = new UserImpl();

			// There are 3 login situations 
			// 1. Java EE web login is used and a username which includes the customer is received eg "bizhub/mike".
			// 2. Java EE web login is used but the "CUSTOMER" parameter is set in web.xml - every login should be for this customer.
			// 3. Single Sign On (SPNEGO/KERBEROS) is used - every login is the same as the network name eg "sandsm.bizhub.com.au" and "CUSTOMER" parameter is set in web.xml
			int slashIndex = userPrincipal.indexOf('/');
			if (slashIndex >= 0) {
				String customerName = userPrincipal.substring(0, slashIndex);
				String userName = userPrincipal.substring(slashIndex + 1);
				result.setName(userName);
				result.setCustomerName(customerName);
			}
			else {
				result.setName(userPrincipal);
			}
			if (UtilImpl.CUSTOMER != null) {
				result.setCustomerName(UtilImpl.CUSTOMER);
			}
		}
		
		return result;
	}
}
