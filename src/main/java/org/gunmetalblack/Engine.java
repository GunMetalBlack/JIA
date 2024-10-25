package org.gunmetalblack;

import org.gunmetalblack.entity.LivingEntitiyManager;
import org.gunmetalblack.input.InputManager;
import org.gunmetalblack.render.Render;
import org.gunmetalblack.render.RenderLayerName;

public class Engine {
    private Init window;
    private Render GameRenderer;
    private LivingEntitiyManager livingEntitiyManager;
    public Engine(Init window, InputManager inputManager)
    {
        this.window = window;
        this.GameRenderer = new Render(window);
        this.livingEntitiyManager = new LivingEntitiyManager(GameRenderer.layerToBeRendered.get(RenderLayerName.GAME_LAYER).getChildLayer(RenderLayerName.GL_LIVING_ENTITY_LAYER));
        inputManager.setLivingEntitiyManager(this.livingEntitiyManager);

        Update();
    }

    /**
     * Update Function!
     */
    public void Update()
    {
        livingEntitiyManager.instantiateLivingEntity(GameRenderer.layerToBeRendered.get(RenderLayerName.GAME_LAYER).getChildLayer(RenderLayerName.GL_LIVING_ENTITY_LAYER),livingEntitiyManager.player);
        while (true)
        {
            GameRenderer.renderAllLayersToFramebuffer();
            GameRenderer.renderMainLayerAndChildrenByName(RenderLayerName.FRAME_BUFFER);
            window.getTerminal().repaint();
        }
    }
}
