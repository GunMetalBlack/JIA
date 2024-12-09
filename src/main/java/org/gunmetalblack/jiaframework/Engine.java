package org.gunmetalblack.jiaframework;

import org.gunmetalblack.jiaframework.entity.LivingEntityManager;
import org.gunmetalblack.jiaframework.input.InputManager;
import org.gunmetalblack.jiaframework.render.Render;
import org.gunmetalblack.jiaframework.render.RenderLayerName;

public class Engine {
    private JIAWindow window;
    private Render GameRenderer;
    private LivingEntityManager livingEntityManager;
    public Engine(JIAWindow window, InputManager inputManager)
    {
        this.window = window;
        this.GameRenderer = new Render(window);
        this.livingEntityManager = new LivingEntityManager(GameRenderer.layerToBeRendered.get(RenderLayerName.GAME_LAYER).getChildLayer(RenderLayerName.GL_LIVING_ENTITY_LAYER));
        inputManager.setLivingEntitiyManager(this.livingEntityManager);

        Update();
    }

    /**
     * Update Function!
     */
    public void Update()
    {
        livingEntityManager.instantiateLivingEntity(GameRenderer.layerToBeRendered.get(RenderLayerName.GAME_LAYER).getChildLayer(RenderLayerName.GL_LIVING_ENTITY_LAYER), livingEntityManager.player);
        while (true)
        {
    
            GameRenderer.renderAllLayersToFramebuffer();
            GameRenderer.renderFrameBufferToWindow();
            GameRenderer.renderMainLayerAndChildrenByName(RenderLayerName.FRAME_BUFFER);
            window.getTerminal().repaint();
        }
    }
}
