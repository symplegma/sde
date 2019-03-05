/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package visual;

import climax.universe;
import visual.GPDomain.DomainType;

/**
 *
 * @author pchr
 */
public interface SDEgraphicsPanel {
    public boolean existGPDomain(int id, DomainType theType);
    public void setUniverse(universe theUniverse);
    public GPDomain getGPDomain(int id, DomainType theType);
    public void setIsoScale(boolean b);
    public void setPlotDomainTrue(int id, DomainType theType);
    public void setPlotDomainFalse(int id, DomainType theType);
    public void setPlotDomainTrue(int id, DomainType theType, boolean single);
    public void setPlotDomainTrue();
    public void setPlotDomainFalse();
    public void setPlotDomainFalse(int id, DomainType theType, boolean single);
}
