package com.play4jpa.test;

import com.play4jpa.jpa.models.DefaultQuery;
import com.play4jpa.test.models.Task;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultQueryTest {

    private DefaultQuery query;

    @Before
    public void before() {
        query = new DefaultQuery(Task.class);
    }

    @Test
    public void createAliasTest() {
        String alias = query.createAlias("creator");
        assertEquals("creator_" + query.getAliasIndex(), alias);

        alias = query.createAlias("creator.name");
        assertEquals("creator_name_" + query.getAliasIndex(), alias);
    }

    @Test(expected = IllegalStateException.class)
    public void createDuplicateAliasTest() {
        String alias = query.createAlias("creator");
        assertEquals("creator_" + query.getAliasIndex(), alias);

        query.createAlias("creator");
    }

    @Test(expected = IllegalStateException.class)
    public void alializeUnjoined1Test() {
        query.alialize("creator.name");
    }

    @Test(expected = IllegalStateException.class)
    public void alializeUnjoined2Test() {
        query.join("creator");
        query.alialize("creator.name.test");
    }

    @Test
    public void alializeTest() {
        String alias = query.alialize("name");
        assertEquals("name", alias);

        query.join("creator");
        alias = query.alialize("creator.name");
        assertEquals("creator_" + query.getAliasIndex() + ".name", alias);

        query.join("creator.name");
        alias = query.alialize("creator.name.test");
        assertEquals("creator_name_" + query.getAliasIndex() + ".test", alias);
    }
}
