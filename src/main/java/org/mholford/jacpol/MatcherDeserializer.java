package org.mholford.jacpol;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatcherDeserializer extends StdDeserializer<Matcher> {
  public MatcherDeserializer() {
    this(null);
  }

  public MatcherDeserializer(Class<?> vc) {
    super(vc);
  }

  @Override
  public Matcher deserialize(JsonParser p, DeserializationContext ctxt)
          throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    // We expect only one subnode here
    String subNodeName = node.fieldNames().next();
    JsonNode subNode = node.get(subNodeName);
    switch (subNodeName) {
      case "equals":
        if (subNode.isArray()) {
          List<String> values = new ArrayList<>();
          for (JsonNode n : subNode){
            values.add(n.textValue());
          }
          return new EqMatcher(values);
        }
        return new EqMatcher(subNode.textValue());

      case "contains":
        List<String> values = new ArrayList<>();
        for (JsonNode n : subNode) {
          values.add(n.textValue());
        }
        return new ContainsMatcher(values);

      default:
        throw new IOException("Unexpected node name: " + subNodeName);
    }
  }
}
