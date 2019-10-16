package pl.touk.exposed.generator.model

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

    fun customerGraphBuilder(typeEnvironment: TypeEnvironment): EntityGraphBuilder {
        val entity = customerTestEntity(typeEnvironment)

        val annEnv = AnnotationEnvironment(listOf(entity), emptyList(), emptyList(), emptyList(), emptyList(),
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

        val annEnv = AnnotationEnvironment(listOf(defaultPropertyNameEntity, customPropertyNameEntity),
                listOf(customPropertyNameEntityId, defaultPropertyNameEntityId),
                listOf(customPropertyNameEntityId, defaultPropertyNameEntityId),
                listOf(defaultPropertyNameEntityProp1, defaultPropertyNameEntityProp2, customPropertyNameEntityProp1),
                emptyList(), emptyList(), emptyList(), emptyList())
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
                        stringPropertyDefinition(typeEnvironment, prop1, id, "prop1", false),
                        stringPropertyDefinition(typeEnvironment, prop2, id, "prop2", false)
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
                        stringPropertyDefinition(typeEnvironment, prop1, id, "prop1_custom", false)
                )
        )
    }

    fun nullablePropertyGraphBuilder(typeEnvironment: TypeEnvironment): EntityGraphBuilder {
        val entity = nullablePropertyEntity(typeEnvironment)
        val id = getVariableElement(entity, typeEnvironment.elementUtils,"id")
        val prop1 = getVariableElement(entity, typeEnvironment.elementUtils,"prop1")

        val annEnv = AnnotationEnvironment(listOf(entity), listOf(id), listOf(id), listOf(prop1),
                emptyList(), emptyList(), emptyList(), emptyList())

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
                        stringPropertyDefinition(typeEnvironment, prop1, id, "prop1", true)
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

        val annEnv = AnnotationEnvironment(listOf(targetEntity, sourceEntity),
                listOf(sourceEntityId, targetEntityId),
                listOf(sourceEntityId, targetEntityId),
                emptyList(), emptyList(), emptyList(), emptyList(), listOf(sourceEntityTarget, targetEntitySource))

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

    private fun autoGenIdDefinition(id: VariableElement, name: Name): IdDefinition {
        return IdDefinition(
                name = id.simpleName,
                columnName = name,
                annotation = id.getAnnotation(Column::class.java),
                type = IdType.LONG,
                typeMirror = id.asType(),
                generatedValue = true
        )
    }

    private fun stringPropertyDefinition(typeEnvironment: TypeEnvironment, property: VariableElement, id: VariableElement, columnName: String, nullable: Boolean): PropertyDefinition {
        return PropertyDefinition(
                name = typeEnvironment.elementUtils.getName(property.simpleName),
                columnName = typeEnvironment.elementUtils.getName(columnName),
                annotation = property.getAnnotation(Column::class.java),
                type = PropertyType.STRING,
                typeMirror = id.asType(),
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