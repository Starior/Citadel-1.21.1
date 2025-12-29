package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.client.gui.data.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.GsonHelper;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BookPage {
    // Custom TypeAdapterFactory to handle java.util.Optional without reflection issues in Java 21
    private static final TypeAdapterFactory OPTIONAL_FACTORY = new TypeAdapterFactory() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != Optional.class) {
                return null;
            }
            final Type innerType = ((ParameterizedType) type.getType()).getActualTypeArguments()[0];
            final TypeAdapter<?> innerAdapter = gson.getAdapter(TypeToken.get(innerType));
            return (TypeAdapter<T>) new TypeAdapter<Optional<?>>() {
                @Override
                public void write(JsonWriter out, Optional<?> value) throws java.io.IOException {
                    if (value == null || value.isEmpty()) {
                        out.nullValue();
                    } else {
                        ((TypeAdapter<Object>) innerAdapter).write(out, value.get());
                    }
                }

                @Override
                public Optional<?> read(JsonReader in) throws java.io.IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return Optional.empty();
                    }
                    return Optional.ofNullable(innerAdapter.read(in));
                }
            };
        }
    };

    public static final Gson GSON = (new GsonBuilder())
            .registerTypeAdapterFactory(OPTIONAL_FACTORY)
            .registerTypeAdapter(BookPage.class, new Deserializer())
            .create();
    public String translatableTitle = null;
    private String parent = "";
    private String textFileToReadFrom = "";
    private List<LinkData> linkedButtons = new ArrayList<>();
    private List<EntityLinkData> linkedEntites = new ArrayList<>();
    private List<ItemRenderData> itemRenders = new ArrayList<>();
    private List<RecipeData> recipes = new ArrayList<>();
    private List<TabulaRenderData> tabulaRenders = new ArrayList<>();
    private List<EntityRenderData> entityRenders = new ArrayList<>();
    private List<ImageData> images = new ArrayList<>();
    private final String title;

    public BookPage(String parent, String textFileToReadFrom, List<LinkData> linkedButtons, List<EntityLinkData> linkedEntities, List<ItemRenderData> itemRenders, List<RecipeData> recipes, List<TabulaRenderData> tabulaRenders, List<EntityRenderData> entityRenders, List<ImageData> images, String title) {
        this.parent = parent;
        this.textFileToReadFrom = textFileToReadFrom;
        this.linkedButtons = linkedButtons;
        this.itemRenders = itemRenders;
        this.linkedEntites = linkedEntities;
        this.recipes = recipes;
        this.tabulaRenders = tabulaRenders;
        this.entityRenders = entityRenders;
        this.images = images;
        this.title = title;
    }

    public static BookPage deserialize(Reader readerIn) {
        return GsonHelper.fromJson(GSON, readerIn, BookPage.class);
    }

    public static BookPage deserialize(String jsonString) {
        return deserialize(new StringReader(jsonString));
    }

    public String getParent() {
        return parent;
    }

    public String getTitle() {
        return title;
    }

    public String getTextFileToReadFrom() {
        return textFileToReadFrom;
    }

    public List<LinkData> getLinkedButtons() {
        return linkedButtons;
    }

    public List<EntityLinkData> getLinkedEntities() {
        return linkedEntites;
    }

    public List<ItemRenderData> getItemRenders() {
        return itemRenders;
    }

    public List<RecipeData> getRecipes() {
        return recipes;
    }

    public List<TabulaRenderData> getTabulaRenders() {
        return tabulaRenders;
    }

    public List<EntityRenderData> getEntityRenders() {
        return entityRenders;
    }

    public List<ImageData> getImages() {
        return images;
    }

    public String generateTitle() {
        if (translatableTitle != null) {
            return I18n.get(translatableTitle);
        }
        return title;
    }

    public static class Deserializer implements JsonDeserializer<BookPage> {

        // Use our own Gson for deserializing arrays to avoid Java 21 module access issues
        private static final Gson ARRAY_GSON = new GsonBuilder().create();

        private <T> T[] safeGetArray(JsonObject jsonobject, String key, T[] defaultValue, Class<T[]> clazz) {
            if (!jsonobject.has(key)) {
                return defaultValue;
            }
            try {
                return ARRAY_GSON.fromJson(jsonobject.get(key), clazz);
            } catch (Exception e) {
                // If deserialization fails due to Java 21 module issues, return default
                return defaultValue;
            }
        }

        public BookPage deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(p_deserialize_1_, "book page");
            
            // Use safe deserialization that won't crash on Java 21
            LinkData[] linkedPageRead = safeGetArray(jsonobject, "linked_page_buttons", new LinkData[0], LinkData[].class);
            EntityLinkData[] linkedEntitesRead = safeGetArray(jsonobject, "entity_buttons", new EntityLinkData[0], EntityLinkData[].class);
            ItemRenderData[] itemRendersRead = safeGetArray(jsonobject, "item_renders", new ItemRenderData[0], ItemRenderData[].class);
            RecipeData[] recipesRead = safeGetArray(jsonobject, "recipes", new RecipeData[0], RecipeData[].class);
            TabulaRenderData[] tabulaRendersRead = safeGetArray(jsonobject, "tabula_renders", new TabulaRenderData[0], TabulaRenderData[].class);
            EntityRenderData[] entityRendersRead = safeGetArray(jsonobject, "entity_renders", new EntityRenderData[0], EntityRenderData[].class);
            ImageData[] imagesRead = safeGetArray(jsonobject, "images", new ImageData[0], ImageData[].class);

            String readParent = "";
            if (jsonobject.has("parent")) {
                readParent = GsonHelper.getAsString(jsonobject, "parent");
            }

            String readTextFile = "";
            if (jsonobject.has("text")) {
                readTextFile = GsonHelper.getAsString(jsonobject, "text");
            }

            String title = "";
            if (jsonobject.has("title")) {
                title = GsonHelper.getAsString(jsonobject, "title");
            }


            BookPage page = new BookPage(readParent, readTextFile, Arrays.asList(linkedPageRead), Arrays.asList(linkedEntitesRead), Arrays.asList(itemRendersRead), Arrays.asList(recipesRead), Arrays.asList(tabulaRendersRead), Arrays.asList(entityRendersRead), Arrays.asList(imagesRead), title);
            if (jsonobject.has("title")) {
                page.translatableTitle = GsonHelper.getAsString(jsonobject, "title");
            }
            return page;
        }
    }
}
