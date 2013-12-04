package com.navclus.ui.classview;


import navclus.ui.classdiagram.classfigure.ClassFigure;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.navclus.ui.classview"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;	
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {		
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	// salee: setImage
    public void setImage() {
    	getImageRegistry().put("int_obj", new Image(Display.getDefault(), ClassFigure.class.getResourceAsStream("int_obj.gif")));
    	getImageRegistry().put("enum_obj", new Image(Display.getDefault(), ClassFigure.class.getResourceAsStream("enum_obj.gif")));
    	getImageRegistry().put("class_obj", new Image(Display.getDefault(), ClassFigure.class.getResourceAsStream("class_obj.gif")));
    	getImageRegistry().put("methpub_obj", new Image(Display.getDefault(), ClassFigure.class.getResourceAsStream("methpub_obj.gif")));
    	getImageRegistry().put("methpri_obj", new Image(Display.getDefault(), ClassFigure.class.getResourceAsStream("methpri_obj.gif")));
    	getImageRegistry().put("methpro_obj", new Image(Display.getDefault(), ClassFigure.class.getResourceAsStream("methpro_obj.gif")));
    	getImageRegistry().put("methdef_obj", new Image(Display.getDefault(), ClassFigure.class.getResourceAsStream("methdef_obj.gif")));
    	getImageRegistry().put("field_public_obj", new Image(Display.getDefault(), ClassFigure.class.getResourceAsStream("field_public_obj.gif")));
    	getImageRegistry().put("field_private_obj", new Image(Display.getDefault(), ClassFigure.class.getResourceAsStream("field_private_obj.gif")));
    	getImageRegistry().put("field_protected_obj", new Image(Display.getDefault(), ClassFigure.class.getResourceAsStream("field_protected_obj.gif")));
    	getImageRegistry().put("field_default_obj", new Image(Display.getDefault(), ClassFigure.class.getResourceAsStream("field_default_obj.gif")));    	
    }
    
    /**
     * Returns a managed image for the given key.
     * These images must <b>not</b> be disposed by clients
     * @param key is an image id, see ProcessDesignerImages
     * @return a managed image
     * @throws IllegalArgumentException if no image for the given key could be found
     * @see de.spiritlink.procam.images.ImageRegistry
     */
    public Image getImage(final String key) {
        final Image result = getImageRegistry().get(key.toLowerCase());
//        if (result == null) {
//            final String msg = "Image for key" + key + " was null"; //$NON-NLS-1$ //$NON-NLS-2$
//            throw new IllegalArgumentException(msg); 
//        }
        return result;
    }    
}
