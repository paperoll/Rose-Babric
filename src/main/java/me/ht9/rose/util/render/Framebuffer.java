package me.ht9.rose.util.render;

import me.ht9.rose.util.Globals;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

public final class Framebuffer implements Globals
{
    private static Framebuffer framebuffer;

    private int width;
    private int height;

    private int fbo;
    public int texture;
    private int renderbuffer;
    private final float[] color;

    public Framebuffer(int width, int height)
    {
        this.fbo = -1;
        this.texture = -1;
        this.renderbuffer = -1;
        this.color = new float[]{0f, 0f, 0f, 0f};
        createBindFramebuffer(width, height);
    }

    public void createBindFramebuffer(int width, int height)
    {
        glEnable(GL_DEPTH_TEST);

        if (fbo > -1)
        {
            delete();
        }

        createFramebuffer(width, height);
        checkFramebufferComplete();
        glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
    }

    public void delete()
    {
        unbindFramebufferTexture();
        unbindFramebuffer();

        if (renderbuffer > -1)
        {
            glDeleteRenderbuffersEXT(renderbuffer);
            renderbuffer = -1;
        }

        if (texture > -1)
        {
            glDeleteTextures(texture);
            texture = -1;
        }

        if (fbo > -1)
        {
            glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
            glDeleteFramebuffersEXT(fbo);
            fbo = -1;
        }
    }

    private void createFramebuffer(int width, int height)
    {
        this.width = width;
        this.height = height;

        fbo = glGenFramebuffersEXT();
        texture = glGenTextures();
        renderbuffer = glGenRenderbuffersEXT();

        glBindTexture(GL_TEXTURE_2D, texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glBindFramebufferEXT(GL_FRAMEBUFFER, fbo);
        glFramebufferTexture2DEXT(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);

        glBindRenderbufferEXT(GL_RENDERBUFFER, renderbuffer);
        glRenderbufferStorageEXT(GL_RENDERBUFFER, 0x81A6, width, height);
        glFramebufferRenderbufferEXT(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderbuffer);

        clearFramebuffer();
        unbindFramebufferTexture();
    }

    private void bindFramebufferTexture()
    {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    private void unbindFramebufferTexture()
    {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bindFramebuffer(boolean setViewport)
    {
        glBindFramebufferEXT(GL_FRAMEBUFFER, fbo);

        if (setViewport)
            glViewport(0, 0, width, height);
    }

    public void unbindFramebuffer()
    {
        glBindFramebufferEXT(GL_FRAMEBUFFER, 0);
    }

    private void checkFramebufferComplete()
    {
        int status = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER);

        if (status != GL_FRAMEBUFFER_COMPLETE)
        {
            throw new RuntimeException("glCheckFramebufferStatusEXT returned status code " + status);
        }
    }

    public void setFramebufferColor(float r, float g, float b, float a)
    {
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
        this.color[3] = a;
    }

    public void renderFramebuffer(int width, int height)
    {
        glColorMask(true, true, true, false);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0.0, width, height, 0.0, 1000.0, 3000.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(0f, 0f, -2000f);
        glViewport(0, 0, width, height);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_BLEND);
        glColor4f(1f, 1f, 1f, 1f);
        glEnable(GL_COLOR_MATERIAL);
        bindFramebufferTexture();

        glBegin(GL_QUADS);
        glTexCoord2f(0.0f, 1.0f);
        glVertex2f(0.0f, 0.0f);
        glTexCoord2f(1.0f, 1.0f);
        glVertex2f(width, 0.0f);
        glTexCoord2f(1.0f, 0.0f);
        glVertex2f(width, height);
        glTexCoord2f(0.0f, 0.0f);
        glVertex2f(0.0f, height);
        glEnd();

        unbindFramebufferTexture();
        glDepthMask(true);
        glColorMask(true, true, true, true);
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private void clearFramebuffer()
    {
        bindFramebuffer(true);
        glClearColor(color[0], color[1], color[2], color[3]);

        glClearDepth(1);

        glClear(0x4200);
        unbindFramebuffer();
    }

    public static Framebuffer framebuffer() {
        return framebuffer;
    }

    public static void setFramebuffer(Framebuffer framebuffer) {
        Framebuffer.framebuffer = framebuffer;
    }
}