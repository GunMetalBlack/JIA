package org.gunmetalblack;

import org.gunmetalblack.render.Render;

public class Engine {
    private Init window;
    private Render GameRenderer;
    public Engine(Init window)
    {
        this.window = window;
        this.GameRenderer = new Render(window);
        Update();
    }

    /**
     * Update Function!
     */
    public void Update()
    {
        GameRenderer.renderLayerByName("GameLayer");
        while (true)
        {

        }
    }
}
