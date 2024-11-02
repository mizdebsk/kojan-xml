/*-
 * Copyright (c) 2024 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kojan.xml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

class Token {
    private String value;
    private Date expiry;
    private List<String> roles = new ArrayList<>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        roles.add(role);
    }
}

class User {
    private final String name;
    private final List<Token> tokens;

    public User(String name, List<Token> tokens) {
        this.name = name;
        this.tokens = List.copyOf(tokens);
    }

    public String getName() {
        return name;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}

class UserBuilder implements Builder<User> {
    private String name;
    private List<Token> tokens = new ArrayList<>();

    public UserBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder addToken(Token token) {
        tokens.add(token);
        return this;
    }

    public User build() {
        return new User(name, tokens);
    }
}

class DemoTest {
    @Test
    public void demo() throws Exception {
        var tokenEntity =
                Entity.ofMutable(
                        "token",
                        Token::new,
                        Attribute.of("value", Token::getValue, Token::setValue),
                        Attribute.ofOptional(
                                "expiry",
                                Token::getExpiry,
                                Token::setExpiry,
                                date -> Long.toString(date.getTime()),
                                str -> new Date(Long.parseLong(str))),
                        Attribute.ofMulti("role", Token::getRoles, Token::addRole));

        Token token = new Token();
        token.setValue("sWbltcSOB3QpL9w");
        token.setExpiry(new Date());
        token.addRole("admin");
        token.addRole("devel");
        System.out.println(tokenEntity.toXML(token));

        Token token2 = tokenEntity.fromXML("<token><value>qPZ6NY09XTBcdk3</value></token>");

        try {
            tokenEntity.fromXML("<token><expiry>7531902468</expiry></token>");
        } catch (XMLException e) {
            System.out.println(e.toString());
        }

        var userEntity =
                Entity.of(
                        "user",
                        UserBuilder::new,
                        Attribute.of("name", User::getName, UserBuilder::setName),
                        Relationship.of(tokenEntity, User::getTokens, UserBuilder::addToken));

        User user = new UserBuilder().setName("jonh").addToken(token).addToken(token2).build();
        System.out.println(userEntity.toXML(user));
    }
}
