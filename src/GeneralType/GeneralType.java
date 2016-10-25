/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package GeneralType;

import com.sun.jdi.ArrayType;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;
import com.sun.tools.jdi.ArrayReferenceImpl;

import java.util.List;
import java.util.Map;


/**
 * Created by michael on 03/10/16.
 */
public class GeneralType {

  Object source;
  GeneralType[] children;
  GeneralType parent;
  Field[] fields;
  String[] strFields;
  int width = 100;
  int height;

  public GeneralType(GeneralType[] children, Field[] fields, Object source) {
    this.children = children;
    this.fields = fields;
    this.source = source;
    height = fields.length * 20;
  }

  //Converts the fields and their values to strings so they can be printed when the tree is drawn
  public static String[] generateFields(GeneralType generalType, Map<Field, Value> valueMap) {
    String[] fields = new String[generalType.fields.length];

    for (int i = 0; i < generalType.fields.length; i++) {
      Field f = generalType.fields[i];
      String field;
      try {
        field = f.name() + ": " + valueMap.get(f).toString();
      } catch (Exception ex) {
        field = f.name() + ": Unable to display value";
      }

      fields[i] = field;
    }

    return  fields;
  }

  //Converts the recursive data type into a GeneralType
  public static GeneralType convert(ObjectReference conv, List<ObjectReference> objList) {
    if (conv == null) {
      return null;
    }

    List<Field> fields = conv.referenceType().allFields();
    Map<Field, Value> valueMap = conv.getValues(fields);
    List<ObjectReference> objectReferenceList = objList;

    GeneralType gt = new GeneralType(new GeneralType[1000], new Field[fields.size()], conv);

    String type = conv.referenceType().name();
    int childCount = 0;
    int fieldCount = 0;

    for (Field field : fields) {
      try {
        if (field.type() instanceof ArrayType) {
          if (((ArrayType)field.type()).componentType().name().equals(type)) {
            if (valueMap.get(field) != null) {
              for (int i = 0; i < ((ArrayReferenceImpl)valueMap.get(field)).length(); i++) {
                ObjectReference child = (ObjectReference)((ArrayReferenceImpl)valueMap.get(field)).getValue(i);
                if (!objectReferenceList.contains(child)) {
                  objectReferenceList.add(child);
                  gt.children[childCount] = convert(child, objectReferenceList);
                  childCount++;
                }
              }
            }
          } else {
            gt.fields[fieldCount] = field;
            fieldCount++;
          }
        } else {
          if (type.equals(field.typeName())) {
            ObjectReference child = (ObjectReference)valueMap.get(field);
            if (!objectReferenceList.contains(child)) {
              objectReferenceList.add(child);
              GeneralType gtChild = convert(child, objectReferenceList);
              if (gtChild != null) {
                gtChild.parent = gt;
                gt.children[childCount] = gtChild;
                childCount++;
              }
            }
          } else {
            gt.fields[fieldCount] = field;
            fieldCount++;
          }
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    GeneralType[] gtChildrenTrimmed = new GeneralType[childCount];
    Field[] gtFieldsTrimmed = new Field[fieldCount];

    for (int i = 0; i < childCount; i++) {
      gtChildrenTrimmed[i] = gt.children[i];
    }

    for (int i = 0; i < fieldCount; i++) {
      gtFieldsTrimmed[i] = gt.fields[i];
    }

    gt.children = gtChildrenTrimmed;
    gt.fields = gtFieldsTrimmed;
    gt.strFields = generateFields(gt, valueMap);
    return  gt;

  }
}
