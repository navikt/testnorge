import React from 'react'
import { shallow } from 'enzyme'
import FormEditor from '../FormEditor'
import AttributtManager from '~/service/kodeverk/AttributtManager/AttributtManager'

const selectedAttributes = ['kjonn', 'statsborgerskap']
const AttributtManagerInstance = new AttributtManager()
const AttributtListe = AttributtManagerInstance.listSelectedAttributesForValueSelection(
	selectedAttributes
)

const formikProps = {
	errors: {},
	values: {},
	touched: {}
}

describe('FormEditor.js', () => {
	const wrapper = shallow(
		<FormEditor
			AttributtListe={AttributtListe}
			getAttributtListByHovedkategori={AttributtManagerInstance.getAttributtListByHovedkategori}
			FormikProps={formikProps}
		/>
	)

	it('should render formEditor', () => {
		expect(wrapper).toHaveLength(1)
	})

	it('should render fieldContainer', () => {
		const subkategori = AttributtListe[0].items[0]
		const rendered = shallow(wrapper.instance().renderFieldContainer(subkategori, 1, formikProps))
		expect(rendered.find('.subkategori').exists()).toBeTruthy()
		expect(rendered.find('.subkategori-field-group').exists()).toBeTruthy()
	})

	it('should render fieldComponent', () => {
		const attribute = AttributtManagerInstance.listAllSelected(['kjonn'])
		const rendered = shallow(wrapper.instance().renderFieldComponent(attribute[0]))
		expect(rendered).toHaveLength(1)
	})

	it('should render a fieldComponent with no inputComponent', () => {
		const wrapperEditMode = shallow(
			<FormEditor
				AttributtListe={AttributtListe}
				getAttributtListByHovedkategori={AttributtManagerInstance.getAttributtListByHovedkategori}
				FormikProps={formikProps}
				editMode
			/>
		)
		const attribute = AttributtManagerInstance.listAllSelected(['inntekt'])
		// chosen attribute: tjeneste (has ApiKodeverk)
		const rendered = shallow(
			wrapperEditMode.instance().renderFieldComponent(attribute[0].items[0], {
				tjeneste: ''
			})
		)
		// rendrer enten staticvalue eller kodeverkValue
		expect(rendered).toHaveLength(1)
	})

	it('should call extraComponentProps and return extra prop "loadOptions"', () => {
		const attribute = AttributtManagerInstance.listAllSelected(['statsborgerskap'])
		const componentProps = wrapper.instance().extraComponentProps(attribute[0])
		expect(componentProps.loadOptions).toBeDefined()
	})

	it('should call extraComponentProps and return no extra props', () => {
		const attribute = AttributtManagerInstance.listAllSelected(['boadresse'])
		// boadresse_gateadresse
		const componentProps = wrapper.instance().extraComponentProps(attribute[1])
		expect(componentProps).toEqual({})
	})

	it('should call extraComponentProps and return type=number', () => {
		const attribute = AttributtManagerInstance.listAllSelected(['boadresse'])
		// boadresse_gateadresse
		const newObj = attribute[1]
		newObj.inputType = 'number'
		const componentProps = wrapper.instance().extraComponentProps(newObj)
		expect(componentProps).toEqual({ type: 'number' })
	})
})
