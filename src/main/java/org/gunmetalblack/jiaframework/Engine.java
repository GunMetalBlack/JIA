package org.gunmetalblack.jiaframework;

import org.gunmetalblack.Init;
import org.gunmetalblack.jiaframework.entity.LivingEntitiyManager;
import org.gunmetalblack.jiaframework.input.InputManager;
import org.gunmetalblack.jiaframework.render.Render;
import org.gunmetalblack.jiaframework.render.RenderLayerName;

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
