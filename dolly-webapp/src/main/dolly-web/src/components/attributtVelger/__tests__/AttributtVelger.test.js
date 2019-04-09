import React from 'react'
import { shallow } from 'enzyme'
import AttributtVelger from '../AttributtVelger'
import { Radio } from 'nav-frontend-skjema'

describe('AttributtVelger.js', () => {
	const selectedIds = ['kjonn']
	const currentBestilling = {
		identOpprettesFra: 'nyIdent',
		attributeIds: ['spraak', 'nasjonalitet']
	}
	const onToggle = jest.fn()
	const rendered = shallow(
		<AttributtVelger
			currentBestilling={currentBestilling}
			selectedIds={selectedIds}
			onToggle={onToggle}
		/>
	)
	it('should render', () => {
		expect(rendered.find('.attributt-velger_panels').exists()).toBe(true)
	})
	it('should render with kjonn selected', () => {
		expect(rendered.find('#kjonn').props().checked).toBe(true)
	})

	it('should render "partner" as a attribute', () => {
		expect(rendered.find('#partner').exists()).toBe(true)
	})

	it('should not render attribute that have parent', () => {
		expect(rendered.find('#partner_kjonn').exists()).toBe(false)
	})

	it('should fire onToggle', () => {
		const value = 'kjonn'
		rendered.find('#kjonn').simulate('change', { target: { id: value } })
		expect(onToggle).toBeCalledWith(value)
	})

	it('should set searchfield to partner and only find that attribute', () => {
		rendered.setState({ search: 'partner' })
		expect(rendered.find('#kjonn').exists()).toBe(false)
		expect(rendered.find('#partner').exists()).toBe(true)
	})

	it('should set searchfield and render no items', () => {
		rendered.setState({ search: 'doesNotExist' })
		expect(
			rendered
				.find('.attributt-velger_panels')
				.find('p')
				.text()
		).toBe('Søket ga ingen treff')
	})

	// TODO: Skal vi ha radio buttons on step 2?
	// const items = [{ id: 'alder', label: 'a' }, { id: 'kjonn', label: 'b' }]
	// const radioButtons = shallow(rendered.instance().renderRadioButtons(items))
	// it('should render radio buttons', () => {
	// 	expect(radioButtons.find(Radio).exists()).toBeTruthy()
	// })

	// it('radio buttons should have onChange function', () => {
	// 	expect(
	// 		radioButtons
	// 			.find(Radio)
	// 			.at(0)
	// 			.props().onChange
	// 	).toBeTruthy()
	// })
	// const selectedItem = { id: 'kjonn', label: 'b' }

	// it('should change radio buttons onClick', () => {
	// 	rendered.instance().onChangeRadioGruppe(items, selectedItem)
	// 	expect(rendered.instance().props.onToggle).toBeCalled()
	// })

	it('should has search function', () => {
		const testdata = '1'
		const radioButtons = rendered.instance().searchOnChange({ target: { value: testdata } })
		expect(rendered.state().search).toEqual(testdata)
	})
})
