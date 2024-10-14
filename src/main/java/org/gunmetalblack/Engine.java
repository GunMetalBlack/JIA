package org.gunmetalblack;

import org.gunmetalblack.entity.LivingEntitiyManager;
import org.gunmetalblack.render.Render;
import org.gunmetalblack.render.RenderLayerName;

public class Engine {
    private Init window;
    private Render GameRenderer;
    private LivingEntitiyManager livingEntitiyManager;
    public Engine(Init window)
    {
        this.window = window;
        this.GameRenderer = new Render(window);
        this.livingEntitiyManager = new LivingEntitiyManager();
        Update();
    }

    /**
     * Update Function!
     */
    public void Update()
    {
        livingEntitiyManager.instantiateLivingEntity(GameRenderer.layerToBeRendered.get(RenderLayerName.GAME_LAYER).getLayers())
        GameRenderer.renderLayerByName(RenderLayerName.GAME_LAYER);
        while (true)
        {

        }
    }
}
