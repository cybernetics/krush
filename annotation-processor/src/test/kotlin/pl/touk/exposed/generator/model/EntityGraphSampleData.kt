package pl.touk.exposed.generator.model

import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import pl.touk.exposed.generator.env.AnnotationEnvironment
import pl.touk.exposed.generator.env.TypeEnvironment
import pl.touk.exposed.generator.env.toTypeElement
import pl.touk.exposed.generator.env.toVariableElement
import javax.lang.model.element.Element
import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.persistence.Column
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@KotlinPoetMetadataPreview
interface EntityGraphSampleData {

    fun customerTestEntity(typeEnvironment: TypeEnvironment): TypeElement {
        return getTypeElement("pl.touk.example.Customer", typeEnvironment.elementUtils)
    }

    fun defaultPropertyNameEntity(typeEnvironment: TypeEnvironment): TypeElement {
        return getTypeElement("pl.touk.example.DefaultPropertyNameEntity", typeEnvironment.elementUtils)
    }

    fun customPropertyNameEntity(typeEnvironment: TypeEnvironment): TypeElement {
        return getTypeElement("pl.touk.example.CustomPropertyNameEntity", typeEnvironment.elementUtils)
    }

    fun oneToOneTargetEntity(typeEnvironment: TypeEnvironment): TypeElement {
        return getTypeElement("pl.touk.example.OneToOneTargetEntity", typeEnvironment.elementUtils)
    }

    fun oneToOneSourceEntity(typeEnvironment: TypeEnvironment): TypeElement {
        return getTypeElement("pl.touk.example.OneToOneSourceEntity", typeEnvironment.elementUtils)
    }

    fun nullablePropertyEntity(typeEnvironment: TypeEnvironment): TypeElement {
        return getTypeElement("pl.touk.example.NullablePropertyEntity", typeEnvironment.elementUtils)
    }

    fun numericPropertyEntity(typeEnvironment: TypeEnvironment): TypeElement {
        return getTypeElement("pl.touk.example.NumericPropertyEntity", typeEnvironment.elementUtils)
    }

    fun datePropertyEntity(typeEnvironment: TypeEnvironment): TypeElement {
        return getTypeElement("pl.touk.example.DatePropertyEntity", typeEnvironment.elementUtils)
    }

    fun characterPropertyEntity(typeEnvironment: TypeEnvironment): TypeElement {
        return getTypeElement("pl.touk.example.CharacterPropertyEntity", typeEnvironment.elementUtils)
    }

    fun customerGraphBuilder(typeEnvironment: TypeEnvironment): EntityGraphBuilder {
        val entity = customerTestEntity(typeEnvironment)

        val annEnv = AnnotationEnvironment(listOf(entity), emptyList(), emptyList(), emptyList(),
                emptyList(), emptyList(), emptyList())

        return EntityGraphBuilder(typeEnvironment, annEnv)
    }

    fun customerTestEntityDefinition(typeEnvironment: TypeEnvironment): EntityDefinition {
        val entity = customerTestEntity(typeEnvironment)

        return EntityDefinition(
                name = entity.simpleName, qualifiedName = entity.qualifiedName,
                table = "customers", id = null)
    }

    fun validTableMappingGraphBuilder(typeEnvironment: TypeEnvironment): EntityGraphBuilder {
        val elements = typeEnvironment.elementUtils

        val defaultPropertyNameEntity = defaultPropertyNameEntity(typeEnvironment)
        val defaultPropertyNameEntityId = getVariableElement(defaultPropertyNameEntity, elements, "id")
        val defaultPropertyNameEntityProp1 = getVariableElement(defaultPropertyNameEntity, elements, "prop1")
        val defaultPropertyNameEntityProp2 = getVariableElement(defaultPropertyNameEntity, elements,"prop2")

        val customPropertyNameEntity = customPropertyNameEntity(typeEnvironment)
        val customPropertyNameEntityId = getVariableElement(customPropertyNameEntity, elements, "id")
        val customPropertyNameEntityProp1 = getVariableElement(customPropertyNameEntity, elements, "prop1")

        val annEnv = AnnotationEnvironment(
                entities = listOf(defaultPropertyNameEntity, customPropertyNameEntity),
                ids = listOf(customPropertyNameEntityId, defaultPropertyNameEntityId),
                columns = listOf(defaultPropertyNameEntityProp1, defaultPropertyNameEntityProp2, customPropertyNameEntityProp1),
                oneToMany = emptyList(), oneToOne = emptyList(), manyToOne = emptyList(), manyToMany = emptyList()
        )
        return EntityGraphBuilder(typeEnvironment, annEnv)
    }

    fun defaultPropertyNameEntityDefinition(typeEnvironment: TypeEnvironment): EntityDefinition {
        val elements = typeEnvironment.elementUtils

        val entity = defaultPropertyNameEntity(typeEnvironment)
        val id = getVariableElement(entity, elements, "id")
        val prop1 = getVariableElement(entity, elements,"prop1")
        val prop2 = getVariableElement(entity, elements,"prop2")

        return EntityDefinition(
                name = entity.simpleName,
                qualifiedName = entity.qualifiedName,
                table = entity.simpleName.asVariable(),
                id = autoGenIdDefinition(id, typeEnvironment.elementUtils.getName(id.simpleName)),
                properties = listOf(
                        propertyDefinition(typeEnvironment, prop1, "prop1", Type("kotlin", "String"), false),
                        propertyDefinition(typeEnvironment, prop2, "prop2", Type("kotlin", "String"), false)
                )
        )
    }

    fun customPropertyNameEntityDefinition(typeEnvironment: TypeEnvironment): EntityDefinition {
        val elements = typeEnvironment.elementUtils

        val entity = customPropertyNameEntity(typeEnvironment)
        val id = getVariableElement(entity, elements, "id")
        val prop1 = getVariableElement(entity, elements,"prop1")

        return EntityDefinition(
                name = entity.simpleName,
                qualifiedName = entity.qualifiedName,
                table = "entity",
                id = autoGenIdDefinition(id, typeEnvironment.elementUtils.getName(id.getAnnotation(Column::class.java).name)),
                properties = listOf(
                        propertyDefinition(typeEnvironment, prop1, "prop1_custom", Type("kotlin", "String"), false)
                )
        )
    }

    fun nullablePropertyGraphBuilder(typeEnvironment: TypeEnvironment): EntityGraphBuilder {
        val entity = nullablePropertyEntity(typeEnvironment)
        val id = getVariableElement(entity, typeEnvironment.elementUtils,"id")
        val prop1 = getVariableElement(entity, typeEnvironment.elementUtils,"prop1")

        val annEnv = AnnotationEnvironment(
                entities = listOf(entity),
                ids = listOf(id), columns = listOf(prop1),
                oneToMany = emptyList(), oneToOne = emptyList(), manyToMany = emptyList(), manyToOne = emptyList()
        )

        return EntityGraphBuilder(typeEnvironment, annEnv)
    }

    fun nullablePropertyEntityDefinition(typeEnvironment: TypeEnvironment): EntityDefinition {
        val entity = nullablePropertyEntity(typeEnvironment)
        val id = getVariableElement(entity, typeEnvironment.elementUtils,"id")
        val prop1 = getVariableElement(entity, typeEnvironment.elementUtils,"prop1")

        return EntityDefinition(
                name = entity.simpleName, qualifiedName = entity.qualifiedName,
                table = "nullablePropertyEntity",
                id = autoGenIdDefinition(id, typeEnvironment.elementUtils.getName(id.simpleName)),
                properties = listOf(
                        propertyDefinition(typeEnvironment, prop1,"prop1", Type("kotlin", "String"), true)
                ))
    }

    fun oneToOneGraphBuilder(typeEnvironment: TypeEnvironment): EntityGraphBuilder {
        val elements = typeEnvironment.elementUtils

        val sourceEntity = oneToOneSourceEntity(typeEnvironment)
        val sourceEntityId = getVariableElement(sourceEntity, elements, "id")
        val sourceEntityTarget = getVariableElement(sourceEntity, elements, "targetEntity")

        val targetEntity = oneToOneTargetEntity(typeEnvironment)
        val targetEntityId = getVariableElement(targetEntity, elements, "id")
        val targetEntitySource = getVariableElement(targetEntity, elements, "sourceEntity")

        val annEnv = AnnotationEnvironment(
                entities = listOf(targetEntity, sourceEntity),
                ids = listOf(sourceEntityId, targetEntityId),
                columns = emptyList(),
                oneToOne = listOf(sourceEntityTarget, targetEntitySource),
                oneToMany = emptyList(), manyToOne = emptyList(), manyToMany = emptyList()
        )

        return EntityGraphBuilder(typeEnvironment, annEnv)
    }

    fun oneToOneSourceEntityDefinition(typeEnvironment: TypeEnvironment): EntityDefinition {
        val elements = typeEnvironment.elementUtils

        val entity = oneToOneSourceEntity(typeEnvironment)
        val id = getVariableElement(entity, elements, "id")
        val targetEntity = getVariableElement(entity, elements,"targetEntity")

        return EntityDefinition(
                name = entity.simpleName,
                qualifiedName = entity.qualifiedName,
                table = entity.simpleName.asVariable(),
                id = autoGenIdDefinition(id, typeEnvironment.elementUtils.getName(id.simpleName)),
                associations = listOf(
                        AssociationDefinition(
                                name =  targetEntity.simpleName,
                                target = targetEntity.toVariableElement().asType().asDeclaredType().asElement().toTypeElement(),
                                joinColumn = targetEntity.getAnnotation(JoinColumn::class.java).name,
                                type = AssociationType.ONE_TO_ONE,
                                targetId = autoGenIdDefinition(id, typeEnvironment.elementUtils.getName(id.simpleName)),
                                mapped = true
                        )
                )
        )
    }

    fun oneToOneTargetEntityDefinition(typeEnvironment: TypeEnvironment): EntityDefinition {
        val elements = typeEnvironment.elementUtils

        val entity = oneToOneTargetEntity(typeEnvironment)
        val id = getVariableElement(entity, elements, "id")
        val sourceEntity = getVariableElement(entity, elements,"sourceEntity")

        return EntityDefinition(
                name = entity.simpleName,
                qualifiedName = entity.qualifiedName,
                table = entity.simpleName.asVariable(),
                id = autoGenIdDefinition(id, typeEnvironment.elementUtils.getName(id.simpleName)),
                associations = listOf(
                        AssociationDefinition(
                                name = sourceEntity.simpleName,
                                target = sourceEntity.toVariableElement().asType().asDeclaredType().asElement().toTypeElement(),
                                type = AssociationType.ONE_TO_ONE,
                                targetId = autoGenIdDefinition(id, typeEnvironment.elementUtils.getName(id.simpleName)),
                                mapped = false,
                                mappedBy = sourceEntity.getAnnotation(OneToOne::class.java).mappedBy
                        )
                )
        )
    }

    fun numericPropertyGraphBuilder(typeEnvironment: TypeEnvironment): EntityGraphBuilder {
        val elements = typeEnvironment.elementUtils

        val numericPropertyEntity = numericPropertyEntity(typeEnvironment)
        val numericPropertyEntityId = getVariableElement(numericPropertyEntity, elements, "id")
        val long = getVariableElement(numericPropertyEntity, typeEnvironment.elementUtils,"long")
        val int = getVariableElement(numericPropertyEntity, typeEnvironment.elementUtils,"int")
        val short = getVariableElement(numericPropertyEntity, typeEnvironment.elementUtils,"short")
        val float = getVariableElement(numericPropertyEntity, typeEnvironment.elementUtils,"float")
        val double = getVariableElement(numericPropertyEntity, typeEnvironment.elementUtils,"double")

        val annEnv = AnnotationEnvironment(entities =  listOf(numericPropertyEntity), ids = listOf(numericPropertyEntityId),
                columns = listOf(long, int, short, float, double), oneToMany = emptyList(), manyToOne = emptyList(),
                manyToMany =  emptyList(), oneToOne = emptyList())

        return EntityGraphBuilder(typeEnvironment, annEnv)
    }

    fun numericPropertyEntityDefinition(typeEnvironment: TypeEnvironment): EntityDefinition {
        val elements = typeEnvironment.elementUtils

        val entity = numericPropertyEntity(typeEnvironment)
        val id = getVariableElement(entity, elements, "id")
        val long = getVariableElement(entity, typeEnvironment.elementUtils,"long")
        val int = getVariableElement(entity, typeEnvironment.elementUtils,"int")
        val short = getVariableElement(entity, typeEnvironment.elementUtils,"short")
        val float = getVariableElement(entity, typeEnvironment.elementUtils,"float")
        val double = getVariableElement(entity, typeEnvironment.elementUtils,"double")

        return EntityDefinition(
                name = entity.simpleName,
                qualifiedName = entity.qualifiedName,
                table = entity.simpleName.asVariable(),
                id = autoGenIdDefinition(id, typeEnvironment.elementUtils.getName(id.simpleName)),
                properties = listOf(
                        propertyDefinition(typeEnvironment, long, "long", Type("kotlin", "Long"), false),
                        propertyDefinition(typeEnvironment, int, "int", Type("kotlin", "Int"), false),
                        propertyDefinition(typeEnvironment, short, "short", Type("kotlin", "Short"), false),
                        propertyDefinition(typeEnvironment, float, "float", Type("kotlin", "Float"), false),
                        propertyDefinition(typeEnvironment, double, "double", Type("kotlin", "Double"), false)
                )
        )
    }

    fun datePropertyGraphBuilder(typeEnvironment: TypeEnvironment): EntityGraphBuilder {
        val elements = typeEnvironment.elementUtils

        val datePropertyEntity = datePropertyEntity(typeEnvironment)
        val datePropertyEntityId = getVariableElement(datePropertyEntity, elements, "id")
        val dateTime = getVariableElement(datePropertyEntity, typeEnvironment.elementUtils,"dateTime")
        val localDateTime = getVariableElement(datePropertyEntity, typeEnvironment.elementUtils,"localDateTime")
        val zonedDateTime = getVariableElement(datePropertyEntity, typeEnvironment.elementUtils,"zonedDateTime")

        val annEnv = AnnotationEnvironment(entities =  listOf(datePropertyEntity), ids = listOf(datePropertyEntityId),
                columns = listOf(dateTime, localDateTime, zonedDateTime), oneToMany = emptyList(), manyToOne = emptyList(),
                manyToMany =  emptyList(), oneToOne = emptyList())

        return EntityGraphBuilder(typeEnvironment, annEnv)
    }

    fun datePropertyEntityDefinition(typeEnvironment: TypeEnvironment): EntityDefinition {
        val elements = typeEnvironment.elementUtils

        val entity = datePropertyEntity(typeEnvironment)
        val id = getVariableElement(entity, elements, "id")
        val dateTime = getVariableElement(entity, typeEnvironment.elementUtils,"dateTime")
        val localDateTime = getVariableElement(entity, typeEnvironment.elementUtils,"localDateTime")
        val zonedDateTime = getVariableElement(entity, typeEnvironment.elementUtils,"zonedDateTime")

        return EntityDefinition(
                name = entity.simpleName,
                qualifiedName = entity.qualifiedName,
                table = entity.simpleName.asVariable(),
                id = autoGenIdDefinition(id, typeEnvironment.elementUtils.getName(id.simpleName)),
                properties = listOf(
                        propertyDefinition(typeEnvironment, dateTime, "dateTime", Type("org.joda.time", "DateTime"), false),
                        propertyDefinition(typeEnvironment, localDateTime, "localDateTime", Type("java.time", "LocalDateTime"), false),
                        propertyDefinition(typeEnvironment, zonedDateTime, "zonedDateTime", Type("java.time", "ZonedDateTime"), false)
                )
        )
    }

    private fun autoGenIdDefinition(id: VariableElement, name: Name): IdDefinition {
        return IdDefinition(
                name = id.simpleName,
                columnName = name,
                annotation = id.getAnnotation(Column::class.java),
                type = Type("kotlin", "Long"),
                generatedValue = true
        )
    }

    private fun propertyDefinition(typeEnvironment: TypeEnvironment, property: VariableElement, columnName: String, type: Type, nullable: Boolean): PropertyDefinition {
        return PropertyDefinition(
                name = typeEnvironment.elementUtils.getName(property.simpleName),
                columnName = typeEnvironment.elementUtils.getName(columnName),
                annotation = property.getAnnotation(Column::class.java),
                type = type,
                nullable = nullable
        )
    }

    private fun getTypeElement(name: String, elements: Elements): TypeElement = elements.getTypeElement(name)

    private fun getVariableElement(typeElt: TypeElement, elements: Elements, name: String): VariableElement =
            elements.getAllMembers(typeElt)
                    .filter { it.simpleName.contentEquals(name) }
                    .map(Element::toVariableElement)
                    .first()

    private fun TypeMirror.asDeclaredType(): DeclaredType {
        require(this is DeclaredType)
        return this
    }
}
