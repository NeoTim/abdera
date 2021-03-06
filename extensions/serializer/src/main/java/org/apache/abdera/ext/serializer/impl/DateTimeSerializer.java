/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera.ext.serializer.impl;

import java.lang.reflect.AccessibleObject;
import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.abdera.ext.serializer.Conventions;
import org.apache.abdera.ext.serializer.ObjectContext;
import org.apache.abdera.ext.serializer.SerializationContext;
import org.apache.abdera.ext.serializer.annotation.Value;
import org.apache.abdera.model.AtomDate;
import org.apache.abdera.writer.StreamWriter;

public class DateTimeSerializer extends ElementSerializer {

    public DateTimeSerializer(QName qname) {
        super(qname);
    }

    public DateTimeSerializer() {
        super(null);
    }

    protected void init(Object source,
                        ObjectContext objectContext,
                        SerializationContext context,
                        Conventions conventions) {
        QName qname = this.qname != null ? this.qname : getQName(objectContext.getAccessor());
        StreamWriter sw = context.getStreamWriter();
        sw.startElement(qname);
    }

    protected void process(Object source,
                           ObjectContext objectContext,
                           SerializationContext context,
                           Conventions conventions) {

        writeAttributes(source, objectContext, context, conventions);
        Object value = null;
        if (!(source instanceof Long)) {
            AccessibleObject accessor = objectContext.getAccessor(Value.class, conventions);
            if (accessor != null) {
                value = eval(accessor, source);
            }
        }
        writeValue(value != null ? value : source, context);
    }

    private void writeValue(Object value, SerializationContext context) {
        StreamWriter sw = context.getStreamWriter();
        Date date = null;
        if (value == null)
            return;
        if (value instanceof Date) {
            date = (Date)value;
        } else if (value instanceof Calendar) {
            date = ((Calendar)value).getTime();
        } else if (value instanceof Long) {
            date = new Date(((Long)value).longValue());
        } else if (value instanceof String) {
            date = AtomDate.parse((String)value);
        } else {
            date = AtomDate.parse(value.toString());
        }
        sw.writeElementText(date);
    }
}
