import React from 'react'
import { shallow } from 'enzyme'
import FormEditorFieldArray, { FieldArrayComponent } from '../FormEditorFieldArray'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'

const selectedAttributes = ['barn']
const AttributtManagerInstance = new AttributtManager()
const AttributtListe = AttributtManagerInstance.listSelectedAttributesForValueSelection(
	selectedAttributes
)

const formikProps = {
	errors: {},
	values: {
		barn: [{ test: 'yolo' }]
	},
	touched: {}
}

describe('FormEditorFieldArray.js', () => {
	const barnObj = AttributtListe[0]
	const renderFieldComponent = jest.fn()
	const wrapper = shallow(FormEditorFieldArray(barnObj, formikProps, renderFieldComponent, false))

	it('should render a fieldArray', () => {
		expect(wrapper.find('.subkategori').exists()).toBeTruthy()
	})
})

describe('FieldArrayComponent', () => {
	//nested structure for getting correct subKategori to render
	const barnObj = AttributtListe[0].items[0].items[0]

	const renderFieldComponent = jest.fn()
	const shouldRenderFieldComponent = jest.fn()

	const arrayHelpers = {
		push: jest.fn(),
		remove: jest.fn()
	}

	const wrapper = shallow(
		<FieldArrayComponent
			item={barnObj}
			formikProps={formikProps}
			renderFieldComponent={renderFieldComponent}
			shouldRenderFieldComponent={shouldRenderFieldComponent}
			editMode={false}
			arrayHelpers={arrayHelpers}
		/>
	)

	it('should render field-group', () => {
		expect(wrapper.find('.subkategori-field-group').exists()).toBeTruthy()
	})

	// it('should call renderFieldComponent', () => {
	// 	expect(renderFieldComponent).toBeCalled()
	// })

	it('should render add and remove buttons', () => {
		expect(wrapper.find('.field-group-remove').exists()).toBeTruthy()
		expect(wrapper.find('.field-group-add').exists()).toBeTruthy()
	})
})
