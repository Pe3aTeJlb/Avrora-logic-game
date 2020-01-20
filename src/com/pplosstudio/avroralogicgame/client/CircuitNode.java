
package com.pplosstudio.avroralogicgame.client;

import java.util.Vector;

class CircuitNode {
    Vector<CircuitNodeLink> links;
    boolean internal;
    CircuitNode() { links = new Vector<CircuitNodeLink>(); }
}
