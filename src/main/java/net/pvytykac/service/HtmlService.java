package net.pvytykac.service;

import net.pvytykac.service.impl.HtmlServiceImpl.Html;

/**
 * @author Paly
 * @since 2025-02-15
 */
public interface HtmlService {

    Html fetchDocument(String url);

}
