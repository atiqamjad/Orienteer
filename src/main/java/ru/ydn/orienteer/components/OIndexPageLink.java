package ru.ydn.orienteer.components;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import ru.ydn.orienteer.components.properties.DisplayMode;
import ru.ydn.orienteer.web.schema.OIndexPage;

import com.orientechnologies.orient.core.index.OIndex;

public class OIndexPageLink extends BookmarkablePageLink<OIndex<?>>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IModel<DisplayMode> displayModeModel;
	public OIndexPageLink(String id, IModel<OIndex<?>> oIndexModel, PageParameters parameters)
	{
		this(id, oIndexModel, DisplayMode.VIEW, parameters);
	}
	
	public OIndexPageLink(String id, IModel<OIndex<?>> oIndexModel)
	{
		this(id, oIndexModel, DisplayMode.VIEW);
	}
	
	public OIndexPageLink(String id, IModel<OIndex<?>> oIndexModel, DisplayMode mode, PageParameters parameters)
	{
		this(id, oIndexModel, resolvePageClass(mode), mode.asModel(), parameters);
	}
	
	public OIndexPageLink(String id, IModel<OIndex<?>> oIndexModel, DisplayMode mode)
	{
		this(id, oIndexModel, resolvePageClass(mode), mode.asModel());
	}
	public <C extends Page> OIndexPageLink(String id, IModel<OIndex<?>> oIndexModel, Class<C> pageClass, 
			IModel<DisplayMode> displayModeModel, PageParameters parameters) {
		super(id, pageClass, parameters);
		setModel(oIndexModel);
		this.displayModeModel = displayModeModel;
	}
	

	public <C extends Page> OIndexPageLink(String id, IModel<OIndex<?>> oClassModel, Class<C> pageClass,
			IModel<DisplayMode> displayModeModel) {
		super(id, pageClass);
		setModel(oClassModel);
		this.displayModeModel = displayModeModel;
	}
	
	private static Class<? extends Page> resolvePageClass(DisplayMode mode)
	{
		switch (mode) {
		case VIEW:
			return OIndexPage.class;
		case EDIT:
			return OIndexPage.class;
		default:
			return OIndexPage.class;
		}
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(getModelObject()!=null);
	}

	public OIndexPageLink setPropertyNameAsBody(boolean indexNameAsBody)
	{
		setBody(indexNameAsBody?new PropertyModel<String>(getModel(), "name"):null);
		return this;
	}
	
	
	@Override
	public PageParameters getPageParameters() {
		return super.getPageParameters().add("indexName", getModelObject().getName());
	}
}